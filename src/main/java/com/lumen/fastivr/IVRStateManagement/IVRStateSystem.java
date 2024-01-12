package com.lumen.fastivr.IVRStateManagement;

import com.lumen.fastivr.IVRUtils.IVRConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.lumen.fastivr.IVRCANST.utils.IVRCANSTConstants.*;
import static com.lumen.fastivr.IVRCNF.utils.IVRCNFConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRConstants.*;

/**
 * This class is for more complex state transitions, 
 * where based on user input (DTMF), the next state change depends.
 * But here, most state changes are direct state transfers, so we are 
 * using a static map to maintain the changes
 * @author 002L2N744
 *
 */
@Service
public class IVRStateSystem {
	
	private Map<String,IVRState> stateMap;
 	
	public IVRStateSystem() {
		loadIVRStateConfiguration();
	}
	
	public Map<String, IVRState> getStateMap() {
		return stateMap;
	}
	
	
	public void loadIVRStateConfiguration() {
		stateMap = new HashMap<>();
		
		//Sign on
		loadSignonStates();

		//LFACS Flow
		loadFACSInquiryStates();
		
		//MLT Flow
		loadMLTStates();
		
		//Admin Flow
		loadAdminStates();
		
		//CNF FLow
		loadCNFStates();
		
		//load ChangeCablePairStatus Flow.
		loadChangeStatusCablePair();
		
		//construction flow
		loadConstructionActivityStates();
		
		// Change or Assign new Serving Terminal flow
		loadChangeOrAssignNewServingTerminalStates();
		
	}

	private void loadCNFStates() {
		addIVRStateChangePattern(STATE_FN0030, STATE_FND035, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0055, STATE_FND055, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0056, STATE_FND059, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0070, STATE_FND075, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0080, STATE_FND085, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FNE090, STATE_FND085, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FNE091, STATE_FND085, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0110, STATE_FND135, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0130, STATE_FND135, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0180, STATE_FND135, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0140, STATE_FND141, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0140, STATE_FND145, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0143, STATE_FND145, DIRECT_STATE_TRANSFER);

		addIVRStateChangePattern(STATE_FN0150, STATE_FND155, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0150, STATE_FND155, DTMF_INPUT_2);
		
		addIVRStateChangePattern(STATE_FN0160, STATE_FND170, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0700, STATE_FND700, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0701, STATE_FND700, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FN0730, STATE_FND740, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0745, STATE_FND746, DTMF_INPUT_3);
	}

	private void loadSignonStates() {
		addIVRStateChangePattern(STATE_SS0110, STATE_SSD110, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0120, STATE_SSD120, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0135, STATE_SSD135, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_SS0180, STATE_SSD180, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_SS0190, STATE_SSD190, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0400, STATE_SSD150, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_SS0150, STATE_SSD150, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_SS0210, STATE_SSD210, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0300, STATE_SSD300, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0160, STATE_SSD160, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SS0165, STATE_SSD165, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_SS0170, STATE_SSD170, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SSE320, STATE_SSD220, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_SSE322, STATE_SSD300, DIRECT_STATE_TRANSFER);
	}

	private void loadAdminStates() {
		addIVRStateChangePattern(STATE_AD0020,STATE_ADD020,DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_AD0035,STATE_ADD035,DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_MM0001,STATE_ADD010,DTMF_INPUT_8);
		addIVRStateChangePattern(STATE_ADE110,STATE_ADD010,DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_AD0011,STATE_ADD100,DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_AD0012,STATE_ADD100,DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_AD0500,STATE_ADD100,DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_AD0110,STATE_ADD110,DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_AD0115,STATE_ADD500,DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_AD0011,STATE_ADD550,DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_AD0012,STATE_ADD550,DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_AD0011,STATE_ADD555,DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_AD0012,STATE_ADD555,DTMF_INPUT_4);
		
	}


	private void loadMLTStates() {
		loadQuickFullLoopStateChanges();
		
		loadVoiceTestResultsStateChanges();
		
		loadAddToneStateChanges();
		
		loadRemoveToneStateChanges();
		
	}

	private void loadRemoveToneStateChanges() {
		addIVRStateChangePattern(STATE_ML0500, STATE_MLD500, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0501, STATE_MLD510, DTMF_INPUT_1);
	}

