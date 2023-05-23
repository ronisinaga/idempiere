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
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.FillMandatoryException;
import org.adempiere.model.POWrapper;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderPaySchedule;
import org.compiere.model.MOrderTax;
import org.compiere.model.MOrg;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MWarehouse;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 *  Order Model.
 * 	Please do not set DocStatus and C_DocType_ID directly. 
 * 	They are set in the process() method. 
 * 	Use DocAction and C_DocTypeTarget_ID instead.
 *
 *  @author Jorg Janke
 *
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  @version $Id: MOrder.java,v 1.5 2006/10/06 00:42:24 jjanke Exp $
 * 
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>BF [ 2419978 ] Voiding PO, requisition don't set on NULL
 * 			<li>BF [ 2892578 ] Order should autoset only active price lists
 * 				https://sourceforge.net/p/adempiere/feature-requests/873/
 * @author Michael Judd, www.akunagroup.com
 *          <li>BF [ 2804888 ] Incorrect reservation of products with attributes
 */
public class MFG_MOrder extends MOrder implements DocAction, DocOptions
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7784588474522162502L;
	
	/**
	 * 	Create new Order by copying
	 * 	@param from order
	 * 	@param dateDoc date of the document date
	 * 	@param C_DocTypeTarget_ID target document type
	 * 	@param isSOTrx sales order 
	 * 	@param counter create counter links
	 *	@param copyASI copy line attributes Attribute Set Instance, Resaouce Assignment
	 * 	@param trxName trx
	 *	@return Order
	 */
	public static MFG_MOrder copyFrom (MFG_MOrder from, Timestamp dateDoc, 
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter, boolean copyASI, 
		String trxName)
	{
		MFG_MOrder to = new MFG_MOrder (from.getCtx(), 0, trxName);
		to.set_TrxName(trxName);
		PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.set_ValueNoCheck ("C_Order_ID", I_ZERO);
		to.set_ValueNoCheck ("DocumentNo", null);
		//
		to.setDocStatus (DOCSTATUS_Drafted);		//	Draft
		to.setDocAction(DOCACTION_Complete);
		//
		to.setC_DocType_ID(0);
		to.setC_DocTypeTarget_ID (C_DocTypeTarget_ID);
		to.setIsSOTrx(isSOTrx);
		//
		to.setIsSelected (false);
		to.setDateOrdered (dateDoc);
		to.setDateAcct (dateDoc);
		to.setDatePromised (dateDoc);	//	assumption
		to.setDatePrinted(null);
		to.setIsPrinted (false);
		//
		to.setIsApproved (false);
		to.setIsCreditApproved(false);
		to.setC_Payment_ID(0);
		to.setC_CashLine_ID(0);
		//	Amounts are updated  when adding lines
		to.setGrandTotal(Env.ZERO);
		to.setTotalLines(Env.ZERO);
		//
		to.setIsDelivered(false);
		to.setIsInvoiced(false);
		to.setIsSelfService(false);
		to.setIsTransferred (false);
		to.setPosted (false);
		to.setProcessed (false);
		if (counter) {
			to.setRef_Order_ID(from.getC_Order_ID());
			MOrg org = MOrg.get(from.getCtx(), from.getAD_Org_ID());
			int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(trxName);
			if (counterC_BPartner_ID == 0)
				return null;
			to.setBPartner(MBPartner.get(from.getCtx(), counterC_BPartner_ID));
		} else
			to.setRef_Order_ID(0);
		//
		if (!to.save(trxName))
			throw new IllegalStateException("Could not create Order");
		if (counter){
			// save to other counter document can re-get refer document  
			from.setRef_Order_ID(to.getC_Order_ID());
			from.saveEx();
		}

		if (to.copyLinesFrom(from, counter, copyASI) == 0)
			throw new IllegalStateException("Could not create Order Lines");
		
		// don't copy linked PO/SO
		to.setLink_Order_ID(0);
		
		return to;
	}	//	copyFrom
	
	/**
	 * 	Copy Lines From other Order
	 *	@param otherOrder order
	 *	@param counter set counter info
	 *	@param copyASI copy line attributes Attribute Set Instance, Resaouce Assignment
	 *	@return number of lines copied
	 */
	public int copyLinesFrom (MFG_MOrder otherOrder, boolean counter, boolean copyASI)
	{
		if (isProcessed() || isPosted() || otherOrder == null)
			return 0;
		MFG_MOrderLine[] fromLines = otherOrder.getLines(false, null);
		int count = 0;
		for (int i = 0; i < fromLines.length; i++)
		{
			MFG_MOrderLine line = new MFG_MOrderLine (this);
			PO.copyValues(fromLines[i], line, getAD_Client_ID(), getAD_Org_ID());
			line.setC_Order_ID(getC_Order_ID());
			//
			line.setQtyDelivered(Env.ZERO);
			line.setQtyInvoiced(Env.ZERO);
			line.setQtyReserved(Env.ZERO);
			line.setQtyLostSales(Env.ZERO);
			line.setDateDelivered(null);
			line.setDateInvoiced(null);
			line.setPrice(fromLines[i].getPriceEntered());
			//
			line.setOrder(this);
			line.set_ValueNoCheck ("C_OrderLine_ID", I_ZERO);	//	new
			//	References
			if (!copyASI)
			{
				line.setM_AttributeSetInstance_ID(0);
				line.setS_ResourceAssignment_ID(0);
			}
			if (counter)
				line.setRef_OrderLine_ID(fromLines[i].getC_OrderLine_ID());
			else
				line.setRef_OrderLine_ID(0);

			// don't copy linked lines
			line.setLink_OrderLine_ID(0);
			//	Tax
			if (getC_BPartner_ID() != otherOrder.getC_BPartner_ID())
				line.setTax();		//	recalculate
			//
			//
			line.setProcessed(false);
			
			if (line.save(get_TrxName()))
				count++;
			//	Cross Link
			if (counter)
			{
				fromLines[i].setRef_OrderLine_ID(line.getC_OrderLine_ID());
				fromLines[i].saveEx(get_TrxName());
			}
		}
		if (fromLines.length != count)
			log.log(Level.SEVERE, "Line difference - From=" + fromLines.length + " <> Saved=" + count);
		return count;
	}	//	copyLinesFrom
	
	/**************************************************************************
	 *  Default Constructor
	 *  @param ctx context
	 *  @param  C_Order_ID    order to load, (0 create new order)
	 *  @param trxName trx name
	 */
	public MFG_MOrder(Properties ctx, int C_Order_ID, String trxName)
	{
		this (ctx, C_Order_ID, trxName, (String[]) null);
	}	//	MOrder

	public MFG_MOrder(Properties ctx, int C_Order_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Order_ID, trxName, virtualColumns);
		//  New
		if (C_Order_ID == 0)
		{
			setDocStatus(DOCSTATUS_Drafted);
			setDocAction (DOCACTION_Prepare);
			//
			setDeliveryRule (DELIVERYRULE_Availability);
			setFreightCostRule (FREIGHTCOSTRULE_FreightIncluded);
			setInvoiceRule (INVOICERULE_Immediate);
			setPaymentRule(PAYMENTRULE_OnCredit);
			setPriorityRule (PRIORITYRULE_Medium);
			setDeliveryViaRule (DELIVERYVIARULE_Pickup);
			//
			setIsDiscountPrinted (false);
			setIsSelected (false);
			setIsTaxIncluded (false);
			setIsSOTrx (true);
			setIsDropShip(false);
			setSendEMail (false);
			//
			setIsApproved(false);
			setIsPrinted(false);
			setIsCreditApproved(false);
			setIsDelivered(false);
			setIsInvoiced(false);
			setIsTransferred(false);
			setIsSelfService(false);
			//
			super.setProcessed(false);
			setProcessing(false);
			setPosted(false);

			setDateAcct (new Timestamp(System.currentTimeMillis()));
			setDatePromised (new Timestamp(System.currentTimeMillis()));
			setDateOrdered (new Timestamp(System.currentTimeMillis()));

			setFreightAmt (Env.ZERO);
			setChargeAmt (Env.ZERO);
			setTotalLines (Env.ZERO);
			setGrandTotal (Env.ZERO);
			
			I_C_Order_Mfg corder = POWrapper.create(this, I_C_Order_Mfg.class);
			corder.setRevisionNo(0);
		}
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MFG_MOrder (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MOrder

	/**	Order Lines					*/
	protected MFG_MOrderLine[] 	m_lines = null;
	/** Force Creation of order		*/
	protected boolean			m_forceCreation = false;
	
	/**************************************************************************
	 * 	Get Lines of Order
	 * 	@param whereClause where clause or null (starting with AND)
	 * 	@param orderClause order clause
	 * 	@return lines
	 */
	public MFG_MOrderLine[] getLines (String whereClause, String orderClause)
	{
		//red1 - using new Query class from Teo / Victor's MDDOrder.java implementation
		StringBuilder whereClauseFinal = new StringBuilder(MOrderLine.COLUMNNAME_C_Order_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MOrderLine.COLUMNNAME_Line;
		//
		List<MOrderLine> list = new Query(getCtx(), I_C_OrderLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		for (MOrderLine ol : list) {
			ol.setHeaderInfo(this);
		}
		//
		return list.toArray(new MFG_MOrderLine[list.size()]);		
	}	//	getLines

	/**
	 * 	Get Lines of Order
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return lines
	 */
	public MFG_MOrderLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line,C_OrderLine_ID";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines

	/**
	 * 	Get Lines of Order.
	 * 	(used by web store)
	 * 	@return lines
	 */
	public MFG_MOrderLine[] getLines()
	{
		return getLines(false, null);
	}	//	getLines
	
	/**************************************************************************
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	processIt
	
	/**	Process Message 			*/
	protected String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	protected boolean		m_justPrepared = false;
	
	/**************************************************************************
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid) 
	 */
	public String prepareIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		
		if (isSOTrx() && getDeliveryViaRule().equals(DELIVERYVIARULE_Shipper))
		{
			if (getM_Shipper_ID() == 0)
			{
				m_processMsg = "@FillMandatory@" + Msg.getElement(getCtx(), COLUMNNAME_M_Shipper_ID);
				return DocAction.STATUS_Invalid;
			}
			
			if (!calculateFreightCharge())
				return DocAction.STATUS_Invalid;
		}

		//	Lines
		MFG_MOrderLine[] lines = getLines(true, MFG_MOrderLine.COLUMNNAME_M_Product_ID);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}
				
		// Bug 1564431
		if (MFG_MOrder.DELIVERYRULE_CompleteOrder.equals(getDeliveryRule()) )
		{
			for (int i = 0; i < lines.length; i++) 
			{
				MFG_MOrderLine line = lines[i];
				MProduct product = line.getProduct();
				if (product != null && product.isExcludeAutoDelivery())
				{
					m_processMsg = "@M_Product_ID@ "+product.getValue()+" @IsExcludeAutoDelivery@";
					return DocAction.STATUS_Invalid;
				}
				if (line.getDatePromised() != null && !line.getDatePromised().equals(getDatePromised()))
				{
					m_processMsg = "@Line@ " + line.getLine() + " - @Invalid@ @DatePromised@";
					return DocAction.STATUS_Invalid;
				}
			}
		}
		
		//Astina 261222 Price Zero not Allowed
		//for (int j = 0; j < lines.length; j++) 
		//{
		//	MFG_MOrderLine line = lines[j];
		//	MProduct product = line.getProduct();
		//	if (line.getPriceEntered().compareTo(Env.ZERO)<=0)
		//	{
		//		m_processMsg = "@PriceEntered@ "+product.getValue()+" "+product.getName()+" = "+line.getPriceEntered();
		//		return DocAction.STATUS_Invalid;
		//	}
		//}//End Astina
		
		//	Convert DocType to Target
		if (getC_DocType_ID() != getC_DocTypeTarget_ID() )
		{
			//	Cannot change Std to anything else if different warehouses
			if (getC_DocType_ID() != 0)
			{
				MDocType dtOld = MDocType.get(getCtx(), getC_DocType_ID());
				if (MDocType.DOCSUBTYPESO_StandardOrder.equals(dtOld.getDocSubTypeSO())		//	From SO
					&& !MDocType.DOCSUBTYPESO_StandardOrder.equals(dt.getDocSubTypeSO()))	//	To !SO
				{
					for (int i = 0; i < lines.length; i++)
					{
						if (lines[i].getM_Warehouse_ID() != getM_Warehouse_ID())
						{
							log.warning("different Warehouse " + lines[i]);
							m_processMsg = "@CannotChangeDocType@";
							return DocAction.STATUS_Invalid;
						}
					}
				}
			}
			
			//	New or in Progress/Invalid
			if (DOCSTATUS_Drafted.equals(getDocStatus()) 
				|| DOCSTATUS_InProgress.equals(getDocStatus())
				|| DOCSTATUS_Invalid.equals(getDocStatus())
				|| getC_DocType_ID() == 0)
			{
				setC_DocType_ID(getC_DocTypeTarget_ID());
			}
			else	//	convert only if offer
			{
				if (dt.isOffer())
					setC_DocType_ID(getC_DocTypeTarget_ID());
				else
				{
					m_processMsg = "@CannotChangeDocType@";
					return DocAction.STATUS_Invalid;
				}
			}
		}	//	convert DocType
		
		//Astina 040223 add condition Non binding offer
		if(!dt.getName().equalsIgnoreCase("Non binding offer"))
		{
			//	Mandatory Product Attribute Set Instance
			for (MFG_MOrderLine line : getLines()) {
				if (line.getM_Product_ID() > 0 && line.getM_AttributeSetInstance_ID() == 0) {
					MProduct product = line.getProduct();
					if (product.isASIMandatoryFor(null, isSOTrx())) {
						if (product.getAttributeSet() != null && !product.getAttributeSet().excludeTableEntry(MFG_MOrderLine.Table_ID, isSOTrx())) {
							StringBuilder msg = new StringBuilder("@M_AttributeSet_ID@ @IsMandatory@ (@Line@ #")
								.append(line.getLine())
								.append(", @M_Product_ID@=")
								.append(product.getValue())
								.append(")");
							m_processMsg = msg.toString();
							return DocAction.STATUS_Invalid;
						}
						
						//Astina 140223 condition for Quotation
						if (isSOTrx() && product.getAttributeSet() != null && line.getM_Product_ID() > 0 
								&& line.getM_AttributeSetInstance_ID() == 0 && dt.getDocSubTypeSO().equalsIgnoreCase("OB")) {
							StringBuilder msg = new StringBuilder("@M_AttributeSet_ID@ @IsMandatory@ (@Line@ #")
								.append(line.getLine())
								.append(", @M_Product_ID@=")
								.append(product.getValue())
								.append(")");
							m_processMsg = msg.toString();
							return DocAction.STATUS_Invalid;
						}
						//End Astina
					}
				}
			}
		}

		//	Lines
		if (explodeBOM())
			lines = getLines(true, MFG_MOrderLine.COLUMNNAME_M_Product_ID);
		if (!reserveStock(dt, lines))
		{
			String innerMsg = CLogger.retrieveErrorString("");
			m_processMsg = "Cannot reserve Stock";
			if (! Util.isEmpty(innerMsg))
				m_processMsg = m_processMsg + " -> " + innerMsg;
			return DocAction.STATUS_Invalid;
		}
		if (!calculateTaxTotal())
		{
			m_processMsg = "Error calculating tax";
			return DocAction.STATUS_Invalid;
		}
		
		if (   getGrandTotal().signum() != 0
			&& (PAYMENTRULE_OnCredit.equals(getPaymentRule()) || PAYMENTRULE_DirectDebit.equals(getPaymentRule())))
		{
			if (!createPaySchedule())
			{
				m_processMsg = "@ErrorPaymentSchedule@";
				return DocAction.STATUS_Invalid;
			}
		} else {
			if (MOrderPaySchedule.getOrderPaySchedule(getCtx(), getC_Order_ID(), 0, get_TrxName()).length > 0) 
			{
				m_processMsg = "@ErrorPaymentSchedule@";
				return DocAction.STATUS_Invalid;
			}
		}
		
		//	Credit Check
		if (isSOTrx())
		{
			if (   MDocType.DOCSUBTYPESO_POSOrder.equals(dt.getDocSubTypeSO())
					&& PAYMENTRULE_Cash.equals(getPaymentRule())
					&& !MSysConfig.getBooleanValue(MSysConfig.CHECK_CREDIT_ON_CASH_POS_ORDER, true, getAD_Client_ID(), getAD_Org_ID())) {
				// ignore -- don't validate for Cash POS Orders depending on sysconfig parameter
			} else if (MDocType.DOCSUBTYPESO_PrepayOrder.equals(dt.getDocSubTypeSO())
					&& !MSysConfig.getBooleanValue(MSysConfig.CHECK_CREDIT_ON_PREPAY_ORDER, true, getAD_Client_ID(), getAD_Org_ID())) {
				// ignore -- don't validate Prepay Orders depending on sysconfig parameter
			} else {
				MBPartner bp = new MBPartner (getCtx(), getBill_BPartner_ID(), get_TrxName()); // bill bp is guaranteed on beforeSave

				if (getGrandTotal().signum() > 0)  // IDEMPIERE-365 - just check credit if is going to increase the debt
				{		 

					if (MBPartner.SOCREDITSTATUS_CreditStop.equals(bp.getSOCreditStatus()))
					{
						m_processMsg = "@BPartnerCreditStop@ - @TotalOpenBalance@=" 
								+ bp.getTotalOpenBalance()
								+ ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();
						return DocAction.STATUS_Invalid;
					}
					if (MBPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus()))
					{
						m_processMsg = "@BPartnerCreditHold@ - @TotalOpenBalance@=" 
								+ bp.getTotalOpenBalance() 
								+ ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();
						return DocAction.STATUS_Invalid;
					}
					BigDecimal grandTotal = MConversionRate.convertBase(getCtx(), 
							getGrandTotal(), getC_Currency_ID(), getDateOrdered(), 
							getC_ConversionType_ID(), getAD_Client_ID(), getAD_Org_ID());
					if (MBPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus(grandTotal)))
					{
						m_processMsg = "@BPartnerOverOCreditHold@ - @TotalOpenBalance@=" 
								+ bp.getTotalOpenBalance() + ", @GrandTotal@=" + grandTotal
								+ ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();
						return DocAction.STATUS_Invalid;
					}
				}
			}  
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		m_justPrepared = true;
		//Astina 081222
		if (DOCACTION_Prepare.equals(getDocAction()))
		{
			I_C_Order_Mfg corder = POWrapper.create(this, I_C_Order_Mfg.class);
			corder.setRevisionNo(corder.getRevisionNo()+1);
			this.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
			
		}
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/**************************************************************************
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		String DocSubTypeSO = dt.getDocSubTypeSO();
		
		//	Just prepare
		if (DOCACTION_Prepare.equals(getDocAction()))
		{
			setProcessed(false);
			//Astina 081222
			if (getDocStatus().equals(DocAction.STATUS_InProgress))
			{
				I_C_Order_Mfg corder = POWrapper.create(this, I_C_Order_Mfg.class);
				corder.setRevisionNo(corder.getRevisionNo()+1);
				this.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
			}
			return DocAction.STATUS_InProgress;
		}

		// Set the definite document number after completed (if needed)
		setDefiniteDocumentNo();

		//	Offers
		if (MDocType.DOCSUBTYPESO_Proposal.equals(DocSubTypeSO)
			|| MDocType.DOCSUBTYPESO_Quotation.equals(DocSubTypeSO)) 
		{
			//	Binding
			if (MDocType.DOCSUBTYPESO_Quotation.equals(DocSubTypeSO))
				reserveStock(dt, getLines(true, MFG_MOrderLine.COLUMNNAME_M_Product_ID));
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
			if (m_processMsg != null)
				return DocAction.STATUS_Invalid;
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
			if (m_processMsg != null)
				return DocAction.STATUS_Invalid;
			setProcessed(true);
			return DocAction.STATUS_Completed;
		}
		//	Waiting Payment - until we have a payment
		if (!m_forceCreation 
			&& MDocType.DOCSUBTYPESO_PrepayOrder.equals(DocSubTypeSO) 
			&& getC_Payment_ID() == 0 && getC_CashLine_ID() == 0)
		{
			setProcessed(true);
			return DocAction.STATUS_WaitingPayment;
		}
		
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		//	Implicit Approval
		if (!isApproved())
			approveIt();
		getLines(true,null);
		if (log.isLoggable(Level.INFO)) log.info(toString());
		StringBuilder info = new StringBuilder();
		
		boolean realTimePOS = MSysConfig.getBooleanValue(MSysConfig.REAL_TIME_POS, false , getAD_Client_ID());
		
		// Counter Documents
		// move by IDEMPIERE-2216
		MOrder counter = createCounterDoc();
		if (counter != null)
			info.append(" - @CounterDoc@: @Order@=").append(counter.getDocumentNo());
		
		//	Create SO Shipment - Force Shipment
		MInOut shipment = null;
		if (MDocType.DOCSUBTYPESO_OnCreditOrder.equals(DocSubTypeSO)		//	(W)illCall(I)nvoice
			|| MDocType.DOCSUBTYPESO_WarehouseOrder.equals(DocSubTypeSO)	//	(W)illCall(P)ickup	
			|| MDocType.DOCSUBTYPESO_POSOrder.equals(DocSubTypeSO)			//	(W)alkIn(R)eceipt
			|| MDocType.DOCSUBTYPESO_PrepayOrder.equals(DocSubTypeSO)) 
		{
			if (!DELIVERYRULE_Force.equals(getDeliveryRule()))
			{
				MWarehouse wh = new MWarehouse (getCtx(), getM_Warehouse_ID(), get_TrxName());
				if (!wh.isDisallowNegativeInv())
					setDeliveryRule(DELIVERYRULE_Force);
			}
			//
			shipment = createShipment (dt, realTimePOS ? null : getDateOrdered());
			if (shipment == null)
				return DocAction.STATUS_Invalid;
			info.append("@M_InOut_ID@: ").append(shipment.getDocumentNo());
			String msg = shipment.getProcessMsg();
			if (msg != null && msg.length() > 0)
				info.append(" (").append(msg).append(")");
		}	//	Shipment
		

		//	Create SO Invoice - Always invoice complete Order
		if ( MDocType.DOCSUBTYPESO_POSOrder.equals(DocSubTypeSO)
			|| MDocType.DOCSUBTYPESO_OnCreditOrder.equals(DocSubTypeSO) 	
			|| MDocType.DOCSUBTYPESO_PrepayOrder.equals(DocSubTypeSO)) 
		{
			MInvoice invoice = createInvoice (dt, shipment, realTimePOS ? null : getDateOrdered());
			if (invoice == null)
				return DocAction.STATUS_Invalid;
			info.append(" - @C_Invoice_ID@: ").append(invoice.getDocumentNo());
			String msg = invoice.getProcessMsg();
			if (msg != null && msg.length() > 0)
				info.append(" (").append(msg).append(")");
		}	//	Invoice
		
		String msg = createPOSPayments();
		if (msg != null) {
			m_processMsg = msg;
			return DocAction.STATUS_Invalid;
		}

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			if (info.length() > 0)
				info.append(" - ");
			info.append(valid);
			m_processMsg = info.toString();
			return DocAction.STATUS_Invalid;
		}

		//landed cost
		if (!isSOTrx())
		{
			String error = landedCostAllocation();
			if (!Util.isEmpty(error))
			{
				m_processMsg = error;
				return DocAction.STATUS_Invalid;
			}
		}

		updateOverReceipt();
		
		setProcessed(true);	
		m_processMsg = info.toString();
		//
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	private void updateOverReceipt() {
		for(MOrderLine line : m_lines) {
			if (line.getM_Product_ID() <= 0) continue;
			if (line.getQtyDelivered().signum() > 0 && line.getQtyOrdered().compareTo(line.getQtyDelivered()) >= 0) {
				DB.executeUpdateEx("UPDATE M_InOutLine Set QtyOverReceipt=0 WHERE C_OrderLine_ID=? AND QtyOverReceipt>0", 
						new Object[] {line.getC_OrderLine_ID()}, get_TrxName());
			}
		}
	}
	
	/**
	 * 	Void Document.
	 * 	Set Qtys to 0 - Sales: reverse all documents
	 * 	@return true if success 
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;
		
		//Astina check Receipt exist 081222
		String sql = "SELECT SUM(QtyDelivered) FROM C_OrderLine "
				+ "WHERE C_Order_ID=? ";
		BigDecimal QtyDelivered = DB.getSQLValueBD(get_TrxName(), sql, get_ID());
		
		if(QtyDelivered.signum() != 0)
		{
			m_processMsg = "Cannot void Shipment/Received Order";
			return false;
		}

		if (getLink_Order_ID() > 0) {
			MFG_MOrder so = new MFG_MOrder(getCtx(), getLink_Order_ID(), get_TrxName());
			so.setLink_Order_ID(0);
			so.saveEx();
		}

		if (!createReversals())
			return false;

		MFG_MOrderLine[] lines = getLines(true, MFG_MOrderLine.COLUMNNAME_M_Product_ID);
		for (int i = 0; i < lines.length; i++)
		{
			MFG_MOrderLine line = lines[i];
			BigDecimal old = line.getQtyOrdered();
			
			//Astina 081222
			I_C_OrderLine_Mfg corder = POWrapper.create(line, I_C_OrderLine_Mfg.class);
			
			if(corder.getM_Requisitionline_ID() > 0)
			{
				MFG_MRequisitionLine.addQtyRemainsPO(getCtx(), corder.getM_Requisitionline_ID(), corder.getQtyRequisition(), get_TrxName());
				MFG_MRequisitionLine MreqLine = new MFG_MRequisitionLine (Env.getCtx(), corder.getM_Requisitionline_ID(), get_TrxName());
				MFG_MRequisition Mreq = new MFG_MRequisition (Env.getCtx(), MreqLine.getM_Requisition_ID(), get_TrxName());
				if(Mreq.getDocStatus().equals("CL"))
				{
					Mreq.setDocStatus("CO");
					Mreq.setDocAction("CL");
					Mreq.saveEx();
				}
			}
			
			if (old.signum() != 0)
			{
				line.addDescription(Msg.getMsg(getCtx(), "Voided") + " (" + old + ")");
				line.setQty(Env.ZERO);
				line.setLineNetAmt(Env.ZERO);
				line.saveEx(get_TrxName());
			}
			//AZ Goodwill	
			if (!isSOTrx())
			{
				deleteMatchPOCostDetail(line);
			}
			if (line.getLink_OrderLine_ID() > 0) {
				MFG_MOrderLine soline = new MFG_MOrderLine(getCtx(), line.getLink_OrderLine_ID(), get_TrxName());
				soline.setLink_OrderLine_ID(0);
				soline.saveEx();
			}
		}
		
		// update taxes
		MOrderTax[] taxes = getTaxes(true);
		for (MOrderTax tax : taxes )
		{
			if ( !(tax.calculateTaxFromLines() && tax.save()) )
				return false;
		}
		
		addDescription(Msg.getMsg(getCtx(), "Voided"));
		//	Clear Reservations
		if (!reserveStock(null, lines))
		{
			m_processMsg = "Cannot unreserve Stock (void)";
			return false;
		}
		
		// UnLink All Requisitions
		MFG_MRequisitionLine.unlinkC_Order_ID(getCtx(), get_ID(), get_TrxName());
		
		/* globalqss - 2317928 - Reactivating/Voiding order must reset posted */
		MFactAcct.deleteEx(MFG_MOrder.Table_ID, getC_Order_ID(), get_TrxName());
		setPosted(false);
		
		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;
		
		setTotalLines(Env.ZERO);
		setGrandTotal(Env.ZERO);
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}	//	voidIt
	
	/**
	 * 	Reject Approval
	 * 	@return true if success 
	 */
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString());
		setIsApproved(false);
		
		//Astina 110123
		this.setProcessed(false);
				
		return true;
	}	//	rejectIt
	
	/**
	 * 	Before Delete
	 *	@return true of it can be deleted
	 */
	protected boolean beforeDelete ()
	{
		if (isProcessed())
			return false;
		// automatic deletion of lines is driven by model cascade definition in dictionary - see IDEMPIERE-2060
		return true;
	}	//	beforeDelete
	
	private volatile static boolean recursiveCall = false;
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		//	Client/Org Check
		if (getAD_Org_ID() == 0)
		{
			int context_AD_Org_ID = Env.getAD_Org_ID(getCtx());
			if (context_AD_Org_ID != 0)
			{
				setAD_Org_ID(context_AD_Org_ID);
				log.warning("Changed Org to Context=" + context_AD_Org_ID);
			}
		}
		if (getAD_Client_ID() == 0)
		{
			m_processMsg = "AD_Client_ID = 0";
			return false;
		}
		
		//Astina 080123
		if(this.getDateOrdered().compareTo(this.getDatePromised())>0)
		{
			if(this.isSOTrx())
				log.saveError("Error", "Order Date cannot higher than Promised Date");
			else
				log.saveError("Error", "Order Date cannot higher than ETA Date");
			return false;
		}
		
		//	New Record Doc Type - make sure DocType set to 0
		if (newRecord && getC_DocType_ID() == 0)
			setC_DocType_ID (0);

		//	Default Warehouse
		if (getM_Warehouse_ID() == 0)
		{
			int ii = Env.getContextAsInt(getCtx(), Env.M_WAREHOUSE_ID);
			if (ii != 0)
				setM_Warehouse_ID(ii);
			else
			{
				throw new FillMandatoryException(COLUMNNAME_M_Warehouse_ID);
			}
		}
		MWarehouse wh = MWarehouse.get(getCtx(), getM_Warehouse_ID());
		//	Warehouse Org
		if (newRecord 
			|| is_ValueChanged("AD_Org_ID") || is_ValueChanged("M_Warehouse_ID"))
		{
			if (wh.getAD_Org_ID() != getAD_Org_ID())
				log.saveWarning("WarehouseOrgConflict", "");
		}

		boolean disallowNegInv = wh.isDisallowNegativeInv();
		String DeliveryRule = getDeliveryRule();
		if((disallowNegInv && DELIVERYRULE_Force.equals(DeliveryRule)) ||
				(DeliveryRule == null || DeliveryRule.length()==0))
			setDeliveryRule(DELIVERYRULE_Availability);
		
		//	Reservations in Warehouse
		if (!newRecord && is_ValueChanged("M_Warehouse_ID"))
		{
			MOrderLine[] lines = getLines(false,null);
			for (int i = 0; i < lines.length; i++)
			{
				if (!lines[i].canChangeWarehouse())
					return false;
			}
		}
		
		//	No Partner Info - set Template
		if (getC_BPartner_ID() == 0)
			setBPartner(MBPartner.getTemplate(getCtx(), getAD_Client_ID()));
		if (getC_BPartner_Location_ID() == 0)
			setBPartner(new MBPartner(getCtx(), getC_BPartner_ID(), null));
		//	No Bill - get from Ship
		if (getBill_BPartner_ID() == 0)
		{
			setBill_BPartner_ID(getC_BPartner_ID());
			setBill_Location_ID(getC_BPartner_Location_ID());
		}
		if (getBill_Location_ID() == 0)
			setBill_Location_ID(getC_BPartner_Location_ID());

		//	Default Price List
		if (getM_PriceList_ID() == 0)
		{
			int ii = DB.getSQLValueEx(null,
				"SELECT M_PriceList_ID FROM M_PriceList "
				+ "WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive=?"
				+ "ORDER BY IsDefault DESC", getAD_Client_ID(), isSOTrx(), true);
			if (ii != 0)
				setM_PriceList_ID (ii);
		}
		//	Default Currency
		if (getC_Currency_ID() == 0)
		{
			String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
			int ii = DB.getSQLValue (null, sql, getM_PriceList_ID());
			if (ii != 0)
				setC_Currency_ID (ii);
			else
				setC_Currency_ID(Env.getContextAsInt(getCtx(), Env.C_CURRENCY_ID));
		}

		//	Default Sales Rep
		if (getSalesRep_ID() == 0)
		{
			int ii = Env.getContextAsInt(getCtx(), Env.SALESREP_ID);
			if (ii != 0)
				setSalesRep_ID (ii);
		}

		//	Default Document Type
		if (getC_DocTypeTarget_ID() == 0)
			setC_DocTypeTarget_ID(DocSubTypeSO_Standard);

		//	Default Payment Term
		if (getC_PaymentTerm_ID() == 0)
		{
			int ii = Env.getContextAsInt(getCtx(), Env.C_PAYMENTTERM_ID);
			if (ii != 0)
				setC_PaymentTerm_ID(ii);
			else
			{
				String sql = "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y' AND IsActive='Y'";
				ii = DB.getSQLValue(null, sql, getAD_Client_ID());
				if (ii != 0)
					setC_PaymentTerm_ID (ii);
			}
		}

		// IDEMPIERE-63
		// for documents that can be reactivated we cannot allow changing 
		// C_DocTypeTarget_ID or C_DocType_ID if they were already processed and isOverwriteSeqOnComplete
		// neither change the Date if isOverwriteDateOnComplete
		BigDecimal previousProcessedOn = (BigDecimal) get_ValueOld(COLUMNNAME_ProcessedOn);
		if (! newRecord && previousProcessedOn != null && previousProcessedOn.signum() > 0) {
			int previousDocTypeID = (Integer) get_ValueOld(COLUMNNAME_C_DocTypeTarget_ID);
			MDocType previousdt = MDocType.get(getCtx(), previousDocTypeID);
			if (is_ValueChanged(COLUMNNAME_C_DocType_ID) || is_ValueChanged(COLUMNNAME_C_DocTypeTarget_ID)) {
				if (previousdt.isOverwriteSeqOnComplete()) {
					log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangeProcessedDocType"));
					return false; 
				}
			}
			if (is_ValueChanged(COLUMNNAME_DateOrdered)) {
				if (previousdt.isOverwriteDateOnComplete()) {
					log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangeProcessedDate"));
					return false; 
				}
			}
		}

		// IDEMPIERE-1597 Price List and Date must be not-updateable
		if (!newRecord && (is_ValueChanged(COLUMNNAME_M_PriceList_ID) || is_ValueChanged(COLUMNNAME_DateOrdered))) {
			int cnt = DB.getSQLValueEx(get_TrxName(), "SELECT COUNT(*) FROM C_OrderLine WHERE C_Order_ID=? AND M_Product_ID>0", getC_Order_ID());
			if (cnt > 0) {
				if (is_ValueChanged(COLUMNNAME_M_PriceList_ID)) {
					log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangePl"));
					return false;
				}
				if (is_ValueChanged(COLUMNNAME_DateOrdered)) {
					MPriceList pList =  MPriceList.get(getCtx(), getM_PriceList_ID(), null);
					MPriceListVersion plOld = pList.getPriceListVersion((Timestamp)get_ValueOld(COLUMNNAME_DateOrdered));
					MPriceListVersion plNew = pList.getPriceListVersion((Timestamp)get_Value(COLUMNNAME_DateOrdered));
					if (plNew == null || !plNew.equals(plOld)) {
						log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangeDateOrdered"));
						return false;
					}
				}
			}
		}

		// IDEMPIERE-4318 Validation - Prepay Order must not allow Cash payment rule
		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		if (   MDocType.DOCSUBTYPESO_PrepayOrder.equals(dt.getDocSubTypeSO())
			&& PAYMENTRULE_Cash.equals(getPaymentRule())) {
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@Invalid@ @PaymentRule@"));
			return false;
		}

		if (! recursiveCall && (!newRecord && is_ValueChanged(COLUMNNAME_C_PaymentTerm_ID))) {
			recursiveCall = true;
			try {
				MPaymentTerm pt = new MPaymentTerm (getCtx(), getC_PaymentTerm_ID(), get_TrxName());
				boolean valid = pt.applyOrder(this);
				setIsPayScheduleValid(valid);
			} catch (Exception e) {
				throw e;
			} finally {
				recursiveCall = false;
			}
		}

		return true;
	}	//	beforeSave
	
	
	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		//	: Grand Total = 123.00 (#1)
		sb.append(": ").
			append(Msg.translate(getCtx(),"GrandTotal")).append("=").append(getGrandTotal());
		if (m_lines != null)
			sb.append(" (#").append(m_lines.length).append(")");
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}	//	getSummary
	
	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
	/**
	 * Set process message
	 * @param processMsg
	 */
	public void setProcessMessage(String processMsg)
	{
		m_processMsg = processMsg;
	}
	
	public void setM_Requisitionline_ID (int M_RequisitionLine_ID)
	{
		if (M_RequisitionLine_ID < 1) 
			set_ValueNoCheck (I_C_OrderLine_Mfg.COLUMNNAME_M_Requisitionline_ID, null);
		else 
			set_ValueNoCheck (I_C_OrderLine_Mfg.COLUMNNAME_M_Requisitionline_ID, Integer.valueOf(M_RequisitionLine_ID));
	}
	
	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx, int AD_Table_ID,
			String[] docAction, String[] options, int index) {
		if (options == null)
			throw new IllegalArgumentException("Option array parameter is null");
		if (docAction == null)
			throw new IllegalArgumentException("Doc action array parameter is null");

		// If a document is drafted or invalid, the users are able to complete, prepare or void
		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_Invalid) || docStatus.equals(DocumentEngine.STATUS_Approved)) {
			options[index++] = DocumentEngine.ACTION_Complete;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Void;

			// If the document is already completed, we also want to be able to reactivate or void it instead of only closing it
		} else if (docStatus.equals(DocumentEngine.STATUS_Completed) || docStatus.equals(DocumentEngine.STATUS_Approved)) {
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_ReActivate;
		}

		return index;
	}
	
}	//	MOrder
