package net.adam_keenan.voxel.world;

public abstract class Entity {
	
	public float x, y, z;
	public float yaw = 0;
	public float fallSpeed;
	
	public Arena arena;

	
	public Entity(int x, int y, int z) {
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
