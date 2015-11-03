package org.bonn.ooka.runtime.util.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by steve on 03.11.15.
 */
public class Config {
    private Path configPath;

    public Config(String filePath) {
        configPath = Paths.get(filePath);

        try {
            if (Files.notExists(configPath))
                Files.createFile(configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getConfig() throws IOException {
        return Files.readAllLines(configPath);
    }

    public void writeToConfig(String entry) throws IOException {
        Files.write(configPath, (entry+System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }
    public String getPath() {
        return configPath.toString();
    }
}
