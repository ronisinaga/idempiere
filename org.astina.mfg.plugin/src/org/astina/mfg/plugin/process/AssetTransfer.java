package org.astina.mfg.plugin.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.model.POWrapper;
import org.astina.mfg.plugin.model.MFG_MAsset;
import org.astina.mfg.plugin.model.MFG_MElementValue;
import org.astina.mfg.plugin.model.I_A_Asset_Acct_Mining;
import org.astina.mfg.plugin.model.I_A_Asset_Mining;
import org.compiere.model.MAccount;
import org.compiere.model.MAssetAcct;
import org.compiere.model.MAssetChange;
import org.compiere.model.MAssetGroupAcct;
import org.compiere.model.MDepreciationWorkfile;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;


public class AssetTransfer extends SvrProcess
{
	/** Organiztion					*/
	private int			p_AD_Org_ID = 0;
	/** Business Unit				*/
	private int			p_User1_ID = 0;
	/**	Asset			*/
	private int			p_A_Asset_ID = 0;
	
	private Timestamp p_MovementDate = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("User1_ID"))
				p_User1_ID = para[i].getParameterAsInt();
			else if (name.equals("MovementDate"))
				p_MovementDate = (Timestamp) para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_A_Asset_ID = getRecord_ID();
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		if (log.isLoggable(Level.INFO)) log.info ("A_Asset_ID=" + p_A_Asset_ID
			+ ", AD_Org_ID=" + p_AD_Org_ID + ", User1_ID=" + p_User1_ID);
		
		MFG_MAsset asset = new MFG_MAsset (getCtx(), p_A_Asset_ID, get_TrxName());
		I_A_Asset_Mining assetMining = POWrapper.create(asset, I_A_Asset_Mining.class);
		if (asset.get_ID() == 0)
			throw new IllegalArgumentException("Not found A_Asset_ID=" + p_A_Asset_ID);
		
		if (asset.getAD_Org_ID() == p_AD_Org_ID)
			throw new IllegalArgumentException("same from/to Organization A_Asset_ID=" + p_A_Asset_ID);
		//if (asset.getA_Asset_Status())
		//	throw new IllegalArgumentException("@Processed@");
		
		MFG_MElementValue ParentBUAccount = new MFG_MElementValue(getCtx(), p_User1_ID, get_TrxName());
		if(ParentBUAccount.getAD_Org_ID() != p_AD_Org_ID)
		{
			throw new IllegalArgumentException("Mismatch Org-Business Unit and Organization A_Asset_ID=" + p_A_Asset_ID);
		}
		
		//update Asset Org and BU
		asset.setAD_Org_ID(p_AD_Org_ID);
		assetMining.setUser1_ID(p_User1_ID);
		assetMining.setMovementDate(p_MovementDate);
		asset.saveEx();
		
		DB.executeUpdateEx("DELETE from A_Asset_Acct WHERE A_Asset_ID=" + asset.getA_Asset_ID(), get_TrxName());
		
		// for each asset group acounting create an asset accounting and a workfile too
		for (MAssetGroupAcct assetgrpacct :  MAssetGroupAcct.forA_Asset_Group_ID(getCtx(), asset.getA_Asset_Group_ID(), null, get_TrxName()))
		{			
			if (assetgrpacct.getAD_Org_ID() == 0 || assetgrpacct.getAD_Org_ID() == p_AD_Org_ID) 
			{
				if (asset.getI_FixedAsset() != null && assetgrpacct.getC_AcctSchema_ID() != asset.getI_FixedAsset().getC_AcctSchema_ID())
					continue;
				
				// Asset Accounting
				//depreciation account
				int bu = 0;
				bu = assetMining.getUser2_ID();
				
				MAccount depOld = new MAccount(getCtx(), assetgrpacct.getA_Depreciation_Acct(), get_TrxName());
				MAccount depNew = new MAccount(getCtx(), 0, get_TrxName());
				PO.copyValues(depOld, depNew);
				if(bu > 0)
				{
					depNew.setUser1_ID(bu);
					depNew.setAD_Org_ID(p_AD_Org_ID);
					depNew.saveEx();
					assetgrpacct.setA_Depreciation_Acct(depNew.getC_ValidCombination_ID());
				}
				
				//disposal Revenue account
				MAccount disOld = new MAccount(getCtx(), assetgrpacct.getA_Disposal_Revenue_Acct(), get_TrxName());
				MAccount disNew = new MAccount(getCtx(), 0, get_TrxName());
				PO.copyValues(disOld, disNew);
				if(bu > 0)
				{
					disNew.setUser1_ID(bu);
					disNew.setAD_Org_ID(p_AD_Org_ID);
					disNew.saveEx();
					assetgrpacct.setA_Disposal_Revenue_Acct(disNew.getC_ValidCombination_ID());
				}
				
				//disposal Loss account
				MAccount losOld = new MAccount(getCtx(), assetgrpacct.getA_Disposal_Loss_Acct(), get_TrxName());
				MAccount losNew = new MAccount(getCtx(), 0, get_TrxName());
				PO.copyValues(losOld, losNew);
				if(bu > 0)
				{
					losNew.setUser1_ID(bu);
					losNew.setAD_Org_ID(p_AD_Org_ID);
					losNew.saveEx();
					assetgrpacct.setA_Disposal_Loss_Acct(losNew.getC_ValidCombination_ID());
				}
				
				// Asset Accounting
				MAssetAcct assetacct = new MAssetAcct(asset, assetgrpacct);
				assetacct.setAD_Org_ID(p_AD_Org_ID); //added by @win
				
				I_A_Asset_Acct_Mining assetAcctMining = POWrapper.create(assetacct, I_A_Asset_Acct_Mining.class);
				
				if(bu > 0)
					assetAcctMining.setUser1_ID(bu);
				
				assetacct.saveEx();
				
				MDepreciationWorkfile getAsset = MDepreciationWorkfile.get(getCtx(), asset.getA_Asset_ID(), "A", get_TrxName());
				
				if(getAsset != null)
				{
					getAsset.setAD_Org_ID(p_AD_Org_ID);
					getAsset.saveEx();
					
					// Change Log
					MAssetChange.createAndSave(getCtx(), "CRT", new PO[]{asset, getAsset, assetacct}, null);
				}else
				{
					// Asset Depreciation Workfile
					MDepreciationWorkfile assetwk = new MDepreciationWorkfile(asset, assetacct.getPostingType(), assetgrpacct);
					assetwk.setAD_Org_ID(p_AD_Org_ID); //added by @win
					assetwk.setUseLifeYears(assetgrpacct.getUseLifeYears());
					assetwk.setUseLifeMonths(assetgrpacct.getUseLifeMonths());
					assetwk.setUseLifeYears_F(assetgrpacct.getUseLifeYears_F());
					assetwk.setUseLifeMonths_F(assetgrpacct.getUseLifeMonths_F());
					assetwk.saveEx();
					
					// Change Log
					MAssetChange.createAndSave(getCtx(), "CRT", new PO[]{asset, assetwk, assetacct}, null);
				}
				
				DB.executeUpdateEx("UPDATE A_Depreciation_Exp SET AD_Org_ID =" + p_AD_Org_ID + ", DR_Account_ID="+depNew.getC_ValidCombination_ID()+" WHERE Processed='N' AND DateAcct>=? AND A_Asset_ID=? ",new Object[]{p_MovementDate, asset.getA_Asset_ID()}, get_TrxName());
			}
		}
		StringBuilder msgreturn = new StringBuilder("@A_Aset_ID@  - #").append(asset.getA_Asset_ID());
		return msgreturn.toString();
	}	//	doIt

}	//	PaySelectionCreateFrom