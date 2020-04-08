package info.unterrainer.commons.cliutils;

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
		try {
			Object obj = cl.getParsedOptionValue(longName);
			CliOption o = builder.defaultValues.get(longName);
			if (obj == null)
				result = null;
			else
				result = (T) convert(obj, o.type());
		} catch (ParseException e) {
			builder.printHelp();
			throw new RuntimeException(e.getMessage());
		}
		if (result == null && builder.defaultValues.containsKey(longName))
			result = (T) builder.defaultValues.get(longName).defaultValue();
		return result;
	}

	public void printHelpIfOptionIsSet() {
		if (isFlagSet("help"))
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
