package com.utilo.test.core.properties;

import com.utilo.core.io.file.ResourceUtil;
import com.utilo.core.properties.file.PropertiesFileLoader;
import com.utilo.test.core.TestBase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestProperties extends TestBase {
    @Test
    public void loadResourceProperties() throws Exception {
        final URI uri = ArrayUtils.class.getResource("/app2.properties").toURI();
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        FileSystem zipfs = FileSystems.newFileSystem(uri, env);
        Path myFolderPath = Paths.get(uri);
        Path testClass = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).resolve("app.properties");
        final URI uri2 = IOUtils.class.getResource("/app3.properties").toURI();
        Map<String, String> env2 = new HashMap<>();
        env.put("create", "true");
        FileSystem zipfs2 = FileSystems.newFileSystem(uri2, env2);
        Path myFolderPath2 = Paths.get(uri2);

        byte[] bytes= Files.readAllBytes(testClass);




        System.out.println("sizes:" + bytes.length + ", " + Files.readAllBytes(myFolderPath.getParent().resolve("app2.properties")).length);
    }

    @Test
    public void testResourceUtil() throws IOException {
        //PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader("app.properties");
        //PropertiesFileLoader propertiesFileLoader2 = new PropertiesFileLoader("app2.properties");
        // PropertiesFileLoader propertiesFileLoader3 = new PropertiesFileLoader("app3.properties");
        PropertiesFileLoader propertiesFileLoader4 = new PropertiesFileLoader(Paths.get("D:\\"), Collections.singletonList("app4.properties"));
        //propertiesFileLoader.loadFiles();
        // propertiesFileLoader2.loadFiles();
        //propertiesFileLoader3.loadFiles();
        propertiesFileLoader4.loadFiles();
    }

    public byte[] getByteFromJar(Path jarPath) throws IOException {
        //jar file oldugunu bilmiyor jar'a folder gibi davraniyor!!!
         return IOUtils.toByteArray(jarPath.toUri().toURL().openStream());
    }

    public byte[] getByteFromJar2(Path jarPath) throws IOException {
        return IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("app2.properties"));
    }
}
