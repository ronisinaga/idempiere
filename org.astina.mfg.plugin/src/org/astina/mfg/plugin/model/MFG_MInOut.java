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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.adempiere.exceptions.NegativeInventoryDisallowedException;
import org.adempiere.exceptions.PeriodClosedException;
import org.adempiere.util.IReservationTracer;
import org.adempiere.util.IReservationTracerFactory;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_M_InOutConfirm;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.MAsset;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MDocTypeCounter;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineMA;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMatchInv;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MRefList;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MStorageReservation;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTransaction;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.IDocsPostProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.TrxEventListener;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;

/**
 *  Shipment Model
 *
 *  @author Jorg Janke
 *  @version $Id: MInOut.java,v 1.4 2006/07/30 00:51:03 jjanke Exp $
 *
 *  Modifications: Added the RMA functionality (Ashley Ramdass)
 *  @author Karsten Thiemann, Schaeffer AG
 * 			<li>Bug [ 1759431 ] Problems with VCreateFrom
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li>FR [ 1948157  ]  Is necessary the reference for document reverse
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  @author Armen Rizal, Goodwill Consulting
 * 			<li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 *  @see https://sourceforge.net/p/adempiere/feature-requests/412/
 *  @author Teo Sarca, teo.sarca@gmail.com
 * 			<li>BF [ 2993853 ] Voiding/Reversing Receipt should void confirmations
 * 				https://sourceforge.net/p/adempiere/bugs/2395/
 */
