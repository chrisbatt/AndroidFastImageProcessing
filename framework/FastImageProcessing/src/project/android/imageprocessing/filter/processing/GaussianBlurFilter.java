package project.android.imageprocessing.filter.processing;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.MultiPixelRenderer;
import project.android.imageprocessing.filter.GroupFilter;

public class GaussianBlurFilter extends GroupFilter {
	public GaussianBlurFilter(float blurSize) {
		AbstractFilter firstPass = new AbstractFilter(blurSize, AbstractFilter.PASS_VERTICAL);
		AbstractFilter secondPass = new AbstractFilter(blurSize, AbstractFilter.PASS_HORIZONTAL);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);	
	}
	
	private class AbstractFilter extends MultiPixelRenderer {
		private static final int PASS_VERTICAL = 0;
		private static final int PASS_HORIZONTAL = 1;
		
		private int passType;
		
		private static final String UNIFORM_BLUR_SIZE = "u_BlurSize";
		
		private int blurSizeHandle;
		private float blurSize;
		
		public AbstractFilter(float blurSize, int passType) {
			this.blurSize = blurSize;
			this.passType = passType;
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
		protected void handleSizeChange() {
			switch(passType) {
				case PASS_VERTICAL: texelWidth = 1.0f / (float)getWidth();
									texelHeight = 0f;
									break;
				case PASS_HORIZONTAL: 	texelWidth = 0f;
										texelHeight = 1.0f / (float)getHeight();
										break;
			}
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
					+"   vec3 sum = vec3(0,0,0);\n"
					+"   vec4 color = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[4]);\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[0]).rgb * 0.05;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[1]).rgb * 0.09;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[2]).rgb * 0.12;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[3]).rgb * 0.15;\n"
					+"   sum += color.rgb * 0.18;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[5]).rgb * 0.15;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[6]).rgb * 0.12;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[7]).rgb * 0.09;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[8]).rgb * 0.05;\n"
			  		+"   gl_FragColor = vec4(sum, color.a);\n"
			  		+"}\n";
		}
	}
}
