package project.android.videotoimageexample;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.processing.ConvolutionFilter;
import project.android.imageprocessing.input.VideoResourceInput;
import project.android.imageprocessing.output.JPGFileEndpoint;
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
	private BasicFilter emboss;
	private FastImageProcessingPipeline pipeline;
	private VideoResourceInput video;
	private JPGFileEndpoint image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		video = new VideoResourceInput(view, this, R.raw.inputvideo);
		emboss = new ConvolutionFilter(new float[] {
			-2, -1, 0,
			-1, 1, 1,
			0, 1, 2
		}, 3, 3);
		image = new JPGFileEndpoint(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/outputImage", false);
		video.addTarget(emboss);
		emboss.addTarget(image);
		pipeline.setRootRenderer(video);
		view.setPipeline(pipeline);
		setContentView(view);
		pipeline.startRendering();
		video.startWhenReady();
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent me) {
				video.stop();
				return true;
			}
			
		});
	}

}
