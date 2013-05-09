/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world.player;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_TRANSFORM_BIT;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;

import net.adam_keenan.voxel.VoxelBender;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Camera {
	
	private int mouseSpeed = 1;
	float yaw = 0;
	float pitch = 0;
	public float x = 0, y = 0, z = 0;
	private float fov = 70;
	private float aspectRatio;
	private float zNear = .1f;
	private float zFar = 300;
	
	private boolean wire;
	
	private Player player;
	
	private UnicodeFont font;
	private FloatBuffer perspectiveProjectionMatrix = BufferUtils.createFloatBuffer(16);
	private FloatBuffer orthographicProjectionMatrix = BufferUtils.createFloatBuffer(16);
	private DecimalFormat formatter = new DecimalFormat("#.##");
	
	public Camera(Player player, int x, int y, int z) {
		setUpFonts();
		this.aspectRatio = (float) VoxelBender.WINDOW_WIDTH / VoxelBender.WINDOW_HEIGHT;
		this.player = player;
		this.x = x + .5f;
		this.y = y + 1.5f;
		this.z = z + .5f;
	}
	
	public void setup() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, aspectRatio, zNear, zFar);
		glPopAttrib();
		
		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		
		yaw = 180;
	}
	
	@SuppressWarnings("unchecked")
	private void setUpFonts() {
		java.awt.Font awtFont = new java.awt.Font("Chalkduster", java.awt.Font.BOLD, 18);
		font = new UnicodeFont(awtFont);
		font.getEffects().add(new ColorEffect(Color.white));
		font.addAsciiGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
	}
	
	public void drawString(int x, int y, String string) {
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(orthographicProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glLoadIdentity();
		font.drawString(x, y, string);
		glPopAttrib();
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
	}
	
	/** Applies the camera translations and rotations to GL_MODELVIEW. */
	public void update() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(-pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glTranslatef(-x, -y, -z);
		glPopAttrib();
	}
	
	public void drawDebug() {
		drawString(10, 110, "Pitch: " + pitch + " Yaw: " + yaw);
		drawString(10, 90, "Player[x=" + formatter.format(player.x) + ",y=" + formatter.format(player.y) + ",z=" + formatter.format(player.z) + "]");
		drawString(10, 70, "Location: Cam[x=" + formatter.format(x) + ",y=" + formatter.format(y) + ",z=" + formatter.format(z) + "]");
		drawString(10, 50, "Flat Arena with a floating block for 360 degree view");
		drawString(10, 30, "Click to grab mouse, right click to release");
		drawString(10, 10, "WASD to move");
		
	}
	
	public void processMouse() {
		final float MAX_LOOK_UP = 100;
		final float MAX_LOOK_DOWN = -90;
		float mouseDX = Mouse.getDX() * 0.16f;
		float mouseDY = Mouse.getDY() * 0.16f;
		if (yaw + mouseDX >= 360) {
			yaw = yaw + mouseDX - 360;
		} else if (yaw + mouseDX < 0) {
			yaw = 360 - yaw + mouseDX;
		} else {
			yaw += mouseDX;
		}
		if (pitch - mouseDY >= MAX_LOOK_DOWN && pitch - mouseDY <= MAX_LOOK_UP) {
			pitch += -mouseDY;
		} else if (pitch - mouseDY < MAX_LOOK_DOWN) {
			pitch = MAX_LOOK_DOWN;
		} else if (pitch - mouseDY > MAX_LOOK_UP) {
			pitch = MAX_LOOK_UP;
		}
	}
	
	public boolean processMouse(float mouseSpeed, float maxLookUp, float maxLookDown) {
		float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
		float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
		if (mouseDX == 0 && mouseDY == 0)
			return false;
		yaw = (yaw + mouseDX) % 360;
		if (yaw < 0)
			yaw += 360;
		if (pitch + mouseDY >= maxLookDown && pitch + mouseDY <= maxLookUp)
			pitch += mouseDY;
		else if (pitch + mouseDY > maxLookUp)
			pitch = maxLookUp;
		else if (pitch + mouseDY < maxLookDown)
			pitch = maxLookDown;
		return true;
	}
	
	void up(float dy) {
		y += dy;
	}
	
	void down(float dy) {
		y -= dy;
	}
	
	void fall(float dy) {
		if (this.y > (int) this.y && this.y - dy < (int) this.y)
			this.y = (int) this.y;
		else
			this.y -= dy;
	}
	
	void move(float x, float z) {
		this.x = x;
		this.z = z;
	}
	
	void moveFromLook(float dx, float dy, float dz) {
		this.z += dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw));
		this.x -= dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw));
		this.y += dy * (float) sin(toRadians(pitch - 90)) + dz * sin(toRadians(pitch));
	}
}
