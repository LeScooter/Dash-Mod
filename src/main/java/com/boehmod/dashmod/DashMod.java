package com.boehmod.dashmod;

import net.fabricmc.api.ModInitializer;

public class DashMod implements ModInitializer {

	/**
	 * Necessary attributes for this mod
	 */
	public static final String MOD_NAME = "DashMod";
	/**
	 * Static instance of this mod container
	 */
	private static DashMod DASH_MOD;

	/**
	 * Default Constructor for the {@link DashMod} container
	 */
	public DashMod() {
		DASH_MOD = this;
	}

	/**
	 * Get Instance - Fetches the static instance of the {@link DashMod} mod container
	 *
	 * @return - Returned static {@link DashMod} instance
	 */
	public static DashMod getInstance() {
		return DASH_MOD;
	}

	/**
	 * On Initialize - Called whilst initializing the mod
	 */
	@Override
	public void onInitialize() {
		//Inform user that the mod has loaded successfully
		System.out.println(String.format("%s has loaded successful!", MOD_NAME));
	}
}