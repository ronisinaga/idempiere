package org.astina.mfg.plugin.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.adempiere.model.POWrapper;
import org.astina.mfg.plugin.model.I_A_Asset_Change_Mining;
import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAssetAcct;
import org.compiere.model.MAssetChange;
import org.compiere.model.MAssetDisposed;
import org.compiere.model.MDocType;
import org.compiere.util.DB;
import org.compiere.util.Env;


/**
 * @author Teo_Sarca, SC ARHIPAC SERVICE SRL
 */
public class Doc_MFGAssetDisposed extends Doc
{
	/**
	 * @param as
	 * @param rs
	 * @param trxName
	 */
	public Doc_MFGAssetDisposed (MAcctSchema as, ResultSet rs, String trxName)
	{
		super(as, MAssetDisposed.class, rs, MDocType.DOCBASETYPE_GLDocument, trxName);
	}

	
	protected String loadDocumentDetails()
	{
		return null;
	}
	
	
	public BigDecimal getBalance()
	{
		return Env.ZERO;
	}

	
	public ArrayList<Fact> createFacts(MAcctSchema as)
	{
		MAssetDisposed assetDisp = (MAssetDisposed)getPO();
		
		ArrayList<Fact> facts = new ArrayList<Fact>();
		Fact fact = new Fact(this, as, assetDisp.getPostingType());
		facts.add(fact);
		MAssetChange ac = MAssetChange.get(getCtx(), assetDisp.getA_Asset_ID(), MAssetChange.CHANGETYPE_Disposal,getTrxName(), as.getC_AcctSchema_ID());
		I_A_Asset_Change_Mining acm = POWrapper.create(ac, I_A_Asset_Change_Mining.class);
		
		if(acm.getA_Sold_Amt() != null)
		{
			int account_ID = 0;
			String sql = null;
			
			sql = "SELECT CH_Expense_Acct FROM C_Charge_Acct WHERE C_Charge_ID=? AND C_AcctSchema_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt (1, acm.getC_Charge_ID());
				pstmt.setInt (2, as.getC_AcctSchema_ID());
				
				rs = pstmt.executeQuery();
				if (rs.next())
					account_ID = rs.getInt(1);
			}
			catch (SQLException e)
			{
				return null;
			}
			finally {
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			
			if(acm.getA_Sold_Profit_Amt().signum() == 1)
			{
				fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Asset_Acct, as)
						, ac.getC_AcctSchema().getC_Currency_ID()
						, Env.ZERO, ac.getAssetValueAmt());
				fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Accumdepreciation_Acct, as)
						, ac.getC_AcctSchema().getC_Currency_ID()
						, ac.getAssetAccumDepreciationAmt(), Env.ZERO);
				fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Disposal_Revenue_Acct, as)
						, ac.getC_AcctSchema().getC_Currency_ID()
						, Env.ZERO, acm.getA_Sold_Profit_Amt());
				
				fact.createLine(null, MAccount.get(getCtx(), account_ID)
						, ac.getC_AcctSchema().getC_Currency_ID()
						, ac.getAssetValueAmt().subtract(ac.getAssetAccumDepreciationAmt()).add(acm.getA_Sold_Profit_Amt()), Env.ZERO);
				
			}else 
				if(acm.getA_Sold_Profit_Amt().signum() == -1)
				{
					fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Asset_Acct, as)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, Env.ZERO, ac.getAssetValueAmt());
					fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Accumdepreciation_Acct, as)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, ac.getAssetAccumDepreciationAmt(), Env.ZERO);
					fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Disposal_Loss_Acct, as)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, ac.getAssetValueAmt().subtract(ac.getAssetAccumDepreciationAmt()).subtract(acm.getA_Sold_Amt()), Env.ZERO);
					
					fact.createLine(null, MAccount.get(getCtx(), account_ID)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, acm.getA_Sold_Amt(), Env.ZERO);
				}else
				{
					
					fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Asset_Acct, as)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, Env.ZERO, ac.getAssetValueAmt());
					fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Accumdepreciation_Acct, as)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, ac.getAssetAccumDepreciationAmt(), Env.ZERO);
					
					fact.createLine(null, MAccount.get(getCtx(), account_ID)
							, ac.getC_AcctSchema().getC_Currency_ID()
							, acm.getA_Sold_Amt(), Env.ZERO);
				}
		}else
		{
			//
			fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Asset_Acct, as)
					, ac.getC_AcctSchema().getC_Currency_ID()
					, Env.ZERO, ac.getAssetValueAmt());
			fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Accumdepreciation_Acct, as)
					, ac.getC_AcctSchema().getC_Currency_ID()
					, ac.getAssetAccumDepreciationAmt(), Env.ZERO);
			fact.createLine(null, getAccount(MAssetAcct.COLUMNNAME_A_Disposal_Loss_Acct, as)
					, ac.getC_AcctSchema().getC_Currency_ID()
					, ac.getAssetBookValueAmt(), Env.ZERO);
			//
		}
		
		return facts;
	}
	
	private MAccount getAccount(String accountName, MAcctSchema as)
	{
		MAssetDisposed assetDisp = (MAssetDisposed)getPO();
		MAssetAcct assetAcct = MAssetAcct.forA_Asset_ID(getCtx(), as.get_ID(), assetDisp.getA_Asset_ID(), assetDisp.getPostingType(), assetDisp.getDateAcct(),null);
		int account_id = (Integer)assetAcct.get_Value(accountName);
		return MAccount.get(getCtx(), account_id);
	}

}
