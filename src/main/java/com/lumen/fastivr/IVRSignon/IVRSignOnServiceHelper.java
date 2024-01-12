package com.lumen.fastivr.IVRSignon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRBusinessException.BusinessException;
import com.lumen.fastivr.IVRDto.*;
import com.lumen.fastivr.IVREntity.FastIvrUser;
import com.lumen.fastivr.IVREntity.Npa;
import com.lumen.fastivr.IVRRepository.FastIvrDBInterface;
import com.lumen.fastivr.IVRRepository.NpaRepository;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.httpclient.IVRHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import static com.lumen.fastivr.IVRUtils.IVRConstants.*;
import static com.lumen.fastivr.IVRUtils.IVRHookReturnCodes.*;

@Component
public class IVRSignOnServiceHelper {
	
	//TODO move all field injection to constructor injection
	
	@Autowired
	private FastIvrDBInterface fastIvrDBInterface;
	
	@Autowired
	private NpaRepository npaRepository;
	
//	@Autowired
//	private PcoutRepository pcoutRepository;
	
//	@Autowired
//	private Map<String, IVRUserSession> userSessionMap;
	
	@Autowired
	private HttpClient httpClient;
	
	@Value("${netapp.api.pager.url}")
	private String netUri;
	
	@Value("${netapp.api.pager.secret}")
	private String applicationSecret;
	
	@Value("${netapp.api.device.url}")
	private String netDeviceBaseUrl;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IVRHttpClient ivrHttpClient;
	
	final static Logger LOGGER = LoggerFactory.getLogger(IVRSignOnServiceHelper.class);
	
	/**
	 * Service method to update Login Jeopardy flag for Technicain
	 * @param user
	 */
	public int updateLoginJeopardyFlag(IVRUserSession user) {
		String jep = convertBooleanToString(user.isLoginJeopardyFlag());
		return fastIvrDBInterface.updateLoginJeopardyFlagByempID(jep, user.getEmpID());
	}
	
	/**
	 * Utility method 
	 * Since Oracle 10.2 cannot accept boolean value need to process it to String (Y/N)
	 * @param flag
	 * @return
	 */
	private String convertBooleanToString(boolean flag) {
		return flag ? "Y" : "N";
	}
	
	/**
	 * Service method to update the number of times wrong password is entered
	 * Currently logic: User can enter maximum of 5 wrong password cumulative across subsequent calls 
	 * @param user
	 */
	public int updatePasswordCounter(IVRUserSession user) {
		return fastIvrDBInterface.updatePasswordCounterByempID(user.getPasswordAttemptCounter(), user.getEmpID());
	}
	
	/**
	 * Service method to update the new password in DB
	 * @param newPassword
	 * @param empID
	 * @return
	 */
	public int updatePasswordInDB(String newPassword, String empID) {
		LOGGER.info("updatePasswordInDB : Calls to update in Database");
		return fastIvrDBInterface.updatePasswordByempID(newPassword, empID);
	}

	/**
	 * Service method to update the new area code in DB
	 * @param newAreaCode
	 * @param empID
	 * @return
	 */
	public int updateAreaCodeInDB(String newAreaCode, String empID) {
		LOGGER.info("updateAreaCodeInDB : Calls to update in Database");
		return fastIvrDBInterface.updateAreaCodeByempID(newAreaCode, empID);
	}
	
