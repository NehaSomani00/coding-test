package com.lumen.fastivr.IVRMLT.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.IVRParameter;
import com.lumen.fastivr.IVRMLT.entity.IvrMltSession;
import com.lumen.fastivr.IVRMLT.utils.IVRMltUtilities;

@ExtendWith(MockitoExtension.class)
class IvrMltHelperTest {

	@InjectMocks
	private IvrMltHelper ivrMltHelper;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void findTestCategoryTest() {
		String test = "1";
		String response = ivrMltHelper.findTestCategory(test);
		assertEquals(IVRMltUtilities.QUICKX_TEST, response);

		String test1 = "2";
		String response1 = ivrMltHelper.findTestCategory(test1);
		assertEquals(IVRMltUtilities.LOOPX_TEST, response1);

		String test2 = "3";
		String response2 = ivrMltHelper.findTestCategory(test2);
		assertEquals(IVRMltUtilities.FULLX_TEST, response2);

		String test3 = "4";
		String response3 = ivrMltHelper.findTestCategory(test3);
		assertEquals(IVRMltUtilities.TONE_PLUS_TEST, response3);

		String test4 = "5";
		String response4 = ivrMltHelper.findTestCategory(test4);
		assertEquals(IVRMltUtilities.TONE_REMOVAL_TEST, response4);

	}

	@Test
	void getOverrideTypeTest() {
		String test = "1";
		String response = ivrMltHelper.getOverrideType(test);
		assertEquals(IVRMltUtilities.N, response);

		String test1 = "2";
		String response1 = ivrMltHelper.getOverrideType(test1);
		assertEquals(IVRMltUtilities.C, response1);

		String test2 = "3";
		String response2 = ivrMltHelper.getOverrideType(test2);
		assertEquals(IVRMltUtilities.O, response2);

		String test3 = "4";
		String response3 = ivrMltHelper.getOverrideType(test3);
		assertEquals(IVRMltUtilities.P, response3);

		String test4 = "5";
		String response4 = ivrMltHelper.getOverrideType(test4);
		assertEquals(IVRMltUtilities.T, response4);

		String test5 = "6";
		String response5 = ivrMltHelper.getOverrideType(test5);
		assertEquals(IVRMltUtilities.Y, response5);

	}

	@Test
	void voltageInformationRequiredTest() {
		// Test case 1: rcsVerCode = "6"
		boolean response1 = ivrMltHelper.voltageInformationRequired("6");
		assertEquals(false, response1);

		// Test case 2: rcsVerCode = "61"
		boolean response2 = ivrMltHelper.voltageInformationRequired("61");
		assertEquals(false, response2);

		// Test case 3: rcsVerCode = "71"
		boolean response3 = ivrMltHelper.voltageInformationRequired("71");
		assertEquals(false, response3);

		// Test case 4: rcsVerCode = "72"
		boolean response4 = ivrMltHelper.voltageInformationRequired("72");
		assertEquals(false, response4);

		// Test case 5: rcsVerCode = "B0"
		boolean response5 = ivrMltHelper.voltageInformationRequired("B0");
		assertEquals(false, response5);

		// Test case 6: rcsVerCode = "B6"
		boolean response6 = ivrMltHelper.voltageInformationRequired("B6");
		assertEquals(false, response6);

		// Test case 7: rcsVerCode = "E0"
		boolean response7 = ivrMltHelper.voltageInformationRequired("E0");
		assertEquals(false, response7);

		// Test case 8: rcsVerCode = "E7"
		boolean response8 = ivrMltHelper.voltageInformationRequired("E7");
		assertEquals(false, response8);

		// Test case 9: rcsVerCode = "F0"
		boolean response9 = ivrMltHelper.voltageInformationRequired("F0");
		assertEquals(false, response9);

		// Test case 10: rcsVerCode = "F2"
		boolean response10 = ivrMltHelper.voltageInformationRequired("F2");
		assertEquals(false, response10);

		// Test case 11: rcsVerCode = "ND"
		boolean response11 = ivrMltHelper.voltageInformationRequired("ND");
		assertEquals(false, response11);

		// Test case 12: rcsVerCode = "NS"
		boolean response12 = ivrMltHelper.voltageInformationRequired("NS");
		assertEquals(false, response12);

		// Test case 13: rcsVerCode = "NT"
		boolean response13 = ivrMltHelper.voltageInformationRequired("NT");
		assertEquals(false, response13);

		// Test case 14: rcsVerCode = "NV"
		boolean response14 = ivrMltHelper.voltageInformationRequired("NV");
		assertEquals(false, response14);

		// Test case 15: rcsVerCode = "SP"
		boolean response15 = ivrMltHelper.voltageInformationRequired("SP");
		assertEquals(false, response15);

		// Test case 16: rcsVerCode = "ABC"
		boolean response16 = ivrMltHelper.voltageInformationRequired("ABC");
		assertEquals(true, response16);

		// Test case 17: rcsVerCode = ""
		boolean response17 = ivrMltHelper.voltageInformationRequired("");
		assertEquals(true, response17);

		// Test case 18: rcsVerCode = ""
		boolean response18 = ivrMltHelper.voltageInformationRequired("TEST");
		assertEquals(true, response18);
	}

	@Test
	void addParamterDataTest() {
		// Test case 1: Single parameter
		List<IVRParameter> response1 = ivrMltHelper.addParamterData("param1");
		assertEquals(1, response1.size());
		assertEquals("param1", response1.get(0).getData());

		// Test case 2: Multiple parameters
		List<IVRParameter> response2 = ivrMltHelper.addParamterData("param1", "param2", "param3");
		assertEquals(3, response2.size());
		assertEquals("param1", response2.get(0).getData());
		assertEquals("param2", response2.get(1).getData());
		assertEquals("param3", response2.get(2).getData());

		// Test case 3: No parameters
		List<IVRParameter> response3 = ivrMltHelper.addParamterData();
		assertEquals(0, response3.size());
	}

	@Test
	void resetTestFieldsTest() {
		// Create a mock IvrMltSession object
		IvrMltSession mltSession = Mockito.mock(IvrMltSession.class);

		// Call the method to be tested
		ivrMltHelper.resetTestFields(mltSession);

		// Verify that the testRequestProxyUrl is reset to an empty string
		Mockito.verify(mltSession).setTestRequestProxyUrl("");

		// Verify that the datachannelId is reset to an empty string
		Mockito.verify(mltSession).setDatachannelId("");

		// Verify that the dataChannelProxyUrl is reset to an empty string
		Mockito.verify(mltSession).setDataChannelProxyUrl("");

		// Verify that the mltTestResult is set to null
		Mockito.verify(mltSession).setMltTestResult(null);
	}
}