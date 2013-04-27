package project.android.imageprocessing.filter.effect;

import project.android.imageprocessing.filter.processing.ConvolutionFilter;

public class EmbossFilter extends ConvolutionFilter {
	public EmbossFilter(float intensity) {
		super(new float[] {
				intensity * -2, -intensity, 0f,
				-intensity, 1f, intensity,
				0f, intensity, intensity * 2
		}, 3, 3);
	}
}
