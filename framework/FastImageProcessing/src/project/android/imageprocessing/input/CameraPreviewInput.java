package project.android.imageprocessing.input;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * A Camera input extension of CameraPreviewInput.  
 * This class takes advantage of the android camera preview to produce new textures for processing. <p>
 * Note: This class requires an API level of at least 14.
 * @author Chris Batt
 */
@TargetApi(value = 14)
public class CameraPreviewInput extends GLTextureOutputRenderer implements OnFrameAvailableListener {
	private static final String UNIFORM_CAM_MATRIX = "u_Matrix";
	
	private Camera camera;
	private SurfaceTexture camTex;
	
	private int matrixHandle;
	private float[] matrix = new float[16];
	
	private GLSurfaceView view;
	
	/**
	 * Creates a CameraPreviewInput which captures the camera preview with all the default camera parameters and settings.
	 */
	public CameraPreviewInput(GLSurfaceView view) {
		super();
		this.camera = createCamera();
		this.view = view;
		setRenderSizeToCameraSize();
	}
	
	private void setRenderSizeToCameraSize() {
		Parameters params = camera.getParameters();
		Size previewSize = params.getPreviewSize();
		setRenderSize(previewSize.width, previewSize.height);
	}
	

	@Override
	protected void initWithGLContext() {
		super.initWithGLContext();
		
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);        
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		texture_in = textures[0];
		
		camTex = new SurfaceTexture(texture_in);
		camTex.setOnFrameAvailableListener(this);

		boolean failed = true;
		while(failed) {			
	        try {
				camera.setPreviewTexture(camTex);
		        camera.startPreview();
		        failed = false;
			} catch (IOException e) {
				Log.e("CameraInput","Failed to set preview texture");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.e("CameraInput", sw.toString());
				camera.release();
				camera = createCamera();
			}
		}
	}
	
	protected Camera createCamera() {
		return Camera.open();
	}
	
	@Override
	protected void drawFrame() {
		camTex.updateTexImage(); 
		super.drawFrame();
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
        matrixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_CAM_MATRIX);    
	}

	@Override
	protected String getVertexShader() {
		return 
					"uniform mat4 "+UNIFORM_CAM_MATRIX+";\n"
				  + "attribute vec4 "+ATTRIBUTE_POSITION+";\n"
				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"	
				  + "varying vec2 "+VARYING_TEXCOORD+";\n"	
				  
				  + "void main() {\n"	
				  + "   vec4 texPos = "+UNIFORM_CAM_MATRIX+" * vec4("+ATTRIBUTE_TEXCOORD+", 1, 1);\n"
				  + "   "+VARYING_TEXCOORD+" = texPos.xy;\n"
				  + "   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 
				  + "}\n";		
	}

	@Override
	protected String getFragmentShader() {
		return "#extension GL_OES_EGL_image_external : require\n"+
		        super.getFragmentShader();
	}
	

	/* (non-Javadoc)
	 * @see android.graphics.SurfaceTexture.OnFrameAvailableListener#onFrameAvailable(android.graphics.SurfaceTexture)
	 */
	@Override
	public void onFrameAvailable(SurfaceTexture arg0) {
		view.requestRender();
	}

}
