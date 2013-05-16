/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel.world.player;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;


import net.adam_keenan.voxel.hud.HUD;
import net.adam_keenan.voxel.utils.Ray;
import net.adam_keenan.voxel.utils.RayTracer;
import net.adam_keenan.voxel.world.Arena;
import net.adam_keenan.voxel.world.Block;
import net.adam_keenan.voxel.world.Entity;
import net.adam_keenan.voxel.world.Physics;
import net.adam_keenan.voxel.world.Block.BlockType;
import net.adam_keenan.voxel.world.player.BendingStyle.Element;

public class Player extends Entity {
	
	BendingStyle style;
	
	private final boolean FLY = false;
	
	private Camera camera;
	
	private boolean jumped;
	private boolean mouseChanged, keyboardChanged;
	
	Block curBlock;
	Vector3f curBlockVec;
	private int lastSearch;
	
	Projectile projectile;
	private int power;
	
	private int x1 = 0, y1 = 0, z1 = 0;
	private final int SIZE = 1;
	
	public Player(Element type, Arena arena, int x, int y, int z) {
		super(x + .5f, y, z + .5f);
		this.arena = arena;
		this.camera = new Camera(this, x, y, z);
		this.camera.setup();
		curBlock = new Block(0, 0, 0, null);
		curBlockVec = new Vector3f();
		this.style = new BendingStyle(this, type);
		this.projectile = this.style.conjure();
		arena.addProjectile(projectile);
		
//		JBulletPhysics.addCube(1);
		
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	private Block getBlockLookedAt() {
		lastSearch++;
		if ((mouseChanged || keyboardChanged) && lastSearch > 5) {
			curBlockVec = getBlock(RayTracer.getScreenCenterRay());
			lastSearch = 0;
		}
		float x, y, z;
		x = curBlockVec.x;
		y = curBlockVec.y;
		z = curBlockVec.z;
		camera.drawString(10, 150, String.format("(%s, %s, %s)", x, y, z));
		camera.drawString(10, 170, String.format("(%s, %s, %s)", (int) x, (int) y, (int) z));
		if (x != -1 && y != -1 && z != -1 && arena.inBounds((int) x, (int) y, (int) z)) {
			camera.drawString(400, 170, arena.blocks[(int) x][(int) y][(int) z].getType().toString());
			return arena.blocks[(int) x][(int) y][(int) z];
		}
		return new Block(-1, -1, -1, BlockType.OUTLINE);
	}
	
	private Vector3f getBlock(Ray ray) {
		int i = 0;
		lbl: while (ray.distance < 10) {
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
//			System.out.println("Found block! " + arena.blocks[(int) ray.pos.x][(int) ray.pos.y][(int) ray.pos.z].getType()+ray.pos);
			x1 = (int) ray.pos.x;
			y1 = (int) ray.pos.y;
			z1 = (int) ray.pos.z;
		} else {
			x1 = 0;
			y1 = 0;
			z1 = 0;
			ray.pos.set(-1, -1, -1);
		}
		return ray.pos;
	}
	
	public void processKeyboard(int delta) {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_O) && this.projectile.attached) {
			this.projectile.attached = false;
			this.projectile.momentum = new Vector3f((float)power / 20, (float)power / 20, (float)power / 20);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
//			projectile.attached = true;
			if (!this.projectile.attached) {
				this.projectile = this.style.conjure();
				arena.addProjectile(this.projectile);
			}
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN)
					power -= power == 0 ? 0 : 1;
				if (Keyboard.getEventKey() == Keyboard.KEY_UP)
					power += power == 10 ? 0 : 1;
			}
		}
		
		boolean keyUp = false, keyDown = false, keyRight = false, keyLeft = false, keySpace = false, keyShift = false;
		
		keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
		keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
		keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		keySpace = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		keyShift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		
		float dx = 0, dy = 0, dz = 0;
		float amount = delta * .003f;
//		if (jumped)
//			amount = delta * .0015f;
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
//			Transform trans = new Transform();
//			JBulletPhysics.getCube(0).getMotionState().getWorldTransform(trans);
//			System.out.println(trans.origin.y);
//			this.y = trans.origin.y;
			if (Physics.gravity(this)) {
				keyboardChanged = true;
				fallSpeed += fallSpeed > 1.5f ? 0 : .01f;
				jumped = true;
			} else {
				keyboardChanged = false;
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
		if (dx == 0 && dz == 0) {
			keyboardChanged = false;
			return;
		}
		keyboardChanged = true;
		Physics.moveWithCollisions(this, dx, dz, null);
	}
	
	public void processMouse() {
		if (Mouse.isButtonDown(0))
			curBlock.move(0, -.1f, 0);
		if (Mouse.isButtonDown(1)) {
			this.projectile.attached = false;
			this.projectile.momentum = RayTracer.getScreenCenterRay().dir;
			this.projectile.speed = (float)power / 20;
		}
		if (Mouse.hasWheel()) {
			int wheel = Mouse.getDWheel();
			if (wheel != 0)
//			System.out.println(wheel);
			if (wheel < 0)
				power -= power == 0 ? 0 : 1;
			if (wheel > 0)
				power += power == 10 ? 0 : 1;
		}
		mouseChanged = camera.processMouse(.75f, 90, -80);
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
		curBlock = getBlockLookedAt();
		GL11.glColor4f(1, 1, 1, .5f);
		for (int row = 0; row < SIZE; row++)
			for (int col = 0; col < SIZE; col++) {
				glBegin(GL11.GL_QUADS);
				{
					GL11.glVertex3f(x1, y1 + 1, col + z1);
					GL11.glVertex3f(x1 + 1, y1 + 1, col + z1);
					GL11.glVertex3f(x1 + 1, y1 + 1, col + z1 + 1);
					GL11.glVertex3f(x1, y1 + 1, col + z1 + 1);
				}
				glEnd();
			}
		GL11.glColor3f(1, 1, 1);
		camera.drawDebug();
		camera.drawString(100, 300, String.format("%f, %f, %f", projectile.x, projectile.y, projectile.z));
		camera.drawString(Display.getWidth() - 200, Display.getHeight() - 20, String.format("Power: %s", power));
		HUD.drawCrosshairs();
		
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", x, y, z);
	}
	
}
