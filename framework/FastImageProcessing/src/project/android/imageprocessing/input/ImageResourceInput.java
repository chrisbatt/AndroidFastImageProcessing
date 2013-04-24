package project.android.imageprocessing.input;

import project.android.imageprocessing.helper.ImageHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

/**
 * A image input renderer extension of the BasicFilter. 
 * This class takes an image as input and processes it so that it can be sent to other filters.
 * The image can be changed at any time without creating a new GLImageToTextureRenderer by using the setImage(int resourceId) method.
 * @author Chris Batt
 */
/**
 * @author Chris
 *
 */
public class ImageResourceInput extends GLTextureOutputRenderer {
	private Context context;
	private GLSurfaceView view;
	private Bitmap bitmap;
	private int imageWidth;
	private int imageHeight;
	
	/**
	 * Creates a GLImageToTextureRenderer using the given resourceId as the image input. 
	 * All future images must also come from the same context.
	 * @param context
	 * The context in which the resourceId exists.
	 * @param resourceId
	 * The resource id of the image which should be processed.
	 */
	public ImageResourceInput(GLSurfaceView view, Context context, int resourceId) {
		this.context = context;
		this.view = view;
		setImage(resourceId);
		curRotation = 2;
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given file path to the image input. 
	 * @param pathName
	 * The file path to the image to load.
	 */
	public ImageResourceInput(GLSurfaceView view, String pathName) {
		this.view = view;
		setImage(pathName);
		curRotation = 2;
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given bitmap as the image input. 
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public ImageResourceInput(GLSurfaceView view, Bitmap bitmap) {
		this.view = view;
		setImage(bitmap);
		curRotation = 2;
	}
	
	/**
	 * Sets the image being output by this renderer to the image loaded from the given id.
	 * @param resourceId
	 * The resource id of the new image to be output by this renderer.
	 */
	public void setImage(int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
		loadImage(BitmapFactory.decodeResource(context.getResources(), resourceId, options));
	}
	
	/**
	 * Sets the image being output by this renderer to the image loaded from the given file path.
	 * @param filePath
	 * The file path to the image to load.
	 */
	public void setImage(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		loadImage(BitmapFactory.decodeFile(filePath, options));
	}
	
	/**
	 * Sets the image being output by this renderer to the given bitmap.
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public void setImage(Bitmap bitmap) {
		loadImage(bitmap);
	}
	
	private void loadImage(Bitmap bitmap) {
		this.bitmap = bitmap;
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
		setRenderSize(imageWidth, imageHeight);
		view.requestRender();
	}
	
	@Override
	protected void initWithGLContext() {
		int[] textureHandle = new int[1];
	    GLES20.glGenTextures(1, textureHandle, 0);
	    texture_in = textureHandle[0];
	    super.initWithGLContext();
	}
	
	private void loadTexture() 	{	 
		texture_in = ImageHelper.bitmapToTexture(bitmap);
	}
	
	/**
	 * Returns the width of the current image being output.
	 * @return image width
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * Returns the height of the current image being output.
	 * @return image height
	 */
	public int getImageHeight() {
		return imageHeight;
	}
	
	@Override
	protected void drawFrame() {
		loadTexture();
		super.drawFrame();
	}
}
