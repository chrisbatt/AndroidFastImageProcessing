package project.android.imageprocessing.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.IntBuffer;

import com.googlecode.javacv.FrameRecorder.Exception;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import project.android.imageprocessing.GLRenderer;
import project.android.imageprocessing.helper.ImageToVideoCreator;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

public class Mp4VideoFileEndpoint extends GLRenderer implements GLTextureInputRenderer {
	private String filename;
	private int fps;
	private ImageToVideoCreator videoRecorder;
	
	public Mp4VideoFileEndpoint(String filename, int fps) {
		this.filename = filename;
		this.fps = fps;
		rotateClockwise90Degrees(2);
	}
	
	public boolean isRecording() {
		return videoRecorder != null;
	}
	
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
	
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		texture_in = texture;
		if(videoRecorder == null) {
			width = source.getWidth();
			height = source.getHeight();
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
