package net.adam_keenan.voxel;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import de.matthiasmann.twl.utils.PNGDecoder;

public class BitmapFont {
	
	private int	fontTexture;
	private FloatBuffer	perspectiveProjectionMatrix;
	private static FloatBuffer	orthographicProjectionMatrix;
	
	/**
	 * A Bitmap font ready to be drawn
	 * @param fontLoc - Location where font .png is
	 */
	public BitmapFont(String fontLoc) {
		perspectiveProjectionMatrix = BufferUtils.createFloatBuffer(16);
		orthographicProjectionMatrix = BufferUtils.createFloatBuffer(16);
		glMatrixMode(GL_PROJECTION);
		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		setUpTextures(fontLoc);
	}
	
	private void setUpTextures(String fontLoc) {
		fontTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, fontTexture);
		PNGDecoder decoder = null;
		ByteBuffer buffer = null;
		try {
			decoder = new PNGDecoder(new FileInputStream(fontLoc));
			buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
			buffer.flip();
		} catch (IOException e) {
			System.err.println("Could not find font.");
			Display.destroy();
			System.exit(1);
		}
		// Load the loaded texture data into the texture obj
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		// Unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private static void renderString(String string, int fontTexture, int gridSize, float x, float y, float characterWidth, float characterHeight) {
		glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, fontTexture);
		// Enable linear texture filtering for smoothed results
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		// Enable additive blending. This means that the colors will be added to already existing colors
		// in the frame buffer. In practice, this makes the black parts of the texture become invisible
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);

		// Store the current modelview matrix
		glPushMatrix();
		// Offset all subsequent (at least up until glPopMatrix) vertex coordinates
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();

//		glColor3f(1, 0, 0);
		//glMatrixMode(GL_MODELVIEW);
		glTranslatef(x, y, 0);
		glBegin(GL_QUADS);
		// Iterate over all the characters in the string)
		for (int i = 0; i < string.length(); i++) {
			// Get the ASCII code of the character by type-casting to int
			int asciiCode = (int) string.charAt(i);
			// There are 16 cells in a texture, and a texture coordinate ranges from 0.0 to 1.0
			final float cellSize = 1.0f / gridSize;
			// The cell's x-coordinate is the greates integer smaller than the remainder of the ASCII-code divided by the amount of cells on the x-axis, times the cell size
			float cellX = ((int) asciiCode % gridSize) * cellSize;
			// The cell's y-coordinate is the greatest integer smaller than the ASCII code divied by the amount of cells on the y-axis
			float cellY = ((int) asciiCode / gridSize) * cellSize;
			glTexCoord2f(cellX, cellY + cellSize);
			glVertex2f(i * characterWidth / 3, y);
			
			glTexCoord2f(cellX + cellSize, cellY + cellSize);
			glVertex2f(i * characterWidth / 3 + characterWidth / 2, y);
			
			glTexCoord2f(cellX + cellSize, cellY);
			glVertex2f(i * characterWidth / 3 + characterWidth / 2, y + characterHeight);
			
			glTexCoord2f(cellX, cellY);
			glVertex2f(i * characterWidth / 3, y + characterHeight);
		}
		glEnd();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		glPopAttrib();
	}
	
	public void draw(String string, float x, float y, float characterWidth, float characterHeight) {
		glMatrixMode(GL_PROJECTION); // For text drawing
		glLoadMatrix(orthographicProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity(); // Clears matrix
		renderString(string, fontTexture, 16, x, y, characterWidth, characterHeight);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}
	
}
