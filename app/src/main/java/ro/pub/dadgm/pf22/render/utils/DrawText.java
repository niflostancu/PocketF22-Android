package ro.pub.dadgm.pf22.render.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.Shader;
import ro.pub.dadgm.pf22.utils.Point3D;

/**
 * Utility OpenGL text drawing library.
 * 
 * <p>Inspired by https://github.com/d3kod/Texample2.</p>
 * 
 * <p>It uses a font map as a texture to draw the text strings using OpenGL functions.</p>
 * 
 * <p>Note: The shader used to draw the text should abide by the following conventions:
 * <ul>
 *     <li>shader's identifier is 'draw_text'.</li>
 *     <li>the vertex shader accepts the a_position and a_textureCoords attributes;</li>
 *     <li>the following uniforms are used: u_texture (for passing the character's texture), 
 *     u_modelMatrix, u_viewMatrix and u_projectionMatrix (for defining the MVP matrix);</li>
 * </ul>
 * </p>
 */
public class DrawText {
	
	// several constants
	
	// the ascii code of the first character in map's interval
	public static final char CHAR_START = 32; // ' ' (space) character
	
	// the last character to generate map for
	public static final char CHAR_END = 126; // '~'
	
	// and the characters count
	public final char CHARS = (CHAR_END - CHAR_START + 1);
	
	/**
	 * The length (in "printed characters") of the internal buffer used to draw.
	 * 
	 * <p>The higher, the better the performance on drawing large texts, but beware of the GPU memory 
	 * consumption!</p>
	 */
	public final int DRAW_BUFFERS_LENGTH = 20;
	
	/**
	 * Texture character padding to use.
	 */
	public final int PAD_X = 2, PAD_Y = 2;
	
	
	/**
	 * Font alignment enum.
	 */
	public static enum FontAlign {
		ALIGN_LEFT,
		ALIGN_CENTER,
		ALIGN_RIGHT
	}
	
	/**
	 * Represents the font map (font as an OpenGL texture) for a specific font and size combination.
	 * 
	 * <p>Its constructor generates the full texture data for the given parameters and the object 
	 * provides several getters for retrieving the generated texture / several font metrics.</p>
	 */
	protected class GLFont {
		
		/**
		 * The name of the represented font.
		 */
		private String font;
		
		// font metrics: 
		
		/**
		 * The size of the represented font.
		 */
		private int fontSize;
		
		/**
		 * Stores the font's descent value (recommended distance from baseline to draw).
		 */
		private float fontDescent;
		
		/**
		 * Stores the maximum height of the font (in pixels).
		 */
		private float maxHeight;
		
		/**
		 * Stores the maximum width of a character.
		 */
		private float maxWidth;
		
		/**
		 * An array with the width (pixels) of each character.
		 */
		private float[] charWidths;
		
		
		// texture coordinates
		
		/**
		 * The width and height of a texture cell (which contains a 2D-rendered character).
		 */
		private int texCellWidth, texCellHeight;
		
		/**
		 * Stores the number of rows and columns of texture cells.
		 */
		private int texRows, texCols;
		
		/**
		 * The chosen size of the texture.
		 */
		private int textureSize;
		
		/**
		 * The generated OpenGL texture handle.
		 */
		private int textureId;
		
		
		/**
		 * Constructs a GLFont instance with the specified parameters.
		 * 
		 * @param font The represented font's name / path.
		 * @param fontSize The font size.
		 */
		public GLFont(String font, int fontSize) {
			this.font = font;
			this.fontSize = fontSize;

			charWidths = new float[CHARS];

			generateTexture();
		}
		
