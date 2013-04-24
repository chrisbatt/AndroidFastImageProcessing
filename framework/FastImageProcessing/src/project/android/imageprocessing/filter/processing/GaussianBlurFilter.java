package project.android.imageprocessing.filter.processing;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.MultiPixelRenderer;
import project.android.imageprocessing.filter.MultiTextureFilter;

public class GaussianBlurFilter extends MultiTextureFilter {

	public GaussianBlurFilter(float blurSize) {
		SinglePassGaussianBlurFilter firstPass = new SinglePassGaussianBlurFilter(blurSize);
		SinglePassGaussianBlurFilter secondPass = new SinglePassGaussianBlurFilter(blurSize);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);
	}
	
	private class SinglePassGaussianBlurFilter extends MultiPixelRenderer {	
		private static final String UNIFORM_BLUR_SIZE = "u_BlurSize";
		
		private float blurSize;
		private int blurSizeHandle;
		
		public SinglePassGaussianBlurFilter(float blurSize) {
			this.blurSize = blurSize;
		}
		
		@Override
		protected void initShaderHandles() {
			super.initShaderHandles();
			blurSizeHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_BLUR_SIZE);
		}
		
		@Override
		protected void passShaderValues() {
			super.passShaderValues();
			GLES20.glUniform1f(blurSizeHandle, blurSize);
		} 
		
		@Override
		protected String getFragmentShader() {
			return 
					 "precision mediump float;\n" 
					+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
					+"varying vec2 "+VARYING_TEXCOORD+";\n"	
					+"uniform float "+UNIFORM_BLUR_SIZE+";\n"
					+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
					+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
							
					
			  		+"void main(){\n"
					+"   vec2 singleStepOffset = vec2("+UNIFORM_TEXELWIDTH+", "+UNIFORM_TEXELHEIGHT+");\n"
					+"   int multiplier = 0;\n"
					+"   vec2 blurStep = vec2(0,0);\n"
					+"   vec2 blurCoordinates[9];"
					+"   for(int i = 0; i < 9; i++) {\n"
					+"     multiplier = (i - 4);\n"
					+"     blurStep = float(multiplier) * singleStepOffset;\n"
					+"     blurCoordinates[i] = "+VARYING_TEXCOORD+".xy + blurStep;\n"
					+"   }\n"
					+"   vec4 sum = vec4(0,0,0,0);\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[0]) * 0.05;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[1]) * 0.09;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[2]) * 0.12;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[3]) * 0.15;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[4]) * 0.18;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[5]) * 0.15;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[6]) * 0.12;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[7]) * 0.09;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[8]) * 0.05;"
			  		+"   gl_FragColor = sum;\n"
			  		+"}\n";
		}
	}

}
