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

	private enum CalculationType {
		MIN, EXACTLY, MAX
	}

	protected CommandLineParser parser = new DefaultParser();
	protected String[] args;
	protected String jarName;
	protected String description;
	final Map<String, Option> options = new HashMap<>();
	final Map<String, CliOption> defaultValues = new HashMap<>();
	protected Set<Occurrences> minNRequired = new HashSet<>();
	protected Set<Occurrences> exactlyNRequired = new HashSet<>();
	protected Set<Occurrences> maxNRequired = new HashSet<>();
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

	public CliParserBuilder addMinRequired(final int numberOfOccurrences, final String... longNames) {
		minNRequired.add(new Occurrences(numberOfOccurrences, longNames));
		return this;
	}

	public CliParserBuilder addExactlyRequired(final int numberOfOccurrences, final String... longNames) {
		exactlyNRequired.add(new Occurrences(numberOfOccurrences, longNames));
		return this;
	}

	public CliParserBuilder addMaxRequired(final int numberOfOccurrences, final String... longNames) {
		maxNRequired.add(new Occurrences(numberOfOccurrences, longNames));
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
		try {
			if (cmdLine.hasOption("help"))
				printHelp();
			else {
				checkOptionsForAvailability(minNRequired);
				checkOptionsForAvailability(exactlyNRequired);
				checkOptionsForAvailability(maxNRequired);
				Set<String> allSetOptionNames = Arrays.stream(cmdLine.getOptions()).map(Option::getLongOpt)
						.collect(Collectors.toSet());
				validateNRequired(CalculationType.MIN, allSetOptionNames, minNRequired);
				validateNRequired(CalculationType.EXACTLY, allSetOptionNames, exactlyNRequired);
				validateNRequired(CalculationType.MAX, allSetOptionNames, maxNRequired);
				validateDependencies(cmdLine);
			}
			return new Cli(cmdLine, this);
		} catch (Exception e) {
			printHelp();
			throw e;
		}
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

	private void checkOptionsForAvailability(final Set<Occurrences> setOfNamesToCheck) {
		Set<String> allAvailable = options.keySet();
		Set<String> namesToCheck = setOfNamesToCheck.stream().map(Occurrences::names).flatMap(Collection::stream)
				.collect(Collectors.toSet());
		if (!allAvailable.containsAll(namesToCheck)) {
			Set<String> unknownOptions = new HashSet<>(namesToCheck);
			unknownOptions.removeAll(allAvailable);
			throw new IllegalStateException("Unknown option: " + unknownOptions);
		}
	}

	private void validateNRequired(final CalculationType calculationType, final Set<String> allSetNames,
			final Set<Occurrences> nRequired) {
		nRequired.forEach(required -> {

			HashMap<String, Integer> countMap = new HashMap<>();
			required.names().forEach(s -> countMap.put(s, 0));
			allSetNames.forEach(s -> {
				Integer count = countMap.get(s);
				if (count != null)
					countMap.put(s, ++count);
			});

			int n = required.numberOfOccurrences();
			String nameList = required.names().stream().sorted().collect(Collectors.joining("', '"));
			int sum = countMap.values().stream().reduce(0, Integer::sum);
			switch (calculationType) {
			case MIN:
				if (sum < n)
					throw new IllegalStateException(
							String.format("You should use at least %s of these options: '%s'", n, nameList));
				break;
			case EXACTLY:
				if (sum != n)
					throw new IllegalStateException(
							String.format("You should use exactly %s of these options: '%s'", n, nameList));
				break;
			case MAX:
				if (sum > n)
					throw new IllegalStateException(
							String.format("You should use at most %s of these options: '%s'", n, nameList));
				break;
			}
		});
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
