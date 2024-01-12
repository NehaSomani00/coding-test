package com.lumen.fastivr.IVRUtils;

public final class IVRConstants {
	
	private IVRConstants() {}
	
	public static final String API_KEY = "fastivr";
	public static final String SECRET = "fastivr";

	public static final String AREA_CODE_SAME_AS_OLD = "New area code same as old area code";
	public static final String AREA_CODE_FOUND = "Area code found";

	public static final String BAD_INPUT_MESSAGE = "Request Parameters are invalid, Please check State & User DTMF Input";
	public static final String MISSING_SESSION_ID = "Session id not present in Request";
	public static final String INACTIVE_STATE = "This State is not active in FASTIVR, Please check the State code";
	public static final String FASTIVR_BACKEND_ERR = "FASTIVR is down, Please check with FASTIVR team";
	public static final String GPDOWN_ERR_MSG = "Problem with FAS Interface";
	
	public static final String RETURN_TO_MAIN_MENU = "Return to main menu";
	public static final String GPDOWN_ERR_MSG_CODE = "500";
	public static final String INVALID_SESSION_ID = "In-Valid Session id, please try with a loaded Session";
	public static final String PASSWORD_WRONG_LENGTH = "Wrong Length";
	public static final String PASSWORD_SAME_AS_OLD = "New Password is same as Old password";
	public static final String PASSWORD_SAME_AS_SECURITYCODE = "New Password is same as Security Code";
	public static final String VALID_DATE_FORMAT = "Date Format Is Valid";
	public static final String INVALID_DATE_FORMAT = "Date Format Is InValid";
	public static final String VALID_OTP = "Valid OTP";
	public static final String INVALID_OTP = "OTP is not VALID";
	public static final String OTP_GEN_FAILED = "OTP generation Failed";
	public static final String VARIABLE_AREA_CODE_NOT_PRESENT = "Technician enters Area Code on each call";
	public static final String VARIABLE_AREA_CODE_SAME_AS_DEFAULT = "Technician has Default Area Code";

	public static final String UNREACHABLE_NEXT_STATE = "Cannot Obtain next State based on User Inputs";

	public static final String NO_ALPHA_PAGER = "CanBePagedEmail and CanBePagedMobile both are false, So We can take current assignment result by voice";

	public static final String OTP_ADDITIONAL_MESSAGE = " is your One Time Password. Do not share this OTP with anyone. Regards, FAST";
	public static final String OTP_MESSAGE_SUBJECT = "FAST OTP";
	public static final String VALID_PAGER_MESSAGE =  "VALID Pager Configuration";
	public static final String INVALID_PAGER_MESSAGE =  "INVALID Pager Configuration";

	public static final String BAD_USER_INPUT_ERR_CODE = "100";
	public static final String BUSINESS_LOGIC_ERR_CODE = "200";

	
	public static final String NET_PHONE_DEVICE = "PHONE";
	public static final String NET_MAIL_DEVICE = "MAIL";
	public static final String NETPAGE_SUBJECT_ADDL_LINE = "FAST Additional Lines Inquiry Result";
	public static final String NETPAGE_SUBJECT_SPARE_PAIR = "FAST Spare Pairs Inquiry Result";
	public static final String NETPAGE_SUBJECT_COE = "FAST Central Office Equipment Inquiry Result";
	public static final String NETPAGE_SUBJECT_DEFECTIVE_PAIRS = "FAST Defective Pairs Result";
	public static final String NETPAGE_SUBJECT_CURRENT_ASSIGNMENT = "FAST Current Assignment Inquiry Result";
	public static final String NETPAGE_SUBJECT_MULTIPLE_APPEARANCES = "FAST Multiple Appearances Inquiry Result";
	
	public static final String NETPAGE_SUBJECT_FACS_INQUIRY_BY_TN = "FACS Inquiry by Telephone Number";
	public static final String NETPAGE_SUBJECT_FACS_INQUIRY_BY_CP = "FACS Inquiry by Cable Pair";
	
