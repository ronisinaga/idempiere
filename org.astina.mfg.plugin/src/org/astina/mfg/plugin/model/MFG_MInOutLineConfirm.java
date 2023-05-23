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
import java.util.Properties;

import org.compiere.model.MInOutLineConfirm;
import org.compiere.util.Env;

/**
 *	Ship Confirmation Line Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MInOutLineConfirm.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MFG_MInOutLineConfirm extends MInOutLineConfirm
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2753405320678781177L;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_InOutLineConfirm_ID id
	 *	@param trxName transaction
	 */
	public MFG_MInOutLineConfirm (Properties ctx, int M_InOutLineConfirm_ID, String trxName)
	{
		super (ctx, M_InOutLineConfirm_ID, trxName);
		if (M_InOutLineConfirm_ID == 0)
		{
			setDifferenceQty(Env.ZERO);
			setScrappedQty(Env.ZERO);
			setProcessed (false);
		}
	}	//	MInOutLineConfirm

	/**
	 * 	Load Construvtor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MInOutLineConfirm (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInOutLineConfirm
	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		//MInOutConfirm parent = new MInOutConfirm(getCtx(), getM_InOutConfirm_ID(), get_TrxName());
		//Astina 1110123
		//if (newRecord && parent.isProcessed()) {
		//	log.saveError("ParentComplete", Msg.translate(getCtx(), "M_InOutConfirm_ID"));
		//	return false;
		//}
		//	Calculate Difference = Target - Confirmed - Scrapped
		BigDecimal difference = getTargetQty();
		difference = difference.subtract(getConfirmedQty());
		difference = difference.subtract(getScrappedQty());
		setDifferenceQty(difference);
		//
		return true;
	}	//	beforeSave
	
}	//	MInOutLineConfirm
