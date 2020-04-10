package info.unterrainer.commons.cliutils;

import org.apache.commons.cli.Option;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class CliOption {
	private Option option;
	private Class<?> type;
	private Object defaultValue;
	private boolean hasDefaultValue;
}
