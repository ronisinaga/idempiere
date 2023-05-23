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

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.FillMandatoryException;
import org.adempiere.exceptions.WarehouseLocatorConflictException;
import org.compiere.model.I_M_AttributeSet;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MLocatorType;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 	InOut Line
 *
 *  @author Jorg Janke
 *  @version $Id: MInOutLine.java,v 1.5 2006/07/30 00:51:03 jjanke Exp $
 *
 *  @author Teo Sarca, www.arhipac.ro
 *  		<li>BF [ 2784194 ] Check Warehouse-Locator conflict
 *  			https://sourceforge.net/p/adempiere/bugs/1871/
 */
public class MFG_MInOutLine extends MInOutLine
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8630611882798722864L;

	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_InOutLine_ID id
	 *	@param trxName trx name
	 */
	public MFG_MInOutLine (Properties ctx, int M_InOutLine_ID, String trxName)
	{
		this (ctx, M_InOutLine_ID, trxName, (String[]) null);
	}	//	MInOutLine

	public MFG_MInOutLine(Properties ctx, int M_InOutLine_ID, String trxName, String... virtualColumns) {
		super(ctx, M_InOutLine_ID, trxName, virtualColumns);
		if (M_InOutLine_ID == 0)
		{
			setM_AttributeSetInstance_ID(0);
			setConfirmedQty(Env.ZERO);
			setPickedQty(Env.ZERO);
			setScrappedQty(Env.ZERO);
			setTargetQty(Env.ZERO);
			setIsInvoiced (false);
			setIsDescription (false);
		}
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MFG_MInOutLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInOutLine

	/** Parent					*/
	private MInOut			m_parent = null;

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MInOut getParent()
	{
		if (m_parent == null)
			m_parent = new MInOut (getCtx(), getM_InOut_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		log.fine("");
		if (newRecord && getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "M_InOut_ID"));
			return false;
		}
		if (getParent().pendingConfirmations()) {
			if (  newRecord ||
				(is_ValueChanged(COLUMNNAME_MovementQty) && !is_ValueChanged(COLUMNNAME_TargetQty))) {

				if (getMovementQty().signum() == 0)
				{
					String docAction = getParent().getDocAction();
					String docStatus = getParent().getDocStatus();
					if (   MInOut.DOCACTION_Void.equals(docAction)
						&& (   MInOut.DOCSTATUS_Drafted.equals(docStatus)
							|| MInOut.DOCSTATUS_Invalid.equals(docStatus)
							|| MInOut.DOCSTATUS_InProgress.equals(docStatus)
							|| MInOut.DOCSTATUS_Approved.equals(docStatus)
							|| MInOut.DOCSTATUS_NotApproved.equals(docStatus)
						   )
						)
					{
						// OK to save qty=0 when voiding
					} else if (   MInOut.DOCACTION_Complete.equals(docAction)
							   && MInOut.DOCSTATUS_InProgress.equals(docStatus))
					{
						// IDEMPIERE-2624 Cant confirm 0 qty on Movement Confirmation
						// zero allowed in this case (action Complete and status In Progress)
					} else {
						log.saveError("SaveError", Msg.parseTranslation(getCtx(), "@Open@: @M_InOutConfirm_ID@"));
						return false;
					}
				}
			}
		}
		// Locator is mandatory if no charge is defined - teo_sarca BF [ 2757978 ]
		if(getProduct() != null && MProduct.PRODUCTTYPE_Item.equals(getProduct().getProductType()))
		{
			if (getM_Locator_ID() <= 0 && getC_Charge_ID() <= 0)
			{
				// Try to load Default Locator

				MWarehouse warehouse = MWarehouse.get(getM_Warehouse_ID());
				
				if(warehouse != null) {
					
					int m_Locator_ID = getProduct().getM_Locator_ID();
					
					if(m_Locator_ID > 0 && MLocator.get(m_Locator_ID).getM_Warehouse_ID() == warehouse.getM_Warehouse_ID()) {
						setM_Locator_ID(m_Locator_ID);
					} 
					else {
						MLocator defaultLocator = warehouse.getDefaultLocator();
						if(defaultLocator != null) 
							setM_Locator_ID(defaultLocator.getM_Locator_ID());
					}
				}

				if (getM_Locator_ID() <= 0)
					throw new FillMandatoryException(COLUMNNAME_M_Locator_ID);
			}
		}

		//	Get Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM M_InOutLine WHERE M_InOut_ID=?";
			int ii = DB.getSQLValueEx (get_TrxName(), sql, getM_InOut_ID());
			setLine (ii);
		}
		//	UOM
		if (getC_UOM_ID() == 0)
			setC_UOM_ID (Env.getContextAsInt(getCtx(), Env.C_UOM_ID));
		if (getC_UOM_ID() == 0)
		{
			int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
			if (C_UOM_ID > 0)
				setC_UOM_ID (C_UOM_ID);
		}
		//	Qty Precision
		if (newRecord || is_ValueChanged("QtyEntered"))
			setQtyEntered(getQtyEntered());
		if (newRecord || is_ValueChanged("MovementQty"))
			setMovementQty(getMovementQty());
		
		//Astina 190123
		if (!newRecord && is_ValueChanged("QtyEntered") && getC_OrderLine_ID() > 0)
		{
			MOrderLine orderLine = new MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());
			String sql = "SELECT SUM(QtyEntered) FROM M_InOutLine WHERE C_OrderLine_ID=? "
					+ "AND M_InOut_ID IN (Select M_InOut_ID from M_InOut where DocStatus IN ('DR', 'IP', 'AP', 'IN', 'NA')) AND M_InOutLine_ID != ? ";
			BigDecimal i2 = DB.getSQLValueBD (get_TrxName(), sql, getC_OrderLine_ID(), getM_InOutLine_ID());
			
			if(i2 == null)
			{
				i2 = Env.ZERO;
			}
			if((orderLine.getQtyReserved().subtract(i2)).compareTo(getQtyEntered())<0)
			{
				String msg = "Quantity Entered Over Quantity Outstanding Order";
				throw new AdempiereException(msg);
			}
			
			if(getQtyEntered().compareTo(Env.ZERO)<=0)
			{
				//String msg = "Quantity Entered must greater than 0";
				//throw new AdempiereException(msg);
				//return false;
			}
		}
		//End Astina

		//	Order/RMA Line
		if (getC_OrderLine_ID() == 0 && getM_RMALine_ID() == 0)
		{
			if (getParent().isSOTrx())
			{
				log.saveError("FillMandatory", Msg.translate(getCtx(), "C_OrderLine_ID"));
				return false;
			}
		}

		// Validate Locator/Warehouse - teo_sarca, BF [ 2784194 ]
		if (getM_Locator_ID() > 0)
		{
			MLocator locator = MLocator.get(getCtx(), getM_Locator_ID());
			if (getM_Warehouse_ID() != locator.getM_Warehouse_ID())
			{
				throw new WarehouseLocatorConflictException(
						MWarehouse.get(getCtx(), getM_Warehouse_ID()),
						locator,
						getLine());
			}

	        // IDEMPIERE-2668
			if (MInOut.MOVEMENTTYPE_CustomerShipment.equals(getParent().getMovementType())) {
	        	if (locator.getM_LocatorType_ID() > 0) {
	        		MLocatorType lt = MLocatorType.get(getCtx(), locator.getM_LocatorType_ID());
	        		if (! lt.isAvailableForShipping()) {
	    				log.saveError("Error", Msg.translate(getCtx(), "LocatorNotAvailableForShipping"));
	    				return false;
	        		}
	        	}
	        }
	        
		}
		I_M_AttributeSet attributeset = null;
		if (getM_Product_ID() > 0)
			attributeset = MProduct.get(getCtx(), getM_Product_ID()).getM_AttributeSet();
		boolean isAutoGenerateLot = false;
		if (attributeset != null)
			isAutoGenerateLot = attributeset.isAutoGenerateLot();
		if (getReversalLine_ID() == 0 && !getParent().isSOTrx() && !getParent().getMovementType().equals(MInOut.MOVEMENTTYPE_VendorReturns) && isAutoGenerateLot
				&& getM_AttributeSetInstance_ID() == 0)
		{
			MAttributeSetInstance asi = MAttributeSetInstance.generateLot(getCtx(), (MProduct)getM_Product(), get_TrxName());
			setM_AttributeSetInstance_ID(asi.getM_AttributeSetInstance_ID());
		}

		/* Carlos Ruiz - globalqss
		 * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		 */
		if (getParent().getC_DocType().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}

		if (MSysConfig.getBooleanValue(MSysConfig.VALIDATE_MATCHING_PRODUCT_ON_SHIPMENT, true, Env.getAD_Client_ID(getCtx()))) {
			if (getC_OrderLine_ID() > 0) {
				MOrderLine orderLine = new MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());
				if (orderLine.getM_Product_ID() != getM_Product_ID()) {
					log.saveError("MInOutLineAndOrderLineProductDifferent", (getM_Product_ID() > 0 ? MProduct.get(getM_Product_ID()).getValue() : "")
							+ " <> " + (orderLine.getM_Product_ID() > 0 ? MProduct.get(orderLine.getM_Product_ID()).getValue() : ""));
					return false;
				}
			}
			
		}

		return true;
	}	//	beforeSave

}	//	MInOutLine
