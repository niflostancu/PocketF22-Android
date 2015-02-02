/**
 * A simple constant color shader (fragment part).
 */

precision mediump float; // use medium precision

varying vec4 vertexColor;

// Shader entry point
void main()
{
	// calculate the output color
	gl_FragColor = vertexColor;
}
