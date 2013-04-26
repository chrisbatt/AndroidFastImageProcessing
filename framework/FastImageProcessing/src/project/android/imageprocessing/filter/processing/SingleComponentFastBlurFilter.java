package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.MultiPixelRenderer;
import project.android.imageprocessing.filter.GroupFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;

public class SingleComponentFastBlurFilter extends GroupFilter {
	public SingleComponentFastBlurFilter() {
		AbstractFilter firstPass = new AbstractFilter(AbstractFilter.PASS_VERTICAL);
		AbstractFilter secondPass = new AbstractFilter(AbstractFilter.PASS_HORIZONTAL);
		firstPass.addTarget(secondPass);
		secondPass.addTarget(this);
		
		registerInitialFilter(firstPass);
		registerTerminalFilter(secondPass);	
	}
	
	private class AbstractFilter extends MultiPixelRenderer {
		private static final int PASS_VERTICAL = 0;
		private static final int PASS_HORIZONTAL = 1;
		
		private int passType;
		
		public AbstractFilter(int passType) {
			this.passType = passType;
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
							
					
			  		+"void main(){\n"
			  		+"   vec2 firstOffset = vec2(1.3846153846 * "+UNIFORM_TEXELWIDTH+", 1.3846153846 * "+UNIFORM_TEXELHEIGHT+");\n"
			  		+"   vec2 secondOffset = vec2(3.2307692308 * "+UNIFORM_TEXELWIDTH+", 3.2307692308 * "+UNIFORM_TEXELHEIGHT+");\n"
					+"   float sum = 0.0;\n"
			  		+"   vec4 color = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+");\n"
					+"   sum += color.r * 0.2270270270;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - firstOffset).r * 0.3162162162;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + firstOffset).r * 0.3162162162;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - secondOffset).r * 0.0702702703;\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + secondOffset).r * 0.0702702703;\n"
			  		+"   gl_FragColor = vec4(vec3(sum), color.a);\n"
			  		+"}\n";
		}
	}
}