	public static final String INVALID_AREA_CODE =  "Invalid area code";
	public static final String OK =  "OK";
	public static final String UPDATE_AREA_CODE =  "Area Code Updated Successfully";
	public static final String VALID_AREA_CODE =  "Valid area code";
	public static final String VALID_TN_MSG = "Valid TN Found";
	public static final String VALID_TN_MSG_CODE = "1";
	public static final String TN_NOT_IN_TABLE = "TN not in table";
	public static final String TN_NOT_IN_TABLE_CODE = "0";
	
	public static final int MAX_PASSWORD_ATTEMPTS = 5;
	
	//endpoint security
	public static final String ENDPOINT_SECURITY_SUCCESS_ADD = "User added Successfully";
	public static final String ENDPOINT_SECURITY_SUCCESS_ADD_ERR = "User cannot be Added";
	public static final String ENDPOINT_SECURITY_SUCCESS_UPD = "User updated Successfully";
	public static final String ENDPOINT_SECURITY_SUCCESS_UPD_ERR = "User cannot be Updated";
	
	//Inquiry Type
	public static final String INQUIRY_BY_TN = "TN";
	public static final String INQUIRY_BY_CABLE_PAIR = "CP";
	public static final String MAINTENANCE_CANDIDATE_PAIRS = "MNT_CAND_PRS";
	public static final String INSTALLATION_CANDIDATE_PAIRS = "INST_CAND_PRS";
	
	//External API Requests
	public static final String MULTIPLE_APPEARANCE_REQUEST = "Multiple Appearance Inquiry API";
	public static final String ADDITIONAL_LINES_REQUEST = "Additional Lines Inquiry API";
	public static final String CURRENT_ASSIGNMENT_REQUEST = "Current Assignment Inquiry API";
	public static final String OFFICE_EQUIPMENT_REQUEST = "Office Equipment Inquiry API";
	public static final String DEFECTIVE_PAIRS_REQUEST = "Defective Pairs Inquiry API";
	public static final String NET_PAGE_REQUEST = "NET PAGE SEND API";
	public static final String NET_PAGER_CONFIG_REQUEST = "NET PAGER Device Config API";
	public static final String CENTRAL_OFFICE_EQUIPMENT= "Central Office Equipment Inquiry API";
	public static final String DEFECTIVE_PAIRS= "Defective Pairs API";
	
	//LFACS messages
	public static final String INVALID_TN_EMPTY = "Invalid digits : TN is empty";
	public static final String INVALID_TN_LENGTH_ERR = "Invalid digits : TN should be of 7 or 10 digits";
	public static final String INVALID_TN_ERR_CODE = "-1"; 
	public static final String CANNOT_FETCH_CURR_ASSG_API_LOSDB_NULL = "Cannot fetch data from Current-Assignment api, as Los DB response is null";
	public static final String SERVICE_ADDRESS_NOT_FOUND = "No service address";
	public static final String SERVICE_ADDRESS_FOUND =  "Initial current assignment results are OK";
	public static final String ADDITIONAL_LINES_PAGE_MSG = "Additional lines at this address will be sent via Mobile";
	public static final String ADDITIONAL_LINES_CANNOT_PAGE_MSG = "Additional lines at this address will be sent via Mail and Voice";
	public static final String CANNOT_FETCH_MULTI_APPEARANCE_API_LOSDB_NULL = "Cannot fetch data from Multiple Appearance api, as Los DB response is null";
	public static final String CANNOT_FETCH_COE_API_LOSDB_NULL = "Cannot fetch data from Central Office Equipment api, as Los DB response is null";
	
	public static final String CANNOT_FETCH_DEFECTIVE_PAIR_API_LOSDB_NULL = "Cannot fetch data from Defective Pairs api, as Los DB response is null";
	public static final String FID625_MSG_1 = "Before Reading one TN";
	public static final String FID625_MSG_2 = "Before Reading n TNs";
	public static final String FID615_MSG_8 = "We have ADLs to speak";
	public static final String FID615_MSG_5 = "No other circuits";
	public static final String FID635_HK_1_MSG = "Only one TN has been read";
	public static final String FID635_HK_2_MSG = "More than one TN has been read";
	
	public static final String ADDL_LINES_UDC_CKT = "udc ckt";
	public static final String ADDL_LINES_SPL_CKT = "special";
	
