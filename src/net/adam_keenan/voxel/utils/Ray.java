/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.utils;

import org.lwjgl.util.vector.Vector3f;

public class Ray {
	
	public Vector3f pos, dir, scaledDir;
	public float distance;
	public float scalar;
	
	public Ray(Vector3f pos, Vector3f dir, float scalar) {
		this.pos = pos;
		this.dir = dir;
		this.scalar = scalar;
		this.scaledDir = scale(dir, scalar);
	}
	
	public void next() {
		pos = Vector3f.add(pos, scaledDir, null);
		distance += scalar;
	}
	
	private Vector3f scale(Vector3f vec, float scalar) {
		Vector3f tmp = new Vector3f();
		tmp.x = vec.x * scalar;
		tmp.y = vec.y * scalar;
		tmp.z = vec.z * scalar;
		return tmp;
	}
	
	@Override
	public String toString() {
		return String.format("Ray: Pos = (%s) Dir = (%s)", pos, dir);
	}
	
}
