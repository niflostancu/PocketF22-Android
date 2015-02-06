package ro.pub.dadgm.pf22.render.objects.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ro.pub.dadgm.pf22.game.models.Terrain;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.AbstractObject3D;
import ro.pub.dadgm.pf22.render.utils.BufferUtils;
import ro.pub.dadgm.pf22.render.utils.NormalUtils;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;
import ro.pub.dadgm.pf22.render.views.GameScene;


/**
 * Draws the 3D terrain.
 */
public class Terrain3D extends AbstractObject3D {
	
	/**
	 * Defines the asset path to the terrain textures.
	 */
	protected final String TEXTURE_PATH = "textures/";
	
	/**
	 * Defines a terrain parcel that has a specific texture.
	 */
	protected class TerrainParcel {
		
		/**
		 * The type identifier of the parcel (index of {@link Terrain#TERRAIN_TYPES}).
		 */
		byte type;
		
		// work arrays
		
		/**
		 * Stores the texture coordinates.
		 */
		float[] textureCoords;
		
		/**
		 * The list of triangles (vertex indices).
		 */
		ArrayList<Integer> triangles;
		
		/**
		 * Stores the triangle indices count (aka the size of the index buffer).
		 */
		int indexCount;
		
		/**
		 * Texture coordinates buffer (for all vertices).
		 */
		FloatBuffer textureCoordsBuf;
		
		/**
		 * Parcel's triangles.
		 */
		ShortBuffer vertexIndicesBuf;
		
		/**
		 * Stores the loaded texture resource for the parcel.
		 */
		int texture;
		
		/**
		 * Initializes a new parcel.
		 * 
		 * @param type Terrain type ID.
		 */
		public TerrainParcel(byte type) {
			this.type = type;
			
			// initialize the arrays / buffers
			textureCoords = new float[Terrain3D.this.vertexCount * 2];
			triangles = new ArrayList<>();
		}
		
		/**
		 * Adds the specified triangle to the list.
		 * 
		 * @param v1 The first vertex.
		 * @param v2 Second vertex.
		 * @param v3 Last vertex.
		 */
		public void addTriangle(int v1, int v2, int v3) {
			triangles.add(v1);
			triangles.add(v2);
			triangles.add(v3);
		}
		
		/**
		 * Sets the specified texture coordinate entry.
		 * 
		 * @param v The target vertex.
		 * @param tx The X texture coordinate.
		 * @param ty The Y texture coordinate.
		 */
		public void addTextureCoordinate(int v, float tx, float ty) {
			textureCoords[2*v] = tx;
			textureCoords[2*v + 1] = ty;
		}
		
