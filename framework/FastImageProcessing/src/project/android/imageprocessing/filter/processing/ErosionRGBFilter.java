package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.MultiPixelRenderer;

public class ErosionRGBFilter extends GroupFilter {
	public ErosionRGBFilter(int erosionRadius) {
		AbstractFilter firstPass = new AbstractFilter(erosionRadius, AbstractFilter.PASS_VERTICAL);
		AbstractFilter secondPass = new AbstractFilter(erosionRadius, AbstractFilter.PASS_HORIZONTAL);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);	
	}
	
	private class AbstractFilter extends MultiPixelRenderer {
		private static final int PASS_VERTICAL = 0;
		private static final int PASS_HORIZONTAL = 1;
		
		private int passType;
		
		private int erosionRadius;
		
		public AbstractFilter(int erosionRadius, int passType) {
			this.passType = passType;
			this.erosionRadius = erosionRadius;
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
					+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
					+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
					+"const int dilationSize = "+(erosionRadius*2+1)+";\n"
					+"const int dilationRadius = "+erosionRadius+";\n"
					
			  		+"void main(){\n"
			  		+"   vec2 step = vec2("+UNIFORM_TEXELWIDTH+", "+UNIFORM_TEXELHEIGHT+");\n"
			  		+"   vec4 stepIntensity[dilationSize];\n"
			  		+"   for(int i = 0; i < dilationSize; i++) {\n"
			  		+"     stepIntensity[i] = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + step * float(i - dilationRadius));\n"
			  		+"   }\n"
			  		
			  		+"   vec4 minValue = vec4(1.0);\n"
			  		+"   for(int i = 0; i < dilationSize; i++) {\n"
			  		+"     minValue = min(minValue, stepIntensity[i]);\n"
			  		+"   }\n"
			  		+"   gl_FragColor = minValue;\n"
			  		+"}\n";
		}
	}
}
