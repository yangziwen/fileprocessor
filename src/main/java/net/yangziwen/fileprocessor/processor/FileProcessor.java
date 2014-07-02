package net.yangziwen.fileprocessor.processor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.yangziwen.fileprocessor.executor.Executor;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileProcessor {

	private File root;
	private IOFileFilter fileFilter;
	private IOFileFilter folderFilter;
	private List<Executor> executors = new LinkedList<Executor>();
	
	public FileProcessor(File root, IOFileFilter fileFilter, IOFileFilter folderFilter) {
		this.root = root;
		this.fileFilter = fileFilter != null? fileFilter: FalseFileFilter.INSTANCE;
		this.folderFilter = folderFilter != null? folderFilter: FalseFileFilter.INSTANCE;
	}
	
	public FileProcessor addExecutor(Executor executor) {
		executors.add(executor);
		return this;
	}
	
	public void process() {
		process(root);
	}

	private void process(File target) {
		if(target == null) {
			return;
		}
		if(target.isFile()) {
			execute(target);
		} else if (target.isDirectory()) {
			for(File file: target.listFiles()) {
				if(file.isDirectory() && folderFilter.accept(file)) {
					process(file);
				} else if (fileFilter.accept(file)) {
					execute(file);
				}
			}
		}
	}
	
	private void execute(File file) {
		for(Executor executor: executors) {
			executor.execute(file, this);
		}
	}
}
