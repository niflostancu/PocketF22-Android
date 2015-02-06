package ro.pub.dadgm.pf22.render.objects.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.AbstractObject3D;

/**
 * Implements an OpenGL sky box.
 */
public class SkyBox extends AbstractObject3D {
	
	/**
	 * The asset path of the model's resources.
	 */
	protected final String TEXTURE_PATH = "textures/cubemap/";

	/**
	 * The cubemap texture.
	 */
	protected int texture;
	
	/**
	 * Initializes the fighter jet 3D object.
	 *  @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public SkyBox(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		
		// get shader program
		shader = scene.getShaderManager().getShader("s3d_tex_phong");
		
		// load the textures
		texture = 0;
	}
	
	@Override
	public void draw() {
		Matrix.setIdentityM(modelMatrix, 0);
		// Matrix.translateM(modelMatrix, 0, 0, 0, 0);
		// Matrix.scaleM(modelMatrix, 0, 0.3f, 0.3f, 0.3f);
		
		shader.use();
		
		// get shader attributes' locations
		int a_position = shader.getAttribLocation("a_position");
		int a_textureCoords = shader.getAttribLocation("a_textureCoords");
		
		// get shader uniforms' locations
		int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
		
		int u_texture = shader.getUniformLocation("u_texture");
		
		// send the matrices
		GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, modelMatrix, 0);
		
		// send the vertex data to the shader
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false,
				3 * 4 /* bytes */, vertexBuffer);
		
		GLES20.glEnableVertexAttribArray(a_textureCoords);
		GLES20.glVertexAttribPointer(a_textureCoords, 2, GLES20.GL_FLOAT, false,
				0, vertexBuffer);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(u_texture, 0);
		
		// draw!
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, /*FIXME: COUNT HERE*/0, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);
	}
	
}
