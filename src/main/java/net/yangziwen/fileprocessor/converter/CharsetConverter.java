package net.yangziwen.fileprocessor.converter;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class CharsetConverter implements IStringConverter<Charset> {

	@Override
	public Charset convert(String charset) {
		try {
			return Charset.forName(charset);
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		}
		throw new ParameterException("charset [" + charset + "] is not supported!");
	}

}
