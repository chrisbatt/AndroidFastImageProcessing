package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;

public class OpeningRGBFilter extends GroupFilter {
	public OpeningRGBFilter(int radius) {
		ErosionRGBFilter erosion = new ErosionRGBFilter(radius);
		DilationRGBFilter dilation = new DilationRGBFilter(radius);
		erosion.addTarget(dilation);
		dilation.addTarget(this);
		
		registerInitialFilter(erosion);
		registerTerminalFilter(dilation);
	}
}
