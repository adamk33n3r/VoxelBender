package net.adam_keenan.voxel.world;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import net.adam_keenan.voxel.Main;
import net.adam_keenan.voxel.utils.Physics;
import net.adam_keenan.voxel.world.Block.BlockType;

public class Player extends Entity {
	
	private final boolean FLY = false;
	
	private Camera camera;
	
	private boolean jumped;
	
	private int x1 = 0, y1 = 0, z1 = 0;
	
	private Vector3f nearVec = new Vector3f(0, 0, 0), farVec = new Vector3f();
	
	public Player(Arena arena, int x, int y, int z) {
		super(x, y, z);
		this.arena = arena;
		this.camera = new Camera(this, x, y, z);
		this.camera.setup();
	}
	
	private Block getBlockLookedAt() {
		Vector3f block = getScreenCenterRay();
		float x, y, z;
		x = block.x;
		y = block.y;
		z = block.z;
		camera.drawString(10, 150, String.format("(%s, %s, %s)", x, y, z));
		camera.drawString(10, 170, String.format("(%s, %s, %s)", (int) x, (int) y, (int) z));
		if (x != -1 && y != -1 && z != -1 && arena.inBounds((int) x, (int) y, (int) z)) {
			camera.drawString(400, 170, arena.blocks[(int) x][(int) y][(int) z].getType().toString());
			return arena.blocks[(int) x][(int) y][(int) z];
		}
		return new Block(-1, -1, -1, BlockType.OUTLINE);
	}
	
	private Vector3f getScreenCenterRay() {
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
		this.nearVec = nearVec;
		this.farVec = farVec;
		Ray ray = new Ray(nearVec, Vector3f.sub(farVec, nearVec, null).normalise(null), .1f);
		
		int i = 0;
		lbl: while (ray.distance < 100) {
			for (Block[][] blockX : arena.blocks) {
				for (Block[] blockY : blockX) {
					for (Block block : blockY) {
						if (!block.isWalkThroughable())
							if (block.contains(ray.pos)) {
								i++;
								break lbl;
							} else if (!arena.contains(ray.pos)) {
								ray.pos.set(-1, -1, -1);
								break lbl;
							}
					}
				}
			}
			ray.next();
		}
		if (i > 0) {
//			System.out.println("Found block! " + arena.blocks[(int) ray.pos.x][(int) ray.pos.y][(int) ray.pos.z].getType());
			x1 = (int) ray.pos.x;
			y1 = (int) ray.pos.y;
			z1 = (int) ray.pos.z;
		} else {
			x1 = 0;
			y1 = 0;
			z1 = 0;
		}
		return ray.pos;
	}
	
	private class Ray {
		
		Vector3f pos, dir, scaledDir;
		float distance;
		float scalar;
		
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
//			return String.format("Ray: Pos = (%s) Dir = (%s)", pos, dir);
			return String.format("Ray: Dir = (%s)", dir);
		}
	}
	
	public void processKeyboard(int delta) {
		boolean keyUp = false, keyDown = false, keyRight = false, keyLeft = false, keySpace = false, keyShift = false;
		
		keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
		keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
		keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
		keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
		keySpace = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		keyShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean keyP = Keyboard.isKeyDown(Keyboard.KEY_P);
		if (keyP)
			getBlockLookedAt();
		
		float dx = 0, dy = 0, dz = 0;
		float amount = delta * .003f;
		if (jumped)
			amount = delta * .0015f;
		dx += keyRight ? amount : 0;
		dx += keyLeft ? -amount : 0;
		dz += keyUp ? -amount : 0;
		dz += keyDown ? amount : 0;
		if (!FLY) {
			if (keySpace && !jumped) {
				jumped = true;
				System.out.println("Pressed jumped");
				fallSpeed = -.15f;
			}
			if (Physics.gravity(this, fallSpeed)) {
				fallSpeed += fallSpeed > 1.5f ? 0 : .01f;
				jumped = true;
			} else {
				fallSpeed = 0;
				jumped = false;
			}
			move(dx, dz);
		} else {
			if (keySpace)
				this.y += .1f;
			if (keyShift)
				this.y -= .1f;
			this.x += dx;
			this.y += dy;
			this.z += dz;
			camera.moveFromLook(dx, dy, dz);
		}
	}
	
	private void move(float dx, float dz) {
		Physics.moveWithCollisions(this, dx, dz);
	}
	
	public void processMouse() {
		camera.processMouse(.75f, 90, -90);
		this.yaw = camera.yaw;
	}
	
	@Override
	public void update() {
		camera.x = this.x;
		camera.y = this.y + 1.62f;
		camera.z = this.z;
		camera.update();
	}
	
	@Override
	public void render() {
		getBlockLookedAt();
		
		GL11.glColor4f(1, 1, 1, .5f);
		glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex3f(x1, y1 + 1, z1);
			GL11.glVertex3f(x1 + 1, y1 + 1, z1);
			GL11.glVertex3f(x1 + 1, y1 + 1, z1 + 1);
			GL11.glVertex3f(x1, y1 + 1, z1 + 1);
		}
		glEnd();
		
		GL11.glLineWidth(3);
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(nearVec.x, nearVec.y, nearVec.z);
			GL11.glVertex3f(farVec.x, farVec.y, farVec.z);
		}
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glColor3f(1, 1, 1);
		
		camera.drawDebug();
		camera.drawString(Main.WINDOW_WIDTH / 2 - 5, Main.WINDOW_HEIGHT / 2 - 15, "+");
		
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", x, y, z);
	}
	
}
