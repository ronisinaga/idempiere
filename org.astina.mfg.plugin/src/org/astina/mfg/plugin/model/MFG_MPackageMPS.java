package org.astina.mfg.plugin.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.model.POWrapper;
import org.compiere.model.MClientInfo;
import org.compiere.model.MPackageMPS;
import org.compiere.util.DB;

public class MFG_MPackageMPS extends MPackageMPS
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2426722699419960060L;

	public MFG_MPackageMPS(Properties ctx, int M_PackageMPS_ID, String trxName)
	{
		super(ctx, M_PackageMPS_ID, trxName);
		if (M_PackageMPS_ID == 0)
		{
			MClientInfo clientInfo = MClientInfo.get(ctx, getAD_Client_ID());
			setC_UOM_Weight_ID(clientInfo.getC_UOM_Weight_ID());
			setC_UOM_Length_ID(clientInfo.getC_UOM_Length_ID());
		}
	}
	
	public MFG_MPackageMPS(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	protected boolean beforeSave(boolean newRecord)
	{
		if (getSeqNo() == 0)
		{
			String sql = "SELECT COALESCE(MAX(SeqNo),0)+10 FROM M_PackageMPS WHERE M_Package_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getM_Package_ID());
			setSeqNo(ii);
		}
		
		if (getWeight() == null || getWeight().compareTo(BigDecimal.ZERO) == 0)
		{
			String sql = "SELECT SUM(LineWeight) FROM X_PackageLineWeight plw WHERE plw.M_PackageMPS_ID=?";
			BigDecimal weight = DB.getSQLValueBD(get_TrxName(), sql, getM_PackageMPS_ID());
			if (weight == null)
				weight = BigDecimal.ZERO;
			setWeight(weight);
		}
		
		//Astina 100223
		if(newRecord || is_ValueChanged(I_M_PackageMPS_Mfg.COLUMNNAME_M_Polybox_ID))
		{
			I_M_PackageMPS_Mfg packageMPS = POWrapper.create(this, I_M_PackageMPS_Mfg.class);
			
			MPolybox mp = new MPolybox(getCtx(), packageMPS.getM_Polybox_ID(), get_TrxName());
			mp.setpolyboxstatus("In Use");
			mp.save(get_TrxName());
		}
		
		//Astina 081222
		if (!newRecord && is_ValueChanged(I_M_PackageMPS_Mfg.COLUMNNAME_IsReturn))
		{   //get current value and compare to new value if new value bigger than do nothing
			
			I_M_PackageMPS_Mfg packageMPS = POWrapper.create(this, I_M_PackageMPS_Mfg.class);
			MPolybox mp = new MPolybox(getCtx(), packageMPS.getM_Polybox_ID(), get_TrxName());
			mp.setpolyboxstatus("Standby");
			mp.save(get_TrxName());
				
			this.setProcessed(true);
		}
				
		return true;
	}
	
	protected boolean beforeDelete()
	{
		String sql = "DELETE FROM M_PackageLine WHERE M_PackageMPS_ID = ?";
		DB.executeUpdate(sql, getM_PackageMPS_ID(), get_TrxName());
		
		//Astina 100223
		I_M_PackageMPS_Mfg packageMPS = POWrapper.create(this, I_M_PackageMPS_Mfg.class);
					
		MPolybox mp = new MPolybox(getCtx(), packageMPS.getM_Polybox_ID(), get_TrxName());
		mp.setpolyboxstatus("Standby");
		mp.save(get_TrxName());
		
		return true;
	}
}
