/**
 * A simple textured shader (vertex part).
 * 
 * Applies M*V*P to the vertices and passes on the texture data (to the fragment shader).
 */

// receives MVP matrices as uniforms
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

// vertex attributes
attribute vec4 a_position;   				
attribute vec2 a_textureCoords;

// texture coordinates to the fragment shader
varying vec2 v_textureCoords;

// Shader entry point
void main()
{
	// pass through the texture data
	v_textureCoords = a_textureCoords;
	
	// calculate the final position of the vertex
	mat4 mvpMatrix = u_modelMatrix * u_viewMatrix * u_projectionMatrix;
	gl_Position = mvpMatrix * a_position;
}
