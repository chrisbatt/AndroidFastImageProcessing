Overview
==========================

Image processing on the android is often a difficult task.  Camera and video input editing are complex to set up and often require 3rd party libraries written in C++. The goal of this project is help with this problem by providing a framework similar to Brad Larson's GPUImage but on android.  Unlike Brad Larson's GPUImage, this framework does not use multiple opengl contexts, and the image processing multi-threaded; however, it does take advantage of opengl shaders to provide a speed up in image processing.

Requirements
==========================
The whole library is written targeted to android 2.2 (API 8) and no 3rd party libraries with the exception the video input and output and Camera input.  The video and camera input require android 4+ (API 14+) because it uses SurfaceTexture.  The video output takes advantage of JavaCV to record the video.  From my tests, the video recording is not working on android 4+ devices but is working on android 2.2.

Documentation
==========================
The java documents for this project can be found in the doc folder in the project.  Also, in the examples folder, examples of each of the filters as well as each of the inputs and outputs are shown.

Setup (Eclipse)
==========================
1. Checkout or download the framework
2. Import the framework into current eclipse workspace
3. Added the framework to project as an android library dependance

i) Right click project -> Build Path -> Configure Build Path
![Alt text](/doc/setup/step1.png)
ii) In the list on the left, select "android" -> under library, click "add" -> in the popup, select "FastImageProcessing"
![Alt text](/doc/setup/step2.png)

4. Write code?
5. 
Basic setup for a simple filter pipeline.  More examples can be found in "/examples".
```java
public class ImageProcessingActivity extends Activity {
  private FastImageProcessingView view;
	private FastImageProcessingPipeline pipeline;
	private ImageResourceInput imageIn;
	private GenericFilter generic;
	private ScreenEndpoint screen;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		view = new FastImageProcessingView(this);
		pipeline = new FastImageProcessingPipeline();
		view.setPipeline(pipeline);
		setContentView(view);
		imageIn = new ImageResourceInput(view, this, R.drawable.wakeboard);
		generic = new GenericFilter();
		screen = new ScreenEndpoint(pipeline);
		imageIn.addTarget(generic);
		generic.addTarget(screen);
		pipeline.addRootRenderer(imageIn);
		pipeline.startRendering();
	}
}
```
