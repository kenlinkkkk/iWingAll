package com.xtel.cms.ccsp;

import java.util.ArrayList;

import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;

public class CCSPAppUtils {

	public static CCSPPkgPolicy getPkgPolicy(ArrayList<CCSPPkgPolicy> listCCSPPkgPolicy, String code) {
		if(listCCSPPkgPolicy == null) {
			return null;
		}
		for(CCSPPkgPolicy p: listCCSPPkgPolicy) {
			if(p.getCode().equalsIgnoreCase(code)) {
				return p;
			}
		}
		return null;
	}
}
