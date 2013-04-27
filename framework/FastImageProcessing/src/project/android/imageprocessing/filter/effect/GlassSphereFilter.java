package project.android.imageprocessing.filter.effect;

import android.graphics.PointF;

public class GlassSphereFilter extends SphereRefractionFilter {
	public GlassSphereFilter(PointF center, float radius, float refractiveIndex, float aspectRatio) {
		super(center, radius, refractiveIndex, aspectRatio);
	}

	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform vec2 "+UNIFORM_CENTER+";\n"
				+"uniform float "+UNIFORM_RADIUS+";\n"
				+"uniform float "+UNIFORM_DISTORTION_AMOUNT+";\n"
				+"uniform float "+UNIFORM_ASPECT_RATIO+";\n"
				+"const highp vec3 lightPosition = vec3(-0.5, 0.5, 1.0);\n"
				+"const highp vec3 ambientLightPosition = vec3(0.0, 0.0, 1.0);\n"

		  		+"void main(){\n"
		  		+"	highp vec2 textureCoordinateToUse = vec2("+VARYING_TEXCOORD+".x, ("+VARYING_TEXCOORD+".y * "+UNIFORM_ASPECT_RATIO+" + 0.5 - 0.5 * "+UNIFORM_ASPECT_RATIO+"));\n"
		  		+"	highp float distanceFromCenter = distance("+UNIFORM_CENTER+", textureCoordinateToUse);\n"
			    +" 	lowp float checkForPresenceWithinSphere = step(distanceFromCenter, "+UNIFORM_RADIUS+");\n"
			    +" 	distanceFromCenter = distanceFromCenter / "+UNIFORM_RADIUS+";\n"
			    +" 	highp float normalizedDepth = "+UNIFORM_RADIUS+" * sqrt(1.0 - distanceFromCenter * distanceFromCenter);\n"
			    +" 	highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - "+UNIFORM_CENTER+", normalizedDepth));\n"
			    +" 	highp vec3 refractedVector = 2.0 * refract(vec3(0.0, 0.0, -1.0), sphereNormal, "+UNIFORM_DISTORTION_AMOUNT+");\n"
			    +"	refractedVector.xy = -refractedVector.xy;\n"
			    +" 	highp vec3 finalSphereColor = texture2D("+UNIFORM_TEXTURE0+", (refractedVector.xy + 1.0) * 0.5).rgb;\n"
			    +"	highp float lightingIntensity = 2.5 * (1.0 - pow(clamp(dot(ambientLightPosition, sphereNormal), 0.0, 1.0), 0.25));\n" // Grazing angle lighting
			    +"	finalSphereColor += lightingIntensity;\n"
			    +"	lightingIntensity  = clamp(dot(normalize(lightPosition), sphereNormal), 0.0, 1.0);\n" // Specular lighting
			    +"	lightingIntensity  = pow(lightingIntensity, 15.0);\n"
			    +"	finalSphereColor += vec3(0.8, 0.8, 0.8) * lightingIntensity;\n"
			    +"	gl_FragColor = vec4(finalSphereColor, 1.0) * checkForPresenceWithinSphere;\n"
		  		+"}\n";
	}
}
