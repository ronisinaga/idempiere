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
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_M_RequisitionLine;
import org.compiere.model.MDocType;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MRequisition;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;

/**
 *	Requisition Model
 *	
 *  @author Jorg Janke
 *
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  @version $Id: MRequisition.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 *  @author red1
 *  		<li>FR [ 2214883 ] Remove SQL code and Replace for Query  
 *  @author Teo Sarca, www.arhipac.ro
 *  		<li>FR [ 2744682 ] Requisition: improve error reporting
 */
public class MFG_MRequisition extends MRequisition implements DocAction, DocOptions
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 898606565778668659L;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_Requisition_ID id
	 */
	public MFG_MRequisition (Properties ctx, int M_Requisition_ID, String trxName)
	{
		super (ctx, M_Requisition_ID, trxName);
		if (M_Requisition_ID == 0)
		{
			setDateDoc(new Timestamp(System.currentTimeMillis()));
			setDateRequired (new Timestamp(System.currentTimeMillis()));
			setDocAction (DocAction.ACTION_Complete);	// CO
			setDocStatus (DocAction.STATUS_Drafted);		// DR
			setPriorityRule (PRIORITYRULE_Medium);	// 5
			setTotalLines (Env.ZERO);
			setIsApproved (false);
			setPosted (false);
			setProcessed (false);
		}
	}	//	MRequisition

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public MFG_MRequisition (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MRequisition
	
	/** Lines						*/
	private MFG_MRequisitionLine[]		m_lines = null;
	
	/**
	 * 	Get Lines
	 *	@return array of lines
	 */
	public MFG_MRequisitionLine[] getLines()
	{
		if (m_lines != null) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		
		//red1 - FR: [ 2214883 ] Remove SQL code and Replace for Query  
 	 	final String whereClause = I_M_RequisitionLine.COLUMNNAME_M_Requisition_ID+"=?";
	 	List <MFG_MRequisitionLine> list = new Query(getCtx(), I_M_RequisitionLine.Table_Name, whereClause, get_TrxName())
			.setParameters(get_ID())
			.setOrderBy(I_M_RequisitionLine.COLUMNNAME_Line+","+I_M_RequisitionLine.COLUMNNAME_M_RequisitionLine_ID)
			.list();
	 	//  red1 - end -

		m_lines = new MFG_MRequisitionLine[list.size ()];
		list.toArray (m_lines);
		return m_lines;
	}	//	getLines
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MRequisition[");
		sb.append(get_ID()).append("-").append(getDocumentNo())
			.append(",Status=").append(getDocStatus()).append(",Action=").append(getDocAction())
			.append ("]");
		return sb.toString ();
	}	//	toString
	
	/**
	 * 	Get Document Info
	 *	@return document info
	 */
	public String getDocumentInfo()
	{
		return Msg.getElement(getCtx(), "M_Requisition_ID") + " " + getDocumentNo();
	}	//	getDocumentInfo
	
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
	private String			m_processMsg = null;
	
	/**	Just Prepared Flag			*/
	private boolean 		m_justPrepared = false;
	
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
		MFG_MRequisitionLine[] lines = getLines();
		
		//	Invalid
		if (getAD_User_ID() == 0 
			|| getM_PriceList_ID() == 0
			|| getM_Warehouse_ID() == 0)
		{
			return DocAction.STATUS_Invalid;
		}
		
		if(lines.length == 0)
		{
			throw new AdempiereException("@NoLines@");
		}
		
		//	Std Period open?
		MPeriod.testPeriodOpen(getCtx(), getDateDoc(), MDocType.DOCBASETYPE_PurchaseRequisition, getAD_Org_ID());
		
		//	Add up Amounts
		int precision = MPriceList.getStandardPrecision(getCtx(), getM_PriceList_ID());
		BigDecimal totalLines = Env.ZERO;
		for (int i = 0; i < lines.length; i++)
		{
			MRequisitionLine line = lines[i];
			BigDecimal lineNet = line.getQty().multiply(line.getPriceActual());
			lineNet = lineNet.setScale(precision, RoundingMode.HALF_UP);
			if (lineNet.compareTo(line.getLineNetAmt()) != 0)
			{
				line.setLineNetAmt(lineNet);
				line.saveEx();
			}
			totalLines = totalLines.add (line.getLineNetAmt());
		}
		if (totalLines.compareTo(getTotalLines()) != 0)
		{
			setTotalLines(totalLines);
			saveEx();
		}
		
		//Astina 240123 Price Zero not Allowed
		for (int j = 0; j < lines.length; j++) 
		{
			MFG_MRequisitionLine line = lines[j];
			MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), get_TrxName());
			if (line.getPriceActual().compareTo(Env.ZERO)<=0)
			{
				m_processMsg = "@PriceActual@ "+product.getValue()+" "+product.getName()+" = "+line.getPriceActual();
				return DocAction.STATUS_Invalid;
			}
		}//End Astina
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		m_justPrepared = true;
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/** 
	 * 	Re-activate
	 * 	@return true if success 
	 */
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("reActivateIt - " + toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

	//	setProcessed(false);
		//if (! reverseCorrectIt())
		//	return false;
		
		setPosted(false);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;
		
		//Astina 211222
		setDocAction(DOCACTION_Complete);
		setProcessed(false);
		
		return true;
	}	//	reActivateIt
	
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
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		//
		setProcessed(true);
		setDocAction(ACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateDoc(TimeUtil.getDay(0));
			MPeriod.testPeriodOpen(getCtx(), getDateDoc(), MDocType.DOCBASETYPE_PurchaseRequisition, getAD_Org_ID());
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}

	
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
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		if (getM_PriceList_ID() == 0)
			setM_PriceList_ID();
		
		//Astina 080123
		if(this.getDateDoc().compareTo(this.getDateRequired())>0)
		{
			log.saveError("Error", "Document Date cannot higher than Required Date");
			return false;
			
		}
					
		return true;
	}	//	beforeSave
	
	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
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
		} else if (docStatus.equals(DocumentEngine.STATUS_Completed)) {
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_ReActivate;
		}

		return index;
	}
	
}	//	MRequisition
