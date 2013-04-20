package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.opengl.GLES20;

/**
 * A multiple filter input extension of the BasicFilter. 
 * This class allows for multiple textures as inputs to the filter. 
 * This class can be used as the base for a filter which requires multiple filter inputs.
 * By itself, this class is not useful because it's fragment shader only uses one texture. 
 * To take advantage of the multiple texture inputs, the getFragmentShader() method should be
 * override to return a more useful fragment shader.  This class supports a maximum of 10
 * input textures.
 * @author Chris Batt
 */
public abstract class MultiInputFilter extends BasicFilter {
	private int numOfInputs;
	private int[] textureHandle;
	private int[] texture;
	private List<GLTextureOutputRenderer> texturesReceived;
	private List<GLTextureOutputRenderer> filterLocations;
	
	/**
	 * Creates a MultiInputFilter with any number of initial filters or filter graphs that produce a
	 * set number of textures which can be used by this filter.
	 * @param numOfInputs
	 * The number of inputs of this filter.  For example, if the fragment shader of this filter 
	 * requires three input textures, then this would be set to three; however, this does not mean 
	 * that there can only be three initial filters.
	 */
	public MultiInputFilter(int numOfInputs) {
		super();
		this.numOfInputs = numOfInputs;
		textureHandle = new int[numOfInputs-1];
		texture = new int[numOfInputs-1];
		texturesReceived = new ArrayList<GLTextureOutputRenderer>(numOfInputs);
		filterLocations = new ArrayList<GLTextureOutputRenderer>(numOfInputs);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		for(int i = 0; i < numOfInputs-1; i++) {
			switch(i) {
				case 0: GLES20.glActiveTexture(GLES20.GL_TEXTURE1); break;
				case 1: GLES20.glActiveTexture(GLES20.GL_TEXTURE2); break;
				case 2: GLES20.glActiveTexture(GLES20.GL_TEXTURE3); break;
				case 3: GLES20.glActiveTexture(GLES20.GL_TEXTURE4); break;
				case 4: GLES20.glActiveTexture(GLES20.GL_TEXTURE5); break;
				case 5: GLES20.glActiveTexture(GLES20.GL_TEXTURE6); break;
				case 6: GLES20.glActiveTexture(GLES20.GL_TEXTURE7); break;
				case 7: GLES20.glActiveTexture(GLES20.GL_TEXTURE8); break;
				case 8: GLES20.glActiveTexture(GLES20.GL_TEXTURE9); break;
			}
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i+1]);
			GLES20.glUniform1f(textureHandle[i+1], i+1);
		}
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		for(int i = 0; i < numOfInputs-1; i++) {
			textureHandle[i+1] = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXTUREBASE+i);
		}
	}
	
	
	/**
	 * Registers the given filter in the given texture location.
	 * @param filter
	 * An output filter which passes its output to this filter.
	 * @param location
	 * The texture location that this filter should pass its output to. This location must be in [0,numOfInputs).
	 */
	public void registerFilter(GLTextureOutputRenderer filter, int location) {
		filterLocations.add(location, filter);
	}
	
	/**
	 * Registers the given filter in the next available texture location.
	 * @param filter
	 * An output filter which passes its output to this filter.
	 */
	public void registerFilter(GLTextureOutputRenderer filter) {
		filterLocations.add(filter);
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.BasicFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public synchronized void newTextureReady(int texture, GLTextureOutputRenderer source) {
		if(!texturesReceived.contains(source)) {
			texturesReceived.add(source);
		}
		int pos = filterLocations.lastIndexOf(source);
		if(pos == 0) {
			texture_in = texture;
		} else {
			this.texture[pos] = texture;
		}
		if(texturesReceived.size() == numOfInputs) {
			width = source.getWidth();
			height = source.getHeight();
			super.onDrawFrame();
		}		
	}
}
