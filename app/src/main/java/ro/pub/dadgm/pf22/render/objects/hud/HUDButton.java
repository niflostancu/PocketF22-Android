package ro.pub.dadgm.pf22.render.objects.hud;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import java.nio.FloatBuffer;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.BufferUtils;
import ro.pub.dadgm.pf22.render.utils.DrawText;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;

/**
 * Draws a menu button (a circle with a letter inside).
 */
public class HUDButton extends HUDObject {
	
	/**
	 * Button texture's dimensions.
	 */
	protected static final int[] dimensions = { 64, 64 };
	
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
	 * The vertex texture coordinates.
	 */
	protected static final float[] staticTextureCoordsArray = {
			0f, 0f, // top left
			0f, 1f, // bottom left
			1f, 1f, // bottom right
			1f, 0f  // top right
	};
	
	/**
	 * Static color to blend the object's texture.
	 */
	protected static final float[] staticColor = {
			0.5f, 0.5f, 0.5f, 0.5f
	};
	
	/**
	 * Static color to blend with the text's texture.
	 */
	protected static final float[] staticTextColor = {
			0.9f, 0.9f, 0.9f, 0.9f
	};
	
	/**
	 * The hover color of the item.
	 */
	protected static final float[] hoverColor = {
			0.9f, 0.9f, 1.0f, 0.7f
	};
	
	/**
	 * The hover color of the item's text.
	 */
	protected static final float[] hoverTextColor = {
			1f, 1f, 1f, 1f
	};
	
	
	/**
	 * Scene's DrawText instance.
	 */
	protected DrawText drawText;
	
	/**
	 * The button's texture handle.
	 */
	protected int texture;
	
	/**
	 * A FloatBuffer for the texture coordinates.
	 */
	protected FloatBuffer textureCoordsBuf;
	
	/**
	 * Stores true if the current item is hovered.
	 */
	protected boolean hovered = false;
	
	/**
	 * HUD button's caption (text).
	 */
	protected String caption;
	
	/**
	 * The onclick listerer bound to the current item.
	 */
	protected View.OnClickListener clickListener;
	
	
	
	/**
	 * Initializes the HUD button object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 * @param caption The menu item's text.
	 * @param clickListener Menu item's click action.   
	 */
	public HUDButton(Scene3D scene, String tag, int priority, String caption, View.OnClickListener clickListener) {
		super(scene, tag, priority);
		
		this.drawText = scene.getDrawText();
		this.clickListener = clickListener;
		
		shader = scene.getShaderManager().getShader("simple_tex");
		
		generateTexture();
		vertexBuffer = BufferUtils.asBuffer(staticVertexArray);
		vertexIndexBuffer = BufferUtils.asBuffer(staticIndexArray);
		textureCoordsBuf = BufferUtils.asBuffer(staticTextureCoordsArray);
		
		// fixed dimensions
		setDimensions(0.7f, 0.7f);
		
		setCaption(caption);
	}
	
	/**
	 * Generates the bitmap texture used for the button.
	 */
	protected void generateTexture() {
		final String cacheKey = "bitmap_hud_button";
		texture = TextureLoader.loadTextureCached(null, cacheKey);
		if (texture > 0)
			return;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		Bitmap bitmap = Bitmap.createBitmap(dimensions[0], dimensions[1], Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(Color.TRANSPARENT); // full transparency
		
		paint.setColor(Color.argb(128, 255, 255, 255)); // semi-transparent white
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(dimensions[0] / 2, dimensions[1] / 2, dimensions[0] / 2 - 4, paint);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		paint.setColor(Color.argb(254, 255, 255, 255)); // opaque white
		canvas.drawCircle(dimensions[0]/2, dimensions[1]/2, dimensions[0]/2 - 4, paint);
		
		texture = TextureLoader.loadTextureCached(bitmap, cacheKey);
		if (texture == 0) 
			throw new RuntimeException("Unable to allocate texture for the button!");
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
	}
	
	
	@Override
	public void draw() {
		if (!visibility) return;
		
		boolean isHovered;
		
		synchronized (this) { // hover is set from the Activity thread
			isHovered = hovered;
		}
		
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
		
		GLES20.glVertexAttribPointer(a_textureCoords, 2 /* coords */, GLES20.GL_FLOAT, false, 0, textureCoordsBuf);
		GLES20.glEnableVertexAttribArray(a_textureCoords);
		
		// send texture and color data (use the first texture unit)
		GLES20.glUniform4fv(u_color, 1, (isHovered? hoverColor : staticColor ), 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(u_texture, 0);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, staticIndexArray.length, GLES20.GL_UNSIGNED_SHORT, vertexIndexBuffer);
		
		prepareDrawText();
		drawText.setColor((isHovered? hoverTextColor : staticTextColor ));
		drawText.drawText(caption);
	}
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_HOVER_ENTER || 
				e.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
			synchronized (this) {
				hovered = true;
			}
			return true;
		}
		
		if (e.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
			synchronized (this) {
				hovered = false;
			}
			return true;
		}
		
		if (e.getAction() == MotionEvent.ACTION_UP) {
			synchronized (this) {
				hovered = false;
			}
			clickListener.onClick(null);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Changes the menu item's caption. 
	 * Automatically recalculates the object's width.
	 * 
	 * @param caption The new caption to set.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		
		prepareDrawText();
	}
	
	/**
	 * Prepares the DrawText instance for drawing text.
	 * 
	 * <p>Aka defines font properties to be used for drawing.</p>
	 */
	protected void prepareDrawText() {
		float fHeight = height * 0.8f;
		
		drawText.reset();
		drawText.useFont("fonts/Roboto-Regular.ttf", 48);
		
		drawText.setStartPosition(position.getX() + width / 2, position.getY() + 0.1f, position.getZ());
		drawText.setScale(fHeight);
		drawText.setAlignment(DrawText.FontAlign.ALIGN_CENTER);
	}
	
}
