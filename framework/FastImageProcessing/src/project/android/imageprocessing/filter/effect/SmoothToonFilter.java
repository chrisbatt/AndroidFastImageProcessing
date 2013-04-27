package project.android.imageprocessing.filter.effect;

import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.processing.GaussianBlurFilter;

public class SmoothToonFilter extends GroupFilter {
	public SmoothToonFilter(float blurSize, float threshold, float quantizationLevels) {
		GaussianBlurFilter blur = new GaussianBlurFilter(blurSize);
		ToonFilter toon = new ToonFilter(threshold, quantizationLevels);
		blur.addTarget(toon);
		toon.addTarget(this);
		
		registerInitialFilter(blur);
		registerTerminalFilter(toon);
	}
}
