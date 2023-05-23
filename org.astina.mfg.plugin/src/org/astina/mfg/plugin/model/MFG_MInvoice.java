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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.ITaxProvider;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_InvoiceTax;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.MConversionRate;
import org.compiere.model.MConversionRateUtil;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MDocTypeCounter;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MMatchInv;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentProcessor;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.MPaymentTransaction;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUser;
import org.compiere.model.MatchPOAutoMatch;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.IDocsPostProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

/**
 *	Invoice Model.
 * 	Please do not set DocStatus and C_DocType_ID directly.
 * 	They are set in the process() method.
 * 	Use DocAction and C_DocTypeTarget_ID instead.
 *
 *  @author Jorg Janke
 *  @version $Id: MInvoice.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 *  		@see https://sourceforge.net/p/adempiere/feature-requests/412/
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  Modifications: Added RMA functionality (Ashley Ramdass)
 *  Modifications: Generate DocNo^ instead of using a new number whan an invoice is reversed (Diego Ruiz-globalqss)
 */
public class MFG_MInvoice extends MInvoice implements DocAction, IDocsPostProcess
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9166700544471146864L;

	/**************************************************************************
	 * 	Invoice Constructor
	 * 	@param ctx context
	 * 	@param C_Invoice_ID invoice or 0 for new
	 * 	@param trxName trx name
	 */
	public MFG_MInvoice (Properties ctx, int C_Invoice_ID, String trxName)
	{
		this (ctx, C_Invoice_ID, trxName, (String[]) null);
	}	//	MInvoice

	public MFG_MInvoice(Properties ctx, int C_Invoice_ID, String trxName, String... virtualColumns) {
		super(ctx, C_Invoice_ID, trxName, virtualColumns);
		if (C_Invoice_ID == 0)
		{
			setDocStatus (DOCSTATUS_Drafted);		//	Draft
			setDocAction (DOCACTION_Complete);
			//
			setPaymentRule(PAYMENTRULE_OnCredit);	//	Payment Terms

			setDateInvoiced (new Timestamp (System.currentTimeMillis ()));
			setDateAcct (new Timestamp (System.currentTimeMillis ()));
			//
			setChargeAmt (Env.ZERO);
			setTotalLines (Env.ZERO);
			setGrandTotal (Env.ZERO);
			//
			setIsSOTrx (true);
			setIsTaxIncluded (false);
			setIsApproved (false);
			setIsDiscountPrinted (false);
			setIsPaid (false);
			setSendEMail (false);
			setIsPrinted (false);
			setIsTransferred (false);
			setIsSelfService(false);
			setIsPayScheduleValid(false);
			setIsInDispute(false);
			setPosted(false);
			super.setProcessed (false);
			setProcessing(false);
		}
	}
	
	/**
	 * 	Create new Invoice by copying
	 * 	@param from invoice
	 * 	@param dateDoc date of the document date
	 *  @param dateAcct original account date 
	 * 	@param C_DocTypeTarget_ID target doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set Order links
	 *	@return Invoice
	 */
	public static MFG_MInvoice copyFrom (MInvoice from, Timestamp dateDoc, Timestamp dateAcct,
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
		String trxName, boolean setOrder)
	{
		return copyFrom (from, dateDoc, dateAcct,
				C_DocTypeTarget_ID, isSOTrx, counter,
				trxName, setOrder,null);
	}

	/**
	 * 	Create new Invoice by copying
	 * 	@param from invoice
	 * 	@param dateDoc date of the document date
	 *  @param dateAcct original account date 
	 * 	@param C_DocTypeTarget_ID target doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set Order links
	 *  @param documentNo Document Number for reversed invoices
	 *	@return Invoice
	 */
	public static MFG_MInvoice copyFrom (MInvoice from, Timestamp dateDoc, Timestamp dateAcct,
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
		String trxName, boolean setOrder, String documentNo)
	{
		MFG_MInvoice to = new MFG_MInvoice (from.getCtx(), 0, trxName);
		PO.copyValues (from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.set_ValueNoCheck ("C_Invoice_ID", I_ZERO);
		to.set_ValueNoCheck ("DocumentNo", documentNo);
		//
		to.setDocStatus (DOCSTATUS_Drafted);		//	Draft
		to.setDocAction(DOCACTION_Complete);
		//
		to.setC_DocType_ID(0);
		to.setC_DocTypeTarget_ID (C_DocTypeTarget_ID);
		to.setIsSOTrx(isSOTrx);
		//
		to.setDateInvoiced (dateDoc);
		to.setDateAcct (dateAcct);
		to.setDatePrinted(null);
		to.setIsPrinted (false);
		//
		to.setIsApproved (false);
		to.setC_Payment_ID(0);
		to.setC_CashLine_ID(0);
		to.setIsPaid (false);
		to.setIsInDispute(false);
		//
		//	Amounts are updated by trigger when adding lines
		to.setGrandTotal(Env.ZERO);
		to.setTotalLines(Env.ZERO);
		//
		to.setIsTransferred (false);
		to.setPosted (false);
		to.setProcessed (false);
		//[ 1633721 ] Reverse Documents- Processing=Y
		to.setProcessing(false);
		//	delete references
		to.setIsSelfService(false);
		if (!setOrder)
			to.setC_Order_ID(0);
		if (counter)
		{
			to.setRef_Invoice_ID(from.getC_Invoice_ID());
			MOrg org = MOrg.get(from.getAD_Org_ID());
			int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(trxName);
			if (counterC_BPartner_ID == 0)
				return null;
			to.setBPartner(MBPartner.get(from.getCtx(), counterC_BPartner_ID));
			//	Try to find Order link
			if (from.getC_Order_ID() != 0)
			{
				MOrder peer = new MOrder (from.getCtx(), from.getC_Order_ID(), from.get_TrxName());
				if (peer.getRef_Order_ID() != 0)
					to.setC_Order_ID(peer.getRef_Order_ID());
			}
			// Try to find RMA link
			if (from.getM_RMA_ID() != 0)
			{
				MRMA peer = new MRMA (from.getCtx(), from.getM_RMA_ID(), from.get_TrxName());
				if (peer.getRef_RMA_ID() > 0)
					to.setM_RMA_ID(peer.getRef_RMA_ID());
			}
			//
		}
		else
			to.setRef_Invoice_ID(0);

		to.saveEx(trxName);
		if (counter)
			from.setRef_Invoice_ID(to.getC_Invoice_ID());

		//	Lines
		if (to.copyLinesFrom(from, counter, setOrder) == 0)
			throw new IllegalStateException("Could not create Invoice Lines");

		return to;
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *	@param trxName transaction
	 */
	public MFG_MInvoice (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInvoice

	/** Reversal Flag		*/
	private boolean m_reversal = false;
	
	/**	Invoice Lines			*/
	private MFG_MInvoiceLine[]	m_lines;
	
	/**	Invoice Taxes			*/
	private MFG_MInvoiceTax[]	m_taxes;

	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	public void setReversal(boolean reversal)
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
	
	/**
	 * 	Get Taxes
	 *	@param requery requery
	 *	@return array of taxes
	 */
	public MFG_MInvoiceTax[] getTaxes (boolean requery)
	{
		if (m_taxes != null && !requery)
			return m_taxes;

		final String whereClause = MInvoiceTax.COLUMNNAME_C_Invoice_ID+"=?";
		List<MFG_MInvoiceTax> list = new Query(getCtx(), I_C_InvoiceTax.Table_Name, whereClause, get_TrxName())
										.setParameters(get_ID())
										.list();
		m_taxes = list.toArray(new MFG_MInvoiceTax[list.size()]);
		return m_taxes;
	}	//	getTaxes
	
	/**
	 * 	Set Processed.
	 * 	Propergate to Lines/Taxes
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		StringBuilder set = new StringBuilder("SET Processed='")
		.append((processed ? "Y" : "N"))
		.append("' WHERE C_Invoice_ID=").append(getC_Invoice_ID());
		
		StringBuilder msgdb = new StringBuilder("UPDATE C_InvoiceLine ").append(set);
		int noLine = DB.executeUpdate(msgdb.toString(), get_TrxName());
		msgdb = new StringBuilder("UPDATE C_InvoiceTax ").append(set);
		int noTax = DB.executeUpdate(msgdb.toString(), get_TrxName());
		m_lines = null;
		m_taxes = null;
		if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine + ", Tax=" + noTax);
	}	//	setProcessed
	
	/**
	 * 	Get Invoice Lines of Invoice
	 * 	@param whereClause starting with AND
	 * 	@return lines
	 */
	private MFG_MInvoiceLine[] getLines (String whereClause)
	{
		String whereClauseFinal = "C_Invoice_ID=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MFG_MInvoiceLine> list = new Query(getCtx(), I_C_InvoiceLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getC_Invoice_ID())
										.setOrderBy("Line, C_InvoiceLine_ID")
										.list();		
		return list.toArray(new MFG_MInvoiceLine[list.size()]);
	}	//	getLines

	/**
	 * 	Get Invoice Lines
	 * 	@param requery
	 * 	@return lines
	 */
	public MFG_MInvoiceLine[] getLines (boolean requery)
	{
		if (m_lines == null || m_lines.length == 0 || requery)
		{
			m_lines = getLines(null);
		}
		set_TrxName(m_lines, get_TrxName());
		return m_lines;
	}	//	getLines

	/**
	 * 	Get Lines of Invoice
	 * 	@return lines
	 */
	public MFG_MInvoiceLine[] getLines()
	{
		return getLines(false);
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
	}	//	process

	/**	Process Message 			*/
	private String		m_processMsg = null;
	
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;
	
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

		MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocTypeTarget_ID(), getAD_Org_ID());

		//	Lines
		MFG_MInvoiceLine[] lines = getLines(true);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		//	Convert/Check DocType
		if (getC_DocType_ID() != getC_DocTypeTarget_ID() )
			setC_DocType_ID(getC_DocTypeTarget_ID());
		if (getC_DocType_ID() == 0)
		{
			m_processMsg = "No Document Type";
			return DocAction.STATUS_Invalid;
		}

		explodeBOM();
		if (!calculateTaxTotal())	//	setTotals
		{
			m_processMsg = "Error calculating Tax";
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
			if (MInvoicePaySchedule.getInvoicePaySchedule(getCtx(), getC_Invoice_ID(), 0, get_TrxName()).length > 0) 
			{
				m_processMsg = "@ErrorPaymentSchedule@";
				return DocAction.STATUS_Invalid;
			}
		}

		//	Credit Status
		if (isSOTrx())
		{
			MDocType doc = (MDocType) getC_DocTypeTarget();
			// IDEMPIERE-365 - just check credit if is going to increase the debt
			if ( (doc.getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo) && getGrandTotal().signum() < 0 ) ||
				(doc.getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice) && getGrandTotal().signum() > 0 )
			   )
			{	
				MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), null);
				if ( MBPartner.SOCREDITSTATUS_CreditStop.equals(bp.getSOCreditStatus()) )
				{
					m_processMsg = "@BPartnerCreditStop@ - @TotalOpenBalance@="
							+ bp.getTotalOpenBalance()
							+ ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();
					return DocAction.STATUS_Invalid;
				}
			}  
		}

		//	Landed Costs
		if (!isSOTrx())
		{
			for (int i = 0; i < lines.length; i++)
			{
				MFG_MInvoiceLine line = lines[i];
				String error = line.allocateLandedCosts();
				if (error != null && error.length() > 0)
				{
					m_processMsg = error;
					return DocAction.STATUS_Invalid;
				}
			}
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Add up Amounts
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/**
	 * 	Explode non stocked BOM.
	 */
	private void explodeBOM ()
	{
		String where = "AND IsActive='Y' AND EXISTS "
			+ "(SELECT * FROM M_Product p WHERE C_InvoiceLine.M_Product_ID=p.M_Product_ID"
			+ " AND	p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";
		//
		String sql = "SELECT COUNT(*) FROM C_InvoiceLine "
			+ "WHERE C_Invoice_ID=? " + where;
		int count = DB.getSQLValueEx(get_TrxName(), sql, getC_Invoice_ID());
		while (count != 0)
		{
			renumberLines (100);

			//	Order Lines with non-stocked BOMs
			MFG_MInvoiceLine[] lines = getLines (where);
			for (int i = 0; i < lines.length; i++)
			{
				MFG_MInvoiceLine line = lines[i];
				MProduct product = MProduct.get (getCtx(), line.getM_Product_ID());
				if (log.isLoggable(Level.FINE)) log.fine(product.getName());
				//	New Lines
				int lineNo = line.getLine ();

				MPPProductBOM bom = MPPProductBOM.getDefault(product, get_TrxName());
				if (bom == null)
					continue;
				for (MPPProductBOMLine bomLine : bom.getLines())
				{
					MInvoiceLine newLine = new MInvoiceLine(this);
					newLine.setLine(++lineNo);
					newLine.setM_Product_ID(bomLine.getM_Product_ID(), true);
					newLine.setQty(line.getQtyInvoiced().multiply(bomLine.getQtyBOM()));
					if (bomLine.getDescription() != null)
						newLine.setDescription(bomLine.getDescription());
					newLine.setPrice();
					newLine.saveEx(get_TrxName());
				}

				//	Convert into Comment Line
				line.setM_Product_ID (0);
				line.setM_AttributeSetInstance_ID (0);
				line.setPriceEntered (Env.ZERO);
				line.setPriceActual (Env.ZERO);
				line.setPriceLimit (Env.ZERO);
				line.setPriceList (Env.ZERO);
				line.setLineNetAmt (Env.ZERO);
				//
				StringBuilder description = new StringBuilder().append(product.getName ());
				if (product.getDescription () != null)
					description.append(" ").append(product.getDescription ());
				if (line.getDescription () != null)
					description.append(" ").append(line.getDescription ());
				line.setDescription (description.toString());
				line.saveEx (get_TrxName());
			} //	for all lines with BOM

			m_lines = null;
			count = DB.getSQLValue (get_TrxName(), sql, getC_Invoice_ID ());
			renumberLines (10);
		}	//	while count != 0
	}	//	explodeBOM
	
	/**
	 * 	(Re) Create Pay Schedule
	 *	@return true if valid schedule
	 */
	private boolean createPaySchedule()
	{
		if (getC_PaymentTerm_ID() == 0)
			return false;
		MPaymentTerm pt = new MPaymentTerm(getCtx(), getC_PaymentTerm_ID(), null);
		if (log.isLoggable(Level.FINE)) log.fine(pt.toString());
		
		int numSchema = pt.getSchedule(false).length;
		
		MInvoicePaySchedule[] schedule = MInvoicePaySchedule.getInvoicePaySchedule
			(getCtx(), getC_Invoice_ID(), 0, get_TrxName());

		if (schedule.length > 0) {
			if (numSchema == 0)
				return false; // created a schedule for a payment term that doesn't manage schedule
			return validatePaySchedule();
		} else {
			boolean isValid = pt.apply(this);		//	calls validate pay schedule
			if (numSchema == 0)
				return true; // no schedule, no schema, OK
			else
				return isValid;
		}
	}	//	createPaySchedule
	
	/**
	 * 	Calculate Tax and Total
	 * 	@return true if calculated
	 */
	public boolean calculateTaxTotal()
	{
		log.fine("");
		//	Delete Taxes
		StringBuilder msgdb = new StringBuilder("DELETE FROM C_InvoiceTax WHERE C_Invoice_ID=").append(getC_Invoice_ID());
		DB.executeUpdateEx(msgdb.toString(), get_TrxName());
		m_taxes = null;

		MTaxProvider[] providers = getTaxProviders();
		for (MTaxProvider provider : providers)
		{
			ITaxProvider calculator = Core.getTaxProvider(provider);
			if (calculator == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
			
			//Comment by Astina 030523
			//if (!calculator.calculateInvoiceTaxTotal(provider, this))
			if (!calculateInvoiceTaxTotal(provider, this))
				return false;
		}
		return true;
	}	//	calculateTaxTotal
	
	//Astina 030523 add this method
	public boolean calculateInvoiceTaxTotal(MTaxProvider provider, MFG_MInvoice invoice) {
		//	Lines
		BigDecimal totalLines = Env.ZERO;
		ArrayList<Integer> taxList = new ArrayList<Integer>();
		MFG_MInvoiceLine[] lines = invoice.getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MFG_MInvoiceLine line = lines[i];
			totalLines = totalLines.add(line.getLineNetAmt());
			if (!taxList.contains(line.getC_Tax_ID()))
			{
				MTax tax = new MTax(invoice.getCtx(), line.getC_Tax_ID(), invoice.get_TrxName());
				if (tax.getC_TaxProvider_ID() != 0)
					continue;
				MFG_MInvoiceTax iTax = MFG_MInvoiceTax.get (line, invoice.getPrecision(), false, invoice.get_TrxName()); //	current Tax
				if (iTax != null)
				{
					iTax.setIsTaxIncluded(invoice.isTaxIncluded());
					if (!iTax.calculateTaxFromLines())
						return false;
					iTax.saveEx();
					taxList.add(line.getC_Tax_ID());
				}
			}
		}

		//	Taxes
		BigDecimal grandTotal = totalLines;
		MFG_MInvoiceTax[] taxes = invoice.getTaxes(true);
		for (int i = 0; i < taxes.length; i++)
		{
			MFG_MInvoiceTax iTax = taxes[i];
			if (iTax.getC_TaxProvider_ID() != 0) {
				if (!invoice.isTaxIncluded())
				    grandTotal = grandTotal.add(iTax.getTaxAmt());
		    	continue;
		    }
			MTax tax = iTax.getTax();
			if (tax.isSummary())
			{
				MTax[] cTaxes = tax.getChildTaxes(false);	//	Multiple taxes
				for (int j = 0; j < cTaxes.length; j++)
				{
					MTax cTax = cTaxes[j];
					BigDecimal taxAmt = cTax.calculateTax(iTax.getTaxBaseAmt(), false, invoice.getPrecision());
					//
					MFG_MInvoiceTax newITax = new MFG_MInvoiceTax(invoice.getCtx(), 0, invoice.get_TrxName());
					setClientOrg(invoice);
					newITax.setAD_Org_ID(invoice.getAD_Org_ID());
					newITax.setC_Invoice_ID(invoice.getC_Invoice_ID());
					newITax.setC_Tax_ID(cTax.getC_Tax_ID());
					newITax.setPrecision(invoice.getPrecision());
					newITax.setIsTaxIncluded(invoice.isTaxIncluded());
					newITax.setTaxBaseAmt(iTax.getTaxBaseAmt());
					newITax.setTaxAmt(taxAmt);
					newITax.saveEx(invoice.get_TrxName());
					//
					if (!invoice.isTaxIncluded())
						grandTotal = grandTotal.add(taxAmt);
				}
				iTax.deleteEx(true, invoice.get_TrxName());
			}
			else
			{
				if (!invoice.isTaxIncluded())
					grandTotal = grandTotal.add(iTax.getTaxAmt());
			}
		}
		//
		invoice.setTotalLines(totalLines);
		invoice.setGrandTotal(grandTotal);
		return true;
	}
	
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

		MFG_MInvoice reversal = reverse(false);
		if (reversal == null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		
		return true;
	}	//	reverseCorrectIt
	
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

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		StringBuilder info = new StringBuilder();
		
		// POS supports multiple payments
		boolean fromPOS = false;
		if ( getC_Order_ID() > 0 )
		{
			fromPOS = getC_Order().getC_POS_ID() > 0;
		}

  		//	Create Cash Payment
		if (PAYMENTRULE_Cash.equals(getPaymentRule()) && !fromPOS )
		{
			String whereClause = "AD_Org_ID=? AND C_Currency_ID=?";
			MBankAccount ba = new Query(getCtx(),MBankAccount.Table_Name,whereClause,get_TrxName())
				.setParameters(getAD_Org_ID(), getC_Currency_ID())
				.setOnlyActiveRecords(true)
				.setOrderBy("IsDefault DESC")
				.first();
			if (ba == null) {
				m_processMsg = "@NoAccountOrgCurrency@";
				return DocAction.STATUS_Invalid;
			}
			
			String docBaseType = "";
			if (isSOTrx())
				docBaseType=MDocType.DOCBASETYPE_ARReceipt;
			else
				docBaseType=MDocType.DOCBASETYPE_APPayment;
			
			MDocType[] doctypes = MDocType.getOfDocBaseType(getCtx(), docBaseType);
			if (doctypes == null || doctypes.length == 0) {
				m_processMsg = "No document type ";
				return DocAction.STATUS_Invalid;
			}
			MDocType doctype = null;
			for (MDocType doc : doctypes) {
				if (doc.getAD_Org_ID() == this.getAD_Org_ID()) {
					doctype = doc;
					break;
				}
			}
			if (doctype == null)
				doctype = doctypes[0];

			MFG_MPayment payment = new MFG_MPayment(getCtx(), 0, get_TrxName());
			payment.setAD_Org_ID(getAD_Org_ID());
			payment.setTenderType(MPayment.TENDERTYPE_Cash);
			payment.setC_BankAccount_ID(ba.getC_BankAccount_ID());
			payment.setC_BPartner_ID(getC_BPartner_ID());
			payment.setC_Invoice_ID(getC_Invoice_ID());
			payment.setC_Currency_ID(getC_Currency_ID());			
			payment.setC_DocType_ID(doctype.getC_DocType_ID());
			if (isCreditMemo())
				payment.setPayAmt(getGrandTotal().negate());
			else
				payment.setPayAmt(getGrandTotal());
			payment.setIsPrepayment(false);					
			payment.setDateAcct(getDateAcct());
			payment.setDateTrx(getDateInvoiced());

			//	Save payment
			payment.saveEx();

			payment.setDocAction(MPayment.DOCACTION_Complete);
			if (!payment.processIt(MPayment.DOCACTION_Complete)) {
				m_processMsg = "Cannot Complete the Payment : [" + payment.getProcessMsg() + "] " + payment;
				return DocAction.STATUS_Invalid;
			}

			payment.saveEx();
			info.append("@C_Payment_ID@: " + payment.getDocumentInfo());

			// IDEMPIERE-2588 - add the allocation generation with the payment
			if (payment.getJustCreatedAllocInv() != null)
				addDocsPostProcess(payment.getJustCreatedAllocInv());
		}	//	Payment

		//	Update Order & Match
		int matchInv = 0;
		int matchPO = 0;
		MFG_MInvoiceLine[] lines = getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MFG_MInvoiceLine line = lines[i];

			//	Matching - Inv-Shipment
			if (!isSOTrx()
				&& line.getM_InOutLine_ID() != 0
				&& line.getM_Product_ID() != 0
				&& !isReversal())
			{
				MInOutLine receiptLine = new MInOutLine (getCtx(),line.getM_InOutLine_ID(), get_TrxName());
				MInOut receipt = receiptLine.getParent();

				if (receipt.isProcessed()){

					BigDecimal movementQty = receiptLine.getM_InOut().getMovementType().charAt(1) == '-' ? receiptLine.getMovementQty().negate() : receiptLine.getMovementQty();
					BigDecimal matchQty = isCreditMemo() ? line.getQtyInvoiced().negate() : line.getQtyInvoiced();

					if (movementQty.compareTo(matchQty) < 0)
						matchQty = movementQty;

					MMatchInv inv = new MMatchInv(line, getDateInvoiced(), matchQty);
					if (!inv.save(get_TrxName()))
					{
						m_processMsg = CLogger.retrieveErrorString("Could not create Invoice Matching");
						return DocAction.STATUS_Invalid;
					}
					matchInv++;
					addDocsPostProcess(inv);
				}
			}
					
			//	Update Order Line
			MOrderLine ol = null;
			if (line.getC_OrderLine_ID() != 0)
			{
				if (isSOTrx()
					|| line.getM_Product_ID() == 0)
				{
					ol = new MOrderLine (getCtx(), line.getC_OrderLine_ID(), get_TrxName());
					if (line.getQtyInvoiced() != null) {
						ol.setQtyInvoiced(ol.getQtyInvoiced().add(isCreditMemo() ? line.getQtyInvoiced().negate() : line.getQtyInvoiced()));
					}
					if (!ol.save(get_TrxName()))
					{
						m_processMsg = "Could not update Order Line";
						return DocAction.STATUS_Invalid;
					}
				}
				//	Order Invoiced Qty updated via Matching Inv-PO
				else if (!isSOTrx()
					&& line.getM_Product_ID() != 0
					&& !isReversal())
				{
					//	MatchPO is created also from MInOut when Invoice exists before Shipment
					BigDecimal matchQty = isCreditMemo() ? line.getQtyInvoiced().negate() : line.getQtyInvoiced();					
					MMatchPO po = MMatchPO.create (line, null,
						getDateInvoiced(), matchQty);
					if (po != null) 
					{
						if (!po.save(get_TrxName()))
						{
							m_processMsg = "Could not create PO Matching";
							return DocAction.STATUS_Invalid;
						}
						matchPO++;
						if (!po.isPosted())
							addDocsPostProcess(po);
						
						MMatchInv[] matchInvoices = MMatchInv.getInvoiceLine(getCtx(), line.getC_InvoiceLine_ID(), get_TrxName());
						if (matchInvoices != null && matchInvoices.length > 0) 
						{
							for(MMatchInv matchInvoice : matchInvoices)
							{
								if (!matchInvoice.isPosted())
								{
									addDocsPostProcess(matchInvoice);
								}
								
								if (matchInvoice.getRef_MatchInv_ID() > 0)
								{
									MMatchInv refMatchInv = new MMatchInv(getCtx(), matchInvoice.getRef_MatchInv_ID(), get_TrxName());
									if (!refMatchInv.isPosted())
										addDocsPostProcess(refMatchInv);
								}
							}
						}
					}
				}
			}
			
			//Update QtyInvoiced RMA Line
			if (line.getM_RMALine_ID() != 0)
			{
				MRMALine rmaLine = new MRMALine (getCtx(),line.getM_RMALine_ID(), get_TrxName());
				if (rmaLine.getQtyInvoiced() != null)
					rmaLine.setQtyInvoiced(rmaLine.getQtyInvoiced().add(line.getQtyInvoiced()));
				else
					rmaLine.setQtyInvoiced(line.getQtyInvoiced());
				if (!rmaLine.save(get_TrxName()))
				{
					m_processMsg = "Could not update RMA Line";
					return DocAction.STATUS_Invalid;
				}
			}
			//			
		}	//	for all lines
		if (matchInv > 0)
			info.append(" @M_MatchInv_ID@#").append(matchInv).append(" ");
		if (matchPO > 0)
			info.append(" @M_MatchPO_ID@#").append(matchPO).append(" ");



		//	Update BP Statistics
		MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), get_TrxName());
		DB.getDatabase().forUpdate(bp, 0);
		//	Update total revenue and balance / credit limit (reversed on AllocationLine.processIt)
		BigDecimal invAmt = null;
		int baseCurrencyId = Env.getContextAsInt(getCtx(), Env.C_CURRENCY_ID);
		if (getC_Currency_ID() != baseCurrencyId && isOverrideCurrencyRate())
		{
			invAmt = getGrandTotal(true).multiply(getCurrencyRate());
			int stdPrecision = MCurrency.getStdPrecision(getCtx(), baseCurrencyId);
			if (invAmt.scale() > stdPrecision)
				invAmt = invAmt.setScale(stdPrecision, RoundingMode.HALF_UP);
		}
		else
		{
			invAmt = MConversionRate.convertBase(getCtx(), getGrandTotal(true),	//	CM adjusted
				getC_Currency_ID(), getDateAcct(), getC_ConversionType_ID(), getAD_Client_ID(), getAD_Org_ID());
		}
		if (invAmt == null)
		{
			m_processMsg = MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
					getC_Currency_ID(), MClient.get(getCtx()).getC_Currency_ID(), getC_ConversionType_ID(), getDateAcct(), get_TrxName());
			return DocAction.STATUS_Invalid;
		}
		//	Total Balance
		BigDecimal newBalance = bp.getTotalOpenBalance();
		if (newBalance == null)
			newBalance = Env.ZERO;
		if (isSOTrx())
		{
			newBalance = newBalance.add(invAmt);
			//
			if (bp.getFirstSale() == null)
				bp.setFirstSale(getDateInvoiced());
			BigDecimal newLifeAmt = bp.getActualLifeTimeValue();
			if (newLifeAmt == null)
				newLifeAmt = invAmt;
			else
				newLifeAmt = newLifeAmt.add(invAmt);
			BigDecimal newCreditAmt = bp.getSO_CreditUsed();
			if (newCreditAmt == null)
				newCreditAmt = invAmt;
			else
				newCreditAmt = newCreditAmt.add(invAmt);
			//
			if (log.isLoggable(Level.FINE)) log.fine("GrandTotal=" + getGrandTotal(true) + "(" + invAmt
				+ ") BP Life=" + bp.getActualLifeTimeValue() + "->" + newLifeAmt
				+ ", Credit=" + bp.getSO_CreditUsed() + "->" + newCreditAmt
				+ ", Balance=" + bp.getTotalOpenBalance() + " -> " + newBalance);
			bp.setActualLifeTimeValue(newLifeAmt);
			bp.setSO_CreditUsed(newCreditAmt);
		}	//	SO
		else
		{
			newBalance = newBalance.subtract(invAmt);
			if (log.isLoggable(Level.FINE)) log.fine("GrandTotal=" + getGrandTotal(true) + "(" + invAmt
				+ ") Balance=" + bp.getTotalOpenBalance() + " -> " + newBalance);
		}
		// the payment just created already updated the open balance
		if ( ! (PAYMENTRULE_Cash.equals(getPaymentRule()) && !fromPOS ) )
		{
			bp.setTotalOpenBalance(newBalance);
		}
		bp.setSOCreditStatus();
		if (!bp.save(get_TrxName()))
		{
			m_processMsg = "Could not update Business Partner";
			return DocAction.STATUS_Invalid;
		}

		//	User - Last Result/Contact
		if (getAD_User_ID() != 0)
		{
			MUser user = new MUser (getCtx(), getAD_User_ID(), get_TrxName());
			user.setLastContact(new Timestamp(System.currentTimeMillis()));
			StringBuilder msgset = new StringBuilder().append(Msg.translate(getCtx(), "C_Invoice_ID")).append(": ").append(getDocumentNo());
			user.setLastResult(msgset.toString());
			if (!user.save(get_TrxName()))
			{
				m_processMsg = "Could not update Business Partner User";
				return DocAction.STATUS_Invalid;
			}
		}	//	user

		//	Update Project
		if (isSOTrx() && getC_Project_ID() != 0)
		{
			MProject project = new MProject (getCtx(), getC_Project_ID(), get_TrxName());
			BigDecimal amt = getGrandTotal(true);
			int C_CurrencyTo_ID = project.getC_Currency_ID();
			if (C_CurrencyTo_ID != getC_Currency_ID())
				amt = MConversionRate.convert(getCtx(), amt, getC_Currency_ID(), C_CurrencyTo_ID,
					getDateAcct(), 0, getAD_Client_ID(), getAD_Org_ID());
			if (amt == null)
			{
				m_processMsg = MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToProjectCurrency",
						getC_Currency_ID(), C_CurrencyTo_ID, 0, getDateAcct(), get_TrxName());
				return DocAction.STATUS_Invalid;
			}
			BigDecimal newAmt = project.getInvoicedAmt();
			if (newAmt == null)
				newAmt = amt;
			else
				newAmt = newAmt.add(amt);
			if (log.isLoggable(Level.FINE)) log.fine("GrandTotal=" + getGrandTotal(true) + "(" + amt
				+ ") Project " + project.getName()
				+ " - Invoiced=" + project.getInvoicedAmt() + "->" + newAmt);
			project.setInvoicedAmt(newAmt);
			if (!project.save(get_TrxName()))
			{
				m_processMsg = "Could not update Project";
				return DocAction.STATUS_Invalid;
			}
		}	//	project
		
		// auto delay capture authorization payment
		if (isSOTrx() && !isReversal())
		{
			StringBuilder whereClause = new StringBuilder();
			whereClause.append("C_Order_ID IN (");
			whereClause.append("SELECT C_Order_ID ");
			whereClause.append("FROM C_OrderLine ");
			whereClause.append("WHERE C_OrderLine_ID IN (");
			whereClause.append("SELECT C_OrderLine_ID ");
			whereClause.append("FROM C_InvoiceLine ");
			whereClause.append("WHERE C_Invoice_ID = ");
			whereClause.append(getC_Invoice_ID()).append("))");
			int[] orderIDList = MOrder.getAllIDs(MOrder.Table_Name, whereClause.toString(), get_TrxName());
			
			int[] ids = MPaymentTransaction.getAuthorizationPaymentTransactionIDs(orderIDList, getC_Invoice_ID(), get_TrxName());			
			if (ids.length > 0)
			{
				boolean pureCIM = true;
				ArrayList<MPaymentTransaction> ptList = new ArrayList<MPaymentTransaction>();
				BigDecimal totalPayAmt = BigDecimal.ZERO;
				for (int id : ids)
				{
					MPaymentTransaction pt = new MPaymentTransaction(getCtx(), id, get_TrxName());
					
					if (!pt.setPaymentProcessor())
					{
						if (pt.getC_PaymentProcessor_ID() > 0)
						{
							MPaymentProcessor pp = new MPaymentProcessor(getCtx(), pt.getC_PaymentProcessor_ID(), get_TrxName());
							m_processMsg = Msg.getMsg(getCtx(), "PaymentNoProcessorModel") + ": " + pp.toString();
						}
						else
							m_processMsg = Msg.getMsg(getCtx(), "PaymentNoProcessorModel");
						return DocAction.STATUS_Invalid;
					}
					
					boolean isCIM = pt.getC_PaymentProcessor_ID() > 0 && pt.getCustomerPaymentProfileID() != null && pt.getCustomerPaymentProfileID().length() > 0;
					if (pureCIM && !isCIM)
						pureCIM = false;
					
					totalPayAmt = totalPayAmt.add(pt.getPayAmt());
					ptList.add(pt);
				}
				
				// automatically void authorization payment and create a new sales payment when invoiced amount is NOT equals to the authorized amount (applied to CIM payment processor)
				if (getGrandTotal().compareTo(totalPayAmt) != 0 && ptList.size() > 0 && pureCIM)
				{
					// create a new sales payment
					MPaymentTransaction newSalesPT = MPaymentTransaction.copyFrom(ptList.get(0), new Timestamp(System.currentTimeMillis()), MPayment.TRXTYPE_Sales, "", get_TrxName());
					newSalesPT.setIsApproved(false);
					newSalesPT.setIsVoided(false);
					newSalesPT.setIsDelayedCapture(false);
					newSalesPT.setDescription("InvoicedAmt: " + getGrandTotal() + " <> TotalAuthorizedAmt: " + totalPayAmt);
					newSalesPT.setC_Invoice_ID(getC_Invoice_ID());
					newSalesPT.setPayAmt(getGrandTotal());
					
					// void authorization payment
					for (MPaymentTransaction pt : ptList)
					{
						pt.setDescription("InvoicedAmt: " + getGrandTotal() + " <> AuthorizedAmt: " + pt.getPayAmt());
						boolean ok = pt.voidOnlineAuthorizationPaymentTransaction();
						pt.saveEx();
						if (!ok)
						{
							m_processMsg = Msg.getMsg(getCtx(), "VoidAuthorizationPaymentFailed") + ": " + pt.getErrorMessage();
							return DocAction.STATUS_Invalid;
						}					
					}
					
					// process the new sales payment
					boolean ok = newSalesPT.processOnline();
					newSalesPT.saveEx();
					if (!ok)
					{
						m_processMsg = Msg.getMsg(getCtx(), "CreateNewSalesPaymentFailed") + ": " + newSalesPT.getErrorMessage();
						return DocAction.STATUS_Invalid;
					}
				}
				else if (getGrandTotal().compareTo(totalPayAmt) != 0 && ptList.size() > 0)
				{
					m_processMsg = "InvoicedAmt: " + getGrandTotal() + " <> AuthorizedAmt: " + totalPayAmt;
					return DocAction.STATUS_Invalid;
				}
				else
				{
					// delay capture authorization payment
					for (MPaymentTransaction pt : ptList)
					{
						boolean ok = pt.delayCaptureOnlineAuthorizationPaymentTransaction(getC_Invoice_ID());
						pt.saveEx();
						if (!ok)
						{
							m_processMsg = Msg.getMsg(getCtx(), "DelayCaptureAuthFailed") + ": " + pt.getErrorMessage();
							return DocAction.STATUS_Invalid;
						}					
					}
				}
				if (testAllocation(true)) {
					saveEx();
				}
			}
		}

		if (PAYMENTRULE_Cash.equals(getPaymentRule())) {
			if (testAllocation(true)) {
				saveEx();
			}
		}
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		//	Counter Documents
		MFG_MInvoice counter = createCounterDoc();
		if (counter != null)
			info.append(" - @CounterDoc@: @C_Invoice_ID@=").append(counter.getDocumentNo());

		m_processMsg = info.toString().trim();
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Create Counter Document
	 * 	@return counter invoice
	 */
	private MFG_MInvoice createCounterDoc()
	{
		//	Is this a counter doc ?
		if (getRef_Invoice_ID() != 0)
			return null;

		//	Org Must be linked to BPartner
		MOrg org = MOrg.get(getAD_Org_ID());
		int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(get_TrxName());
		if (counterC_BPartner_ID == 0)
			return null;
		//	Business Partner needs to be linked to Org
		MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), null);
		int counterAD_Org_ID = bp.getAD_OrgBP_ID();
		if (counterAD_Org_ID == 0)
			return null;

		MBPartner counterBP = new MBPartner (getCtx(), counterC_BPartner_ID, null);

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
		MFG_MInvoice counter = copyFrom(this, getDateInvoiced(), getDateAcct(),
			C_DocTypeTarget_ID, !isSOTrx(), true, get_TrxName(), true);
		//
		counter.setAD_Org_ID(counterAD_Org_ID);
		//	References (Should not be required)
		counter.setSalesRep_ID(getSalesRep_ID());
		counter.saveEx(get_TrxName());

		//	Update copied lines
		MFG_MInvoiceLine[] counterLines = counter.getLines(true);
		for (int i = 0; i < counterLines.length; i++)
		{
			MFG_MInvoiceLine counterLine = counterLines[i];
			//Astina 270223
			//counterLine.setClientOrg(counter);
			
			if (counter.getAD_Org_ID() != getAD_Org_ID())
				counterLine.setAD_Org_ID(counter.getAD_Org_ID());
			//End Astina
			
			counterLine.setInvoice(counter);	//	copies header values (BP, etc.)
			counterLine.setPrice();
			counterLine.setTax();
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
	
	/* Save array of documents to process AFTER completing this one */
	ArrayList<PO> docsPostProcess = new ArrayList<PO>();

	private void addDocsPostProcess(PO doc) {
		docsPostProcess.add(doc);
	}

	@Override
	public List<PO> getDocsPostProcess() {
		return docsPostProcess;
	}
	
	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		if (isReversal() && ! MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getAD_Client_ID())) // IDEMPIERE-1771
			return;
		MDocType dt = MDocType.get(getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateInvoiced(TimeUtil.getDay(0));
			if (getDateAcct().before(getDateInvoiced())) {
				setDateAcct(getDateInvoiced());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}

	private MFG_MInvoice reverse(boolean accrual) {
		Timestamp reversalDate = accrual ? Env.getContextAsDate(getCtx(), Env.DATE) : getDateAcct();
		if (reversalDate == null) {
			reversalDate = new Timestamp(System.currentTimeMillis());
		}
		Timestamp reversalDateInvoiced = accrual ? reversalDate : getDateInvoiced();
		
		MPeriod.testPeriodOpen(getCtx(), reversalDate, getC_DocType_ID(), getAD_Org_ID());
		//
		reverseAllocations(accrual, getC_Invoice_ID());
		//	Reverse/Delete Matching
		if (!isSOTrx())
		{
			MatchPOAutoMatch.unmatch(getCtx(), getC_Invoice_ID(), get_TrxName());
			
			MMatchInv[] mInv = MMatchInv.getInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
			for (int i = 0; i < mInv.length; i++)
			{
				if (mInv[i].getReversal_ID() > 0)
					continue;
				
				if (!mInv[i].reverse(reversalDate)) 
				{
					m_processMsg = "Could not Reverse MatchInv";
					return null;
				}
				addDocsPostProcess(new MMatchInv(Env.getCtx(), mInv[i].getReversal_ID(), get_TrxName()));
			}			
			
			MMatchPO[] mPO = MMatchPO.getInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
			for (int i = 0; i < mPO.length; i++)
			{
				if (mPO[i].getReversal_ID() > 0)
					continue;
				
				if (mPO[i].getM_InOutLine_ID() == 0)
				{
					if(mPO[i].isPosted())
					{
						if (!mPO[i].reverse(reversalDate)) 
						{
							m_processMsg = "Could not Reverse MatchPO";
							return null;
						}
						addDocsPostProcess(new MMatchPO(Env.getCtx(), mPO[i].getReversal_ID(), get_TrxName()));
					} 
					else
					{
						mPO[i].deleteEx(true);						
					}
				}
				else
				{
					mPO[i].setC_InvoiceLine_ID(null);
					mPO[i].saveEx(get_TrxName());
				}
			}
		}
		//
		load(get_TrxName());	//	reload allocation reversal info

		//	Deep Copy
		MFG_MInvoice reversal = null;
		if (MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getAD_Client_ID()))
			reversal = (MFG_MInvoice) copyFrom (this, reversalDateInvoiced, reversalDate, getC_DocType_ID(), isSOTrx(), false, get_TrxName(), true);
		else 
			reversal = (MFG_MInvoice) copyFrom (this, reversalDateInvoiced, reversalDate, getC_DocType_ID(), isSOTrx(), false, get_TrxName(), true, getDocumentNo()+"^");
		if (reversal == null)
		{
			m_processMsg = "Could not create Invoice Reversal";
			return null;
		}
		reversal.setReversal(true);

		//	Reverse Line Qty
		MFG_MInvoiceLine[] oLines = getLines(false);
		MFG_MInvoiceLine[] rLines = reversal.getLines(true);
		for (int i = 0; i < rLines.length; i++)
		{
			MFG_MInvoiceLine rLine = rLines[i];
			rLine.getParent().setReversal(true);
			MFG_MInvoiceLine oLine = oLines[i];
			rLine.setQtyEntered(oLine.getQtyEntered().negate());
			rLine.setQtyInvoiced(oLine.getQtyInvoiced().negate());
			rLine.setLineNetAmt(oLine.getLineNetAmt().negate());
			rLine.setTaxAmt(oLine.getTaxAmt().negate());
			rLine.setLineTotalAmt(oLine.getLineTotalAmt().negate());
			rLine.setPriceActual(oLine.getPriceActual());
			rLine.setPriceList(oLine.getPriceList());
			rLine.setPriceLimit(oLine.getPriceLimit());
			rLine.setPriceEntered(oLine.getPriceEntered());
			rLine.setC_UOM_ID(oLine.getC_UOM_ID());
			if (!rLine.save(get_TrxName()))
			{
				m_processMsg = "Could not correct Invoice Reversal Line";
				return null;
			}
		}
		reversal.setC_Order_ID(getC_Order_ID());
		StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
		reversal.addDescription(msgadd.toString());
		//FR1948157
		reversal.setReversal_ID(getC_Invoice_ID());
		reversal.saveEx(get_TrxName());
		//
		reversal.docsPostProcess = this.docsPostProcess;
		this.docsPostProcess = new ArrayList<PO>();
		//
		if (!reversal.processIt(DocAction.ACTION_Complete))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}
		//
		reverseAllocations(accrual, reversal.getC_Invoice_ID());

		reversal.setC_Payment_ID(0);
		reversal.setIsPaid(true);
		reversal.closeIt();
		reversal.setProcessing (false);
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());
		//
		msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
		addDescription(msgadd.toString());

		//	Clean up Reversed (this)
		MInvoiceLine[] iLines = getLines(false);
		for (int i = 0; i < iLines.length; i++)
		{
			MInvoiceLine iLine = iLines[i];
			if (iLine.getM_InOutLine_ID() != 0)
			{
				MInOutLine ioLine = new MInOutLine(getCtx(), iLine.getM_InOutLine_ID(), get_TrxName());
				ioLine.setIsInvoiced(false);
				ioLine.saveEx(get_TrxName());
				//	Reconsiliation
				iLine.setM_InOutLine_ID(0);
				iLine.saveEx(get_TrxName());
			}
        }
		setProcessed(true);
		//FR1948157
		setReversal_ID(reversal.getC_Invoice_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);
		setC_Payment_ID(0);
		setIsPaid(true);
		
		//Astina 241222
		StringBuilder msgd = new StringBuilder(getDocumentNo()).append("<-").append(reversal.getDocumentNo());
		setDocumentNo(msgd.toString());

		//	Create Allocation
		StringBuilder msgall = new StringBuilder().append(Msg.translate(getCtx(), "C_Invoice_ID")).append(": ").append(getDocumentNo()).append("/").append(reversal.getDocumentNo());
		MAllocationHdr alloc = new MAllocationHdr(getCtx(), false, reversalDate,
			getC_Currency_ID(),
			msgall.toString(),
			get_TrxName());
		alloc.setAD_Org_ID(getAD_Org_ID());
		alloc.saveEx();
		//	Amount
		BigDecimal gt = getGrandTotal(true);
		if (!isSOTrx())
			gt = gt.negate();
		//	Orig Line
		MAllocationLine aLine = new MAllocationLine (alloc, gt,
				Env.ZERO, Env.ZERO, Env.ZERO);
		aLine.setC_Invoice_ID(getC_Invoice_ID());
		aLine.saveEx();
		//	Reversal Line
		MAllocationLine rLine = new MAllocationLine (alloc, gt.negate(),
				Env.ZERO, Env.ZERO, Env.ZERO);
		rLine.setC_Invoice_ID(reversal.getC_Invoice_ID());
		rLine.saveEx();
		// added AdempiereException by zuhri
		if (!alloc.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(Msg.getMsg(getCtx(), "FailedProcessingDocument") + " - " + alloc.getProcessMsg());
		// end added
		alloc.saveEx();
		
		return reversal;
	}
	
	private void reverseAllocations(boolean accrual, int invoiceID) {
		for (MAllocationHdr allocation : MAllocationHdr.getOfInvoice(getCtx(), invoiceID, get_TrxName())) {
			if (accrual) {
				allocation.setDocAction(DocAction.ACTION_Reverse_Accrual);
				allocation.reverseAccrualIt();
			} else {
				allocation.setDocAction(DocAction.ACTION_Reverse_Correct);
				allocation.reverseCorrectIt();
			}
			allocation.saveEx(get_TrxName());
		}
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

		MFG_MInvoice reversal = reverse(true);
		if (reversal == null)
			return false;
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		
		return true;
	}	//	reverseAccrualIt
	
	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(false);
		
		//Astina 110123
		this.setProcessed(false);
				
		return true;
	}	//	rejectIt
	
	/**
	 * 	Before Delete
	 *	@return true if it can be deleted
	 */
	protected boolean beforeDelete ()
	{
		if (getC_Order_ID() != 0)
		{
			//Load invoice lines for afterDelete()
			getLines();	
		}
		
		String sql = "SELECT MAX(C_JobCode_ID) FROM C_JobCode WHERE AD_Client_ID=? AND value=?";
		int ii = DB.getSQLValue (get_TrxName(), sql, getAD_Client_ID(), getDocumentNo());
		
		String sql1 = "DELETE FROM C_JobCode WHERE C_JobCode_ID=?";
		int i2 = DB.executeUpdate(sql1, ii, get_TrxName());
		if (i2 == 0);
		
		return true;
		
	}	//	beforeDelete
	
	/**
	 * After Delete
	 * @param success success
	 * @return deleted
	 */
	protected boolean afterDelete(boolean success) {
		// If delete invoice failed then do nothing
		if (!success)
			return success;
		
		if (getC_Order_ID() != 0) {
			// reset shipment line invoiced flag
			MFG_MInvoiceLine[] lines = getLines(false);
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].getM_InOutLine_ID() > 0) {
					MInOutLine sLine = new MInOutLine(getCtx(), lines[i].getM_InOutLine_ID(), get_TrxName());
					sLine.setIsInvoiced(false);
					sLine.saveEx();
				}
			}	
		}			
		return true;
	} //afterDelete

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt;
		if (getC_DocType_ID() == 0) {
			dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		} else {
			dt = MDocType.get(getCtx(), getC_DocType_ID());
		}
		StringBuilder msgreturn = new StringBuilder().append(dt.getNameTrl()).append(" ").append(getDocumentNo());
		return msgreturn.toString();
	}	//	getDocumentInfo


	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return success
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		//Astina 280323
		String sql1 = "SELECT C_JobCode_ID FROM C_JobCode WHERE AD_Client_ID=? AND value=?";
		int ii = DB.getSQLValue (get_TrxName(), sql1, getAD_Client_ID(), getDocumentNo());
		
		if(ii <= 0 && newRecord)
		{
			MJobCode jc = new MJobCode(this.getCtx(), 0, this.get_TrxName());
			jc.setAD_Org_ID(this.getAD_Org_ID());
			jc.setValue(this.getDocumentNo());
			jc.setName(this.getDocumentNo());
			jc.setDescription(this.getDescription());
			jc.saveEx(get_TrxName());
		}else
		{
			MJobCode jc = new MJobCode(this.getCtx(), ii, this.get_TrxName());
			jc.setAD_Org_ID(this.getAD_Org_ID());
			jc.setValue(this.getDocumentNo());
			jc.setName(this.getDocumentNo());
			jc.setDescription(this.getDescription());
			jc.saveEx(get_TrxName());
		}
		//End Astina
		
		if (!success || newRecord)
			return success;

		if (is_ValueChanged("AD_Org_ID"))
		{
			StringBuilder sql = new StringBuilder("UPDATE C_InvoiceLine ol")
				.append(" SET AD_Org_ID =")
					.append("(SELECT AD_Org_ID")
					.append(" FROM C_Invoice o WHERE ol.C_Invoice_ID=o.C_Invoice_ID) ")
				.append("WHERE C_Invoice_ID=").append(getC_Invoice_ID());
			int no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Lines -> #" + no);
		}
			
		return true;
	}	//	afterSave
	
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
			append(Msg.translate(getCtx(),"GrandTotal")).append("=").append(getGrandTotal())
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
		return getGrandTotal();
	}	//	getApprovalAmt

}	//	MInvoice
