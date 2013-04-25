package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.MultiPixelRenderer;
import project.android.imageprocessing.filter.GroupFilter;

public class BoxBlurFilter extends GroupFilter {
	
	public BoxBlurFilter() {
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
					+"   vec2 firstOffset = vec2(1.5 * "+UNIFORM_TEXELWIDTH+", 1.5 * "+UNIFORM_TEXELHEIGHT+");\n"
					+"   vec2 secondOffset = vec2(3.5 * "+UNIFORM_TEXELWIDTH+", 3.5 * "+UNIFORM_TEXELHEIGHT+");\n"
					+"   vec4 sum = vec4(0,0,0,0);\n"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+") * 0.2;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - firstOffset) * 0.2;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + firstOffset) * 0.2;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - secondOffset) * 0.2;"
					+"   sum += texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + secondOffset) * 0.2;"
			  		+"   gl_FragColor = sum;\n"
			  		+"}\n";
		}
	}
}
