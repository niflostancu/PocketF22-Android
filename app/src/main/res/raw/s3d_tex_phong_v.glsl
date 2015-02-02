/**
 * A texture-based Phong illuminated shader (vertex part).
 * 
 * Applies M*V*P to the vertices and passes on the normal and light sources to the fragment.
 */

// receives MVP matrices as uniforms
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_normalMatrix;

// lights
uniform vec3 u_lightPos;

// vertex attributes
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_textureCoords;

// pass the vertex and color information to the fragment shader
varying vec3 v_normal;
varying vec2 v_textureCoords;
varying vec3 lightDir;
varying vec3 viewDir;

// Shader entry point
void main()
{
	// calculate the final position of the vertex
	mat4 mvMatrix = u_viewMatrix * u_modelMatrix;
	mat4 mvpMatrix = u_projectionMatrix * mvMatrix;
	
	gl_Position = mvpMatrix * vec4(a_position, 1.0);
	
	// compute vertex and normal coordinates in ModelView space
	vec3 v_position = vec3(mvMatrix * vec4(a_position, 1.0));
	v_normal = vec3(mvMatrix * vec4(a_normal, 0.0));
	v_textureCoords = a_textureCoords;
	
	vec3 mvLightPos = vec3(u_viewMatrix * vec4(u_lightPos, 0.0));
	lightDir = normalize(mvLightPos.xyz - v_position.xyz);
	viewDir = normalize(-v_position);
}
