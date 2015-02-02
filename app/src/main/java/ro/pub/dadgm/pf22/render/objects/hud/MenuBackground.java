package ro.pub.dadgm.pf22.render.objects.hud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.BufferUtils;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;

/**
 * Draws a background image (quad filled with a texture).
 * 
 * <p>This object's dimensions must be equal to that of the scene's and the image will be zoomed 
 * across them.</p>
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
		0f, 1f, 0, // top left
		0f, 0f, 0, // bottom left
		1f, 0f, 0, // bottom right
		1f, 1f, 0  // top right
	};
	
	/**
	 * Object's triangles (vertex indices array).
	 */
	protected static final short[] staticIndexArray = {
		0, 1, 2, 0, 2, 3 // 2 triangles that form a quad
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
	 * Texture's width/height ratio.
	 */
	protected float textureRatio;
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public MenuBackground(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		
		// load the GLSL program
		shader = scene.getShaderManager().getShader("simple_tex");
		
		// initialize the object's geometry
		vertexBuffer = BufferUtils.asBuffer(staticVertexArray);
		vertexIndexBuffer = BufferUtils.asBuffer(staticIndexArray);
		
		// load the menu background as a texture
		// disable pre-scaling
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getAppContext().getResources(), R.drawable.background, options);
		if (bitmap == null) 
			throw new RuntimeException("Unable to load the background image!");
		
		texture = TextureLoader.loadTexture(bitmap);
		textureRatio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
		bitmap.recycle();
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		
		textureCoordsBuf = BufferUtils.allocateFloatBuffer(4 * 2);
		
		if (texture == 0) 
			throw new RuntimeException("Unable to load the background texture!");
	}
	
	
	@Override
	public void draw() {
		// update the object's model matrix
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.getX(), position.getY(), position.getZ());
		Matrix.scaleM(modelMatrix, 0, width, height, 1);
		
		shader.use();
		
		// get shader attributes' locations
		int a_position = shader.getAttribLocation("a_position");
		int a_textureCoords = shader.getAttribLocation("a_textureCoords");
		
		// get shader uniforms' locations
		int u_texture = shader.getUniformLocation("u_texture");
		int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
		int u_color = shader.getUniformLocation("u_color");
		
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
		
		// zoom the image, centered by X
		float screenRatio = width / height;
		float dx = Math.abs( textureRatio - screenRatio ) / 2.0f / textureRatio;
		float[] textureCoords = {
				dx, 0f, // top left
				dx, 1f, // bottom left
				1.0f - dx, 1f, // bottom right
				1.0f - dx, 0f  // top right
		};
		
		// send the texture coords
		textureCoordsBuf.put(textureCoords);
		textureCoordsBuf.flip();
		GLES20.glVertexAttribPointer(a_textureCoords, 2 /* coords */, GLES20.GL_FLOAT, false, 0, textureCoordsBuf);
		GLES20.glEnableVertexAttribArray(a_textureCoords);
		textureCoordsBuf.clear();
		
		// draw!
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, staticIndexArray.length, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);
	}
	
}
