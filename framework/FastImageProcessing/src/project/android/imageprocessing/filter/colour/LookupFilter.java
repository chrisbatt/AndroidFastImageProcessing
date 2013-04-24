package project.android.imageprocessing.filter.colour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import project.android.imageprocessing.filter.MultiInputFilter;
import project.android.imageprocessing.helper.ImageHelper;
import project.android.imageprocessing.input.GLTextureOutputRenderer;

public class LookupFilter extends MultiInputFilter {
	private int lookup_texture;
	private Bitmap lookupBitmap;
	
	public LookupFilter(Context context, int id) {
		super(2);
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
		lookupBitmap = BitmapFactory.decodeResource(context.getResources(), id, options);
	}
	
	@Override
	protected void initWithGLContext() {
		super.initWithGLContext();
		lookup_texture = ImageHelper.bitmapToTexture(lookupBitmap);
        texture[0] = lookup_texture;
	}
	
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		if(filterLocations.size() < 2 || !source.equals(filterLocations.get(0))) {
			clearRegisteredFilters();
			registerFilter(source, 0);
			registerFilter(this, 1);
		}
		
		super.newTextureReady(lookup_texture, this);
		super.newTextureReady(texture, source);
	}
	
	@Override
	protected String getVertexShader() {
		return
					"attribute vec4 "+ATTRIBUTE_POSITION+";\n"
				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"	
				  + "attribute vec4 "+ATTRIBUTE_POSITION+1+";\n"
				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+1+";\n"
				  + "varying vec2 "+VARYING_TEXCOORD+";\n"	
				  
				  + "void main() {\n"	
				  + "  "+VARYING_TEXCOORD+" = "+ATTRIBUTE_TEXCOORD+";\n"
				  + "   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 
				  + "}\n";
	}
	
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
				+"uniform sampler2D "+UNIFORM_TEXTUREBASE+1+";\n" 
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				
		  		+ "void main(){\n"
		  		+ "  vec4 texColour = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"
		  		+ "  float blueColor = texColour.b * 63.0;\n"
		  		+ "  vec2 quad1;\n"
		  		+ "  quad1.y = floor(floor(blueColor) / 8.0);\n"
		  		+ "  quad1.x = floor(blueColor) - (quad1.y * 8.0);\n"
		  		+ "  vec2 quad2;\n"
		  		+ "  quad2.y = floor(ceil(blueColor) / 8.0);\n"
		  		+ "  quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n"
		  		+ "  vec2 texPos1;\n"
		  		+ "  texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * texColour.r);\n"
		  		+ "  texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * texColour.g);\n"
		  		+ "  vec2 texPos2;\n"
		  		+ "  texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * texColour.r);\n"
		  	    + "  texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * texColour.g);\n"
		  		+ "  vec4 newColor1 = texture2D("+UNIFORM_TEXTUREBASE+1+", texPos1);\n"
		  		+ "  vec4 newColor2 = texture2D("+UNIFORM_TEXTUREBASE+1+", texPos2);\n"
		  		+ "  vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n"
		  		+ "  gl_FragColor = vec4(newColor.rgb, texColour.a);\n"
		  		+ "}\n";		
	}
}
