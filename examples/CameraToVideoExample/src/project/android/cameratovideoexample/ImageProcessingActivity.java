package project.android.cameratovideoexample;

import java.io.File;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;
import project.android.imageprocessing.filter.processing.ConvolutionFilter;
import project.android.imageprocessing.input.CameraPreviewInput;
import project.android.imageprocessing.output.Mp4VideoFileEndpoint;
import project.android.imageprocessing.output.ScreenEndpoint;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessingActivity extends Activity {

	private FastImageProcessingView view;
	private BasicFilter sharpen;
	private long touchTime;
	private FastImageProcessingPipeline pipeline;
	private CameraPreviewInput camera;
	private Mp4VideoFileEndpoint video;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		camera = new CameraPreviewInput(view);
		sharpen = new ConvolutionFilter(new float[] {
				0, -1, 0,
				-1, 5, -1,
				0, -1, 0
			}, 3, 3);
		video = new Mp4VideoFileEndpoint(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies/cameraOuput", 15);
		camera.addTarget(sharpen);
		sharpen.addTarget(video);
		pipeline.setRootRenderer(camera);
		view.setPipeline(pipeline);
		setContentView(view);
		pipeline.startRendering();
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				if(System.currentTimeMillis() - touchTime > 100) {
					touchTime = System.currentTimeMillis();
					if(video.isRecording()) {
						video.finishRecording();
					} else {
						video.startRecording();
					}
				}
				return false;
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

}
