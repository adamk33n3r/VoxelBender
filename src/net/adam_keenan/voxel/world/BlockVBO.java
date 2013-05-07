package net.adam_keenan.voxel.world;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.adam_keenan.voxel.world.Block.BlockType;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

public class BlockVBO {
	
	private final int vertexBufferID, colorBufferID, dirtTextureID, stoneTextureID, grassTextureID, indexBufferID;
	
	private static BlockVBO instance;
	
	private final static int TEXTURE_SIZE = 4;
	
	private BlockVBO() {
		this.vertexBufferID = createVBOID();
		this.colorBufferID = createVBOID();
		this.dirtTextureID = createVBOID();
		this.stoneTextureID = createVBOID();
		this.grassTextureID = createVBOID();
		
		this.indexBufferID = createVBOID();
	}
	
	public static BlockVBO getInstance() {
		if (instance == null) {
			instance = new BlockVBO();
			instance.bufferData(instance.vertexBufferID, createVertexFloatBuffer());
			instance.bufferData(instance.colorBufferID, createColorFloatBuffer());
			instance.bufferData(instance.dirtTextureID, createTextureFloatBuffer(BlockType.DIRT));
			instance.bufferData(instance.stoneTextureID, createTextureFloatBuffer(BlockType.STONE));
			instance.bufferData(instance.grassTextureID, createTextureFloatBuffer(BlockType.GRASS));
			
			instance.bufferElementData(instance.indexBufferID, createIndexBuffer());
		}
		return instance;
	}
	