	//Events 
	public static final String SIGNON_EVENT = "SIGNON";
	public static final String MAIN_MENU = "MAIN_MENU";
	public static final String MLT_EVENT = "MLT";
	public static final String LFACS_EVENT = "LFACS";
	public static final String CUT_TO_NEW_FACILITIES = "CUT_TO_NEW_FACILITIES";
	public static final String CHANGE_STATUS = "CHANGE_STATUS";
	public static final String CALLER_ID = "CALLER_ID";
	public static final String CHANGE_SERVICE_TERMINAL = "CHANGE_SERVICE_TERMINAL";
	public static final String CONSTRUCTION_ACTIVITY = "CONSTRUCTION_ACTIVITY";
	public static final String ADMIN = "ADMINISTRATION";
	public static final String NEWS = "NEWS";
	public static final String HELPLINE = "HELPLINE";
	
	//user inputs
	public static final String DTMF_INPUT = "-1";
	public static final String DTMF_INPUT_0 = "0";
	public static final String DTMF_INPUT_1 = "1";
	public static final String DTMF_INPUT_2 = "2";
	public static final String DTMF_INPUT_3 = "3";
	public static final String DTMF_INPUT_4 = "4";
	public static final String DTMF_INPUT_5 = "5";
	public static final String DTMF_INPUT_6 = "6";
	public static final String DTMF_INPUT_7 = "7";
	public static final String DTMF_INPUT_8 = "8";
	public static final String DTMF_INPUT_9 = "9";
	public static final String DTMF_INPUT_10 = "10";
	public static final String DTMF_INPUT_HASH = "#";
	
	//sign on states
	public static final String STATE_SS0110 = "SS0110";
	public static final String STATE_SSD110 = "SSD110";
	public static final String STATE_SSD111 = "SSD111";
	public static final String  STATE_SS0120 = "SS0120";
	public static final String  STATE_SSD120 = "SSD120";
	public static final String STATE_SS0135 = "SS0135";
	public static final String STATE_SSD135 = "SSD135";
	public static final String STATE_SS0180 = "SS0180";
	public static final String STATE_SSD180 = "SSD180";
	public static final String STATE_SS0190 = "SS0190";
	public static final String STATE_SSD190 = "SSD190";
	public static final String STATE_SS0400 = "SS0400";
	public static final String STATE_SS0150 = "SS0150";
	public static final String STATE_SSD150 = "SSD150";
	public static final String STATE_SS0210 = "SS0210";
	public static final String STATE_SSD210 = "SSD210";
	public static final String STATE_SSD220 = "SSD220";
	public static final String STATE_SS0300 = "SS0300";
	public static final String STATE_SSD300 = "SSD300";
	public static final String STATE_SS0160 = "SS0160";
	public static final String STATE_SSD160 = "SSD160";
	public static final String STATE_SS0165 = "SS0165";
	public static final String STATE_SSD165 = "SSD165";
	public static final String STATE_SS0170 = "SS0170";
	public static final String STATE_SSD170 = "SSD170";
	public static final String STATE_SSE320 = "SSE320";
	public static final String STATE_SSE322 = "SSE322";
	
	//mlt states
	public static final String STATE_ML0020 = "ML0020";
	public static final String STATE_ML0025 = "ML0025";
	public static final String STATE_ML0030 = "ML0030";
	public static final String STATE_ML0060 = "ML0060";
	public static final String STATE_ML0070 = "ML0070";
	public static final String STATE_ML0080 = "ML0080";
	public static final String STATE_ML0082 = "ML0082";
	public static final String STATE_ML0084 = "ML0084";
	public static final String STATE_ML0021 = "ML0021";
	public static final String STATE_MLD021 = "MLD021";
	public static final String STATE_MLD026 = "MLD026";
	public static final String STATE_MLD027 = "MLD027";
	public static final String STATE_MLD040 = "MLD040";
	public static final String STATE_MLD075 = "MLD075";
	public static final String STATE_MLD082 = "MLD082";
	public static final String STATE_MLD084 = "MLD084";
	public static final String STATE_MLD300 = "MLD300";
	public static final String STATE_ML0110 = "ML0110";
	public static final String STATE_ML0086 = "ML0086";
	public static final String STATE_MLD086 = "MLD086";
	public static final String STATE_MLD088 = "MLD088";
	public static final String STATE_ML0088 = "ML0088";
	public static final String STATE_ML0090 = "ML0090";
	public static final String STATE_ML0092 = "ML0092";
	public static final String STATE_MLD090 = "MLD090";
	public static final String STATE_MLD092 = "MLD092";
	public static final String STATE_MLD094 = "MLD094";
	
