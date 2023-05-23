package org.astina.mfg.plugin.factory;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.astina.mfg.plugin.model.MAssetLocation;
import org.astina.mfg.plugin.model.MAssetModel;
import org.astina.mfg.plugin.model.MFG_MAllocationHdr;
import org.astina.mfg.plugin.model.MFG_MAsset;
import org.astina.mfg.plugin.model.MFG_MAssetDisposed;
import org.astina.mfg.plugin.model.MFG_MBankStatementLine;
import org.astina.mfg.plugin.model.MFG_MBankTransfer;
import org.astina.mfg.plugin.model.MFG_MCost;
import org.astina.mfg.plugin.model.MFG_MCostDetail;
import org.astina.mfg.plugin.model.MFG_MCostQueue;
import org.astina.mfg.plugin.model.MFG_MElementValue;
import org.astina.mfg.plugin.model.MFG_MInOut;
import org.astina.mfg.plugin.model.MFG_MInOutLine;
import org.astina.mfg.plugin.model.MFG_MInventory;
import org.astina.mfg.plugin.model.MFG_MInventoryLineMA;
import org.astina.mfg.plugin.model.MFG_MInvoice;
import org.astina.mfg.plugin.model.MFG_MInvoiceLine;
import org.astina.mfg.plugin.model.MFG_MInvoiceTax;
import org.astina.mfg.plugin.model.MFG_MMovement;
import org.astina.mfg.plugin.model.MFG_MMovementConfirm;
import org.astina.mfg.plugin.model.MFG_MMovementLine;
import org.astina.mfg.plugin.model.MFG_MOrder;
import org.astina.mfg.plugin.model.MFG_MOrderLine;
import org.astina.mfg.plugin.model.MFG_MPackageMPS;
import org.astina.mfg.plugin.model.MFG_MPaySelectionCheck;
import org.astina.mfg.plugin.model.MFG_MPayment;
import org.astina.mfg.plugin.model.MFG_MPaymentAllocate;
import org.astina.mfg.plugin.model.MFG_MRequisition;
import org.astina.mfg.plugin.model.MFG_MRequisitionLine;
import org.astina.mfg.plugin.model.MJobCode;
import org.astina.mfg.plugin.model.MPRRoute;
import org.astina.mfg.plugin.model.MPaymentLine;
import org.astina.mfg.plugin.model.MPolybox;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class AstinaMfgModelFactory implements IModelFactory{

	@Override
	public Class<?> getClass(String tableName) {
		
		if (tableName.equalsIgnoreCase(MFG_MElementValue.Table_Name))
			return MFG_MElementValue.class;
		
		if (tableName.equalsIgnoreCase(MFG_MRequisition.Table_Name))
			return MFG_MRequisition.class;
		
		if (tableName.equalsIgnoreCase(MFG_MRequisitionLine.Table_Name))
			return MFG_MRequisitionLine.class;
		
		if (tableName.equalsIgnoreCase(MPRRoute.Table_Name))
			return MPRRoute.class;
		
		if (tableName.equalsIgnoreCase(MFG_MOrder.Table_Name))
			return MFG_MOrder.class;
		
		if (tableName.equalsIgnoreCase(MFG_MOrderLine.Table_Name))
			return MFG_MOrderLine.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInvoice.Table_Name))
			return MFG_MInvoice.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceLine.Table_Name))
			return MFG_MInvoiceLine.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceTax.Table_Name))
			return MFG_MInvoiceTax.class;
		
		if (tableName.equalsIgnoreCase(MFG_MPayment.Table_Name))
			return MFG_MPayment.class;
		
		if (tableName.equalsIgnoreCase(MFG_MPaymentAllocate.Table_Name))
			return MFG_MPaymentAllocate.class;
		
		if (tableName.equalsIgnoreCase(MFG_MPaySelectionCheck.Table_Name))
			return MFG_MPaySelectionCheck.class;
		
		if (tableName.equalsIgnoreCase(MFG_MAllocationHdr.Table_Name))
			return MFG_MAllocationHdr.class;
		
		if (tableName.equalsIgnoreCase(MFG_MBankStatementLine.Table_Name))
			return MFG_MBankStatementLine.class;
		
		if (tableName.equalsIgnoreCase(MFG_MBankTransfer.Table_Name))
			return MFG_MBankTransfer.class;
		
		if (tableName.equalsIgnoreCase(MFG_MAsset.Table_Name))
			return MFG_MAsset.class;
		
		if (tableName.equalsIgnoreCase(MFG_MAssetDisposed.Table_Name))
			return MFG_MAssetDisposed.class;
		
		if (tableName.equalsIgnoreCase(MAssetLocation.Table_Name))
			return MAssetLocation.class;
		
		if (tableName.equalsIgnoreCase(MAssetModel.Table_Name))
			return MAssetModel.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInventory.Table_Name))
			return MFG_MInventory.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInventoryLineMA.Table_Name))
			return MFG_MInventoryLineMA.class;
		
		if (tableName.equalsIgnoreCase(MFG_MMovement.Table_Name))
			return MFG_MMovement.class;
		
		if (tableName.equalsIgnoreCase(MFG_MMovementLine.Table_Name))
			return MFG_MMovement.class;
		
		if (tableName.equalsIgnoreCase(MFG_MMovementConfirm.Table_Name))
			return MFG_MMovementConfirm.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInOut.Table_Name))
			return MFG_MInOut.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInOutLine.Table_Name))
			return MFG_MInOutLine.class;
		
		if (tableName.equalsIgnoreCase(MFG_MInOutLine.Table_Name))
			return MFG_MInOutLine.class;
		
		if (tableName.equalsIgnoreCase(MFG_MCost.Table_Name))
			return MFG_MCost.class;
		
		if (tableName.equalsIgnoreCase(MFG_MCostDetail.Table_Name))
			return MFG_MCostDetail.class;
		
		if (tableName.equalsIgnoreCase(MFG_MCostQueue.Table_Name))
			return MFG_MCostQueue.class;
		
		if (tableName.equalsIgnoreCase(MPolybox.Table_Name))
			return MPolybox.class;
		
		if (tableName.equalsIgnoreCase(MFG_MPackageMPS.Table_Name))
			return MFG_MPackageMPS.class;
		
		if (tableName.equalsIgnoreCase(MJobCode.Table_Name))
			return MJobCode.class;
		
		if (tableName.equalsIgnoreCase(MPaymentLine.Table_Name))
			return MPaymentLine.class;
		
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		
		if (tableName.equalsIgnoreCase(MFG_MElementValue.Table_Name))
			return new MFG_MElementValue(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MRequisition.Table_Name))
			return new MFG_MRequisition(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MRequisitionLine.Table_Name))
			return new MFG_MRequisitionLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MPRRoute.Table_Name))
			return new MPRRoute(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MOrder.Table_Name))
			return new MFG_MOrder(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MOrderLine.Table_Name))
			return new MFG_MOrderLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoice.Table_Name))
			return new MFG_MInvoice(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceLine.Table_Name))
			return new MFG_MInvoiceLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceTax.Table_Name))
			return new MFG_MInvoiceTax(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPayment.Table_Name))
			return new MFG_MPayment(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPaymentAllocate.Table_Name))
			return new MFG_MPaymentAllocate(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPaySelectionCheck.Table_Name))
			return new MFG_MPaySelectionCheck(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAllocationHdr.Table_Name))
			return new MFG_MPaySelectionCheck(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MBankStatementLine.Table_Name))
			return new MFG_MBankStatementLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MBankTransfer.Table_Name))
			return new MFG_MBankTransfer(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAsset.Table_Name))
			return new MFG_MAsset(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAssetDisposed.Table_Name))
			return new MFG_MAssetDisposed(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MAssetLocation.Table_Name))
			return new MAssetLocation(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MAssetModel.Table_Name))
			return new MAssetModel(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInventory.Table_Name))
			return new MFG_MInventory(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInventoryLineMA.Table_Name))
			return new MFG_MInventoryLineMA(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovement.Table_Name))
			return new MFG_MMovement(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovementLine.Table_Name))
			return new MFG_MMovementLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovementConfirm.Table_Name))
			return new MFG_MMovementConfirm(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInOut.Table_Name))
			return new MFG_MInOut(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInOutLine.Table_Name))
			return new MFG_MInOutLine(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCost.Table_Name))
			return new MFG_MCost(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCostDetail.Table_Name))
			return new MFG_MCostDetail(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCostQueue.Table_Name))
			return new MFG_MCostQueue(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MPolybox.Table_Name))
			return new MPolybox(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPackageMPS.Table_Name))
			return new MFG_MPackageMPS(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MJobCode.Table_Name))
			return new MJobCode(Env.getCtx(), Record_ID, trxName);
		
		if (tableName.equalsIgnoreCase(MPaymentLine.Table_Name))
			return new MPaymentLine(Env.getCtx(), Record_ID, trxName);
		
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		
		if (tableName.equalsIgnoreCase(MFG_MElementValue.Table_Name))
			return new MFG_MElementValue(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MRequisition.Table_Name))
			return new MFG_MRequisition(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MRequisitionLine.Table_Name))
			return new MFG_MRequisitionLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MPRRoute.Table_Name))
			return new MPRRoute(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MOrder.Table_Name))
			return new MFG_MOrder(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MOrderLine.Table_Name))
			return new MFG_MOrderLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoice.Table_Name))
			return new MFG_MInvoice(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceLine.Table_Name))
			return new MFG_MInvoiceLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInvoiceTax.Table_Name))
			return new MFG_MInvoiceTax(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPayment.Table_Name))
			return new MFG_MPayment(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPaymentAllocate.Table_Name))
			return new MFG_MPaymentAllocate(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPaySelectionCheck.Table_Name))
			return new MFG_MPaySelectionCheck(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAllocationHdr.Table_Name))
			return new MFG_MPaySelectionCheck(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MBankStatementLine.Table_Name))
			return new MFG_MBankStatementLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MBankTransfer.Table_Name))
			return new MFG_MBankTransfer(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAsset.Table_Name))
			return new MFG_MAsset(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MAssetDisposed.Table_Name))
			return new MFG_MAssetDisposed(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MAssetLocation.Table_Name))
			return new MAssetLocation(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MAssetModel.Table_Name))
			return new MAssetModel(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInventory.Table_Name))
			return new MFG_MInventory(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovement.Table_Name))
			return new MFG_MMovement(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovementLine.Table_Name))
			return new MFG_MMovementLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MMovementConfirm.Table_Name))
			return new MFG_MMovementConfirm(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInventoryLineMA.Table_Name))
			return new MFG_MInventoryLineMA(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInOut.Table_Name))
			return new MFG_MInOut(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MInOutLine.Table_Name))
			return new MFG_MInOutLine(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCost.Table_Name))
			return new MFG_MCost(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCostDetail.Table_Name))
			return new MFG_MCostDetail(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MCostQueue.Table_Name))
			return new MFG_MCostQueue(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MPolybox.Table_Name))
			return new MPolybox(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MFG_MPackageMPS.Table_Name))
			return new MFG_MPackageMPS(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MJobCode.Table_Name))
			return new MJobCode(Env.getCtx(), rs, trxName);
		
		if (tableName.equalsIgnoreCase(MPaymentLine.Table_Name))
			return new MPaymentLine(Env.getCtx(), rs, trxName);
		
		return null;
	}

}
