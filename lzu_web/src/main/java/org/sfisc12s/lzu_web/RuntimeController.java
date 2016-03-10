package org.sfisc12s.lzu_web;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
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
import java.nio.file.Files;

@Named("runtime")
@SessionScoped
public class RuntimeController implements Serializable {

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeController.class);

    private RuntimeEnvironment re = RuntimeEnvironment.getInstance();

    public RuntimeController() {
    }

    public void loadLibrary(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String fileName = file.getFileName();
        try {
            File f = new File("\\libs\\" + fileName);

            // TODO: Popup oder alert?
            if (f.exists()) {
                test = "file exists";
                return;
            }
            OutputStream outputStream = new FileOutputStream(f);
            InputStream inputStream = file.getInputstream();

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            re.getOrAdd(new ReferenceComponent(fileName, f.toURI().toURL(), "no scope web")).load();

            test = "load lib: " + event.getFile().getFileName();
        } catch (IOException | StateException e) {
            e.printStackTrace();
            log.error(e, "Error while writing uploaded file '%s' to file-system", file.getFileName());
            test = "Error while writing uploaded file '%s' to file-system\", file.getFileName()";
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
