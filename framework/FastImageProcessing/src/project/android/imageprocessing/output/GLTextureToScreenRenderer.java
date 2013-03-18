package project.android.imageprocessing.output;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

/**
 * A screen renderer extension of GLRenderer. 
 * This class accepts a texture as input and renders it to the screen.
 * @author Chris Batt
 */
public class GLTextureToScreenRenderer extends GLRenderer implements GLTextureInputRenderer {
	private FastImageProcessingPipeline rendererContext;
	private boolean fullScreenTexture;
	
	/**
	 * Creates a GLTextureToScreenRenderer. 
	 * If it is not set to full screen mode, the reference to the render context is allowed to be null.
	 * @param rendererContext
	 * A reference to the GLSurfaceView.Renderer that contains the OpenGL context.
	 * @param fullScreenTexture
	 * Whether or not to use the input filter size as the render size or to render full screen.
	 */
	public GLTextureToScreenRenderer(FastImageProcessingPipeline rendererContext, boolean fullScreenTexture) {
		super();
		this.rendererContext = rendererContext;
		this.fullScreenTexture = fullScreenTexture;
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.GLRenderer#onDrawFrame()
	 */
	public void onDrawFrame() {
		if(width == 0 || height == 0) {
			width = rendererContext.getWidth();
			height = rendererContext.getHeight();
		}
		super.onDrawFrame();
	}

	/* (non-Javadoc)
	 * @see project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		texture_in = texture;
		if(!fullScreenTexture) {
			width = source.getWidth();
			height = source.getHeight();
		}
		onDrawFrame();
	}
}