		/**
		 * Determines the font metrics and generates the texture.
		 */
		public void generateTexture() {
			// use a Paint instance to draw the 2D characters of the current font
			Typeface tf = Typeface.createFromAsset(MainActivity.getAppContext().getAssets(), font);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(fontSize);
			paint.setColor(0xffffffff);
			paint.setTypeface(tf);
			
			// get font's metrics
			Paint.FontMetrics fm = paint.getFontMetrics();
			maxHeight = (float)Math.ceil( Math.abs(fm.bottom) + Math.abs(fm.top) );
			fontDescent = (float)Math.ceil( Math.abs(fm.descent) );
			
			// determine the width of each character
			char[] s = new char[2]; // character string to draw
			float[] w = new float[2]; // output widths array
			int cnt = 0; // character counter
			for (char c = CHAR_START; c <= CHAR_END; c++)  {
				s[0] = c;
				paint.getTextWidths( s, 0, 1, w ); // get the character's width
				charWidths[cnt] = w[0];
				if ( charWidths[cnt] > maxWidth )
					maxWidth = charWidths[cnt];
				
				cnt++;
			}
			
			// determine texture cell size
			texCellWidth = (int)maxWidth + (2 * PAD_X);
			texCellHeight = (int)maxHeight + (2 * PAD_Y);
			
			// compute the size of the texture that needs to be allocated
			int[] textureSizes = { 256, 512, 1024, 2048 };
			textureSize = 0;
			for (int size: textureSizes) {
				texCols = size / texCellWidth;
				texRows = size / texCellHeight;
				if (texRows * texCols >= CHARS) {
					// found the optimal texture size!
					textureSize = size;
					break;
				}
			}
			if (textureSize == 0)
				throw new RuntimeException("The font map doesn't fit inside a OpenGL texture! " +
						"Try with a smaller font size.");
			
			// create the texture bitmap
			Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bitmap);
			bitmap.eraseColor( Color.TRANSPARENT ); // full transparency
			
			// build the font map!
			for (char c = CHAR_START; c <= CHAR_END; c++)  {
				s[0] = c;
				float[] coords = getCharCoords(c);
				canvas.drawText( s, 0, 1, 
						coords[0] + PAD_X, // add X padding
						coords[1] + texCellHeight-1 - fontDescent - PAD_Y, // use fontDescent from baseline
						paint );
			}
			
			// transform the generated bitmap in a texture
			textureId = TextureLoader.loadTexture(bitmap);
			if (textureId == 0)
				throw new RuntimeException("Unable to allocate texture for the font map!");
			
			// set texture properties
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			
			bitmap.recycle(); // free bitmap memory
		}

		/**
		 * Destroys all allocated resources (textures) used by this font.
		 */
		public void destroy() {
			TextureLoader.unloadTexture(textureId);
		}
		
		/**
		 * Getter for the allocated texture size.
		 * 
		 * @return The texture size that needs to be allocated.
		 */
		public int getTextureSize() {
			return textureSize;
		}
		
		/**
		 * Getter for the texture's GL handle.
		 * 
		 *  @return Generated texture's handle.
		 */
		public int getTextureId() {
			return textureId;
		}
		
		/**
		 * Returns the texture start coodinates of a specific character.
		 * 
		 * @param c The character to retrieve the texture coordinates for.
		 * @return The 2D texture coordinates as an [x, y] float array.
		 */
		public float[] getCharCoords(char c) {
			char idx = (char)(c - CHAR_START);
			if (idx < 0 || idx >= charWidths.length) {
				idx = 0;
			}
			
			int i = (int)Math.floor(idx / texCols); // the line that contains the cell
			int j = idx - texCols * i; // the column that contains the cell
			
			return new float[] { j * texCellWidth, i * texCellHeight };
		}
		
