/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class RayTracer {
	
	private RayTracer() {
	}
	
	public static Ray getScreenCenterRay() {
		float winX = Display.getWidth() / 2, winY = Display.getHeight() / 2;
		
		IntBuffer viewport = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
		FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		
		FloatBuffer positionNear = BufferUtils.createFloatBuffer(3);
		FloatBuffer positionFar = BufferUtils.createFloatBuffer(3);
		GLU.gluUnProject(winX, winY, 0, modelview, projection, viewport, positionNear);
		GLU.gluUnProject(winX, winY, 1, modelview, projection, viewport, positionFar);
		
		Vector3f nearVec = new Vector3f(positionNear.get(0), positionNear.get(1), positionNear.get(2));
		Vector3f farVec = new Vector3f(positionFar.get(0), positionFar.get(1), positionFar.get(2));
		return new Ray(nearVec, Vector3f.sub(farVec, nearVec, null).normalise(null), .1f);
	}
	
}
