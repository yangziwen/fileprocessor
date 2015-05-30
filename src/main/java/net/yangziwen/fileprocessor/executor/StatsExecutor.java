package net.yangziwen.fileprocessor.executor;

import static org.apache.commons.io.FileUtils.ONE_EB;
import static org.apache.commons.io.FileUtils.ONE_GB;
import static org.apache.commons.io.FileUtils.ONE_KB;
import static org.apache.commons.io.FileUtils.ONE_MB;
import static org.apache.commons.io.FileUtils.ONE_PB;
import static org.apache.commons.io.FileUtils.ONE_TB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.yangziwen.fileprocessor.processor.FileProcessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class StatsExecutor implements Executor {
	
	private static final String COLUMN_SEPARATOR = " |";
	
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
		int padSize = 15;
		outputLine();
		outputLine(
				StringUtils.leftPad("suffix" + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad("files" + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad("size" + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad("lines" + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad("nonblanks" + COLUMN_SEPARATOR, padSize)
		);
		outputLine(StringUtils.repeat(StringUtils.repeat("-", padSize - 1) + "+", 5));
		outputSuffixStats("[all]", overviewStats, padSize);
		for(Entry<String, FileStats> entry: suffixStatsMap.entrySet()) {
			outputSuffixStats(entry.getKey(), entry.getValue(), padSize);
		}
	}
	
	private static void outputSuffixStats(String suffix, FileStats suffixStats, int padSize) {
		outputLine(
				StringUtils.leftPad(suffix + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad(String.valueOf(suffixStats.fileNum) + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad(byteCountToDisplaySize(suffixStats.fileSize) + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad(String.valueOf(suffixStats.lineNum) + COLUMN_SEPARATOR, padSize)
				+
				StringUtils.leftPad(String.valueOf(suffixStats.nonblankLineNum) + COLUMN_SEPARATOR, padSize)
		);
	}
	
	private static void outputLine() {
		outputLine("");
	}
	
	private static void outputLine(String format, Object... args) {
		System.out.println(String.format(format, args));
	}
	
	private static FileStats ensureStats(String key, Map<String, FileStats> statsMap) {
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
	
	private static String byteCountToDisplaySize(int byteCount) {
		return byteCountToDisplaySize(byteCount, 2);
	}
	
	private static String byteCountToDisplaySize(int byteCount, int digitsAfterDecimalPoint) {
		double doubleSize = byteCount;
		String unit = " b";
		if (byteCount / ONE_EB > 0) {
			doubleSize /= ONE_EB;
            unit = "EB";
        } else if (byteCount / ONE_PB > 0) {
        	doubleSize /= ONE_PB;
            unit = "PB";
        } else if (byteCount / ONE_TB > 0) {
            doubleSize /= ONE_TB;
            unit = "TB";
        } else if (byteCount / ONE_GB > 0) {
        	doubleSize /= ONE_GB;
        	unit = "GB";
        } else if (byteCount / ONE_MB > 0) {
        	doubleSize /= ONE_MB;
        	unit = "MB";
        } else if (byteCount / ONE_KB > 0) {
        	doubleSize /= ONE_KB;
        	unit = "KB";
        }
		DecimalFormat format = Math.floor(doubleSize) < doubleSize && digitsAfterDecimalPoint > 0
				? new DecimalFormat("." + StringUtils.repeat('0', digitsAfterDecimalPoint))
				: new DecimalFormat("#");
        return format.format(doubleSize) + " " + unit;
	}

}
