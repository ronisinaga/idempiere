package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.model.POWrapper;
import org.compiere.model.MAccount;
import org.compiere.model.MAsset;
import org.compiere.model.MAssetAcct;
import org.compiere.model.MAssetChange;
import org.compiere.model.MAssetGroup;
import org.compiere.model.MAssetGroupAcct;
import org.compiere.model.MDepreciationWorkfile;
import org.compiere.model.MTable;
import org.compiere.model.MTree_Base;
import org.compiere.model.MTree_Node;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Asset Model
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
@SuppressWarnings("serial")
public class MFG_MAsset extends MAsset {
	
	/**
	 * Get Asset
	 * @param ctx context
	 * @param A_Asset_ID asset
	 * @param trxName
	 */
	public static MFG_MAsset get (Properties ctx, int A_Asset_ID, String trxName)
	{
		return (MFG_MAsset)MTable.get(ctx, MFG_MAsset.Table_Name).getPO(A_Asset_ID, trxName);
	}	//	get

	/**
	 * Load Constructor
	 * @param ctx context
	 * @param rs result set record
	 */
	public MFG_MAsset (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MAsset

	public MFG_MAsset(Properties ctx, int A_Asset_ID, String trxName, String... virtualColumns) {
		super(ctx, A_Asset_ID, trxName, virtualColumns);
	}
	
	int bu = 0;

	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if(!success)
		{
			return success;
		}
		
		//
		// Set parent
		if(getA_Parent_Asset_ID() <= 0)
		{
			int A_Asset_ID = getA_Asset_ID();
			setA_Parent_Asset_ID(A_Asset_ID);
			DB.executeUpdateEx("UPDATE A_Asset SET A_Parent_Asset_ID=A_Asset_ID WHERE A_Asset_ID=" + A_Asset_ID, get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("A_Parent_Asset_ID=" + getA_Parent_Asset_ID());
		}
		
		//
		// Set inventory number:
		String invNo = getInventoryNo();
		if(invNo == null || invNo.trim().length() == 0)
		{
			invNo = "" + get_ID();
			setInventoryNo(invNo);
			DB.executeUpdateEx("UPDATE A_Asset SET InventoryNo=" + DB.TO_STRING(invNo) + " WHERE A_Asset_ID=" + getA_Asset_ID(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("InventoryNo=" + getInventoryNo());
		}
		
		//Astina 030123
		I_A_Asset_Mining assetMining = POWrapper.create(this, I_A_Asset_Mining.class);
		
		// If new record, create accounting and workfile
		if (newRecord)
		{
			//Astina 030123
			//Set Business Unit
			if(getA_Asset_ID() == getA_Parent_Asset_ID())
			{
				setBusinessUnit (assetMining.getUser1_ID());
			}else
			{
				bu = assetMining.getUser2_ID();
			}
			
			setAssetLocation (assetMining.getUser1_ID(), assetMining.getOperationStartDate());
			
			//@win: set value at asset group as default value for asset
			MAssetGroup assetgroup = new MAssetGroup(getCtx(), getA_Asset_Group_ID(), get_TrxName());
			String isDepreciated = (assetgroup.isDepreciated()) ? "Y" : "N";
			String isOwned = (assetgroup.isOwned()) ? "Y" : "N";
			setIsDepreciated(assetgroup.isDepreciated());
			setIsOwned(assetgroup.isOwned());
			DB.executeUpdateEx("UPDATE A_Asset SET IsDepreciated='" + isDepreciated + "', isOwned ='" + isOwned + "' WHERE A_Asset_ID=" + getA_Asset_ID(), get_TrxName());
			//end @win
			
			// for each asset group accounting create an asset accounting and a workfile too
			for (MAssetGroupAcct assetgrpacct :  MAssetGroupAcct.forA_Asset_Group_ID(getCtx(), getA_Asset_Group_ID(), null, get_TrxName()))
			{			
				if (assetgrpacct.getAD_Org_ID() == 0 || assetgrpacct.getAD_Org_ID() == getAD_Org_ID()) 
				{
					if (getI_FixedAsset() != null && assetgrpacct.getC_AcctSchema_ID() != getI_FixedAsset().getC_AcctSchema_ID())
						continue;
					
					// Asset Accounting
					//depreciation account
					MAccount depOld = new MAccount(getCtx(), assetgrpacct.getA_Depreciation_Acct(), get_TrxName());
					MAccount depNew = new MAccount(getCtx(), 0, get_TrxName());
					PO.copyValues(depOld, depNew);
					if(bu > 0)
					{
						depNew.setUser1_ID(bu);
						depNew.setAD_Org_ID(getAD_Org_ID());
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
						disNew.setAD_Org_ID(getAD_Org_ID());
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
						losNew.setAD_Org_ID(getAD_Org_ID());
						losNew.saveEx();
						assetgrpacct.setA_Disposal_Loss_Acct(losNew.getC_ValidCombination_ID());
					}
					
					MAssetAcct assetacct = new MAssetAcct(this, assetgrpacct);
					I_A_Asset_Acct_Mining assetAcctMining = POWrapper.create(assetacct, I_A_Asset_Acct_Mining.class);
					assetacct.setAD_Org_ID(getAD_Org_ID()); //added by @win
					
					if(bu > 0)
						assetAcctMining.setUser1_ID(bu);
					assetacct.saveEx();
					
					// Asset Depreciation Workfile
					MDepreciationWorkfile assetwk = new MDepreciationWorkfile(this, assetacct.getPostingType(), assetgrpacct);
					assetwk.setAD_Org_ID(getAD_Org_ID()); //added by @win
					assetwk.setUseLifeYears(assetgrpacct.getUseLifeYears());
					assetwk.setUseLifeMonths(assetgrpacct.getUseLifeMonths());
					assetwk.setUseLifeYears_F(assetgrpacct.getUseLifeYears_F());
					assetwk.setUseLifeMonths_F(assetgrpacct.getUseLifeMonths_F());
					assetwk.saveEx();
					
					// Change Log
					MAssetChange.createAndSave(getCtx(), "CRT", new PO[]{this, assetwk, assetacct}, null);
				}
			}
			
		}
		else
		{
			MAssetChange.createAndSave(getCtx(), "UPD", new PO[]{this}, null);
		}
		
		//
		// Update child.IsDepreciated flag
		if (!newRecord && is_ValueChanged(COLUMNNAME_IsDepreciated))
		{
			final String sql = "UPDATE " + MDepreciationWorkfile.Table_Name
				+" SET " + MDepreciationWorkfile.COLUMNNAME_IsDepreciated+"=?"
				+" WHERE " + MDepreciationWorkfile.COLUMNNAME_A_Asset_ID+"=?";
			DB.executeUpdateEx(sql, new Object[]{isDepreciated(), getA_Asset_ID()}, get_TrxName());
		}
		
		if (!newRecord && is_ValueChanged(I_A_Asset_Mining.COLUMNNAME_User1_ID))
		{
			if(this.getA_Asset_ID() == this.getA_Parent_Asset_ID())
			{
				setBusinessUnit (assetMining.getUser1_ID());
			}else
			{
				bu = assetMining.getUser2_ID();
			}
			
			String sql5 = "select A_Asset_Location_ID from A_Asset_Location cev where cev.ad_Org_id = ? "
					+ "and cev.A_Asset_id = ? and cev.MovementDate = ?";
			int ic = DB.getSQLValue (null, sql5, getAD_Org_ID(), getA_Asset_ID(), assetMining.getMovementDate());
			if(ic <=0)
			{
				setAssetLocation (assetMining.getUser1_ID(), assetMining.getMovementDate());
			}
			
		}
		
		if (!newRecord && is_ValueChanged(COLUMNNAME_A_Asset_Group_ID))
		{
			DB.executeUpdateEx("DELETE from A_Asset_Acct WHERE A_Asset_ID=" + getA_Asset_ID(), get_TrxName());
			
			//@win: set value at asset group as default value for asset
			MAssetGroup assetgroup = new MAssetGroup(getCtx(), getA_Asset_Group_ID(), get_TrxName());
			String isDepreciated = (assetgroup.isDepreciated()) ? "Y" : "N";
			String isOwned = (assetgroup.isOwned()) ? "Y" : "N";
			setIsDepreciated(assetgroup.isDepreciated());
			setIsOwned(assetgroup.isOwned());
			DB.executeUpdateEx("UPDATE A_Asset SET IsDepreciated='" + isDepreciated + "', isOwned ='" + isOwned + "' WHERE A_Asset_ID=" + getA_Asset_ID(), get_TrxName());
			//end @win
			
			// for each asset group acounting create an asset accounting and a workfile too
			for (MAssetGroupAcct assetgrpacct :  MAssetGroupAcct.forA_Asset_Group_ID(getCtx(), getA_Asset_Group_ID(), null, get_TrxName()))
			{			
				if (assetgrpacct.getAD_Org_ID() == 0 || assetgrpacct.getAD_Org_ID() == getAD_Org_ID()) 
				{
					if (getI_FixedAsset() != null && assetgrpacct.getC_AcctSchema_ID() != getI_FixedAsset().getC_AcctSchema_ID())
						continue;
					
					// Asset Accounting
					MFG_MElementValue ParentBUAccount = new MFG_MElementValue(getCtx(), assetMining.getUser1_ID(), get_TrxName());
					int ic = 0;
					if(this.getA_Asset_ID() == this.getA_Parent_Asset_ID())
					{
						String sql0 = "select coalesce (min (cev.c_elementvalue_id), 0) from c_elementvalue cev where cev.ad_client_id = ? "
								+ "and cev.c_element_id = ? and cev.value = ?";
						ic = DB.getSQLValue (null, sql0, ParentBUAccount.getAD_Client_ID(), ParentBUAccount.getC_Element_ID(), getValue());
					}else
					{
						ic = assetMining.getUser2_ID();
					}
					
					
					
					//depreciation account
					MAccount depOld = new MAccount(getCtx(), assetgrpacct.getA_Depreciation_Acct(), get_TrxName());
					MAccount depNew = new MAccount(getCtx(), 0, get_TrxName());
					PO.copyValues(depOld, depNew);
					if(ic > 0)
					{
						depNew.setUser1_ID(ic);
						depNew.setAD_Org_ID(getAD_Org_ID());
						depNew.saveEx();
						assetgrpacct.setA_Depreciation_Acct(depNew.getC_ValidCombination_ID());
					}
					
					//disposal Revenue account
					MAccount disOld = new MAccount(getCtx(), assetgrpacct.getA_Disposal_Revenue_Acct(), get_TrxName());
					MAccount disNew = new MAccount(getCtx(), 0, get_TrxName());
					PO.copyValues(disOld, disNew);
					if(ic > 0)
					{
						disNew.setUser1_ID(ic);
						disNew.setAD_Org_ID(getAD_Org_ID());
						disNew.saveEx();
						assetgrpacct.setA_Disposal_Revenue_Acct(disNew.getC_ValidCombination_ID());
					}
					
					//disposal Loss account
					MAccount losOld = new MAccount(getCtx(), assetgrpacct.getA_Disposal_Loss_Acct(), get_TrxName());
					MAccount losNew = new MAccount(getCtx(), 0, get_TrxName());
					PO.copyValues(losOld, losNew);
					if(ic > 0)
					{
						losNew.setUser1_ID(ic);
						losNew.setAD_Org_ID(getAD_Org_ID());
						losNew.saveEx();
						assetgrpacct.setA_Disposal_Loss_Acct(losNew.getC_ValidCombination_ID());
					}
					MAssetAcct assetacct = new MAssetAcct(this, assetgrpacct);
					I_A_Asset_Acct_Mining assetAcctMining = POWrapper.create(assetacct, I_A_Asset_Acct_Mining.class);
					
					assetacct.setAD_Org_ID(getAD_Org_ID()); //added by @win
					
					assetAcctMining.setUser1_ID(ic);
					assetacct.saveEx();
					
					// Asset Depreciation Workfile
					MDepreciationWorkfile assetwk = new MDepreciationWorkfile(this, assetacct.getPostingType(), assetgrpacct);
					assetwk.setAD_Org_ID(getAD_Org_ID()); //added by @win
					assetwk.setUseLifeYears(assetgrpacct.getUseLifeYears());
					assetwk.setUseLifeMonths(assetgrpacct.getUseLifeMonths());
					assetwk.setUseLifeYears_F(assetgrpacct.getUseLifeYears_F());
					assetwk.setUseLifeMonths_F(assetgrpacct.getUseLifeMonths_F());
					assetwk.saveEx();
					
					// Change Log
					MAssetChange.createAndSave(getCtx(), "CRT", new PO[]{this, assetwk, assetacct}, null);
				}
			}
			
		}
		
		return true;
	}	//	afterSave
	
	public void setBusinessUnit (int User1_ID)
	{
		if(User1_ID>0)
		{
			MFG_MElementValue ParentBUAccount = new MFG_MElementValue(getCtx(), User1_ID, get_TrxName());
			
			String sql0 = "select coalesce (min (cev.c_elementvalue_id), 0) from c_elementvalue cev where cev.ad_client_id = ? "
					+ "and cev.c_element_id = ? and cev.value = ?";
			int ic = DB.getSQLValue (null, sql0, ParentBUAccount.getAD_Client_ID(), ParentBUAccount.getC_Element_ID(), getValue());
			
			String sql = "SELECT MAX(AD_Tree_ID) FROM AD_TreeNode WHERE Node_ID=? AND AD_Client_ID=?";
			int ii = DB.getSQLValue (null, sql, ParentBUAccount.getC_ElementValue_ID(), ParentBUAccount.getAD_Client_ID());
		
			String sql1 = "select max (atn.seqno) + 1 from ad_treenode atn where atn.ad_tree_id = ? and atn.parent_id = ?";
			int iii = DB.getSQLValue (null, sql1, ii, User1_ID);
			
			bu = 0;
			
			if(ic > 0)
			{
				MFG_MElementValue BuAccount = new MFG_MElementValue(getCtx(), ic, get_TrxName());
				BuAccount.setAD_Org_ID(getAD_Org_ID());
				BuAccount.saveEx();
				
				bu = BuAccount.getC_ElementValue_ID();
			}else
			{
				MFG_MElementValue BuAccount = new MFG_MElementValue(getCtx(), null, get_TrxName());
				BuAccount.setIsSummary (false);
				BuAccount.setAccountSign (MFG_MElementValue.ACCOUNTSIGN_Natural);
				BuAccount.setAccountType (MFG_MElementValue.ACCOUNTTYPE_Expense);
				BuAccount.setIsDocControlled(false);
				BuAccount.setIsForeignCurrency(false);
				BuAccount.setIsBankAccount(false);
			
				BuAccount.setPostActual (true);
				BuAccount.setPostBudget (true);
				BuAccount.setPostEncumbrance (true);
				BuAccount.setPostStatistical (true);
				BuAccount.setC_Currency_ID(ParentBUAccount.getC_Currency_ID());
				BuAccount.setC_Element_ID(ParentBUAccount.getC_Element_ID());
				BuAccount.setValue(getValue());
				BuAccount.setName(getValue());
				BuAccount.setAD_Org_ID(getAD_Org_ID());
				BuAccount.saveEx();
				
				bu = BuAccount.getC_ElementValue_ID();
			}
			
			DB.executeUpdateEx("UPDATE A_Asset SET User2_ID=" + bu + " WHERE A_Asset_ID=" + getA_Asset_ID(), get_TrxName());
			
			MTree_Base tree = new MTree_Base(getCtx(), ii, get_TrxName());
			MTree_Node treeNode = MTree_Node.get(tree, bu);
			//treeNode.setAD_Org_ID(getAD_Org_ID());
			//treeNode.setNode_ID(BuAccount.getC_ElementValue_ID());
			treeNode.setParent_ID(ParentBUAccount.getC_ElementValue_ID());
			//treeNode.setAD_Tree_ID(ii);
			treeNode.setSeqNo (iii);
			treeNode.saveEx();
		}
	}
	
	public void setAssetLocation (int User1_ID, Timestamp OperationStartDate)
	{
		String sql = "SELECT MAX(A_Asset_Location_ID) FROM A_Asset_Location WHERE Service_Status='C' AND A_Asset_ID=?";
		int ii = DB.getSQLValue (null, sql, getA_Asset_ID());
		
		if(ii > 0)
		{
			MAssetLocation Oldloc = new MAssetLocation(getCtx(), ii, get_TrxName());
			Oldloc.setservice_status("H");
			try {
				SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
				String date1 = new java.text.SimpleDateFormat("MM/dd/yyyy").format(OperationStartDate);
				Date d1 = myFormat.parse(date1);
				Date d = addDays(d1, -1);
				Timestamp ts = new Timestamp(d.getTime());
				Oldloc.setEndDate(ts);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Oldloc.setAD_User_ID(Env.getAD_User_ID(getCtx()));
			Oldloc.saveEx();
		}
		
		MAssetLocation Newloc = new MAssetLocation(getCtx(), null, get_TrxName());
		Newloc.setservice_status("C");
		Newloc.setMovementDate(OperationStartDate);
		Newloc.setAD_User_ID(Env.getAD_User_ID(getCtx()));
		Newloc.setA_Asset_ID(getA_Asset_ID());
		Newloc.setUser1_ID(User1_ID);
		Newloc.setAD_Org_ID(getAD_Org_ID());
		Newloc.setIsActive(true);
		Newloc.saveEx();
	}
	
	private static Date addDays(Date d1, int i) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d1);
        cal.add(Calendar.DATE, i);
        return cal.getTime();
    }
	
}
