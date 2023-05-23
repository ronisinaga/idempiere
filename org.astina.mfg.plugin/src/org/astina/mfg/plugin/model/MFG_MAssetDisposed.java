package org.astina.mfg.plugin.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.POWrapper;
import org.compiere.model.I_A_Asset_Disposed;
import org.compiere.model.MAsset;
import org.compiere.model.MAssetChange;
import org.compiere.model.MAssetDisposed;
import org.compiere.model.MClient;
import org.compiere.model.MDepreciationExp;
import org.compiere.model.MDepreciationWorkfile;
import org.compiere.model.MRefList;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.util.Env;


/**
 * Asset Disposal Model
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MFG_MAssetDisposed extends MAssetDisposed
implements DocAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1763997880662445638L;

	public MFG_MAssetDisposed (Properties ctx, int A_Asset_Disposed_ID, String trxName)
	{
		super (ctx, A_Asset_Disposed_ID, trxName);
		if (A_Asset_Disposed_ID == 0)
		{
			setProcessed (false);
			setProcessing (false);
		}
		
	}
	//end @win: autocreate asset disposal from ar invoice
	
	public MFG_MAssetDisposed (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}
	
	public MAsset getAsset()
	{
		return MAsset.get(getCtx(), getA_Asset_ID(), null);
	}
	
	
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	processIt
	
	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	
	public boolean unlockIt()
	{
		setProcessing(false);
		return true;
	}	//	unlockIt
	
	
	public boolean invalidateIt()
	{
		return false;
	}	//	invalidateIt
	
	public boolean approveIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("approveIt - " + toString());
		setIsApproved(true);
		return true;
	}	//	approveIt
	
	
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt
	
	
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
		
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		
		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		//
		
		//loading asset
		MAsset asset = getAsset();
		if (log.isLoggable(Level.FINE)) log.fine("asset=" + asset);

		// Activation
		if(!isDisposal())
		{
			String method = getA_Activation_Method();
			if(method.equals(A_ACTIVATION_METHOD_Activation))
			{ // reactivation
				asset.changeStatus(MAsset.A_ASSET_STATUS_Activated, getDateDoc());
			}
			else
			{
				//throw new AssetNotSupportedException(COLUMNNAME_A_Activation_Method, method);
				//Astina 040123
				throw new AdempiereException("@NotSupported@ @"+COLUMNNAME_A_Activation_Method+"@ "+method);
			}
		}
		// Preservation/Partial Retirement/etc
		else
		{
			String method = getA_Disposed_Method();
			if (A_DISPOSED_METHOD_Preservation.equals(method))
			{
				asset.changeStatus(MAsset.A_ASSET_STATUS_Preservation, getDateDoc());
			}
			else if (A_DISPOSED_METHOD_Simple.equals(method)
					|| A_DISPOSED_METHOD_Trade.equals(method)
				)
			{
				asset.changeStatus(MAsset.A_ASSET_STATUS_Disposed, null);
				//Astina comment 020123
				//setA_Disposal_Amt(getA_Asset_Cost());
				//setExpense(getA_Disposal_Amt().subtract(getA_Accumulated_Depr_Delta()));
				setA_Accumulated_Depr_Delta(getA_Accumulated_Depr());
				setExpense(getA_Asset_Cost().subtract(getA_Accumulated_Depr_Delta()));
				createDisposal();
			}
			else if (A_DISPOSED_METHOD_PartialRetirement.equals(method))
			{
				createDisposal();
			}
			else
			{
				//throw new AssetNotSupportedException(COLUMNNAME_A_Disposed_Method, method);
				//Astina 040123
				throw new AdempiereException("@NotSupported@ @"+COLUMNNAME_A_Disposed_Method+"@ "+method);
			}
		}
		
		asset.saveEx(get_TrxName());
		

		//	User Validation
		valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		
		// Done
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	public boolean closeIt()
	{
		setDocAction(DOCACTION_None);
		return true;
	}	//	closeIt
	
	public String getSummary()
	{
		return new StringBuilder()
				.append(getDocumentNo()).append("/").append(getDateDoc())
				.toString();
	}

	
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
	
	public int getDoc_User_ID()
	{
		return getCreatedBy();
	}

	
	public BigDecimal getApprovalAmt()
	{
		return Env.ZERO;
	} 
	
	
	public int getC_Currency_ID()
	{
		return MClient.get(getCtx(), getAD_Client_ID()).getAcctSchema().getC_Currency_ID();
	}
	
	public File createPDF ()
	{
		return null;
	}	//	createPDF
	
	/**
	 * Check if this is a disposal (if the asset is not disposed)
	 * @return true if is disposal
	 */
	public boolean isDisposal()
	{
		return !isDisposed();
	}
	
	public static void setA_Disposal_Amt(I_A_Asset_Disposed bean)
	{
		int precision = 2;
		BigDecimal A_Asset_Cost = bean.getA_Asset_Cost();
		BigDecimal A_Disposal_Amt = bean.getA_Disposal_Amt();
		BigDecimal coef = Env.ZERO;
		if (A_Asset_Cost.signum() != 0)
		{
			coef = A_Disposal_Amt.divide(A_Asset_Cost, 12, RoundingMode.HALF_UP);
		}
		//
		BigDecimal A_Accumulated_Depr = bean.getA_Accumulated_Depr();
		BigDecimal A_Accumulated_Depr_Delta = A_Accumulated_Depr.multiply(coef).setScale(precision, RoundingMode.HALF_UP);
		BigDecimal Expense = A_Disposal_Amt.subtract(A_Accumulated_Depr_Delta);
		//
		bean.setA_Accumulated_Depr_Delta(A_Accumulated_Depr_Delta);
		bean.setExpense(Expense);
	}
	
	private void createDisposal()
	{
		for (MDepreciationWorkfile assetwk :  MDepreciationWorkfile.forA_Asset_ID(getCtx(), getA_Asset_ID(), get_TrxName()))
		{
			BigDecimal disposalAmt = Env.ZERO;
			BigDecimal accumDeprAmt = Env.ZERO;
			if (assetwk.getC_AcctSchema().getC_Currency_ID() != getC_Currency_ID()) 
			{
				disposalAmt  =  assetwk.getA_Asset_Cost();
				accumDeprAmt = assetwk.getA_Accumulated_Depr();
			} else
			{
				disposalAmt = getA_Disposal_Amt();
				accumDeprAmt = getA_Accumulated_Depr_Delta();
			}			
			
			MAssetChange change = new MAssetChange (getCtx(), 0, get_TrxName());
			change.setAD_Org_ID(getAD_Org_ID()); 
			change.setA_Asset_ID(getA_Asset_ID());
			change.setChangeType(MAssetChange.CHANGETYPE_Disposal);
			change.setTextDetails(MRefList.getListDescription (getCtx(),"A_Update_Type" , MAssetChange.CHANGETYPE_Disposal));
			change.setPostingType(assetwk.getPostingType());
			change.setAssetValueAmt(disposalAmt);
			change.setAssetBookValueAmt(assetwk.getA_Asset_Remaining());
			change.setAssetAccumDepreciationAmt(accumDeprAmt);
			change.setA_QTY_Current(assetwk.getA_QTY_Current());
			change.setC_AcctSchema_ID(assetwk.getC_AcctSchema_ID());
			change.setAssetDisposalDate(getA_Disposed_Date());
			change.setIsDisposed(true);
			
			//Astina 200922
			I_A_Asset_Change_Mining ac = POWrapper.create(change, I_A_Asset_Change_Mining.class);
			I_A_Asset_Disposed_Mining ad = POWrapper.create(this, I_A_Asset_Disposed_Mining.class);
			ac.setC_Charge_ID(ad.getC_Charge_ID());
			ac.setA_Sold_Amt(ad.getA_Sold_Amt());
			ac.setA_Sold_Profit_Amt(ad.getA_Sold_Profit_Amt());
			
			change.saveEx(get_TrxName());
			
			assetwk.adjustCost(disposalAmt.negate(), Env.ZERO, false);
			assetwk.adjustAccumulatedDepr(accumDeprAmt.negate(), accumDeprAmt.negate(), false);
			assetwk.saveEx();
			assetwk.buildDepreciation();
		}
		//
		// Delete not processed expense entries
		List<MDepreciationExp> list = MDepreciationExp.getNotProcessedEntries(getCtx(), getA_Asset_ID(), getPostingType(), get_TrxName());
		for (MDepreciationExp ex : list)
		{
			ex.deleteEx(false);
		}	
	}
}
