package net.yangziwen.fileprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.yangziwen.fileprocessor.command.Command;
import net.yangziwen.fileprocessor.command.EncodeCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class FileProcessorMain {

	public static void main(String[] args) {
		Map<String, Command> commandMap = new HashMap<String, Command>();
		JCommander commander = new JCommander();
		
		List<Command> commandList = Arrays.<Command>asList(new EncodeCommand());
		
		for(Command command: commandList) {
			commandMap.put(command.getName(), command);
			commander.addCommand(command.getName(), command);
		}
		
		if(ArrayUtils.isEmpty(args)) {
			commander.usage();
			return;
		}
		
		if(args.length == 1) {
			args = ArrayUtils.add(args, "--help");
		}
		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			return;
		}
		
		Command invokedCommand = commandMap.get(commander.getParsedCommand());
		if(invokedCommand != null) {
			invokedCommand.invoke(commander);
		} else {
			System.err.println("Invalid command!");
		}
	}
	
}