		/**
		 * Returns the width of the specified character.
		 * 
		 * @param c The character to retrieve the width for.
		 * @return The character's width.
		 */
		public float getCharWidth(char c) {
			char idx = (char)(c - CHAR_START);
			if (idx < 0 || idx >= charWidths.length) {
				idx = 0;
			}
			return charWidths[idx];
		}
	}
	
	
	/**
	 * The parent scene.
	 */
	protected Scene3D scene;
	
	/**
	 * The compiled (with textures generated) fonts cache.
	 * 
	 * <p>The key has the form "{fontFile}_{size}".</p> 
	 */
	protected Map<String, GLFont> fontCache;
	
	/**
	 * The current font to use for rendering text.
	 */
	protected GLFont currentFont;
	
	/**
	 * Stores the working model matrix used for positioning the text.
	 * 
	 * <p>Defaults to the identity matrix.</p>
	 */
	protected float[] workModelMatrix = new float[16];
	
	/**
	 * The work vertex buffer used to draw text.
	 */
	protected FloatBuffer workVertexBuf;
	
	/**
	 * The indices buffer used for drawing.
	 */
	protected ShortBuffer workIndexBuf;
	
	/**
	 * A temporary texture coordinates buffer used to map texture to the vertices.
	 */
	protected FloatBuffer workTextureCoordsBuf;
	
	/**
	 * The current font color.
	 */
	protected float[] currentColor;
	
	/**
	 * The X letter spacing (in texture pixels). 
	 */
	protected float spaceX;
	
	/**
	 * The 3D position to begin drawing from.
	 */
	protected Point3D startPosition;
	
	/**
	 * The scale to use for drawing.
	 */
	protected float scale;
	
	/**
	 * The font alignment to use for rendering.
	 */
	protected FontAlign alignment;
	
	
	/**
	 * Initializes the DrawText library.
	 * 
	 * @param scene The parent scene that provides drawing services.
	 */
	public DrawText(Scene3D scene) {
		final int NUM_VERTEX_COORDINATES = 3; // use 3D coordinates
		final int NUM_VERTICES_CHAR = 4; // unique vertices per character
		final int NUM_TRIANGLES_CHAR = 2; // triangles per character
		
		this.scene = scene;
		this.fontCache = new HashMap<>();
		Matrix.setIdentityM(workModelMatrix, 0);
		
		currentColor = new float[4];
		startPosition = new Point3D();
		
		// allocate the internal buffers
		// we have 4 unique vertices for each character
		workVertexBuf = BufferUtils.allocateFloatBuffer(DRAW_BUFFERS_LENGTH * 
				NUM_VERTEX_COORDINATES * NUM_VERTICES_CHAR);
		workTextureCoordsBuf = BufferUtils.allocateFloatBuffer(DRAW_BUFFERS_LENGTH *
				NUM_VERTEX_COORDINATES * NUM_VERTICES_CHAR);
		// that form a quad
		workIndexBuf = BufferUtils.allocateShortBuffer(DRAW_BUFFERS_LENGTH *
				NUM_TRIANGLES_CHAR * 3); /* each triangle has 3 vertices */
		
		currentFont = null;
		reset();
	}
	
	/**
	 * Destroys the instance, freeing all allocated resources (i.e. textures).
	 */
	public void destroy() {
		for (Map.Entry<String, GLFont> fontEntry: fontCache.entrySet()) {
			fontEntry.getValue().destroy();
		}
		fontCache.clear();
	}
	
	/**
	 * Changes the current font to the specified one.
	 * 
	 * <p>Beware that if the specified font and size combination was never used before, a new texture 
	 * needs to be generated (which can take some time). Make sure to call this at least once during 
	 * the init process of the view that uses it.</p>
	 * 
	 * @param fontFile The path to the font asset.
	 * @param size Font size to use.
	 */
	public void useFont(String fontFile, int size) {
		String key = fontFile + "_" + size;
		if (fontCache.containsKey(key)) {
			currentFont = fontCache.get(key);
			
		} else {
			// we need to generate it
			GLFont glFont = new GLFont(fontFile, size);
			currentFont = glFont;
			fontCache.put(key, glFont);
		}
	}
	
	/**
	 * Changes the draw color.
	 * 
	 * @param color The new color to set (RGBA).
	 */
	public void setColor(float[] color) {
		System.arraycopy(color, 0, currentColor, 0, 4);
	}
	
	/**
	 * Sets the letter X spacing.
	 * 
	 * @param spaceX The X spacing of the letter, in tex pixels.
	 */
	@SuppressWarnings("unused")
	public void setSpaceX(float spaceX) {
		this.spaceX = spaceX;
	}

	/**
	 * Sets the starting position of the text.
	 *
	 * @param position The new position to set.
	 */
	public void setStartPosition(Point3D position) {
		this.startPosition.setCoordinates(position.getX(), position.getY(), position.getZ());
	}
	
	/**
	 * Sets the starting position of the text.
	 * 
	 * @param x The X coordinate to set.
	 * @param y The Y coordinate to set.
	 * @param z The Z coordinate to set.
	 */
	public void setStartPosition(float x, float y, float z) {
		this.startPosition.setCoordinates(x, y, z);
	}
	
	/**
	 * Sets the uniform scaling factor to use when drawing.
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	/**
	 * Changes font alignment to use for drawing.
	 * 
	 * @param alignment The font alignment to set.
	 */
	public void setAlignment(FontAlign alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * Resets all text draw properties to their default values.
	 * 
	 * <p>Does not affect the current font.</p>
	 */
	public void reset() {
		setColor(new float[]{0f, 0f, 0f, 1f});
		setSpaceX(0);
		setStartPosition(0, 0, 0);
		setScale(1.0f);
		setAlignment(FontAlign.ALIGN_LEFT);
	}
	
	
	/**
	 * Draws text to the screen.
	 * 
	 * <p>Make sure to initialize the shader program and give it a model matrix to specify 
	 * the position where you want the text to be rendered before calling this method!</p>
	 * 
	 * <p>Note that the text will be rendered with the current font.
	 * If no font was specified, a runtime exception will be thrown.</p>
	 * 
	 * @param text The text to draw.
	 */
	public void drawText(String text) {
		float curX; // current X position to draw the character
		final float curY = 0; // doesn't change
		
		if (currentFont == null)
			throw new IllegalStateException("No font selected!");
		
		// set starting X position
		final float drawWidth = calculateDrawWidth(text);
		switch (alignment) {
			case ALIGN_CENTER:
				curX = -drawWidth / 2.0f; break;
			case ALIGN_RIGHT:
				curX = -drawWidth; break;
			case ALIGN_LEFT:
			default:
				curX = 0;
		}
		
		// split the text into chunks for max DRAW_BUFFERS_LENGTH characters
		int charsLeft = text.length();
		int curCharPosition = 0;
		while (charsLeft > 0) {
			int chunkLength = charsLeft;
			if (charsLeft > DRAW_BUFFERS_LENGTH)
				chunkLength = DRAW_BUFFERS_LENGTH;
			short vi = 0;
			
			// fill out the vertex/index/texture coordinate buffers
			for (short i=0; i<chunkLength; i++) {
				final char c = text.charAt(curCharPosition);
				final float width = currentFont.getCharWidth(c) + 2*PAD_X;
				final float height = currentFont.maxHeight + 2*PAD_Y;
				
				final float textureSize = (float)currentFont.getTextureSize();
				final float[] texCoords = currentFont.getCharCoords(c);
				texCoords[0] /= textureSize;
				texCoords[1] /= textureSize;
				final float texWidth = width / textureSize;
				final float texHeight = height / textureSize;
				
				// compute the vertices of the character's quad
				workVertexBuf.put(new float[]{
						// top-left vertex:
						curX, curY + height, 0,
						// bottom-left vertex:
						curX, curY, 0,
						// bottom-right vertex:
						curX + width, curY, 0,
						// top-right vertex:
						curX + width, curY + height, 0,
				});
				// put the vertices' indexes
				workIndexBuf.put(new short[]{
						// 2 triangles that form a quad
						vi, (short) (vi+1), (short) (vi+2), 
						vi, (short) (vi+2), (short) (vi+3)
				});
				// now fill in the texture coordinates for the current char
				workTextureCoordsBuf.put(new float[]{
						// top-left vertex:
						texCoords[0], texCoords[1],
						// bottom-left vertex:
						texCoords[0], texCoords[1] + texHeight,
						// bottom-right vertex:
						texCoords[0] + texWidth, texCoords[1] + texHeight,
						// top-right vertex:
						texCoords[0] + texWidth, texCoords[1],
				});
				
				// advance the iterators
				vi += 4; // 4 vertices per iteration
				curX += width + spaceX;
				curCharPosition++;
				charsLeft--;
			}
			
			// draw the buffers
			workVertexBuf.flip();
			workIndexBuf.flip();
			workTextureCoordsBuf.flip();
			
			final Shader shader = scene.getShaderManager().getShader("draw_text");
			shader.use();
			
			Matrix.setIdentityM(workModelMatrix, 0);
			Matrix.translateM(workModelMatrix, 0,
					startPosition.getX(), startPosition.getY(), startPosition.getZ());
			Matrix.scaleM(workModelMatrix, 0, 1f / currentFont.maxHeight * scale, 1f / currentFont.maxHeight * scale, 1);
			
			// get shader attributes' locations
			int a_position = shader.getAttribLocation("a_position");
			int a_textureCoords = shader.getAttribLocation("a_textureCoords");
			
			// get shader uniforms' locations
			int u_texture = shader.getUniformLocation("u_texture");
			int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
			int u_color = shader.getUniformLocation("u_color");
			
			// send the matrices
			GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, workModelMatrix, 0);
			
			// send the vertex data to the shader
			GLES20.glEnableVertexAttribArray(a_position);
			GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false, 0, workVertexBuf);
			
			// send texture and color data (use the first texture unit)
			GLES20.glUniform4fv(u_color, 1, currentColor, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentFont.getTextureId());
			GLES20.glUniform1i(u_texture, 0);
			
			// send the texture coords
			GLES20.glVertexAttribPointer(a_textureCoords, 2 /* coords */, GLES20.GL_FLOAT, false, 0, workTextureCoordsBuf);
			GLES20.glEnableVertexAttribArray(a_textureCoords);
			
			// draw!
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, chunkLength * 6, GLES20.GL_UNSIGNED_SHORT, workIndexBuf);
			
			// clear the buffers
			workVertexBuf.clear();
			workIndexBuf.clear();
			workTextureCoordsBuf.clear();
		}
	}
	
	/**
	 * Calculates the text's width (in OpenGL object space).
	 * 
	 * @param text The text to calculate dimension for.
	 * @return Text's width when drawn.
	 */
	public float calculateDrawWidth(String text) {
		if (currentFont == null)
			throw new IllegalStateException("No font selected!");
		
		float width = 0;
		
		for (int i=0; i<text.length(); i++) {
			final char c = text.charAt(i);
			final float charWidth = currentFont.getCharWidth(c) + 2*PAD_X;
			
			width += charWidth + spaceX;
		}
		
		return width;
	}
	
	/**
	 * Draws the entire texture map on screen.
	 * 
	 * <p>Used for debugging purposes.</p>
	 */
	@SuppressWarnings("unused")
	public void drawTextureMap() {
		
		// create a rectangle from -1 to 1
		final float[] debugVertexArray = {
				-1f,  1f, 0, // top left
				-1f, -1f, 0, // bottom left
				1f, -1f, 0, // bottom right
				1f,  1f, 0 // top right
		};
		
		// quad's triangles
		final short[] debugIndexArray = {
				0, 1, 2, 0, 2, 3
		};
		
		// texture's coordinates
		final float[] debugTextureCoords = {
				0f, 0f, // top left
				0f, 1f, // bottom left
				1f, 1f, // bottom right
				1f, 0f  // top right
		};
		
		// the color to use for drawing.
		final float[] debugColor = {
				1.0f, 0.1f, 0.1f, 1.0f
		};
		
		workVertexBuf.put(debugVertexArray);
		workIndexBuf.put(debugIndexArray);
		workTextureCoordsBuf.put(debugTextureCoords);
		
		workVertexBuf.flip();
		workIndexBuf.flip();
		workTextureCoordsBuf.flip();
		
		final Shader shader = scene.getShaderManager().getShader("draw_text");
		shader.use();
		
		// get shader attributes' locations
		int a_position = shader.getAttribLocation("a_position");
		int a_textureCoords = shader.getAttribLocation("a_textureCoords");
		
		// get shader uniforms' locations
		int u_texture = shader.getUniformLocation("u_texture");
		int u_modelMatrix = shader.getUniformLocation("u_modelMatrix");
		int u_color = shader.getUniformLocation("u_color");
		
		// send the matrices
		GLES20.glUniformMatrix4fv(u_modelMatrix, 1, false, workModelMatrix, 0);
		Matrix.setIdentityM(workModelMatrix, 0);
		// Matrix.scaleM(workModelMatrix, 0, 1, 1, 1);
		// Matrix.translateM(workModelMatrix, 0, 0, 0, 0);
		
		// send the vertex data to the shader
		GLES20.glEnableVertexAttribArray(a_position);
		GLES20.glVertexAttribPointer(a_position, 3 /* coords */, GLES20.GL_FLOAT, false,
				3 * 4 /* bytes */, workVertexBuf);
		
		// send texture and color data (use the first texture unit)
		GLES20.glUniform4fv(u_color, 1, debugColor, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentFont.getTextureId());
		GLES20.glUniform1i(u_texture, 0);
		
		// send the texture coords
		workTextureCoordsBuf.position(0);
		GLES20.glVertexAttribPointer(a_textureCoords, 2 /* coords */, GLES20.GL_FLOAT, false, 0, workTextureCoordsBuf);
		GLES20.glEnableVertexAttribArray(a_textureCoords);
		
		// draw!
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, debugIndexArray.length, GLES20.GL_UNSIGNED_SHORT, workIndexBuf);
		
		workVertexBuf.clear();
		workIndexBuf.clear();
		workTextureCoordsBuf.clear();
	}
	
}
