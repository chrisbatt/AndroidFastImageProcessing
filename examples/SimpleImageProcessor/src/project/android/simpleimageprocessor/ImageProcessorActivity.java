package project.android.simpleimageprocessor;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.ConvolutionFilter;
import project.android.imageprocessing.filter.GreyScaleFilter;
import project.android.imageprocessing.filter.RGBFilter;
import project.android.imageprocessing.input.GLImageToTextureRenderer;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.GLTextureToScreenRenderer;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessorActivity extends Activity {
	private FastImageProcessingView view;
	private BasicFilter[] filters;
	private int curFilter;
	private GLTextureOutputRenderer image;
	private long touchTime;
	private FastImageProcessingPipeline pipeline;
	private GLTextureToScreenRenderer screen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		view.setPipeline(pipeline);
		setContentView(view);
		image = new GLImageToTextureRenderer(this, R.drawable.tiger);
		filters = new BasicFilter[5];
		filters[0] = new RGBFilter(0.33f,0.67f,1.34f);
		filters[1] = new GreyScaleFilter();
		filters[2] = new ConvolutionFilter(new float[] {0,-1,0,-1,5,-1,0,-1,0}, 3, 3);
		filters[3] = new GreyScaleFilter();
		filters[4] = new ConvolutionFilter(new float[] {
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f,
														1/25f,1/25f,1/25f,1/25f,1/25f}, 5, 5);
		BasicFilter edge = new ConvolutionFilter(new float[] {0,-1,0,-1,4,-1,0,-1,0}, 3, 3);
		screen = new GLTextureToScreenRenderer(pipeline, true);
		image.addTarget(filters[0]);
		filters[0].addTarget(screen);
		filters[1].addTarget(screen);
		filters[2].addTarget(screen);
		filters[3].addTarget(edge);
		edge.addTarget(screen);
		filters[4].addTarget(screen);
		pipeline.setRootRenderer(image);
		pipeline.startRendering();
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				if(System.currentTimeMillis() - touchTime > 100) {
					touchTime = System.currentTimeMillis();
					pipeline.pauseRendering();
					if(curFilter == 5) {
						image.removeTarget(screen);
					} else {
						image.removeTarget(filters[curFilter]);
					}
					curFilter=(curFilter+1)%6;
					if(curFilter == 5) {
						image.addTarget(screen);
					} else {
						image.addTarget(filters[curFilter]);
					}
					pipeline.filtersChanged();
					pipeline.startRendering();
					
				}
				return false;
			}
		});
	}
	
	
}
