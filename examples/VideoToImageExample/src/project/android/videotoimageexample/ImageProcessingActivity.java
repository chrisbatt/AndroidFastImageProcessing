package project.android.videotoimageexample;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.processing.SobelEdgeDetectionFilter;
import project.android.imageprocessing.input.VideoResourceInput;
import project.android.imageprocessing.output.JPGFileEndpoint;
import project.android.imageprocessing.output.ScreenEndpoint;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessingActivity extends Activity {
	private FastImageProcessingView view;
	private BasicFilter edgeDetect;
	private FastImageProcessingPipeline pipeline;
	private VideoResourceInput video;
	private JPGFileEndpoint image;
	private ScreenEndpoint screen;
	private long touchTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		video = new VideoResourceInput(view, this, R.raw.birds);
		edgeDetect = new SobelEdgeDetectionFilter();
		image = new JPGFileEndpoint(this, false, Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/outputImage", false);
		screen = new ScreenEndpoint(pipeline);
		video.addTarget(edgeDetect);
		edgeDetect.addTarget(image);
		edgeDetect.addTarget(screen);
		pipeline.addRootRenderer(video);
		view.setPipeline(pipeline);
		setContentView(view);
		pipeline.startRendering();
		video.startWhenReady();
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent me) {
				if(System.currentTimeMillis() - 100 > touchTime) {
					touchTime = System.currentTimeMillis();
					if(video.isPlaying()) {
						video.stop();
					} else {
						video.startWhenReady();
					}
				}
				return true;
			}
			
		});
	}

}
