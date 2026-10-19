package net.jules.core.content.weapon;

import net.jules.core.scene.GeoBody;

public abstract sealed class Weapon permits RangedWeapon, MeleeWeapon {
	
	protected String displayName;
	protected int damage;
	protected GeoBody model;
	
	//abstract functions
	/**
	 * Per-frame necessary updates
	 */
	public abstract void update();
	
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
	
	protected void setModel(GeoBody model) {
		this.model = model;
	}
	
	protected GeoBody getModel() {
		return model;
	}
}