package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;

public class OpeningFilter extends GroupFilter {
	public OpeningFilter(int radius) {
		ErosionFilter erosion = new ErosionFilter(radius);
		DilationFilter dilation = new DilationFilter(radius);
		erosion.addTarget(dilation);
		dilation.addTarget(this);
		
		registerInitialFilter(erosion);
		registerTerminalFilter(dilation);
	}
}
