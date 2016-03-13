package org.sfisc12s.lzu_web.util;

import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.util.MessageDigestUtil;
import org.primefaces.model.UploadedFile;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private static Logger log = LoggerFactory.getRuntimeLogger(FileUtil.class);

    private static String LIBRARY_PATH_URL = System.getProperty("user.dir") + File.separator + "upload" + File.separator;

    private static Path LIBRARY_PATH = Paths.get(LIBRARY_PATH_URL);

    public static Path getLibraryPath() {
        return LIBRARY_PATH;
    }

    public static Path getLibraryPath(String fileName) {
        return Paths.get(LIBRARY_PATH_URL + fileName);
    }

    public static URL getFileUrl(Path filePath) throws MalformedURLException {
        return new URL("file:" + filePath.toString());
    }

    public static void addFileToFileSystem(Path filePath, byte[] inputStreamContent) throws IOException {
        log.debug("Adding file %s to fileSystem", filePath.toString());

        // create file
        Files.createFile(filePath);

        // write content from uploaded file to destination
        OutputStream outputStream = Files.newOutputStream(filePath);
        outputStream.write(inputStreamContent);
    }

    public static void addFileToFileSystem(UploadedFile file) throws IOException {
        addFileToFileSystem(FileUtil.getLibraryPath(file.getFileName()), IOUtils.readFully(file.getInputstream(), -1, true));
    }

    static {
        try {
            Files.createDirectories(FileUtil.getLibraryPath());
        } catch (IOException e) {
            log.error(e, "Error while creating library path");
        }
    }

}
