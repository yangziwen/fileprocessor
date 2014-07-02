package net.yangziwen.fileprocessor.command;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import net.yangziwen.fileprocessor.converter.CharsetConverter;
import net.yangziwen.fileprocessor.executor.EncodeExecutor;
import net.yangziwen.fileprocessor.processor.FileProcessor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.BooleanUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=", commandDescription = "encode a file or the files under a folder into the certain charset")
public class EncodeCommand extends AbstractCommand {
	
	public EncodeCommand() {
		super("encode");
	}
	
	@Parameter(names = {"-h", "--help"}, description = "print this message", help = true)
	public boolean help;
	
	@Parameter(
		names = {"-c", "--charset"},
		description = "the charset of the file(s) after encode",
		converter = CharsetConverter.class,
		required = true
	)
	public Charset charset;
	
	@Parameter(
		names = {"-s", "--suffix"},
		description = "suffix the target files should have"
	)
	public List<String> suffixList;
	
	@Parameter(
		names = {"-p", "--prefix"},
		description = "prefix the target files should have"
	)
	public List<String> prefixList;
	
	@Parameter(
		names = {"-t", "--target"},
		description = "the target file or folder of files to encode",
		required = true
	)
	public File target;		// target file or folder
	
	@Parameter(
		names = {"-a", "--all"},
		description = "whether to include hidden folders and files"
	)
	public Boolean all = false;

	@Override
	public void invoke(JCommander commander) {
		if(help) {
			commander.usage(this.getName());
			return;
		}
		new FileProcessor(target, buildFileFilter(), buildFolderFilter())
				.addExecutor(new EncodeExecutor(charset.name()))
				.process();
	}
	
	private IOFileFilter buildFileFilter() {
		AndFileFilter andFilter = new AndFileFilter();
		if(CollectionUtils.isNotEmpty(prefixList)) {
			andFilter.addFileFilter(new PrefixFileFilter(prefixList));
		}
		if(CollectionUtils.isNotEmpty(suffixList)) {
			andFilter.addFileFilter(new SuffixFileFilter(suffixList));
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
