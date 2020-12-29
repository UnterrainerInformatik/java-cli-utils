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
 * file a: [  4%]
 *
 * ...
 *
 * file a: [ 56%]
 *
 * ...
 *
 * file a: [100%]
 * }
 * </pre>
 *
 * This bar ignores the width-parameter for the drawing of the percentage.
 * However you may use it to regulate the sensitivity of the gauge (a higher
 * width updates the gauge more often). Widths over 100 don't make sense in this
 * context since it will be drawn more often but the value won't change.
 * <p>
 * You may specify any other begin- , end- , percent- or empty-character you
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
 * <td><b>percent</b></td>
 * <td>"%"</td>
 * </tr>
 * <tr>
 * <td><b>empty</b></td>
 * <td>{@code ' '}</td>
 * </tr>
 * </table>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PercentGauge implements DrawableComponent {

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
	private String percent;
	@Getter
	@Setter
	private Character empty;

	@Builder
	public PercentGauge(final String begin, final String end, final String percent, final Character empty,
			final String prefix) {
		super();
		this.prefix = Optional.ofNullable(prefix).orElse("");
		this.begin = Optional.ofNullable(begin).orElse("[");
		this.end = Optional.ofNullable(end).orElse("]");
		this.percent = Optional.ofNullable(percent).orElse("%");
		this.empty = Optional.ofNullable(empty).orElse(' ');
	}

	@Override
	public void draw(final PrintStream ps, final Fader fader, final int width, final boolean drawInitialized,
			final int value, final int lastValue) {
		int v = (int) (fader.getPercentage() * 100);

		String s = prefix;
		s += begin;
		if (v < 10)
			s += empty;
		if (v < 100)
			s += empty;
		s += v;
		s += percent;
		s += end;
		ps.print(s);
	}

	@Override
	public void remove(final PrintStream ps, final int width, final int lastValue) {
		// Delete already drawn bar using command-characters.
		String s = "\b".repeat(prefix.length() + begin.length() + percent.length() + 3 + end.length());
		ps.print(s);
	}
}
