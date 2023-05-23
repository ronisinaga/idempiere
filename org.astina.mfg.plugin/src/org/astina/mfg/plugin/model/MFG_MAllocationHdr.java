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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MDocType;
import org.compiere.process.DocAction;
import org.compiere.util.Env;

/**
 *  Payment Allocation Model.
 * 	Allocation Trigger update C_BPartner
 *
 *  @author 	Jorg Janke
 *  @version 	$Id: MAllocationHdr.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 *  			<li>FR [ 1866214 ]  
 *				<li> https://sourceforge.net/p/adempiere/feature-requests/298/
 * 				<li>FR [ 2520591 ] Support multiples calendar for Org 
 *				<li> https://sourceforge.net/p/adempiere/feature-requests/631/ 
 *				<li>BF [ 2880182 ] Error you can allocate a payment to invoice that was paid
 *				<li> https://sourceforge.net/p/adempiere/bugs/2181/
*/
public class MFG_MAllocationHdr extends MAllocationHdr implements DocAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7787519874581251920L;
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param C_AllocationHdr_ID id
	 *	@param trxName transaction
	 */
	public MFG_MAllocationHdr (Properties ctx, int C_AllocationHdr_ID, String trxName)
	{
		super (ctx, C_AllocationHdr_ID, trxName);
		if (C_AllocationHdr_ID == 0)
		{
			setDateTrx (new Timestamp(System.currentTimeMillis()));
			setDateAcct (getDateTrx());
			setDocAction (DOCACTION_Complete);
			setDocStatus (DOCSTATUS_Drafted);
			setApprovalAmt (Env.ZERO);
			setIsApproved (false);
			setIsManual (false);
			//
			setPosted (false);
			setProcessed (false);
			setProcessing(false);
			setC_DocType_ID(MDocType.getDocType("CMA"));
		}
	}	//	MAllocation

	/**
	 * 	Mandatory New Constructor
	 *	@param ctx context
	 *	@param IsManual manual trx
	 *	@param DateTrx date (if null today)
	 *	@param C_Currency_ID currency
	 *	@param description description
	 *	@param trxName transaction
	 */
	public MFG_MAllocationHdr (Properties ctx, boolean IsManual, Timestamp DateTrx, 
		int C_Currency_ID, String description, String trxName)
	{
		this (ctx, 0, trxName);
		setIsManual(IsManual);
		if (DateTrx != null)
		{
			setDateTrx (DateTrx);
			setDateAcct (DateTrx);
		}
		setC_Currency_ID (C_Currency_ID);
		if (description != null)
			setDescription(description);
	}	//  create Allocation


	/** 
	 * 	Load Constructor
	 * 	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MAllocationHdr (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MAllocation

}   //  MAllocation
