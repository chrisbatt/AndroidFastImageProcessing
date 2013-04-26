package project.android.imageprocessing.filter.processing;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.MultiPixelRenderer;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;

public class ThresholdEdgeDetectionFilter extends GroupFilter {
	public ThresholdEdgeDetectionFilter(float threshold) {
		GreyScaleFilter firstPass = new GreyScaleFilter();
		AbstractFilter secondPass = new AbstractFilter(threshold);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);	
	}
	
	private class AbstractFilter extends MultiPixelRenderer {
		private static final String UNIFORM_THRESHOLD = "u_Threshold";
			
		private int thresholdHandle;
		private float threshold;

		public AbstractFilter(float threshold) {
			this.threshold = threshold;
		}
		
		@Override
		protected void initShaderHandles() {
			super.initShaderHandles();
			thresholdHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_THRESHOLD);
		}
		
		@Override
		protected void passShaderValues() {
			super.passShaderValues();
			GLES20.glUniform1f(thresholdHandle, threshold);
		}
		
		@Override
		protected String getFragmentShader() {
			return 
					 "precision mediump float;\n" 
					+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
					+"varying vec2 "+VARYING_TEXCOORD+";\n"	
					+"uniform float "+UNIFORM_THRESHOLD+";\n"
					+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
					+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
							
					
			  		+"void main(){\n"
			  		+"   vec2 up = vec2(0.0, "+UNIFORM_TEXELHEIGHT+");\n"
			  		+"   vec2 right = vec2("+UNIFORM_TEXELWIDTH+", 0.0);\n"
			  		+"   float bottomLeftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up - right).r;\n"
			  		+"   float topRightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up + right).r;\n"
				    +"   float topLeftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up - right).r;\n"
				    +"   float bottomRightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up + right).r;\n"
				    +"   float leftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - right).r;\n"
				    +"   float rightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + right).r;\n"
				    +"   float bottomIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up).r;\n"
				    +"   float topIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up).r;\n"
				    +"   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n"
				    +"   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n"
				     
				    +"   float mag = length(vec2(h, v));\n"
				    +"   mag = step("+UNIFORM_THRESHOLD+", mag);\n"
				     
				    +"   gl_FragColor = vec4(vec3(mag), 1.0);\n"
			  		+"}\n";
		}
	}
}
