package project.android.imageprocessing.input;

import project.android.imageprocessing.filter.BasicFilter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A image input renderer extension of the BasicFilter. 
 * This class takes an image as input and processes it so that it can be sent to other filters.
 * The image can be changed at any time without creating a new GLImageToTextureRenderer by using the setImage(int resourceId) method.
 * @author Chris Batt
 */
public class ImageResourceInput extends BasicFilter {
	private Context context;
	private int tex;
	private int fps;
	private long lastTime;
	private Bitmap bitmap;
	private int imageWidth;
	private int imageHeight;
	private boolean imageChanged;
	
	/**
	 * Creates a GLImageToTextureRenderer using the given resourceId as the image input. 
	 * All future images must also come from the same context.
	 * @param context
	 * The context in which the resourceId exists.
	 * @param resourceId
	 * The resource id of the image which should be processed.
	 */
	public ImageResourceInput(Context context, int resourceId) {
		super();
		this.context = context;
		loadImage(resourceId);
		imageChanged = true;
		curRotation = 2;
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given file path to the image input. 
	 * @param pathName
	 * The file path to the image to load.
	 */
	public ImageResourceInput(String pathName) {
		super();
		loadImage(pathName);
		imageChanged = true;
		curRotation = 2;
	}
	
	/**
	 * Creates a GLImageToTextureRenderer using the given bitmap as the image input. 
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public ImageResourceInput(Bitmap bitmap) {
		super();
		loadImage(bitmap);
        imageChanged = true;
		imageChanged = true;
		curRotation = 2;
	}
	
	private void loadImage(int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        imageChanged = true;
	}
	
	private void loadImage(String pathName) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        bitmap = BitmapFactory.decodeFile(pathName, options);
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        imageChanged = true;
	}
	
	private void loadImage(Bitmap bitmap) {
		this.bitmap = bitmap;
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        imageChanged = true;
	}
	
	/**
	 * Sets the image being output by this renderer to the image loaded from the given id.
	 * @param resourceId
	 * The resource id of the new image to be output by this renderer.
	 */
	public void setImage(int resourceId) {
		loadImage(resourceId);
	}
	
	/**
	 * Sets the image being output by this renderer to the image loaded from the given file path.
	 * @param pathName
	 * The file path to the image to load.
	 */
	public void setImage(String filePath) {
		loadImage(filePath);
	}
	
	/**
	 * Sets the image being output by this renderer to the given bitmap.
	 * @param bitmap
	 * The bitmap which contains the image.
	 */
	public void setImage(Bitmap bitmap) {
		loadImage(bitmap);
	}
	
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#onSurfaceCreated()
	 */
	@Override
	public void onSurfaceCreated() {
		setRenderSize(imageWidth, imageHeight);
		super.onSurfaceCreated();
		tex = loadTexture();
		imageChanged = false;
	}
	
	private int loadTexture()
	{
	    final int[] textureHandle = new int[1];
	 
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0) {
	 
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    return textureHandle[0];
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
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#onDrawFrame()
	 */
	@Override
	public void onDrawFrame() {
		if(!initialized) {
			onSurfaceCreated();
			initialized = true;
		}
		if(imageChanged) {
			tex = loadTexture();
			imageChanged = false;
		}
		fps++;
		if(System.currentTimeMillis() - lastTime > 1000) {
			Log.e("OPENGL_FPS", "gl: "+fps);
			fps = 0;
			lastTime = System.currentTimeMillis();
		}
		newTextureReady(tex, this);
	}
		
}