	public static final String STATE_ML0300 = "ML0300";
	public static final String STATE_ML0305 = "ML0305";
	public static final String STATE_MLD307 = "MLD307";
	public static final String STATE_ML0307 = "ML0307";
	public static final String STATE_MLD310 = "MLD310";
	public static final String STATE_ML0340 = "ML0340";
	public static final String STATE_MLD340 = "MLD340";
	public static final String STATE_ML0040 = "ML0040";
	public static final String STATE_ML0330 = "ML0330";
	
	public static final String STATE_ML0500 = "ML0500";
	public static final String STATE_MLD500 = "MLD500";
	public static final String STATE_ML0501 = "ML0501";
	public static final String STATE_MLD510 = "MLD510";
	
	// LFACS States
	public static final String STATE_FI0010 = "FI0010";
	public static final String STATE_FID011 = "FID011";
	public static final String STATE_FI0025 = "FI0025";
	public static final String STATE_FID020 = "FID020";
	public static final String STATE_FID025 = "FID025";
	public static final String STATE_FI0030 = "FI0030";
	public static final String STATE_FID035 = "FID035";
	public static final String STATE_FID040 = "FID040";
	public static final String STATE_FID045 = "FID045";

	public static final String STATE_FI0275 = "FI0275";
	public static final String STATE_FID400 = "FID400";
	public static final String STATE_FI0410 = "FI0410";
	public static final String STATE_FI0411 = "FI0411";
	public static final String STATE_FI0412 = "FI0412";
	public static final String STATE_FI0413 = "FI0413";
	public static final String STATE_FID420 = "FID420";
	public static final String STATE_FI0421 = "FI0421";
	public static final String STATE_FI0422 = "FI0422";
	public static final String STATE_FI0423 = "FI0423";
	public static final String STATE_FI0424 = "FI0424";
	public static final String STATE_FI0425 = "FI0425";
	public static final String STATE_FI0426 = "FI0426";
	public static final String STATE_FI0427 = "FI0427";
	public static final String STATE_FID429 = "FID429";
	public static final String STATE_FI0430 = "FI0430";
	public static final String STATE_FID055 = "FID055";
	public static final String STATE_FI0441 = "FI0441";
	public static final String STATE_FID455 = "FID455";
	public static final String STATE_FI0443 = "FI0443";
	public static final String STATE_FID445 = "FID445";
	public static final String STATE_FI0450 = "FI0450";
	public static final String STATE_FI0460 = "FI0460";
	public static final String STATE_FI0442 = "FI0442";
	public static final String STATE_FID600 = "FID600";
	public static final String STATE_FID300 = "FID300";

	public static final String STATE_FID500 = "FID500";
	public static final String STATE_FI0015 = "FI0015";
	public static final String STATE_FID068 = "FID068";
	public static final String STATE_FI0065 = "FI0065";
	

	public static final String STATE_FI0040 = "FI0040";
	public static final String STATE_FI0056 = "FI0056";
	public static final String STATE_FI0050 = "FI0050";
	
	public static final String STATE_FID060 = "FID060";
	public static final String STATE_FI0051="FI0051";
	public static final String STATE_FI0510="FI0510";
	public static final String STATE_FID515 = "FID515";
	public static final String STATE_FID525 = "FID525";
	public static final String STATE_FID532 = "FID532";
	public static final String STATE_FI0521 = "FI0521";
	public static final String STATE_FI0553 = "FI0553";
	public static final String STATE_FID535 = "FID535";
	public static final String STATE_FI0530 = "FI0530";
	
	public static final String STATE_FID070 = "FID070";
	public static final String STATE_FID090 = "FID090";
	public static final String STATE_FID080 = "FID080";
	
	
	public static final String STATE_FI0210 = "FI0210";
	public static final String STATE_FID211 = "FID211";
	
