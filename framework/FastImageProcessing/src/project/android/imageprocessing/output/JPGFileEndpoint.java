package project.android.imageprocessing.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.provider.MediaStore;

/**
 * A JPG image renderer extension of GLRenderer. 
 * This class accepts a texture as input and renders it to a jpeg image file.
 * Images will be saved to file every time the texture is updated.  If increment is on, it will save the file with the 
 * given base name and an added number in the format "filePath%d.jpg".  The filepath is the path and name of the image 
 * file that should be written to (".jpg" will be appened automatically).  This class does not
 * handle displaying to the screen; however it does use the screen to render to the video recorder, so if display is not
 * required the opengl context should be hidden.  Storing to the camera roll is optional.  If it is false, the context is allowed
 * to be null.  Either way the image is written to filePath; however, it will only be visible in the gallery if storing to media is 
 * true. Storing to media may slow down the file writing.  Also, if it is true, every image will be stored to the camera roll whether or not increment
 * is on.
 * @author Chris Batt
 */
public class JPGFileEndpoint extends GLRenderer implements GLTextureInputRenderer{
	private String filePath;
	private boolean increment;
	private int curNumber;
	private Context context;
	private boolean storeToMedia;
	
	/**
	 * Creates a JPGFileEndpoint that writes to disk on the given file path and may or may not also write to 
	 * the camera roll.
	 * @param context
	 * An activity context. Can be null if storeToMedia is false.
	 * @param storeToMedia
	 * Whether or not it should also be written to the camera roll.
	 * @param filePath
	 * The file path and name of the file that the image should be written to.
	 * @param increment
	 * Whether or not a new image should be written for each input change.
	 */
	public JPGFileEndpoint(Context context, boolean storeToMedia, String filePath, boolean increment) {
		this.context = context;
		this.storeToMedia = storeToMedia;
		this.filePath = filePath;
		this.increment = increment;
		curNumber = 1;
		textureVertices = new FloatBuffer[4];
		
		float[] texData0 = new float[] {
	        0.0f, 1.0f,
	        1.0f, 1.0f,
	        0.0f, 0.0f,
	        1.0f, 0.0f,
		};
		textureVertices[0] = ByteBuffer.allocateDirect(texData0.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[0].put(texData0).position(0);
		
		float[] texData1 = new float[] {
	        1.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        0.0f, 0.0f,
		};
		textureVertices[1] = ByteBuffer.allocateDirect(texData1.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[1].put(texData1).position(0);
			
		float[] texData2 = new float[] {
	        1.0f, 0.0f,
	        0.0f, 0.0f,
	        1.0f, 1.0f,
	        0.0f, 1.0f,
		};
		textureVertices[2] = ByteBuffer.allocateDirect(texData2.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[2].put(texData2).position(0);
		
		float[] texData3 = new float[] {
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f,
		};
		textureVertices[3] = ByteBuffer.allocateDirect(texData3.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[3].put(texData3).position(0);
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		texture_in = texture;
		setWidth(source.getWidth());
		setHeight(source.getHeight());
		onDrawFrame();
		int[] pixels = new int[getWidth()*getHeight()];
		IntBuffer intBuffer = IntBuffer.wrap(pixels);
		intBuffer.position(0);
		GLES20.glReadPixels(0, 0, getWidth(), getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = (pixels[i] & (0xFF00FF00)) | ((pixels[i] >> 16) & 0x000000FF) | ((pixels[i] << 16) & 0x00FF0000); //swap red and blue to translate back to bitmap rgb style
		}
		Bitmap image = Bitmap.createBitmap(pixels, getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		String filePathName;
		if(increment) {
			filePathName = filePath+curNumber+".jpg";
			curNumber++;
		} else {
			filePathName = filePath+".jpg";
		}
		try {
			OutputStream out = new FileOutputStream(new File(filePathName));
			image.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			if(storeToMedia) {
				String[] name = filePath.split("/");
				MediaStore.Images.Media.insertImage(context.getContentResolver(), filePathName, name[name.length-1], "");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
