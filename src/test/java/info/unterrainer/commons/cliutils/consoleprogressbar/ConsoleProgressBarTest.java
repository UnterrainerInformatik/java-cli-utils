package info.unterrainer.commons.cliutils.consoleprogressbar;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.PercentGauge;
import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.ProgressBar;
import info.unterrainer.commons.cliutils.consoleprogressbar.drawablecomponents.SimpleInsertBar;

public class ConsoleProgressBarTest {

	@Test
	@Disabled
	public void SimpleInsertBarTest() throws InterruptedException {
		ConsoleProgressBar bar = ConsoleProgressBar.builder()
				.minValue(0d)
				.maxValue(100d)
				.component(SimpleInsertBar.builder().prefix("prefix: ").build())
				.build();
		for (int i = 0; i <= 100; i++) {
			bar.updateValue(i);
			bar.redraw(System.out);
			Thread.sleep(30);
		}
		bar.complete().redraw(System.out);
	}

	@Test
	@Disabled
	public void ProgressBarTest() throws InterruptedException {
		ConsoleProgressBar bar = ConsoleProgressBar.builder()
				.minValue(0d)
				.maxValue(100d)
				.component(ProgressBar.builder().prefix("prefix: ").build())
				.build();
		for (int i = 0; i <= 100; i++) {
			bar.updateValue(i);
			bar.redraw(System.out);
			Thread.sleep(30);
		}
		bar.complete().redraw(System.out);
	}

	@Test

	public void PercentGaugeTest() throws InterruptedException {
		ConsoleProgressBar bar = ConsoleProgressBar.builder()
				.minValue(0d)
				.maxValue(100d)
				.component(PercentGauge.builder().prefix("prefix: ").build())
				.build();
		for (int i = 0; i <= 100; i++) {
			bar.updateValue(i);
			bar.redraw(System.out);
			Thread.sleep(30);
		}
		bar.complete().redraw(System.out);
	}
}
