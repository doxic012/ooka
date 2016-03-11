package org.ooka.sfisc12s.runtime.util;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SFI on 10.03.2016.
 */
public class MessageDigestUtil {
    public static Logger log = LoggerFactory.getRuntimeLogger(MessageDigestUtil.class);

    public static String getMD5Hex(URL url) {
        if (url == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(url.getPath())));
            byte[] digest = md.digest();

            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException | IOException | InvalidPathException e) {
            log.error(e, "Error at getMD5Hex");
        }

        return null;
    }

}
