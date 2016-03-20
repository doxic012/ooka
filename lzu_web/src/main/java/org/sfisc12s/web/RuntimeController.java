package org.sfisc12s.web;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.persistence.impl.ClassComponent;
import org.ooka.sfisc12s.runtime.environment.persistence.impl.JarComponent;
import org.ooka.sfisc12s.runtime.environment.persistence.impl.ReferenceComponent;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable.Scope;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.MessageDigestUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.sfisc12s.web.util.FileUtil;
import sun.misc.IOUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;

@Named("runtime")
@RequestScoped
public class RuntimeController implements Serializable {

    @PostConstruct
    public void postConstruct() {
        log = LoggerFactory.getRuntimeLogger(RuntimeController.class);
        re = RuntimeEnvironment.getInstance();
    }

    private static Logger log;

    private RuntimeEnvironment re;

    private Component activeComponent = null;

    public Component getActiveComponent() {
        return activeComponent;
    }

    public void setActiveComponent(Component activeComponent) {
        this.activeComponent = activeComponent;
    }

    public boolean isActiveComponent(Component component) {
        return Objects.equals(component, getActiveComponent());
    }

    /* All scopes */
    private Scope[] scopes = Scope.values();

    public Scope[] getScopes() {
        return scopes;
    }

    /* Current scope */
    public Scope getCurrentScope() {
        return re.getScope();
    }

    public void setCurrentScope(Scope scope) {
        if (scope == null)
            return;

        setActiveComponent(null);
        re.setScope(scope);
    }

    public List<Component> getScopedComponents() {
        return re.getScopedComponents();
    }

    /* Injections */
    public Map<Field, Object> getInjectionsFrom(Component component) {
        return re.getInjectionsFrom(component);
    }

    /* Start method */
    public void startComponent(Component component) {
        try {
            component.start();
        } catch (StateException | ScopeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            log.error(e, "Error at startComponent");
        }
    }

    /* Stop method */
    public void stopComponent(Component component) {
        try {
            component.stop();
        } catch (StateException | ScopeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            log.error(e, "Error at stopComponent");
        }
    }

    /* Unload method */
    public void unloadComponent(Component component) {
        try {
            component.unload();
        } catch (StateException | ScopeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            log.error(e, "Error at unloadComponent");
        }
    }

    /* Load method */
    public void loadComponent(Component component) {
        try {
            component.load();
        } catch (StateException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            log.error(e, "Error at loadComponent");
        }
    }

    public void setComponentScope(Component component, Scope scope) {
        component.setScope(scope);
        re.update(component);

        try {
            if (component.isRunning()) {
                component.stop();
                component.start();
            }
        } catch (StateException | ScopeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            log.error(e, "Error at setComponentScope");
        }

        setActiveComponent(null);
    }

    /* Remove method */
    public void removeComponent(Component component) {
        re.remove(component);
        setActiveComponent(null);
    }

    /* Add component (Class / Jar) */
    public void addComponent(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        addFileAsComponent(file, file.getFileName().endsWith(".jar") ? new JarComponent() : new ClassComponent());
    }

    /* Load library (Reference jar) */
    public void loadLibrary(FileUploadEvent event) {
        addFileAsComponent(event.getFile(), new ReferenceComponent());
    }

    private void addFileAsComponent(UploadedFile file, Component component) {
        String fileName = file.getFileName();
        Path filePath = FileUtil.getLibraryPath(fileName);

        if (component == null) {
            log.debug("Error: no component specified");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No component specified"));
            return;
        }

        try {
            byte[] fileContent = IOUtils.readFully(file.getInputstream(), -1, true);
            if (!re.getComponents().isEmpty() && re.get(MessageDigestUtil.getMD5Hex(fileContent)) != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Component already exists"));
                log.debug("Error: Component already exists.");
                return;
            }

            // delete and redeploy library
            Files.deleteIfExists(filePath);
            FileUtil.addFileToFileSystem(filePath, fileContent);

            component.setUrl(FileUtil.getFileUrl(filePath));
            component.setFileName(fileName);
            component.setScope(re.getScope());
            component = re.getOrAdd(component);

            if (component == null) {
                Files.deleteIfExists(filePath);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Component was not added to runtime environment because it is invalid."));
                log.debug("Error: Component was not added to runtime environment because it is invalid.");
                return;
            }

            component.load();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Component was added to the runtime environment."));
            log.debug("Success: Component was added to the runtime environment");

        } catch (IOException | StateException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", String.format("Error Uploaded file '%s' could not be written to the file-system at '%s'", file.getFileName(), FileUtil.getLibraryPath())));
            log.error(e, "Error while writing uploaded file '%s' to file-system %s", fileName, FileUtil.getLibraryPath());
        }
    }

}
