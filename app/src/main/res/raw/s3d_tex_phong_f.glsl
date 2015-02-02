/**
 * A texture-based Phong illuminated shader (fragment part).
 * 
 * Receives the interpolated vertices, normals and texture coordinates and computes the final color 
 * of the fragment using the Phong model.
 */

precision mediump float; // use medium precision

// texture
uniform sampler2D u_texture;
uniform int u_textureEnable;

// material properties
uniform vec3 u_ambientColor;
uniform vec3 u_diffuseColor;
uniform vec3 u_specularColor;
uniform float u_alpha;
uniform float u_shininess;

// receive the interpolated values from the vertex shader
varying vec3 v_normal;
varying vec2 v_textureCoords;

varying vec3 lightDir;
varying vec3 viewDir;

// Shader entry point
void main()
{
	vec3 reflectDir = reflect(-lightDir, v_normal);
    
	float lambertian = max(dot(lightDir, v_normal), 0.0);
	float specular = 0.0;
	
	if (lambertian > 0.0) {
		float specAngle = max(dot(reflectDir, viewDir), 0.0);
		specular = pow(specAngle, u_shininess);
	}
	
	vec3 texColor = vec3(1.0, 1.0, 1.0);
	float alpha = u_alpha;
	if (u_textureEnable == 1) {
		vec4 texColor4 = texture2D(u_texture, v_textureCoords);
		texColor = vec3(texColor4);
		if (texColor4.a < 1.0)
			alpha = texColor4.a;
	}
	
	gl_FragColor = vec4(u_ambientColor + u_diffuseColor * texColor + specular * u_specularColor * 0.01, alpha);
	
}
