package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MPRRoute extends X_C_PRRoute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 250604635333587294L;

	public MPRRoute(Properties ctx, int C_PRRoute_ID, String trxName) {
		super(ctx, C_PRRoute_ID, trxName);
	}
	
	public MPRRoute(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
}
