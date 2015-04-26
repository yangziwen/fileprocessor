package net.yangziwen.fileprocessor.command;

import java.io.File;
import java.util.List;

import net.yangziwen.fileprocessor.executor.StatsExecutor;
import net.yangziwen.fileprocessor.processor.FileProcessor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "investigate files in the specified folder")
public class StatsCommand extends AbstractCommand {

	public StatsCommand() {
		super("stats");
	}
	
	@Parameter(names = {"-h", "--help"}, description = "print this message", help = true)
	public boolean help;
	
	@Parameter(
		names = {"-a", "--all"},
		description = "whether to include hidden folders and files"
	)
	public Boolean all = false;
	
	@Parameter(
		names = {"-n", "--included-name"},
		description = "only files with these names are considered"
	)
	public List<String> includedNameList;
	
	@Parameter(
		names = {"-en", "--excluded-name"},
		description = "files with these names are not considered"
	)
	public List<String> excludedNameList;
	
	@Parameter(
		names = {"-f", "--folder"},
		description = "the directory to investigate",
		required = true
	)
	public File folder;

	@Override
	public void invoke(JCommander commander) {
		if(help) {
			commander.usage(this.getName());
			return;
		}
		new FileProcessor(folder, buildFileFilter(), buildFolderFilter())
				.addExecutor(new StatsExecutor())
				.process();
	}
	
	private IOFileFilter buildFileFilter() {
		AndFileFilter andFilter = new AndFileFilter();
		if(CollectionUtils.isNotEmpty(includedNameList)) {
			andFilter.addFileFilter(new WildcardFileFilter(includedNameList));
		}
		if(CollectionUtils.isNotEmpty(excludedNameList)) {
			andFilter.addFileFilter(new NotFileFilter(new WildcardFileFilter(excludedNameList)));
		}
		if(BooleanUtils.isNotTrue(all)) {
			andFilter.addFileFilter(HiddenFileFilter.VISIBLE);
			andFilter.addFileFilter(new NotFileFilter(new PrefixFileFilter(".")));
		}
		return CollectionUtils.isEmpty(andFilter.getFileFilters())
				? TrueFileFilter.INSTANCE
				: andFilter;
	}
	
	private IOFileFilter buildFolderFilter() {
		AndFileFilter andFilter = new AndFileFilter();
		if(BooleanUtils.isNotTrue(all)) {
			andFilter.addFileFilter(HiddenFileFilter.VISIBLE);
			andFilter.addFileFilter(new NotFileFilter(new PrefixFileFilter(".")));
		}
		return CollectionUtils.isEmpty(andFilter.getFileFilters())
				? TrueFileFilter.INSTANCE
				: andFilter;
	}

}
