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

import org.compiere.model.MMovementLine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	Inventory Move Line Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MMovementLine.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MFG_MMovementLine extends MMovementLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5614562023263896756L;

	/**
	 * 	Standard Cosntructor
	 *	@param ctx context
	 *	@param M_MovementLine_ID id
	 *	@param trxName transaction
	 */
	public MFG_MMovementLine (Properties ctx, int M_MovementLine_ID, String trxName)
	{
		super (ctx, M_MovementLine_ID, trxName);
		if (M_MovementLine_ID == 0)
		{
			setM_AttributeSetInstance_ID(0);	//	ID
			setMovementQty (Env.ZERO);	// 1
			setTargetQty (Env.ZERO);	// 0
			setScrappedQty(Env.ZERO);
			setConfirmedQty(Env.ZERO);
			setProcessed (false);
		}	
	}	//	MMovementLine

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MMovementLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MMovementLine

	/**
	 * 	Parent constructor
	 *	@param parent parent
	 */
	public MFG_MMovementLine (MFG_MMovement parent)
	{
		this (parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		setM_Movement_ID(parent.getM_Movement_ID());
	}	//	MMovementLine
	
	/** Parent							*/
	protected MFG_MMovement m_parent = null;
	
	/**
	 * get Parent
	 * @return Parent Movement
	 */
	public MFG_MMovement getParent() 
	{
		if (m_parent == null)
			m_parent = new MFG_MMovement (getCtx(), getM_Movement_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		if (newRecord && getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "M_Movement_ID"));
			return false;
		}
		
		//Comment by Astina 270123
		//if (getParent().pendingConfirmations()) {
		//	if (  newRecord ||
		//		(is_ValueChanged(COLUMNNAME_MovementQty) && !is_ValueChanged(COLUMNNAME_TargetQty))) {
		//		log.saveError("SaveError", Msg.parseTranslation(getCtx(), "@Open@: @M_MovementConfirm_ID@"));
		//		return false;
		//	}
		//}
		//	Set Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_MovementLine WHERE M_Movement_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getM_Movement_ID());
			setLine (ii);
		}
		
		 //either movement between locator or movement between lot
		if (getM_Locator_ID() == getM_LocatorTo_ID() && getM_AttributeSetInstance_ID() == getM_AttributeSetInstanceTo_ID())
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_Locator_ID@ == @M_LocatorTo_ID@ and @M_AttributeSetInstance_ID@ == @M_AttributeSetInstanceTo_ID@"));
			return false;
		}

		if (getMovementQty().signum() == 0)
		{
			String docAction = getParent().getDocAction();
			String docStatus = getParent().getDocStatus();
			if (   MFG_MMovement.DOCACTION_Void.equals(docAction)
				&& (   MFG_MMovement.DOCSTATUS_Drafted.equals(docStatus)
					|| MFG_MMovement.DOCSTATUS_Invalid.equals(docStatus)
					|| MFG_MMovement.DOCSTATUS_InProgress.equals(docStatus)
					|| MFG_MMovement.DOCSTATUS_Approved.equals(docStatus)
					|| MFG_MMovement.DOCSTATUS_NotApproved.equals(docStatus)
				   )
				)
			{
				// [ 2092198 ] Error voiding an Inventory Move - globalqss
				// zero allowed in this case (action Void and status Draft)
			} else if (   MFG_MMovement.DOCACTION_Complete.equals(docAction)
					   && MFG_MMovement.DOCSTATUS_InProgress.equals(docStatus))
			{
				// IDEMPIERE-2624 Cant confirm 0 qty on Movement Confirmation
				// zero allowed in this case (action Complete and status In Progress)
			} else {
				log.saveError("FillMandatory", Msg.getElement(getCtx(), "MovementQty"));
				return false;
			}
		}

		//	Qty Precision
		if (newRecord || is_ValueChanged(COLUMNNAME_MovementQty))
			setMovementQty(getMovementQty());

		if (getM_AttributeSetInstanceTo_ID() == 0)
		{
			//instance id default to same for movement between locator 
			if (getM_Locator_ID() != getM_LocatorTo_ID())
			{
				if (getM_AttributeSetInstance_ID() != 0)        //set to from
					setM_AttributeSetInstanceTo_ID(getM_AttributeSetInstance_ID());
			}

		}       //      ASI

		return true;
	}	//	beforeSave

}	//	MMovementLine
