package com.utilo.core.io.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;

public class ResourceUtil {
    /**
     *
     * Will open a new fileSystem if parentPath is a jar file and closing is the responsibility of the client!
     *
     * @param resourceRelativePath
     * @return
     * @throws IOException
     */
    public static Path getParentPathForResource(String resourceRelativePath) throws IOException {
        String exceptionMessage = "Can not get resource:" + resourceRelativePath;
        Path parentPath = null;
        FileSystem fileSystem = null;
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(resourceRelativePath);
            if (url == null)
                throw new IOException(exceptionMessage);
            URI uri = url.toURI();
            if (uri.getScheme().equals("jar")) {
                FileSystems.newFileSystem(uri, Collections.emptyMap());
            }
            return Paths.get(uri).getParent();
        } catch (URISyntaxException e) {
            throw new IOException(exceptionMessage, e);
        } finally {

        }
    }

    public static void main(String[] args) throws IOException {
        getParentPathForResource("app.properties");
    }
}