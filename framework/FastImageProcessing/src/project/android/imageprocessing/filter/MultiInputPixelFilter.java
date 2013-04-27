package project.android.imageprocessing.filter;

import android.opengl.GLES20;

public class MultiInputPixelFilter extends MultiInputFilter {
	protected static final String UNIFORM_TEXELWIDTH = "u_TexelWidth";
	protected static final String UNIFORM_TEXELHEIGHT = "u_TexelHeight";
	
	protected float texelWidth;
	protected float texelHeight;
	private int texelWidthHandle;
	private int texelHeightHandle;
	
	/**
	 * Creates a MultiInputPixelFilter that passes the texel width and height information to the shaders and
	 * accepts multiple textures as input.
	 */
	public MultiInputPixelFilter(int numOfInputs) {
		super(numOfInputs);
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		texelWidthHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELWIDTH);
		texelHeightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELHEIGHT);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(texelWidthHandle, texelWidth);
		GLES20.glUniform1f(texelHeightHandle, texelHeight);
	}
	
	@Override
	protected void handleSizeChange() {
		texelWidth = 1.0f / (float)getWidth();
		texelHeight = 1.0f / (float)getHeight();
	}

}
