package ro.pub.dadgm.pf22.render.utils;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import ro.pub.dadgm.pf22.activity.MainActivity;

/**
 * Utility static class that can be used for loading OpenGL2 shader programs.
 * 
 * <p>It reads the shader code from the application's raw resources directory and builds the shader 
 * program. It also caches the compiled shaders and future load requests will be fetched from cache.</p>
 * 
 * <p>Use the methods only from the rendering thread!</p>
 */
public class ShaderLoader {
	
	/**
	 * An internal shaders cache used to store already compiled shaders.
	 * 
	 * <p>It will also store linked programs. The keys used are: 
	 * <ul>
	 *     <li><i>shad_{TYPE}_{RESOURCEID}</i> - for shaders</li>
	 *     <li><i>prog_{VERTEX_SHADER}_{FRAGMENT_SHADER}</i> - for shader programs</li>
	 * </ul>
	 * </p>
	 */
	protected static Map<String, Integer> shaderCache = new HashMap<>();
	
	
	/**
	 * Loads a shader program from a resource.
	 * 
	 * @param type GL shader's type (GL_VERTEX_SHADER / GL_FRAGMENT_SHADER).
	 * @param resourceId The resource ID of the program.
	 * @return Shader's GL identifier, 0 if load failed.
	 */
	public static int loadShader(int type, int resourceId) {
		// check the shader cache if the shader was already loaded
		String cacheKey = "shad_" + type + "_" + resourceId;
		if (shaderCache.containsKey(cacheKey)) {
			return shaderCache.get(cacheKey);
		}
		
		String shaderCode = readRawTextResource(
				MainActivity.getAppContext(), resourceId);
		if (shaderCode == null)
			return 0; // shader not found
		
		int shader = GLES20.glCreateShader(type);
		if (shader == 0) {
			return 0;
		}
		
		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		// add it to the cache
		shaderCache.put(cacheKey, shader);
		
		return shader;
	}

	/**
	 * Loads the shader resources specified and links them together into a program.
	 * 
	 * @param vertexResource The android resource ID for the vertex shader code.
	 * @param fragmentResource The android resource ID for the fragment shader code.
	 * @return A GL program identifier if successful, 0 otherwise.
	 */
	public static int createProgram(int vertexResource, int fragmentResource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexResource);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentResource);
		
		// check the shader cache if the program was already linked
		String cacheKey = "prog_" + vertexShader + "_" + fragmentShader;
		if (shaderCache.containsKey(cacheKey)) {
			return shaderCache.get(cacheKey);
		}
		
		if (vertexShader == 0 || fragmentShader == 0) {
			return 0; // shader creation failed
		}
		
		int program = GLES20.glCreateProgram();
		if (program == 0) {
			return 0; // error
		}
		
		// link the two shaders into a program
		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);
		
		// add it to the cache
		shaderCache.put(cacheKey, program);
		
		return program;
	}

	/**
	 * Reads a raw text resource file from the application's assets directory.
	 * 
	 * @param context Application's context.
	 * @param resourceId The ID of the resource to read.
	 * @return A string with the contents of the resource, null if reading failed.
	 */
	protected static String readRawTextResource(Context context, int resourceId) {
		InputStream inputStream = context.getResources().openRawResource(resourceId);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		String nextLine;
		StringBuilder body = new StringBuilder();
		
		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}
		
		return body.toString();
	}

}
