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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.model.POWrapper;
import org.compiere.model.MDocType;
import org.compiere.model.MInventory;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementConfirm;
import org.compiere.model.MMovementLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;
import org.compiere.wf.MWorkflow;


/**
 *	Inventory Movement Confirmation
 *	
 *  @author Jorg Janke
 *
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  @version $Id: MMovementConfirm.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MFG_MMovementConfirm extends MMovementConfirm implements DocAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3617284116557414217L;

	/**
	 * 	Create Confirmation or return existing one
	 *	@param move movement
	 *	@param checkExisting if false, new confirmation is created
	 *	@return Confirmation
	 */
	public static MFG_MMovementConfirm create (MFG_MMovement move, boolean checkExisting)
	{
		if (checkExisting)
		{
			MFG_MMovementConfirm[] confirmations = move.getConfirmations(false);
			if (confirmations.length > 0)
			{
				MFG_MMovementConfirm confirm = confirmations[0];
				return confirm;
			}
		}

		MFG_MMovementConfirm confirm = new MFG_MMovementConfirm (move);
		confirm.saveEx(move.get_TrxName());
		MMovementLine[] moveLines = move.getLines(false);
		for (int i = 0; i < moveLines.length; i++)
		{
			MMovementLine mLine = moveLines[i];
			MFG_MMovementLineConfirm cLine = new MFG_MMovementLineConfirm (confirm);
			cLine.setMovementLine(mLine);
			cLine.saveEx(move.get_TrxName());
		}
		return confirm;
	}	//	create

	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_MovementConfirm_ID id
	 *	@param trxName transaction
	 */
	public MFG_MMovementConfirm (Properties ctx, int M_MovementConfirm_ID, String trxName)
	{
		super (ctx, M_MovementConfirm_ID, trxName);
		if (M_MovementConfirm_ID == 0)
		{
			setDocAction (DOCACTION_Complete);
			setDocStatus (DOCSTATUS_Drafted);
			setIsApproved (false);	// N
			setProcessed (false);
		}
	}	//	MMovementConfirm

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MMovementConfirm (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MMovementConfirm

	/**
	 * 	Parent Constructor
	 *	@param move movement
	 */
	public MFG_MMovementConfirm (MMovement move)
	{
		this (move.getCtx(), 0, move.get_TrxName());
		setClientOrg(move);
		setM_Movement_ID(move.getM_Movement_ID());
	}	//	MMovementConfirm
	
	/**	Confirm Lines					*/
	protected MFG_MMovementLineConfirm[]	m_lines = null;
	
	/**	Physical Inventory From	*/
	protected MInventory				m_inventoryFrom = null;
	/**	Physical Inventory To	*/
	protected MInventory				m_inventoryTo = null;
	/**	Physical Inventory Info	*/
	protected String					m_inventoryInfo = null;
	protected List<MInventory>		m_inventoryDoc = null;		

	/**
	 * 	Get Lines
	 *	@param requery requery
	 *	@return array of lines
	 */
	public MFG_MMovementLineConfirm[] getLines (boolean requery)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		String sql = "SELECT * FROM M_MovementLineConfirm "
			+ "WHERE M_MovementConfirm_ID=?";
		ArrayList<MFG_MMovementLineConfirm> list = new ArrayList<MFG_MMovementLineConfirm>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, getM_MovementConfirm_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add(new MFG_MMovementLineConfirm(getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e); 
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		m_lines = new MFG_MMovementLineConfirm[list.size ()];
		list.toArray (m_lines);
		return m_lines;
	}	//	getLines
	
	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else
			setDescription(desc + " | " + description);
	}	//	addDescription
	
	
	/**
	 * 	Set Approved
	 *	@param IsApproved approval
	 */
	public void setIsApproved (boolean IsApproved)
	{
		if (IsApproved && !isApproved())
		{
			int AD_User_ID = Env.getAD_User_ID(getCtx());
			MUser user = MUser.get(getCtx(), AD_User_ID);
			String info = user.getName() 
				+ ": "
				+ Msg.translate(getCtx(), "IsApproved")
				+ " - " + new Timestamp(System.currentTimeMillis());
			addDescription(info);
		}
		super.setIsApproved (IsApproved);
	}	//	setIsApproved
	
	
	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		return Msg.getElement(getCtx(), "M_MovementConfirm_ID") + " " + getDocumentNo();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		try
		{
			File temp = File.createTempFile(get_TableName()+get_ID()+"_", ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}	//	createPDF

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		return null;
	}	//	createPDF

	
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

	/**
	 * 	Unlock Document.
	 * 	@return true if success 
	 */
	public boolean unlockIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	}	//	unlockIt
	
	/**
	 * 	Invalidate Document
	 * 	@return true if success 
	 */
	public boolean invalidateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + toString());
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

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getUpdated(), MDocType.DOCBASETYPE_MaterialMovement, getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		
		MFG_MMovementLineConfirm[] lines = getLines(true);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}
		for (int i = 0; i < lines.length; i++)
		{
			if (!lines[i].isFullyConfirmed())
			{
				break;
			}
			
			//Astina 260123
			I_M_MovementLineConfirm_Mfg mlc = POWrapper.create(lines[i], I_M_MovementLineConfirm_Mfg.class);
			if(mlc.getQtyReject().compareTo(Env.ZERO)>0)
			{
				MFG_MMovementLine mml = new MFG_MMovementLine(getCtx(), lines[i].getM_MovementLine_ID(), get_TrxName());
				MFG_MMovementLine mmlnew = new MFG_MMovementLine(getCtx(), 0, get_TrxName());
				copyValues(mml, mmlnew);
				mmlnew.setMovementQty(mlc.getQtyReject());
				mmlnew.setM_LocatorTo_ID(mlc.getM_LocatorTo_ID());
				//mmlnew.setM_Product_ID(mml.getM_Product_ID());
				//mmlnew.setAD_Org_ID(mml.getAD_Org_ID());
				//mmlnew.setM_Locator_ID(mml.getM_Locator_ID());
				//mmlnew.setM_AttributeSetInstance_ID(mml.getM_AttributeSetInstance_ID());
				//mmlnew.setM_AttributeSetInstanceTo_ID(mml.getM_AttributeSetInstanceTo_ID());
				//mmlnew.setM_Movement_ID(mml.getM_Movement_ID());
				mmlnew.setDescription("Scrapped Qty");
				mmlnew.saveEx(get_TrxName());
				
				//mml.setMovementQty(mml.getMovementQty().subtract(mlc.getQtyReject()));
				//mml.saveEx(get_TrxName());
				
			}
			//End Astina
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
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
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info("completeIt - " + toString());
		//
		m_inventoryDoc = new ArrayList<MInventory>();
		MMovement move = new MMovement (getCtx(), getM_Movement_ID(), get_TrxName());
		MFG_MMovementLineConfirm[] lines = getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MFG_MMovementLineConfirm confirm = lines[i];
			confirm.set_TrxName(get_TrxName());
			if (!confirm.processLine ())
			{
				m_processMsg = "ShipLine not saved - " + confirm;
				return DocAction.STATUS_Invalid;
			}
			
			if (confirm.isFullyConfirmed() && confirm.getScrappedQty().signum() == 0)
			{
				confirm.setProcessed(true);
				confirm.saveEx(get_TrxName());
			}
			else
			{
				if (createDifferenceDoc (move, confirm))
				{
					confirm.setProcessed(true);
					confirm.saveEx(get_TrxName());
				}
				else
				{
					log.log(Level.SEVERE, "completeIt - Scrapped=" + confirm.getScrappedQty()
						+ " - Difference=" + confirm.getDifferenceQty());
					
					if (m_processMsg == null)
						m_processMsg = "Difference Doc not created";
					return DocAction.STATUS_Invalid;
				}
			}
		}	//	for all lines
		
		//complete movement
		setProcessed(true);
		saveEx();
		ProcessInfo processInfo = MWorkflow.runDocumentActionWorkflow(move, DocAction.ACTION_Complete);
		if (processInfo.isError()) 
		{
			m_processMsg = processInfo.getSummary();
			setProcessed(false);
			return DocAction.STATUS_Invalid;
		}
				
		if (m_inventoryInfo != null)
		{
			//complete inventory doc
			for(MInventory inventory : m_inventoryDoc)
			{
				processInfo = MWorkflow.runDocumentActionWorkflow(inventory, DocAction.ACTION_Complete);
				if (processInfo.isError()) 
				{
					m_processMsg = processInfo.getSummary();
					setProcessed(false);
					return DocAction.STATUS_Invalid;
				}
			}
			
			m_processMsg = " @M_Inventory_ID@: " + m_inventoryInfo;
			addDescription(Msg.translate(getCtx(), "M_Inventory_ID") 
				+ ": " + m_inventoryInfo);
		}				
		
		m_inventoryDoc = null;
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			setProcessed(false);
			return DocAction.STATUS_Invalid;
		}
		
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt

	/**
	 * 
	 */
	protected void updateProcessMsg(String msg) {
		if (m_processMsg != null)
			m_processMsg = m_processMsg + " " + msg;
		else
			m_processMsg = msg;
		ValueNamePair error = CLogger.retrieveError();
		if (error != null)
			m_processMsg = m_processMsg + ": " + Msg.getMsg(Env.getCtx(), error.getValue()) + " " + error.getName();
	}

	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
		sb.append(": ")
			.append(Msg.translate(getCtx(),"ApprovalAmt")).append("=").append(getApprovalAmt())
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
		return getUpdatedBy();
	}	//	getDoc_User_ID

	/**
	 * 	Get Document Currency
	 *	@return C_Currency_ID
	 */
	public int getC_Currency_ID()
	{
		return 0;
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

}	//	MMovementConfirm
