package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.opengl.GLES20;

/**
 * A multiple filter input extension of the BasicFilter. 
 * This class allows for multiple textures as inputs to the filter. 
 * This class can be used as the base for a filter which requires multiple filter inputs.
 * By itself, this class is not useful because it's fragment shader only uses one texture. 
 * To take advantage of the multiple texture inputs, the getFragmentShader() method should be
 * override to return a more useful fragment shader.
 * @author Chris Batt
 */
public abstract class MultiInputFilter extends BasicFilter {
	private int numOfEndpoints;
	private int[] textureHandle;
	private int[] texture;
	private List<BasicFilter> initialFilters;
	private List<BasicFilter> filterEndPoints;
	
	/**
	 * Creates a MultiInputFilter with any number of initial filters or filter graphs that produce a
	 * set number of textures which can be used by this filter.
	 * @param numOfEndpoints
	 * The number of endpoints of this filter.  For example, if the fragment shader of this filter 
	 * requires three input textures, then this would be set to three; however, this does not mean 
	 * that there can only be three initial filters.
	 */
	public MultiInputFilter(int numOfEndpoints) {
		super();
		this.numOfEndpoints = numOfEndpoints;
		textureHandle = new int[numOfEndpoints-1];
		texture = new int[numOfEndpoints-1];
		initialFilters = new ArrayList<BasicFilter>(numOfEndpoints);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		for(int i = 0; i < numOfEndpoints-1; i++) {
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
		for(int i = 0; i < numOfEndpoints-1; i++) {
			textureHandle[i+1] = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXTUREBASE+i);
		}
	}
	
	protected void addInitialFilter(BasicFilter filter, BasicFilter endpoint) {
		initialFilters.add(filter);
		if(!filterEndPoints.contains(endpoint)) {
			filterEndPoints.add(endpoint);
		}
	}
	
	protected void addInitialFilter(BasicFilter filter) {
		initialFilters.add(filter);
		if(!filterEndPoints.contains(filter)) {
			filterEndPoints.add(filter);
		}
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.BasicFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		if(filterEndPoints.size() != numOfEndpoints) {
			return;
		}
		
		/*
		 * If the source is one of the end points of the input filters then it is the result 
		 * of one of the internal filters. When all internal filters have finished we can
		 * draw the multi-input filter. If the source is not in the list of renderers then it 
		 * must be an external input which should be passed to each of the initial renderers
		 * of this multi-input filter.
		 */
		if(filterEndPoints.contains(source)) {
			int pos = filterEndPoints.lastIndexOf(source);
			if(pos == 0) {
				texture_in = texture;
			} else {
				this.texture[pos] = texture;
			}
			if(pos == numOfEndpoints-1) {
				width = source.getWidth();
				height = source.getHeight();
				super.onDrawFrame();
			}
		} else {
			for(BasicFilter initialFilter : initialFilters) {
				initialFilter.newTextureReady(texture, source);
			}
		}
		
	}
}
