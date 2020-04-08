package info.unterrainer.commons.cliutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliParserBuilder {

	protected CommandLineParser parser = new DefaultParser();
	protected String[] args;
	protected String jarName;
	protected String description;
	final Map<String, Option> options = new HashMap<>();
	final Map<String, CliOption> defaultValues = new HashMap<>();
	protected Set<Set<String>> atLeastOneRequired = new HashSet<>();
	private final Map<String, Set<String>> dependencies = new HashMap<>();

	public CliParserBuilder(final String[] args, final String jarName, final String description) {
		this.args = args;
		this.jarName = jarName;
		this.description = description;
	}

	public CliParserBuilder addRawOption(final Option option) {
		validateOpt(option.getOpt());
		validatelongName(option.getLongOpt());
		options.put(option.getLongOpt(), option);
		return this;
	}

	public CliParserBuilder addFlag(final Flag flagBuilder) {
		flagBuilder.addToOptions(this);
		return this;
	}

	public <T> CliParserBuilder addArg(final Arg<T> argBuilder) {
		argBuilder.addToOptions(this);
		return this;
	}

	public CliParserBuilder atLeastOneRequired(final String... longNames) {
		atLeastOneRequired.add(new HashSet<>(Arrays.asList(longNames)));
		return this;
	}

	public CliParserBuilder addDependency(final String parentOption, final String... childOptions) {
		dependencies.put(parentOption, new HashSet<>(Arrays.asList(childOptions)));
		return this;
	}

	private void validateOpt(final String opt) {
		if ("h".equalsIgnoreCase(opt))
			throw new IllegalStateException("'h' is reserved for the 'help'-option.");
	}

	private void validatelongName(final String longName) {
		if (longName == null)
			throw new IllegalStateException("You have to define a long option name.");
	}

	public Cli create() {
		if (!options.containsKey("help"))
			addFlag(Flag.builder("help").description("show this message").shortName("h"));

		CommandLine cmdLine = startParser();
		validateAtLeastOneRequired(cmdLine);
		validateDependencies(cmdLine);
		return new Cli(cmdLine, this);
	}

	private void validateDependencies(final CommandLine cmdLine) {
		Set<String> all = Arrays.stream(cmdLine.getOptions()).map(Option::getLongOpt).collect(Collectors.toSet());

		dependencies.forEach((parent, children) -> {
			if (all.contains(parent) && !all.containsAll(children)) {
				Set<String> missedOptions = new HashSet<>(children);
				missedOptions.removeAll(all);
				throw new IllegalStateException(
						String.format("With '%s' option you must also specify these: %s", parent, missedOptions));
			}
		});
	}

	private void validateAtLeastOneRequired(final CommandLine cmdLine) {
		Set<String> all = options.keySet();
		Set<String> allAtLeastOneRequired = atLeastOneRequired.stream().flatMap(Collection::stream)
				.collect(Collectors.toSet());
		if (!all.containsAll(allAtLeastOneRequired)) {
			Set<String> unknownOptions = new HashSet<>(allAtLeastOneRequired);
			unknownOptions.removeAll(all);
			throw new IllegalStateException("Unknown option: " + unknownOptions);
		}

		Set<String> allRuntimeOptionNames = Arrays.stream(cmdLine.getOptions()).map(Option::getLongOpt)
				.collect(Collectors.toSet());
		atLeastOneRequired.forEach(s -> validateAtLeastOneRequiredSubSet(cmdLine, allRuntimeOptionNames, s));
	}

	private void validateAtLeastOneRequiredSubSet(final CommandLine cmdLine, final Set<String> allRuntimeOptionNames,
			final Set<String> atLeastOneRequired) {
		if (allRuntimeOptionNames.stream().noneMatch(shortName -> atLeastOneRequired.contains(shortName)))
			throw new IllegalStateException(String.format("You should use at least one of these options: '%s'",
					atLeastOneRequired.stream().sorted().collect(Collectors.joining("', '"))));
	}

	private CommandLine startParser() {
		CommandLine cmdLine;
		try {
			cmdLine = new DefaultParser().parse(createOptions(), args);
		} catch (ParseException e) {
			printHelp();
			throw new RuntimeException(e.getMessage());
		}
		return cmdLine;
	}

	void printHelp() {
		new HelpFormatter().printHelp(
				String.format("java -jar %s.jar", Optional.ofNullable(jarName).orElse("<JAR-NAME>")), description,
				createOptions(), "", true);
	}

	private Options createOptions() {
		Options result = new Options();
		options.values().forEach(result::addOption);
		return result;
	}
}
