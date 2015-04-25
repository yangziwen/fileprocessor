package net.yangziwen.fileprocessor.executor;

import java.io.File;

import net.yangziwen.fileprocessor.processor.FileProcessor;

public interface Executor {

	public abstract void execute(File file, FileProcessor processor);
	
	public abstract void after();
	
}
