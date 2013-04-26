package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;

public class ClosingFilter extends GroupFilter {
	public ClosingFilter(int radius) {
		DilationFilter dilation = new DilationFilter(radius);
		ErosionFilter erosion = new ErosionFilter(radius);
		dilation.addTarget(erosion);
		erosion.addTarget(this);
		
		registerInitialFilter(dilation);
		registerTerminalFilter(erosion);
	}
}
