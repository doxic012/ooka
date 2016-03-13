package org.ooka.sfisc12s.runtime.util;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import sun.misc.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {
    public static Logger log = LoggerFactory.getRuntimeLogger(MessageDigestUtil.class);

    public static String getMD5Hex(URL url) {
        if (url == null)
            return null;

        try {
            return getMD5Hex(Files.readAllBytes(Paths.get(url.getPath())));
        } catch (IOException e) {
            log.error(e, "Error at getMD5Hex");
        }

        return null;
    }

    public static String getMD5Hex(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);

            return DatatypeConverter.printHexBinary(md.digest());
        } catch (NoSuchAlgorithmException | InvalidPathException e) {
            log.error(e, "Error at getMD5Hex");
        }

        return null;
    }

    public static String getMD5Hex(InputStream fileStream) {
        if (fileStream == null)
            return null;

        try {
            return getMD5Hex(IOUtils.readFully(fileStream, -1, true));
        } catch (IOException e) {
            log.error(e, "Error at getMD5Hex");
        }

        return null;
    }

}
