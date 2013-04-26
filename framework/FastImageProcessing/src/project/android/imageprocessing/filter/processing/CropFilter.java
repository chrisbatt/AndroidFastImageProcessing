package project.android.imageprocessing.filter.processing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import project.android.imageprocessing.filter.BasicFilter;

public class CropFilter extends BasicFilter {
	
	public CropFilter(float minX, float minY, float maxX, float maxY) {
		textureVertices = new FloatBuffer[4];
		
		float[] texData0 = new float[] {
	        minX, minY,//0.0f, 0.0f,
	        maxX, minY,//1.0f, 0.0f,
	        minX, maxY,//0.0f, 1.0f,
	        maxX, maxY//1.0f, 1.0f,
		};
		textureVertices[0] = ByteBuffer.allocateDirect(texData0.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[0].put(texData0).position(0);
		
		float[] texData1 = new float[] {
	        minX, maxY,//0.0f, 1.0f,
	        minX, minY,//0.0f, 0.0f,
	        maxX, maxY,//1.0f, 1.0f,
	        maxX, minY,//1.0f, 0.0f,
		};
		textureVertices[1] = ByteBuffer.allocateDirect(texData1.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[1].put(texData1).position(0);
			
		float[] texData2 = new float[] {
	        maxX, maxY,//1.0f, 1.0f,
	        minX, maxY,//0.0f, 1.0f,
	        maxX, minY,//1.0f, 0.0f,
	        minX, minY,//0.0f, 0.0f,
		};
		textureVertices[2] = ByteBuffer.allocateDirect(texData2.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[2].put(texData2).position(0);
		
		float[] texData3 = new float[] {
	        maxX, minY,//1.0f, 0.0f,
	        maxX, maxY,//1.0f, 1.0f,
	        minX, minY,//0.0f, 0.0f,
	        minX, maxY,//0.0f, 1.0f,
		};
		textureVertices[3] = ByteBuffer.allocateDirect(texData3.length * 4).order(ByteOrder. nativeOrder()).asFloatBuffer();
		textureVertices[3].put(texData3).position(0);
	}
}
