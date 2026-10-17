package net.jules.core.content.weapon;

public abstract non-sealed class RangedWeapon extends Weapon {
	private int magSize;
	private float shotInterval;
	
	/**
	 * Sets all necessary parameters
	 * @param displayName
	 * @param magSize
	 * @param shotInterval
	 * @param damage
	 */
	protected RangedWeapon(String displayName, int magSize, float shotInterval, int damage) {
		this.displayName = displayName;
		this.magSize = magSize;
		this.shotInterval = shotInterval;
		this.damage = damage;
	}
	
	protected int magSize() {
		return magSize;
	}
	
	protected float shotInterval() {
		return shotInterval;
	}

}
