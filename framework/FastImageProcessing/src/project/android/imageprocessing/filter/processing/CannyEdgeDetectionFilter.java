package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;

public class CannyEdgeDetectionFilter extends GroupFilter {
	public CannyEdgeDetectionFilter(float blurSize, float lowerThreshold, float upperThreshold) {
		GreyScaleFilter grey = new GreyScaleFilter();
		GaussianBlurFilter blur = new GaussianBlurFilter(blurSize);
		DirectionalSobelEdgeDetectionFilter sobel = new DirectionalSobelEdgeDetectionFilter();
		DirectionalNonMaximumSuppressionFilter suppression = new DirectionalNonMaximumSuppressionFilter(upperThreshold, lowerThreshold);
		WeakPixelInclusionFilter weakPixel = new WeakPixelInclusionFilter();
		grey.addTarget(blur);
		blur.addTarget(sobel);
		sobel.addTarget(suppression);
		suppression.addTarget(weakPixel);
		weakPixel.addTarget(this);
		
		registerInitialFilter(grey);
		registerFilter(blur);
		registerFilter(sobel);
		registerFilter(suppression);
		registerTerminalFilter(weakPixel);
	}
}
