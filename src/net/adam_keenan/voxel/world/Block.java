package net.adam_keenan.voxel.world;

import org.lwjgl.util.vector.Vector3f;

public class Block extends Entity {
	
	private BlockType type;
	private boolean isActive;
//	public int x = 0, y = 0, z = 0;
//	private static float	CUBE_LENGTH	= 1;
	private BlockVBO vbo;
	
	public enum BlockType {
		AIR(0), GRASS(1), DIRT(2), WATER(3), STONE(4), WOOD(5), SAND(6), NUM_TYPES(8), OUTLINE(7);
		
		private int blockID;
		
		BlockType(int i) {
			blockID = i;
		}
		
		public int getID() {
			return blockID;
		}
	}
	
	public Block(int x, int y, int z, BlockType type) {
		super(x, y, z);
		this.type = type;
		this.vbo = BlockVBO.getInstance();
	}
	
	public BlockType getType() {
		return type;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public boolean isWalkThroughable() {
		switch (type) {
			case WATER:
			case AIR:
				return true;
			default:
				return false;
		}
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}
	
	public int getID() {
		return type.getID();
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void render() {
		this.vbo.render(type, (int) x, (int) y, (int) z);
	}

	public boolean contains(Vector3f loc) {
//		if(x == 2 && y == 10 && z == 1)
//			System.out.println(String.format("%s,%s,%s %s,%s,%s",loc.x,loc.y,loc.z,x,y,z));
		return (loc.x >= x && loc.x < x + 1 &&
				loc.y >= y && loc.y < y + 1 &&
				loc.z >= z && loc.z < z + 1);
	}
	
}
