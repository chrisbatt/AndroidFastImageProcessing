package project.android.allfiltersexample;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.colour.ColourMatrixFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;
import project.android.imageprocessing.filter.colour.HueFilter;
import project.android.imageprocessing.filter.colour.ImageBrightnessFilter;
import project.android.imageprocessing.filter.colour.ImageContrastFilter;
import project.android.imageprocessing.filter.colour.ImageExposureFilter;
import project.android.imageprocessing.filter.colour.ImageGammaFilter;
import project.android.imageprocessing.filter.colour.ImageLevelsFilter;
import project.android.imageprocessing.filter.colour.ImageSaturationFilter;
import project.android.imageprocessing.filter.colour.RGBFilter;
import project.android.imageprocessing.filter.processing.ConvolutionFilter;
import project.android.imageprocessing.input.ImageResourceInput;
import project.android.imageprocessing.output.Mp4VideoFileEndpoint;
import project.android.imageprocessing.output.ScreenEndpoint;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessingActivity extends Activity {

	private FastImageProcessingView view;
	private BasicFilter[] filters;
	private int curFilter;
	private ImageResourceInput image;
	private long touchTime;
	private FastImageProcessingPipeline pipeline;
	private ScreenEndpoint screen;
	private int numOfFilters = 11;
	private int i;
	
	private void addFilter(BasicFilter filter) {
		filters[i] = filter;
		i++;		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		view.setPipeline(pipeline);
		setContentView(view);
		image = new ImageResourceInput(this, R.drawable.tiger);
		filters = new BasicFilter[numOfFilters];
		addFilter(new HueFilter(3.14f/6f));
		addFilter(new ImageBrightnessFilter(0.5f));
		addFilter(new ColourMatrixFilter(new float[]{	0.33f,0f,0f,0f,
														0f,0.67f,0f,0f,
														0f,0f,1.34f,0f,
														0.2f,0.2f,0.2f,1.0f}, 0.5f));
		addFilter(new RGBFilter(0.33f,0.67f,1.34f));
		addFilter(new GreyScaleFilter());
		addFilter(new ConvolutionFilter(new float[] {
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f}, 5, 5));
		addFilter(new ImageExposureFilter(0.95f));
		addFilter(new ImageContrastFilter(1.5f));
		addFilter(new ImageSaturationFilter(0.5f));
		addFilter(new ImageGammaFilter(1.75f));
		addFilter(new ImageLevelsFilter(0.2f,0.8f,1f,0f,1f));
		
		screen = new ScreenEndpoint(pipeline, true);
		
		image.addTarget(screen);
		for(int i = 0; i < numOfFilters; i++) {
			filters[i].addTarget(screen);
		}
		
		pipeline.setRootRenderer(image);
		pipeline.startRendering();
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				if(System.currentTimeMillis() - touchTime > 100) {
					Log.e("TOUCH", "touch");
					pipeline.pauseRendering();
					touchTime = System.currentTimeMillis();
					if(curFilter == 0) {
						image.removeTarget(screen);
					} else {
						image.removeTarget(filters[curFilter-1]);
					}
					curFilter=(curFilter+1)%(numOfFilters+1);
					if(curFilter == 0) {
						image.addTarget(screen);
					} else {
						image.addTarget(filters[curFilter-1]);
					}
					pipeline.startRendering();
					Log.e("TOUCH", "new filter set");
				}
				return false;
			}
		});
	}

}
