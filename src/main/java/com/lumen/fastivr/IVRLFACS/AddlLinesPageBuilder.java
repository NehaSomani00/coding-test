package com.lumen.fastivr.IVRLFACS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumen.fastivr.IVRDto.additionalLines.AdditionalLinesReportResponseDto;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;
import com.lumen.fastivr.IVRUtils.FormatUtilities;
import com.lumen.fastivr.IVRUtils.IVRConstants;

@Service
public class AddlLinesPageBuilder {
	
	//TODO constructor injection
	@Autowired
	private ObjectMapper objectmapper;
	
	@Autowired
	private IVRLfacsServiceHelper ivrLfacsServiceHelper;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AddlLinesPageBuilder.class);
	
	public void processAddlLinesResponse(AdditionalLinesReportResponseDto response,
			IVRUserSession session, String ckid, String device) throws JsonProcessingException {
		int countADL = 0;

		if (response != null && response.getMessageStatus() != null
				&& response.getMessageStatus().getErrorStatus() != null 
				&& response.getMessageStatus().getErrorMessage()!= null) {
			
			if (!response.getMessageStatus().getErrorMessage().isBlank()
					|| response.getMessageStatus().getErrorStatus().equalsIgnoreCase("S")) {
				// success scenario

				for (int i = 0; i < response.getReturnDataSet().size(); i++) {

					if (response.getReturnDataSet().get(i) != null) {

						// Is ckt udc?
						if (response.getReturnDataSet().get(i).contains("UDC")) {
							countADL++;
							response.getReturnDataSet().set(i, IVRConstants.ADDL_LINES_UDC_CKT);

						} else {
							// Is a special circuit
							String temp = FormatUtilities.FormatTelephoneNNNXXXX(response.getReturnDataSet().get(i));
							if ((temp == "") || (response.getReturnDataSet().get(i).length() > 12)) {
								countADL++;
								response.getReturnDataSet().set(i, IVRConstants.ADDL_LINES_SPL_CKT);

							} else {
								// normal TN, check whether it's the inquired TN
								if (!temp.equalsIgnoreCase(ckid)) {
									countADL++;
								}
							}
						}
					}
				}

				// transaction succeeded so page
				LOGGER.info("Session: " + session.getSessionId()
						+ " Successfully processed the Additional lines response for PAGE text");
				if (device != null) {
					// PAGE to mobile or email
					CSLPage cslPage = new CSLPage("ADL", ckid);
					String pageMsg = cslPage.formatAddlLines(response, countADL, session.getSessionId());
					ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_ADDL_LINE, pageMsg, device,
							session);
				}
				session.setAdditionalLinesResponse(objectmapper.writeValueAsString(response));

			} else {
				// failure scenario
				LOGGER.info("Session: " + session.getSessionId() + " Error in Additional Lines: "
						+ response.getMessageStatus().getErrorMessage());
				CSLPage csLPage = new CSLPage("ADL", ckid);
				String pageMsg = csLPage.fmtErr(response.getMessageStatus().getErrorMessage());
				ivrLfacsServiceHelper.sendTestResultToTech(IVRConstants.NETPAGE_SUBJECT_ADDL_LINE, pageMsg, device,
						session);
			}

		}
	}
}
