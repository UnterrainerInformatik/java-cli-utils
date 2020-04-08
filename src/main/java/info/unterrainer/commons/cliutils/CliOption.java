package info.unterrainer.commons.cliutils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class CliOption {
	private final Object defaultValue;
	private final Class<?> type;
}
