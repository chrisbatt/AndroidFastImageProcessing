package project.android.imageprocessing.filter.colour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.graphics.Point;
import android.opengl.GLES20;

import project.android.imageprocessing.filter.BasicFilter;

public class ToneCurveFilter extends BasicFilter {
	private static final String UNIFORM_RED_CURVE = "u_RedCurve";
	private static final String UNIFORM_GREEN_CURVE = "u_GreenCurve";
	private static final String UNIFORM_BLUE_CURVE = "u_BlueCurve";
	
	private int redCurveHandle;
	private int greenCurveHandle;
	private int blueCurveHandle;
	private int[] redPart;
	private int[] greenPart;
	private int[] bluePart;
	
	
	public ToneCurveFilter(Point[] red, Point[] green, Point[] blue, Point[] rgbComposite) {
		float[] redCurve = getPreparedSpline(red);
		float[] blueCurve = getPreparedSpline(blue);
		float[] greenCurve = getPreparedSpline(green);
		float[] rgbCompositeCurve = getPreparedSpline(rgbComposite);
		
		redPart = new int[256];
		greenPart = new int[256];
		bluePart = new int[256];
		
		for (int i = 0; i < 256; i++) {
            redPart[i] = (int) Math.min(Math.max(i + redCurve[i] + rgbCompositeCurve[i], 0), 255);
            greenPart[i] = (int) Math.min(Math.max(i + greenCurve[i] + rgbCompositeCurve[i], 0), 255);
            bluePart[i] = (int) Math.min(Math.max(i + blueCurve[i] + rgbCompositeCurve[i], 0), 255);
        }
	}
	
	private float[] getPreparedSpline(Point[] points) {
		Arrays.sort(points, new Comparator<Point>() {
			@Override
			public int compare(Point lhs, Point rhs) {
				return lhs.x - rhs.x;
			}
		});
		
		List<Point> spline = getSplineCurve(points);
        // If we have a first point like (0.3, 0) we'll be missing some points at the beginning
        // that should be 0.		
		if(spline.get(0).x > 0) {
			for(int i = spline.get(0).x; i >= 0; i--) {
				spline.add(0, new Point(i,0));
			}
		}

        // Insert points similarly at the end, if necessary.
		if(spline.get(spline.size()-1).x < 255) {
			for(int i = spline.get(spline.size()-1).x; i < 256; i++) {
				spline.add(new Point(i,255));
			}
		}
		
		// Prepare the spline points.
        float[] preparedSplinePoints = new float[spline.size()];
        for (int i=0; i<spline.size(); i++) 
        {
            Point newPoint = spline.get(i);
            Point origPoint = new Point(newPoint.x, newPoint.x);
            
            float distance = (float) Math.sqrt(Math.pow((origPoint.x - newPoint.x), 2.0) + Math.pow((origPoint.y - newPoint.y), 2.0));
            
            if (origPoint.y > newPoint.y) 
            {
                distance = -distance;
            }
            
            preparedSplinePoints[i] = distance;
        }
        
        return preparedSplinePoints;
	}
	
	private List<Point> getSplineCurve(Point[] points) {
	    double[] sdA = secondDerivative(points);
	    
	    int n = sdA.length;
	    if (n < 1) {
	        return null;
	    }
	    
	    
	    List<Point> output = new ArrayList<Point>(n+1);
	                              
	    for(int i=0; i<n-1 ; i++) {
	        Point cur = points[i];
	        Point next = points[(i+1)];
	        
	        for(int x=cur.x;x<(int)next.x;x++) {
	            double t = (double)(x-cur.x)/(next.x-cur.x);
	            
	            double a = 1-t;
	            double b = t;
	            double h = next.x-cur.x;
	            
	            double y= a*cur.y + b*next.y + (h*h/6)*( (a*a*a-a)*sdA[i]+ (b*b*b-b)*sdA[i+1] );
	                        
	            if (y > 255.0) {
	                y = 255.0;   
	            } else if (y < 0.0) {
	                y = 0.0;   
	            }
	            
	           output.add(new Point(x,(int)y));
	        }
	    }
	    
	    if(output.size() == 255) {
	    	output.add(points[points.length-1]);
	    }
	    
	    return output;
	}