public class MFG_MInOut extends MInOut implements DocAction, IDocsPostProcess
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8699990804131725782L;
	
	/**
	 * 	Create new Shipment by copying
	 * 	@param from shipment
	 * 	@param dateDoc date of the document date
	 * 	@param C_DocType_ID doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set the order link
	 *	@return Shipment
	 */
	public static MFG_MInOut copyFrom (MInOut from, Timestamp dateDoc, Timestamp dateAcct,
		int C_DocType_ID, boolean isSOTrx, boolean counter, String trxName, boolean setOrder)
	{
		MFG_MInOut to = new MFG_MInOut (from.getCtx(), 0, null);
		to.set_TrxName(trxName);
		copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.set_ValueNoCheck ("M_InOut_ID", I_ZERO);
		to.set_ValueNoCheck ("DocumentNo", null);
		//
		to.setDocStatus (DOCSTATUS_Drafted);		//	Draft
		to.setDocAction(DOCACTION_Complete);
		//
		to.setC_DocType_ID (C_DocType_ID);
		to.setIsSOTrx(isSOTrx);
		if (counter)
		{
			to.setMovementType();
		}

		//
		to.setDateOrdered (dateDoc);
		to.setDateAcct (dateAcct);
		to.setMovementDate(dateDoc);
		to.setDatePrinted(null);
		to.setIsPrinted (false);
		to.setDateReceived(null);
		to.setNoPackages(0);
		to.setShipDate(null);
		to.setPickDate(null);
		to.setIsInTransit(false);
		//
		to.setIsApproved (false);
		to.setC_Invoice_ID(0);
		to.setTrackingNo(null);
		to.setIsInDispute(false);
		//
		to.setPosted (false);
		to.setProcessed (false);
		//[ 1633721 ] Reverse Documents- Processing=Y
		to.setProcessing(false);
		to.setC_Order_ID(0);	//	Overwritten by setOrder
		to.setM_RMA_ID(0);      //  Overwritten by setOrder
		if (counter)
		{
			to.setC_Order_ID(0);
			to.setRef_InOut_ID(from.getM_InOut_ID());
			//	Try to find Order/Invoice link
			if (from.getC_Order_ID() != 0)
			{
				MOrder peer = new MOrder (from.getCtx(), from.getC_Order_ID(), from.get_TrxName());
				if (peer.getRef_Order_ID() != 0)
					to.setC_Order_ID(peer.getRef_Order_ID());
			}
			if (from.getC_Invoice_ID() != 0)
			{
				MInvoice peer = new MInvoice (from.getCtx(), from.getC_Invoice_ID(), from.get_TrxName());
				if (peer.getRef_Invoice_ID() != 0)
					to.setC_Invoice_ID(peer.getRef_Invoice_ID());
			}
			//find RMA link
			if (from.getM_RMA_ID() != 0)
			{
				MRMA peer = new MRMA (from.getCtx(), from.getM_RMA_ID(), from.get_TrxName());
				if (peer.getRef_RMA_ID() > 0)
					to.setM_RMA_ID(peer.getRef_RMA_ID());
			}
		}
		else
		{
			to.setRef_InOut_ID(0);
			if (setOrder)
			{
				to.setC_Order_ID(from.getC_Order_ID());
				to.setM_RMA_ID(from.getM_RMA_ID()); // Copy also RMA
			}
		}
		//
		if (!to.save(trxName))
			throw new IllegalStateException("Could not create Shipment");
		if (counter)
			from.setRef_InOut_ID(to.getM_InOut_ID());

		if (to.copyLinesFrom(from, counter, setOrder) <= 0)
			throw new IllegalStateException("Could not create Shipment Lines");

		return to;
	}	//	copyFrom
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_InOut_ID
	 *	@param trxName rx name
	 */
	public MFG_MInOut (Properties ctx, int M_InOut_ID, String trxName)
	{
		this (ctx, M_InOut_ID, trxName, (String[]) null);
	}	//	MInOut

	public MFG_MInOut(Properties ctx, int M_InOut_ID, String trxName, String... virtualColumns) {
		super(ctx, M_InOut_ID, trxName, virtualColumns);
		if (M_InOut_ID == 0)
		{
			setIsSOTrx (false);
			setMovementDate (new Timestamp (System.currentTimeMillis ()));
			setDateAcct (getMovementDate());
			setDeliveryRule (DELIVERYRULE_Availability);
			setDeliveryViaRule (DELIVERYVIARULE_Pickup);
			setFreightCostRule (FREIGHTCOSTRULE_FreightIncluded);
			setDocStatus (DOCSTATUS_Drafted);
			setDocAction (DOCACTION_Complete);
			setPriorityRule (PRIORITYRULE_Medium);
			setNoPackages(0);
			setIsInTransit(false);
			setIsPrinted (false);
			setSendEMail (false);
			setIsInDispute(false);
			//
			setIsApproved(false);
			super.setProcessed (false);
			setProcessing(false);
			setPosted(false);
		}
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *	@param trxName transaction
	 */
	public MFG_MInOut (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInOut

	/**	Lines					*/
	protected MInOutLine[]	m_lines = null;
	/** Confirmations			*/
	protected MInOutConfirm[]	m_confirms = null;
	/** BPartner				*/
	protected MBPartner		m_partner = null;


	/**
	 * 	Get Document Status
	 *	@return Document Status Clear Text
	 */
	public String getDocStatusName()
	{
		return MRefList.getListName(getCtx(), 131, getDocStatus());
	}	//	getDocStatusName

	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else{
			StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
			setDescription(msgd.toString());
		}	
	}	//	addDescription

	/**
	 *	String representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MInOut[")
			.append (get_ID()).append("-").append(getDocumentNo())
			.append(",DocStatus=").append(getDocStatus())
			.append ("]");
		return sb.toString ();
	}	//	toString

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		StringBuilder msgreturn = new StringBuilder().append(dt.getNameTrl()).append(" ").append(getDocumentNo());
		return msgreturn.toString();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		try
		{
			StringBuilder msgfile = new StringBuilder().append(get_TableName()).append(get_ID()).append("_");
			File temp = File.createTempFile(msgfile.toString(), ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}	//	getPDF

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.SHIPMENT, getM_InOut_ID(), get_TrxName());
		if (re == null)
			return null;
		MPrintFormat format = re.getPrintFormat();
		// We have a Jasper Print Format
		// ==============================
		if(format.getJasperProcess_ID() > 0)	
		{
			ProcessInfo pi = new ProcessInfo ("", format.getJasperProcess_ID());
			pi.setRecord_ID ( getM_InOut_ID() );
			pi.setIsBatch(true);
			
			ServerProcessCtl.process(pi, null);
			
			return pi.getPDFReport();
		}
		// Standard Print Format (Non-Jasper)
		// ==================================
		return re.getPDF(file);
	}	//	createPDF

	/**
	 * 	Get Lines of Shipment
	 * 	@param requery refresh from db
	 * 	@return lines
	 */
	public MInOutLine[] getLines (boolean requery)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		List<MInOutLine> list = new Query(getCtx(), I_M_InOutLine.Table_Name, "M_InOut_ID=?", get_TrxName())
		.setParameters(getM_InOut_ID())
		.setOrderBy(MInOutLine.COLUMNNAME_Line+","+MInOutLine.COLUMNNAME_M_InOutLine_ID)
		.list();
		//
		m_lines = new MInOutLine[list.size()];
		list.toArray(m_lines);
		return m_lines;
	}	//	getMInOutLines

	/**
	 * 	Get Lines of Shipment
	 * 	@return lines
	 */
	public MInOutLine[] getLines()
	{
		return getLines(false);
	}	//	getLines


	/**
	 * 	Get Confirmations
	 * 	@param requery requery
	 *	@return array of Confirmations
	 */
	public MInOutConfirm[] getConfirmations(boolean requery)
	{
		if (m_confirms != null && !requery)
		{
			set_TrxName(m_confirms, get_TrxName());
			return m_confirms;
		}
		List<MInOutConfirm> list = new Query(getCtx(), I_M_InOutConfirm.Table_Name, "M_InOut_ID=?", get_TrxName())
		.setParameters(getM_InOut_ID())
		.list();
		m_confirms = new MInOutConfirm[list.size ()];
		list.toArray (m_confirms);
		return m_confirms;
	}	//	getConfirmations

	/** Reversal Flag		*/
	protected boolean m_reversal = false;

	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	protected void setReversal(boolean reversal)
	{
		m_reversal = reversal;
	}	//	setReversal
	/**
	 * 	Is Reversal
	 *	@return reversal
	 */
	public boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal

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
	}	//	process

	/**	Process Message 			*/
	protected String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	protected boolean		m_justPrepared = false;

	/**
	 * 	Unlock Document.
	 * 	@return true if success
	 */
	public boolean unlockIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setProcessing(false);
		return true;
	}	//	unlockIt

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	public boolean invalidateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}	//	invalidateIt

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid)
	 */
	public String prepareIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());

		//  Order OR RMA can be processed on a shipment/receipt
		if (getC_Order_ID() != 0 && getM_RMA_ID() != 0)
		{
		    m_processMsg = "@OrderOrRMA@";
		    return DocAction.STATUS_Invalid;
		}
		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}

		// Validate Close Order
		if (!isReversal())
		{
			StringBuilder sql = new StringBuilder("SELECT DISTINCT o.DocumentNo FROM M_InOut io ")
					.append("JOIN M_InOutLine iol ON (io.M_InOut_ID=iol.M_InOut_ID) ")
					.append("JOIN C_OrderLine ol ON (iol.C_OrderLine_ID=ol.C_OrderLine_ID) ")
					.append("JOIN C_Order o ON (ol.C_Order_ID=o.C_Order_ID) ")
					.append("WHERE o.DocStatus='CL' AND (ol.M_Product_ID > 0 OR ol.C_Charge_ID > 0) AND iol.MovementQty != 0 ")
					.append("AND ol.IsActive='Y' AND iol.IsActive='Y' ")
					.append("AND io.M_InOut_ID=? ");
			List<List<Object>> closeOrders = DB.getSQLArrayObjectsEx(get_TrxName(), sql.toString(), getM_InOut_ID());
			if (closeOrders != null && closeOrders.size() > 0) 
			{
				m_processMsg = Msg.getMsg(p_ctx,"OrderClosed")+" (";
				for(int i = 0; i< closeOrders.size(); i++)
				{
					if (i > 0)
						m_processMsg += ", ";
					m_processMsg += closeOrders.get(i).get(0).toString();
				}
				m_processMsg += ")";
				return DocAction.STATUS_Invalid;
			}
		}
				
		//	Credit Check
		if (isSOTrx() && !isReversal() && !isCustomerReturn())
		{
			I_C_Order order = getC_Order();
			if (order != null && MDocType.DOCSUBTYPESO_PrepayOrder.equals(order.getC_DocType().getDocSubTypeSO())
					&& !MSysConfig.getBooleanValue(MSysConfig.CHECK_CREDIT_ON_PREPAY_ORDER, true, getAD_Client_ID(), getAD_Org_ID())) {
				// ignore -- don't validate Prepay Orders depending on sysconfig parameter
			} else {
				MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), get_TrxName());
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
				if (!MBPartner.SOCREDITSTATUS_NoCreditCheck.equals(bp.getSOCreditStatus())
						&& Env.ZERO.compareTo(bp.getSO_CreditLimit()) != 0)
				{
					BigDecimal notInvoicedAmt = MBPartner.getNotInvoicedAmt(getC_BPartner_ID());
					if (MBPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus(notInvoicedAmt)))
					{
						m_processMsg = "@BPartnerOverSCreditHold@ - @TotalOpenBalance@="
							+ bp.getTotalOpenBalance() + ", @NotInvoicedAmt@=" + notInvoicedAmt
							+ ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();
						return DocAction.STATUS_Invalid;
					}
				}
			}
		}

		//	Lines
		MInOutLine[] lines = getLines(true);
		if (lines == null || lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}
		BigDecimal Volume = Env.ZERO;
		BigDecimal Weight = Env.ZERO;

		//	Mandatory Attributes
		for (int i = 0; i < lines.length; i++)
		{
			MInOutLine line = lines[i];
			MProduct product = line.getProduct();
			if (product != null)
			{
				Volume = Volume.add(product.getVolume().multiply(line.getMovementQty()));
				Weight = Weight.add(product.getWeight().multiply(line.getMovementQty()));
			}
			//
			if (line.getM_AttributeSetInstance_ID() != 0)
				continue;
			if (product != null && product.isASIMandatoryFor(MAttributeSet.MANDATORYTYPE_WhenShipping,isSOTrx()))
			{
				if (product.getAttributeSet() != null && !product.getAttributeSet().excludeTableEntry(MInOutLine.Table_ID, isSOTrx())) {
					BigDecimal qtyDiff = line.getMovementQty();
					// verify if the ASIs are captured on lineMA
					MInOutLineMA mas[] = MInOutLineMA.get(getCtx(),
							line.getM_InOutLine_ID(), get_TrxName());
					BigDecimal qtyma = Env.ZERO;
					for (MInOutLineMA ma : mas) {
						if (! ma.isAutoGenerated()) {
							qtyma = qtyma.add(ma.getMovementQty());
						}
					}
					if (qtyma.subtract(qtyDiff).signum() != 0) {
						m_processMsg = "@M_AttributeSet_ID@ @IsMandatory@ (@Line@ #" + lines[i].getLine() +
								", @M_Product_ID@=" + product.getValue() + ")";
						return DocAction.STATUS_Invalid;
					}
				}
			}
		}
		setVolume(Volume);
		setWeight(Weight);

		if (!isReversal())	//	don't change reversal
		{
			createConfirmation();
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt

	/**
	 * Check if Document is Customer Return.
	 * @return True if Document is Customer Return
	 */
	private boolean isCustomerReturn() {
		MDocType doctype = MDocType.get(getC_DocType_ID());
		if(isSOTrx() && doctype.getDocBaseType().equals("MMR") && doctype.isSOTrx())
			return true;
		return false;
	}

	/**
	 * 	Approve Document
	 * 	@return true if success
	 */
	public boolean  approveIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(true);
		return true;
	}	//	approveIt

	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt

	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		// Set the definite document number after completed (if needed)
		setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		if (pendingCustomerConfirmations()) {
			m_processMsg = "@Open@: @M_InOutConfirm_ID@";
			return DocAction.STATUS_InProgress;
		}

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		StringBuilder info = new StringBuilder();

		StringBuilder errors = new StringBuilder();
		//	For all lines
		MInOutLine[] lines = getLines(false);
		for (int lineIndex = 0; lineIndex < lines.length; lineIndex++)
		{
			MInOutLine sLine = lines[lineIndex];
			MProduct product = sLine.getProduct();
			
			if(getC_Order_ID()>0)
			{
				MClient mc = new MClient(getCtx(), this.getAD_Client_ID(), get_TrxName());
				if(mc.getAcctSchema().getC_Currency_ID() != getC_Order().getC_Currency_ID())
				{
					BigDecimal cc = MConversionRate.convert (getCtx(),
							sLine.getC_OrderLine().getPriceEntered(), getC_Order().getC_Currency_ID(), mc.getAcctSchema().getC_Currency_ID(),
							getDateAcct(), getC_Order().getC_ConversionType_ID(), mc.getAD_Client_ID(), getAD_Org_ID());
					
					if(cc == null)
					{
						m_processMsg = "No Conversion Currency Rate available " + getC_Order().getDocumentNo();
						return DOCSTATUS_Invalid;
					}
				}
			}
			
			try
			{
				//	Qty & Type
				String MovementType = getMovementType();
				BigDecimal Qty = sLine.getMovementQty();
				if (MovementType.charAt(1) == '-')	//	C- Customer Shipment - V- Vendor Return
					Qty = Qty.negate();
	
				//	Update Order Line
				MFG_MOrderLine oLine = null;
				if (sLine.getC_OrderLine_ID() != 0)
				{
					oLine = new MFG_MOrderLine (getCtx(), sLine.getC_OrderLine_ID(), get_TrxName());
					if (log.isLoggable(Level.FINE)) log.fine("OrderLine - Reserved=" + oLine.getQtyReserved()
						+ ", Delivered=" + oLine.getQtyDelivered());
				}
	
	
	            // Load RMA Line
	            MRMALine rmaLine = null;
	
	            if (sLine.getM_RMALine_ID() != 0)
	            {
	                rmaLine = new MRMALine(getCtx(), sLine.getM_RMALine_ID(), get_TrxName());
	            }
	
				if (log.isLoggable(Level.INFO)) log.info("Line=" + sLine.getLine() + " - Qty=" + sLine.getMovementQty());
	
				//	Stock Movement - Counterpart MOrder.reserveStock
				if (product != null
					&& product.isStocked() )
				{
					//Ignore the Material Policy when is Reverse Correction
					if(!isReversal())
					{
						BigDecimal movementQty = sLine.getMovementQty();
						BigDecimal qtyOnLineMA = MInOutLineMA.getManualQty(sLine.getM_InOutLine_ID(), get_TrxName());
	
						if (   (movementQty.signum() != 0 && qtyOnLineMA.signum() != 0 && movementQty.signum() != qtyOnLineMA.signum()) // must have same sign
							|| (qtyOnLineMA.abs().compareTo(movementQty.abs())>0)) { // compare absolute values
							// More then line qty on attribute tab for line 10
							m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + sLine.getLine();
							return DOCSTATUS_Invalid;
						}
						
						checkMaterialPolicy(sLine,movementQty.subtract(qtyOnLineMA));
					}
	
					log.fine("Material Transaction");
					MTransaction mtrx = null;
					
					if (!isReversal()) 
					{
						if (oLine != null) 
						{
							BigDecimal toDelivered = oLine.getQtyOrdered()
									.subtract(oLine.getQtyDelivered());
							if (toDelivered.signum() < 0) // IDEMPIERE-2889
								toDelivered = Env.ZERO;
						}
					} 
					
					BigDecimal storageReservationToUpdate = sLine.getMovementQty();
					if (oLine != null)
					{
						if (!isReversal()) 
						{
							if (storageReservationToUpdate.compareTo(oLine.getQtyReserved()) > 0) 
								storageReservationToUpdate = oLine.getQtyReserved();
						}
						else
						{
							BigDecimal tmp = storageReservationToUpdate.negate().add(oLine.getQtyReserved());
							if (tmp.compareTo(oLine.getQtyOrdered()) > 0)
								storageReservationToUpdate = oLine.getQtyOrdered().subtract(oLine.getQtyReserved());
						}
					}
					
					//
					if (sLine.getM_AttributeSetInstance_ID() == 0)
					{
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(),
							sLine.getM_InOutLine_ID(), get_TrxName());
						for (int j = 0; j < mas.length; j++)
						{
							MInOutLineMA ma = mas[j];
							BigDecimal QtyMA = ma.getMovementQty();
							if (MovementType.charAt(1) == '-')	//	C- Customer Shipment - V- Vendor Return
								QtyMA = QtyMA.negate();
	
							if (product != null && QtyMA.signum() < 0 && MovementType.equals(MOVEMENTTYPE_CustomerShipment) && ma.getM_AttributeSetInstance_ID() > 0
								&& oLine != null && oLine.getM_AttributeSetInstance_ID()==0 && !ma.isAutoGenerated() && !isReversal()) 
							{
								String status = moveOnHandToShipmentASI(product, sLine.getM_Locator_ID(), ma.getM_AttributeSetInstance_ID(), QtyMA.negate(), ma.getDateMaterialPolicy(), 
										sLine.get_ID(), false, get_TrxName());
								if (status != null)
									return status;
							}
							
							//	Update Storage - see also VMatch.createMatchRecord
							if (!MStorageOnHand.add(getCtx(),
								sLine.getM_Locator_ID(),
								sLine.getM_Product_ID(),
								ma.getM_AttributeSetInstance_ID(),
								QtyMA,ma.getDateMaterialPolicy(),
								get_TrxName()))
							{
								String lastError = CLogger.retrieveErrorString("");
								m_processMsg = "Cannot correct Inventory OnHand (MA) [" + product.getValue() + "] - " + lastError;
								return DocAction.STATUS_Invalid;
							}					
							
							//	Create Transaction
							mtrx = new MTransaction (getCtx(), sLine.getAD_Org_ID(),
								MovementType, sLine.getM_Locator_ID(),
								sLine.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
								QtyMA, getMovementDate(), get_TrxName());
							mtrx.setM_InOutLine_ID(sLine.getM_InOutLine_ID());
							if (!mtrx.save())
							{
								m_processMsg = "Could not create Material Transaction (MA) [" + product.getValue() + "]";
								return DocAction.STATUS_Invalid;
							}
							
							if (product != null && QtyMA.signum() > 0 && MovementType.equals(MOVEMENTTYPE_CustomerShipment) && ma.getM_AttributeSetInstance_ID() > 0
									&& oLine != null && oLine.getM_AttributeSetInstance_ID()==0 && !ma.isAutoGenerated() && isReversal()) 
							{
								String status = moveOnHandToShipmentASI(product, sLine.getM_Locator_ID(), ma.getM_AttributeSetInstance_ID(), QtyMA.negate(), ma.getDateMaterialPolicy(), 
										sLine.get_ID(), true, get_TrxName());
								if (status != null)
									return status;
							}
						}
						
						if (oLine!=null && mtrx!=null && 
						   ((!isReversal() && oLine.getQtyReserved().signum() > 0) || (isReversal() && oLine.getQtyOrdered().signum() > 0)))
						{					
							if (sLine.getC_OrderLine_ID() != 0 && oLine.getM_Product_ID() > 0)
							{
								IReservationTracer tracer = null;
								IReservationTracerFactory factory = Core.getReservationTracerFactory();
								if (factory != null) {
									tracer = factory.newTracer(getC_DocType_ID(), getDocumentNo(), sLine.getLine(), 
											sLine.get_Table_ID(), sLine.get_ID(), oLine.getM_Warehouse_ID(), 
											oLine.getM_Product_ID(), oLine.getM_AttributeSetInstance_ID(), isSOTrx(), 
											get_TrxName());
								}
								if (!MStorageReservation.add(getCtx(), oLine.getM_Warehouse_ID(),
										oLine.getM_Product_ID(),
										oLine.getM_AttributeSetInstance_ID(),
										storageReservationToUpdate.negate(),
										isSOTrx(),
										get_TrxName(), tracer))
								{
									String lastError = CLogger.retrieveErrorString("");
									m_processMsg = "Cannot correct Inventory " + (isSOTrx()? "Reserved" : "Ordered") + " (MA) - [" + product.getValue() + "] - " + lastError;
									return DocAction.STATUS_Invalid;
								}
							}
						}
						
					}

					if (mtrx == null)
					{
						if (product != null  && MovementType.equals(MOVEMENTTYPE_CustomerShipment) && sLine.getM_AttributeSetInstance_ID() > 0 && Qty.signum() < 0
							&& oLine != null && oLine.getM_AttributeSetInstance_ID()==0 && !isReversal()) 
						{
							String status = moveOnHandToShipmentASI(product, sLine.getM_Locator_ID(), sLine.getM_AttributeSetInstance_ID(), Qty.negate(), null, sLine.get_ID(), false, get_TrxName());
							if (status != null)
								return status;
						}
						
						Timestamp dateMPolicy= null;
						BigDecimal pendingQty = Qty;
						if (pendingQty.signum() < 0) {  // taking from inventory
							MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), 0,
									sLine.getM_Product_ID(), sLine.getM_AttributeSetInstance_ID(), null,
									MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), false,
									sLine.getM_Locator_ID(), get_TrxName());
							for (MStorageOnHand storage : storages) {
								if (pendingQty.signum() == 0)
									break;
								if (storage.getQtyOnHand().compareTo(pendingQty.negate()) >= 0) {
									dateMPolicy = storage.getDateMaterialPolicy();
									break;
								} else if (storage.getQtyOnHand().signum() > 0) {
									BigDecimal onHand = storage.getQtyOnHand();
									// this locator has less qty than required, ship all qtyonhand and iterate to next locator
									if (!MStorageOnHand.add(getCtx(), 
											sLine.getM_Locator_ID(),
											sLine.getM_Product_ID(),
											sLine.getM_AttributeSetInstance_ID(),
											onHand.negate(),storage.getDateMaterialPolicy(),get_TrxName()))
									{
										String lastError = CLogger.retrieveErrorString("");
										m_processMsg = "Cannot correct Inventory OnHand [" + product.getValue() + "] - " + lastError;
										return DocAction.STATUS_Invalid;
									}
									pendingQty = pendingQty.add(onHand);
								}
							}

							if (dateMPolicy == null && storages.length > 0)
								dateMPolicy = storages[0].getDateMaterialPolicy();
						}
	
						if (dateMPolicy == null && product.getM_AttributeSet_ID() > 0) {
							MAttributeSet as = MAttributeSet.get(getCtx(), product.getM_AttributeSet_ID());
							if (as.isUseGuaranteeDateForMPolicy()) {
								MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), sLine.getM_AttributeSetInstance_ID(), get_TrxName());
								if (asi != null && asi.getGuaranteeDate() != null) {
									dateMPolicy = asi.getGuaranteeDate();
								}
							}
						}

						if (dateMPolicy == null)
							dateMPolicy = getMovementDate();

						//	Fallback: Update Storage - see also VMatch.createMatchRecord
						if (pendingQty.signum() != 0 &&
							!MStorageOnHand.add(getCtx(), 
							sLine.getM_Locator_ID(),
							sLine.getM_Product_ID(),
							sLine.getM_AttributeSetInstance_ID(),
							pendingQty,dateMPolicy,get_TrxName()))
						{
							String lastError = CLogger.retrieveErrorString("");
							m_processMsg = "Cannot correct Inventory OnHand [" + product.getValue() + "] - " + lastError;
							return DocAction.STATUS_Invalid;
						}
						if (oLine!=null && oLine.getM_Product_ID() > 0 &&
							((!isReversal() && oLine.getQtyReserved().signum() > 0) || (isReversal() && oLine.getQtyOrdered().signum() > 0)))  
						{
							IReservationTracer tracer = null;
							IReservationTracerFactory factory = Core.getReservationTracerFactory();
							if (factory != null) {
								tracer = factory.newTracer(getC_DocType_ID(), getDocumentNo(), sLine.getLine(), 
										sLine.get_Table_ID(), sLine.get_ID(), oLine.getM_Warehouse_ID(), 
										oLine.getM_Product_ID(), oLine.getM_AttributeSetInstance_ID(), isSOTrx(), 
										get_TrxName());
							}
							if (!MStorageReservation.add(getCtx(), oLine.getM_Warehouse_ID(),
									oLine.getM_Product_ID(),
									oLine.getM_AttributeSetInstance_ID(),
									storageReservationToUpdate.negate(), isSOTrx(), get_TrxName(), tracer))
							{
								m_processMsg = "Cannot correct Inventory Reserved " + (isSOTrx()? "Reserved [" :"Ordered [") + product.getValue() + "]";
								return DocAction.STATUS_Invalid;
							}
						}
						
						//	FallBack: Create Transaction
						mtrx = new MTransaction (getCtx(), sLine.getAD_Org_ID(),
							MovementType, sLine.getM_Locator_ID(),
							sLine.getM_Product_ID(), sLine.getM_AttributeSetInstance_ID(),
							Qty, getMovementDate(), get_TrxName());
						mtrx.setM_InOutLine_ID(sLine.getM_InOutLine_ID());
						if (!mtrx.save())
						{
							m_processMsg = CLogger.retrieveErrorString("Could not create Material Transaction [" + product.getValue() + "]");
							return DocAction.STATUS_Invalid;
						}
						
						if (product != null  && MovementType.equals(MOVEMENTTYPE_CustomerShipment) && sLine.getM_AttributeSetInstance_ID() > 0 && Qty.signum() > 0
							&& oLine != null && oLine.getM_AttributeSetInstance_ID()==0 && isReversal()) 
						{
							String status = moveOnHandToShipmentASI(product, sLine.getM_Locator_ID(), sLine.getM_AttributeSetInstance_ID(), Qty.negate(), getMovementDate(), sLine.get_ID(), true, get_TrxName());
							if (status != null)
								return status;
						}
					}
				}	//	stock movement
	
				//	Correct Order Line
				if (product != null && oLine != null)		//	other in VMatch.createMatchRecord
				{
					if (oLine.getQtyOrdered().signum() >= 0)
					{
						oLine.setQtyReserved(oLine.getQtyReserved().subtract(sLine.getMovementQty()));

						if (oLine.getQtyReserved().signum() == -1)
							oLine.setQtyReserved(Env.ZERO);
						else if (oLine.getQtyDelivered().compareTo(oLine.getQtyOrdered()) > 0)
							oLine.setQtyReserved(Env.ZERO);
					}
				}
	
				//	Update Sales Order Line
				if (oLine != null)
				{
					if (isSOTrx()							//	PO is done by Matching
						|| sLine.getM_Product_ID() == 0)	//	PO Charges, empty lines
					{
						if (isSOTrx())
							oLine.setQtyDelivered(oLine.getQtyDelivered().subtract(Qty));
						else
							oLine.setQtyDelivered(oLine.getQtyDelivered().add(Qty));
						oLine.setDateDelivered(getMovementDate());	//	overwrite=last
					}
					if (!oLine.save())
					{
						m_processMsg = "Could not update Order Line";
						return DocAction.STATUS_Invalid;
					}
					else
						if (log.isLoggable(Level.FINE)) log.fine("OrderLine -> Reserved=" + oLine.getQtyReserved()
							+ ", Delivered=" + oLine.getQtyReserved());
				}
	            //  Update RMA Line Qty Delivered
	            else if (rmaLine != null)
	            {
	                if (isSOTrx())
	                {
	                    rmaLine.setQtyDelivered(rmaLine.getQtyDelivered().add(Qty));
	                }
	                else
	                {
	                    rmaLine.setQtyDelivered(rmaLine.getQtyDelivered().subtract(Qty));
	                }
	                if (!rmaLine.save())
	                {
	                    m_processMsg = "Could not update RMA Line";
	                    return DocAction.STATUS_Invalid;
	                }
	            }
	
				//	Create Asset for SO
				if (product != null
					&& isSOTrx()
					&& product.isCreateAsset()
					&& !product.getM_Product_Category().getA_Asset_Group().isFixedAsset()
					&& sLine.getMovementQty().signum() > 0
					&& !isReversal())
				{
					log.fine("Asset");
					info.append("@A_Asset_ID@: ");
					int noAssets = sLine.getMovementQty().intValue();
					if (!product.isOneAssetPerUOM())
						noAssets = 1;
					for (int i = 0; i < noAssets; i++)
					{
						if (i > 0)
							info.append(" - ");
						int deliveryCount = i+1;
						if (!product.isOneAssetPerUOM())
							deliveryCount = 0;
						MAsset asset = new MAsset (this, sLine, deliveryCount);
						if (!asset.save(get_TrxName()))
						{
							m_processMsg = "Could not create Asset";
							return DocAction.STATUS_Invalid;
						}
						info.append(asset.getValue());
					}
				}	//	Asset
	
	
				//	Matching
				if (!isSOTrx()
					&& sLine.getM_Product_ID() != 0
					&& !isReversal())
				{
					BigDecimal matchQty = sLine.getMovementQty();
					//	Invoice - Receipt Match (requires Product)
					MInvoiceLine iLine = MInvoiceLine.getOfInOutLine (sLine);
					if (iLine != null && iLine.getM_Product_ID() != 0)
					{
						if (matchQty.compareTo(iLine.getQtyInvoiced())>0)
							matchQty = iLine.getQtyInvoiced();
	
						MMatchInv[] matches = MMatchInv.get(getCtx(),
							sLine.getM_InOutLine_ID(), iLine.getC_InvoiceLine_ID(), get_TrxName());
						if (matches == null || matches.length == 0)
						{
							MMatchInv inv = new MMatchInv (iLine, getMovementDate(), matchQty);
							if (sLine.getM_AttributeSetInstance_ID() != iLine.getM_AttributeSetInstance_ID())
							{
								iLine.setM_AttributeSetInstance_ID(sLine.getM_AttributeSetInstance_ID());
								iLine.saveEx();	//	update matched invoice with ASI
								inv.setM_AttributeSetInstance_ID(sLine.getM_AttributeSetInstance_ID());
							}
							if (!inv.save(get_TrxName()))
							{
								m_processMsg = CLogger.retrieveErrorString("Could not create Inv Matching");
								return DocAction.STATUS_Invalid;
							}
							addDocsPostProcess(inv);
						}
					}
	
					//	Link to Order
					if (sLine.getC_OrderLine_ID() != 0)
					{
						log.fine("PO Matching");
						//	Ship - PO
						MMatchPO po = MMatchPO.create (null, sLine, getMovementDate(), matchQty);
						if (po != null) {
							if (!po.save(get_TrxName()))
							{
								m_processMsg = "Could not create PO Matching";
								return DocAction.STATUS_Invalid;
							}
							if (!po.isPosted())
								addDocsPostProcess(po);
							
							MMatchInv[] matchInvList = MMatchInv.getInOut(getCtx(), getM_InOut_ID(), get_TrxName());
							for (MMatchInv matchInvCreated : matchInvList)
								addDocsPostProcess(matchInvCreated);
						}
						//	Update PO with ASI
						if (   oLine != null && oLine.getM_AttributeSetInstance_ID() == 0
							&& sLine.getMovementQty().compareTo(oLine.getQtyOrdered()) == 0) //  just if full match [ 1876965 ]
						{
							oLine.setM_AttributeSetInstance_ID(sLine.getM_AttributeSetInstance_ID());
							oLine.saveEx(get_TrxName());
						}
					}
					else	//	No Order - Try finding links via Invoice
					{
						//	Invoice has an Order Link
						if (iLine != null && iLine.getC_OrderLine_ID() != 0)
						{
							//	Invoice is created before  Shipment
							log.fine("PO(Inv) Matching");
							//	Ship - Invoice
							MMatchPO po = MMatchPO.create (iLine, sLine,
								getMovementDate(), matchQty);
							if (po != null) {
								if (!po.save(get_TrxName()))
								{
									m_processMsg = "Could not create PO(Inv) Matching";
									return DocAction.STATUS_Invalid;
								}
								if (!po.isPosted())
									addDocsPostProcess(po);
							}
							
							//	Update PO with ASI
							oLine = new MFG_MOrderLine (getCtx(), iLine.getC_OrderLine_ID(), get_TrxName());
							if (   oLine != null && oLine.getM_AttributeSetInstance_ID() == 0
								&& sLine.getMovementQty().compareTo(oLine.getQtyOrdered()) == 0) //  just if full match [ 1876965 ]
							{
								oLine.setM_AttributeSetInstance_ID(sLine.getM_AttributeSetInstance_ID());
								oLine.saveEx(get_TrxName());
							}
						}
					}	//	No Order
				}	//	PO Matching
			}
			catch (NegativeInventoryDisallowedException e)
			{
				log.severe(e.getMessage());
				errors.append(Msg.getElement(getCtx(), "Line")).append(" ").append(sLine.getLine()).append(": ");
				errors.append(e.getMessage()).append("\n");
			}
		}	//	for all lines

		if (errors.toString().length() > 0)
		{
			m_processMsg = errors.toString();
			return DocAction.STATUS_Invalid;
		}
		
		//	Counter Documents
		MFG_MInOut counter = createCounterDoc();
		if (counter != null)
			info.append(" - @CounterDoc@: @M_InOut_ID@=").append(counter.getDocumentNo());

		//  Drop Shipments
		MFG_MInOut dropShipment = createDropShipment();
		if (dropShipment != null)
		{
			info.append(" - @DropShipment@: @M_InOut_ID@=").append(dropShipment.getDocumentNo());
			ProcessInfo pi = MWFActivity.getCurrentWorkflowProcessInfo();
			if (pi != null)
			{
				Trx.get(get_TrxName(), false).addTrxEventListener(new TrxEventListener() {					
					@Override
					public void afterRollback(Trx trx, boolean success) {
						trx.removeTrxEventListener(this);
					}
					
					@Override
					public void afterCommit(Trx trx, boolean success) {
						if (success)
							pi.addLog(pi.getAD_PInstance_ID(), null, null, dropShipment.getDocumentInfo(), Table_ID, dropShipment.get_ID());
						trx.removeTrxEventListener(this);
					}
					
					@Override
					public void afterClose(Trx trx) {
					}
				});
			}
		}
		if (dropShipment != null)
			addDocsPostProcess(dropShipment);
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		m_processMsg = info.toString();
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt

	

	/* Save array of documents to process AFTER completing this one */
	ArrayList<PO> docsPostProcess = new ArrayList<PO>();

	protected void addDocsPostProcess(PO doc) {
		docsPostProcess.add(doc);
	}

	@Override
	public List<PO> getDocsPostProcess() {
		return docsPostProcess;
	}

	/**
	 * Automatically creates a customer shipment for any
	 * drop shipment material receipt
	 * Based on createCounterDoc() by JJ
	 * @return shipment if created else null
	 */
	protected MFG_MInOut createDropShipment() {

		if ( isSOTrx() || !isDropShip() || getC_Order_ID() == 0 )
			return null;

		int linkedOrderID = new MOrder (getCtx(), getC_Order_ID(), get_TrxName()).getLink_Order_ID();
		if (linkedOrderID <= 0)
			return null;

		//	Document Type
		int C_DocTypeTarget_ID = 0;
		MDocType[] shipmentTypes = MDocType.getOfDocBaseType(getCtx(), MDocType.DOCBASETYPE_MaterialDelivery);

		for (int i = 0; i < shipmentTypes.length; i++ )
		{
			if (shipmentTypes[i].isSOTrx() && ( C_DocTypeTarget_ID == 0 || shipmentTypes[i].isDefault() ) )
				C_DocTypeTarget_ID = shipmentTypes[i].getC_DocType_ID();
		}

		//	Deep Copy
		MFG_MInOut dropShipment = copyFrom(this, getMovementDate(), getDateAcct(),
			C_DocTypeTarget_ID, !isSOTrx(), false, get_TrxName(), true);

		dropShipment.setC_Order_ID(linkedOrderID);

		// get invoice id from linked order
		int invID = new MOrder (getCtx(), linkedOrderID, get_TrxName()).getC_Invoice_ID();
		if ( invID != 0 )
			dropShipment.setC_Invoice_ID(invID);

		dropShipment.setC_BPartner_ID(getDropShip_BPartner_ID());
		dropShipment.setC_BPartner_Location_ID(getDropShip_Location_ID());
		dropShipment.setAD_User_ID(getDropShip_User_ID());
		dropShipment.setIsDropShip(false);
		dropShipment.setDropShip_BPartner_ID(0);
		dropShipment.setDropShip_Location_ID(0);
		dropShipment.setDropShip_User_ID(0);
		dropShipment.setMovementType(MOVEMENTTYPE_CustomerShipment);
		if (!Util.isEmpty(getTrackingNo()) && getM_Shipper_ID() > 0 && 
				DELIVERYVIARULE_Shipper.equals(getDeliveryViaRule()))
		{
			dropShipment.setTrackingNo(getTrackingNo());
			dropShipment.setDeliveryViaRule(DELIVERYVIARULE_Shipper);
			dropShipment.setM_Shipper_ID(getM_Shipper_ID());
		}
		
		//	References (Should not be required
		dropShipment.setSalesRep_ID(getSalesRep_ID());
		dropShipment.saveEx(get_TrxName());

		//		Update line order references to linked sales order lines
		MInOutLine[] lines = dropShipment.getLines(true);
		for (int i = 0; i < lines.length; i++)
		{
			MInOutLine dropLine = lines[i];
			MOrderLine ol = new MOrderLine(getCtx(), dropLine.getC_OrderLine_ID(), null);
			if ( ol.getC_OrderLine_ID() != 0 ) {
				dropLine.setC_OrderLine_ID(ol.getLink_OrderLine_ID());
				dropLine.saveEx();
			}
		}

		if (log.isLoggable(Level.FINE)) log.fine(dropShipment.toString());

		dropShipment.setDocAction(DocAction.ACTION_Complete);
		// do not post immediate dropshipment, should post after source shipment
		dropShipment.set_Attribute(DocumentEngine.DOCUMENT_POST_IMMEDIATE_AFTER_COMPLETE, Boolean.FALSE);
		// added AdempiereException by Zuhri
		if (!dropShipment.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + dropShipment.getProcessMsg());
		// end added
		dropShipment.saveEx();

		return dropShipment;
	}

	/**
	 * 	Set the definite document number after completed
	 */
	protected void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setMovementDate(TimeUtil.getDay(0));
			if (getDateAcct().before(getMovementDate())) {
				setDateAcct(getMovementDate());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}

	/**
	 * 	Check Material Policy
	 * 	Sets line ASI
	 */
	protected void checkMaterialPolicy(MInOutLine line,BigDecimal qty)
	{
			
		int no = MInOutLineMA.deleteInOutLineMA(line.getM_InOutLine_ID(), get_TrxName());
		if (no > 0)
			if (log.isLoggable(Level.CONFIG)) log.config("Delete old #" + no);
		
		if(Env.ZERO.compareTo(qty)==0)
			return;
		
		//	Incoming Trx
		String MovementType = getMovementType();
		boolean inTrx = MovementType.charAt(1) == '+';	//	V+ Vendor Receipt

		boolean needSave = false;

		MProduct product = line.getProduct();

		//	Need to have Location
		if (product != null
				&& line.getM_Locator_ID() == 0)
		{
			line.setM_Warehouse_ID(getM_Warehouse_ID());
			line.setM_Locator_ID(inTrx ? Env.ZERO : line.getMovementQty());	//	default Locator
			needSave = true;
		}

		//	Attribute Set Instance
		//  Create an  Attribute Set Instance to any receipt FIFO/LIFO
		if (product != null && line.getM_AttributeSetInstance_ID() == 0)
		{
			//Validate Transaction
			if (getMovementType().compareTo(MFG_MInOut.MOVEMENTTYPE_VendorReceipts) == 0 )
			{
				//auto balance negative on hand
				BigDecimal qtyToReceive = autoBalanceNegative(line, product,qty);
				
				//Allocate remaining qty.
				if (qtyToReceive.compareTo(Env.ZERO)>0)
				{
					MInOutLineMA ma = MInOutLineMA.addOrCreate(line, 0, qtyToReceive, getMovementDate(),true); 
					ma.saveEx();
				}
				
			} else if (getMovementType().compareTo(MFG_MInOut.MOVEMENTTYPE_CustomerReturns) == 0){
				BigDecimal qtyToReturn = autoBalanceNegative(line, product,qty);
				
				if (line.getM_RMALine_ID()!=0 && qtyToReturn.compareTo(Env.ZERO)>0){
					//Linking to shipment line
					MRMALine rmaLine = new MRMALine(getCtx(), line.getM_RMALine_ID(), get_TrxName());
					if(rmaLine.getM_InOutLine_ID()>0){
						//retrieving ASI which is not already returned
						MInOutLineMA shipmentMAS[] = MInOutLineMA.getNonReturned(getCtx(), rmaLine.getM_InOutLine_ID(), get_TrxName());
						
						for(MInOutLineMA sMA : shipmentMAS){
							BigDecimal lineMAQty = sMA.getMovementQty();
							if(lineMAQty.compareTo(qtyToReturn)>0){
								lineMAQty = qtyToReturn;
							}
							
							MInOutLineMA ma = MInOutLineMA.addOrCreate(line, sMA.getM_AttributeSetInstance_ID(), lineMAQty, sMA.getDateMaterialPolicy(),true); 
							ma.saveEx();			
							
							qtyToReturn = qtyToReturn.subtract(lineMAQty);
							if(qtyToReturn.compareTo(Env.ZERO)==0)
								break;
						}
					}
				}
				if(qtyToReturn.compareTo(Env.ZERO)>0){
					//Use movement data for  Material policy if no linkage found to Shipment.
					MInOutLineMA ma = MInOutLineMA.addOrCreate(line, 0, qtyToReturn, getMovementDate(),true); 
					ma.saveEx();			
				}	
			}
			// Create consume the Attribute Set Instance using policy FIFO/LIFO
			else if(getMovementType().compareTo(MFG_MInOut.MOVEMENTTYPE_VendorReturns) == 0 || getMovementType().compareTo(MFG_MInOut.MOVEMENTTYPE_CustomerShipment) == 0)
			{
				String MMPolicy = product.getMMPolicy();
				Timestamp minGuaranteeDate = getMovementDate();
				MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						minGuaranteeDate, MClient.MMPOLICY_FiFo.equals(MMPolicy), true, line.getM_Locator_ID(), get_TrxName(), false);
				BigDecimal qtyToDeliver = qty;
				for (MStorageOnHand storage: storages)
				{
					if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0)
					{
						MInOutLineMA ma = new MInOutLineMA (line,
								storage.getM_AttributeSetInstance_ID(),
								qtyToDeliver,storage.getDateMaterialPolicy(),true);
						ma.saveEx();
						qtyToDeliver = Env.ZERO;
					}
					else
					{
						MInOutLineMA ma = new MInOutLineMA (line,
								storage.getM_AttributeSetInstance_ID(),
								storage.getQtyOnHand(),storage.getDateMaterialPolicy(),true);
						ma.saveEx();
						qtyToDeliver = qtyToDeliver.subtract(storage.getQtyOnHand());
						if (log.isLoggable(Level.FINE)) log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);
					}

					if (qtyToDeliver.signum() == 0)
						break;
				}

				if (qtyToDeliver.signum() != 0)
				{					
					//Over Delivery
					MInOutLineMA ma = MInOutLineMA.addOrCreate(line, line.getM_AttributeSetInstance_ID(), qtyToDeliver, getMovementDate(),true);
					ma.saveEx();
					if (log.isLoggable(Level.FINE)) log.fine("##: " + ma);
				}
			}	//	outgoing Trx
		}	//	attributeSetInstance

		if (needSave)
		{
			line.saveEx();
		}
	}	//	checkMaterialPolicy

	protected BigDecimal autoBalanceNegative(MInOutLine line, MProduct product,BigDecimal qtyToReceive) {
		MStorageOnHand[] storages = MStorageOnHand.getWarehouseNegative(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
				null, MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), line.getM_Locator_ID(), get_TrxName(), false);
		
		Timestamp dateMPolicy = null;
			
		for (MStorageOnHand storage : storages)
		{
			if (storage.getQtyOnHand().signum() < 0 && qtyToReceive.compareTo(Env.ZERO)>0)
			{
				dateMPolicy = storage.getDateMaterialPolicy();
				BigDecimal lineMAQty = qtyToReceive;
				if(lineMAQty.compareTo(storage.getQtyOnHand().negate())>0)
					lineMAQty = storage.getQtyOnHand().negate();
				
				//Using ASI from storage record
				MInOutLineMA ma = new MInOutLineMA (line, storage.getM_AttributeSetInstance_ID(), lineMAQty,dateMPolicy,true);
				ma.saveEx();			
				qtyToReceive = qtyToReceive.subtract(lineMAQty);
			}
		}
		return qtyToReceive;
	}


	/**************************************************************************
	 * 	Create Counter Document
	 * 	@return InOut
	 */
	protected MFG_MInOut createCounterDoc()
	{
		//	Is this a counter doc ?
		if (getRef_InOut_ID() != 0)
			return null;

		//	Org Must be linked to BPartner
		MOrg org = MOrg.get(getCtx(), getAD_Org_ID());
		int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(get_TrxName());
		if (counterC_BPartner_ID == 0)
			return null;
		//	Business Partner needs to be linked to Org
		MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), get_TrxName());
		int counterAD_Org_ID = bp.getAD_OrgBP_ID();
		if (counterAD_Org_ID == 0)
			return null;

		MBPartner counterBP = new MBPartner (getCtx(), counterC_BPartner_ID, null);
		MOrgInfo counterOrgInfo = MOrgInfo.get(getCtx(), counterAD_Org_ID, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

		//	Document Type
		int C_DocTypeTarget_ID = 0;
		MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getCtx(), getC_DocType_ID());
		if (counterDT != null)
		{
			if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString());
			if (!counterDT.isCreateCounter() || !counterDT.isValid())
				return null;
			C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
		}
		else	//	indirect
		{
			C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID(getCtx(), getC_DocType_ID());
			if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
			if (C_DocTypeTarget_ID <= 0)
				return null;
		}

		//	Deep Copy
		MFG_MInOut counter = copyFrom(this, getMovementDate(), getDateAcct(),
			C_DocTypeTarget_ID, !isSOTrx(), true, get_TrxName(), true);

		//
		counter.setAD_Org_ID(counterAD_Org_ID);
		counter.setM_Warehouse_ID(counterOrgInfo.getM_Warehouse_ID());
		//
		counter.setBPartner(counterBP);

		if ( isDropShip() )
		{
			counter.setIsDropShip(true );
			counter.setDropShip_BPartner_ID(getDropShip_BPartner_ID());
			counter.setDropShip_Location_ID(getDropShip_Location_ID());
			counter.setDropShip_User_ID(getDropShip_User_ID());
		}

		//	Refernces (Should not be required
		counter.setSalesRep_ID(getSalesRep_ID());
		counter.saveEx(get_TrxName());

		String MovementType = counter.getMovementType();
		boolean inTrx = MovementType.charAt(1) == '+';	//	V+ Vendor Receipt

		//	Update copied lines
		MInOutLine[] counterLines = counter.getLines(true);
		for (int i = 0; i < counterLines.length; i++)
		{
			MInOutLine counterLine = counterLines[i];
			
			//Astina 130223 
			//counterLine.setClientOrg(counter);
			counterLine.setAD_Org_ID(counter.getAD_Org_ID());
			
			counterLine.setM_Warehouse_ID(counter.getM_Warehouse_ID());
			counterLine.setM_Locator_ID(0);
			counterLine.setM_Locator_ID(inTrx ? Env.ZERO : counterLine.getMovementQty());
			//
			counterLine.saveEx(get_TrxName());
		}

		if (log.isLoggable(Level.FINE)) log.fine(counter.toString());

		//	Document Action
		if (counterDT != null)
		{
			if (counterDT.getDocAction() != null)
			{
				counter.setDocAction(counterDT.getDocAction());
				// added AdempiereException by zuhri
				if (!counter.processIt(counterDT.getDocAction()))
					throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + counter.getProcessMsg());
				// end added
				counter.saveEx(get_TrxName());
			}
		}
		return counter;
	}	//	createCounterDoc

	/**
	 * 	Void Document.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());		

		if (DOCSTATUS_Closed.equals(getDocStatus())
			|| DOCSTATUS_Reversed.equals(getDocStatus())
			|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			return false;
		}

		//	Not Processed
		if (DOCSTATUS_Drafted.equals(getDocStatus())
			|| DOCSTATUS_Invalid.equals(getDocStatus())
			|| DOCSTATUS_InProgress.equals(getDocStatus())
			|| DOCSTATUS_Approved.equals(getDocStatus())
			|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
		{
			// Before Void
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;
			
			//	Set lines to 0
			MInOutLine[] lines = getLines(false);
			for (int i = 0; i < lines.length; i++)
			{
				MInOutLine line = lines[i];
				BigDecimal old = line.getMovementQty();
				if (old.signum() != 0)
				{
					line.setQty(Env.ZERO);
					StringBuilder msgadd = new StringBuilder("Void (").append(old).append(")");
					line.addDescription(msgadd.toString());
					line.saveEx(get_TrxName());
				}
			}
			//
			// Void Confirmations
			setDocStatus(DOCSTATUS_Voided); // need to set & save docstatus to be able to check it in MInOutConfirm.voidIt()
			saveEx();
			voidConfirmations();
		}
		else
		{
			boolean accrual = false;
			try 
			{
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
			catch (PeriodClosedException e) 
			{
				accrual = true;
			}
			
			if (accrual)
				return reverseAccrualIt();
			else
				return reverseCorrectIt();
		}

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}	//	voidIt

	/**
	 * 	Close Document.
	 * 	@return true if success
	 */
	public boolean closeIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;
		return true;
	}	//	closeIt

	/**
	 * 	Reverse Correction - same date
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		MFG_MInOut reversal = reverse(false);
		if (reversal == null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		setProcessed(true);
		setDocStatus(DOCSTATUS_Reversed);		//	 may come from void
		setDocAction(DOCACTION_None);
		return true;
	}	//	reverseCorrectionIt

	protected MFG_MInOut reverse(boolean accrual) {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		Timestamp reversalDate = accrual ? Env.getContextAsDate(getCtx(), Env.DATE) : getDateAcct();
		if (reversalDate == null) {
			reversalDate = new Timestamp(System.currentTimeMillis());
		}
		Timestamp reversalMovementDate = accrual ? reversalDate : getMovementDate();
		if (!MPeriod.isOpen(getCtx(), reversalDate, dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return null;
		}

		//	Reverse/Delete Matching
		if (!isSOTrx())
		{
			if (!reverseMatching(reversalDate))
				return null;			
		}

		//	Deep Copy
		MFG_MInOut reversal = copyFrom (this, reversalMovementDate, reversalDate,
			getC_DocType_ID(), isSOTrx(), false, get_TrxName(), true);
		if (reversal == null)
		{
			m_processMsg = "Could not create Ship Reversal";
			return null;
		}
		reversal.setReversal(true);

		//	Reverse Line Qty
		MInOutLine[] sLines = getLines(false);
		MInOutLine[] rLines = reversal.getLines(false);
		for (int i = 0; i < rLines.length; i++)
		{
			MInOutLine rLine = rLines[i];
			rLine.setQtyEntered(rLine.getQtyEntered().negate());
			rLine.setMovementQty(rLine.getMovementQty().negate());
			rLine.setM_AttributeSetInstance_ID(sLines[i].getM_AttributeSetInstance_ID());
			// Goodwill: store original (voided/reversed) document line
			rLine.setReversalLine_ID(sLines[i].getM_InOutLine_ID());
			if (!rLine.save(get_TrxName()))
			{
				m_processMsg = "Could not correct Ship Reversal Line";
				return null;
			}
			//	We need to copy MA
			if (rLine.getM_AttributeSetInstance_ID() == 0)
			{
				MInOutLineMA mas[] = MInOutLineMA.get(getCtx(),
					sLines[i].getM_InOutLine_ID(), get_TrxName());
				for (int j = 0; j < mas.length; j++)
				{
					MInOutLineMA ma = new MInOutLineMA (rLine,
						mas[j].getM_AttributeSetInstance_ID(),
						mas[j].getMovementQty().negate(),mas[j].getDateMaterialPolicy(),mas[j].isAutoGenerated());
					ma.saveEx();
				}
			}
			//	De-Activate Asset
			MAsset asset = MAsset.getFromShipment(getCtx(), sLines[i].getM_InOutLine_ID(), get_TrxName());
			if (asset != null)
			{
				asset.setIsActive(false);
				asset.setDescription(asset.getDescription() + " (" + reversal.getDocumentNo() + " #" + rLine.getLine() + "<-)");
				asset.saveEx();
			}
			// Un-Link inoutline to Invoiceline
			String sql = "SELECT C_InvoiceLine_ID FROM C_InvoiceLine WHERE M_InOutLine_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, sLines[i].getM_InOutLine_ID());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					int invoiceLineId = rs.getInt(1);
					if (invoiceLineId > 0 ){
						MInvoiceLine iLine = new MInvoiceLine(getCtx(),invoiceLineId , get_TrxName());
						iLine.setM_InOutLine_ID(0);
						iLine.saveEx();
					}
				}
			}
			catch (SQLException e)
			{
				throw new DBException(e, sql);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
		}
		reversal.setC_Order_ID(getC_Order_ID());
		// Set M_RMA_ID
		reversal.setM_RMA_ID(getM_RMA_ID());
		StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
		reversal.addDescription(msgadd.toString());
		//FR1948157
		reversal.setReversal_ID(getM_InOut_ID());
		reversal.saveEx(get_TrxName());
		//
		reversal.docsPostProcess = this.docsPostProcess;
		this.docsPostProcess = new ArrayList<PO>();
		//
		if (!reversal.processIt(DocAction.ACTION_Complete)
			|| !reversal.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}
		reversal.closeIt();
		reversal.setProcessing (false);
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());
		//
		msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
		addDescription(msgadd.toString());
		
		//
		// Void Confirmations
		setDocStatus(DOCSTATUS_Reversed); // need to set & save docstatus to be able to check it in MInOutConfirm.voidIt()
		saveEx();
		//FR1948157
		this.setReversal_ID(reversal.getM_InOut_ID());
		voidConfirmations();
		return reversal;
	}

	protected boolean reverseMatching(Timestamp reversalDate) {
		MMatchInv[] mInv = MMatchInv.getInOut(getCtx(), getM_InOut_ID(), get_TrxName());
		for (MMatchInv mMatchInv : mInv)
		{		
			if (mMatchInv.getReversal_ID() > 0)
				continue;
			
			String description = mMatchInv.getDescription();
			if (description == null || !description.endsWith("<-)"))
			{
				if (!mMatchInv.reverse(reversalDate))
				{
					log.log(Level.SEVERE, "Failed to create reversal for match invoice " + mMatchInv.getDocumentNo());
					return false;
				}
				addDocsPostProcess(new MMatchInv(Env.getCtx(), mMatchInv.getReversal_ID(), get_TrxName()));
			}
		}
		MMatchPO[] mMatchPOList = MMatchPO.getInOut(getCtx(), getM_InOut_ID(), get_TrxName());
		for (MMatchPO mMatchPO : mMatchPOList) 
		{
			if (mMatchPO.getReversal_ID() > 0)
				continue;
			
			String description = mMatchPO.getDescription();
			if (description == null || !description.endsWith("<-)"))
			{
				if (!mMatchPO.reverse(reversalDate))
				{
					log.log(Level.SEVERE, "Failed to create reversal for match purchase order " + mMatchPO.getDocumentNo());
					return false;
				}
				addDocsPostProcess(new MMatchPO(Env.getCtx(), mMatchPO.getReversal_ID(), get_TrxName()));
			}
		}
		return true;
	}

	/**
	 * 	Reverse Accrual - none
	 * 	@return false
	 */
	public boolean reverseAccrualIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		MFG_MInOut reversal = reverse(true);
		if (reversal == null)
			return false;
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		setProcessed(true);
		setDocStatus(DOCSTATUS_Reversed);		//	 may come from void
		setDocAction(DOCACTION_None);
		return true;
	}	//	reverseAccrualIt

	/**
	 * 	Re-activate
	 * 	@return false
	 */
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		return false;
	}	//	reActivateIt


	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
		sb.append(":")
			.append(" (#").append(getLines(false).length).append(")");
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
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	public int getDoc_User_ID()
	{
		return getSalesRep_ID();
	}	//	getDoc_User_ID

	/**
	 * 	Get Document Approval Amount
	 *	@return amount
	 */
	public BigDecimal getApprovalAmt()
	{
		return Env.ZERO;
	}	//	getApprovalAmt

	/**
	 * 	Get C_Currency_ID
	 *	@return Accounting Currency
	 */
	public int getC_Currency_ID ()
	{
		return Env.getContextAsInt(getCtx(),Env.C_CURRENCY_ID);
	}	//	getC_Currency_ID

	/**
	 * 	Document Status is Complete or Closed
	 *	@return true if CO, CL or RE
	 */
	public boolean isComplete()
	{
		String ds = getDocStatus();
		return DOCSTATUS_Completed.equals(ds)
			|| DOCSTATUS_Closed.equals(ds)
			|| DOCSTATUS_Reversed.equals(ds);
	}	//	isComplete

	/**
	 * For product with mix of No ASI and ASI inventory, this move Non ASI on hand to the new ASI created at shipment line or shipment line ma
	 * @param product
	 * @param M_Locator_ID shipment line locator id
	 * @param M_AttributeSetInstance_ID
	 * @param qty
	 * @param dateMaterialPolicy
	 * @param M_InOutLine_ID
	 * @param reversal
	 * @param trxName
	 * @return error doc status if there are any errors
	 */
	protected String moveOnHandToShipmentASI(MProduct product, int M_Locator_ID, int M_AttributeSetInstance_ID, BigDecimal qty,
			Timestamp dateMaterialPolicy, int M_InOutLine_ID, boolean reversal, String trxName) {
		if (qty.signum() == 0 || (qty.signum() < 0 && !reversal) || (qty.signum() > 0 && reversal))
			return null;
		if (M_AttributeSetInstance_ID == 0)
			return null;
		if (dateMaterialPolicy != null) {
			MStorageOnHand asi = MStorageOnHand.get(getCtx(), M_Locator_ID, product.getM_Product_ID(), M_AttributeSetInstance_ID, dateMaterialPolicy, trxName);
			if (asi != null && asi.getQtyOnHand().signum() != 0 && !reversal)
				return null;
			
			if (reversal) {
				if (!MStorageOnHand.add(getCtx(), M_Locator_ID, product.getM_Product_ID(), 0, qty.negate(), dateMaterialPolicy, trxName)) {
					String lastError = CLogger.retrieveErrorString("");
					m_processMsg = "Cannot move Inventory OnHand to Non ASI [" + product.getValue() + "] - " + lastError;
					return DocAction.STATUS_Invalid;
				}
				MTransaction trxFrom = new MTransaction (Env.getCtx(), getAD_Org_ID(), getMovementType(), M_Locator_ID, product.getM_Product_ID(), 0,
						qty.negate(), getMovementDate(), trxName);
				trxFrom.setM_InOutLine_ID(M_InOutLine_ID);
				if (!trxFrom.save()) {
					m_processMsg = "Transaction From not inserted (MA) [" + product.getValue() + "] - ";
					return DocAction.STATUS_Invalid;
				}
				if (!MStorageOnHand.add(getCtx(), M_Locator_ID, product.getM_Product_ID(), M_AttributeSetInstance_ID, qty, dateMaterialPolicy, trxName)) {
					String lastError = CLogger.retrieveErrorString("");
					m_processMsg = "Cannot move Inventory OnHand to Shipment ASI [" + product.getValue() + "] - " + lastError;
					return DocAction.STATUS_Invalid;
				}
				MTransaction trxTo = new MTransaction (Env.getCtx(), getAD_Org_ID(), getMovementType(), M_Locator_ID, product.getM_Product_ID(), M_AttributeSetInstance_ID,
						qty, getMovementDate(), trxName);
				trxTo.setM_InOutLine_ID(M_InOutLine_ID);
				if (!trxTo.save()) {
					m_processMsg = "Transaction To not inserted (MA) [" + product.getValue() + "] - ";
					return DocAction.STATUS_Invalid;
				}
			} else {
				return doMove(product, M_Locator_ID, M_AttributeSetInstance_ID, dateMaterialPolicy, qty, M_InOutLine_ID, reversal, trxName);
			}
		} else {
			BigDecimal totalASI = BigDecimal.ZERO;			
			MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), 0,
					product.getM_Product_ID(), M_AttributeSetInstance_ID, null,
					MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), false,
					M_Locator_ID, get_TrxName());
			for (MStorageOnHand onhand : storages) {
				totalASI = totalASI.add(onhand.getQtyOnHand());
			}
			if (!reversal && totalASI.signum() != 0) 
				return null;
			else if (reversal && (totalASI.compareTo(qty) < 0))
				return null;
			
			return doMove(product, M_Locator_ID, M_AttributeSetInstance_ID, dateMaterialPolicy, qty, M_InOutLine_ID, reversal, trxName);
		}
		
		return null;
	}

	private String doMove(MProduct product, int M_Locator_ID, int M_AttributeSetInstance_ID, Timestamp dateMaterialPolicy, BigDecimal qty,
			int M_InOutLine_ID, boolean reversal, String trxName) {
		MStorageOnHand[] storages;
		BigDecimal totalOnHand = BigDecimal.ZERO;
		Timestamp onHandDateMaterialPolicy = null;
		storages = MStorageOnHand.getWarehouse(getCtx(), 0,
				product.getM_Product_ID(), 0, null,
				MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), true,
				M_Locator_ID, get_TrxName());
		List<MStorageOnHand> nonASIList = new ArrayList<>();
		for (MStorageOnHand storage : storages) {
			if (storage.getM_AttributeSetInstance_ID() == 0) {
				totalOnHand = totalOnHand.add(storage.getQtyOnHand());
				nonASIList.add(storage);
			}
		}
		if (totalOnHand.compareTo(qty) >= 0 || reversal) {
			BigDecimal totalToMove = qty;
			for (MStorageOnHand onhand : nonASIList) {
				BigDecimal toMove = totalToMove;
				if (!reversal && toMove.compareTo(onhand.getQtyOnHand()) >= 0) {
					toMove = onhand.getQtyOnHand();							
				}
				if (!MStorageOnHand.add(getCtx(), M_Locator_ID, product.getM_Product_ID(), 0, toMove.negate(), onhand.getDateMaterialPolicy(), trxName)) {
					String lastError = CLogger.retrieveErrorString("");
					m_processMsg = "Cannot move Inventory OnHand to Non ASI [" + product.getValue() + "] - " + lastError;
					return DocAction.STATUS_Invalid;
				}
				MTransaction trxFrom = new MTransaction (Env.getCtx(), getAD_Org_ID(), getMovementType(), M_Locator_ID, product.getM_Product_ID(), 0,
						toMove.negate(), getMovementDate(), trxName);
				trxFrom.setM_InOutLine_ID(M_InOutLine_ID);
				if (!trxFrom.save()) {
					m_processMsg = "Transaction From not inserted (MA) [" + product.getValue() + "] - ";
					return DocAction.STATUS_Invalid;
				}
				onHandDateMaterialPolicy = onhand.getDateMaterialPolicy();
				totalToMove = totalToMove.subtract(toMove);
				if ((!reversal && totalToMove.signum() <= 0) || (reversal && totalToMove.signum() >= 0))
					break;
			}
			if (!MStorageOnHand.add(getCtx(), M_Locator_ID, product.getM_Product_ID(), M_AttributeSetInstance_ID, qty, 
					(dateMaterialPolicy != null ? dateMaterialPolicy : onHandDateMaterialPolicy), trxName)) {
				String lastError = CLogger.retrieveErrorString("");
				m_processMsg = "Cannot move Inventory OnHand to Shipment ASI [" + product.getValue() + "] - " + lastError;
				return DocAction.STATUS_Invalid;
			}
			MTransaction trxTo = new MTransaction (Env.getCtx(), getAD_Org_ID(), getMovementType(), M_Locator_ID, product.getM_Product_ID(), M_AttributeSetInstance_ID,
					qty, getMovementDate(), trxName);
			trxTo.setM_InOutLine_ID(M_InOutLine_ID);
			if (!trxTo.save()) {
				m_processMsg = "Transaction To not inserted (MA) [" + product.getValue() + "] - ";
				return DocAction.STATUS_Invalid;
			}
		}
		return null;
	}
	
	/**
	 * Create Line from orderline/invoiceline/rmaline
	 * @param C_OrderLine_ID
	 * @param C_InvoiceLine_ID
	 * @param M_RMALine_ID
	 * @param M_Product_ID
	 * @param C_UOM_ID
	 * @param Qty
	 * @param M_Locator_ID
	 */
	public void createLineFrom(int C_OrderLine_ID, int C_InvoiceLine_ID, int M_RMALine_ID, 
			int M_Product_ID, int C_UOM_ID, BigDecimal Qty, int M_Locator_ID)
	{
		MInvoiceLine il = null;
		if (C_InvoiceLine_ID != 0)
			il = new MInvoiceLine (Env.getCtx(), C_InvoiceLine_ID, get_TrxName());
		
		MInOutLine iol = new MInOutLine (this);
		iol.setM_Product_ID(M_Product_ID, C_UOM_ID);	//	Line UOM
		iol.setQty(Qty);							//	Movement/Entered
		//
		MOrderLine ol = null;
		MRMALine rmal = null;
		if (C_OrderLine_ID != 0)
		{
			iol.setC_OrderLine_ID(C_OrderLine_ID);
			ol = new MOrderLine (Env.getCtx(), C_OrderLine_ID, get_TrxName());
			if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0)
			{
				iol.setMovementQty(Qty
						.multiply(ol.getQtyOrdered())
						.divide(ol.getQtyEntered(), 12, RoundingMode.HALF_UP));
				iol.setC_UOM_ID(ol.getC_UOM_ID());
			}
			iol.setM_AttributeSetInstance_ID(ol.getM_AttributeSetInstance_ID());
			iol.setDescription(ol.getDescription());
			//
			iol.setC_Project_ID(ol.getC_Project_ID());
			iol.setC_ProjectPhase_ID(ol.getC_ProjectPhase_ID());
			iol.setC_ProjectTask_ID(ol.getC_ProjectTask_ID());
			iol.setC_Activity_ID(ol.getC_Activity_ID());
			iol.setC_Campaign_ID(ol.getC_Campaign_ID());
			iol.setAD_OrgTrx_ID(ol.getAD_OrgTrx_ID());
			iol.setUser1_ID(ol.getUser1_ID());
			iol.setUser2_ID(ol.getUser2_ID());
		}
		else if (il != null)
		{
			if (il.getQtyEntered().compareTo(il.getQtyInvoiced()) != 0)
			{
				iol.setMovementQty(Qty
						.multiply(il.getQtyInvoiced())
						.divide(il.getQtyEntered(), 12, RoundingMode.HALF_UP));
				iol.setC_UOM_ID(il.getC_UOM_ID());
			}
			iol.setDescription(il.getDescription());
			iol.setC_Project_ID(il.getC_Project_ID());
			iol.setC_ProjectPhase_ID(il.getC_ProjectPhase_ID());
			iol.setC_ProjectTask_ID(il.getC_ProjectTask_ID());
			iol.setC_Activity_ID(il.getC_Activity_ID());
			iol.setC_Campaign_ID(il.getC_Campaign_ID());
			iol.setAD_OrgTrx_ID(il.getAD_OrgTrx_ID());
			iol.setUser1_ID(il.getUser1_ID());
			iol.setUser2_ID(il.getUser2_ID());
		}
		else if (M_RMALine_ID != 0)
		{
			rmal = new MRMALine(Env.getCtx(), M_RMALine_ID, get_TrxName());
			iol.setM_RMALine_ID(M_RMALine_ID);
			iol.setQtyEntered(Qty);
			iol.setDescription(rmal.getDescription());
			iol.setM_AttributeSetInstance_ID(rmal.getM_AttributeSetInstance_ID());
			iol.setC_Project_ID(rmal.getC_Project_ID());
			iol.setC_ProjectPhase_ID(rmal.getC_ProjectPhase_ID());
			iol.setC_ProjectTask_ID(rmal.getC_ProjectTask_ID());
			iol.setC_Activity_ID(rmal.getC_Activity_ID());
			iol.setAD_OrgTrx_ID(rmal.getAD_OrgTrx_ID());
			iol.setUser1_ID(rmal.getUser1_ID());
			iol.setUser2_ID(rmal.getUser2_ID());
		}

		//	Charge
		if (M_Product_ID == 0)
		{
			if (ol != null && ol.getC_Charge_ID() != 0)			//	from order
				iol.setC_Charge_ID(ol.getC_Charge_ID());
			else if (il != null && il.getC_Charge_ID() != 0)	//	from invoice
				iol.setC_Charge_ID(il.getC_Charge_ID());
			else if (rmal != null && rmal.getC_Charge_ID() != 0) // from rma
				iol.setC_Charge_ID(rmal.getC_Charge_ID());
		}
		// Set locator
		iol.setM_Locator_ID(M_Locator_ID);
		iol.saveEx();
		//	Create Invoice Line Link
		if (il != null)
		{
			il.setM_InOutLine_ID(iol.getM_InOutLine_ID());
			il.saveEx();
		}
	}
	
}	//	MInOut
