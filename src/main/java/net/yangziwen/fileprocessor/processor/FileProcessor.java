package net.yangziwen.fileprocessor.processor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.yangziwen.fileprocessor.executor.Executor;

import org.apache.commons.io.filefilter.IOFileFilter;

public class FileProcessor {

	private File root;
	private IOFileFilter filter;
	private List<Executor> executors = new LinkedList<Executor>();
	
	public FileProcessor(File root, IOFileFilter filter) {
		this.root = root;
		this.filter = filter;
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
				if(file.isDirectory()) {
					process(file);
				} else if (filter.accept(file)) {
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
