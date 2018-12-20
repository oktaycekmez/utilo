package com.utilo.core.properties.file;

import com.utilo.core.io.file.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */
public class PropertiesFileLoader extends FileLoader {
    private final Logger logger = LoggerFactory.getLogger(PropertiesFileLoader.class);
    protected Map<String, PropertiesFileInfo> filePathsToProperties = new ConcurrentHashMap<>();


    public PropertiesFileLoader(String path) {
        super(null, Collections.singletonList(path));
    }

    public PropertiesFileLoader(List<String> absolutePaths) {
        super(null, absolutePaths);
    }

    public PropertiesFileLoader(Path basePath, List<String> absolutePaths) {
        super(basePath, absolutePaths);
    }

    public PropertiesFileInfo getPropertiesFileInfo(String absolutePath) {
        return filePathsToProperties.get(absolutePath);
    }

    public static PropertiesFileInfo loadPropertiesFile(String path) throws IOException {
        PropertiesFileLoader fileLoader = new PropertiesFileLoader(path);
        fileLoader.loadFiles();
        return fileLoader.getPropertiesFileInfo(path);
    }

    public PropertiesFile loadPropertiesFile(String path, byte[] fileBytes) throws IOException {
        PropertiesFile propertiesFile = new PropertiesFile(path);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

        //Encoding verilmediginde InputStreamReader altında sd -> cs attribute'unde otomatik olarak set ediliyor
        //web tarafından uygulama ayaga kalktığından win1254'e set etti,
        //web modulunde bir test sınıfında çalıştırınca utf-8'e set etti
        //idea web olarak calistirken utf-8 olarak baslatiyor
        /**
         *  note buffered'a normalde gerek yok ama InputStream'e gore daha efficient gozukuyor
         *  @see BufferedReader
         */

        try {
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
            propertiesFile.load(bfReader);
            return propertiesFile;
        } catch (IOException e) {
            throw new IOException("Can't get properties file contents", e);
        }
    }

    public synchronized void loadFiles() throws IOException {
        super.loadFiles();

        for (Map.Entry<String, FileInfo> fileInfo : filePathsToFileInfo.entrySet()) {
            try {
                PropertiesFile propertiesFile = loadPropertiesFile(fileInfo.getKey(), fileInfo.getValue().getFileContents());
                filePathsToProperties.put(fileInfo.getKey(), new PropertiesFileInfo(fileInfo.getValue(), propertiesFile));
            } catch (IOException e) {
                throw new IOException("Can't get properties file contents", e);
            }
        }
    }

    public class PropertiesFileInfo {
        FileLoader.FileInfo fileInfo;
        PropertiesFile propertiesFile;

        public PropertiesFileInfo(FileLoader.FileInfo fileInfo, PropertiesFile propertiesFile) {
            this.fileInfo = fileInfo;
            this.propertiesFile = propertiesFile;
        }

        public FileLoader.FileInfo getFileInfo() {
            return fileInfo;
        }

        public void setFileInfo(FileLoader.FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        public PropertiesFile getPropertiesFile() {
            return propertiesFile;
        }

        public void setPropertiesFile(PropertiesFile propertiesFile) {
            this.propertiesFile = propertiesFile;
        }


    }
}
