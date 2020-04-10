package info.unterrainer.commons.cliutils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Occurrences {
	public Occurrences(final int numberOfOccurrences, final String[] names) {
		this.numberOfOccurrences = numberOfOccurrences;
		this.names = new HashSet<>(Arrays.asList(names));
	}

	private Set<String> names = new HashSet<>();
	private int numberOfOccurrences;
}
