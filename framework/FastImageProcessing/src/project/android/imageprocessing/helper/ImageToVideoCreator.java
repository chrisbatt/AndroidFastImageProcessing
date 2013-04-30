package project.android.imageprocessing.helper;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import java.nio.IntBuffer;

import android.opengl.GLES20;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A helper class for video creation.
 * @author Chris Batt
 */
public class ImageToVideoCreator {
	private FrameRecorder recorder;
	private int width;
	private int height;
	
	/**
	 * Creates a ImageToVideoCreator that outputs to the given outputName with a fixed width, height and fps.
	 * @param outputName
	 * The output file name and path to render the output to.
	 * @param width
	 * The width of the video being produced.
	 * @param height
	 * The height of the video being produced.
	 * @param fps
	 * The frames per second of the video being produced.
	 */
	public ImageToVideoCreator(String outputName, int width, int height, int fps) {
		recorder = new FFmpegFrameRecorder(outputName+".mp4", width, height);
		recorder.setFormat("mp4");
		recorder.setFrameRate(fps);
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Starts the recording of the video
	 * @throws Exception 
	 * Exceptions may be thrown from trying to start the recorder.
	 */
	public void startRecording() throws Exception {
		recorder.start();
	}
	
	/**
	 * Should be called when a new frame has been draw to the opengl context.  Will read the current
	 * opengl frame buffer and record it as a frame.
	 * @throws Exception
	 * Exceptions may be thrown from trying to record a frame
	 */
	public void frameAvailable() throws Exception {
		if(recorder == null) {
			return;
		}
		int[] pixels = new int[width*height];
		IntBuffer intBuffer = IntBuffer.wrap(pixels);
		intBuffer.position(0);
		GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
		IplImage frame = IplImage.create(width, height, IPL_DEPTH_8U, 4);
		frame.getIntBuffer().put(pixels);
		recorder.record(frame);
	}
	
	/**
	 * Stops recording the video and finalizes it.
	 * @throws Exception
	 * Exceptions may be thrown from trying to stop the recording
	 */
	public void stopRecording() throws Exception {
		recorder.stop();
		recorder.release();
		recorder = null;
	} 
	
	/**
	 * Returns the width of the video being recorded.
	 * @return width
	 * The width of the video being recorded
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of the video being recorded.
	 * @return height
	 * The height of the video being recorded
	 */
	public int getHeight() {
		return height;
	}
}