	public static final String STATE_FI0235 = "FI0235";
	public static final String STATE_FID237 = "FID237";
	public static final String STATE_FID560 = "FID560";
	
	public static final String STATE_FI0551 = "FI0551";
	public static final String STATE_FI0552 = "FI0552";
	public static final String STATE_FI0631 = "FI0631";
	public static final String STATE_FI0636 = "FI0636";
	public static final String STATE_FI0637 = "FI0637";
	public static final String STATE_FI0720 = "FI0720";
	
	public static final String STATE_FI0240 = "FI0240";
	public static final String STATE_FID250 = "FID250";
	public static final String STATE_FID271 = "FID271";
	public static final String STATE_FID272 = "FID272";
	public static final String STATE_FID273 = "FID273";
	public static final String STATE_FID274 = "FID274";
	public static final String STATE_FID291 = "FID291";
	public static final String STATE_FI0529 = "FI0529";
	public static final String STATE_FI0528 = "FI0528";
	public static final String STATE_FI0526 = "FI0526";
	public static final String STATE_FI0527 = "FI0527";
	public static final String STATE_FI0627 = "FI0627";
	public static final String STATE_FI0632 = "FI0632";
	public static final String STATE_FID630 = "FID630";
	public static final String STATE_FI0279 = "FI0279";
	public static final String STATE_FI0280 = "FI0280";
	public static final String STATE_FI0095 = "FI0095";
	public static final String STATE_FI0310 = "FI0310";
	
	public static final String STATE_FID700 = "FID700";
	public static final String STATE_FI0610 = "FI0610";
	public static final String STATE_FID615 = "FID615";
	public static final String STATE_FID625 = "FID625";
	public static final String STATE_FI0554 = "FI0554";
	public static final String STATE_FID635 = "FID635";
	public static final String STATE_FI0630 = "FI0630";
	public static final String STATE_FI0633 = "FI0633";
	public static final String STATE_FI0634 = "FI0634";
	public static final String STATE_FID282 = "FID282";
	public static final String STATE_FI0536 = "FI0536";
	public static final String STATE_FI0537 = "FI0537";
	public static final String STATE_FI0538 = "FI0538";
	public static final String STATE_FI0539 = "FI0539";
	public static final String STATE_FI0540 = "FI0540";
	public static final String STATE_FI0541 = "FI0541";
	public static final String STATE_FI0542 = "FI0542";
	public static final String STATE_FI0220 = "FI0220";
	public static final String STATE_FID224 = "FI0224";
	
	//ADMIN States
	
	public static final String STATE_MM0001 = "MM0001";
	public static final String STATE_ADE110 = "ADE110";
	public static final String STATE_ADD010 = "ADD010";
	public static final String STATE_ADD020 = "ADD020";
	public static final String STATE_ADD035 = "ADD035";
	public static final String STATE_ADD550 = "ADD550";
	public static final String STATE_ADD100 = "ADD100";
	public static final String STATE_ADD110 = "ADD110";
	public static final String STATE_AD0020 = "AD0020";
	public static final String STATE_AD0035 = "AD0035";
	public static final String STATE_AD0500 = "AD0500";
	public static final String STATE_AD0011 = "AD0011";
	public static final String STATE_AD0012 = "AD0012";
	public static final String STATE_AD0110 = "AD0110";
	public static final String STATE_AD0115 = "AD0115";
	public static final String STATE_ADD500 = "ADD500";
	public static final String STATE_ADD555 = "ADD555";
	public static final String STATE_FP0010 = "FP0010";
	public static final String STATE_FPD011 = "FPD011";
	public static final String STATE_FPD060 = "FPD060";
	public static final String STATE_FP0040 = "FP0040";
	public static final String STATE_FP0050 = "FP0050";
	public static final String STATE_FPD005 = "FPD005";
	public static final String STATE_FND741 = "FND741";
	public static final String STATE_FPD100 = "FPD100";
	public static final String STATE_FP0110 = "FP0110";
	public static final String STATE_FP0070 = "FP0070";
	public static final String STATE_FP0020 = "FP0020";
	public static final String STATE_FPD020 = "FPD020";
	
