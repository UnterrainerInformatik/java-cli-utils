package info.unterrainer.commons.cliutils;

public class CliParser {

	public static CliParserBuilder cliFor(String[] args, String jarName, String description) {
		return new CliParserBuilder(args, jarName, description);
	}
}