		/**
		 * Loads the buffers/textures and cleans up the temporary data used.
		 */
		public void load() {
			textureCoordsBuf = BufferUtils.asBuffer(textureCoords);
			textureCoords = null;
			
			vertexIndicesBuf = BufferUtils.allocateShortBuffer(triangles.size());
			for (Integer v: triangles)
				vertexIndicesBuf.put((short)((int)v));
			vertexIndicesBuf.position(0);
			indexCount = triangles.size();
			triangles = null;
			
			Object[] terrainInfo = Terrain.TERRAIN_TYPES[type];
			String textureFile = (String)terrainInfo[1];
			if (textureFile != null) {
				texture = TextureLoader.loadTextureFromAsset(TEXTURE_PATH + textureFile);
				if (texture == 0)
					throw new RuntimeException("Unable to load texture file '" + textureFile + "'!");
				
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST_MIPMAP_LINEAR);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			}
		}
	}
	
	/**
	 * Reference to the terrain's model object.
	 */
	protected Terrain terrain;
	
	/**
	 * The total number of vertices.
	 */
	protected int vertexCount;
	
	/**
	 * Computes normals buffer.
	 */
	protected FloatBuffer normalsBuf;
	
	/**
	 * The different parcels that make up the terrain.
	 * 
	 * <p>The key is the index of the type in {@link Terrain#TERRAIN_TYPES}.</p>
	 */
	protected TerrainParcel[] parcels;
	
	
	/**
	 * Initializes the terrain 3D object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public Terrain3D(Scene3D scene, Terrain terrain, String tag, int priority) {
		super(scene, tag, priority);
		
		this.terrain = terrain;
		
		// get shader program
		shader = scene.getShaderManager().getShader("s3d_tex_phong");
		
		// generate the terrain from the model object's height map
		parcels = new TerrainParcel[Terrain.TERRAIN_TYPES.length];
		generateTerrain3D();
	}
	
	/**
	 * Generates the terrain's vertices, polygons, texture coordinates and normals.
	 */
	protected void generateTerrain3D() {
		int[] dims = terrain.getDimensions();
		float[][] heightMap = terrain.getHeightMap();
		byte[][] typeMap = terrain.getTypeMap();
		
		vertexCount = (dims[0] * dims[1]);
		
		// allocate the working buffers
		float[] vertexArr = new float[vertexCount * 3];
		float[] normalsArr = new float[vertexCount * 3];
		
		for (int i=0; i<dims[0]; i++) {
			for (int j=0; j<dims[1]; j++) {
				int v = (i * dims[1] + j); // the current vertex
				
				// fill in the vertex array
				vertexArr[3*v] = i; // x
				vertexArr[3*v+1] = j; // y
				vertexArr[3*v+2] = heightMap[i][j]; // z
				
				// compute the triangles
				byte t = typeMap[i][j];
				if (parcels[t] == null) {
					parcels[t] = new TerrainParcel(t);
				}
				TerrainParcel parcel = parcels[t];
				
				if ((i < dims[0]-1) && (j < dims[1]-1)) {
					int v2 = ((i+1)*dims[1] + j); // bottom-left
					int v3 = ((i+1)*dims[1] + j+1); // bottom-right
					int v4 = (i*dims[1] + j+1); // top-right
					
					parcel.addTriangle(v, v2, v3);
					parcel.addTriangle(v, v3, v4);
					
					// compute texture coordinates for the triangle's vertices
					final float texScale = 1/3f;
					parcel.addTextureCoordinate(v, j*texScale, i*texScale);
					parcel.addTextureCoordinate(v2, j*texScale, (i+1f)*texScale);
					parcel.addTextureCoordinate(v3, (j+1f)*texScale, (i+1f)*texScale);
					parcel.addTextureCoordinate(v4, (j+1f)*texScale, i*texScale);
				}
			}
		}
		
		// calculate the normals
		for (int i=0; i<dims[0]; i++) {
			for (int j = 0; j < dims[1]; j++) {
				int v = (i * dims[1] + j); // current vertex
				
				// there can be up to 6 neighbouring triangles to the current vertex
				float[] tmp = new float[3];
				int v1, v2, v3, v4; // the 4 vertices of a quad
				
				if (i > 0 && j > 0) { // upper-left quad
					v1 = ((i-1)*dims[1] + j-1);
					v2 = ((i)*dims[1] + j-1);
					v3 = v; // current vertex is the third in the quad
					v4 = ((i-1)*dims[1] + j);
					// The 3-1-2 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v3, vertexArr, 3*v1, vertexArr, 3*v2);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
					// The 3-4-1 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v3, vertexArr, 3*v4, vertexArr, 3*v1);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
				}
				
				if (i > 0 && j < (dims[1]-1)) { // upper-right quad
					v1 = ((i-1)*dims[1] + j);
					v2 = v; // current vertex is the second in the quad
					v3 = ((i)*dims[1] + j+1);
					// v4 = 0; // not important
					// The 2-3-1 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v2, vertexArr, 3*v3, vertexArr, 3*v1);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
				}
				
				if (i < (dims[0]-1) && j > 0) { // lower-left quad
					v1 = ((i)*dims[1] + j-1);
					// v2 = 0; // not important
					v3 = ((i+1)*dims[1] + j);
					v4 = v; // current vertex is the last in the quad
					// The 4-1-3 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v4, vertexArr, 3*v1, vertexArr, 3*v3);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
				}
				
				if (i < (dims[0]-1) && j < (dims[1]-1)) { // upper-right quad
					v1 = v; // current vertex is the third in the quad
					v2 = ((i+1)*dims[1] + j);
					v3 = ((i+1)*dims[1] + j+1);
					v4 = ((i)*dims[1] + j+1);
					// The 1-2-3 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v1, vertexArr, 3*v2, vertexArr, 3*v3);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
					// The 1-3-4 triangle
					NormalUtils.computeNormal(tmp, 0, vertexArr, 3*v1, vertexArr, 3*v3, vertexArr, 3*v4);
					normalsArr[3*v] += tmp[0];
					normalsArr[3*v+1] += tmp[1];
					normalsArr[3*v+2] += tmp[2];
				}
				
				// average all the normals
				NormalUtils.normalize(normalsArr, 3*v);
			}
		}
		
		// load the generated data
		for (TerrainParcel parcel : parcels) {
			if (parcel != null) {
				parcel.load();
			}
		}
		vertexBuffer = BufferUtils.asBuffer(vertexArr);
		normalsBuf = BufferUtils.asBuffer(normalsArr);
	}
	
	
	@Override
	public void draw() {
		Matrix.setIdentityM(modelMatrix, 0);
		
		final float[] lightPosition = GameScene.LIGHT_POSITION;
		final float[] ambientColor = new float[] { 0.3f, 0.3f, 0.3f } ;
		final float[] diffuseColor = new float[] { 1.0f, 1.0f, 1.0f } ;
		final float[] specularColor = new float[] { 0.5f, 0.5f, 0.5f } ;
		
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
				3 * 4 /* bytes */, vertexBuffer);
		
		GLES20.glEnableVertexAttribArray(a_normal);
		GLES20.glVertexAttribPointer(a_normal, 3 /* coords */, GLES20.GL_FLOAT, false,
				3 * 4 /* bytes */, normalsBuf);
		
		GLES20.glUniform3fv(u_lightPos, 1, lightPosition, 0);
		
		for (TerrainParcel parcel: parcels) {
			if (parcel == null) continue;
			
			if (parcel.texture > 0) {
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, parcel.texture);
				GLES20.glUniform1i(u_texture, 0);
				GLES20.glUniform1i(u_textureEnable, 1);
				
				GLES20.glVertexAttribPointer(a_textureCoords, 2, GLES20.GL_FLOAT, false,
						0, parcel.textureCoordsBuf);
				GLES20.glEnableVertexAttribArray(a_textureCoords);
				
			} else {
				// disable texture
				GLES20.glUniform1i(u_textureEnable, 0);
			}
			
			// set up colors and textures
			GLES20.glUniform3fv(u_ambientColor, 1, ambientColor, 0);
			GLES20.glUniform3fv(u_diffuseColor, 1, diffuseColor, 0);
			GLES20.glUniform3fv(u_specularColor, 1, specularColor, 0);
			GLES20.glUniform1f(u_alpha, 1.0f);
			GLES20.glUniform1f(u_shininess, 8.0f);
			
			// draw!
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, parcel.indexCount, GLES20.GL_UNSIGNED_SHORT, parcel.vertexIndicesBuf);
		}
	}
	
}
