package com.lumen.fastivr.IVRUtils;

public enum MainMenu {
	
	FACS_INQUIRY(1),
	MLT(2),
	CUT_TO_NEW_FACILITIES(3),
	CHANGE_PAIR_STATUS(4),
	RINGBACK_CALLER_ID(5),
	CHANGE_SERVING_TERMINAL(6),
	CABLE_TRANSFERS(7),
	ADMIN(8),
	NEWS(9);
	
	private int menuIndex;
	
	private MainMenu(int menuIndex) {
		this.menuIndex = menuIndex;
	}
	
	public int getMenuIndex() {
		return menuIndex;
	}
	

}
