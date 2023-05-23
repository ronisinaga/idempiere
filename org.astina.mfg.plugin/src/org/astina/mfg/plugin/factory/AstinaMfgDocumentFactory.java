package org.astina.mfg.plugin.factory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.adempiere.base.IDocFactory;
import org.astina.mfg.plugin.acct.Doc_MFGAllocationHdr;
import org.astina.mfg.plugin.acct.Doc_MFGAssetDisposed;
import org.astina.mfg.plugin.acct.Doc_MFGInOut;
import org.astina.mfg.plugin.acct.Doc_MFGInventory;
import org.astina.mfg.plugin.acct.Doc_MFGInvoice;
import org.astina.mfg.plugin.acct.Doc_MFGMatchPO;
import org.astina.mfg.plugin.acct.Doc_MFGOrder;
import org.astina.mfg.plugin.acct.Doc_MFGPayment;
import org.astina.mfg.plugin.model.MFG_MAllocationHdr;
import org.astina.mfg.plugin.model.MFG_MAssetDisposed;
import org.astina.mfg.plugin.model.MFG_MInvoice;
import org.astina.mfg.plugin.model.MFG_MOrder;
import org.astina.mfg.plugin.model.MFG_MPayment;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MInOut;
import org.compiere.model.MInventory;
import org.compiere.model.MMatchPO;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class AstinaMfgDocumentFactory implements IDocFactory{

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		//
		Doc doc = null;
		StringBuffer sql = new StringBuffer("SELECT * FROM ")
			.append(tableName)
			.append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				doc = getDocument(as, AD_Table_ID, rs, trxName);
			}
			else
			{
				//s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
			}
		}
		catch (Exception e)
		{
			//s_log.log (Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		return doc;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs,
			String trxName) {
		Doc doc = null;
		String tableName = MTable.getTableName(Env.getCtx(), AD_Table_ID);
		
		if(tableName.equalsIgnoreCase(MFG_MOrder.Table_Name)){
			return new Doc_MFGOrder(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MFG_MInvoice.Table_Name)){
			return new Doc_MFGInvoice(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MFG_MAllocationHdr.Table_Name)){
			return new Doc_MFGAllocationHdr(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MFG_MPayment.Table_Name)){
			return new Doc_MFGPayment(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MFG_MAssetDisposed.Table_Name)){
			return new Doc_MFGAssetDisposed(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MInOut.Table_Name)){
			return new Doc_MFGInOut(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MMatchPO.Table_Name)){
			return new Doc_MFGMatchPO(as, rs, trxName);
		}
		
		if(tableName.equalsIgnoreCase(MInventory.Table_Name)){
			return new Doc_MFGInventory(as, rs, trxName);
		}
		
		return doc;
	}

}
