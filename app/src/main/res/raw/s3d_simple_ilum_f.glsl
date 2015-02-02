/**
 * A Phong shader (fragment part).
 * 
 * Receives the interpolated vertices and normals and computes the final color of the fragment 
 * using the Phong model.
 */

precision mediump float; // use medium precision

// receive the interpolated values from the vertex shader
// varying vec4 v_color;
varying vec3 v_position;
varying vec3 v_normal;

// constants used for testing
const vec3 objectColor = vec3(1.0, 0.1, 0.1);
const vec3 ambientColor = vec3(0.1, 0.1, 0.1);
const vec3 specColor = vec3(1.0, 1.0, 1.0);

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
		specular = pow(specAngle, 5.0);
	}
	
	gl_FragColor = vec4(ambientColor * objectColor + lambertian * objectColor + specular * specColor, 1.0);
}
