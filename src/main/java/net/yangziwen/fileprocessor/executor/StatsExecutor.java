package net.yangziwen.fileprocessor.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.yangziwen.fileprocessor.processor.FileProcessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class StatsExecutor implements Executor {
	
	private FileStats overviewStats = new FileStats();
	
	private Map<String, FileStats> suffixStatsMap = new TreeMap<String, FileStats>();

	@Override
	public void execute(File file, FileProcessor processor) {
		String suffix = FilenameUtils.getExtension(file.getName());
		
		FileStats suffixStats = ensureStats(suffix, suffixStatsMap);
		overviewStats.fileNum ++;
		suffixStats.fileNum ++;
		
		long fileSize = FileUtils.sizeOf(file);
		overviewStats.fileSize += fileSize;
		suffixStats.fileSize += fileSize;
		
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				overviewStats.lineNum ++;
				suffixStats.lineNum ++;
				if(StringUtils.isNotBlank(line)) {
					overviewStats.nonblankLineNum ++;
					suffixStats.nonblankLineNum ++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		};
	}

	@Override
	public void after() {
		int padSize = 12;
		outputLine("[%d] files are investigated and the total size is [%s]", 
				overviewStats.fileNum, 
				FileUtils.byteCountToDisplaySize(overviewStats.fileSize));
		outputLine("There are [%d] lines and [%d] nonblank lines", 
				overviewStats.lineNum, overviewStats.nonblankLineNum);
		outputLine("details are as follows:");
		outputLine(
				StringUtils.rightPad("suffix", padSize)
				+
				StringUtils.rightPad("files", padSize)
				+
				StringUtils.rightPad("size", padSize)
				+
				StringUtils.rightPad("lines", padSize)
				+
				StringUtils.rightPad("nonblanks", padSize)
		);
		for(Entry<String, FileStats> entry: suffixStatsMap.entrySet()) {
			FileStats suffixStats = entry.getValue();
			outputLine(
					StringUtils.rightPad(entry.getKey(), padSize)
					+
					StringUtils.rightPad(String.valueOf(suffixStats.fileNum), padSize)
					+
					StringUtils.rightPad(FileUtils.byteCountToDisplaySize(suffixStats.fileSize), padSize)
					+
					StringUtils.rightPad(String.valueOf(suffixStats.lineNum), padSize)
					+
					StringUtils.rightPad(String.valueOf(suffixStats.nonblankLineNum), padSize)
			);
		}
	}
	
	private void outputLine(String format, Object... args) {
		System.out.println(String.format(format, args));
	}
	
	private FileStats ensureStats(String key, Map<String, FileStats> statsMap) {
		if(!statsMap.containsKey(key)) {
			statsMap.put(key, new FileStats());
		}
		return statsMap.get(key);
	}
	
	
	private static class FileStats {
		int fileNum;
		int fileSize;
		int lineNum;
		int nonblankLineNum;
	}

}
