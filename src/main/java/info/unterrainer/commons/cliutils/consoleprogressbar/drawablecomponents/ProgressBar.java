package info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents;

import java.io.PrintStream;
import java.util.Optional;

import info.unterrainer.commons.datastructures.Fader;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This progress-bar draws a bar like:
 *
 * <pre>
 * {@code
 * file a: [####-----------]
 *
 * ...
 *
 * file a: [########-------]
 *
 * ...
 *
 * file a: [###############]
 * }
 * </pre>
 *
 * You may specify any other begin- , end- , full- or empty-character you
 * like.<br>
 * This bar is only a good choice if your console supports control characters
 * since for this representation you have to to clear all characters on each
 * redraw using '\b' (backspace).
 * <p>
 * <table>
 * <caption>Default values</caption>
 * <tr>
 * <td><b>prefix</b></td>
 * <td>"file a: "</td>
 * </tr>
 * <tr>
 * <td><b>begin</b></td>
 * <td>"["</td>
 * </tr>
 * <tr>
 * <td><b>end</b></td>
 * <td>"]"</td>
 * </tr>
 * <tr>
 * <td><b>full</b></td>
 * <td>'#'</td>
 * </tr>
 * <tr>
 * <td><b>empty</b></td>
 * <td>'-'</td>
 * </tr>
 * </table>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressBar implements DrawableComponent {

	@Getter
	@Setter
	private String prefix;
	@Getter
	@Setter
	private String begin;
	@Getter
	@Setter
	private String end;
	@Getter
	@Setter
	private Character full;
	@Getter
	@Setter
	private Character empty;

	@Builder
	public ProgressBar(final String begin, final String end, final Character full, final Character empty,
			final String prefix) {
		super();
		this.prefix = Optional.ofNullable(prefix).orElse("");
		this.begin = Optional.ofNullable(begin).orElse("[");
		this.end = Optional.ofNullable(end).orElse("]");
		this.full = Optional.ofNullable(full).orElse('#');
		this.empty = Optional.ofNullable(empty).orElse('-');
	}

	@Override
	public void draw(final PrintStream ps, final Fader fader, final int width, final boolean drawInitialized,
			final int value, final int lastValue) {
		String s = prefix;
		s += begin;
		s += full.toString().repeat(value);
		s += empty.toString().repeat(width - value);
		s += end;
		ps.print(s);
	}

	@Override
	public void remove(final PrintStream ps, final int width, final int lastValue) {
		// Delete already drawn bar using command-characters.
		int len = prefix.length() + begin.length() + width + end.length();
		String s = "\b".repeat(len);
		s += " ".repeat(len);
		s += "\b".repeat(len);
		ps.print(s);
	}
}
