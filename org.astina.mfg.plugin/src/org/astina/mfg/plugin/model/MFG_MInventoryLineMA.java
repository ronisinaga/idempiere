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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.I_M_Inventory;
import org.compiere.model.I_M_InventoryLine;
import org.compiere.model.I_M_InventoryLineMA;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MInventoryLineMA;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 *	Inventory Material Allocation
 *	
 *  @author Jorg Janke
 *  @version $Id: MInventoryLineMA.java,v 1.3 2006/07/30 00:51:04 jjanke Exp $
 */
public class MFG_MInventoryLineMA extends MInventoryLineMA
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5654504108476140057L;
	
	/**	Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MInventoryLineMA.class);
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_InventoryLineMA_ID ignored
	 *	@param trxName trx
	 */
	public MFG_MInventoryLineMA (Properties ctx, int M_InventoryLineMA_ID, String trxName)
	{
		super (ctx, M_InventoryLineMA_ID, trxName);
		if (M_InventoryLineMA_ID != 0)
			throw new IllegalArgumentException("Multi-Key");
	}	//	MInventoryLineMA
	
	/**
	 * 	Get Material Allocations for Line
	 *	@param ctx context
	 *	@param M_InventoryLine_ID line
	 *	@param trxName trx
	 *	@return allocations
	 */
	public static MFG_MInventoryLineMA[] get (Properties ctx, int M_InventoryLine_ID, String trxName)
	{
		ArrayList<MFG_MInventoryLineMA> list = new ArrayList<MFG_MInventoryLineMA>();
		String sql = "SELECT * FROM M_InventoryLineMA WHERE M_InventoryLine_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, M_InventoryLine_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add (new MFG_MInventoryLineMA (ctx, rs, trxName));
		}
		catch (Exception e)
		{
			s_log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MFG_MInventoryLineMA[] retValue = new MFG_MInventoryLineMA[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	get
	
	/**
	 * 	Load Cosntructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MFG_MInventoryLineMA (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MInventoryLineMA
	
	/**
	 * 	Parent Constructor
	 *	@param parent parent
	 *	@param M_AttributeSetInstance_ID asi
	 *	@param MovementQty qty
	 *  @param DateMaterialPolicy
	 */
	public MFG_MInventoryLineMA (MInventoryLine parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty,Timestamp DateMaterialPolicy)
	{
		this(parent,M_AttributeSetInstance_ID,MovementQty,DateMaterialPolicy,true);
	}
	
	/**
	 * @param parent
	 * @param M_AttributeSetInstance_ID
	 * @param MovementQty
	 * @param DateMaterialPolicy
	 * @param isAutoGenerated
	 */
	public MFG_MInventoryLineMA (MInventoryLine parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty,Timestamp DateMaterialPolicy,boolean isAutoGenerated)
	{
		this (parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		setM_InventoryLine_ID(parent.getM_InventoryLine_ID());
		//
		setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
		setMovementQty(MovementQty);
		if (DateMaterialPolicy == null)
		{
			if (M_AttributeSetInstance_ID > 0)
			{
				DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(parent.getM_Product_ID(), M_AttributeSetInstance_ID, parent.get_TrxName());
			}
			if (DateMaterialPolicy == null)
			{
				DateMaterialPolicy = parent.getParent().getMovementDate();
			}
		}
		setDateMaterialPolicy(DateMaterialPolicy);
		setIsAutoGenerated(isAutoGenerated);
	}	//	MInventoryLineMA
	
	@Override
	public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
		if (DateMaterialPolicy != null)
			DateMaterialPolicy = Util.removeTime(DateMaterialPolicy);
		super.setDateMaterialPolicy(DateMaterialPolicy);
	}
	
	public static MFG_MInventoryLineMA addOrCreate(MInventoryLine line, int M_AttributeSetInstance_ID, BigDecimal MovementQty, Timestamp DateMaterialPolicy){
		return addOrCreate(line, M_AttributeSetInstance_ID, MovementQty,DateMaterialPolicy,true);
	}
	
	public static MFG_MInventoryLineMA addOrCreate(MInventoryLine line, int M_AttributeSetInstance_ID, BigDecimal MovementQty, Timestamp DateMaterialPolicy,boolean isAutoGenerated)
	{
		Query query = new Query(Env.getCtx(), I_M_InventoryLineMA.Table_Name, "M_InventoryLine_ID=? AND M_AttributeSetInstance_ID=? AND DateMaterialPolicy=trunc(cast(? as date))", 
					line.get_TrxName());
		MFG_MInventoryLineMA po = query.setParameters(line.getM_InventoryLine_ID(), M_AttributeSetInstance_ID, DateMaterialPolicy).first();
		if (po == null)
			po = new MFG_MInventoryLineMA(line, M_AttributeSetInstance_ID, MovementQty, DateMaterialPolicy,isAutoGenerated);
		else
			po.setMovementQty(po.getMovementQty().add(MovementQty));
		return po;
	}
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		//Astina 110123
		//MInventoryLine parentline = new MInventoryLine(getCtx(), getM_InventoryLine_ID(), get_TrxName());
		//if (newRecord && parentline.getParent().isProcessed()) {
		//	log.saveError("ParentComplete", Msg.translate(getCtx(), "M_Inventory_ID"));
		//	return false;
		//}
		
		//Set DateMaterialPolicy
		if(!newRecord && is_ValueChanged(COLUMNNAME_M_AttributeSetInstance_ID)){
			I_M_InventoryLine line = getM_InventoryLine();
			
			Timestamp dateMPolicy = null;
			if(getM_AttributeSetInstance_ID()>0)
			{
				dateMPolicy = MStorageOnHand.getDateMaterialPolicy(line.getM_Product_ID(), getM_AttributeSetInstance_ID(), get_TrxName());
			}
			
			if(dateMPolicy == null)
			{
				I_M_Inventory  inventory = line.getM_Inventory();
				dateMPolicy = inventory.getMovementDate();
			}
			
			setDateMaterialPolicy(dateMPolicy);
		}
		
		return true;
	} //beforeSave
}	//	MInventoryLineMA