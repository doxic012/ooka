package org.sfisc12s.lzu_web;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.impl.ClassComponent;
import org.ooka.sfisc12s.runtime.environment.component.impl.JarComponent;
import org.ooka.sfisc12s.runtime.environment.component.impl.ReferenceComponent;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable.Scope;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.MessageDigestUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.sfisc12s.lzu_web.util.FileUtil;
import sun.misc.IOUtils;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Named("runtime")
@SessionScoped
public class RuntimeController implements Serializable {

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeController.class);

    private RuntimeEnvironment re = RuntimeEnvironment.getInstance();

    private String errorMessage = "";

    private String successMessage = "";

    private Scope[] scopes = Scope.values();

    private ComponentBase activeComponent = null;

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public Scope[] getScopes() {
        return scopes;
    }

    public Scope getCurrentScope() {
        return re.getScope();
    }

    public void setCurrentScope(Scope scope) {
        if (scope == null)
            return;

        setActiveComponent(null);
        re.setScope(scope);
    }

    public List<ComponentBase> getScopedComponents() {
        return re.getScopedComponents();
    }

    public ComponentBase getActiveComponent() {
        return activeComponent;
    }

    public void setActiveComponent(ComponentBase activeComponent) {
        this.activeComponent = activeComponent;
    }

    public boolean isActiveComponent(ComponentBase component) {
        return Objects.equals(component, getActiveComponent());
    }

    public void loadLibrary(FileUploadEvent event) {
        addFileAsComponent(event.getFile(), new ReferenceComponent());
    }

    public void addComponent(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        addFileAsComponent(file, file.getFileName().endsWith(".jar") ? new JarComponent() : new ClassComponent());
    }

    private void addFileAsComponent(UploadedFile file, ComponentBase component) {
        String fileName = file.getFileName();
        Path filePath = FileUtil.getLibraryPath(fileName);

        if (component == null) {
            log.debug("Error: no component specified");
            errorMessage = "Error: no component specified";
            return;
        }

        try {
            byte[] fileContent = IOUtils.readFully(file.getInputstream(), -1, true);

            // Liste nicht leer und component checksum inside
            if (!re.getComponents().isEmpty() && re.get(MessageDigestUtil.getMD5Hex(fileContent)) != null) {
                log.debug("Error: Component already exists.");
                errorMessage = "Error: Component already exists.";
                return;
            }

            // delete and redeploy library
            Files.deleteIfExists(filePath);
            FileUtil.addFileToFileSystem(filePath, fileContent);

            component.setUrl(FileUtil.getFileUrl(filePath));
            component.setFileName(fileName);
            component.setScope(re.getScope());

            log.debug("Current scope: %s", re.getScope());
            log.debug("Component is valid: %s, url: %s, baseType: %s, checksum: %s", component.isValid(), component.getUrl(), component.getBaseType(), component.getChecksum());
            component = re.getOrAdd(component);

            if (component == null) {
                Files.deleteIfExists(filePath);
                log.debug("Error: Component was not added to runtime environment because it is invalid.");
                errorMessage = "Error: Component was not added to runtime environment because it is invalid.";
                return;
            }

            component.load();
            log.debug("Success: Component was added to the runtime environment");
            successMessage = "Success: Component was added to the runtime environment";

        } catch (IOException | StateException e) {
            errorMessage = String.format("Error Uploaded file '%s' could not be written to the file-system at '%s'", file.getFileName(), FileUtil.getLibraryPath());
            log.error(e, "Error while writing uploaded file '%s' to file-system %s", fileName, FileUtil.getLibraryPath());
        }
    }

}
