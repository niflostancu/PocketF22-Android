/**
 * A simple static color shader (vertex part).
 * 
 * Applies M*V*P to the vertices.
 */

// receives MVP matrices as uniforms
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

// vertex attributes
attribute vec3 a_position;

// Shader entry point
void main()
{
	// calculate the final position of the vertex
	mat4 mvpMatrix = u_projectionMatrix * u_viewMatrix * u_modelMatrix;
	gl_Position = mvpMatrix * vec4(a_position, 1.0);
}
