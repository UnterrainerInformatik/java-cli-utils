package info.unterrainer.commons.cliutils;

import org.apache.commons.cli.Option;

public class Flag {

	protected String shortName;
	protected String longName;
	protected String description;
	protected boolean isRequired;

	public static Flag builder(final String longName) {
		return new Flag(longName);
	}

	protected Flag(final String longName) {
		this.longName = longName;
	}

	void addToOptions(final CliParserBuilder parent) {
		parent.options.put(longName,
				Option.builder(shortName).required(isRequired).longOpt(longName).desc(description).build());
	}

	public Flag shortName(final String shortName) {
		this.shortName = shortName;
		return this;
	}

	public Flag description(final String description) {
		this.description = description;
		return this;
	}

	public Flag isRequired() {
		isRequired = true;
		return this;
	}
}
