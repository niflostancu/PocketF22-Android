package ro.pub.dadgm.pf22.render.objects.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;

import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.game.models.Plane;
import ro.pub.dadgm.pf22.game.models.PrimaryPlane;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.AbstractObject3D;
import ro.pub.dadgm.pf22.render.utils.objloader.Material;
import ro.pub.dadgm.pf22.render.utils.objloader.OBJParser;
import ro.pub.dadgm.pf22.render.utils.objloader.TDModel;
import ro.pub.dadgm.pf22.render.utils.objloader.TDModelPart;
import ro.pub.dadgm.pf22.render.views.GameScene;
import ro.pub.dadgm.pf22.utils.Point3D;

/**
 * Implements a 3D fighter jet model.
 */
public class FighterJet3D extends AbstractObject3D {
	
	/**
	 * The asset path of the model's resources.
	 */
	protected final String MODEL_PATH = "objects/f22_raptor/"; 
	
	/**
	 * The fighter jet's model object.
	 */
	protected TDModel modelObj;
	
	/**
	 * The plane model object.
	 */
	protected Plane plane;
	
	/**
	 * Initializes the fighter jet 3D object.
	 *  @param scene The parent scene object.
	 * @param plane The plane's model object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public FighterJet3D(Scene3D scene, PrimaryPlane plane, String tag, int priority) {
		super(scene, tag, priority);
		
		this.plane = plane;
		
		// get shader program
		shader = scene.getShaderManager().getShader("s3d_tex_phong");
		
		// load the object's assets
		OBJParser parser = new OBJParser();
		InputStream modelStream, materialStream;
		try {
			modelStream = MainActivity.getAppContext().getAssets().open(MODEL_PATH + "model.obj");
			materialStream = MainActivity.getAppContext().getAssets().open(MODEL_PATH + "materials.mtl");
			
		} catch (IOException e) {
			throw new RuntimeException("Unable to read fighter jet model file!", e);
		}
		
		modelObj = parser.parseOBJ(modelStream, materialStream);
	}
	
	@Override
	public void draw() {
		Point3D position = plane.getPosition();
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, position.getX(), position.getY(), position.getZ());
		Matrix.scaleM(modelMatrix, 0, 0.3f, 0.3f, 0.3f);
		// Matrix.rotateM(modelMatrix, 0, -90, 1, 0, 0);
		
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
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false,
				3 * 4 /* bytes */, modelObj.getVertexBuf());
		
		GLES20.glEnableVertexAttribArray(a_normal);
		GLES20.glVertexAttribPointer(a_normal, 3 /* coords */, GLES20.GL_FLOAT, false,
				3 * 4 /* bytes */, modelObj.getNormalBuf());
		
		GLES20.glUniform3fv(u_lightPos, 1, lightPosition, 0);
		
		// send the faces (parts)
		for (TDModelPart part: modelObj.getParts()) {
			// load the texture
			Material mat = part.getMaterial();
			int texture = mat.loadTexture(MODEL_PATH);
			
			if (texture > 0) {
				GLES20.glEnableVertexAttribArray(a_textureCoords);
				GLES20.glVertexAttribPointer(a_textureCoords, 2, GLES20.GL_FLOAT, false,
						0, modelObj.getTextureCoordsBuf(mat));
				
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
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, part.getFacesCount(), GLES20.GL_UNSIGNED_SHORT, part.getFaceBuffer());
		}
	}
	
}
