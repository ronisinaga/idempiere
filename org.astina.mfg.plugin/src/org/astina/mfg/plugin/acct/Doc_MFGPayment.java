/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.astina.mfg.plugin.acct;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCharge;
import org.compiere.model.MClientInfo;
import org.compiere.model.MDocType;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;

/**
 *  Post Invoice Documents.
 *  <pre>
 *  Table:              C_Payment (335)
 *  Document Types      ARP, APP
 *  </pre>
 *  @author Jorg Janke
 *  @version  $Id: Doc_Payment.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_MFGPayment extends Doc
{
	/**
	 *  Constructor
	 * 	@param as accounting schema
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_MFGPayment (MAcctSchema as, ResultSet rs, String trxName)
	{
		super (as, MPayment.class, rs, null, trxName);
	}	//	Doc_Payment

	/**	Tender Type				*/
	private String		m_TenderType = null;
	/** Prepayment				*/
	private boolean		m_Prepayment = false;
	/** Bank Account			*/
	private int			m_C_BankAccount_ID = 0;

	/**
	 *  Load Specific Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails ()
	{
		MPayment pay = (MPayment)getPO();
		setDateDoc(pay.getDateTrx());
		m_TenderType = pay.getTenderType();
		m_Prepayment = pay.isPrepayment();
		m_C_BankAccount_ID = pay.getC_BankAccount_ID();
		//	Amount
		setAmount(Doc.AMTTYPE_Gross, pay.getPayAmt());
		return null;
	}   //  loadDocumentDetails


	/**************************************************************************
	 *  Get Source Currency Balance - always zero
	 *  @return Zero (always balanced)
	 */
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		return retValue;
	}   //  getBalance

	/**
	 *  Create Facts (the accounting logic) for
	 *  ARP, APP.
	 *  <pre>
	 *  ARP
	 *      BankInTransit   DR
	 *      UnallocatedCash         CR
	 *      or Charge/C_Prepayment
	 *  APP
	 *      PaymentSelect   DR
	 *      or Charge/V_Prepayment
	 *      BankInTransit           CR
	 *  CashBankTransfer
	 *      -
	 *  </pre>
	 *  @param as accounting schema
	 *  @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		//	Cash Transfer
		if ("X".equals(m_TenderType) && !MSysConfig.getBooleanValue(MSysConfig.CASH_AS_PAYMENT, true , getAD_Client_ID()))
		{
			ArrayList<Fact> facts = new ArrayList<Fact>();
			facts.add(fact);
			return facts;
		}

		int AD_Org_ID = getBank_Org_ID();		//	Bank Account Org
		if (getDocumentType().equals(DOCTYPE_ARReceipt))
		{
			//	Asset
			FactLine fl = fact.createLine(null, getAccount(Doc.ACCTTYPE_BankInTransit, as),
				getC_Currency_ID(), getAmount(), null);
			if (fl != null && AD_Org_ID != 0)
				fl.setAD_Org_ID(AD_Org_ID);
			//
			MAccount acct = null;
			//Astina 251222
			MAccount acct1 = null;
			if (getC_Charge_ID() != 0)
			{
				acct = MCharge.getAccount(getC_Charge_ID(), as);
				//Astina 251222
				acct1 = getAccount(Doc.ACCTTYPE_BankInTransit, as);
				if(acct.equals(acct1))
				{
					ArrayList<Fact> facts = new ArrayList<Fact>();
					facts.clear();
					return facts;
				}
			}
			else if (m_Prepayment)
				acct = getAccount(Doc.ACCTTYPE_C_Prepayment, as);
			else
			{
				//Astina 251222
				//acct = getAccount(Doc.ACCTTYPE_UnallocatedCash, as);
				MDocType dteFrg = MDocType.get(getCtx(), getC_DocType_ID());
				if(dteFrg.getName().startsWith("AR Receipt Foreign"))
				{
					acct = getAccount(Doc.ACCTTYPE_UnallocatedCash, as);
				}else
				{
					acct = getAccount(Doc.ACCTTYPE_C_Receivable, as);
				}
			}
			fl = fact.createLine(null, acct,
				getC_Currency_ID(), null, getAmount());
			if (fl != null && AD_Org_ID != 0
				&& getC_Charge_ID() == 0)		//	don't overwrite charge
				fl.setAD_Org_ID(AD_Org_ID);
		}
		//  APP
		else if (getDocumentType().equals(DOCTYPE_APPayment))
		{
			//Astina 251222 Exclude Advance and Prepayment
			String sql = "SELECT C_DocType_ID FROM C_Doctype "
					+ "WHERE name = ? and AD_Client_ID = ?";
			int doctype_adv = org.compiere.util.DB.getSQLValue(null,sql, "Advance Employee", Env.getAD_Client_ID(getCtx()));
			
			String sql2 = "SELECT C_DocType_ID FROM C_Doctype "
					+ "WHERE name=? and AD_Client_ID = ?";
			int doctype_pre = org.compiere.util.DB.getSQLValue(null,sql2, "Prepayment", Env.getAD_Client_ID(getCtx()));
			
			String sql3 = "SELECT C_DocType_ID FROM C_Doctype "
					+ "WHERE name=? and AD_Client_ID = ?";
			int doctype_direct = org.compiere.util.DB.getSQLValue(null,sql3, "Direct Payment", Env.getAD_Client_ID(getCtx()));
			
			String sql8 = "SELECT C_DocType_ID FROM C_Doctype "
					+ "WHERE name = ? and AD_Client_ID = ?";
			int doctype_advcon = org.compiere.util.DB.getSQLValue(null,sql8, "Advance Employee (Confidential)", Env.getAD_Client_ID(getCtx()));
			
			String sql9 = "SELECT C_DocType_ID FROM C_Doctype "
					+ "WHERE name = ? and AD_Client_ID = ?";
			int doctype_payInst = org.compiere.util.DB.getSQLValue(null,sql9, "Payment Instruction", Env.getAD_Client_ID(getCtx()));
			
			if(getC_DocType_ID() == doctype_adv || getC_DocType_ID() == doctype_pre || 
					getC_DocType_ID() == doctype_direct || getC_DocType_ID() == doctype_advcon || getC_DocType_ID() == doctype_payInst)
			{
				ArrayList<Fact> facts = new ArrayList<Fact>();
				facts.clear();
				return facts;
			}
			//End Astina
			
			MAccount acct = null;
			//Astina 251222
			MAccount acct1 = null;
			
			if (getC_Charge_ID() != 0)
			{
				acct = MCharge.getAccount(getC_Charge_ID(), as);
				//Astina 251222
				acct1 = getAccount(Doc.ACCTTYPE_BankInTransit, as);
				if(acct.equals(acct1))
				{
					ArrayList<Fact> facts = new ArrayList<Fact>();
					facts.clear();
					return facts;
				}//end Astina
			}
			else if (m_Prepayment)
				acct = getAccount(Doc.ACCTTYPE_V_Prepayment, as);
			else
				acct = getAccount(Doc.ACCTTYPE_PaymentSelect, as);
			FactLine fl = fact.createLine(null, acct,
				getC_Currency_ID(), getAmount(), null);
			if (fl != null && AD_Org_ID != 0
				&& getC_Charge_ID() == 0)		//	don't overwrite charge
				fl.setAD_Org_ID(AD_Org_ID);

			//	Asset
			fl = fact.createLine(null, getAccount(Doc.ACCTTYPE_BankInTransit, as),
				getC_Currency_ID(), null, getAmount());
			if (fl != null && AD_Org_ID != 0)
				fl.setAD_Org_ID(AD_Org_ID);
		}
		else
		{
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			fact = null;
		}
		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(fact);
		return facts;
	}   //  createFact

	/**
	 * 	Get AD_Org_ID from Bank Account
	 * 	@return AD_Org_ID or 0
	 */
	private int getBank_Org_ID ()
	{
		if (m_C_BankAccount_ID == 0)
			return 0;
		//
		MBankAccount ba = MBankAccount.get(getCtx(), m_C_BankAccount_ID);
		return ba.getAD_Org_ID();
	}	//	getBank_Org_ID


	@Override
	public BigDecimal getCurrencyRate() {
		if (getC_Currency_ID() == getAcctSchema().getC_Currency_ID())
			return null;
		
		MPayment pay = (MPayment)getPO();
		int baseCurrencyId = MClientInfo.get(getCtx(), pay.getAD_Client_ID()).getC_Currency_ID();
		if (baseCurrencyId != getAcctSchema().getC_Currency_ID())
			return null;
		
		if (pay.isOverrideCurrencyRate()) {
			return pay.getCurrencyRate();
		} else {
			return null;
		}		
	}	
	
	@Override
	public boolean isConvertible (MAcctSchema acctSchema) {
		MPayment pay = (MPayment)getPO();
		if (pay.getC_Currency_ID() != acctSchema.getC_Currency_ID()) {
			int baseCurrencyId = MClientInfo.get(getCtx(), pay.getAD_Client_ID()).getC_Currency_ID();
			if (baseCurrencyId == acctSchema.getC_Currency_ID() && pay.isOverrideCurrencyRate()) {
				return true;
			}
		}
		
		return super.isConvertible(acctSchema);
	}
}   //  Doc_Payment