	public static final String STATE_ID0010 = "ID0010";
	public static final String STATE_IDD011 = "IDD011";
	public static final String STATE_ID0015 = "ID0015";
	public static final String STATE_IDD020 = "IDD020";
	public static final String STATE_ID0030 = "ID0030";
	public static final String STATE_IDD035 = "IDD035";
	

	//Construction Activity states
	public static final String STATE_CTD500 = "CTD500";
	public static final String STATE_CT0049 ="CT0049";
	public static final String STATE_CT0400 ="CT0400";
	public static final String STATE_CT0405 ="CT0405";
	public static final String STATE_CTD400 ="CTD400";
	public static final String STATE_CTD403 ="CTD403";
	public static final String STATE_CTD410 ="CTD410";
	
	public static final String WHITE_TRACER = "W,";
	public static final String RED_TRACER = "R,";
	public static final String BLACK_TRACER = "BK,";
	public static final String BP = "BP";
	public static final String TONE = "TONE";
	public static final String COMMA = ",";
	public static final String EMPTY = "";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String OPEN = "Open";
	public static final String PEND = "Pend";
	public static final String NO_OPENING_NUMBER = "No opening number";
	public static final String OPENING_NUMBER = "Opening number exists";
	public static final String TRANSFER_CALL_HOOK_MESSAGE = "Transfer call for manual assistance";

	
	public static final String STATE_INVALID_INPUT = "INVALID_INPUT";
	public static final String DIRECT_STATE_TRANSFER = "DIRECT_STATE_TRANSFER";
	
	public static final String NETAPI_APPLICATION_ID = "FASTIVR";
	public static final String NETAPI_SEND_TYPE = "PAGE";
	public static final String NETAPI_PHONE_DEVICE = "PHONE";
	public static final String NETAPI_MAIL_DEVICE = "MAIL";
	public static final String NETAPI_MESSAGING_SUCCESS_CODE = "200";
	
	public static final String IVRSUCCESS = "1";
	public static final String IVRFAILURE = "0";
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	
	public static final String WHITE_TRACER_MSG = "White Tracer plus Color Code";
	public static final String RED_TRACER_MSG = "Red Tracer plus Color Code";
	public static final String BLACK_TRACER_MSG = "Black Tracer plus Color Code";
	public static final String BINDING_POST = "Binding Post";
	public static final String NO_BINDING = "No Binding Post";
	public static final String BINDING_POST_TONE = "Binding Post Tone";
	public static final String COLOR_CODE = "Color Code";
	public static final String FINISHED_F1 = "Finished Reading Only F1";
	public static final String FINISHED_F2 = "Finished Reading F1 and F2";
	public static final String FINISHED_F3 = "Finished Reading F1, F2 and F3";
	public static final String NEXT_SEGMENT_F2 = "Next Segment F2";
	public static final String NEXT_SEGMENT_F3 = "Next Segment F3";
	public static final String WRONG_SEGMENT = "Wrong Segment";
	
	//Wrpper Api Authorization details
	public class WrapperAuthorizationInfo {
		public static final String USERID = "fasfast"; 
		public static final String PASSWORD = "9312qrty!"; 
	}
	public static final String WRAPPER_API_REQUEST_ID = "FASTFAST";
	public static final String WRAPPER_API_WEB_SERVICE_NAME = "SIABusService";
	public static final String WRAPPER_API_REQUEST_PURPOSE = "TS";
	public static final String WRAPPER_API_DEFAULT_EMPLOYEE_ID = "999";
	public static final String WRAPPER_API_ERROR_STATUS_SUCCESS = "S";
	public static final String WRAPPER_API_ERROR_STATUS_FAILED = "F";
	
	
	public static final String REGEX_NON_NUMERIC = "^[a-zA-Z . ]*$";
	
	public static final String REGEX_NUMERIC = "^[0-9]*$";
	
	public static final String NETPAGE_SUBJECT_CUT_TO_NEW_FACILITY_INQUIRY = "FAST Cut To New Facility Inquiry Result";
	
	public static final String NETPAGE_SUBJECT_CHANGE_NEW_SERV_TERM_INQUIRY = "FAST Change New Serving Terminal Inquiry Result";
}
