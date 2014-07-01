package net.yangziwen.fileprocessor.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.yangziwen.fileprocessor.processor.FileProcessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class EncodeExecutor implements Executor {
	
	private String charset;
	
	public EncodeExecutor(String charset) {
		this.charset = charset;
	}

	public void execute(File file, FileProcessor processor) {
		String detectedCharset = detectCharset(file);
		if(StringUtils.isBlank(detectedCharset)) {
			System.err.println("Failed to detect the charset of file [" + file.getAbsolutePath() + "]");
			return;
		}
		if("utf8".equalsIgnoreCase(detectedCharset) || "utf-8".equalsIgnoreCase(detectedCharset)) {
			return;
		}
		try {
			String content = FileUtils.readFileToString(file, detectedCharset);
			File tempFile = new File(file.getCanonicalPath() + ".tmp");
			FileUtils.writeStringToFile(tempFile, content, charset);
			FileUtils.deleteQuietly(file);
			FileUtils.moveFile(tempFile, file);
		} catch (Exception e) {
			System.err.println("Failed to encode the file [" + file.getAbsolutePath() + "] with charset of [" + charset + "]");
		}
	}
	
	/**
	 * 探测文本的字符编码
	 */
	public static String detectCharset(File file) {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("File [" + file.getAbsolutePath() + "] doesn't exist!");
		}
		byte[] buf = new byte[4096];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);
			int len;
			while((len = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, len);
			}
			detector.dataEnd();
			return detector.getDetectedCharset();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return null;
	}
}
