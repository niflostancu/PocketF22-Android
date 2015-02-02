/**
 * The draw text shader (fragment part).
 * 
 * Applies the received texture to the fragments.
 * Also receives a color uniform that specifies whether to blend the texture.
 */

precision mediump float; // use medium precision

// receive a texture image and a static color
uniform vec4 u_color;
uniform sampler2D u_texture;

// receive the varying texture coordinates from the vertex shader
varying vec2 v_textureCoords;

// Shader entry point
void main()
{
	// calculate the output color
	gl_FragColor = u_color * texture2D(u_texture, v_textureCoords);
}
