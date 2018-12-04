package com.utilo.core.io.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Oktay CEKMEZ<br>
 * Date: 04.02.2015<br>
 * Time: 17:14<br>
 */

public class FileLoader {
	private final Logger                logger              = LoggerFactory.getLogger(FileLoader.class);
	protected     Map<String, FileInfo> filePathsToFileInfo = null;
	protected     List<String>          filePaths;
	public FileLoader() {

	}

	public FileLoader(String absolutePath) {
		filePaths = new ArrayList<>();
		filePaths.add(absolutePath);
	}

	public FileLoader(List<String> absolutePaths) {
		filePaths = absolutePaths;
	}

	public synchronized void loadFiles() throws IOException {
		try {
			Map<String, FileInfo> filePathsToFileInfo = new ConcurrentHashMap<>();
			String fileRelativeOrAbsolutePath;

			logger.info("loading files started");
			for (int i = 0; i < filePaths.size(); i++) {
				fileRelativeOrAbsolutePath = filePaths.get(i);
				FileInfo fileInfo = new FileInfo(fileRelativeOrAbsolutePath);
				byte[] fileContents = loadFile(fileInfo);
				fileInfo.setFileContents(fileContents);
				filePathsToFileInfo.put(fileRelativeOrAbsolutePath, fileInfo);
			}

			this.filePathsToFileInfo = filePathsToFileInfo;
			logger.info("loading files finished");
		} catch (Exception e) {
			throw new IOException("Can't load files:" + Arrays.toString(filePaths.toArray()), e);
		}
	}

	public class FileInfo {
		protected String fileAbsolutePath;
		protected Path   filePath;
		//Decided this to be a byte array as if it was input stream, we will
		//give responsibility of closing the input stream to the client.
		protected byte[] fileContents;

		public FileInfo(String fileAbsolutePath) {
			this.fileAbsolutePath = fileAbsolutePath;
		}

		public String getFileAbsolutePath() {
			return fileAbsolutePath;
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

	protected InputStream getTheFile(FileInfo fileInfo) throws FileNotFoundException {
		InputStream inputStream = null;

		logger.debug("Looking for the file:" + fileInfo.getFileAbsolutePath() +
				" at absolutePath");
		File file = new File(fileInfo.getFileAbsolutePath());
		if (file.exists()) {
			fileInfo.setFilePath(file.toPath());
			inputStream = new FileInputStream(file);
		} else {
			throw new FileNotFoundException("Can't find the file at absolutePath:" + fileInfo.getFileAbsolutePath());
		}

		return inputStream;
	}

	/*protected InputStream getTheFileFromProgramExecutionClassPath(FileInfo fileInfo) throws FileNotFoundException {
		InputStream inputStream;
		try {
			logger.debug("Looking for the file:" + fileInfo.getFileAbsolutePath() + " at classpath");
			logger.debug("Program execution classpath:" + Thread.currentThread().getContextClassLoader().getResource(""));
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileInfo.getFileAbsolutePath());
		} catch (Exception e) {
			throw new FileNotFoundException("Can't find the file at program execution classpath:" + fileInfo.getFileAbsolutePath());
		}

		if (inputStream != null) {
			URL url = Thread.currentThread().getContextClassLoader().getResource(fileInfo.getFileAbsolutePath());
			if (url != null) {
				try {
					fileInfo.setFilePath(Paths.get(url.toURI()));
				} catch (URISyntaxException e) {
					//ignore
				}
			}
		}

		return inputStream;
	}*/

	protected byte[] loadFile(FileInfo fileInfo) throws FileNotFoundException {
		InputStream inputStream = null;
		byte[] fileContents = null;
		Exception causeException = null;

		try {
			inputStream = getTheFile(fileInfo);
		} catch (Exception e) {
			causeException = e;
		}

		try {
			/*if (inputStream == null && lookFileAtClasspath) {
				inputStream = getTheFileFromProgramExecutionClassPath(fileInfo);
			}*/

		} catch (Exception e) {
			causeException = e;
			inputStream = null;
		}

		try {
			if (inputStream != null) {
				fileContents = IOUtils.toByteArray(inputStream);
			} else {
				causeException = new StreamCorruptedException("Can't read the inputStream for the file:" + fileInfo.getFileAbsolutePath());
			}
		} catch (Exception e) {
			causeException = e;
		}

		IOUtils.closeQuietly(inputStream);

		if (fileContents == null) {
			FileNotFoundException fileEx = new FileNotFoundException("Can't read the file:" + fileInfo.getFileAbsolutePath());
			fileEx.initCause(causeException);
			throw fileEx;
		}

		return fileContents;
	}


	/**
	 * If it is not set FileLoader will only search the file  at execution path.
	 * it is recommended to set true for project resource files
	 *
	 * @/param lookFileAtClasspath

	public void setLookFileAtClasspath(boolean lookFileAtClasspath) {
		this.lookFileAtClasspath = lookFileAtClasspath;
	}*/

	public Map<String, FileInfo> getFilePathsToFileInfo() {
		return filePathsToFileInfo;
	}
}
