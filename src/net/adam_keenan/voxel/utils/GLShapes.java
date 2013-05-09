package net.adam_keenan.voxel.utils;

import net.adam_keenan.voxel.world.Block.BlockType;
import net.adam_keenan.voxel.world.BlockVBO;

public class GLShapes {
	
	private GLShapes() {
	}
	
	public static void drawCube(BlockType type, float x, float y, float z) {
//		float rad = size / 2;
		BlockVBO.getInstance().render(type, x, y, z);
//		GL11.glBegin(GL11.GL_QUADS);
//		{
//			// Front
//			GL11.glVertex3f(x - rad, y - rad, z - rad);
//			GL11.glVertex3f(x + rad, y - rad, z - rad);
//			GL11.glVertex3f(x + rad, y + rad, z - rad);
//			GL11.glVertex3f(x - rad, y + rad, z - rad);
//			
//			// Back
//			GL11.glVertex3f(x + rad, y - rad, z + rad);
//			GL11.glVertex3f(x - rad, y - rad, z + rad);
//			GL11.glVertex3f(x - rad, y + rad, z + rad);
//			GL11.glVertex3f(x + rad, y + rad, z + rad);
//			
//			// Left
//			GL11.glVertex3f(x + rad, y - rad, z + rad);
//			GL11.glVertex3f(x - rad, y - rad, z + rad);
//			GL11.glVertex3f(x - rad, y + rad, z + rad);
//			GL11.glVertex3f(x + rad, y + rad, z + rad);
//		}
//		GL11.glEnd();
	}
	
}
