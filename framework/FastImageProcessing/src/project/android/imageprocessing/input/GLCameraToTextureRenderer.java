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
 * A Camera input extension of GLTextureOutputRenderer.  
 * This class takes advantage of the android camera preview to produce new textures for processing. <p>
 * Note: This class requires an API level of at least 14.
 * @author Chris Batt
 */
@TargetApi(value = 14)
public class GLCameraToTextureRenderer extends GLTextureOutputRenderer implements OnFrameAvailableListener {
	protected static final String UNIFORM_MATRIX = "u_Matrix";
	
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
	 * Creates a GLCameraToTextureRenderer which captures the camera preview with all the default camera parameters and settings.
	 */
	public GLCameraToTextureRenderer() {
		super();
		camera = setupNewCamera();
		curRotation = 2;
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
	            Parameters params = camera.getParameters();
	    		Size previewSize = params.getPreviewSize();
	    		setRenderSize(previewSize.width, previewSize.height);
	    		Log.e("CAMERA_SIZE", String.format("%d %d", previewSize.width, previewSize.height));
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
		checkCameraHandled();
		checkFPS();
		camTex.updateTexImage(); 
        
		super.onDrawFrame();
	}

	@Override
	protected void passShaderValues() {
		super.passShaderValues();
	    camTex.getTransformMatrix(matrix);
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);
	}

	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
        matrixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_MATRIX);    
	}

	@Override
	protected String getVertexShader() {
		return 	
				"attribute vec4 "+ATTRIBUTE_POSITION+";\n"
			  +"attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"	
			  +"varying vec2 "+VARYING_TEXCOORD+";\n"
			  +"uniform mat4 "+UNIFORM_MATRIX+";\n"
			  
			  +"void main() {\n"	
			  +"  vec4 texPos = "+UNIFORM_MATRIX+" * vec4("+ATTRIBUTE_TEXCOORD+", 1, 1);\n"
			  +"  "+VARYING_TEXCOORD+" = texPos.xy;\n"
			  +"   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 
			  +"}\n"; 
	}

	@Override
	protected String getFragmentShader() {
		return "#extension GL_OES_EGL_image_external : require\n"
				+super.getFragmentShader();
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
