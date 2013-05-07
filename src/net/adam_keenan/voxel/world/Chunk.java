package net.adam_keenan.voxel.world;

import java.nio.FloatBuffer;
import java.util.Random;

import net.adam_keenan.voxel.world.Block.BlockType;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class Chunk {
	
	final int			CHUNK_SIZE	= 20;
	final int			CUBE_LENGTH	= 1;
	public Block[][][]	blocks;
	private int			VBOVertexHandle;
	private int			VBOColorHandle;
	private Random		r;
	
	public Chunk(int startX, int startY, int startZ) {
		r = new Random();
		blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					if (r.nextFloat() > 0.85f) {
						blocks[x][y][z] = new Block(z, z, z, BlockType.GRASS);
					} else if (r.nextFloat() > 0.8f) {
						blocks[x][y][z] = new Block(z, z, z, BlockType.DIRT);
					} else if (r.nextFloat() > 0.7f) {
						blocks[x][y][z] = new Block(z, z, z, BlockType.WATER);
					} else {
						blocks[x][y][z] = new Block(z, z, z, BlockType.STONE);
					}
					//blocks[x][y][z] = new Block(z, z, z, BlockType.AIR);
					
				}
			}
		}
		VBOColorHandle = GL15.glGenBuffers();
		VBOVertexHandle = GL15.glGenBuffers();
		rebuildMesh(startX, startY, startZ);
	}
	
	public void render() {
		GL11.glPushMatrix();
		{
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
			GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0L);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
		}
		GL11.glPopMatrix();
	}
	
	public static void enableLighting(boolean enabled) {
		//GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		
	}
	
	public void rebuildMesh(int startX, int startY, int startZ) {
		VBOColorHandle = GL15.glGenBuffers();
		VBOVertexHandle = GL15.glGenBuffers();
		FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * /*#POS*/3 * /*#POINTSPERFACE*/4 * /*#FACES*/6);
		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * /*#COLORVAL*/4 * /*#PONTS*/24);
		FloatBuffer t = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 0 });
		for (int x = 0; x < CHUNK_SIZE; x += 1) {
			for (int y = 0; y < CHUNK_SIZE; y += 1) {
				for (int z = 0; z < CHUNK_SIZE; z += 1) {
//					VertexPositionData.put(Block.createBlock(startX + x * CUBE_LENGTH, startY + y * CUBE_LENGTH, startZ + z * CUBE_LENGTH));
					VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(x - startX)][(y - startY)][(z - startZ)])));
				}
			}
		}
		
		VertexColorData.flip();
		VertexPositionData.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexPositionData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexColorData, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	
	private float[] createCubeVertexCol(float[] CubeColorArray) {
		Random r = new Random();
		float[] colors = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1f, .5f, 0f, 1, 1f, .5f, 0f, 1, };
		float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
			//			cubeColors[i] = colors[i % colors.length];
		}
		return cubeColors;
	}
	
	private float[] getCubeColor(Block block) {
		switch (block.getType()) {
			case GRASS:
				return new float[] { 0, 1, 0, 1 };
			case DIRT:
				return new float[] { 1, 0.5f, 0, 1 };
			case WATER:
				return new float[] { 0, 0, 1, 1 };
			case AIR:
				return new float[] { 1, 1, 1, 0 };
			default:
				return new float[] { 0, 0, 0, 1 };
		}
	}
	
	private float[] getNormalVector() {
		return new float[] {
				//BOTTOM
		0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
				//TOP
		0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
				//FRONT
		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				//BOTTOM
		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				//LEFT QUAD
		1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
				//RIGHT QUAD
		-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, };
	}
}
