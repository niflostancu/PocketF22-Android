package ro.pub.dadgm.pf22.render.objects.hud;

import android.opengl.GLES20;
import android.opengl.Matrix;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.BufferUtils;

/**
 * Draws a semitransparent menu overlay image (quad filled with a color).
 * 
 * <p>This object's dimensions must be equal to that of the scene's.</p>
 */
public class MenuOverlay extends HUDObject {
	
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
	 * Static semitransparent color for the overlay.
	 */
	protected static final float[] staticColor = {
		0.0f, 0.0f, 0.0f, 0.7f
	};
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public MenuOverlay(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		
		// load the GLSL program
		shader = scene.getShaderManager().getShader("simple_color");
		
		// initialize the object's geometry
		vertexBuffer = BufferUtils.asBuffer(staticVertexArray);
		vertexIndexBuffer = BufferUtils.asBuffer(staticIndexArray);
	}
	
	
	@Override
	public void draw() {
		if (!visibility) return;
		
		// update the object's model matrix
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.getX(), position.getY(), position.getZ());
		Matrix.scaleM(modelMatrix, 0, width, height, 1);
		
		shader.use();
		
		// get shader attributes' locations
		int a_position = shader.getAttribLocation("a_position");
		
		// get shader uniforms' locations
		int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
		int u_color = shader.getUniformLocation("u_color");
		
		// send the matrices
		GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, modelMatrix, 0);
		
		// send the vertex data to the shader
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false, 
				3 * 4 /* bytes */, vertexBuffer);
		
		// send color data
		GLES20.glUniform4fv(u_color, 1, staticColor, 0);
		
		// draw!
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, staticIndexArray.length, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);
	}
	
}