	private double[] secondDerivative(Point[] points) {
	    int n = points.length;
	    if (n <= 1) {
	        return null;
	    }
	    
	    double[][] matrix = new double[n][3];
	    double[] result = new double[n];
	    matrix[0][1]=1;
	    // What about matrix[0][1] and matrix[0][0]? Assuming 0 for now (Brad L.)
	    matrix[0][0]=0;    
	    matrix[0][2]=0;    
	    
	    for(int i=1;i<n-1;i++) {
	        Point P1 = points[(i-1)];
	        Point P2 = points[i];
	        Point P3 = points[(i+1)];
	        
	        matrix[i][0]=(double)(P2.x-P1.x)/6;
	        matrix[i][1]=(double)(P3.x-P1.x)/3;
	        matrix[i][2]=(double)(P3.x-P2.x)/6;
	        result[i]=(double)(P3.y-P2.y)/(P3.x-P2.x) - (double)(P2.y-P1.y)/(P2.x-P1.x);
	    }
	    
	    // What about result[0] and result[n-1]? Assuming 0 for now (Brad L.)
	    result[0] = 0;
	    result[n-1] = 0;

	    matrix[n-1][1]=1;
	    // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
	    matrix[n-1][0]=0;
	    matrix[n-1][2]=0;
	    
	  	// solving pass1 (up->down)
	  	for(int i=1;i<n;i++) 
	    {
			double k = matrix[i][0]/matrix[i-1][1];
			matrix[i][1] -= k*matrix[i-1][2];
			matrix[i][0] = 0;
			result[i] -= k*result[i-1];
	    }
		// solving pass2 (down->up)
		for(int i=n-2;i>=0;i--) 
	    {
			double k = matrix[i][2]/matrix[i+1][1];
			matrix[i][1] -= k*matrix[i+1][0];
			matrix[i][2] = 0;
			result[i] -= k*result[i+1];
		}
	    
	    double[] y2 = new double[n];
	    for(int i=0;i<n;i++) y2[i]=result[i]/matrix[i][1];
	    
	    return y2;
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		redCurveHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_RED_CURVE);
		greenCurveHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_GREEN_CURVE); 
		blueCurveHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_BLUE_CURVE);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1iv(redCurveHandle, 256, redPart, 0);
		GLES20.glUniform1iv(greenCurveHandle, 256, greenPart, 0);
		GLES20.glUniform1iv(blueCurveHandle, 256, bluePart, 0);
	}

	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
				+"uniform int "+UNIFORM_RED_CURVE+"[256];\n"
				+"uniform int "+UNIFORM_GREEN_CURVE+"[256];\n"
				+"uniform int "+UNIFORM_BLUE_CURVE+"[256];\n"
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				
		  		+ "void main(){\n"
		  		+ "   vec4 texColour = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"
		  		+ "   float r = texColour.r;\n"
		  		+ "   float r2 = r * 255.0;\n"
		  		+ "   int rInt = int(r2);\n"
		  		+ "   float rVal = float("+UNIFORM_RED_CURVE+"[rInt]) / 255.0;\n"
		  		+ "   float g = texColour.g;\n"
		  		+ "   float g2 = g * 255.0;\n"
		  		+ "   int gInt = int(g2);\n"
		  		+ "   float gVal = float("+UNIFORM_GREEN_CURVE+"[gInt]) / 255.0;\n"
		  		+ "   float b = texColour.b;\n"
		  		+ "   float b2 = b * 255.0;\n"
		  		+ "   int bInt = int(b2);\n"
		  		+ "   float bVal = float("+UNIFORM_BLUE_CURVE+"[bInt]) / 255.0;\n"
		  		+ "   gl_FragColor = vec4(rVal,gVal,bVal,texColour.a);\n"
		  		+ "}\n";		
	}
}
