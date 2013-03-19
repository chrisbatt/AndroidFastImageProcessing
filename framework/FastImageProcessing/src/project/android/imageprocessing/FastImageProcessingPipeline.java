package project.android.imageprocessing;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.GLTextureInputRenderer;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;


/**
 * Filter pipeline renderer implementation of the GLSurfaceView.Renderer.  Implements all of the rendering implementations.
 * In addition to the GLSurfaceView.Renderer methods, this class provides methods for processing the given graph of filters.
 * This graph of filters can be set by creating the filter graph and then passing the root of the graph to this class using
 * setRootRenderer(GLRenderer rootRenderer).
 * @author Chris Batt
 */
public class FastImageProcessingPipeline implements Renderer {
	private boolean treeChanged;
	private boolean rendering;
	private boolean previousRenderingState;
	private GLRenderer rootRenderer;
	private int width;
	private int height;
	private List<GLRenderer> processors;
	
	/**
	 * Creates a FastImageProcessingPipeline with the initial state as paused and having no rootRenderer.
	 */
	public FastImageProcessingPipeline() {
		processors = new ArrayList<GLRenderer>();
		rendering = false;
		previousRenderingState = false;
	}
	
	/**
	 * Sets the root node of graph of filters that the pipeline will process and draw to the given endpoints of the graph.
	 * @param rootRenderer 
	 * The root node (input node) of the graph of filters and endpoints.
	 */
	public synchronized void setRootRenderer(GLRenderer rootRenderer) {
		this.rootRenderer = rootRenderer;
		treeChanged = true;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public synchronized void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(0, 0, 0, 1);
		if(treeChanged && rootRenderer != null) {
			initChanged();
			treeChanged = false;
		}
		if(rendering) {
			rootRenderer.onDrawFrame();
		}
	}
	
	private void initChanged() {
		List<GLRenderer> newTree = new ArrayList<GLRenderer>();
		newTree.add(rootRenderer);
		findChildren(newTree, rootRenderer);
		List<GLRenderer> changed = new ArrayList<GLRenderer>();
		changed.addAll(newTree);
		changed.removeAll(processors);
		processors.clear();
		processors.addAll(newTree);
		for(GLRenderer render : changed) {
			render.onSurfaceCreated();
		}
	}

	private void findChildren(List<GLRenderer> nodes, GLRenderer node) {
		if(!(node instanceof GLTextureOutputRenderer)) {
			return;
		}
		for(GLTextureInputRenderer target : ((GLTextureOutputRenderer)node).getTargets()) {
			if(target instanceof GLRenderer) {
				nodes.add((GLRenderer)target);
				findChildren(nodes, (GLRenderer)target);
			} else {
				throw new RuntimeException("All image processors must extend GLRenderer.");
			}
		}
	}
	
	/**
	 * Pauses the rendering of the graph. This method should always be called before the alteration of the filter graph.
	 */
	public synchronized void pauseRendering() {
		rendering = false;
	}
	
	/**
	 * Starts the rendering of the graph.
	 */
	public synchronized void startRendering() {
		if(rootRenderer != null) {
			rendering = true;
		}
	}
	
	/**
	 * Signals the pipeline that at least one node in the graph has changed. 
	 * This method should only be called when the pipeline is in the paused state and
	 * should be called every time the graph changes.  Calling this method is not required
	 * when a new root node is set.
	 */
	public void filtersChanged() {
		treeChanged = true;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
	}

	/**
	 * Returns the width of GLSurfaceView on the screen.
	 * @return width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of GLSurfaceView on the screen.
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Should be called when ever onPause() is called in the activity using the FastImageProcessingView that this FastImageProcessingPipeline
	 * is associated with.
	 */
	public void onPause() {
		previousRenderingState = rendering;
		pauseRendering();
		for(GLRenderer renderer : processors) {
			renderer.onPause();
		}
	}
	
	/**
	 * Should be called when ever onResume() is called in the activity using the FastImageProcessingView that this FastImageProcessingPipeline
	 * is associated with.
	 */
	public void onResume() {
		for(GLRenderer renderer : processors) {
			renderer.onResume();
		}
		if(previousRenderingState) {
			startRendering();
		}
	}
}
