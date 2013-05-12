/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import net.adam_keenan.voxel.world.Block.BlockType;
import net.adam_keenan.voxel.world.player.Projectile;

public class Arena {
	
	public final int X_SIZE = 20, Y_SIZE = 20, Z_SIZE = 20;
	final int CUBE_LENGTH = 1;
	
	public Block[][][] blocks;
	private ArrayList<Projectile> projectiles;
	
	public Arena() {
	}
	
	public void addProjectile(Projectile proj) {
		if (projectiles == null)
			projectiles = new ArrayList<Projectile>();
		projectiles.add(proj);
	}
	
	public void genTwoBlocks() {
		blocks = new Block[1][1][2];
		blocks[0][0][0] = new Block(0, 0, 0, Block.BlockType.DIRT);
		blocks[0][0][1] = new Block(0, 0, 1, Block.BlockType.DIRT);
	}
	
	public boolean inBounds(int x, int y, int z) {
		return (x >= 0 && x < X_SIZE && y >= 0 && y < Y_SIZE && z >= 0 && z < Z_SIZE);
	}
	
	public void genArena() {
		blocks = new Block[X_SIZE][Y_SIZE][Z_SIZE];
		BlockType type = null;
		for (int x = 0; x < X_SIZE; x++)
			for (int y = 0; y < Y_SIZE; y++)
				for (int z = 0; z < Z_SIZE; z++) {
					if ((y == Y_SIZE - 1 && x == 5 && z == 5) || (y == 15 && x == 10 && z == 10) || (y == Y_SIZE - 6 && x == 10 && z == 11)
							|| (y == 11 && x == 1 && z == 1) || (y == 10 && x == 2 && z == 1))
						type = BlockType.GRASS;
					else if (y > 9 && ((x == 0 || x == X_SIZE - 1) || (z == 0 || z == Z_SIZE - 1))) {
						type = BlockType.DIRT;
					}
					else if (y < 5 || (y == 12 && x == 1 && z == 2))
						type = BlockType.STONE;
					else if (y < Y_SIZE / 2 - 1)
						type = BlockType.DIRT;
					else if (y < Y_SIZE / 2)
						type = BlockType.GRASS;
					else
						type = BlockType.AIR;
					blocks[x][y][z] = new Block(x, y, z, type);
				}
		System.out.println("Done building Arena");
	}
	
	public void genDemoBlocks() {
		blocks = new Block[6][1][2];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 2; j++) {
				switch (i) {
					case 0:
					case 1:
						blocks[i][0][j] = new Block(i, 0, j, Block.BlockType.DIRT);
						break;
					case 2:
					case 3:
						blocks[i][0][j] = new Block(i, 0, j, Block.BlockType.GRASS);
						break;
					case 4:
					case 5:
						blocks[i][0][j] = new Block(i, 0, j, Block.BlockType.STONE);
						break;
				}
			}
		}/*for (Block[][] blockX : blocks)
			for (Block[] blockY : blockX)
				for (Block block : blockY)
					System.out.println(String.format("(%s, %s, %s) : %s", block.x, block.y, block.z, block.getType()));*/
		
	}
	
	public void generate() {
		blocks = new Block[X_SIZE][Y_SIZE][Z_SIZE];
		System.out.println(blocks.length);
		for (int x = 0; x < X_SIZE; x++)
			for (int y = 0; y < Y_SIZE; y++)
				for (int z = 0; z < Z_SIZE; z++) {
					if (y > 4) {
						blocks[x][y][z] = new Block(x, y, z, Block.BlockType.AIR);
					} else if ((x == 2 || x == 3) && z > 2 && z < 18 && y > 0)
						blocks[x][y][z] = new Block(x, y, z, Block.BlockType.WATER);
					else
						blocks[x][y][z] = new Block(x, y, z, Block.BlockType.DIRT);
				}
		for (int x = 0; x < X_SIZE; x++) {
			blocks[x][5][0] = new Block(x, 5, 0, Block.BlockType.DIRT);
			blocks[x][5][Z_SIZE - 1] = new Block(x, 5, 0, Block.BlockType.DIRT);
		}
		blocks[5][5][5] = new Block(5, 5, 5, Block.BlockType.GRASS);
//		makeVBOs();
	}
	
//	private void makeVBOs() {
//		FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((X_SIZE * Y_SIZE * Z_SIZE) * /*#POS*/3 * /*#POINTSPERFACE*/4 * /*#FACES*/6);
//		FloatBuffer TexturePositionData = BufferUtils.createFloatBuffer((X_SIZE * Y_SIZE * Z_SIZE) * /*#POS*/3 * /*#POINTSPERFACE*/4 * /*#FACES*/6);
//		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((X_SIZE * Y_SIZE * Z_SIZE) * /*#COLORVAL*/4 * /*#POINTS*/24);
//		for (int x = 0; x < X_SIZE; x += 1) {
//			for (int y = 0; y < Y_SIZE - 1; y += 1) {
//				for (int z = 0; z < Z_SIZE; z += 1) {
//					VertexPositionData.put(Block.createBlock(0 + x * CUBE_LENGTH, 0 + y * CUBE_LENGTH, 0 + z * CUBE_LENGTH));
//					TexturePositionData.put(Block.createTex(blocks[x][y][z].getType()));
//					VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(x - 0)][(y + 0)][(z - 0)])));
//				}
//			}
//		}
//		
//		VertexPositionData.flip();
//		TexturePositionData.flip();
//		VertexColorData.flip();
//		
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
//		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexPositionData, GL15.GL_STATIC_DRAW);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//		
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOTextureHandle);
//		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, TexturePositionData, GL15.GL_STATIC_DRAW);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//		
////		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
////		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexColorData, GL15.GL_STATIC_DRAW);
////		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//	}
	
	public void update() {
		for (Projectile projectile : projectiles)
			projectile.update();
	}
	
	public void render() {
		for (Block[][] blockX : blocks)
			for (Block[] blockY : blockX)
				for (Block block : blockY)
					if (block.getType() != BlockType.AIR)
						block.render();
		for (Projectile projectile : projectiles)
			projectile.render();
	}

	public boolean contains(Vector3f pos) {
		return (pos.x >= 0 && pos.x < X_SIZE &&
				pos.y >= 0 && pos.y < Y_SIZE &&
				pos.z >= 0 && pos.z < Z_SIZE);
	}
	
}
