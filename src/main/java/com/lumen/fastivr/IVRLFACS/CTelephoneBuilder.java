package com.lumen.fastivr.IVRLFACS;

import com.lumen.fastivr.IVRDto.LOSDB.CTelephone;
import com.lumen.fastivr.IVRSessionManagement.IVRUserSession;

public class CTelephoneBuilder {
	
	private CTelephone telephone;
	private IVRUserSession session;
	
	public CTelephoneBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	//for case 8,7
	private CTelephoneBuilder(IVRUserSession session) {
		this.session =  session;
		telephone = new CTelephone();
	}
	
	public static CTelephoneBuilder newBuilder(IVRUserSession session) {
		return new CTelephoneBuilder(session);
	}

	public CTelephoneBuilder setTelephone(String ckid) {
		String npa = "";
		String nxx = "";
		String lineNumber = "";
		int length = ckid.length();

		switch (length) {
		case 13: // (NPA)NXX-1234
			npa = ckid.substring(1, 4);
			nxx = ckid.substring(5, 8);
			lineNumber = ckid.substring(9);
			break;
			
		case 12: // NPA NXX-1234
			npa = ckid.substring(0, 3);
			nxx = ckid.substring(4, 7);
			lineNumber = ckid.substring(8);
			break;
			
		case 10: // NPANXX1234
			npa = ckid.substring(0, 3);
			nxx = ckid.substring(3, 6);
			lineNumber = ckid.substring(6);
			break;
			
		case 8: // NXX-1234
			npa = session.getNpaPrefix();
			nxx = ckid.substring(0,3);
			lineNumber = ckid.substring(4);
			break;
			
		case 7: // NXX1234
			npa = session.getNpaPrefix();
			nxx = ckid.substring(0,3);
			lineNumber = ckid.substring(3);
			break;
		default:
			return null;
			
		}
		
		telephone.setNpa(npa);
		telephone.setNxx(nxx);
		telephone.setLineNumber(lineNumber);
		return this;
	}
	
	public CTelephone build() {
		return telephone;
	}

}
