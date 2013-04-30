package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.TwoPassMultiPixelFilter;

public class DilationRGBFilter extends TwoPassMultiPixelFilter {
	private int dilationRadius;
	
	public DilationRGBFilter(int dilationRadius) {
		this.dilationRadius = dilationRadius;
	}
	
	
	@Override
	protected String getFragmentShader() {
		return 
				"precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
				+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
				+"const int dilationSize = "+(dilationRadius*2+1)+";\n"
				+"const int dilationRadius = "+dilationRadius+";\n"
				
		  		+"void main(){\n"
		  		+"   vec2 step = vec2("+UNIFORM_TEXELWIDTH+", "+UNIFORM_TEXELHEIGHT+");\n"
		  		+"   vec4 stepIntensity[dilationSize];\n"
		  		+"   for(int i = 0; i < dilationSize; i++) {\n"
		  		+"     stepIntensity[i] = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + step * float(i - dilationRadius));\n"
		  		+"   }\n"
		  		
		  		+"   vec4 maxValue = vec4(0,0,0,0);\n"
		  		+"   for(int i = 0; i < dilationSize; i++) {\n"
		  		+"     maxValue = max(maxValue, stepIntensity[i]);\n"
		  		+"   }\n"
		  		+"   gl_FragColor = maxValue;\n"
		  		+"}\n";
	}
}
