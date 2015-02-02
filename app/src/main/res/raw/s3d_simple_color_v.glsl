/**
 * A simple constant color shader (vertex part).
 */

// receives MVP matrices as uniforms
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

// vertex attributes
attribute vec3 a_position;

varying vec4 vertexColor;

const vec4 c_color = vec4(1.0, 0.1, 0.1, 1.0);

// Shader entry point
void main()
{
	// pass through the texture data
	vertexColor = c_color;
	
	// calculate the final position of the vertex
	mat4 mvpMatrix = u_projectionMatrix * u_viewMatrix * u_modelMatrix;
	gl_Position = mvpMatrix * vec4(a_position, 1.0);
}
