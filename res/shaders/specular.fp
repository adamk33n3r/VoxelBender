varying vec3 color;


void main() {
	// Turns the varying color into a 4D color and stores in the built-in output gl_FracColor
	gl_FragColor = vec4(color, 1);
}