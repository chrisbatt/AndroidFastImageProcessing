package project.android.imageprocessing.output;

import java.io.PrintWriter;
import java.io.StringWriter;

import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.helper.ImageToVideoCreator;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.util.Log;

import com.googlecode.javacv.FrameRecorder.Exception;

/**
 * A mp4 video renderer extension of GLRenderer. 
 * This class accepts a texture as input and renders it to a mp4 video file at the given frame rate.
 * This class currently fails on most android 4+ (API 14+) devices but does work on most android 2.2 (API 8) devices.
 * Hopefully, this will be fixed in future updates.  Video recording will start when startRecording is called.
 * It will finish recording and close the video recorder when finishRecording is called. This class does not
 * handle displaying to the screen; however it does use the screen to render to the video recorder, so if display is not
 * required the opengl context should be hidden.
 * @author Chris Batt
 */
public class Mp4VideoFileEndpoint extends GLRenderer implements GLTextureInputRenderer {
	private String filename;
	private int fps;
	private ImageToVideoCreator videoRecorder;
	
	/**
	 * Creates a new Mp4VideoFileEndpoint
	 * @param filename
	 * The file name and path that the video should be written to. ".mp4" will appended to the filename
	 * @param fps 
	 * The frames per second that the video should be encoded at
	 */
	public Mp4VideoFileEndpoint(String filename, int fps) {
		this.filename = filename;
		this.fps = fps;
		rotateClockwise90Degrees(2);
	}
	
	/**
	 * Returns whether or not the recorder is running
	 */
	public boolean isRecording() {
		return videoRecorder != null;
	}
	
	/**
	 * Starts a new video recording
	 */
	public void startRecording() {
		videoRecorder = new ImageToVideoCreator(filename, getWidth(), getHeight(), fps);
		try {
			videoRecorder.startRecording();
		} catch (Exception e) {
			Log.e("VideoRecorderEndpoint", "Failed to start video recorder");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("VideoRecorderEndpoint", sw.toString());
			videoRecorder = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.output.GLTextureInputRenderer#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		texture_in = texture;
		if(videoRecorder == null) {
			setWidth(source.getWidth());
			setHeight(source.getHeight());
			return;
		} 
		onDrawFrame();
		try {
			videoRecorder.frameAvailable();
		} catch (Exception e) {
			Log.e("VideoRecorderEndpoint", "Failed to record frame");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("VideoRecorderEndpoint", sw.toString());
		}
	}
	
	/**
	 * Stops and finalizes the video recording. Unknown results if called when video recorder is not recording.
	 */
	public void finishRecording() {
		try {
			videoRecorder.stopRecording();
			videoRecorder = null;
		} catch (Exception e) {
			Log.e("VideoRecorderEndpoint", "Failed to finalize recording");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.e("VideoRecorderEndpoint", sw.toString());
		}
	}
}