	private int createVBOID() {
		if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			Display.destroy();
			System.err.println("Does not support VBO");
			System.exit(1);
			return 0;
		} else {
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			ARBVertexBufferObject.glGenBuffersARB(buffer);
			return buffer.get(0);
		}
	}
	
	private static FloatBuffer createVertexFloatBuffer() {
		// 3 Coords, 4 Points per face, 6 Faces
		FloatBuffer fBuf = BufferUtils.createFloatBuffer(3 * 4 * 6);
		fBuf.put(createVertexArray(0, 0, 0));
		fBuf.flip();
		return fBuf;
	}
	
	private static float[] createVertexArray(int x, int y, int z) {
		// TODO: The sides may not be in the right order
		return new float[] {
				// BOTTOM QUAD
				x, y, z, x + 1, y, z, x + 1, y, z + 1, x, y, z + 1,
				// TOP QUAD
				x, y + 1, z + 1, x + 1, y + 1, z + 1, x + 1, y + 1, z, x, y + 1, z,
				// FRONT QUAD
				x + 1, y, z, x, y, z, x, y + 1, z, x + 1, y + 1, z,
				// BACK QUAD
				x, y, z + 1, x + 1, y, z + 1, x + 1, y + 1, z + 1, x, y + 1, z + 1,
				// LEFT QUAD
				x, y, z, x, y, z + 1, x, y + 1, z + 1, x, y + 1, z,
				// RIGHT QUAD
				x + 1, y, z + 1, x + 1, y, z, x + 1, y + 1, z, x + 1, y + 1, z + 1 };
	}
	
	private static FloatBuffer createColorFloatBuffer() {
		// 4 Colors, 4 Points per face, 6 Faces
		FloatBuffer fBuf = BufferUtils.createFloatBuffer(4 * 4 * 6);
		fBuf.put(createColorArray());
		fBuf.flip();
		return fBuf;
	}
	
	private static float[] createColorArray() {
//		return createCubeVertexCol(new float[] { .52f, .37f, .26f, 1 });
		return createCubeVertexCol(new float[] { 1, 1, 1, 0 });
	}
	
	private static float[] createCubeVertexCol(float[] cubeColorArray) {
		float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
		}
		return cubeColors;
	}
	
	private static FloatBuffer createTextureFloatBuffer(BlockType type) {
		FloatBuffer fBuf = BufferUtils.createFloatBuffer(2 * 4 * 6);
		float[] arr = null;
		switch (type) {
			case DIRT:
				arr = createSymmetricTextureFloatArray(0, 0, TEXTURE_SIZE);
				break;
			case STONE:
				arr = createSymmetricTextureFloatArray(1, 1, TEXTURE_SIZE);
				break;
			case GRASS:
				arr = createGrassTextureFloatArray(3, 0, 0, 0, 2, 0, TEXTURE_SIZE);
				break;
			default:
				arr = createSymmetricTextureFloatArray(0, 1, TEXTURE_SIZE);
				break;
		}
		fBuf.put(arr);
		fBuf.flip();
		return fBuf;
	}
	
	private static float[] createGrassTextureFloatArray(int topX, int topY, int bottomX, int bottomY, int sideX, int sideY, int texSize) {
		float bx = (float) bottomX / texSize, by = (float) bottomY / texSize;
		float tx = (float) topX / texSize, ty = (float) topY / texSize;
		float sx = (float) sideX / texSize, sy = (float) sideY / texSize;
		float stride = (float) 1 / texSize;
		return new float[] {
				// Bottom
				bx, by + stride, bx + stride, by + stride, bx + stride, by, bx, by,
				// Top
				tx, ty + stride, tx + stride, ty + stride, tx + stride, ty, tx, ty,
				// Front
				sx, sy + stride, sx + stride, sy + stride, sx + stride, sy, sx, sy,
				// Back
				sx, sy + stride, sx + stride, sy + stride, sx + stride, sy, sx, sy,
				// Left
				sx, sy + stride, sx + stride, sy + stride, sx + stride, sy, sx, sy,
				// Right
				sx, sy + stride, sx + stride, sy + stride, sx + stride, sy, sx, sy, };
	}
	
	private static float[] createSymmetricTextureFloatArray(int xLoc, int yLoc, int texSize) {
		float dx = (float) xLoc / texSize;
		float dy = (float) yLoc / texSize;
		float stride = (float) 1 / texSize;
//		System.out.println(dx + " " + dy);
		return new float[] {
				// BOTTOM QUAD
				stride + dx, 0 + dy, 0 + dx, 0 + dy, 0 + dx, stride + dy, stride + dx, stride + dy,
				// TOP!
				0 + dx, 0 + dy, stride + dx, 0 + dy, stride + dx, stride + dy, 0 + dx, stride + dy,
				// FRONT QUAD
				dx, dy + stride, dx + stride, dy + stride, dx + stride, dy, dx, dy,
				// BACK QUAD
				dx, dy + stride, dx + stride, dy + stride, dx + stride, dy, dx, dy,
//				0 + dx, stride + dy, stride + dx, stride + dy, stride + dx, 0 + dy, 0 + dx, 0 + dy,
				// LEFT QUAD
				dx, dy + stride, dx + stride, dy + stride, dx + stride, dy, dx, dy,
//			0 + dx, stride + dy, stride + dx, stride + dy, stride + dx, 0 + dy, 0 + dx, 0 + dy,
				// RIGHT QUAD
				dx, dy + stride, dx + stride, dy + stride, dx + stride, dy, dx, dy, };
//			0 + dx, stride + dy, stride + dx, stride + dy, stride + dx, 0 + dy, 0 + dx, 0 + dy, };
	}
	
	private static IntBuffer createIndexBuffer() {
		IntBuffer iBuf = BufferUtils.createIntBuffer(4 * 6);
		iBuf.put(createIndices());
		iBuf.flip();
		return iBuf;
	}
	
	private static int[] createIndices() {
		int[] arr = new int[4 * 6];
		int i = 0;
		for (int face = 0; face < 6; face++) {
			for (int vertex = 0; vertex < 4; vertex++) {
				arr[i] = i;
				i++;
			}
		}
		return arr;
	}
	
	public void render(BlockType type, int x, int y, int z) {
		int id;
		switch (type) {
			case DIRT:
				id = dirtTextureID;
				break;
			case STONE:
				id = stoneTextureID;
				break;
			case GRASS:
				id = grassTextureID;
				break;
			case AIR:
				id = 0;
				break;
			default:
				id = stoneTextureID;
				break;
		
		}
		if (id == 0)
			return;
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		// Vertex array
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexBufferID);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		
		// Color array
//		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, colorBufferID);
//		GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
		
		// Texture array
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		if (id != 0)
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
		// Index array
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, indexBufferID);
		
		// Draw 'em up
		GL12.glDrawRangeElements(GL11.GL_QUADS, 0, 6 * 4, 6 * 4, GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
//		if (id == 0)
//			glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL11.glPopMatrix();
		
	}
	
	private void bufferData(int id, FloatBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		}
	}
	
	private void bufferElementData(int id, IntBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		}
	}
	
}
