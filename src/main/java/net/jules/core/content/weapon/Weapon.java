package net.jules.core.content.weapon;

public abstract sealed class Weapon permits RangedWeapon, MeleeWeapon {
	
	protected String displayName;
	protected int damage;
	
	//abstract functions
	/**
	 * Automatically executes code on left click (what else bro, what else)
	 */
	public abstract void onLeftClick();
	/**
	 * Automatically executes code on left click (what else bro, what else)
	 */
	public abstract void onRightClick();
	
	protected String getDisplayName() {
		return displayName;
	}
	
	protected int getDamage() {
		return damage;
	}
}