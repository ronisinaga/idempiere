/******************************************************************************
 * Copyright (C) 2013 Elaine Tan                                              *
 * Copyright (C) 2013 Trek Global
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.astina.mfg.plugin.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.model.POWrapper;
import org.astina.mfg.plugin.model.I_C_BankStatementLine_Mining;
import org.astina.mfg.plugin.model.I_C_PaymentAllocate_Mining;
import org.astina.mfg.plugin.model.MFG_MBankStatementLine;
import org.astina.mfg.plugin.model.MFG_MPayment;
import org.astina.mfg.plugin.model.MPaymentLine;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.GridTab;
import org.compiere.model.MBankStatement;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MPaymentAllocate;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

/**
 * 
 * @author Elaine
 *
 */
public abstract class CreateFromStatement extends CreateFromBatchMfg 
{
	public CreateFromStatement(GridTab mTab) 
	{
		super(mTab);
		if (log.isLoggable(Level.INFO)) log.info(mTab.toString());
	}

	public boolean dynInit() throws Exception
	{
		log.config("");
		setTitle(Msg.getElement(Env.getCtx(), "C_BankStatement_ID") + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));
		
		return true;
	}
	
	/**
	 * @return transactions (selection,dateTrx,[c_payment_id,documentNo],[c_currency_id,iso_code],payamt,convertedAmt,bpName)
	 */
	@Override
	protected Vector<Vector<Object>> getBankAccountData(Integer BankAccount, Integer BPartner, String DocumentNo, 
			Timestamp DateFrom, Timestamp DateTo, BigDecimal AmtFrom, BigDecimal AmtTo, Integer DocType, String TenderType, String AuthCode)
	{
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.DateTrx,p.C_Payment_ID,p.DocumentNo, p.C_Currency_ID,c.ISO_Code, p.PayAmt,");
		sql.append("currencyConvertPayment(p.C_Payment_ID,ba.C_Currency_ID), bp.Name, d.C_DocType_ID, d.name ");
		sql.append("FROM C_BankAccount ba");
		sql.append(" INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID)");
		sql.append(" INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID)");
		sql.append(" INNER JOIN C_DocType d ON (p.C_DocType_ID=d.C_DocType_ID)");
		sql.append(" LEFT OUTER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ");
		sql.append(getSQLWhere(BPartner, DocumentNo, DateFrom, DateTo, AmtFrom, AmtTo, DocType, TenderType, AuthCode));
		sql.append(" ORDER BY p.DateTrx Desc");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), getTrxName());
			setParameters(pstmt, BankAccount, BPartner, DocumentNo, DateFrom, DateTo, AmtFrom, AmtTo, DocType, TenderType, AuthCode);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				Vector<Object> line = new Vector<Object>(6);
				line.add(Boolean.FALSE);       //  0-Selection
				line.add(rs.getTimestamp(1));       //  1-DateTrx
				KeyNamePair pp = new KeyNamePair(rs.getInt(2), rs.getString(3));
				line.add(pp);                       //  2-C_Payment_ID
				pp = new KeyNamePair(rs.getInt(4), rs.getString(5));
				line.add(pp);                       //  3-Currency
				line.add(rs.getBigDecimal(6));      //  4-PayAmt
				line.add(rs.getBigDecimal(7));      //  5-Conv Amt
				line.add(rs.getString(8));      	//  6-BParner
				pp = new KeyNamePair(rs.getInt(9), rs.getString(10));
				line.add(pp);                       //  7-DocType
				data.add(line);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		return data;
	}
	
	protected void configureMiniTable(IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);      //  0-Selection
		miniTable.setColumnClass(1, Timestamp.class, false);    //  1-TrxDate / nmicoud - idempiere 240 Let user choose the 'Statement Line Date'
		miniTable.setColumnClass(2, String.class, true);        //  2-Payment
		miniTable.setColumnClass(3, String.class, true);        //  3-Currency
		miniTable.setColumnClass(4, BigDecimal.class, true);    //  4-Amount
		miniTable.setColumnClass(5, BigDecimal.class, true);    //  5-ConvAmount
		miniTable.setColumnClass(6, String.class, true);    	//  6-BPartner
		miniTable.setColumnClass(7, String.class, true);        //  7-DocType
		//  Table UI
		miniTable.autoSize();
	}

	public boolean save(IMiniTable miniTable, String trxName)
	{
		//  fixed values
		int C_BankStatement_ID = ((Integer) getGridTab().getValue("C_BankStatement_ID")).intValue();
		MBankStatement bs = new MBankStatement (Env.getCtx(), C_BankStatement_ID, trxName);
		if (log.isLoggable(Level.CONFIG)) log.config(bs.toString());

		//  Lines
		for(int i = 0; i < miniTable.getRowCount(); i++)
		{
			if(((Boolean) miniTable.getValueAt(i, 0)).booleanValue())
			{
				Timestamp trxDate = (Timestamp) miniTable.getValueAt(i, 1);  //  1-DateTrx
				KeyNamePair pp = (KeyNamePair) miniTable.getValueAt(i, 2);   //  2-C_Payment_ID
				int C_Payment_ID = pp.getKey();
				MFG_MPayment payment = new MFG_MPayment (Env.getCtx(), C_Payment_ID, trxName);
				pp = (KeyNamePair) miniTable.getValueAt(i, 3);               //  3-Currency
				int C_Currency_ID = pp.getKey();
				BigDecimal TrxAmt = (BigDecimal) miniTable.getValueAt(i, 5); //  5- Conv Amt

				if (log.isLoggable(Level.FINE)) log.fine("Line Date=" + trxDate
					+ ", Payment=" + C_Payment_ID + ", Currency=" + C_Currency_ID + ", Amt=" + TrxAmt);
				//	
				
				MFG_MBankStatementLine bsl = new MFG_MBankStatementLine (bs);
				bsl.setStatementLineDate(trxDate);
				
				//Astina add c_charge
				MDocType docType = new MDocType (Env.getCtx(), payment.getC_DocType_ID(), trxName);
				if(docType.getName().startsWith("Direct Payment"))
				{
					MPaymentLine[] payline = MPaymentLine.get(payment);
					for (int j = 0; j < payline.length; j++)
					{
						MPaymentLine paymentline = payline[j];
						bsl.setPaymentAstina(payment);
						bsl.setC_Charge_ID(paymentline.getC_Charge_ID());
						bsl.setChargeAmt(paymentline.getLineTotalAmt());
						bsl.setDescription(paymentline.getDescription());
						I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
						if(paymentline.getC_Activity_ID()>0)
						{
							po_bsLine.setC_Activity_ID(paymentline.getC_Activity_ID());
						}
						
						if(paymentline.getUser1_ID()>0)
						{
							po_bsLine.setUser1_ID(paymentline.getUser1_ID());
						}
						
						if(paymentline.getC_JobCode_ID()>0)
						{
							po_bsLine.setC_JobCode_ID(paymentline.getC_JobCode_ID());
						}
					}
				}else if(docType.getName().startsWith("Advance Employee") || 
						docType.getName().startsWith("Payment Instruction"))
				{
					bsl.setPaymentAstina(payment);
					bsl.setC_Charge_ID(payment.getC_Charge_ID());
					bsl.setChargeAmt(TrxAmt);
					
					I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
					if(payment.getC_Activity_ID()>0)
					{
						po_bsLine.setC_Activity_ID(payment.getC_Activity_ID());
					}
					
					if(payment.getUser1_ID()>0)
					{
						po_bsLine.setUser1_ID(payment.getUser1_ID());
					}
					
				}else if (docType.getName().startsWith("AP Payment") && payment.getC_Charge_ID()!=0)
				{
					String sql2 = "select cc.c_charge_id " + 
							"from c_charge cc " + 
							"join c_charge_acct cca on cc.c_charge_id = cca.c_charge_id " + 
							"join C_BP_Vendor_Acct mpa on cca.ch_expense_acct = mpa.v_liability_acct " + 
							"join c_chargetype ct on ct.c_chargetype_id = cc.c_chargetype_id " +
							"join c_chargetype_doctype cd on cd.c_chargetype_id = ct.c_chargetype_id " +
							"where cc.ad_client_id = ? " + 
							"and mpa.C_BPartner_ID = ? and cd.c_doctype_id=?";
					int charge_ID = org.compiere.util.DB.getSQLValue(null,sql2, Env.getAD_Client_ID(Env.getCtx()), payment.getC_BPartner_ID(), payment.getC_DocType_ID());
					bsl.setPaymentAstina(payment);
					bsl.setC_Charge_ID(charge_ID);
					bsl.setChargeAmt(TrxAmt);
					I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
					if(payment.getC_Activity_ID()>0)
					{
						po_bsLine.setC_Activity_ID(payment.getC_Activity_ID());
					}
					
					if(payment.getUser1_ID()>0)
					{
						po_bsLine.setUser1_ID(payment.getUser1_ID());
					}
				}else if (docType.getName().startsWith("AP Lease Payment") && payment.getC_Charge_ID()!=0)
				{
					String sql2 = "select cc.c_charge_id " + 
							"from c_charge cc " + 
							"join c_charge_acct cca on cc.c_charge_id = cca.c_charge_id " + 
							"join C_BP_Vendor_Acct mpa on cca.ch_expense_acct = mpa.v_liability_acct " + 
							"join c_chargetype ct on ct.c_chargetype_id = cc.c_chargetype_id " +
							"join c_chargetype_doctype cd on cd.c_chargetype_id = ct.c_chargetype_id " +
							"where cc.ad_client_id = ? " + 
							"and mpa.C_BPartner_ID = ? and cd.c_doctype_id=?";
					int charge_ID = org.compiere.util.DB.getSQLValue(null,sql2, Env.getAD_Client_ID(Env.getCtx()), payment.getC_BPartner_ID(), payment.getC_DocType_ID());
					bsl.setPaymentAstina(payment);
					
					String sql3 = "select max(c_paymentallocate_id) from c_paymentallocate where ad_client_id = ? and c_payment_id=?";
					int allocate_ID = org.compiere.util.DB.getSQLValue(null,sql3, Env.getAD_Client_ID(Env.getCtx()), payment.getC_Payment_ID());
					
					MPaymentAllocate payall = new MPaymentAllocate(Env.getCtx(), allocate_ID, trxName);
					MInvoice inv = new MInvoice(Env.getCtx(), payall.getC_Invoice_ID(), trxName);
					
					String sql4 = "select max(c_invoiceline_id) from c_invoiceline where ad_client_id = ? and c_invoice_id=?";
					int invLine_ID = org.compiere.util.DB.getSQLValue(null,sql4, Env.getAD_Client_ID(Env.getCtx()), inv.getC_Invoice_ID());
					MInvoiceLine invLine = new MInvoiceLine(Env.getCtx(), invLine_ID, trxName);
					
					if(inv.getC_DocType().getName().equalsIgnoreCase("Interest Lease"))
					{
						bsl.setC_Charge_ID(invLine.getC_Charge_ID());
					}else
						bsl.setC_Charge_ID(charge_ID);
					
					bsl.setChargeAmt(TrxAmt);
					I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
					if(inv.getC_Activity_ID()>0)
					{
						po_bsLine.setC_Activity_ID(inv.getC_Activity_ID());
					}
					
					if(inv.getUser1_ID()>0)
					{
						po_bsLine.setUser1_ID(inv.getUser1_ID());
					}
					
					I_C_PaymentAllocate_Mining po_allocate = POWrapper.create(payall, I_C_PaymentAllocate_Mining.class);
					MInvoicePaySchedule paysched = new MInvoicePaySchedule(Env.getCtx(), po_allocate.getC_InvoicePaySchedule_ID(), trxName);
					String name = paysched.getC_PaySchedule().getC_PaymentTerm().getName();
					bsl.setDescription(invLine.getDescription()+" - "+name+" - "+inv.getDocumentNo());
					
				}else if (docType.getName().startsWith("Prepayment"))
				{
					String sql2 = "select cc.c_charge_id " + 
							"from c_charge cc " + 
							"join c_charge_acct cca on cc.c_charge_id = cca.c_charge_id " + 
							"join C_BP_Vendor_Acct mpa on cca.ch_expense_acct = mpa.v_prepayment_acct " + 
							"join c_chargetype ct on ct.c_chargetype_id = cc.c_chargetype_id " +
							"join c_chargetype_doctype cd on cd.c_chargetype_id = ct.c_chargetype_id " +
							"where cc.ad_client_id = ? " + 
							"and mpa.C_BPartner_ID = ? and cd.c_doctype_id=?";
					int charge_ID = org.compiere.util.DB.getSQLValue(null,sql2, Env.getAD_Client_ID(Env.getCtx()), payment.getC_BPartner_ID(), payment.getC_DocType_ID());
					bsl.setPaymentAstina(payment);
					bsl.setC_Charge_ID(charge_ID);
					bsl.setChargeAmt(TrxAmt);
					I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
					if(po_bsLine.getC_Activity_ID()>0)
					{
						po_bsLine.setC_Activity_ID(payment.getC_Activity_ID());
					}
					
					if(po_bsLine.getUser1_ID()>0)
					{
						po_bsLine.setUser1_ID(payment.getUser1_ID());
					}
					
				}else if (docType.getName().startsWith("AR Receipt") && payment.getC_Charge_ID()!=0)
				{
					String sql2 = "select cc.c_charge_id " + 
							"from c_charge cc " + 
							"join c_charge_acct cca on cc.c_charge_id = cca.c_charge_id " + 
							"join  C_BP_Customer_Acct mpa on cca.ch_expense_acct = mpa.c_receivable_acct " + 
							"join c_chargetype ct on ct.c_chargetype_id = cc.c_chargetype_id " +
							"join c_chargetype_doctype cd on cd.c_chargetype_id = ct.c_chargetype_id " +
							"where cc.ad_client_id = ? " + 
							"and mpa.C_BPartner_ID = ? and cd.c_doctype_id=?";
					int charge_ID = org.compiere.util.DB.getSQLValue(null,sql2, Env.getAD_Client_ID(Env.getCtx()), payment.getC_BPartner_ID(), payment.getC_DocType_ID());
					bsl.setPaymentAstina(payment);
					bsl.setC_Charge_ID(charge_ID);
					bsl.setChargeAmt(TrxAmt);
					I_C_BankStatementLine_Mining po_bsLine = POWrapper.create(bsl, I_C_BankStatementLine_Mining.class);
					if(po_bsLine.getC_Activity_ID()>0)
					{
						po_bsLine.setC_Activity_ID(payment.getC_Activity_ID());
					}
					
					if(po_bsLine.getUser1_ID()>0)
					{
						po_bsLine.setUser1_ID(payment.getUser1_ID());
					}
				}else
				{
					bsl.setTrxAmt(TrxAmt);
					bsl.setPayment(payment);
				}
				
				bsl.setStmtAmt(TrxAmt);
				bsl.setC_Currency_ID(bs.getBankAccount().getC_Currency_ID()); 
				
				if (!bsl.save())
					log.log(Level.SEVERE, "Line not created #" + i);
			}   //   if selected
		}   //  for all rows
		return true;
	}   //  save
	
	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
		Vector<String> columnNames = new Vector<String>(7);
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
		columnNames.add(Msg.translate(Env.getCtx(), "Date"));
		columnNames.add(Msg.getElement(Env.getCtx(), "C_Payment_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_Currency_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "Amount"));
		columnNames.add(Msg.translate(Env.getCtx(), "ConvertedAmount"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_DocType_ID"));
	    
	    return columnNames;
	}
}