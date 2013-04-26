package project.android.twoinputfilterexample;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.MultiInputFilter;
import project.android.imageprocessing.filter.blend.*;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;
import project.android.imageprocessing.filter.processing.GaussianBlurFilter;
import project.android.imageprocessing.input.ImageResourceInput;
import project.android.imageprocessing.output.ScreenEndpoint;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessingActivity extends Activity {

	private FastImageProcessingView view;
	private List<MultiInputFilter> filters;
	private int curFilter;
	private ImageResourceInput image;
	private long touchTime;
	private FastImageProcessingPipeline pipeline;
	private ScreenEndpoint screen;
	private GaussianBlurFilter blur;
	private GreyScaleFilter grey;
	
	private void addFilter(MultiInputFilter filter) {
		filters.add(filter);	
		filter.addTarget(screen);
		filter.registerFilter(blur);
		filter.registerFilter(grey);
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
		image = new ImageResourceInput(view, this, R.drawable.tiger);
		blur = new GaussianBlurFilter(2f);
		grey = new GreyScaleFilter();
		filters = new ArrayList<MultiInputFilter>();
		
		screen = new ScreenEndpoint(pipeline);
		
		image.addTarget(blur);
		image.addTarget(grey);
		
		addFilter(new MaskFilter());
		addFilter(new LinearBurnBlendFilter());
		addFilter(new LuminosityBlendFilter());
		addFilter(new SaturationBlendFilter());
		addFilter(new HueBlendFilter());
		addFilter(new ColourBlendFilter());
		addFilter(new NormalBlendFilter());
		addFilter(new SourceOverBlendFilter());
		addFilter(new SoftLightBlendFilter());
		addFilter(new HardLightBlendFilter());
		addFilter(new DifferenceBlendFilter());
		addFilter(new ExclusionBlendFilter());
		addFilter(new ScreenBlendFilter());
		addFilter(new ColourDodgeBlendFilter());
		addFilter(new ColourBurnBlendFilter());
		addFilter(new LightenBlendFilter());
		addFilter(new DarkenBlendFilter());
		addFilter(new OverlayBlendFilter());
		addFilter(new DivideBlendFilter());
		addFilter(new SubtractBlendFilter());
		addFilter(new AddBlendFilter());
		addFilter(new MultiplyBlendFilter());
		addFilter(new DissolveBlendFilter(0.7f));
		addFilter(new ChromaKeyBlendFilter(new float[] {1.0f, 0.3f, 0.0f}, 0.4f, 0.1f));
		addFilter(new AlphaBlendFilter(0.9f));
		
		blur.addTarget(filters.get(0));
		grey.addTarget(filters.get(0));
		
		pipeline.setRootRenderer(image);
		pipeline.startRendering();
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				if(System.currentTimeMillis() - touchTime > 100) {
					pipeline.pauseRendering();
					touchTime = System.currentTimeMillis();
					blur.removeTarget(filters.get(curFilter));
					grey.removeTarget(filters.get(curFilter));
					curFilter=(curFilter+1)%filters.size();
					blur.addTarget(filters.get(curFilter));
					grey.addTarget(filters.get(curFilter));
					pipeline.startRendering();
					view.requestRender();
				}
				return false;
			}
		});
	}

}
