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
 * file a: [>>>>>>>>>>>>>>>]
 *          ####
 *
 *  ...
 *
 * file a: [>>>>>>>>>>>>>>>]
 *          ############
 *
 *  ...
 *
 * file a: [>>>>>>>>>>>>>>>]
 *          ###############
 * }
 * </pre>
 *
 * ... with an ever growing number of '#' characters.<br>
 * You may specify any other begin- , end- , full- or legendFill-character you
 * like. <br>
 * This bar is always working. Even if your console doesn't support control
 * characters like the Eclipse console-implementation (before Mars (4.5)) or a
 * pipe to a file.
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
 * <td><b>legendFill</b></td>
 * <td>'&gt;'</td>
 * </tr>
 * </table>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleInsertBar implements DrawableComponent {

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
	private Character legendFill;

	@Builder
	public SimpleInsertBar(final String prefix, final String begin, final String end, final Character full,
			final Character legendFill) {
		super();
		this.prefix = Optional.ofNullable(prefix).orElse("");
		this.begin = Optional.ofNullable(begin).orElse("[");
		this.end = Optional.ofNullable(end).orElse("]");
		this.full = Optional.ofNullable(full).orElse('#');
		this.legendFill = Optional.ofNullable(legendFill).orElse('>');
	}

	@Override
	public void draw(final PrintStream ps, final Fader fader, final int width, final boolean drawInitialized,
			final int value, final int lastValue) {
		if (!drawInitialized) {
			// Draw the lead-line, the legend to the bar.
			int l = begin.length() + prefix.length();
			ps.print(prefix + begin + legendFill.toString().repeat(width) + end + "\n" + " ".repeat(l));
		}

		ps.print(full.toString().repeat(value - lastValue));
	}

	@Override
	public void remove(final PrintStream ps, final int width, final int lastValue) {
	}
}
