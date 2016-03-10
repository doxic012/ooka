package org.ooka.sfisc12s.runtime.util;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SFI on 10.03.2016.
 */
public class MessageDigestUtil {

    public static String getMD5Hex(URL url) {
        if (url == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(url.getPath())));
            byte[] digest = md.digest();

            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
