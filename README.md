Overview
==========================

Image processing on the android is often a difficult task.  Camera and video input editing are complex to set up and often require 3rd party libraries written in C++. The goal of this project is help with this problem by providing a framework similar to Brad Larson's GPUImage but on android.  Unlike Brad Larson's GPUImage, this framework does not use multiple opengl contexts, and the image processing multi-threaded; however, it does take advantage of opengl shaders to provide a speed up in image processing.

Requirements
==========================
The whole library is written targeted to android 2.2 (API 8) and no 3rd party libraries with the exception the video input and output and Camera input.  The video and camera input require android 4+ (API 14+) because it uses SurfaceTexture.  The video output takes advantage of JavaCV to record the video.  From my tests, the video recording is not working on android 4+ devices but is working on android 2.2.

Documentation
==========================
The java documents for this project can be found in the doc folder in the project.  Also, in the examples folder, examples of each of the filters as well as each of the inputs and outputs are shown.
