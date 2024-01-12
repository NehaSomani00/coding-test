package com.lumen.fastivr.IVRUtils;

import static com.lumen.fastivr.IVRUtils.MainMenu.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MainMenuTest {
	
	private MainMenu mainMenu;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetMenuIndex() {
		mainMenu = ADMIN;
		mainMenu = FACS_INQUIRY;
		mainMenu = CABLE_TRANSFERS;
		mainMenu = CHANGE_PAIR_STATUS;
		mainMenu = CHANGE_SERVING_TERMINAL;
		mainMenu = CUT_TO_NEW_FACILITIES;
		
		int menuIndex = mainMenu.getMenuIndex();
		
		assertEquals(3, menuIndex);
	}

}
