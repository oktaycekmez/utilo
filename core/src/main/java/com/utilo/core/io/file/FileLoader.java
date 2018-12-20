package com.utilo.core.io.file;

import com.utilo.core.exception.IllegalParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */

public class FileLoader {
    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    protected Map<String, FileInfo> filePathsToFileInfo = null;
    private List<String> filePaths;
    private Path parentPath;

    /**
     * @param parentPath give null if you are giving absolute paths for the files.
     *                   otherwise if you are giving root folder/jar where the files are located,
     *                   set filePaths to relative paths of the files.
     * @param filePaths  give path which files should be loaded @{@link FileLoader}
     */
    public FileLoader(Path parentPath, List<String> filePaths) {
        this.parentPath = parentPath;
        this.filePaths = new ArrayList<>(filePaths);
    }

    protected boolean isRelativePath(String path) {
        return !path.contains("/") && !path.contains("\\");
    }

    protected void resolveParentPath() throws IOException {
        if (filePaths.size() > 0 && parentPath == null) {
            if (isRelativePath(filePaths.get(0))) {
                /*
                  will open a fileSystem if parentPath is a jar file and we are not closing it to resolve file paths!(@parentPath.resolve)
                 */
                parentPath = ResourceUtil.getParentPathForResource(filePaths.get(0));
                String parentAbsolutePath = parentPath.toString();
                if (parentPath.toUri().getScheme().equals("jar"))
                    parentAbsolutePath = parentPath.getFileSystem().toString();
                logger.warn("FileLoader did not find any file separator in the filePath. So will search resources in :" + parentAbsolutePath);
            }
        }
    }

    protected void validate() throws IllegalParameterException {
        boolean isRelative = parentPath != null;
        boolean isAbsolute = false;
        for (int i = 0; i < filePaths.size(); i++) {
            boolean relativePath = isRelativePath(filePaths.get(i));
            if (relativePath) {
                if (isAbsolute) {
                    throw new IllegalParameterException("There are both of absolute and relative paths. Relative path:" + filePaths.get(i) +
                            " You can not use both of absolute and relative paths in the same " + this.getClass());
                } else {
                    isRelative = true;
                }
            } else {
                if (isRelative) {
                    throw new IllegalParameterException("parentPath is set or a relativePath exists,but there is an absolute path: " +
                            filePaths.get(i) + " you can not use both of absolute and relative paths in the same " + this.getClass());
                } else {
                    isAbsolute = true;
                }
            }
        }
    }

    public synchronized void loadFiles() throws IOException {
        try {
            Map<String, FileInfo> filePathsToFileInfo = new ConcurrentHashMap<>();
            String path;

            logger.info("loading files started");
            resolveParentPath();
            validate();
            for (int i = 0; i < filePaths.size(); i++) {
                path = filePaths.get(i);
                FileInfo fileInfo = new FileInfo(path);
                byte[] fileContents = loadFile(fileInfo);
                fileInfo.setFileContents(fileContents);
                filePathsToFileInfo.put(path, fileInfo);
            }

            this.filePathsToFileInfo = filePathsToFileInfo;
            logger.info("loading files finished");
        } catch (Exception e) {
            throw new IOException("Can't load files:" + Arrays.toString(filePaths.toArray()), e);
        }
    }

    public Path getParentPath() {
        return parentPath;
    }


    public class FileInfo {
        protected String fileReferencePath;
        protected Path filePath;
        //Decided this to be a byte array as if it was input stream, we will
        //give responsibility of closing the input stream to the client.
        protected byte[] fileContents;

        public FileInfo(String fileReferencePath) {
            this.fileReferencePath = fileReferencePath;
        }

        public String getFileReferencePath() {
            return fileReferencePath;
        }

        public Path getFilePath() {
            return filePath;
        }

        public void setFilePath(Path filePath) {
            this.filePath = filePath;
        }

        public void setFileContents(byte[] fileContents) {
            this.fileContents = fileContents;
        }

        public byte[] getFileContents() {
            return fileContents;
        }
    }

    protected byte[] loadFile(FileInfo fileInfo) throws IOException {
        StringBuilder message = new StringBuilder();
        try {
            if (parentPath != null) {
                message.append(parentPath).append(File.separator);
                fileInfo.setFilePath(parentPath.resolve(fileInfo.getFileReferencePath()));
            }
            return Files.readAllBytes(fileInfo.getFilePath());
        } catch (Exception e) {
            throw new IOException("Can not read file:" +message.append(fileInfo.getFileReferencePath()), e);
        }
    }

    public Map<String, FileInfo> getFilePathsToFileInfo() {
        return filePathsToFileInfo;
    }
}
