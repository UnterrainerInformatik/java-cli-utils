package info.unterrainer.commons.cliutils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

public class Arg<T> extends Flag {

	protected boolean unlimitedArgs;
	protected boolean isOptional;
	protected String[] argNames = new String[] { "ARG" };
	protected char valueSeparator = ',';
	protected Class<?> type;
	protected boolean hasDefault;
	protected T defaultValue;

	public static Arg<String> String(final String longName) {
		return new Arg<>(longName, String.class);
	}

	public static Arg<Integer> Integer(final String longName) {
		return new Arg<>(longName, Integer.class);
	}

	public static Arg<Float> Float(final String longName) {
		return new Arg<>(longName, Float.class);
	}

	public static Arg<Double> Double(final String longName) {
		return new Arg<>(longName, Double.class);
	}

	private Arg(final String longName, final Class<?> type) {
		super(longName);
		this.type = type;
	}

	private Class<?> convert(final Class<?> type) {
		if (type == Float.class || type == Integer.class || type == Double.class)
			return Number.class;
		return String.class;
	}

	@Override
	void addToOptions(final CliParserBuilder parent) {
		Builder builder = Option.builder(shortName).required(isRequired).longOpt(longName).desc(description)
				.type(convert(type)).valueSeparator(valueSeparator);
		if (isOptional)
			builder.optionalArg(true);
		else
			builder.hasArg();
		if (unlimitedArgs)
			builder.hasArgs().argName("ARG> <...");
		else
			builder.numberOfArgs(argNames.length).argName(String.join("> <", argNames));
		CliOption opt = new CliOption().option(builder.build()).type(type).hasDefaultValue(hasDefault);
		if (hasDefault)
			opt.defaultValue(defaultValue);
		parent.options.put(longName, opt);
	}

	@Override
	public Arg<T> shortName(final String shortName) {
		this.shortName = shortName;
		return this;
	}

	@Override
	public Arg<T> description(final String description) {
		this.description = description;
		return this;
	}

	@Override
	public Arg<T> isRequired() {
		isRequired = true;
		return this;
	}

	public Arg<T> name(final String... argNames) {
		this.argNames = argNames;
		return this;
	}

	public Arg<T> optional() {
		isOptional = true;
		return this;
	}

	public Arg<T> separator(final char valueSeparator) {
		this.valueSeparator = valueSeparator;
		return this;
	}

	public Arg<T> unlimited() {
		unlimitedArgs = true;
		return this;
	}

	public Arg<T> defaultValue(final T defaultValue) {
		this.defaultValue = defaultValue;
		hasDefault = true;
		return this;
	}
}
