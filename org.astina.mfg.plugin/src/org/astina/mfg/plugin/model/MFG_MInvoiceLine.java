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
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.ITaxProvider;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MRole;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUOM;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *	Invoice Line Model
 *
 *  @author Jorg Janke
 *  @version $Id: MInvoiceLine.java,v 1.5 2006/07/30 00:51:03 jjanke Exp $
 * 
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>BF [ 2804142 ] MInvoice.setRMALine should work only for CreditMemo invoices
 * 				https://sourceforge.net/p/adempiere/bugs/1937/
 * @author red1 FR: [ 2214883 ] Remove SQL code and Replace for Query
 */
public class MFG_MInvoiceLine extends MInvoiceLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1590896898028805978L;

	/**	Static Logger	*/
	protected static CLogger	s_log	= CLogger.getCLogger (MFG_MInvoiceLine.class);

	/** Tax							*/
	protected MTax 		m_tax = null;
	
	
	/**************************************************************************
	 * 	Invoice Line Constructor
	 * 	@param ctx context
	 * 	@param C_InvoiceLine_ID invoice line or 0
	 * 	@param trxName transaction name
	 */
	public MFG_MInvoiceLine (Properties ctx, int C_InvoiceLine_ID, String trxName)
	{
		this (ctx, C_InvoiceLine_ID, trxName, (String[]) null);
	}	//	MInvoiceLine

	public MFG_MInvoiceLine(Properties ctx, int C_InvoiceLine_ID, String trxName, String... virtualColumns) {
		super(ctx, C_InvoiceLine_ID, trxName, virtualColumns);
		if (C_InvoiceLine_ID == 0)
		{
			setIsDescription(false);
			setIsPrinted (true);
			setLineNetAmt (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceActual (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setM_AttributeSetInstance_ID(0);
			setTaxAmt(Env.ZERO);
			//
			setQtyEntered(Env.ZERO);
			setQtyInvoiced(Env.ZERO);
		}
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MFG_MInvoiceLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInvoiceLine
	
	/**
	 * 	Calculate Tax Amt.
	 * 	Assumes Line Net is calculated
	 */
	public void setTaxAmt ()
	{
		BigDecimal TaxAmt = Env.ZERO;
		if (getC_Tax_ID() == 0)
			return;
		setLineNetAmt();
		MTax tax = MTax.get (getCtx(), getC_Tax_ID());
		if (tax.isDocumentLevel() && m_IsSOTrx)		//	AR Inv Tax
			return;
		//
		TaxAmt = tax.calculateTax(getLineNetAmt(), isTaxIncluded(), getPrecision());
		if (isTaxIncluded())
			setLineTotalAmt(getLineNetAmt());
		else
			setLineTotalAmt(getLineNetAmt().add(TaxAmt));
		super.setTaxAmt (TaxAmt);
	}	//	setTaxAmt
	
	/**
	 * 	Calculate Extended Amt.
	 * 	May or may not include tax
	 */
	public void setLineNetAmt ()
	{
		//	Calculations & Rounding
		//Astina 241222
		BigDecimal bd = this.getPriceEntered().multiply(getQtyEntered()); //getPriceActual().multiply(getQtyInvoiced());
		int precision = getPrecision();
		if (bd.scale() > precision)
			bd = bd.setScale(precision, RoundingMode.HALF_UP);
		super.setLineNetAmt (bd);
	}	//	setLineNetAmt
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return true if save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		if (log.isLoggable(Level.FINE)) log.fine("New=" + newRecord);
		boolean parentComplete = getParent().isProcessed();
		boolean isReversal = getParent().isReversal();
		if (newRecord && parentComplete) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "C_Invoice_ID"));
			return false;
		}
		// Re-set invoice header (need to update m_IsSOTrx flag) - phib [ 1686773 ]
		setInvoice(getParent());

	  if (!parentComplete && !isReversal) {  // do not change things when parent is complete
		//	Charge
		if (getC_Charge_ID() != 0)
		{
			if (getM_Product_ID() != 0)
				setM_Product_ID(0);
		}
		else	//	Set Product Price
		{
			if (!m_priceSet
				&&  Env.ZERO.compareTo(getPriceActual()) == 0
				&&  Env.ZERO.compareTo(getPriceList()) == 0)
				setPrice();
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
				//
		}

		//	Set Tax
		if (getC_Tax_ID() == 0)
			setTax();

		//	Get Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_InvoiceLine WHERE C_Invoice_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getC_Invoice_ID());
			setLine (ii);
		}
		//	UOM
		if (getC_UOM_ID() == 0)
		{
			int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
			if (C_UOM_ID > 0)
				setC_UOM_ID (C_UOM_ID);
		}
		//	Qty Precision
		if (newRecord || is_ValueChanged("QtyEntered"))
			setQtyEntered(getQtyEntered());
		if (newRecord || is_ValueChanged("QtyInvoiced"))
			setQtyInvoiced(getQtyInvoiced());

		//	Calculations & Rounding
		setLineNetAmt();
		// TaxAmt recalculations should be done if the TaxAmt is zero
		// or this is an Invoice(Customer) - teo_sarca, globalqss [ 1686773 ]
		
		//Comment by Astina Exclude IsSOTrx
		//if (m_IsSOTrx || getTaxAmt().compareTo(Env.ZERO) == 0)
		
		//Astina Include Invoice (Customer)
		if (getTaxAmt().compareTo(Env.ZERO) == 0)
			setTaxAmt();
		//
		
		/* Carlos Ruiz - globalqss
		 * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		 */
		if (getParent().getC_DocTypeTarget().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0 && (getPriceEntered().signum() != 0 || getQtyEntered().signum() != 0)) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}
	  }
		
		return true;
	}	//	beforeSave
	
	/**
	 * Recalculate invoice tax
	 * @param oldTax true if the old C_Tax_ID should be used
	 * @return true if success, false otherwise
	 *
	 * author teo_sarca [ 1583825 ]
	 */
	protected boolean updateInvoiceTax(boolean oldTax) {
		int C_Tax_ID = getC_Tax_ID();
		boolean isOldTax = oldTax && is_ValueChanged(MInvoiceTax.COLUMNNAME_C_Tax_ID); 
		if (isOldTax)
		{
			Object old = get_ValueOld(MInvoiceTax.COLUMNNAME_C_Tax_ID);
			if (old == null)
			{
				return true;
			}
			C_Tax_ID = ((Integer)old).intValue();
		}
		if (C_Tax_ID == 0)
		{
			return true;
		}
		
		MTax t = MTax.get(C_Tax_ID);
		if (t.isSummary())
		{
			MFG_MInvoiceTax[] invoiceTaxes = MFG_MInvoiceTax.getChildTaxes(this, getPrecision(), oldTax, get_TrxName());
			if (invoiceTaxes != null && invoiceTaxes.length > 0)
			{
				for(MFG_MInvoiceTax tax : invoiceTaxes)
				{
					if (!tax.calculateTaxFromLines())
						return false;
				
					if (!tax.save(get_TrxName()))
						return false;
				}
			}
		}
		else
		{
			MFG_MInvoiceTax tax = MFG_MInvoiceTax.get (this, getPrecision(), oldTax, get_TrxName());
			if (tax != null) {
				if (!tax.calculateTaxFromLines())
					return false;
			
				// red1 - solving BUGS #[ 1701331 ] , #[ 1786103 ]
				if (!tax.save(get_TrxName()))
					return false;
			}
		}
		return true;
	}
	
	/**
	 *	Update Tax and Header
	 *	@return true if header updated with tax
	 */
	public boolean updateHeaderTax()
	{
		// Update header only if the document is not processed - teo_sarca BF [ 2317305 ]
		if (isProcessed() && !is_ValueChanged(COLUMNNAME_Processed))
			return true;

		//	Recalculate Tax for this Tax
        MTax tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
        MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());
		ITaxProvider calculator = Core.getTaxProvider(provider);
		if (calculator == null)
			throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
    	if (!calculator.updateInvoiceTax(provider, this))
			return false;

		return calculator.updateHeaderTax(provider, this);
	}	//	updateHeaderTax

	public void clearParent()
	{
		this.m_parent = null;
	}

}	//	MInvoiceLine
