package info.unterrainer.commons.cliutils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class BuilderTests {

	public void exampleCheckForHelpAndDisplayIt() {
		String[] args = "-h".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program")
				.addFlag(Flag.builder("test").description("a test flag"))
				.addArg(Arg.String("source").description("the source file").defaultValue("testsource"))
				.addArg(Arg.Float("float").description("testfloat").defaultValue(3.2F)).addArg(Arg.String("arg"))
				.create();

		if (cli.isHelpSet())
			System.exit(0);
	}

	@Test
	public void example() {
		String[] args = "".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program")
				.addFlag(Flag.builder("test").description("a test flag"))
				.addArg(Arg.String("source").description("the source file").defaultValue("testsource"))
				.addArg(Arg.Float("float").description("testfloat").defaultValue(3.2F)).addArg(Arg.String("arg"))
				.create();

		assertThat((String) cli.getArgValue("source")).isEqualTo("testsource");
		Float f = cli.getArgValue("float");
		assertThat(f).isEqualByComparingTo(3.2F);
		assertThat((String) cli.getArgValue("arg")).isNull();
	}

	@Test
	public void addedFlagCanBeRetrieved() {
		String[] args = "--flag".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program")
				.addFlag(Flag.builder("flag").description("a test flag")).create();

		assertThat(cli.isFlagSet("flag")).isTrue();
	}

	@Test
	public void notAddedFlagThrowsRuntimeException() {
		String[] args = "--flat".split(" ");
		assertThrows(RuntimeException.class, () -> CliParser.cliFor(args, "test", "a test program").create());
	}

	@Test
	public void addingArgsWithDefaultValuesReturnsDefaultValues() {
		String[] args = "".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program").addArg(Arg.String("string").defaultValue("test"))
				.addArg(Arg.Float("float").defaultValue(3.2F)).addArg(Arg.Double("double").defaultValue(3.3D))
				.addArg(Arg.Integer("integer").defaultValue(2)).create();

		assertThat((String) cli.getArgValue("string")).isEqualTo("test");
		assertThat((Float) cli.getArgValue("float")).isEqualByComparingTo(3.2F);
		assertThat((Double) cli.getArgValue("double")).isEqualByComparingTo(3.3D);
		assertThat((Integer) cli.getArgValue("integer")).isEqualByComparingTo(2);
	}

	@Test
	public void addingAndSettingArgsWithDefaultValuesReturnsSetValues() {
		String[] args = "-h --string blah --float 1.9 --double 2.3 --integer 3".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program").addArg(Arg.String("string").defaultValue("test"))
				.addArg(Arg.Float("float").defaultValue(3.2F)).addArg(Arg.Double("double").defaultValue(3.3D))
				.addArg(Arg.Integer("integer").defaultValue(2)).create();

		assertThat((String) cli.getArgValue("string")).isEqualTo("blah");
		assertThat((Float) cli.getArgValue("float")).isEqualByComparingTo(1.9F);
		assertThat((Double) cli.getArgValue("double")).isEqualByComparingTo(2.3D);
		assertThat((Integer) cli.getArgValue("integer")).isEqualByComparingTo(3);
	}

	@Test
	public void addingExactlyConstraintAndViolatingItThrowsException() {
		String[] args = "--flat --row".split(" ");
		assertThrows(RuntimeException.class,
				() -> CliParser.cliFor(args, "test", "a test program").addFlag(Flag.builder("flat"))
						.addFlag(Flag.builder("row")).addExactlyRequired(1, "flat", "row").create());
	}

	@Test
	public void addingExactlyConstraintAndNotViolatingItWorks() {
		String[] args = "--flat".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program").addFlag(Flag.builder("flat"))
				.addFlag(Flag.builder("row")).addExactlyRequired(1, "flat", "row").create();
		assertThat(cli.isFlagSet("flat")).isTrue();
	}

	@Test
	public void addingMinConstraintAndViolatingItThrowsException() {
		String[] args = "".split(" ");
		assertThrows(RuntimeException.class, () -> CliParser.cliFor(args, "test", "a test program")
				.addFlag(Flag.builder("flat")).addFlag(Flag.builder("row")).addMinRequired(1, "flat", "row").create());
	}

	@Test
	public void addingMinConstraintAndNotViolatingItWorks() {
		String[] args = "--row".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program").addFlag(Flag.builder("flat"))
				.addFlag(Flag.builder("row")).addMinRequired(1, "flat", "row").create();
		assertThat(cli.isFlagSet("row")).isTrue();
	}

	@Test
	public void addingMaxConstraintAndViolatingItThrowsException() {
		String[] args = "--flat --row".split(" ");
		assertThrows(RuntimeException.class, () -> CliParser.cliFor(args, "test", "a test program")
				.addFlag(Flag.builder("flat")).addFlag(Flag.builder("row")).addMaxRequired(1, "flat", "row").create());
	}

	@Test
	public void addingMaxConstraintAndNotViolatingItWorks() {
		String[] args = "--flat".split(" ");
		Cli cli = CliParser.cliFor(args, "test", "a test program").addFlag(Flag.builder("flat"))
				.addFlag(Flag.builder("row")).addMaxRequired(1, "flat", "row").create();
		assertThat(cli.isFlagSet("flat")).isTrue();
	}
}
