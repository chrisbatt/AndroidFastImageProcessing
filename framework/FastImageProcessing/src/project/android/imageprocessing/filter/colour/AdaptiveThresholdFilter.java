package project.android.imageprocessing.filter.colour;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.MultiInputFilter;
import project.android.imageprocessing.filter.MultiTextureFilter;
import project.android.imageprocessing.filter.processing.BoxBlurFilter;

public class AdaptiveThresholdFilter extends MultiTextureFilter {
	
	public AdaptiveThresholdFilter() {
		GreyScaleFilter luminance = new GreyScaleFilter();	
		BoxBlurFilter blur = new BoxBlurFilter();
		ThresholdFilter thresholdFilter = new ThresholdFilter();
		luminance.addTarget(blur);
		blur.addTarget(thresholdFilter);
		luminance.addTarget(thresholdFilter);
		thresholdFilter.addTarget(this);
		thresholdFilter.registerFilter(luminance);
		thresholdFilter.registerFilter(blur);
		
		registerInitialFilter(luminance);
		registerTerminalFilter(thresholdFilter);
	}
	
	private class ThresholdFilter extends MultiInputFilter {
		
		public ThresholdFilter() {
			super(2);
		}
		
		@Override
		protected String getFragmentShader() {
			return 
					"precision mediump float;\n" 
					+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
					+"uniform sampler2D "+UNIFORM_TEXTUREBASE+1+";\n" 
					+"varying vec2 "+VARYING_TEXCOORD+";\n"	
					
			  		+"void main(){\n"
			  		+"   vec4 luminance = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"	
			  		+"   vec4 blur = texture2D("+UNIFORM_TEXTUREBASE+1+","+VARYING_TEXCOORD+");\n"
			  		+"   gl_FragColor = vec4(vec3(step(blur - 0.05, luminance)), 1.0);\n"
			  		+"}\n";	
		}
	}
}
