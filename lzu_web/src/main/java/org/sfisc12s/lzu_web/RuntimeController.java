package org.sfisc12s.lzu_web;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.impl.ReferenceComponent;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

@Named("runtime")
@SessionScoped
public class RuntimeController implements Serializable {

    private static String LIBRARY_PATH = System.getProperty("user.dir") + "\\upload\\";

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeController.class);

    private RuntimeEnvironment re = RuntimeEnvironment.getInstance();

    public RuntimeController() {
    }

    public void loadLibrary(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String fileName = file.getFileName();
        Path libraryPath = Paths.get(LIBRARY_PATH);
        Path filePath = Paths.get(LIBRARY_PATH + fileName);

        try {
            File f = new File(LIBRARY_PATH + fileName);
            if (Files.exists(filePath)) {
                test = "file already exists";
                return;
            }
            Files.createDirectories(libraryPath);
            Files.createFile(filePath);

            OutputStream outputStream = Files.newOutputStream(filePath);
            InputStream inputStream = file.getInputstream();

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            URL url = new URL("file:"+filePath.toString());
            test = "load lib: " + event.getFile().getFileName()+" url: "+url.toString()+" filePath: "+filePath.toString();

            ComponentBase component = re.getOrAdd(new ReferenceComponent(fileName, url, "no scope web"));

            if(component != null)
            component.load();

        } catch (IOException | StateException e) {
            e.printStackTrace();
            log.error(e, "Error while writing uploaded file '%s' to file-system %s", file.getFileName(), LIBRARY_PATH);
            test = String.format("Error while writing uploaded file '%s' to file-system %s", file.getFileName(), LIBRARY_PATH);

            try {
                Files.delete(filePath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void addComponent(FileUploadEvent event) {
        test = "add cmp: " + event.getFile().getFileName();
    }

    private String test = "test";

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
