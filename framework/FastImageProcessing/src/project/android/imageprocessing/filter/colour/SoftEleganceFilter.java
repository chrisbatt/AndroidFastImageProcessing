package project.android.imageprocessing.filter.colour;

import android.content.Context;
import project.android.fastimageprocessing.R;
import project.android.imageprocessing.filter.MultiTextureFilter;
import project.android.imageprocessing.filter.blend.AlphaBlendFilter;
import project.android.imageprocessing.filter.processing.GaussianBlurFilter;

public class SoftEleganceFilter extends MultiTextureFilter {

	public SoftEleganceFilter(Context context) {
		LookupFilter img1 = new LookupFilter(context, R.drawable.lookup_soft_elegance_1);
		GaussianBlurFilter gaussianBlur = new GaussianBlurFilter(9.7f);
		AlphaBlendFilter alphaBlend = new AlphaBlendFilter(0.14f);
		LookupFilter img2 = new LookupFilter(context, R.drawable.lookup_soft_elegance_2);
		img1.addTarget(gaussianBlur);
		img1.addTarget(alphaBlend);
		gaussianBlur.addTarget(alphaBlend);
		alphaBlend.registerFilter(img1);
		alphaBlend.registerFilter(gaussianBlur);
		alphaBlend.addTarget(img2);
		img2.addTarget(this);
		
		registerInitialFilter(img1);
		registerTerminalFilter(img2);
	}
}
