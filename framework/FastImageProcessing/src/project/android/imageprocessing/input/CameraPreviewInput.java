package project.android.imageprocessing.input;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

/**
 * A Camera input extension of CameraPreviewInput.  
 * This class takes advantage of the android camera preview to produce new textures for processing. <p>
 * Note: This class requires an API level of at least 14.
 * @author Chris Batt
 */
@TargetApi(value = 14)
public class CameraPreviewInput extends GLTextureOutputRenderer implements OnFrameAvailableListener {
	private Camera camera;
	private SurfaceTexture camTex;
	
	private int matrixHandle;
	private float[] matrix = new float[16];
	
	private long lastTime;
	private int fps;
	private int glFps;
	
	private boolean cameraHandled;
	private boolean changed;
	
	/**
	 * Creates a CameraPreviewInput which captures the camera preview with all the default camera parameters and settings.
	 */
	public CameraPreviewInput() {
		super();
		camera = setupNewCamera();
		setRenderSizeToCameraSize();
		//rotateClockwise90Degrees(1);
	}
	
	public void setRenderSizeToCameraSize() {
		Parameters params = camera.getParameters();
		Size previewSize = params.getPreviewSize();
		setRenderSize(previewSize.width, previewSize.height);
		Log.e("CAMERA_SIZE", String.format("%d %d", previewSize.width, previewSize.height));
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#onSurfaceCreated()
	 */
	@Override
	public void onSurfaceCreated() {
		super.onSurfaceCreated();
		
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);        
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		texture_in = textures[0];	
		
		checkCameraHandled();
	}
	
	protected Camera setupNewCamera() {
		Camera camera = Camera.open();
		cameraHandled = false;
		return camera;
	}
	
	private void checkCameraHandled() {
		if(!cameraHandled) {
			camTex = new SurfaceTexture(texture_in);
			camTex.setOnFrameAvailableListener(this);	    	
	        try {
	            camera.setPreviewTexture(camTex);
	            camera.startPreview();
	            cameraHandled = true;
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		}
	}
	
	private void checkFPS() {
		glFps++;
		if(changed) {
			changed = false;
			fps++;
			if(System.currentTimeMillis() - lastTime > 1000) {
				Log.e("OPENGL_FPS", "camera: "+fps);
				Log.e("OPENGL_FPS", "gl: "+glFps);
				fps = 0;
				glFps = 0;
				lastTime = System.currentTimeMillis();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#onDrawFrame()
	 */
	@Override
	public void onDrawFrame() {
		if(!initialized) {
			onSurfaceCreated();
			onPause();
			onResume();
			initialized = true;
		}
		checkCameraHandled();
		checkFPS();
		camTex.updateTexImage(); 
        
		super.onDrawFrame();
	}
	
	private void bindTexture() {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture_in);
	}

	@Override
	protected void passShaderValues() {
		squareVertices[curRotation].position(0);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, squareVertices[curRotation]);  
		GLES20.glEnableVertexAttribArray(positionHandle); 
		squareVertices[curRotation].position(3);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 20, squareVertices[curRotation]);  
		GLES20.glEnableVertexAttribArray(texCoordHandle); 
		
		bindTexture();
	    GLES20.glUniform1i(textureHandle, 0);
	    
	    camTex.getTransformMatrix(matrix);
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);
	}


	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
        matrixHandle = GLES20.glGetUniformLocation(programHandle, "u_Matrix");    
	}

	@Override
	protected String getVertexShader() {
		return "uniform mat4 u_Matrix;      \n"		// A constant representing the combined model/view/projection matrix.
				
				  + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.	
				  + "attribute vec2 a_TexCoord;     \n"		// Per-vertex position information we will pass in. 
				  + "varying vec2 v_TexCoord;     \n"		// Per-vertex position information we will pass in.	

				  + "void main()                    \n"		// The entry point for our vertex shader.
				  + "{                              \n"
				  +" vec4 texPos = u_Matrix * vec4(a_TexCoord, 1, 1);"
				  + "v_TexCoord = texPos.xy;       \n"
				  + "   gl_Position = a_Position;   \n" 	// gl_Position is a special variable used to store the final position.
				  + "                \n"     // Multiply the vertex by the matrix to get the final point in 			                                            			 
				  + "}                              \n";    // normalized screen coordinates.
	}

	@Override
	protected String getFragmentShader() {
		return "#extension GL_OES_EGL_image_external : require\n"+
		        "precision mediump float;" +                          
		        "uniform samplerExternalOES u_Texture;"  
				  + "varying vec2 v_TexCoord;     \n"		// Per-vertex position information we will pass in.	
				
		  + "void main()                    \n"		// The entry point for our fragment shader.
		  + "{                              \n"
		  + "   gl_FragColor = texture2D(u_Texture, v_TexCoord);     \n"		// Pass the color directly through the pipeline.		  
		  + "}                              \n";
	}

	/* (non-Javadoc)
	 * @see project.android.imageprocessing.GLRenderer#onPause()
	 */
	@Override
	public void onPause() {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	/* (non-Javadoc)
	 * @see project.android.imageprocessing.GLRenderer#onResume()
	 */
	@Override
	public void onResume() {
		if(camera == null) {
			camera = setupNewCamera();
		}
	}

	/* (non-Javadoc)
	 * @see android.graphics.SurfaceTexture.OnFrameAvailableListener#onFrameAvailable(android.graphics.SurfaceTexture)
	 */
	@Override
	public void onFrameAvailable(SurfaceTexture arg0) {
		changed = true;
	}

}
