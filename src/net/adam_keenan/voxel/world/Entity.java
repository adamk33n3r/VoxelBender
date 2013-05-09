/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world;

public abstract class Entity {
	
	public float x, y, z;
	public float yaw = 0;
	public float fallSpeed;
	
	public Arena arena;

	
	public Entity(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = 0;
	}
	
	
	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	
	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	
	/**
	 * @return the z
	 */
	public float getZ() {
		return z;
	}

	public abstract void update();
	public abstract void render();
	
}
