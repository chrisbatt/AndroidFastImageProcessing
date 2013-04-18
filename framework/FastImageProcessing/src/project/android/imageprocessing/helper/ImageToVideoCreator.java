package project.android.imageprocessing.helper;

//import java.nio.Buffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

public class ImageToVideoCreator {
	private FrameRecorder recorder;
	private int width;
	private int height;
	
	public ImageToVideoCreator(String outputName, int width, int height, int fps) {
		recorder = new FFmpegFrameRecorder(outputName+".mp4", width, height);
		recorder.setFormat("mp4");
		recorder.setFrameRate(fps);
		
		this.width = width;
		this.height = height;
	}
	
	public void startRecording() throws Exception {
		recorder.start();
	}
	
	public void frameAvailable() throws Exception {
		if(recorder == null) {
			return;
		}
		int[] pixels = new int[width*height];
		IntBuffer intBuffer = IntBuffer.wrap(pixels);
		intBuffer.position(0);
		GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
		//for(int i = 0; i < pixels.length; i++) {
		//	pixels[i] = (pixels[i] & (0xFF00FF00)) | ((pixels[i] >> 16) & 0x000000FF) | ((pixels[i] << 16) & 0x00FF0000); //swap red and blue to translate back to bitmap rgb style
		//}
		IplImage frame = IplImage.create(width, height, IPL_DEPTH_8U, 4);
		frame.getIntBuffer().put(pixels);
		recorder.record(frame);
	}
	
	public void stopRecording() throws Exception {
		recorder.stop();
		recorder.release();
		recorder = null;
	} 
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
