package net.adam_keenan.voxel.world.player;

import org.lwjgl.util.vector.Vector3f;

import net.adam_keenan.voxel.utils.GLShapes;
import net.adam_keenan.voxel.utils.RayTracer;
import net.adam_keenan.voxel.world.Entity;
import net.adam_keenan.voxel.world.Physics;
import net.adam_keenan.voxel.world.Block.BlockType;
import net.adam_keenan.voxel.world.player.BendingStyle.Element;

public class Projectile extends Entity {
	
	Player player;
	Element type;
	
	boolean attached;
	
	public Projectile(Player player, Element type) {
		super(player.x, player.y + 1.5f, player.z);
		this.player = player;
		this.arena = this.player.arena;
		this.type = type;
		this.attached = true;
	}
	
	@Override
	public void update() {
		if (attached) {
			fallSpeed = 0;
			Vector3f dir = RayTracer.getScreenCenterRay().dir;
			this.x = this.player.x + dir.x;
			this.y = this.player.y + 1.5f + dir.y;
			this.z = this.player.z + dir.z;
		} else {
			fallSpeed += .01f;
//			accel+=.05f;
			Physics.gravity(this);
			Physics.moveEntityMomentum(this, player);
		}
	}
	
	@Override
	public void render() {
		GLShapes.drawCube(BlockType.FIRE, x, y, z);
	}
	
}
