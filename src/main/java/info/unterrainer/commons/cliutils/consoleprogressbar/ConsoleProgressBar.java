package info.unterrainer.commons.cliutils.consoleprogressbar;

import java.io.PrintStream;
import java.util.Optional;

import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.DrawableComponent;
import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.ProgressBar;
import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.SimpleInsertBar;
import info.unterrainer.commons.datastructures.Fader;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This class enables your console-applications to draw a progress-bar.
 * <p>
 * You may specify if your console supports control-characters (like
 * standard-out) or not (like the Eclipse console-implementation (before Mars
 * (4.5)) or a pipe to a file) if you'd like to use one of the two standard
 * {@link DrawableComponent} implementations.<br>
 * You also may implement your own {@link DrawableComponent} and use that in
 * your applications.
 * <p>
 * Default values are:
 * <table>
 * <caption>Default values</caption>
 * <tr>
 * <td><b>width</b></td>
 * <td>50</td>
 * </tr>
 * <tr>
 * <td><b>minValue</b></td>
 * <td>0.0d</td>
 * </tr>
 * <tr>
 * <td><b>maxValue</b></td>
 * <td>1.0d</td>
 * </tr>
 * <tr>
 * <td><b>controlCharacterSupport</b></td>
 * <td>true</td>
 * </tr>
 * </table>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ConsoleProgressBar {

	@Getter
	private Fader fader;
	private double minValue;
	private double maxValue;

	@Getter
	@Setter
	private boolean controlCharacterSupport;

	@Getter
	@Setter
	private int width;

	@Getter
	private boolean drawInitialized = false;
	private int lastNumberOfCharactersDrawn;
	@Getter
	private DrawableComponent component;

	@Builder
	public ConsoleProgressBar(final Integer width, final Double minValue, final Double maxValue,
			final Boolean controlCharacterSupport, final DrawableComponent component) {

		this.minValue = Optional.ofNullable(minValue).orElse(0.0d);
		this.maxValue = Optional.ofNullable(maxValue).orElse(1.0d);
		this.width = Optional.ofNullable(width).orElse(50);
		this.controlCharacterSupport = Optional.ofNullable(controlCharacterSupport).orElse(true);

		if (component == null) {
			if (this.controlCharacterSupport)
				this.component = ProgressBar.builder().build();
			else
				this.component = SimpleInsertBar.builder().build();
		} else
			this.component = component;
	}

	private void checkFader() {
		if (fader == null)
			fader = new Fader(minValue, maxValue);
	}

	/**
	 * Updates the bar to a given value.
	 *
	 * @param v the value to set the bar to
	 * @return the console progress bar
	 */
	public ConsoleProgressBar updateValue(final double v) {
		checkFader();
		fader.setValue(v);
		return this;
	}

	/**
	 * Updates the bar by a given value.
	 *
	 * @param v the value
	 * @return the console progress bar
	 */
	public ConsoleProgressBar updateValueBy(final double v) {
		checkFader();
		fader.setValue(fader.getValue() + v);
		return this;
	}

	/**
	 * Updates the bar to a given percentage.
	 *
	 * @param p the percentage to set the bar to
	 * @return the console progress bar
	 */
	public ConsoleProgressBar updatePercentage(final double p) {
		checkFader();
		fader.setPercentage(p);
		return this;
	}

	/**
	 * Updates the bar by a given percentage.
	 *
	 * @param p the percentage
	 * @return the console progress bar
	 */
	public ConsoleProgressBar updatePercentageBy(final double p) {
		checkFader();
		fader.setPercentage(fader.getPercentage() + p);
		return this;
	}

	/**
	 * Resets the bar to its minimum value.
	 *
	 * @return the console progress bar
	 */
	public ConsoleProgressBar reset() {
		checkFader();
		fader.setValue(fader.getMinimalValue());
		return this;
	}

	/**
	 * Sets the bar to its maximum value.
	 *
	 * @return the console progress bar
	 */
	public ConsoleProgressBar complete() {
		checkFader();
		fader.setValue(fader.getMaximalValue());
		return this;
	}

	/**
	 * Decides if a redraw is necessary.
	 * <p>
	 * This is the case if the last drawn value differs from the current one or if
	 * the component hasn't been drawn yet at all.
	 *
	 * @return a boolean value
	 */
	public boolean isRedrawNecessary() {
		return !drawInitialized || (int) (fader.getPercentage() * width) != lastNumberOfCharactersDrawn;
	}

	/**
	 * Redraws the draw-able component of this progress bar by calling
	 * {@link #remove(PrintStream)} and then {@link #draw(PrintStream)}.
	 * <p>
	 * Please call this as frequent as possible.<br>
	 * The real remove-draw call will only be issued if there really has been a
	 * change in the graphical representation of the bar.
	 *
	 * @param ps the print-stream to draw to
	 * @return the console progress bar
	 */
	public ConsoleProgressBar redraw(final PrintStream ps) {
		if (ps != null) {
			checkFader();
			int fullNumber = (int) (fader.getPercentage() * width);

			if (isRedrawNecessary()) {
				if (drawInitialized)
					component.remove(ps, width, lastNumberOfCharactersDrawn);
				component.draw(ps, fader, width, drawInitialized, fullNumber, lastNumberOfCharactersDrawn);
				drawInitialized = true;
				lastNumberOfCharactersDrawn = fullNumber;
			}
		}
		return this;
	}

	/**
	 * This method will draw the component. It will not remove the component first
	 * and it will draw at any circumstances.<br>
	 * If you want to re-draw it (update its values visually) consider calling
	 * redraw instead.
	 *
	 * @param ps the print-stream to draw to
	 * @return the console progress bar
	 */
	public ConsoleProgressBar draw(final PrintStream ps) {
		if (ps != null) {
			checkFader();
			int fullNumber = (int) (fader.getPercentage() * width);
			component.draw(ps, fader, width, drawInitialized, fullNumber, lastNumberOfCharactersDrawn);
			drawInitialized = true;
			lastNumberOfCharactersDrawn = fullNumber;
			ps.flush();
		}
		return this;
	}

	/**
	 * This method removes the component from the print-stream if possible.
	 * <p>
	 * If you use a component that cannot do so, e.g. components designed for
	 * consoles with no control-character support, then this call will do nothing.
	 *
	 * @param ps the print-stream to draw to
	 * @return the console progress bar
	 */
	public ConsoleProgressBar remove(final PrintStream ps) {
		if (ps != null) {
			checkFader();
			if (drawInitialized) {
				component.remove(ps, width, lastNumberOfCharactersDrawn);
				ps.flush();
			}
		}
		return this;
	}
}
