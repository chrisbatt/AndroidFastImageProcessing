package project.android.imageprocessing.filter.colour;

import project.android.imageprocessing.filter.CompositeFilter;
import project.android.imageprocessing.filter.processing.BoxBlurFilter;

public class AdaptiveThresholdFilter extends CompositeFilter {
	
	public AdaptiveThresholdFilter() {
		super(2);
		GreyScaleFilter luminance = new GreyScaleFilter();	
		BoxBlurFilter blur = new BoxBlurFilter();
		luminance.addTarget(blur);
		blur.addTarget(this);
		luminance.addTarget(this);
		
		registerFilter(luminance, 0);
		registerFilter(blur, 1);
		
		registerInitialFilter(luminance);
		registerTerminalFilter(luminance);
		registerTerminalFilter(blur);
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
