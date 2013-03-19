package project.android.imageprocessing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * The base renderer class that all inputs, filters and endpoints must extend.  
 * @author Chris Batt
 */
public abstract class GLRenderer {
	protected static final String ATTRIBUTE_POSITION = "a_Position";
	protected static final String ATTRIBUTE_TEXCOORD = "a_TexCoord";
	protected static final String VARYING_TEXCOORD = "v_TexCoord";
	protected static final String UNIFORM_TEXTUREBASE = "u_Texture";
	protected static final String UNIFORM_TEXTURE0 = UNIFORM_TEXTUREBASE+0;
	
	protected int curRotation;
	protected FloatBuffer[] squareVertices;
	
	protected int programHandle;
	protected int textureHandle;
	protected int positionHandle;
	protected int texCoordHandle;
	
	protected int texture_in;
	
	protected int width;
	protected int height;
	
	protected boolean customSizeSet;
	
	public GLRenderer() {
		squareVertices = new FloatBuffer[4];
		
		float[] squareData0 = {
	        -1f, 1f, 0.0f,
	        0f, 1f,
	        
	        1f, 1f, 0.0f,
	        1f, 1f,
	        
	        1f, -1f, 0.0f,
	        1f, 0f,
	        
	        -1f, 1f, 0.0f,
	        0f, 1f,
	        
	        1f, -1f, 0.0f,
	        1f, 0f,
	        
	        -1f, -1f, 0.0f,
	        0f, 0f
		};
	
		squareVertices[0] = ByteBuffer.allocateDirect(squareData0.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		squareVertices[0].put(squareData0).position(0);
		
		float[] squareData1 = {
            -1f, 1f, 0.0f,
            0f, 0f,
            
            1f, 1f, 0.0f,
            0f, 1f,
            
            1f, -1f, 0.0f,
            1f, 1f,
            
            -1f, 1f, 0.0f,
            0f, 0f,
            
            1f, -1f, 0.0f,
            1f, 1f,
            
            -1f, -1f, 0.0f,
            1f, 0f
		};

		squareVertices[1] = ByteBuffer.allocateDirect(squareData1.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		squareVertices[1].put(squareData1).position(0);
		
		float[] squareData2 = {
	            -1f, 1f, 0.0f,
	            1f, 0f,
	            
	            1f, 1f, 0.0f,
	            0f, 0f,
	            
	            1f, -1f, 0.0f,
	            0f, 1f,
	            
	            -1f, 1f, 0.0f,
	            1f, 0f,
	            
	            1f, -1f, 0.0f,
	            0f, 1f,
	            
	            -1f, -1f, 0.0f,
	            1f, 1f
			};

			squareVertices[2] = ByteBuffer.allocateDirect(squareData2.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
			squareVertices[2].put(squareData2).position(0);
			
			float[] squareData3 = {
			    -1f, 1f, 0.0f,
			    1f, 1f,
			    
			    1f, 1f, 0.0f,
			    1f, 0f,
			    
			    1f, -1f, 0.0f,
			    0f, 0f,
			    
			    -1f, 1f, 0.0f,
			    1f, 1f,
			    
			    1f, -1f, 0.0f,
			    0f, 0f,
			    
			    -1f, -1f, 0.0f,
			    0f, 1f
			};
			
			squareVertices[3] = ByteBuffer.allocateDirect(squareData3.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
			squareVertices[3].put(squareData3).position(0);
		
		curRotation = 0;
		texture_in = 0;
		customSizeSet = false;
	}
	
	/**
	 * Returns the current width the GLRenderer is rendering at.
	 * @return width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the current height the GLRenderer is rendering at.
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Rotates the renderer clockwise by 90 degrees a given number of times.
	 * @param numOfTimes
	 * The number of times this renderer should be rotated clockwise by 90 degrees.
	 */
	public void rotateClockwise90Degrees(int numOfTimes) {
		curRotation += numOfTimes;
		curRotation = curRotation % 4;
		if(numOfTimes%2==1) {
			int temp = width;
			width = height;
			height = temp;
		}
	}
	
	/**
	 * Rotates the renderer counter-clockwise by 90 degrees a given number of times.
	 * @param numOfTimes
	 * The number of times this renderer should be rotated counter-clockwise by 90 degrees.
	 */
	public void rotateCounterClockwise90Degrees(int numOfTimes) {
		curRotation += 4 - (numOfTimes % 4);
		curRotation = curRotation % 4;
		if(numOfTimes%2==1) {
			int temp = width;
			width = height;
			height = temp;
		}
	}
	
	/**
	 * Sets the render size of the renderer to the given width and height. 
	 * This also prevents the size of the renderer from changing automatically 
	 * when one of the source(s) of the renderer has a size change.
	 * @param width
	 * The width at which the renderer should draw at.
	 * @param height
	 * The height at which the renderer should draw at.
	 */
	public void setRenderSize(int width, int height) {
		customSizeSet = true;
		if(curRotation%2 == 1) {
			this.width = height;
			this.height = width;
		} else {
			this.width = width;
			this.height = height;
		}
	}
	
	protected void passShaderValues() {
		squareVertices[curRotation].position(0);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, squareVertices[curRotation]);  
		GLES20.glEnableVertexAttribArray(positionHandle); 
		squareVertices[curRotation].position(3);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 20, squareVertices[curRotation]);  
		GLES20.glEnableVertexAttribArray(texCoordHandle); 
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_in);
	    GLES20.glUniform1i(textureHandle, 0);
	}
	
	protected void bindShaderAttributes() {
		GLES20.glBindAttribLocation(programHandle, 0, ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(programHandle, 1, ATTRIBUTE_TEXCOORD);
	}
	
	protected void initShaderHandles() {
        textureHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXTURE0);
        positionHandle = GLES20.glGetAttribLocation(programHandle, ATTRIBUTE_POSITION);
        texCoordHandle = GLES20.glGetAttribLocation(programHandle, ATTRIBUTE_TEXCOORD);
	}
	
	/**
	 * Draws the given texture using OpenGL and the given vertex and fragment shaders.
	  Calling of this method is handled by the {@link FastImageProcessingPipeline} and should not be called manually.
	 */
	public void onDrawFrame() {
		if(texture_in == 0) {
			return;
		}
		GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(programHandle);

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1f);
		
		passShaderValues();
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6); 
	}
	
	protected String getVertexShader() {
		return 
					"attribute vec4 "+ATTRIBUTE_POSITION+";\n"
				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"	
				  + "varying vec2 "+VARYING_TEXCOORD+";\n"	
				  
				  + "void main() {\n"	
				  + "  "+VARYING_TEXCOORD+" = "+ATTRIBUTE_TEXCOORD+";\n"
				  + "   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 
				  + "}\n";
	}

	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				
		  		+ "void main(){\n"
		  		+ "   gl_FragColor = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"	
		  		+ "}\n";		
	}
	
	/**
	 * Sets up the OpenGL context for the given renderer.  Compiles the program along with the vertex and fragment shaders.
	 * Calling of this method is handled by the {@link FastImageProcessingPipeline} and should not be called manually.
	 */
	public void onSurfaceCreated() {
		final String vertexShader = getVertexShader();
		final String fragmentShader = getFragmentShader();									

		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

		if (vertexShaderHandle != 0) {
			GLES20.glShaderSource(vertexShaderHandle, vertexShader);
			GLES20.glCompileShader(vertexShaderHandle);
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			if (compileStatus[0] == 0) {				
				GLES20.glDeleteShader(vertexShaderHandle);
				vertexShaderHandle = 0;
			}
		}

		if (vertexShaderHandle == 0) {
			throw new RuntimeException("Could not create vertex shader.");
		}

		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		if (fragmentShaderHandle != 0) {
			GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
			GLES20.glCompileShader(fragmentShaderHandle);
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			if (compileStatus[0] == 0) {				
				GLES20.glDeleteShader(fragmentShaderHandle);
				fragmentShaderHandle = 0;
			}
		}
		if (fragmentShaderHandle == 0) {
			throw new RuntimeException("Could not create fragment shader.");
		}

			programHandle = GLES20.glCreateProgram();
			if (programHandle != 0) {
				GLES20.glAttachShader(programHandle, vertexShaderHandle);	
				GLES20.glAttachShader(programHandle, fragmentShaderHandle);
				
				bindShaderAttributes();
				
				GLES20.glLinkProgram(programHandle);
				final int[] linkStatus = new int[1];
				GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
				if (linkStatus[0] == 0) {				
					GLES20.glDeleteProgram(programHandle);
					programHandle = 0;
				}
			}
			if (programHandle == 0) {
				throw new RuntimeException("Could not create program.");
			}

	        initShaderHandles();
	}
	
	/**
	 * An empty method that can be override if required by classes extending this renderer.
	 * Calling of this method is handled by the {@link FastImageProcessingPipeline} and should not be called manually.
	 */
	public void onPause() {
		
	}
	
	/**
	 * An empty method that can be override if required by classes extending this renderer.
	 * Calling of this method is handled by the {@link FastImageProcessingPipeline} and should not be called manually.
	 */
	public void onResume() {
		
	}
}
