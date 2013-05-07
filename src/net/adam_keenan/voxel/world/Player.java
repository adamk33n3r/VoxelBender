/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import net.adam_keenan.voxel.Main;
import net.adam_keenan.voxel.utils.Physics;
import net.adam_keenan.voxel.utils.Ray;
import net.adam_keenan.voxel.utils.RayTracer;
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
		Vector3f block = getBlock(RayTracer.getScreenCenterRay());
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
	
	
	
	public Vector3f getBlock(Ray ray) {
		lbl: while (ray.distance < 100) {
			for (Block[][] blockX : arena.blocks) {
				for (Block[] blockY : blockX) {
					for (Block block : blockY) {
						if (!block.isWalkThroughable())
							if (block.contains(ray.pos))
								break lbl;
							else if (!arena.contains(ray.pos)) {
								ray.pos.set(-1, -1, -1);
								break lbl;
							}
					}
				}
			}
			ray.next();
		}
		return ray.pos;
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