	private void loadAddToneStateChanges() {
		addIVRStateChangePattern(STATE_ML0300, STATE_MLD300, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0305, STATE_MLD307, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0307, STATE_MLD310, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_ML0307, STATE_MLD310, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_ML0340,STATE_MLD340, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0330,STATE_MLD340, DTMF_INPUT_2);
	}

	private void loadVoiceTestResultsStateChanges() {
		addIVRStateChangePattern(STATE_ML0060, STATE_MLD075, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0070, STATE_MLD075, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0082, STATE_MLD084, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0080, STATE_MLD082, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0084, STATE_MLD086, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0086, STATE_MLD088, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0088, STATE_MLD090, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0090, STATE_MLD092, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0092, STATE_MLD094, DIRECT_STATE_TRANSFER);
	}

	private void loadQuickFullLoopStateChanges() {
		addIVRStateChangePattern(STATE_ML0020, STATE_MLD021, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0025, STATE_MLD026, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_ML0025, STATE_MLD026, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_ML0110, STATE_MLD027, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_ML0030, STATE_MLD040, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_ML0040, STATE_MLD040, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_ML0110, STATE_MLD027, DTMF_INPUT_2);
	}

	private void loadFACSInquiryStates() {
		
		addIVRStateChangePattern(STATE_FI0220, STATE_FID224, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0015, STATE_FID020, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0030, STATE_FID035, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0410, STATE_FID420, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0411, STATE_FID420, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0412, STATE_FID420, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0413, STATE_FID420, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0421, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0422, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0423, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0424, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0425, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0426, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0427, STATE_FID429, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0430, STATE_FID420, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FI0441, STATE_FID400, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0442, STATE_FID400, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0443, STATE_FID400, DTMF_INPUT_5);
		
		addIVRStateChangePattern(STATE_FI0441, STATE_FID455, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0442, STATE_FID455, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0443, STATE_FID455, DTMF_INPUT_4);		
		addIVRStateChangePattern(STATE_FI0441, STATE_FID445, DTMF_INPUT_1);	
		addIVRStateChangePattern(STATE_FI0442, STATE_FID445, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0442, STATE_FID445, DTMF_INPUT_2);
		
		addIVRStateChangePattern(STATE_FI0443, STATE_FID445, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0443, STATE_FID445, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0443, STATE_FID445, DTMF_INPUT_3);		
		addIVRStateChangePattern(STATE_FI0450, STATE_FID445, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0460, STATE_FID445, DIRECT_STATE_TRANSFER);
		
		
		addIVRStateChangePattern(STATE_FI0010, STATE_FID011, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0025, STATE_FID025, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0030, STATE_FID045, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0030, STATE_FID600, DTMF_INPUT_4);		
		addIVRStateChangePattern(STATE_FI0040, STATE_FID400, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0275, STATE_FID400, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0030, STATE_FID060, DTMF_INPUT_3);
		
		addIVRStateChangePattern(STATE_FI0510, STATE_FID515, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0521, STATE_FID525, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0530, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0530, STATE_FID535, DTMF_INPUT_10);

		addIVRStateChangePattern(STATE_FI0210, STATE_FID211, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0235, STATE_FID237, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FI0050, STATE_FID500, DTMF_INPUT_1); 
		addIVRStateChangePattern(STATE_FI0050, STATE_FID500, DTMF_INPUT_2); 
		addIVRStateChangePattern(STATE_FI0050, STATE_FID500, DTMF_INPUT_3); 
		addIVRStateChangePattern(STATE_FI0050, STATE_FID500, DTMF_INPUT_4); 
		addIVRStateChangePattern(STATE_FI0051, STATE_FID500, DTMF_INPUT_1); 
		addIVRStateChangePattern(STATE_FI0051, STATE_FID500, DTMF_INPUT_2); 
		addIVRStateChangePattern(STATE_FI0051, STATE_FID500, DTMF_INPUT_3); 

		
		addIVRStateChangePattern(STATE_FI0441, STATE_FID560, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FI0442, STATE_FID560, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FI0443, STATE_FID560, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FI0551, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0552, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0553, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0631, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0636, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0637, STATE_FID560, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0720, STATE_FID560, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0632, STATE_FID560, DTMF_INPUT_5);
		

		addIVRStateChangePattern(STATE_FI0065, STATE_FID068, DTMF_INPUT_1); 
		addIVRStateChangePattern(STATE_FI0065, STATE_FID068, DTMF_INPUT_2); 
		addIVRStateChangePattern(STATE_FI0065, STATE_FID068, DTMF_INPUT_3); 
		
		addIVRStateChangePattern(STATE_FI0240, STATE_FID250, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0240, STATE_FID250, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0240, STATE_FID250, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0240, STATE_FID250, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FI0240, STATE_FID273, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0240, STATE_FID274, DTMF_INPUT_6);

		addIVRStateChangePattern(STATE_FI0030, STATE_FID090, DTMF_INPUT_5);	
		addIVRStateChangePattern(STATE_FI0529, STATE_FID525, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0528, STATE_FID525, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0526, STATE_FID525, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0527, STATE_FID525, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0552, STATE_FID525, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0552, STATE_FID525, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0552, STATE_FID525, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0551, STATE_FID525, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0551, STATE_FID525, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0551, STATE_FID525, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0551, STATE_FID525, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0553, STATE_FID525, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0553, STATE_FID525, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0553, STATE_FID525, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0554, STATE_FID525, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FI0636, STATE_FID630, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0636, STATE_FID630, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0636, STATE_FID630, DTMF_INPUT_4);
		
		addIVRStateChangePattern(STATE_FI0637, STATE_FID630, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0637, STATE_FID630, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0637, STATE_FID630, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0637, STATE_FID630, DTMF_INPUT_4);
		
		
		addIVRStateChangePattern(STATE_FI0627, STATE_FID630, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0631, STATE_FID630, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0632, STATE_FID630, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0632, STATE_FID630, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0632, STATE_FID630, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FI0634, STATE_FID630, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0633, STATE_FID630, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0630, STATE_FID630, DTMF_INPUT_10);
		//addIVRStateChangePattern(STATE_FI0279, STATE_FID291, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FI0280, STATE_FID291, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FI0610, STATE_FID615, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FI0630, STATE_FID635, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0633, STATE_FID635, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0634, STATE_FID635, DTMF_INPUT_0);
		
		addIVRStateChangePattern(STATE_FI0279, STATE_FID282, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FI0095, STATE_FID700, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FI0310, STATE_FID700, DTMF_INPUT_2);
		
		addIVRStateChangePattern(STATE_FI0536, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0537, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0538, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0539, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0540, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0541, STATE_FID532, DTMF_INPUT_0);
		addIVRStateChangePattern(STATE_FI0542, STATE_FID532, DTMF_INPUT_0);
		
		addIVRStateChangePattern(STATE_FI0536, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0537, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0538, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0539, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0540, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0541, STATE_FID525, DTMF_INPUT_10);
		addIVRStateChangePattern(STATE_FI0542, STATE_FID525, DTMF_INPUT_10);
		
		//CNF FLow
		addIVRStateChangePattern(STATE_FN0030, STATE_FND035, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0055, STATE_FND055, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0056, STATE_FND059, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0070, STATE_FND075, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0080, STATE_FND085, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FNE090, STATE_FND085, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FNE091, STATE_FND085, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0110, STATE_FND135, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0180, STATE_FND135, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0140, STATE_FND141, DTMF_INPUT_1);
		
		//TODO Impl
		addIVRStateChangePattern(STATE_FN0140, STATE_FND141, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0140, STATE_FND145, DTMF_INPUT_2);

		addIVRStateChangePattern(STATE_FN0150, STATE_FND155, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0150, STATE_FND155, DTMF_INPUT_2);
		
		addIVRStateChangePattern(STATE_FN0160, STATE_FND170, DTMF_INPUT_1);
		
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0190, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0200, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0210, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0211, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0212, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_3);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_6);
		addIVRStateChangePattern(STATE_FN0213, STATE_FND215, DTMF_INPUT_7);
		
		addIVRStateChangePattern(STATE_FN0700, STATE_FND700, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0701, STATE_FND700, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FN0730, STATE_FND740, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FN0745, STATE_FND746, DTMF_INPUT_2);
		
	}
	
