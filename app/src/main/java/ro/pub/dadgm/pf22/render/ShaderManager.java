package ro.pub.dadgm.pf22.render;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of shader programs that are used to draw the scene.
 * 
 * <p>The shaders can be registered by the view or the scene objects.</p>
 */
public class ShaderManager {
	
	/**
	 * A map of available shaders.
	 * 
	 * <p>A shader program is identified by a unique name.</p>
	 */
	protected Map<String, Shader> shaderMap;
	
	
	/**
	 * Constructs a new ShaderManager instance.
	 */
	public ShaderManager() {
		shaderMap = new HashMap<>();
	}
	
	/**
	 * Initializes and registers the shader from the specified raw Android resources.
	 * 
	 * @param name Shader's identifier.
	 * @param vertexResource The vertex shader's android resource identifier.
	 * @param fragmentResource The fragment shader's android resource identifier.
	 */
	public void registerShader(String name, int vertexResource, int fragmentResource) {
		Shader shader = new Shader(vertexResource, fragmentResource);
		
		shaderMap.put(name, shader);
	}
	
	/**
	 * Returns the shader with the specified name.
	 * 
	 * <p>If the shader is not registered, returns null.</p>
	 * 
	 * @param name Shader's identifier.
	 * @return The shader object or null if not found.
	 */
	public Shader getShader(String name) {
		return shaderMap.get(name);
	}
	
	/**
	 * Notifies all shader programs that the camera object has changed.
	 * 
	 * @param camera The new or modified camera object to send to the shaders.
	 */
	public void notifyCameraChanged(Camera camera) {
		for (Shader shader: shaderMap.values()) {
			shader.setCamera(camera);
		}
	}
	
	/**
	 * Cleans up the internal state of the object.
	 * To be called when the EGL context has been reset.
	 */
	public void clear() {
		shaderMap.clear();
	}
	
}
