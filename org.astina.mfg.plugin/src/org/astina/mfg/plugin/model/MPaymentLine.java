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
package org.astina.mfg.plugin.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MDocType;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * 	Payment Allocate Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MPaymentAllocate.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MPaymentLine extends X_C_PaymentLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2894385378672375131L;

	/**
	 * 	Get active Payment Allocation of Payment
	 *	@param parent payment
	 *	@return array of allocations
	 */
	public static MPaymentLine[] get (MFG_MPayment parent)
	{
		final String whereClause = "C_Payment_ID=?";
		Query query = MTable.get(parent.getCtx(), Table_ID).createQuery(whereClause, parent.get_TrxName());
		query.setParameters(parent.getC_Payment_ID()).setOnlyActiveRecords(true);
		List<MPaymentLine> list = query.list();
		return list.toArray(new MPaymentLine[list.size()]);
	}	//	get
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param C_PaymentAllocate_ID id
	 *	@param trxName trx
	 */
	public MPaymentLine (Properties ctx, int C_PaymentLine_ID, String trxName)
	{
		super (ctx, C_PaymentLine_ID, trxName);
		if (C_PaymentLine_ID == 0)
		{
			this.setPriceActual(Env.ZERO);
			this.setQtyEntered(Env.ZERO);
			this.setLineTotalAmt(Env.ZERO);
		}	
	}	//	MPaymentAllocate

	/**
	 * 	Load Cosntructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MPaymentLine (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MPaymentAllocate
	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		MFG_MPayment payment = new MFG_MPayment (getCtx(), getC_Payment_ID(), get_TrxName());
		
		MDocType doctype = new MDocType (getCtx(), payment.getC_DocType_ID(), get_TrxName());
		if(!doctype.getName().equalsIgnoreCase("Direct Payment"))
		{
			log.saveError("PaymentIsAllocated", "");
			return false;
		}	
		
		return true;
	}	//	beforeSave
	
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		
		BigDecimal ii = DB.getSQLValueBD(get_TrxName(),
				"SELECT COALESCE(SUM(a.LineTotalAmt),0) FROM C_PaymentLine a "
				+ "WHERE a.C_Payment_ID=? ", getC_Payment_ID());
		MFG_MPayment payment = new MFG_MPayment (getCtx(), getC_Payment_ID(), get_TrxName());
		payment.setPayAmt(ii);
		
		payment.saveEx();
		
		return true;
	}	//	afterSave
	
	protected boolean beforeDelete ()
	{
		BigDecimal ii = DB.getSQLValueBD(get_TrxName(),
				"SELECT COALESCE(SUM(a.LineTotalAmt),0) FROM C_PaymentLine a "
				+ "WHERE a.C_Payment_ID=? ", getC_Payment_ID());
		MFG_MPayment payment = new MFG_MPayment (getCtx(), getC_Payment_ID(), get_TrxName());
		payment.setPayAmt(ii);
		payment.saveEx();
		
		return true;
	}	//	afterSave
	
}	//	MPaymentAllocate
