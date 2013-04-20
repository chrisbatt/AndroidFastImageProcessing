package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * A multi-pixel filter extension of the BasicFilter.  This class passes the texel width and height
 * information to the shaders so that neighbouring pixel locations can be calculated in the shader.
 * @author Chris Batt
 */
public abstract class MultiPixelRenderer extends BasicFilter {
	protected static final String UNIFORM_TEXELWIDTH = "u_TexelWidth";
	protected static final String UNIFORM_TEXELHEIGHT = "u_TexelHeight";
	
	private float texelWidth;
	private float texelHeight;
	private int texelWidthHandle;
	private int texelHeightHandle;
	
	/**
	 * Creates a MultiPixelRender that passes the texel width and height information to the shaders.
	 */
	public MultiPixelRenderer() {
		super();
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
	protected void sizeChanged() {
		texelWidth = 1.0f / (float)width;
		texelHeight = 1.0f / (float)height;
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#setRenderSize(int, int)
	 */
	@Override
	public void setRenderSize(int width, int height) {
		super.setRenderSize(width, height);
		texelWidth = 1.0f / (float)width;
		texelHeight = 1.0f / (float)height;
	}
}