package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;

public class ClosingRGBFilter extends GroupFilter {
	public ClosingRGBFilter(int radius) {
		DilationRGBFilter dilation = new DilationRGBFilter(radius);
		ErosionRGBFilter erosion = new ErosionRGBFilter(radius);
		dilation.addTarget(erosion);
		erosion.addTarget(this);
		
		registerInitialFilter(dilation);
		registerTerminalFilter(erosion);
	}
}
