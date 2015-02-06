package ro.pub.dadgm.pf22.render.objects.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;

import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.game.models.Projectile;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.AbstractObject3D;
import ro.pub.dadgm.pf22.render.utils.objloader.Material;
import ro.pub.dadgm.pf22.render.utils.objloader.OBJParser;
import ro.pub.dadgm.pf22.render.utils.objloader.TDModel;
import ro.pub.dadgm.pf22.render.utils.objloader.TDModelPart;
import ro.pub.dadgm.pf22.render.views.GameScene;

/**
 * Implements a 3D rocket projectile model.
 */
public class RocketProjectile3D extends AbstractObject3D {
	
	/**
	 * The asset path of the model's resources.
	 */
	protected final static String MODEL_PATH = "objects/rocket/";
	
	/**
	 * The fighter jet's model object.
	 */
	protected static TDModel modelObj = null;
	
	/**
	 * The plane model object.
	 */
	protected Projectile projectile;
	
	/**
	 * Initializes the rocket 3D object.
	 * 
	 *  @param scene The parent scene object.
	 * @param projectile The projectile's model object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public RocketProjectile3D(Scene3D scene, Projectile projectile, String tag, int priority) {
		super(scene, tag, priority);
		
		this.projectile = projectile;
		
		// get shader program
		shader = scene.getShaderManager().getShader("s3d_tex_phong");
		
		// load the object's assets
		OBJParser parser = new OBJParser();
		InputStream modelStream/*, materialStream*/;
		try {
			modelStream = MainActivity.getAppContext().getAssets().open(MODEL_PATH + "model.obj");
			// materialStream = MainActivity.getAppContext().getAssets().open(MODEL_PATH + "materials.mtl");
			
		} catch (IOException e) {
			throw new RuntimeException("Unable to read fighter rocket model file!", e);
		}
		if (modelObj == null)
			modelObj = parser.parseOBJ(modelStream, null);
	}
	
	@Override
	public void draw() {
		float[] position = projectile.getPosition().toArray();
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2]);
		Matrix.scaleM(modelMatrix, 0, 1 / 100000f, 1 / 100000f, 1 / 100000f);
		// Matrix.rotateM(modelMatrix, 0, -90, 0, 0, 1);
		
		float[] lightPosition = GameScene.LIGHT_POSITION;
		float[] normalMatrix = scene.getCamera().computeNormalMatrix(modelMatrix);
		
		shader.use();
		
		// get shader attributes' locations
		int a_position = shader.getAttribLocation("a_position");
		int a_normal = shader.getAttribLocation("a_normal");
		int a_textureCoords = shader.getAttribLocation("a_textureCoords");
		
		// get shader uniforms' locations
		int u_normalMatrix = shader.getUniformLocation("u_normalMatrix");
		int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
		int u_lightPos = shader.getUniformLocation("u_lightPos");
		
		int u_texture = shader.getUniformLocation("u_texture");
		int u_textureEnable = shader.getUniformLocation("u_textureEnable");
		
		int u_ambientColor = shader.getUniformLocation("u_ambientColor");
		int u_diffuseColor = shader.getUniformLocation("u_diffuseColor");
		int u_specularColor = shader.getUniformLocation("u_specularColor");
		int u_alpha = shader.getUniformLocation("u_alpha");
		int u_shininess = shader.getUniformLocation("u_shininess");
		
		// send the matrices
		GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(u_normalMatrix, 1, false, normalMatrix, 0);
		
		// send the vertex data to the shader
		int vbo = modelObj.getVBO();
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false,
				4 * 4 * 2 /* bytes */, 0);
		GLES20.glVertexAttribPointer(a_normal, 3 /* coords */, GLES20.GL_FLOAT, false,
				4 * 4 * 2 /* bytes */, 4 * 4);
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glEnableVertexAttribArray(a_normal);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		GLES20.glUniform3fv(u_lightPos, 1, lightPosition, 0);
		
		// send the faces (parts)
		for (TDModelPart part: modelObj.getParts()) {
			// load the texture
			Material mat = new Material("rocket");
			mat.setAmbientColor(0.3f, 0.3f, 0.3f);
			mat.setDiffuseColor(1, 1, 1);
			mat.setSpecularColor(0.7f, 0.7f, 0.7f);
			
			int texture = mat.loadTexture(MODEL_PATH);
			
			if (texture > 0) {
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, modelObj.getTextureCoordsBuf(mat));
				GLES20.glVertexAttribPointer(a_textureCoords, 2, GLES20.GL_FLOAT, false,
						0 /* bytes */, 0);
				GLES20.glEnableVertexAttribArray(a_textureCoords);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
				GLES20.glUniform1i(u_texture, 0);
				GLES20.glUniform1i(u_textureEnable, 1);
				
			} else {
				// disable texture
				GLES20.glUniform1i(u_textureEnable, 0);
			}
			
			// set up colors and textures
			GLES20.glUniform3fv(u_ambientColor, 1, mat.getAmbientColor(), 0);
			GLES20.glUniform3fv(u_diffuseColor, 1, mat.getDiffuseColor(), 0);
			GLES20.glUniform3fv(u_specularColor, 1, mat.getSpecularColor(), 0);
			GLES20.glUniform1f(u_alpha, mat.getAlpha());
			GLES20.glUniform1f(u_shininess, mat.getShine());
			
			// draw!
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, part.getIBO());
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, part.getFacesCount(), GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
	}
	
}
