/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.hud;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class HUD {
	
	private HUD() {
		
	}
	
	public static void drawCrosshairs() {
		
		float screenx = (float)Display.getWidth() / 2, screeny = (float)Display.getHeight() / 2;
		float x1, y1, x2, y2, width1 = 4, height1 = 20, width2 = height1, height2 = width1;
		x1 = screenx - width1 / 2;
		y1 = screeny - height1 / 2;
		x2 = screenx - width2 / 2;
		y2 = screeny - height2 / 2;
		
		FloatBuffer perspectiveProjectionMatrix;
		FloatBuffer orthographicProjectionMatrix;
		perspectiveProjectionMatrix = BufferUtils.createFloatBuffer(16);
		orthographicProjectionMatrix = BufferUtils.createFloatBuffer(16);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
//		GL11.glLoadMatrix(perspectiveProjectionMatrix);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION); // For text drawing
		GL11.glLoadMatrix(orthographicProjectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glLoadIdentity(); // Clears matrix
		
		GL11.glColor4f(0, 0, 0, .5f);
		GL11.glRectf(x1, y1, x1 + width1, y1 + height1);
		GL11.glRectf(x2, y2, x2 + (width2 - width1) / 2, y2 + height2);
		GL11.glRectf(x2 + (width2 + width1) / 2, y2, x2 + width2, y2 + height2);
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(perspectiveProjectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
	}
	
}
