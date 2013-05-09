/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Physics {
	
	public Physics() {
		
	}
	
	/**
	 * 
	 * @param entity
	 * @param x
	 * @param y
	 * @param z
	 * @return true if CAN move
	 */
	public static boolean moveWithCollisions(Entity entity, float dx, float dz) {
		Arena arena = entity.arena;
//		System.out.println(String.format("Standing on: (%s, %s, %s) : %s", entity.x, entity.y, entity.z,
//				arena.blocks[(int) entity.x][(int) entity.y - 1][(int) entity.z].getType()));
		if (entity.x < 0 || entity.y < 0 || entity.z < 0 || entity.x >= arena.X_SIZE || entity.y >= arena.Y_SIZE || entity.z >= arena.Z_SIZE)
			return false;
		
		float xAm = -(dx * (float) sin(toRadians(entity.yaw - 90)) + dz * (float) sin(toRadians(entity.yaw)));
		float zAm = dx * (float) cos(toRadians(entity.yaw - 90)) + dz * (float) cos(toRadians(entity.yaw));
		Block testBlock = arena.blocks[(int) (entity.x + xAm)][(int) entity.y][(int) (entity.z + zAm)];
		if (testBlock.isWalkThroughable() ||
				(int) testBlock.x == (int) entity.x && (int) testBlock.y == (int) entity.y - 1 && (int) testBlock.z == (int) entity.z) {
			entity.x += xAm;
			entity.z += zAm;
		} else {
			if ((int) (entity.x + xAm) < (int) entity.x)
				entity.x = (int) entity.x;
			else if ((int) (entity.x + xAm) > (int) entity.x)
				entity.x = (int) entity.x + .99f;
			if ((int) (entity.z + zAm) < (int) entity.z)
				entity.z = (int) entity.z;
			else if ((int) (entity.z + zAm) > (int) entity.z)
				entity.z = (int) entity.z + .99f;
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param entity The entity to add gravity effects to
	 * @param fallSpeed
	 * @return true if is falling
	 */
	public static boolean gravity(Entity entity, float fallSpeed) {
		Arena arena = entity.arena;
		Block blockUnder = arena.blocks[(int) entity.x][(int) entity.y - 1][(int) entity.z];
		if (!blockUnder.isWalkThroughable()) {
			if (fallSpeed < 0) {
				entity.y -= fallSpeed;
				return true;
			}
			if ((int) blockUnder.x == (int) entity.x && (int) blockUnder.y == (int) entity.y - 1 && (int) blockUnder.z == (int) entity.z) {
				if (entity.y - fallSpeed < blockUnder.y + 1 || entity.y == blockUnder.y + 1) {	// If it will be put underground or it is on the ground
					entity.y = blockUnder.y + 1; 												// Then put it on the ground
					return false;
				}
			} else {
				System.out.println("else " + fallSpeed);
			}
		}
		entity.y -= fallSpeed;
		return true;
	}
}
