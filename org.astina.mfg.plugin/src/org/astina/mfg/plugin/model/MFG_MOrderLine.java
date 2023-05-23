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
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.base.IProductPricing;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.ProductNotOnPriceListException;
import org.adempiere.model.ITaxProvider;
import org.adempiere.model.POWrapper;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MDocType;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MRole;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUOM;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *  Order Line Model.
 * 	<code>
 * 			MOrderLine ol = new MOrderLine(m_order);
			ol.setM_Product_ID(wbl.getM_Product_ID());
			ol.setQtyOrdered(wbl.getQuantity());
			ol.setPrice();
			ol.setPriceActual(wbl.getPrice());
			ol.setTax();
			ol.saveEx();

 *	</code>
 *  @author Jorg Janke
 *  @version $Id: MOrderLine.java,v 1.6 2006/10/02 05:18:39 jjanke Exp $
 * 
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 *			<li>BF [ 2588043 ] Insufficient message ProductNotOnPriceList
 */
public class MFG_MOrderLine extends MOrderLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7152360636393521683L;

	/**	Logger	*/
	protected static CLogger s_log = CLogger.getCLogger (MFG_MOrderLine.class);
	
	public MFG_MOrderLine (Properties ctx, int C_OrderLine_ID, String trxName)
	{
		super (ctx, C_OrderLine_ID, trxName);
	}	//	MOrderLine
	
	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MFG_MOrderLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MOrderLine
	
	/**
	 *  Parent Constructor.
	 		ol.setM_Product_ID(wbl.getM_Product_ID());
			ol.setQtyOrdered(wbl.getQuantity());
			ol.setPrice();
			ol.setPriceActual(wbl.getPrice());
			ol.setTax();
			ol.saveEx();
	 *  @param  order parent order
	 */
	public MFG_MOrderLine (MFG_MOrder order)
	{
		this (order.getCtx(), 0, order.get_TrxName());
		if (order.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setC_Order_ID (order.getC_Order_ID());	//	parent
		setOrder(order);
	}	//	MOrderLine

	protected int 			m_M_PriceList_ID = 0;
	//
	protected boolean			m_IsSOTrx = true;
	//	Product Pricing
	protected IProductPricing	m_productPrice = null;

	/** Parent					*/
	protected MFG_MOrder			m_parent = null;
	
	/**
	 * 	Set Defaults from Order.
	 * 	Does not set Parent !!
	 * 	@param order order
	 */
	public void setOrder (MFG_MOrder order)
	{
		setClientOrg(order);
		setC_BPartner_ID(order.getC_BPartner_ID());
		setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
		setM_Warehouse_ID(order.getM_Warehouse_ID());
		setDateOrdered(order.getDateOrdered());
		setDatePromised(order.getDatePromised());
		setC_Currency_ID(order.getC_Currency_ID());
		//
		setHeaderInfo(order);	//	sets m_order
		//	Don't set Activity, etc as they are overwrites
	}	//	setOrder

	/**
	 * 	Set Header Info
	 *	@param order order
	 */
	public void setHeaderInfo (MFG_MOrder order)
	{
		m_parent = order;
		m_precision = Integer.valueOf(order.getPrecision());
		m_M_PriceList_ID = order.getM_PriceList_ID();
		m_IsSOTrx = order.isSOTrx();
	}	//	setHeaderInfo
	
	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MFG_MOrder getParent()
	{
		if (m_parent == null)
			m_parent = new MFG_MOrder(getCtx(), getC_Order_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	/**
	 * 	Set Price for Product and PriceList.
	 * 	Use only if newly created.
	 * 	Uses standard price list of not set by order constructor
	 */
	public void setPrice()
	{
		if (getM_Product_ID() == 0)
			return;
		if (m_M_PriceList_ID == 0)
			throw new IllegalStateException("PriceList unknown!");
		setPrice (m_M_PriceList_ID);
	}	//	setPrice

	/**
	 * 	Set Price for Product and PriceList
	 * 	@param M_PriceList_ID price list
	 */
	public void setPrice (int M_PriceList_ID)
	{
		if (getM_Product_ID() == 0)
			return;
		//
		if (log.isLoggable(Level.FINE)) log.fine(toString() + " - M_PriceList_ID=" + M_PriceList_ID);
		getProductPricing (M_PriceList_ID);
		setPriceActual (m_productPrice.getPriceStd());
		setPriceList (m_productPrice.getPriceList());
		setPriceLimit (m_productPrice.getPriceLimit());
		//
		if (getQtyEntered().compareTo(getQtyOrdered()) == 0)
			setPriceEntered(getPriceActual());
		else
			setPriceEntered(getPriceActual().multiply(getQtyOrdered()
				.divide(getQtyEntered(), 12, RoundingMode.HALF_UP)));	//	recision
		
		//	Calculate Discount
		setDiscount(m_productPrice.getDiscount());
		//	Set UOM
		if (getC_UOM_ID()==0)
			setC_UOM_ID(m_productPrice.getC_UOM_ID());
	}	//	setPrice
	
	/**
	 * 	Get and calculate Product Pricing
	 *	@param M_PriceList_ID id
	 *	@return product pricing
	 */
	protected IProductPricing getProductPricing (int M_PriceList_ID)
	{
		m_productPrice = Core.getProductPricing();
		m_productPrice.setOrderLine(this, get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice
	
	//Astina 081222
	public void setQtyRequisition (BigDecimal QtyRequisition)
	{
		MProduct product = getProduct();
		if (QtyRequisition != null && product != null)
		{
			int precision = product.getUOMPrecision();
			QtyRequisition = QtyRequisition.setScale(precision, RoundingMode.HALF_UP);
		}
		MFG_MOrderLine orderline = new MFG_MOrderLine(Env.getCtx(), get_ID(),get_TrxName());
		I_C_OrderLine_Mfg corderline = POWrapper.create(orderline, I_C_OrderLine_Mfg.class);
		corderline.setQtyRequisition(QtyRequisition);
	}	//	setQtyRequisition
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return true if it can be saved
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		if (newRecord && getParent().isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "C_Order_ID"));
			return false;
		}
		
		//Astina 081222
		I_C_OrderLine_Mfg corderline = POWrapper.create(this, I_C_OrderLine_Mfg.class);
		
		//	Get Defaults from Parent
		if (getC_BPartner_ID() == 0 || getC_BPartner_Location_ID() == 0
			|| getM_Warehouse_ID() == 0 
			|| getC_Currency_ID() == 0)
			setOrder (getParent());
		if (m_M_PriceList_ID == 0)
			setHeaderInfo(getParent());

		
		//	R/O Check - Product/Warehouse Change
		if (!newRecord 
			&& (is_ValueChanged("M_Product_ID") || is_ValueChanged("M_Warehouse_ID") || 
			(!getParent().isProcessed() && is_ValueChanged(COLUMNNAME_M_AttributeSetInstance_ID)))) 
		{
			if (!canChangeWarehouse())
				return false;
		}	//	Product Changed
		
		//	Charge
		if (getC_Charge_ID() != 0 && getM_Product_ID() != 0)
		{
				setM_Product_ID(0);
				//Astina 081222
				corderline.setProductType("");
		}
		//	No Product
		if (getM_Product_ID() == 0)
			setM_AttributeSetInstance_ID(0);
		//	Product
		else if (!isProcessed())	//	Set/check Product Price
		{
			//	Set Price if Actual = 0
			if (m_productPrice == null 
				&&  Env.ZERO.compareTo(getPriceActual()) == 0
				&&  Env.ZERO.compareTo(getPriceList()) == 0)
				setPrice();
			//	Check if on Price list
			if (m_productPrice == null)
				getProductPricing(m_M_PriceList_ID);
			// IDEMPIERE-1574 Sales Order Line lets Price under the Price Limit when updating
			//	Check PriceLimit
			boolean enforce = m_IsSOTrx && getParent().getM_PriceList().isEnforcePriceLimit();
			if (enforce && MRole.getDefault().isOverwritePriceLimit())
				enforce = false;
			//	Check Price Limit?
			if (enforce && getPriceLimit() != Env.ZERO
			  && getPriceActual().compareTo(getPriceLimit()) < 0)
			{
				log.saveError("UnderLimitPrice", "PriceEntered=" + getPriceEntered() + ", PriceLimit=" + getPriceLimit()); 
				return false;
			}
			int C_DocType_ID = getParent().getDocTypeID();
			MDocType docType = MDocType.get(getCtx(), C_DocType_ID);
			//
			if (!docType.isNoPriceListCheck() && !m_productPrice.isCalculated())
			{
				throw new ProductNotOnPriceListException(m_productPrice, getLine());
			}
			
			//Astina 081222 auto ASI if PO
			MProduct product = MProduct.get(Env.getCtx(), getM_Product_ID());
			corderline.setProductType(product.getProductType());
			if(newRecord && product.getM_AttributeSet_ID()>0 && !m_IsSOTrx)
			{
				MAttributeSetInstance msi = MAttributeSetInstance.generateLot(Env.getCtx(), product, get_TrxName());
				setM_AttributeSetInstance_ID(msi.getM_AttributeSetInstance_ID());
			}	
		}

		//	UOM
		if (getC_UOM_ID() == 0)
			setDefaultC_UOM_ID();
		//	Qty Precision
		if (newRecord || is_ValueChanged("QtyEntered"))
			setQtyEntered(getQtyEntered());
		if (newRecord || is_ValueChanged("QtyOrdered"))
			setQtyOrdered(getQtyOrdered());
		
		//Astina 081222
		if (!newRecord && is_ValueChanged("QtyEntered"))
		{   //get current value and compare to new value if new value bigger than do nothing
			
			BigDecimal a = getQtyEntered();
			if(a.compareTo(getQtyDelivered())<0)
			{
				log.saveError("Enter Qty cannot under Receipt Qty", Msg.translate(getCtx(), "QtyEntered"));
				return false;
			}
			
			if(corderline.getM_Requisitionline_ID() > 0)
			{
				
				if(getQtyOrdered().compareTo(corderline.getQtyRequisition()) >0)
				{
					log.saveError("Enter Qty cannot over current Qty", Msg.translate(getCtx(), "QtyEntered"));
					return false;
				}
				//if(getQtyEntered().compareTo(corderline.getQtyRequisition()) < 0)
				//{
					MFG_MRequisitionLine.addQtyRemainsPO(getCtx(), corderline.getM_Requisitionline_ID(), corderline.getQtyRequisition().subtract(getQtyOrdered()), get_TrxName());
					corderline.setQtyRequisition(getQtyOrdered());
					MFG_MRequisitionLine MreqLine = new MFG_MRequisitionLine (Env.getCtx(), corderline.getM_Requisitionline_ID(), get_TrxName());
					MFG_MRequisition Mreq = new MFG_MRequisition (Env.getCtx(), MreqLine.getM_Requisition_ID(), get_TrxName());
					if(Mreq.getDocStatus().equals("CL"))
					{
						Mreq.setDocStatus("CO");
						Mreq.setDocAction("CL");
						Mreq.saveEx();
					}
				//}
			}
		}
		
		//	FreightAmt Not used
		if (Env.ZERO.compareTo(getFreightAmt()) != 0)
			setFreightAmt(Env.ZERO);

		//	Set Tax
		if (getC_Tax_ID() == 0)
			setTax();

		//	Get Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_OrderLine WHERE C_Order_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getC_Order_ID());
			setLine (ii);
		}
		
		//	Calculations & Rounding
		setLineNetAmt();	//	extended Amount with or without tax
		setDiscount();

		/* Carlos Ruiz - globalqss
		 * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		 */
		if (getParent().getC_DocTypeTarget().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0 && (getPriceEntered().signum() != 0 || getQtyEntered().signum() != 0)) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}
		
		/**
		//Astina 110223
		//String sql1 = "SELECT DocSubTypeSO FROM C_DocType cd "
		//		+ "join C_Order mr on mr.c_doctypetarget_id = cd.c_doctype_id "
		//		+ "WHERE C_Order_ID=?";
		//String docType = DB.getSQLValueStringEx (get_TrxName(), sql1, getC_Order_ID());
		
		I_C_OrderLine_Mfg orderline = POWrapper.create(this, I_C_OrderLine_Mfg.class);
		if (newRecord && getParent().isSOTrx())
		{
			if(getParent().getC_DocTypeTarget().getDocSubTypeSO().equals("ON") || getParent().getC_DocTypeTarget().getDocSubTypeSO().equals("OB"))
			{
				orderline.setQtyRemains_SO(this.getQtyEntered());
			}else
				orderline.setQtyRemains_SO(Env.ZERO);
			
		}else if(getParent().isSOTrx())
		{
			if((is_ValueChanged("QtyEntered") && getParent().getC_DocTypeTarget().getDocSubTypeSO().equals("ON")) || (is_ValueChanged("QtyEntered") && getParent().getC_DocTypeTarget().getDocSubTypeSO().equals("OB")))
			{
				//Alex Sembiring
				//Check Qty on PO
				
				MFG_MOrderLine reqline = new MFG_MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());
				I_C_OrderLine_Mfg mreqlines = POWrapper.create(this, I_C_OrderLine_Mfg.class);
				//check void
				if(getQtyEntered().compareTo(BigDecimal.valueOf(0)) == 0 && orderline.getQtyRemains_SO().compareTo(reqline.getQtyEntered()) != 0)
				{
					log.saveError("Qty on SO", Msg.translate(getCtx(), "Change Qty not Allowed"));
					return false;
				}else 
				{	
					if(getQtyEntered().compareTo(BigDecimal.valueOf(0)) != 0)
					{
						//decrease qty input
						if(getQtyEntered().compareTo(reqline.getQtyEntered()) < 0) 
						{
							if (mreqlines.getQtyRemains_SO().compareTo(BigDecimal.valueOf(0)) == 0 || reqline.getQtyEntered().subtract(getQtyEntered()).compareTo(mreqlines.getQtyRemains_SO()) > 0)
							{
								log.saveError("Qty on SO", Msg.translate(getCtx(), "Change Qty not Allowed"));
								return false;
							}
							if(mreqlines.getQtyRemains_SO().compareTo(reqline.getQtyEntered()) == 0)
							{
								mreqlines.setQtyRemains_SO(getQtyEntered());
							}else
								mreqlines.setQtyRemains_SO(mreqlines.getQtyRemains_SO().subtract(reqline.getQtyEntered().subtract(getQtyEntered())));
						}else
							if(getQtyEntered().compareTo(reqline.getQtyEntered()) > 0)
							{
								mreqlines.setQtyRemains_SO(mreqlines.getQtyRemains_SO().add(getQtyEntered().subtract(reqline.getQtyEntered())));
							}
					}else {
						//Fill Qty Remains zero value for void
						mreqlines.setQtyRemains_SO(BigDecimal.valueOf(0));
					}
				}
			}
		}
		//End Astina
		*/
		
		return true;
	}	//	beforeSave
	
	/***
	 * Sets the default unit of measure
	 * If there's a product, it sets the UOM of the product
	 * If not, it sets the default UOM of the client
	 */
	private void setDefaultC_UOM_ID() {
		int C_UOM_ID = 0;
		
		if (MProduct.get(getCtx(), getM_Product_ID()) != null) {
			C_UOM_ID = MProduct.get(getCtx(), getM_Product_ID()).getC_UOM_ID();	
		} else {
			C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
		}

		if (C_UOM_ID > 0)
			setC_UOM_ID (C_UOM_ID);
	}
	
	/**
	 * 	Before Delete
	 *	@return true if it can be deleted
	 */
	protected boolean beforeDelete ()
	{
		//	R/O Check - Something delivered. etc.
		if (Env.ZERO.compareTo(getQtyDelivered()) != 0)
		{
			log.saveError("DeleteError", Msg.translate(getCtx(), "QtyDelivered") + "=" + getQtyDelivered());
			return false;
		}
		if (Env.ZERO.compareTo(getQtyInvoiced()) != 0)
		{
			log.saveError("DeleteError", Msg.translate(getCtx(), "QtyInvoiced") + "=" + getQtyInvoiced());
			return false;
		}
		if (Env.ZERO.compareTo(getQtyReserved()) != 0)
		{
			//	For PO should be On Order
			log.saveError("DeleteError", Msg.translate(getCtx(), "QtyReserved") + "=" + getQtyReserved());
			return false;
		}
		
		//Astina Delete if Unreceipt before
		DB.executeUpdateEx("Update m_costdetail set C_OrderLine_ID = null WHERE qty = 0 and C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		DB.executeUpdateEx("Update m_matchpo set C_OrderLine_ID = null WHERE reversal_id>0 and C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		DB.executeUpdateEx("Update m_inoutline set C_OrderLine_ID = null WHERE M_InOut_ID IN (Select M_InOut_ID from M_InOut where DocStatus IN ('RE', 'VO')) and C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		DB.executeUpdateEx("Update m_inoutline set C_OrderLine_ID = null WHERE M_InOut_ID IN (Select M_InOut_ID from M_InOut where DocStatus IN ('CO')) and QtyEntered = 0 and C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		DB.executeUpdateEx("Update c_invoiceline set C_OrderLine_ID = null WHERE C_Invoice_ID IN (Select C_Invoice_ID from C_Invoice where DocStatus IN ('RE', 'VO')) and C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		DB.executeUpdateEx("delete from PP_MRP where C_OrderLine_ID=" + getC_OrderLine_ID(), get_TrxName());
		
		I_C_OrderLine_Mfg corderline = POWrapper.create(this, I_C_OrderLine_Mfg.class);
		if(corderline.getM_Requisitionline_ID() > 0)
		{
			MFG_MRequisitionLine.addQtyRemainsPO(getCtx(), corderline.getM_Requisitionline_ID(), corderline.getQtyRequisition(), get_TrxName());
			MFG_MRequisitionLine MreqLine = new MFG_MRequisitionLine (Env.getCtx(), corderline.getM_Requisitionline_ID(), get_TrxName());
			MFG_MRequisition Mreq = new MFG_MRequisition (Env.getCtx(), MreqLine.getM_Requisition_ID(), get_TrxName());
			if(Mreq.getDocStatus().equals("CL"))
			{
				Mreq.setDocStatus("CO");
				Mreq.setDocAction("CL");
				Mreq.saveEx();
			}
		}
		
				// UnLink All Requisitions
		MFG_MRequisitionLine.unlinkC_OrderLine_ID(getCtx(), get_ID(), get_TrxName());
		
		return true;
	}	//	beforeDelete
	
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return saved
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		
		//Astina 081222
		if (!newRecord && is_ValueChanged("QtyDelivered"))
		{   //get current value and compare to new value if new value bigger than do nothing
			
			I_C_OrderLine_Mfg corderline = POWrapper.create(this, I_C_OrderLine_Mfg.class);
			
			if(corderline.getM_Requisitionline_ID() > 0)
			{
				MFG_MRequisitionLine mr = new MFG_MRequisitionLine(getCtx(), corderline.getM_Requisitionline_ID(),get_TrxName());
				BigDecimal sumReceipt = DB.getSQLValueBD(get_TrxName(),
						"select SUM(co.qtydelivered) from c_orderline co "
						+ "join m_requisitionline mr on mr.m_requisitionline_id = co.m_requisitionline_id "
						+ "where mr.m_requisition_id = ? ",
						mr.getM_Requisition_ID());
				
				BigDecimal sumPR = DB.getSQLValueBD(get_TrxName(),
						"select SUM(mr.qty) from m_requisitionline mr "
						+ "where mr.m_requisition_id = ? ",
						mr.getM_Requisition_ID());
				
				if(sumReceipt.compareTo(sumPR) >= 0)
				{
					MFG_MRequisition Mreq = new MFG_MRequisition (Env.getCtx(), mr.getM_Requisition_ID(), get_TrxName());
					if(Mreq.getDocStatus().equals("CO"))
					{
						Mreq.setDocStatus("CL");
						Mreq.saveEx();
					}
				}
				
				//}
			}
		}
		
		if (getParent().isProcessed())
			return success;
		if (   newRecord
			|| is_ValueChanged(MFG_MOrderLine.COLUMNNAME_C_Tax_ID)
			|| is_ValueChanged(MFG_MOrderLine.COLUMNNAME_LineNetAmt)) {
			MTax tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
	        MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());
			ITaxProvider calculator = Core.getTaxProvider(provider);
			if (calculator == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
	    	return calculator.recalculateTax(provider, this, newRecord);
		}
		return success;
	}	//	afterSave
	
	/**
	 * 	Get Tax (immutable)
	 *	@return tax
	 */
	protected MTax getTax()
	{
		if (m_tax == null)
			m_tax = MTax.get(getCtx(), getC_Tax_ID());
		return m_tax;
	}	//	getTax
	
}	//	MOrderLine
