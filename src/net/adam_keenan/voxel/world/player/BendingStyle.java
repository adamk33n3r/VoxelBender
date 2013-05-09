package net.adam_keenan.voxel.world.player;


public class BendingStyle {
	
	public enum Element {
		AIR, EARTH, WATER, FIRE
	}
	
	private Player player;
	private Element element;
	
	public BendingStyle(Player player, Element element) {
		this.player = player;
		this.element = element;
	}
	
	public Projectile conjure() {
		switch(element) {
			case AIR:
			case FIRE:
				return new Projectile(player, element);
			default:
				return null;
		}
	}
	
}
