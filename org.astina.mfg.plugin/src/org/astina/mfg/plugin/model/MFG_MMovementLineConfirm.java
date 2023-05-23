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
import java.util.Properties;

import org.adempiere.model.POWrapper;
import org.compiere.model.MMovementConfirm;
import org.compiere.model.MMovementLineConfirm;
import org.compiere.util.Env;

/**
 *	Inventory Movement Confirmation Line
 *	
 *  @author Jorg Janke
 *  @version $Id: MMovementLineConfirm.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MFG_MMovementLineConfirm extends MMovementLineConfirm
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5447921784818655144L;

	/**
	 * 	Standard Constructor
	 *	@param ctx ctx
	 *	@param M_MovementLineConfirm_ID id
	 *	@param trxName transaction
	 */
	public MFG_MMovementLineConfirm (Properties ctx, int M_MovementLineConfirm_ID, String trxName)
	{
		super (ctx, M_MovementLineConfirm_ID, trxName);
		if (M_MovementLineConfirm_ID == 0)
		{
			setConfirmedQty (Env.ZERO);
			setDifferenceQty (Env.ZERO);
			setScrappedQty (Env.ZERO);
			setTargetQty (Env.ZERO);
			setProcessed (false);
		}	}	//	M_MovementLineConfirm

	/**
	 * 	M_MovementLineConfirm
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MMovementLineConfirm (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	M_MovementLineConfirm
	
	/**
	 * 	Parent Constructor
	 *	@param parent parent
	 */
	public MFG_MMovementLineConfirm (MMovementConfirm parent)
	{
		this (parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		setM_MovementConfirm_ID(parent.getM_MovementConfirm_ID());
	}	//	MMovementLineConfirm

	/**
	 * 	Is Fully Confirmed
	 *	@return true if Target = Confirmed qty
	 */
	public boolean isFullyConfirmed()
	{
		I_M_MovementLineConfirm_Mfg mlc = POWrapper.create(this, I_M_MovementLineConfirm_Mfg.class);
		if(mlc.getQtyReject().compareTo(Env.ZERO)>0)
		{
			return true;
			
		}else
		{
			return getTargetQty().compareTo(getConfirmedQty()) == 0;
		}
	}	//	isFullyConfirmed
	
}	//	M_MovementLineConfirm