//	private void loadMltStateConfiguration() {
//		addIVRStateChangePattern(STATE_ML0020, STATE_MLD021, DIRECT_STATE_TRANSFER);
//		addIVRStateChangePattern(STATE_ML0025, STATE_MLD026, DTMF_INPUT_1);
//		addIVRStateChangePattern(STATE_ML0025, STATE_MLD026, DTMF_INPUT_3);
//		addIVRStateChangePattern(STATE_ML0060, STATE_MLD075, DIRECT_STATE_TRANSFER);
//		addIVRStateChangePattern(STATE_ML0070, STATE_MLD075, DIRECT_STATE_TRANSFER);
//		addIVRStateChangePattern(STATE_ML0082, STATE_MLD084, DIRECT_STATE_TRANSFER);
//		addIVRStateChangePattern(STATE_ML0080, STATE_MLD082, DIRECT_STATE_TRANSFER);
//		
//	}
	
	private void loadChangeStatusCablePair() {
		addIVRStateChangePattern(STATE_MM0001, STATE_FPD005, DTMF_INPUT_4);
		addIVRStateChangePattern(IVRConstants.STATE_FND741, STATE_FPD005, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0010, STATE_FPD011, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0040, STATE_FPD060, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0050, STATE_FPD060, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0020, STATE_FPD020, DIRECT_STATE_TRANSFER);
		
	}
	
	private void loadConstructionActivityStates() {
		addIVRStateChangePattern(STATE_CT0049, STATE_CTD500, DTMF_INPUT_5);
		addIVRStateChangePattern(STATE_CT0049, STATE_CTD400, DTMF_INPUT_4);
		addIVRStateChangePattern(STATE_CT0400, STATE_CTD403, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_CT0405, STATE_CTD410, DTMF_INPUT_1);
	}
	
	private void loadChangeOrAssignNewServingTerminalStates() {
		
		addIVRStateChangePattern(STATE_FT0010, STATE_FTD011, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0020, STATE_FTD030, DTMF_INPUT_1); 
		addIVRStateChangePattern(STATE_FT0055, STATE_FTD060, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0100, STATE_FTD120, DTMF_INPUT_1); 
		addIVRStateChangePattern(STATE_FT0130, STATE_FTD135, DIRECT_STATE_TRANSFER); 
		addIVRStateChangePattern(STATE_FT0150, STATE_FTD160, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FT0180, STATE_FTD190, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FT0195, STATE_FTD197, DTMF_INPUT_2);
		addIVRStateChangePattern(STATE_FT0200, STATE_FTD210, DIRECT_STATE_TRANSFER);// TODO check what to give in 3rd Param
		addIVRStateChangePattern(STATE_FT0310, STATE_FTD315, DIRECT_STATE_TRANSFER);

		addIVRStateChangePattern(STATE_FT0232, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0233, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0234, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0235, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0236, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0237, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0238, STATE_FTD240, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0230, STATE_FTD231, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FT0195, STATE_FTD400, DTMF_INPUT_2);

		addIVRStateChangePattern(STATE_FT0350, STATE_FTD351, DIRECT_STATE_TRANSFER);
		
		addIVRStateChangePattern(STATE_FT0365, STATE_FTD370, DTMF_INPUT_1);
		addIVRStateChangePattern(STATE_FTD370, STATE_FTD371, DTMF_INPUT_0); 

	}
	
	private void loadCallerIdTest() {
		addIVRStateChangePattern(STATE_MM0001, STATE_FPD005, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(IVRConstants.STATE_FND741, STATE_FPD005, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0010, STATE_FPD011, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0040, STATE_FPD060, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0050, STATE_FPD060, DIRECT_STATE_TRANSFER);
		addIVRStateChangePattern(STATE_FP0020, STATE_FPD020, DIRECT_STATE_TRANSFER);
		
	}
	
	public void addIVRStateChangePattern(String genesysState, String fastState, String pattern) {
		IVRState genesysIvrState = null;
		if(stateMap.containsKey(genesysState)) {
			genesysIvrState = stateMap.get(genesysState);
	    } else {
	    	genesysIvrState = new IVRState(genesysState);
	    }
	    IVRState fastIvrState = new IVRState(fastState);
	    IVRStateTransition stateTransition = new IVRStateTransition(fastIvrState, pattern);
	    genesysIvrState.addTransitions(stateTransition);
	    stateMap.put(genesysState, genesysIvrState);
	    //stateMap.put(fastState, fastIvrState);
	}
}