	/**
	 * Checks the NPA prefix from NPA table
	 * @param npaPrefix
	 * @return
	 */
	public boolean isNpaPresentInDB(String npaPrefix) {
		List<Npa> npaList = npaRepository.findByNpaPrefix(npaPrefix);
		if (npaList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Fetches password from DB for any security-code (empid)
	 * @param empID
	 * @return
	 */
	public String findPasswordByEmpID(String empID) {
		 Optional<String> optionalPassword = fastIvrDBInterface.findPasswordByempID(empID);
		 if(optionalPassword.isPresent()) {
			 return optionalPassword.get();
		 } else {
			 return "";
		 }
		//return fastIvrDBInterface.findPasswordByempID(empID).orElseThrow(() -> new BusinessException("FASTIVR DB is down"));
	}
	
	public FastIvrUser matchEmployeeID(String securityCode) {
		
		Optional<FastIvrUser> techInDB = fastIvrDBInterface.findByempID(securityCode);
		if(techInDB.isPresent()) {
			return techInDB.get();
		} else {
			return null;
		}

	}
	
	public int updateTechnicianBirthdate(String date, String empid) {
		return fastIvrDBInterface.updateBirthdateByEmpID(date, empid);
	}

	boolean isBirthdateSet(String date) {
		if (date == null || date.isBlank())
			return false;
		else
			return true;
	}

	public boolean isNpaExists(String npaCode) {
		if (npaCode == null || npaCode.isEmpty())
			return false;
		else
			return true;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//Name				:   validateSecurityCode										//
	//Input Parameters	:	userInput, sessionId									,	//
	//Global Data		:	User session map											//
	//Processing		:	Matches the security code with the value in database		//
	//Output			:	0,1,2														//
	//Instructions		:	Call this function to verify the user status				//
	//Current State		:   Called from SSD110														//
	//Output:				User not in Database (0)									//
	//						User present in Database 									//
	//								-Enabled(1)											//
	//								-Not Enabled(2)										//
	//////////////////////////////////////////////////////////////////////////////////////
	public String validateSecurityCode(String userInputDTMF, IVRUserSession userSession) {
		String sessionId = userSession.getSessionId();
		try {
			// userSession = userSessionMap.get(sessionId);
			LOGGER.info("validateSecurityCode: Session: " + sessionId + "," + " current state:" + STATE_SSD110);

			// fetch tech details from database
			FastIvrUser fastIvrTech = this.matchEmployeeID(userInputDTMF);
			if (fastIvrTech == null) {
				// this scenario is when wrong tech security id is entered.
				// need to log the count of wrong entry (max 3) -> taken care in Genesys
				userSession.setAuthenticated(false);
				// userSessionMap.put(userSession.getSessionId(), userSession);

				LOGGER.info("validateSecurityCode: Session: " + sessionId + ","
						+ "Technician not present in Database, Invalid Security code");
				return HOOK_RETURN_0;
			} else {
				// Tech's Security code matches with the value in DB
				if(userSession.isNewSession()) {
					fillSessionData(fastIvrTech, userSession);
					userSession.setNewSession(false);
				}
				if (userSession.isEnabledFlag()) {
					return HOOK_RETURN_1;
				} else {
					return HOOK_RETURN_2;
				}
			}

		} catch (Exception ex) {
			LOGGER.error(FASTIVR_BACKEND_ERR + " Exception stack trace: ", ex);
			throw new BusinessException(sessionId, FASTIVR_BACKEND_ERR);
		}

	}
	
	/**
	 * Copies the data from DB into a user cache 
	 * @param fastIvrTech
	 * @param user
	 */
	private void fillSessionData(FastIvrUser fastIvrTech, IVRUserSession user) {
		user.setCuid(fastIvrTech.getCuid());
		user.setEmpID(fastIvrTech.getEmpID());
		user.setAge(fastIvrTech.getAge());
		user.setNpaPrefix(fastIvrTech.getNpaPrefix());
		user.setCutPageSent(convertStringDataToBoolean(fastIvrTech.getCutPageSent()));
		user.setEnabledFlag(convertStringDataToBoolean(fastIvrTech.getIsEnabledFlag()));
		user.setEc(fastIvrTech.getEc());
		user.setMc(fastIvrTech.getMc());
		user.setPasswordExpireDate(fastIvrTech.getExpireDate());
		user.setAge(fastIvrTech.getAge());
		user.setBirthdate(fastIvrTech.getBirthdate());
		user.setPasswordAttemptCounter(fastIvrTech.getPasswordCounter());
		user.setLoginJeopardyFlag(convertStringDataToBoolean(fastIvrTech.getLoginJeopardyFlag()));
		user.setTechASplicer(convertStringDataToBoolean(fastIvrTech.getIsTechASplicer()));
		user.setDebugFlag(convertStringDataToBoolean(fastIvrTech.getDebugFlag()));
		user.setPagerCo(fastIvrTech.getPagerCo());
		
		String pagerCo = fastIvrTech.getPagerCo();
		user.setPagerCo(pagerCo);
		//TODO: implement a call to NET device to check which active devices tech has configured in NET
		IVRPagerConfig pagerConfiguration = this.loadTechPagerConfiguration(user);
		user.setCanBePagedEmail(pagerConfiguration.isMailEnabled());  
		user.setCanBePagedMobile(pagerConfiguration.isPhoneEnabled());  
		
		//variable npa flag
		boolean variableNpa = fastIvrTech.getNpaPrefix() != null ? false : true;
		user.setVariableNpaFlag(variableNpa);
		
		LOGGER.info("Session: " + user.getSessionId() + ", User :" + user.getEmpID() + " PAGER Status: Mobile: "
				+ user.isCanBePagedMobile() + ", Email: " + user.isCanBePagedEmail());
	}
	
	/**
	 * Utility method to convert String flag data into boolean and store in session
	 * Oracle 10.2 doesn't have boolean datatype 
	 * TODO: If needed move this method to Utility 
	 * @param flagData
	 * @return
	 */
	private boolean convertStringDataToBoolean(String flagData) {
		return flagData != null ? flagData.equalsIgnoreCase("Y") ? true: false  : false;
	}


	/**
	 * This method validates the login password with the password in DB
	 * Currently using plaintext approach. 
	 * TODO: Move to a approach of using BCrypt encrypton to store password in DB
	 * TODO: Check password using passwordEncoder instead of plain-text string checks 
	 * Returns the below scenarios
	 * 1 = Success
	 * 2 = Wrong password
	 * 6 = Jeopardy or exceed max subsequent password attempts
	 * @param passsword
	 * @param user
	 * @return
	 */
	public String validateLoginPassword(String passsword, IVRUserSession user) {
		//actual password should be fetched from DB, instead of pulling from Session
		String actualPassword = findPasswordByEmpID(user.getEmpID());
		int totalPasswordAttempts = user.getPasswordAttemptCounter();

		// User password-counter is over 5 OR user has login jeopardy
		// if birthdate set return 7 else 6
		if (totalPasswordAttempts > MAX_PASSWORD_ATTEMPTS || user.isLoginJeopardyFlag()) {

			// update jeopardy flag in the db incase password counter is more than 9
			if (!user.isLoginJeopardyFlag()) {
				user.setLoginJeopardyFlag(true);
				updateLoginJeopardyFlag(user);
			}
			return HOOK_RETURN_6;
		}
		
		//TODO session object will not store password, fetch from DB and check 
		
		// wrong password entered
		if (!passsword.equalsIgnoreCase(actualPassword) || actualPassword.isBlank()) {
			//password counter already increased before entering this method
//			totalPasswordAttempts++;
			user.setPasswordAttemptCounter(totalPasswordAttempts);
			updatePasswordCounter(user);
			return HOOK_RETURN_2;

		} else {
			// entered password is correct
			return HOOK_RETURN_1;

		}
	}

	/**
	 * Check if expire date is in past
	 * 
	 * @param expireDate
	 * @return
	 */
	boolean hasUserExpired(LocalDate expireDate) {
		LOGGER.info("LocalDate now: "+ LocalDate.now().toString() );
		LOGGER.info("Expire date : "+ expireDate.toString() );
		if (expireDate.isAfter(LocalDate.now())) 
			return false;
		else
			return true;
	}

	/**OLD LOGIC:
	 * 
	 * Logic to check if Tech has PAGER enabled:
	 * So presently, the concept is that C++ code is checking it in 2 parts
	 * -Validating the PAGERCO
	 * -Checking If Tech has PAGER
	 * 
	 * In Validating the PAGERCO: 
	 * 		Check count of "ALL" PG_CNTR in PCOUT table 
	 * 		If count > 0 then send False
	 * 		Else 
	 * 			Check if Tech's PAGERCO matches with PG_CNTR in PCOUT table 
	 * 			If count is > 0 then return false
	 * 			Else return true 
	 * 
	 * In Tech has PAGER
	 * 		Checking whether technican's PAGERCO is not null
	 * 		If NOT NULL then return true, else false 
	 * 
	 * While Sign-on : it checks 
	 * 		If "Tech has PAGER" returns true -> Sets PAGER_TYPE to Alpha 
	 * 								  false -> Sets PAGER_TYPE to Digital
	 * 
	 * While checking for If Tech Can be Paged: 
	 * 			It returns the Value from "Validating the PAGERCO"
	 * 
	 * TL;DR
	 * In order to send a PAGE, Tech's PAGERCO should not be present in PCOUT table. 
	 * 
	 * NEW LOGIC:
	 * 
	 * We check in NET Deivce api to check what are the configured devices in NET for the tech
	 * Pager conditions: 
	 * 	Tech has alteast a Mobile or Email configured in NET
	 * @param user
	 * @param pagerCo
	 * @return
	 */
	public boolean isUserPagerEnabled(IVRUserSession user) {
		// check if PG_CNTR is ALL, if yes return false
//		int countPageCentreALL = pcoutRepository.countPageCentreALL("ALL");
//		if (countPageCentreALL > 0) {
//			return false;
//
//		} else {
//			int countPagerByEmp = pcoutRepository.countPagerByEmp(user.getEmpID());
//			if (countPagerByEmp > 0)
//				return false;
//		}
//		return true;
		
		if(user.isCanBePagedMobile() || user.isCanBePagedEmail()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Performs the User provided birthdate validaton checks 
	 * Output:
	 * 	1 (Validation Passed)
	 *  0 (Validations failed)
	 * @param userInputDTMF
	 * @param sessionId
	 * @return
	 */
	String birthDateValidation(String userInputDTMF, IVRUserSession user) {
		String GENESYS_FORMAT = "MMddyyyy";
		String LOG_FORMAT = "MMM dd YYYY";
		String sessionId = user.getSessionId();
		if (userInputDTMF == null || userInputDTMF.length() != 8) {
			return HOOK_RETURN_0;

		} else {

			boolean isDateValid = isValidDateFormat(userInputDTMF);
			if (isDateValid) {
				String date = formatDate(userInputDTMF, GENESYS_FORMAT, LOG_FORMAT);
				LOGGER.info("Session: " + sessionId + " User entered correct Birthdate : " + date);
				//userSessionMap.get(sessionId).setNewBirthDate(userInputDTMF);
				user.setNewBirthDate(userInputDTMF);
				return HOOK_RETURN_1;

			} else {
				LOGGER.error("Session: " + sessionId + " User entered Incorrect Birthdate or Birthdate-format");
				return HOOK_RETURN_0;
			}
		}
	}
	
	/**
	 * Checks the syntax of the date 
	 * Date should be a length of 8 
	 * With month and day within range 
	 * Also checking If the date-month-year combination is valid using built-in libraries 
	 * @param dateStr
	 * @return
	 */
	private static boolean isValidDateFormat(String dateStr) {
		if (dateStr.length() != 8) {
			return false;
		}

		try {
			int month = Integer.parseInt(dateStr.substring(0, 2));
			int day = Integer.parseInt(dateStr.substring(2, 4));
			int year = Integer.parseInt(dateStr.substring(4));

			if (month < 1 || month > 12 || day < 1 || day > 31) {
				return false;
			}

			LocalDate validated = LocalDate.of(year, month, day);
			return true;
		} catch (NumberFormatException | java.time.DateTimeException e) {
			return false;
		}
	}
	
	/**
	 * Formats the date needed for birthdate validation
	 * @param dateStr
	 * @param inputPattern
	 * @param outputPattern
	 * @return
	 */
	private static String formatDate(String dateStr, String inputPattern, String outputPattern) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern);
		LocalDate date = LocalDate.parse(dateStr, inputFormatter);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputPattern);
		return date.format(outputFormatter);
	}
	
	/**
	 * Generates the four digit OTP from ThreadLcocalRandom class
	 * TODO: This is a CVE level risk approach. Need to find better approach of generating secure numbers. Try SecureRandom
	 * @return
	 */
	public String generateFourDigitOtp() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1000, 10000));
	}
	
	/**
	 * This method sends a API request to NET Messaging enpoint
	 * Since this is used to send OTP, our first choice is Phone
	 * If Phone is not configured in NET then we push the message to MAIL 
	 * @param sessionId
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String pushOtpToTechCuid(IVRUserSession session) throws JsonProcessingException  {
		
		//IVRUserSession session = userSessionMap.get(sessionId);
		String otp = session.getOtpGenerated();
		String messageText = otp + OTP_ADDITIONAL_MESSAGE;
		//String netUri = "https://netapp.corp.intranet/messaging";
		//In this part we know user can be paged
		String device = session.isCanBePagedMobile() ? NETAPI_PHONE_DEVICE : NETAPI_MAIL_DEVICE;
		NETMessagingRequestDto netRequest = buildNetRequest(session.getCuid(), OTP_MESSAGE_SUBJECT, messageText, device);
		
		//String requestJsonStr = gson.toJson(netRequest);
		String requestJsonStr = objectMapper.writeValueAsString(netRequest);
		
		String responseString = ivrHttpClient.httpPostCall(requestJsonStr, netUri, session.getSessionId(), NET_PAGE_REQUEST);

		return responseString;
	}
	
	/**
	 * Processes the Response String.
	 * If the OTP has been sent to Technician, it sends a true , else false
	 * @param jsonString
	 * @return
	 */
	public boolean processJsonStringNETMessaging(String jsonString) {
		try {
			NETMessagingResponseDto response = objectMapper.readValue(jsonString, NETMessagingResponseDto.class);
			if (response.getReasonCode().equalsIgnoreCase(NETAPI_MESSAGING_SUCCESS_CODE)) {
				return true;
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in processing the JSON from NET api: ", e);
		}
		return false;
	}
	
	/**
	 * Builds the Request payload for the NET Messaging Endpoint api
	 * @param cuid
	 * @param messageSubject
	 * @param messageText
	 * @param device
	 * @return
	 */
	private NETMessagingRequestDto buildNetRequest(String cuid, String messageSubject, String messageText, String device) {
		NETMessagingRequestDto netRequest = new NETMessagingRequestDto();
		netRequest.setApplicationId(NETAPI_APPLICATION_ID);
		netRequest.setApplicationKey(applicationSecret);
		netRequest.setTo(cuid);
		netRequest.setFrom(NETAPI_APPLICATION_ID);
		netRequest.setSendType(NETAPI_SEND_TYPE);
		netRequest.setDevice(device);
		netRequest.setSubject(messageSubject);
		netRequest.setMessageText(messageText);
		
		return netRequest;
	}
	
	/**
	 * This method sends a request to NET Device api.
	 * There we check which devices users have configured in NET 
	 * If MOBILE/PHONE is configured then we set canBePagedMobile to true 
	 * If MAIL is set then canBePagedMail is set to true
	 * Return 1 - tech can be paged
	 * Return 0 - tech cannot be paged
	 * @param session
	 * @return
	 */
	public IVRPagerConfig loadTechPagerConfiguration(IVRUserSession session) {
		
		Map<String,String> requestParam = new HashMap<>();
		requestParam.put("cuid", session.getCuid());
		requestParam.put("type", "json");
		requestParam.put("display", "device_id");
		
		URI uri = buildUriWithParameters(netDeviceBaseUrl, requestParam);
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();
		
		CompletableFuture<String> futureResponse = httpClient.sendAsync(httpRequest, BodyHandlers.ofString())
			.thenApply(response -> response.body());
		LOGGER.info("PAGER config request sent to NET API, Request URI: "+ uri.toString());
		String responseString = "";
		try {
			responseString = futureResponse.get();
			LOGGER.info("PAGER config response: "+ responseString);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error while fetching response: ", e);
			throw new BusinessException(session.getSessionId(), "Error while fetching data from NET Device API");
		}
		
		return checkDeviceMobileOrEmail(responseString);
		
	}
	
	/**
	 * Utility method to build the Query Parameters (Request Parameters)
	 * @param netDeviceBaseUrl
	 * @param paramters
	 * @return
	 */
	private URI buildUriWithParameters(String netDeviceBaseUrl, Map<String, String> paramters) {
		StringBuilder builder = new StringBuilder(netDeviceBaseUrl.trim());
		boolean firstParam = true;
		for(Map.Entry<String, String> param: paramters.entrySet()) {
			builder.append(firstParam ? "?" : "&")
					.append(param.getKey())
					.append("=")
					.append(param.getValue());
			firstParam = false;
		}
		
		return URI.create(builder.toString());
	}
	
	/**
	 * Scans the response from NET Device API and sets the Tech's PAGE configuration accordingly 
	 * @param responseString
	 * @return
	 */
	public IVRPagerConfig checkDeviceMobileOrEmail(String responseString) {
		IVRPagerConfig config = new IVRPagerConfig();
		config.setPhoneEnabled(false);
		config.setMailEnabled(false);
		//Gson library is not supporting Jackson annotations used in Dto classes 
//		Gson gson = new Gson();
//		NETTechDevices deviceObj = gson.fromJson(responseString, NETTechDevices.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			NETTechDevices deviceObj = mapper.readValue(responseString, NETTechDevices.class);
			LOGGER.info("Device POJO: " + deviceObj);

			if (Objects.nonNull(deviceObj) && deviceObj.getDevices() != null) {

				if (deviceObj.getDevices().isArray()) {
					deviceObj.getDevices().forEach(node -> {
						String device = node.asText();
						if (device.contains(NET_PHONE_DEVICE))
							config.setPhoneEnabled(true);
						if (device.contains(NET_MAIL_DEVICE))
							config.setMailEnabled(true);
					});

				} else if (deviceObj.getDevices().isTextual()) {
					String device = deviceObj.getDevices().asText();
					if (device.contains(NET_PHONE_DEVICE))
						config.setPhoneEnabled(true);
					if (device.contains(NET_MAIL_DEVICE))
						config.setMailEnabled(true);
				}

			}
		} catch (JsonMappingException e) {
				LOGGER.error("Error in mapping the Response String to NETTechDevices class", e);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in Processing the Response String to NETTechDevices class", e);
		}
		return config;
	}
	
	public List<IVRParameter> addParamterData(String... str) {
		List<IVRParameter> params = new ArrayList<>();
		for(String s : str) {
			IVRParameter param = new IVRParameter();
			param.setData(s);
			params.add(param);
		}
		
		return params;
		
	}
	
	/**
	 * Updates the password expiry date as sysdate + age (set in database)
	 */
	public int updatePasswordExpireDate(IVRUserSession session) {
		//default extension is 30 days 
		int passwordAge = session.getAge() != 0 ? session.getAge() : 30;
		LocalDate updatePasswordExpireDate = LocalDate.now().plusDays(passwordAge);
		LOGGER.info("Sessionid: " + session.getSessionId() + ", updatePasswordExpireDate : new expire date: "
				+ updatePasswordExpireDate.toString() + ", For Security Code:" + session.getEmpID()+ " with password age :"+ passwordAge);
		return fastIvrDBInterface.updatePasswordExpireByEmpID(updatePasswordExpireDate, session.getEmpID());
		
	}
}
