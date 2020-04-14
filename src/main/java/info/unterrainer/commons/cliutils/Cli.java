package info.unterrainer.commons.cliutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class Cli {
	protected final CommandLine cl;
	protected final CliParserBuilder builder;

	public Cli(final CommandLine cl, final CliParserBuilder builder) {
		this.cl = cl;
		this.builder = builder;
	}

	public boolean isFlagSet(final String longName) {
		return cl.hasOption(longName);
	}

	public boolean isArgSet(final String longName) {
		return cl.hasOption(longName);
	}

	@SuppressWarnings("unchecked")
	public <T> T getArgValue(final String longName) {
		T result;
		CliOption o = builder.options.get(longName);
		try {
			Object obj = cl.getParsedOptionValue(longName);
			if (obj == null)
				result = null;
			else
				result = (T) convert(obj, o.type());
		} catch (ParseException e) {
			builder.printHelp();
			throw new RuntimeException(e.getMessage());
		}
		if (result == null && o.hasDefaultValue())
			result = (T) o.defaultValue();
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getArgValues(final String longName) {
		CliOption o = builder.options.get(longName);
		String[] strings = cl.getOptionValues(longName);
		List<T> results = new ArrayList<>();

		for (String s : strings) {
			T result;
			if (s == null)
				result = null;
			else
				result = (T) convert(s, o.type());
			if (result == null && o.hasDefaultValue())
				result = (T) o.defaultValue();
			results.add(result);
		}
		return results;
	}

	public boolean isHelpSet() {
		return isFlagSet("help");
	}

	public void printHelp() {
		builder.printHelp();
	}

	@SuppressWarnings("unchecked")
	private <T> T convert(final Object obj, final Class<?> type) {
		String s = obj + "";
		if (type == Float.class)
			return (T) (Float) Float.parseFloat(s);
		if (type == Integer.class)
			return (T) (Integer) Integer.parseInt(s);
		if (type == Double.class)
			return (T) (Double) Double.parseDouble(s);

		return (T) (String) obj;
	}
}
