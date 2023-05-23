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
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.base.IProductPricing;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.POWrapper;
import org.compiere.model.I_M_Requisition;
import org.compiere.model.MCharge;
import org.compiere.model.MProduct;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MUOMConversion;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
/**
 *	Requisition Line Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MRequisitionLine.java,v 1.2 2006/07/30 00:51:03 jjanke Exp $
 * 
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>BF [ 2419978 ] Voiding PO, requisition don't set on NULL
 * 			<li>BF [ 2608617 ] Error when I want to delete a PO document
 * 			<li>BF [ 2609604 ] Add M_RequisitionLine.C_BPartner_ID
 * 			<li>FR [ 2841841 ] Requisition Improvements
 * 				https://sourceforge.net/p/adempiere/feature-requests/792/
 */
public class MFG_MRequisitionLine extends MRequisitionLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2567343619431184322L;
	
	//	Product Pricing
	protected IProductPricing	m_productPrice = null;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_RequisitionLine_ID id
	 *	@param trxName transaction
	 */
	public MFG_MRequisitionLine (Properties ctx, int M_RequisitionLine_ID, String trxName)
	{
		this (ctx, M_RequisitionLine_ID, trxName, (String[]) null);
	}	//	MRequisitionLine

	public MFG_MRequisitionLine(Properties ctx, int M_RequisitionLine_ID, String trxName, String... virtualColumns) {
		super(ctx, M_RequisitionLine_ID, trxName, virtualColumns);
		if (M_RequisitionLine_ID == 0)
		{
			setLine (0);	// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_RequisitionLine WHERE M_Requisition_ID=@M_Requisition_ID@
			setLineNetAmt (Env.ZERO);
			setPriceActual (Env.ZERO);
			setQty (Env.ONE);	// 1
		}
	}

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MRequisitionLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MRequisitionLine
	
	/**
	 * 	Parent Constructor
	 *	@param req requisition
	 */
	public MFG_MRequisitionLine (MFG_MRequisition req)
	{
		this (req.getCtx(), 0, req.get_TrxName());
		setClientOrg(req);
		setM_Requisition_ID(req.getM_Requisition_ID());
		m_M_PriceList_ID = req.getM_PriceList_ID();
		m_parent = req;
	}	//	MRequisitionLine
	
	private MFG_MRequisition	m_parent = null;
	
	/**	PriceList				*/
	private int 	m_M_PriceList_ID = 0;
	
	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MFG_MRequisition getParent()
	{
		if (m_parent == null)
			m_parent = new MFG_MRequisition (getCtx(), getM_Requisition_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	@Override
	public I_M_Requisition getM_Requisition()
	{
		return getParent();
	}
	
	/**
	 * 	Set Price
	 */
	public void setPrice()
	{
		if (getC_Charge_ID() != 0)
		{
			MCharge charge = MCharge.get(getCtx(), getC_Charge_ID());
			setPriceActual(charge.getChargeAmt());
		}
		if (getM_Product_ID() == 0)
			return;
		if (m_M_PriceList_ID == 0)
			m_M_PriceList_ID = getParent().getM_PriceList_ID();
		if (m_M_PriceList_ID == 0)
		{
			throw new AdempiereException("PriceList unknown!");
		}
		setPrice (m_M_PriceList_ID);
	}	//	setPrice
	
	public static void addQtyRemainsPO(Properties ctx, int M_RequisitionLine_ID, BigDecimal qty, String trxName)
	{
		MFG_MRequisitionLine reqline = new MFG_MRequisitionLine(ctx, M_RequisitionLine_ID, trxName);
		if(reqline != null) {	
			I_M_RequisitionLine_Mfg mreqline = POWrapper.create(reqline, I_M_RequisitionLine_Mfg.class);
			mreqline.setQtyRemains_PO(mreqline.getQtyRemains_PO().add(qty));
			reqline.saveEx();
		}
	}
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		if (newRecord && getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "M_Requisition_ID"));
			return false;
		}
		
		//Astina 08122022
		String sql1 = "SELECT docbasetype FROM C_DocType cd "
				+ "join M_Requisition mr on mr.c_doctype_id = cd.c_doctype_id "
				+ "WHERE M_Requisition_ID=?";
		String docType = DB.getSQLValueStringEx (get_TrxName(), sql1, getM_Requisition_ID());
		
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM M_RequisitionLine WHERE M_Requisition_ID=?";
			int ii = DB.getSQLValueEx (get_TrxName(), sql, getM_Requisition_ID());
			setLine (ii);
		}
		if (getM_RequisitionLine_ID() == 0)
		{
			I_M_RequisitionLine_Mfg mreqline = POWrapper.create(this, I_M_RequisitionLine_Mfg.class);
			if(docType.equals("POR"))
			{
				mreqline.setQtyRemains_PO(getQty());
			}else
				mreqline.setQtyRemains_PO(Env.ZERO);
			
			// Product Type
			if (getM_Product_ID() > 0)
			{
				mreqline.setProductType(getM_Product().getProductType());
			}
			
		}else
		{
			if(is_ValueChanged("Qty") && docType.equals("POR"))
			{
				//Alex Sembiring
				//Check Qty on PO
				
				MFG_MRequisitionLine reqline = new MFG_MRequisitionLine(getCtx(), getM_RequisitionLine_ID(), get_TrxName());
				I_M_RequisitionLine_Mfg mreqlines = POWrapper.create(this, I_M_RequisitionLine_Mfg.class);
				//check void
				if(getQty().compareTo(BigDecimal.valueOf(0)) == 0 && mreqlines.getQtyRemains_PO().compareTo(reqline.getQty()) != 0)
				{
					log.saveError("Qty on PO", Msg.translate(getCtx(), "Change Qty not Allowed"));
					return false;
				}else 
				{	
					if(getQty().compareTo(BigDecimal.valueOf(0)) != 0)
					{
						//decrease qty input
						if(getQty().compareTo(reqline.getQty()) < 0) 
						{
							if (mreqlines.getQtyRemains_PO().compareTo(BigDecimal.valueOf(0)) == 0 || reqline.getQty().subtract(getQty()).compareTo(mreqlines.getQtyRemains_PO()) > 0)
							{
								log.saveError("Qty on PO", Msg.translate(getCtx(), "Change Qty not Allowed"));
								return false;
							}
							if(mreqlines.getQtyRemains_PO().compareTo(reqline.getQty()) == 0)
							{
								mreqlines.setQtyRemains_PO(getQty());
							}else
								mreqlines.setQtyRemains_PO(mreqlines.getQtyRemains_PO().subtract(reqline.getQty().subtract(getQty())));
						}else
							if(getQty().compareTo(reqline.getQty()) > 0)
							{
								mreqlines.setQtyRemains_PO(mreqlines.getQtyRemains_PO().add(getQty().subtract(reqline.getQty())));
							}
					}else {
						//Fill Qty Remains zero value for void
						mreqlines.setQtyRemains_PO(BigDecimal.valueOf(0));
					}
				}
			}
		}
		//	Product & ASI - Charge
		if (getM_Product_ID() != 0 && getC_Charge_ID() != 0)
			setC_Charge_ID(0);
		if (getM_AttributeSetInstance_ID() != 0 && getC_Charge_ID() != 0)
			setM_AttributeSetInstance_ID(0);
		// Product UOM
		if (getM_Product_ID() > 0 && getC_UOM_ID() <= 0)
		{
			setC_UOM_ID(getM_Product().getC_UOM_ID());
		}
		
		//Astina 140123
		MProduct product = MProduct.get(Env.getCtx(), getM_Product_ID());
		if(getPriceActual().signum() == 0 && getM_Product_ID() != 0 && product.getProductType().equalsIgnoreCase("I"))
		{
			getProductPricing (getParent().getM_PriceList_ID());
			BigDecimal PriceEntered = MUOMConversion.convertProductFrom (Env.getCtx(), product.getM_Product_ID(),
					getC_UOM_ID(), m_productPrice.getPriceStd());
			
			
			setPriceActual (PriceEntered);
			
		}else {
		//
		if (getPriceActual().signum() == 0)
			setPrice();
		}
		
		setLineNetAmt();

		/* Carlos Ruiz - globalqss
		 * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		 */
		if (getParent().getC_DocType().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0 && (getPriceActual().signum() != 0 || getQty().signum() != 0)) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}
		
		return true;
	}	//	beforeSave
	
	/**
	 * 	Calculate Line Net Amt
	 */
	public void setLineNetAmt ()
	{
		BigDecimal lineNetAmt = getQty().multiply(getPriceActual());
		super.setLineNetAmt (lineNetAmt);
	}	//	setLineNetAmt
	
	/**
	 * 	After Save.
	 * 	Update Total on Header
	 *	@param newRecord if new record
	 *	@param success save was success
	 *	@return true if saved
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		
		return updateHeader();
	}	//	afterSave

	
	/**
	 * 	After Delete
	 *	@param success
	 *	@return true/false
	 */
	protected boolean afterDelete (boolean success)
	{
		if (!success)
			return success;
		return updateHeader();
	}	//	afterDelete
	
	/**
	 * 	Update Header
	 *	@return header updated
	 */
	private boolean updateHeader()
	{
		log.fine("");
		String sql = "UPDATE M_Requisition r"
			+ " SET TotalLines="
				+ "(SELECT COALESCE(SUM(LineNetAmt),0) FROM M_RequisitionLine rl "
				+ "WHERE r.M_Requisition_ID=rl.M_Requisition_ID), "
				+ " TotalQty="
				+ "(SELECT COALESCE(SUM(Qty),0) FROM M_RequisitionLine rl "
				+ "WHERE r.M_Requisition_ID=rl.M_Requisition_ID) "
			+ "WHERE M_Requisition_ID=?";
		int no = DB.executeUpdateEx(sql, new Object[]{getM_Requisition_ID()}, get_TrxName());
		if (no != 1)
			log.log(Level.SEVERE, "Header update #" + no);
		m_parent = null;
		return no == 1;
	}	//	updateHeader
	
	/**
	 * 	Get and calculate Product Pricing
	 *	@param M_PriceList_ID id
	 *	@return product pricing
	 */
	protected IProductPricing getProductPricing (int M_PriceList_ID)
	{
		m_productPrice = Core.getProductPricing();
		m_productPrice.setRequisitionLine(this, get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice
	
}	//	MRequisitionLine
