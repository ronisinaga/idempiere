package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MAssetModel extends X_A_Asset_Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 958386408556795199L;

	public MAssetModel(Properties ctx, int A_Asset_Model_ID, String trxName) {
		super(ctx, A_Asset_Model_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAssetModel(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}

