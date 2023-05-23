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
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.NegativeInventoryDisallowedException;
import org.compiere.model.I_M_AttributeSet;
import org.compiere.model.I_M_Cost;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MCost;
import org.compiere.model.MDocType;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTransaction;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

/**
 *  Physical Inventory Model
 *
 *  @author Jorg Janke
 *  @version $Id: MInventory.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li>FR [ 1948157  ]  Is necessary the reference for document reverse
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *  @author Armen Rizal, Goodwill Consulting
 * 			<li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 *  @see https://sourceforge.net/p/adempiere/feature-requests/412/
 */
public class MFG_MInventory extends MInventory implements DocAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3877357565525655884L;
	
	/** Reversal Indicator			*/
	public static String	REVERSE_INDICATOR = "^";
	
	/**
	 * 	Get Inventory
	 *	@param M_Inventory_ID id
	 *	@return MInventory
	 */
	public static MFG_MInventory get (int M_Inventory_ID)
	{
		return get(Env.getCtx(), M_Inventory_ID);
	}
	
	/**
	 * 	Get Inventory 
	 *	@param ctx context
	 *	@param M_Inventory_ID id
	 *	@return MInventory
	 */
	public static MFG_MInventory get (Properties ctx, int M_Inventory_ID)
	{
		MFG_MInventory inventory = new MFG_MInventory(ctx, M_Inventory_ID, (String)null);
		if (inventory.get_ID() == M_Inventory_ID)
			return inventory;
		else
			return null;
	} //	get

	/**
	 * 	Standard Constructor
	 *	@param ctx context 
	 *	@param M_Inventory_ID id
	 *	@param trxName transaction
	 */
	public MFG_MInventory (Properties ctx, int M_Inventory_ID, String trxName)
	{
		super (ctx, M_Inventory_ID, trxName);
		if (M_Inventory_ID == 0)
		{
			setMovementDate (new Timestamp(System.currentTimeMillis()));
			setDocAction (DOCACTION_Complete);	// CO
			setDocStatus (DOCSTATUS_Drafted);	// DR
			setIsApproved (false);
			setMovementDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
			setPosted (false);
			setProcessed (false);
		}
	}	//	MInventory

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MInventory (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInventory

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
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, getAD_Org_ID());
		MInventoryLine[] lines = getLines(false);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		// Validate mandatory ASI on lines - IDEMPIERE-1770 - ASI validation must be moved to MInventory.prepareIt
		for (MInventoryLine line : lines) {
			//	Product requires ASI
			if (line.getM_AttributeSetInstance_ID() == 0)
			{
				MProduct product = MProduct.get(getCtx(), line.getM_Product_ID(), get_TrxName());
				if (product != null && product.isASIMandatoryFor(null, line.isSOTrx()))
				{
					if (product.getAttributeSet() != null && !product.getAttributeSet().excludeTableEntry(MInventoryLine.Table_ID, line.isSOTrx())) {
						MDocType dt = MDocType.get(getC_DocType_ID());
						String docSubTypeInv = dt.getDocSubTypeInv();
						BigDecimal qtyDiff = line.getQtyInternalUse();
						if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv))
							qtyDiff = line.getQtyBook().subtract(line.getQtyCount());
						// verify if the ASIs are captured on lineMA
						MFG_MInventoryLineMA mas[] = MFG_MInventoryLineMA.get(getCtx(),
								line.getM_InventoryLine_ID(), get_TrxName());
						BigDecimal qtyma = Env.ZERO;
						for (MFG_MInventoryLineMA ma : mas) {
							if (! ma.isAutoGenerated()) {
								qtyma = qtyma.add(ma.getMovementQty());
							}
						}
						if (qtyma.subtract(qtyDiff).signum() != 0) {
							m_processMsg = "@Line@ " + line.getLine() + ": @FillMandatory@ @M_AttributeSetInstance_ID@";
							return DocAction.STATUS_Invalid;
						}
					}
				}
			}	//	No ASI
		}
		
		//		TODO: Add up Amounts
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/**	Process Message 			*/
	protected String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	protected boolean		m_justPrepared = false;
	
	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		MDocType dt = MDocType.get(getC_DocType_ID());
		String docSubTypeInv = dt.getDocSubTypeInv();
		if (Util.isEmpty(docSubTypeInv)) {
			m_processMsg = "Document inventory subtype not configured, cannot complete";
			return DocAction.STATUS_Invalid;
		}

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

		StringBuilder errors = new StringBuilder();
		MInventoryLine[] lines = getLines(false);
		for (MInventoryLine line : lines)
		{
			if (!line.isActive())
				continue;

			MProduct product = line.getProduct();	
			try
			{
				BigDecimal qtyDiff = Env.ZERO;
				if (MDocType.DOCSUBTYPEINV_InternalUseInventory.equals(docSubTypeInv))
					qtyDiff = line.getQtyInternalUse().negate();
				else if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv))
					qtyDiff = line.getQtyCount().subtract(line.getQtyBook());
				else if (MDocType.DOCSUBTYPEINV_CostAdjustment.equals(docSubTypeInv))
				{
					if (!isReversal())
					{
						BigDecimal currentCost = line.getCurrentCostPrice();
						MClient client = MClient.get(getCtx(), getAD_Client_ID());
						MAcctSchema as = client.getAcctSchema();
						MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(getCtx(), client.get_ID());
						
						if (as.getC_Currency_ID() != getC_Currency_ID()) 
						{
							for (int i = 0; i < ass.length ; i ++)
							{
								MAcctSchema a =  ass[i];
								if (a.getC_Currency_ID() ==  getC_Currency_ID()) 
									as = a ; 
							}
						}
	
						MCost cost = product.getCostingRecord(as, getAD_Org_ID(), line.getM_AttributeSetInstance_ID(), getCostingMethod());
						if (cost != null && cost.getCurrentCostPrice().compareTo(currentCost) != 0) 
						{
							m_processMsg = "Current Cost for Line " + line.getLine() + " have changed.";
							return DocAction.STATUS_Invalid; 
						}
					}
				}
	
				//If Quantity Count minus Quantity Book = Zero, then no change in Inventory
				if(MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv) && !isReversal()) {
					
					//Astina 180123 check cost
					MClient client = MClient.get(getCtx(), getAD_Client_ID());
					MAcctSchema as = client.getAcctSchema();
					String CostingMethod = product.getCostingMethod(as);
					MCost cost = product.getCostingRecord(as, getAD_Org_ID(), line.getM_AttributeSetInstance_ID(), CostingMethod);
					
					if(cost == null)
					{
						//Sementara dicomment permintaan lord Adi
						//m_processMsg = "No Current Cost for Line " + line.getLine() + " Product: " +product.getValue();
						//return DocAction.STATUS_Invalid; 
					}
					//End Astina
					
					// We want to update Date Last Inventory on this records as well. 
					if (line.getM_AttributeSetInstance_ID() == 0 ) {							
						MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
								null, MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), true, line.getM_Locator_ID(), get_TrxName(), false);	
						if(storages != null) {
							for(MStorageOnHand storage: storages) {										
								storage.setDateLastInventory(getMovementDate());
								if (!storage.save(get_TrxName())) {
									m_processMsg = "Storage on hand not updated for DateLastInventory";
									return DocAction.STATUS_Invalid;
								}		
							}						
						}												
					} else {
						MStorageOnHand[] storages = MStorageOnHand.getAll(getCtx(), line.getM_Product_ID(), 
								line.getM_Locator_ID(),	line.getM_AttributeSetInstance_ID(), null, false, get_TrxName());						
						if(storages != null) {
							for(MStorageOnHand storage: storages) {										
								storage.setDateLastInventory(getMovementDate());
								if (!storage.save(get_TrxName())) {
									m_processMsg = "Storage on hand not updated for DateLastInventory";
									return DocAction.STATUS_Invalid;
								}		
							}						
						}	
					}														
				}
				if (qtyDiff.signum() == 0)
					continue;
	
				//Ignore the Material Policy when is Reverse Correction
				if(!isReversal()){
					BigDecimal qtyOnLineMA = MFG_MInventoryLineMA.getManualQty(line.getM_InventoryLine_ID(), get_TrxName());
					
					if(qtyDiff.signum()<0){
						if(qtyOnLineMA.compareTo(qtyDiff)<0){
							m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + line.getLine();
							return DOCSTATUS_Invalid;
						}
					}else{
						if(qtyOnLineMA.compareTo(qtyDiff)>0){
							m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + line.getLine();
							return DOCSTATUS_Invalid;
						}
					}
					checkMaterialPolicy(line, qtyDiff.subtract(qtyOnLineMA));
				}
				//	Stock Movement - Counterpart MOrder.reserveStock
				if (product != null 
						&& product.isStocked() )
				{
					log.fine("Material Transaction");
					MTransaction mtrx = null; 
	
					//If AttributeSetInstance = Zero then create new  AttributeSetInstance use Inventory Line MA else use current AttributeSetInstance
					if (line.getM_AttributeSetInstance_ID() == 0 || qtyDiff.compareTo(Env.ZERO) == 0)
					{
						MFG_MInventoryLineMA mas[] = MFG_MInventoryLineMA.get(getCtx(),
								line.getM_InventoryLine_ID(), get_TrxName());
	
						for (int j = 0; j < mas.length; j++)
						{
							MFG_MInventoryLineMA ma = mas[j];
							BigDecimal QtyMA = ma.getMovementQty();
							BigDecimal QtyNew = QtyMA.add(qtyDiff);
							if (log.isLoggable(Level.FINE)) log.fine("Diff=" + qtyDiff 
									+ " - Instance OnHand=" + QtyMA + "->" + QtyNew);
	
							if (!MStorageOnHand.add(getCtx(), 
									line.getM_Locator_ID(),
									line.getM_Product_ID(), 
									ma.getM_AttributeSetInstance_ID(), 
									QtyMA.negate(),ma.getDateMaterialPolicy(), getMovementDate(), get_TrxName()))
							{
								String lastError = CLogger.retrieveErrorString("");
								m_processMsg = "Cannot correct Inventory (MA) - " + lastError;
								return DocAction.STATUS_Invalid;
							}
	
							String m_MovementType =null;
							if(QtyMA.negate().compareTo(Env.ZERO) > 0 )
								m_MovementType = MTransaction.MOVEMENTTYPE_InventoryIn;
							else
								m_MovementType = MTransaction.MOVEMENTTYPE_InventoryOut;
							//	Transaction
							mtrx = new MTransaction (getCtx(), line.getAD_Org_ID(), m_MovementType,
									line.getM_Locator_ID(), line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
									QtyMA.negate(), getMovementDate(), get_TrxName());
							
								mtrx.setM_InventoryLine_ID(line.getM_InventoryLine_ID());
								if (!mtrx.save())
								{
									m_processMsg = "Transaction not inserted(2)";
									return DocAction.STATUS_Invalid;
								}
								
								qtyDiff = QtyNew;						
	
						}	
					}
	
					// Fallback
					if (mtrx == null)
					{
						Timestamp dateMPolicy= qtyDiff.signum() > 0 ? getMovementDate() : null;
						if (line.getM_AttributeSetInstance_ID() > 0)
						{
							Timestamp t = MStorageOnHand.getDateMaterialPolicy(line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), line.getM_Locator_ID(), line.get_TrxName());
							if (t != null)
								dateMPolicy = t;
						}
						
						//Fallback: Update Storage - see also VMatch.createMatchRecord
						if (!MStorageOnHand.add(getCtx(), 
								line.getM_Locator_ID(),
								line.getM_Product_ID(), 
								line.getM_AttributeSetInstance_ID(), 
								qtyDiff,dateMPolicy,getMovementDate(),get_TrxName()))
						{
							String lastError = CLogger.retrieveErrorString("");
							m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
							return DocAction.STATUS_Invalid;
						}
	
						String m_MovementType = null;
						if(qtyDiff.compareTo(Env.ZERO) > 0 )
							m_MovementType = MTransaction.MOVEMENTTYPE_InventoryIn;
						else
							m_MovementType = MTransaction.MOVEMENTTYPE_InventoryOut;
						//	Transaction
						mtrx = new MTransaction (getCtx(), line.getAD_Org_ID(), m_MovementType,
								line.getM_Locator_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								qtyDiff, getMovementDate(), get_TrxName());
						mtrx.setM_InventoryLine_ID(line.getM_InventoryLine_ID());
						if (!mtrx.save())
						{
							m_processMsg = "Transaction not inserted(2)";
							return DocAction.STATUS_Invalid;
						}					
					}	//	Fallback
				}	//	stock movement
			}
			catch (NegativeInventoryDisallowedException e)
			{
				log.severe(e.getMessage());
				errors.append(Msg.getElement(getCtx(), "Line")).append(" ").append(line.getLine()).append(": ");
				errors.append(e.getMessage()).append("\n");
			}

		}	//	for all lines

		if (errors.toString().length() > 0)
		{
			m_processMsg = errors.toString();
			return DocAction.STATUS_Invalid;
		}
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		//
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Set the definite document number after completed
	 */
	protected void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setMovementDate(TimeUtil.getDay(0));
			MPeriod.testPeriodOpen(getCtx(), getMovementDate(), MDocType.DOCBASETYPE_MaterialPhysicalInventory, getAD_Org_ID());
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}

	/**
	 * 	Check Material Policy.
	 */
	protected void checkMaterialPolicy(MInventoryLine line, BigDecimal qtyDiff)
	{	
		
		int no = MFG_MInventoryLineMA.deleteInventoryLineMA(line.getM_InventoryLine_ID(), get_TrxName());
		if (no > 0)
			if (log.isLoggable(Level.CONFIG)) log.config("Delete old #" + no);		
		
		if(qtyDiff.compareTo(Env.ZERO)==0)
			return;
		
		//	Attribute Set Instance
		if (line.getM_AttributeSetInstance_ID() == 0)
		{
			MProduct product = MProduct.get(getCtx(), line.getM_Product_ID(), get_TrxName());
			boolean serial = product.isSerial();
			if (qtyDiff.signum() > 0)	//	Incoming Trx
			{
				//auto balance negative on hand
				MStorageOnHand[] storages = MStorageOnHand.getWarehouseNegative(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
						null, MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()), line.getM_Locator_ID(), get_TrxName(), false);
				for (MStorageOnHand storage : storages)
				{
					if (storage.getM_AttributeSetInstance_ID() > 0 && serial)
					{
						MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), storage.getM_AttributeSetInstance_ID(), get_TrxName()); 
						if (!Util.isEmpty(asi.getSerNo(), true))
							continue;
					}
					if (storage.getQtyOnHand().signum() < 0)
					{
						BigDecimal maQty = qtyDiff;
						if(maQty.compareTo(storage.getQtyOnHand().negate())>0)
						{
							maQty = storage.getQtyOnHand().negate();
						}
						
						//backward compatibility: -ve in MA is incoming trx, +ve in MA is outgoing trx 
						MFG_MInventoryLineMA lineMA =  new MFG_MInventoryLineMA(line, storage.getM_AttributeSetInstance_ID(), maQty.negate(), storage.getDateMaterialPolicy(),true);
						lineMA.saveEx();
						
						qtyDiff = qtyDiff.subtract(maQty);
						if (qtyDiff.compareTo(Env.ZERO)==0)
							break;
					}
				}
				
				if(qtyDiff.compareTo(Env.ZERO)>0)
				{
					//AttributeSetInstance enable
					I_M_AttributeSet as = line.getM_Product().getM_AttributeSet();
					if (as != null && as.isInstanceAttribute())
					{
						//add quantity to last attributesetinstance
						storages = MStorageOnHand.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0, null,
								false, true, 0, get_TrxName());
						for (MStorageOnHand storage : storages)
						{
							if (storage.getM_AttributeSetInstance_ID() == 0)
								continue;
							
							if (serial)
							{
								MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), storage.getM_AttributeSetInstance_ID(), get_TrxName());
								if (!Util.isEmpty(asi.getSerNo(), true))
								{
									continue;
								}
							}
							BigDecimal maQty = qtyDiff;
							//backward compatibility: -ve in MA is incoming trx, +ve in MA is outgoing trx 
							MFG_MInventoryLineMA lineMA =  new MFG_MInventoryLineMA(line, storage.getM_AttributeSetInstance_ID(), maQty.negate(), storage.getDateMaterialPolicy(),true);
							lineMA.saveEx();
							qtyDiff = qtyDiff.subtract(maQty);

							if (qtyDiff.compareTo(Env.ZERO)==0)
								break;
							
						}
					} 
					if(qtyDiff.compareTo(Env.ZERO)>0)
					{
						MClientInfo m_clientInfo = MClientInfo.get(getCtx(), getAD_Client_ID(), get_TrxName());
						MAcctSchema acctSchema = new MAcctSchema(getCtx(), m_clientInfo.getC_AcctSchema1_ID(), get_TrxName());
						if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(acctSchema)) )
						{
							String sqlWhere = "M_Product_ID=? AND M_Locator_ID=? AND QtyOnHand = 0 AND M_AttributeSetInstance_ID > 0 ";
							MStorageOnHand storage = new Query(getCtx(), MStorageOnHand.Table_Name, sqlWhere, get_TrxName())
									.setParameters(line.getM_Product_ID(), line.getM_Locator_ID())
									.setOrderBy(MStorageOnHand.COLUMNNAME_DateMaterialPolicy+","+ MStorageOnHand.COLUMNNAME_M_AttributeSetInstance_ID)
									.first();
								
							if (storage != null )
							{
								MFG_MInventoryLineMA lineMA =  MFG_MInventoryLineMA.addOrCreate(line, storage.getM_AttributeSetInstance_ID(), qtyDiff.negate(), getMovementDate(),true);
								lineMA.saveEx();
							} 
							else
							{
								String costingMethod = product.getCostingMethod(acctSchema);
								StringBuilder localWhereClause = new StringBuilder("M_Product_ID =?" )
										.append(" AND C_AcctSchema_ID=?")
										.append(" AND ce.CostingMethod = ? ")
										.append(" AND CurrentCostPrice <> 0 ");
								MCost cost = new Query(getCtx(),I_M_Cost.Table_Name,localWhereClause.toString(),get_TrxName())
									.setParameters(line.getM_Product_ID(), acctSchema.get_ID(), costingMethod)
									.addJoinClause(" INNER JOIN M_CostElement ce ON (M_Cost.M_CostElement_ID =ce.M_CostElement_ID ) ")
									.setOrderBy("Updated DESC")
									.first();
								if (cost != null)
								{
									MFG_MInventoryLineMA lineMA =  MFG_MInventoryLineMA.addOrCreate(line, cost.getM_AttributeSetInstance_ID(), qtyDiff.negate(), getMovementDate(),true);
									lineMA.saveEx();
								} 
								else
								{
									m_processMsg = "Cannot retrieve cost of Inventory " ;
								}
							}
							
						} else
						{
							MFG_MInventoryLineMA lineMA =  MFG_MInventoryLineMA.addOrCreate(line, 0, qtyDiff.negate(), getMovementDate(),true);
							lineMA.saveEx();
						}
						
					}
				}				
			}
			else	//	Outgoing Trx
			{
				String MMPolicy = product.getMMPolicy();
				MStorageOnHand[] storages = MStorageOnHand.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
						null, MClient.MMPOLICY_FiFo.equals(MMPolicy), true, line.getM_Locator_ID(), get_TrxName(), false);

				BigDecimal qtyToDeliver = qtyDiff.negate();
				for (MStorageOnHand storage: storages)
				{					
					if (serial && storage.getM_AttributeSetInstance_ID() > 0)
					{
						MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), storage.getM_AttributeSetInstance_ID(), get_TrxName());
						if (!Util.isEmpty(asi.getSerNo(), true))
							continue;
					}
					if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0)
					{
						MFG_MInventoryLineMA ma = new MFG_MInventoryLineMA (line, 
								storage.getM_AttributeSetInstance_ID(),
								qtyToDeliver,storage.getDateMaterialPolicy(),true);
						ma.saveEx();		
						qtyToDeliver = Env.ZERO;
						if (log.isLoggable(Level.FINE)) log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);		
					}
					else
					{	
						MFG_MInventoryLineMA ma = new MFG_MInventoryLineMA (line, 
								storage.getM_AttributeSetInstance_ID(),
								storage.getQtyOnHand(),storage.getDateMaterialPolicy(),true);
						ma.saveEx();
						qtyToDeliver = qtyToDeliver.subtract(storage.getQtyOnHand());
						if (log.isLoggable(Level.FINE)) log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);		
					}
					if (qtyToDeliver.signum() == 0)
						break;
				}

				//	No AttributeSetInstance found for remainder
				if (qtyToDeliver.signum() != 0)
				{
					MFG_MInventoryLineMA lineMA =  MFG_MInventoryLineMA.addOrCreate(line, 0, qtyToDeliver, getMovementDate(),true);
					lineMA.saveEx();
					if (log.isLoggable(Level.FINE)) log.fine("##: " + lineMA);
				}
			}	//	outgoing Trx
		}	//	for all lines

	}	//	checkMaterialPolicy
	
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
	protected boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal
	
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

}	//	MInventory
