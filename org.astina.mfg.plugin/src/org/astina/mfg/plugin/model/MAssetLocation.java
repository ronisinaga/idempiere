package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MAssetLocation extends X_A_Asset_Location {

	/**
	 * 
	 */
	private static final long serialVersionUID = 958386408556795199L;

	public MAssetLocation(Properties ctx, int A_Asset_Location_ID, String trxName) {
		super(ctx, A_Asset_Location_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAssetLocation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}

