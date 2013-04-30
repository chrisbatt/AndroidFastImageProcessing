package project.android.imageprocessing.filter;

import android.opengl.GLES20;

public class TwoPassMultiPixelFilter extends TwoPassFilter {
	protected static final String UNIFORM_TEXELWIDTH = "u_TexelWidth";
	protected static final String UNIFORM_TEXELHEIGHT = "u_TexelHeight";
	
	protected float texelWidth;
	protected float texelHeight;
	private int texelWidthHandle;
	private int texelHeightHandle;
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		texelWidthHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELWIDTH);
		texelHeightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELHEIGHT);
	}
	
	@Override
	protected void passShaderValues() {
		if(getCurrentPass() == 1) {
			texelWidth = 1.0f / (float)getWidth();
			texelHeight = 0f;
		} else {
			texelWidth = 0f;
			texelHeight = 1.0f / (float)getHeight();
		}
		super.passShaderValues();
		GLES20.glUniform1f(texelWidthHandle, texelWidth);
		GLES20.glUniform1f(texelHeightHandle, texelHeight);
	}

	@Override
	protected void handleSizeChange() {
		super.handleSizeChange();
		texelWidth = 1.0f / (float)getWidth();
		texelHeight = 1.0f / (float)getHeight();
	}
}
