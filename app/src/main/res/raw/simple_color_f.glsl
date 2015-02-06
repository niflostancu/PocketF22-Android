/**
 * A simple static color shader (fragment part).
 * 
 * Applies the received color to the fragments.
 */

precision mediump float; // use medium precision

// receive a static color
uniform vec4 u_color;

// Shader entry point
void main()
{
	// calculate the output color
	gl_FragColor = u_color;
}
