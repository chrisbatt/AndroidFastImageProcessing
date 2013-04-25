package project.android.allfiltersexample;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.colour.ColourInvertFilter;
import project.android.imageprocessing.filter.colour.ColourMatrixFilter;
import project.android.imageprocessing.filter.colour.GreyScaleFilter;
import project.android.imageprocessing.filter.colour.HueFilter;
import project.android.imageprocessing.filter.colour.BrightnessFilter;
import project.android.imageprocessing.filter.colour.MissEtikateFilter;
import project.android.imageprocessing.filter.colour.MonochromeFilter;
import project.android.imageprocessing.filter.colour.SoftEleganceFilter;
import project.android.imageprocessing.filter.colour.ToneCurveFilter;
import project.android.imageprocessing.filter.colour.HighlightShadowFilter;
import project.android.imageprocessing.filter.colour.ContrastFilter;
import project.android.imageprocessing.filter.colour.LookupFilter;
import project.android.imageprocessing.filter.colour.AmatorkaFilter;
import project.android.imageprocessing.filter.colour.ExposureFilter;
import project.android.imageprocessing.filter.colour.GammaFilter;
import project.android.imageprocessing.filter.colour.LevelsFilter;
import project.android.imageprocessing.filter.colour.SaturationFilter;
import project.android.imageprocessing.filter.colour.FalseColourFilter;
import project.android.imageprocessing.filter.colour.RGBFilter;
import project.android.imageprocessing.filter.colour.HazeFilter;
import project.android.imageprocessing.filter.colour.SepiaFilter;
import project.android.imageprocessing.filter.colour.OpacityFilter;
import project.android.imageprocessing.filter.colour.LuminanceThresholdFilter;
import project.android.imageprocessing.filter.colour.AdaptiveThresholdFilter;
import project.android.imageprocessing.filter.colour.ChromaKeyFilter;
import project.android.imageprocessing.filter.processing.ConvolutionFilter;
import project.android.imageprocessing.filter.processing.GaussianBlurFilter;
import project.android.imageprocessing.filter.processing.BoxBlurFilter;
import project.android.imageprocessing.filter.processing.GaussianBlurPositionFilter;
import project.android.imageprocessing.filter.processing.GaussianSelectiveBlurFilter;
import project.android.imageprocessing.filter.processing.SingleComponentFastBlurFilter;
import project.android.imageprocessing.filter.processing.TransformFilter;
import project.android.imageprocessing.filter.processing.MedianFilter;
import project.android.imageprocessing.filter.processing.CropFilter;
import project.android.imageprocessing.filter.processing.LanczosResamplingFilter;
import project.android.imageprocessing.filter.processing.SharpenFilter;
import project.android.imageprocessing.filter.processing.SingleComponentGaussianBlurFilter;
import project.android.imageprocessing.filter.processing.FastBlurFilter;
import project.android.imageprocessing.filter.processing.UnsharpMaskFilter;
import project.android.imageprocessing.input.ImageResourceInput;
import project.android.imageprocessing.output.ScreenEndpoint;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageProcessingActivity extends Activity {

	private FastImageProcessingView view;
	private List<BasicFilter> filters;
	private int curFilter;
	private ImageResourceInput image;
	private long touchTime;
	private FastImageProcessingPipeline pipeline;
	private ScreenEndpoint screen;
	
	private void addFilter(BasicFilter filter) {
		filters.add(filter);		
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
		filters = new ArrayList<BasicFilter>();
		addFilter(new MedianFilter());
		GaussianBlurPositionFilter positionBlur = new GaussianBlurPositionFilter(2.3f, 1.2f, new PointF(0.4f,0.5f), 0.5f, 0.1f);
		positionBlur.registerFilter(image);
		addFilter(positionBlur);
		GaussianSelectiveBlurFilter selectBlur = new GaussianSelectiveBlurFilter(2.3f, 1.2f, new PointF(0.4f,0.5f), 0.5f, 0.1f);
		selectBlur.registerFilter(image);
		addFilter(selectBlur);
		addFilter(new SingleComponentGaussianBlurFilter(2.3f));
		addFilter(new SingleComponentFastBlurFilter());
		addFilter(new FastBlurFilter());
		UnsharpMaskFilter unsharp = new UnsharpMaskFilter(2.0f, 0.5f);
		unsharp.registerFilter(image);
		addFilter(unsharp);
		addFilter(new SharpenFilter(1f));
		addFilter(new LanczosResamplingFilter(256, 128));
		addFilter(new CropFilter(0.25f,0f,0.75f,1f));
		BasicFilter cFilter1 = new CropFilter(0.25f,0f,0.75f,1f);
		cFilter1.rotateClockwise90Degrees(1);
		addFilter(cFilter1);
		BasicFilter cFilter2 = new CropFilter(0.25f,0f,0.75f,1f);
		cFilter2.rotateClockwise90Degrees(2);
		addFilter(cFilter2);
		BasicFilter cFilter3 = new CropFilter(0.25f,0f,0.75f,1f);
		cFilter3.rotateClockwise90Degrees(3);
		addFilter(cFilter3);
		addFilter(new TransformFilter(new float[] {
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f,
				-0.5f, 0f, 0f, 1f
		}, false, false));
		addFilter(new ChromaKeyFilter(new float[] {1.0f, 0.3f, 0.0f}, 0.4f, 0.1f));
		addFilter(new AdaptiveThresholdFilter());
		addFilter(new BoxBlurFilter());
		addFilter(new LuminanceThresholdFilter(0.4f));
		addFilter(new OpacityFilter(0.5f));
		addFilter(new SepiaFilter());
		addFilter(new HazeFilter(0.3f,0.1f));
		addFilter(new FalseColourFilter(new float[]{0.0f,0.0f,0.5f}, new float[]{1.0f,0.0f,0.0f}));
		addFilter(new MonochromeFilter(new float[]{1.0f,0.8f,0.8f}, 1.0f));
		addFilter(new ColourInvertFilter());
		addFilter(new SoftEleganceFilter(this));
		addFilter(new GaussianBlurFilter(2.3f));
		addFilter(new MissEtikateFilter(this));
		addFilter(new AmatorkaFilter(this));
		addFilter(new LookupFilter(this, R.drawable.lookup_soft_elegance_1));
		addFilter(new HighlightShadowFilter(0f, 1f));
		Point[] defaultCurve = new Point[] {new Point(128,128), new Point(64,0), new Point(192,255)};
		addFilter(new ToneCurveFilter(defaultCurve,defaultCurve,defaultCurve,defaultCurve));
		addFilter(new HueFilter(3.14f/6f));
		addFilter(new BrightnessFilter(0.5f));
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
		addFilter(new ExposureFilter(0.95f));
		addFilter(new ContrastFilter(1.5f));
		addFilter(new SaturationFilter(0.5f));
		addFilter(new GammaFilter(1.75f));
		addFilter(new LevelsFilter(0.2f,0.8f,1f,0f,1f));
		
		screen = new ScreenEndpoint(pipeline);
		
		image.addTarget(screen);
		for(BasicFilter filter : filters) {
			filter.addTarget(screen);
		}
		
		pipeline.setRootRenderer(image);
		pipeline.startRendering();
		view.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				if(System.currentTimeMillis() - touchTime > 100) {
					pipeline.pauseRendering();
					touchTime = System.currentTimeMillis();
					if(curFilter == 0) {
						image.removeTarget(screen);
					} else {
						image.removeTarget(filters.get(curFilter-1));
					}
					curFilter=(curFilter+1)%(filters.size()+1);
					if(curFilter == 0) {
						image.addTarget(screen);
					} else {
						image.addTarget(filters.get(curFilter-1));
					}
					pipeline.startRendering();
					view.requestRender();
				}
				return false;
			}
		});
	}

}
