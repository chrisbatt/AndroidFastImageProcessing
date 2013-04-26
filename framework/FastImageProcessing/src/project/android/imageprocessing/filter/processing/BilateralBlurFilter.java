package project.android.imageprocessing.filter.processing;

import android.opengl.GLES20;
import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.MultiPixelRenderer;

public class BilateralBlurFilter extends GroupFilter {
	public BilateralBlurFilter(float distanceNormalizationFactor) {
		AbstractFilter firstPass = new AbstractFilter(distanceNormalizationFactor, AbstractFilter.PASS_VERTICAL);
		AbstractFilter secondPass = new AbstractFilter(distanceNormalizationFactor, AbstractFilter.PASS_HORIZONTAL);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);	
	}
	
	private class AbstractFilter extends MultiPixelRenderer {
		private static final int PASS_VERTICAL = 0;
		private static final int PASS_HORIZONTAL = 1;
		
		private int passType;
		
		private static final String UNIFORM_DISTANCE_NORMALIZATION = "u_DistanceNormalization";
		
		private int distanceNormalizationHandle;
		private float distanceNormalization;
		
		public AbstractFilter(float distanceNormalization, int passType) {
			this.distanceNormalization = distanceNormalization;
			this.passType = passType;
		}
			
		@Override
		protected void initShaderHandles() {
			super.initShaderHandles();
			distanceNormalizationHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_DISTANCE_NORMALIZATION);
		}
		
		@Override
		protected void passShaderValues() {
			super.passShaderValues();
			GLES20.glUniform1f(distanceNormalizationHandle, distanceNormalization);
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
					+"uniform float "+UNIFORM_DISTANCE_NORMALIZATION+";\n"
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
					
					+"   vec4 centralColor;\n"
					+"   float gaussianWeightTotal;\n"
					+"   vec4 sum;\n"
					+"   vec4 sampleColor;\n"
					+"   float distanceFromCentralColor;\n"
					+"   float gaussianWeight;\n"
					     
					+"   centralColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[4]);\n"
					+"   gaussianWeightTotal = 0.18;\n"
					+"   sum = centralColor * 0.18;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[0]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[1]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[2]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[3]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[5]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[6]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[7]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   sampleColor = texture2D("+UNIFORM_TEXTURE0+", blurCoordinates[8]);\n"
					+"   distanceFromCentralColor = min(distance(centralColor, sampleColor) * "+UNIFORM_DISTANCE_NORMALIZATION+", 1.0);\n"
					+"   gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n"
					+"   gaussianWeightTotal += gaussianWeight;\n"
					+"   sum += sampleColor * gaussianWeight;\n"
					     
					+"   gl_FragColor = sum / gaussianWeightTotal;\n"
					+"}\n";
		}
	}
}
