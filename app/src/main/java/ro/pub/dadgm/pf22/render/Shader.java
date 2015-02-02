package ro.pub.dadgm.pf22.render;

import android.opengl.GLES20;

import ro.pub.dadgm.pf22.render.utils.ShaderLoader;

/**
 * The Shader class manages an OpenGL shader program.
 * 
 * <p>All shaders used with this class must follow the following conventions: 
 * <ul>
 *     <li>the individual shader programs use the following suffixes added to the given name: 
 *     "_v" for the vertex shader and "_f" for the fragment;</li>
 *     <li>the following uniform names are used for defining the MVP matrix: u_modelMatrix, 
 *     u_viewMatrix and u_projectionMatrix;</li>
 * </ul></p>
 * 
 * <p>It provides convenience methods for setting attributes uniforms and textures.</p>
 * 
 * <p>Can be extended to enhance its functionality with that of a specific shader group.</p>
 */
public class Shader {
	
	/**
	 * The camera object that contains the view and projection matrices.
	 */
	protected Camera camera = null;
	
	/**
	 * The initial vertex and fragment resources used to load the shader.
	 */
	protected Integer vertexResource, fragmentResource;
	
	/**
	 * The linked shader's handle.
	 */
	protected int program;
	
	
	
	/**
	 * Loads / initializes the shader program using the specified raw android resources.
	 * 
	 * @param vertexResource The vertex shader's android resource identifier.
	 * @param fragmentResource The fragment shader's android resource identifier.   
	 */
	public Shader(int vertexResource, int fragmentResource) {
		this.vertexResource = vertexResource;
		this.fragmentResource = fragmentResource;
		
		this.program = ShaderLoader.createProgram(vertexResource, fragmentResource, null);
		if (this.program == 0)
			throw new UnknownError("Unable to load the shader program! Please check the log for errors.");
	}
	
	
	/**
	 * Destroys the shader and all managed resources.
	 * 
	 * <p>This instance should not be used afterwards (dereference it from all objects).<p/>
	 */
	public void destroy() {
		if (vertexResource != null && this.fragmentResource != null) {
			ShaderLoader.deleteShaders(this.vertexResource, this.fragmentResource);
		}
	}
	
	/**
	 * Sets a new/modified camera object.
	 * 
	 * <p>Will resend of the shader's view/projection uniforms.</p>
	 * 
	 * @param camera The new/modified camera object.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
		
		use();
		int u_viewMatrix = GLES20.glGetUniformLocation(program, "u_viewMatrix");
		int u_projectionMatrix = GLES20.glGetUniformLocation(program, "u_projectionMatrix");
		GLES20.glUniformMatrix4fv(u_viewMatrix, 1, false, camera.getViewMatrix(), 0);
		GLES20.glUniformMatrix4fv(u_projectionMatrix, 1, false, camera.getProjectionMatrix(), 0);
	}
	
	/**
	 * Activates the current shader program.
	 */
	public void use() {
		GLES20.glUseProgram(program);
	}

	/**
	 * Returns the specified shader attribute's location.
	 * 
	 * @param attributeName The attribute to return location for.
	 * @return The attribute's GL handle.
	 */
	public int getAttribLocation(String attributeName) {
		return GLES20.glGetAttribLocation(program, attributeName);
	}
	
	/**
	 * Returns the specified shader uniform's location.
	 *
	 * @param uniformName The uniform to return location for.
	 * @return The uniform's GL handle.
	 */
	public int getUniformLocation(String uniformName) {
		return GLES20.glGetUniformLocation(program, uniformName);
	}
	
	/**
	 * Returns the shader's GL handle for use in external GLES calls.
	 * 
	 * @return The shader program's handle.
	 */
	public int getProgram() {
		return program;
	}
	
	
}
