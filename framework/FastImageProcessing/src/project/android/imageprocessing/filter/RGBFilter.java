package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * A red, green and blue alteration filter extension of the BasicFilter. 
 * This class allows the alteration of each pixel by multiplying each of red, green
 * and blue by constant floats.
 * @author Chris Batt
 */
public class RGBFilter extends BasicFilter {
	protected static final String UNIFORM_REDPART = "u_Red";
	protected static final String UNIFORM_GREENPART = "u_Green";
	protected static final String UNIFORM_BLUEPART = "u_Blue";
	
	private int redHandle;
	private int greenHandle;
	private int blueHandle;
	private float red;
	private float green;
	private float blue;
	
	/**
	 * Creates a RGBFilter which sets each pixel in the given image to a new color specified 
	 * by the multiplication constants for each of red, green and blue.
	 * @param red
	 * The constant float value to multiply all red values by.
	 * @param green
	 * The constant float value to multiply all green values by.
	 * @param blue
	 * The constant float value to multiply all blue values by.
	 */
	public RGBFilter(float red, float green, float blue) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		redHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_REDPART);
		greenHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_GREENPART);
		blueHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_BLUEPART);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(redHandle, red);
		GLES20.glUniform1f(greenHandle, green);
		GLES20.glUniform1f(blueHandle, blue);
	} 
	
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_REDPART+";\n"
				+"uniform float "+UNIFORM_GREENPART+";\n"
				+"uniform float "+UNIFORM_BLUEPART+";\n"
				
		  		+"void main(){\n"
		  		+"   vec4 color = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"	
				+"   gl_FragColor = vec4(color.r * "+UNIFORM_REDPART+", color.g * "+UNIFORM_GREENPART+", color.b * "+UNIFORM_BLUEPART+", color.a);\n"
		  		+"}\n";
	}
}
