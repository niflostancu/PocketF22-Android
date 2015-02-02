package ro.pub.dadgm.pf22.render.objects.hud;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.render.Camera;
import ro.pub.dadgm.pf22.render.utils.BufferUtils;
import ro.pub.dadgm.pf22.render.utils.ShaderLoader;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;

/**
 * Draws the menu background (an image).
 * 
 * <p>The menu background is a quad filled with a texture.</p>
 *
 * <p>The scene manager is responsible for correctly initializing its dimensions and a z depth so it 
 * is always drawn behind everything else.</p>
 */
public class MenuBackground extends HUDObject {
	
	/**
	 * Handle of the loaded texture object.
	 */
	protected int texture;
	
	/**
	 * The object's triangles.
	 */
	protected static final float[] staticVertexArray = {
		-1f,  1f, 0, // top left
		-1f, -1f, 0, // bottom left
		 1f, -1f, 0, // bottom right
		 1f,  1f, 0 // top right
	};
	
	/**
	 * Object's triangles (vertex indices array).
	 */
	protected static final short[] staticIndexArray = {
		0, 1, 2, 0, 2, 3 // 2 triangles that form a quad
	};
	
	/**
	 * Texture's coordinates.
	 */
	protected static final float[] staticTextureCoords = {
		0f, 0f, // top left
		0f, 1f, // bottom left
		1f, 1f, // bottom right
		1f, 0f  // top right
	};

	/**
	 * Static color to blend the texture.
	 */
	protected static final float[] staticColor = {
		0.5f, 0.5f, 0.5f, 1.0f
	};
	
	
	/**
	 * A FloatBuffer for the texture coordinates.
	 */
	protected FloatBuffer textureCoordsBuf;
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 * @param camera Reference to the camera object to use.
	 */
	public MenuBackground(String tag, int priority, Camera camera) {
		super(tag, priority, camera);
		
		// load the GLSL program
		program = ShaderLoader.createProgram(R.raw.simple_tex_v, R.raw.simple_tex_f, null);
		if (program == 0)
			throw new RuntimeException("The 'simple_tex' shader program could not be loaded!");
		
		// initialize the object's geometry
		vertexBuffer = BufferUtils.asBuffer(staticVertexArray);
		vertexIndexBuffer = BufferUtils.asBuffer(staticIndexArray);
		
		// load the menu background as a texture
		// texture = TextureLoader.loadTextureFromAsset("menu_background.png");
		texture = TextureLoader.loadTextureFromResource(R.drawable.background);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		
		textureCoordsBuf = BufferUtils.asBuffer(staticTextureCoords);
		
		if (texture == 0) 
			throw new RuntimeException("Unable to load the background texture!");
	}
	
	
	@Override
	public void draw() {
		// update the object's model matrix
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.scaleM(modelMatrix, 0, width, height, 1);
		Matrix.translateM(modelMatrix, 0, position.getX(), position.getY(), position.getZ());
		
		GLES20.glUseProgram(program);
		
		// get shader attributes' locations
		int a_position = GLES20.glGetAttribLocation(program, "a_position");
		int a_textureCoords = GLES20.glGetAttribLocation(program, "a_textureCoords");
		
		// get shader uniforms' locations
		int u_texture = GLES20.glGetUniformLocation(program, "u_texture");
		int u_modelMatrix = GLES20.glGetUniformLocation(program, "u_modelMatrix");
		int u_color = GLES20.glGetUniformLocation(program, "u_color");
		
		// send the matrices
		GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, modelMatrix, 0);
		
		// send the vertex data to the shader
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false, 
				3 * 4 /* bytes */, vertexBuffer);
		
		// send texture and color data (use the first texture unit)
		GLES20.glUniform4fv(u_color, 1, staticColor, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(u_texture, 0);
		
		// send the texture coords
		textureCoordsBuf.position(0);
		GLES20.glVertexAttribPointer(a_textureCoords, 2 /* coords */, GLES20.GL_FLOAT, false, 0, textureCoordsBuf);
		GLES20.glEnableVertexAttribArray(a_textureCoords);
		
		// draw!
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, staticIndexArray.length, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);
	}
	
}
