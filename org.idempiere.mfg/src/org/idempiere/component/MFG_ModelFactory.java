// 
// Decompiled by Procyon v0.5.36
// 

package org.idempiere.component;

import java.sql.ResultSet;
import org.compiere.model.MOrderLine;
import org.compiere.util.Env;
import org.compiere.model.PO;
import org.libero.model.LiberoMovementLine;
import org.compiere.model.MOrder;
import org.eevolution.model.MDDOrderLine;
import org.eevolution.model.MDDOrder;
import org.eevolution.model.MPPProductPlanning;
import org.eevolution.model.MPPProductBOMLine;
import org.eevolution.model.MPPProductBOM;
import org.libero.model.MQMSpecificationLine;
import org.libero.model.MQMSpecification;
import org.libero.model.MPPWFNodeProduct;
import org.libero.model.MPPWFNodeAsset;
import org.libero.model.MPPOrderWorkflow;
import org.libero.model.MPPOrderNodeProduct;
import org.libero.model.MPPOrderNodeNext;
import org.libero.model.MPPOrderNodeAsset;
import org.libero.model.MPPOrderNode;
import org.libero.model.MPPOrderBOMLine;
import org.libero.model.MPPOrderCost;
import org.libero.model.MPPOrderBOM;
import org.libero.model.MPPOrder;
import org.libero.model.MPPMRP;
import org.libero.model.MPPCostCollectorMA;
import org.libero.model.MPPCostCollector;
import org.libero.model.MDDNetworkDistributionLine;
import org.libero.model.MDDNetworkDistribution;
import org.adempiere.base.IModelFactory;

public class MFG_ModelFactory implements IModelFactory
{
    public Class<?> getClass(final String tableName) {
        if (tableName.equals("DD_NetworkDistribution")) {
            return MDDNetworkDistribution.class;
        }
        if (tableName.equals("DD_NetworkDistributionLine")) {
            return MDDNetworkDistributionLine.class;
        }
        if (tableName.equals("PP_Cost_Collector")) {
            return MPPCostCollector.class;
        }
        if (tableName.equals("PP_Cost_CollectorMA")) {
            return MPPCostCollectorMA.class;
        }
        if (tableName.equals("PP_MRP")) {
            return MPPMRP.class;
        }
        if (tableName.equals("PP_Order")) {
            return MPPOrder.class;
        }
        if (tableName.equals("PP_Order_BOM")) {
            return MPPOrderBOM.class;
        }
        if (tableName.equals("PP_Order_Cost")) {
            return MPPOrderCost.class;
        }
        if (tableName.equals("PP_Order_BOMLine")) {
            return MPPOrderBOMLine.class;
        }
        if (tableName.equals("PP_Order_Node")) {
            return MPPOrderNode.class;
        }
        if (tableName.equals("PP_Order_Node_Asset")) {
            return MPPOrderNodeAsset.class;
        }
        if (tableName.equals("PP_Order_NodeNext")) {
            return MPPOrderNodeNext.class;
        }
        if (tableName.equals("PP_Order_Node_Product")) {
            return MPPOrderNodeProduct.class;
        }
        if (tableName.equals("PP_Order_Workflow")) {
            return MPPOrderWorkflow.class;
        }
        if (tableName.equals("PP_WF_Node_Asset")) {
            return MPPWFNodeAsset.class;
        }
        if (tableName.equals("PP_WF_Node_Product")) {
            return MPPWFNodeProduct.class;
        }
        if (tableName.equals("QM_Specification")) {
            return MQMSpecification.class;
        }
        if (tableName.equals("QM_SpecificationLine")) {
            return MQMSpecificationLine.class;
        }
        if (tableName.equals("PP_Product_BOM")) {
            return MPPProductBOM.class;
        }
        if (tableName.equals("PP_Product_BOMLine")) {
            return MPPProductBOMLine.class;
        }
        if (tableName.equals("PP_Product_Planning")) {
            return MPPProductPlanning.class;
        }
        if (tableName.equals("DD_Order")) {
            return MDDOrder.class;
        }
        if (tableName.equals("DD_OrderLine")) {
            return MDDOrderLine.class;
        }
        if (tableName.equals("C_Order")) {
            return MOrder.class;
        }
        if (tableName.equals("M_MovementLine")) {
            return LiberoMovementLine.class;
        }
        return null;
    }
    
    public PO getPO(final String tableName, final int Record_ID, final String trxName) {
        if (tableName.equals("DD_NetworkDistribution")) {
            return new MDDNetworkDistribution(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("DD_NetworkDistributionLine")) {
            return new MDDNetworkDistributionLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Cost_Collector")) {
            return new MPPCostCollector(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Cost_CollectorMA")) {
            return new MPPCostCollectorMA(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_MRP")) {
            return new MPPMRP(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order")) {
            return (PO)new MPPOrder(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_BOM")) {
            return new MPPOrderBOM(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_BOMLine")) {
            return new MPPOrderBOMLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_Cost")) {
            return new MPPOrderCost(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_Node")) {
            return new MPPOrderNode(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_Node_Asset")) {
            return new MPPOrderNodeAsset(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_NodeNext")) {
            return new MPPOrderNodeNext(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_Node_Product")) {
            return new MPPOrderNodeProduct(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Order_Workflow")) {
            return new MPPOrderWorkflow(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_WF_Node_Asset")) {
            return new MPPWFNodeAsset(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_WF_Node_Product")) {
            return new MPPWFNodeProduct(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("QM_Specification")) {
            return new MQMSpecification(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("QM_SpecificationLine")) {
            return new MQMSpecificationLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Product_BOM")) {
            return (PO)new MPPProductBOM(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Product_BOMLine")) {
            return (PO)new MPPProductBOMLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("DD_Order")) {
            return (PO)new MDDOrder(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("DD_OrderLine")) {
            return (PO)new MDDOrderLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("PP_Product_Planning")) {
            return (PO)new MPPProductPlanning(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("C_OrderLine")) {
            return (PO)new MOrderLine(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("C_Order")) {
            return (PO)new MOrder(Env.getCtx(), Record_ID, trxName);
        }
        if (tableName.equals("M_MovementLine")) {
            return (PO)new LiberoMovementLine(Env.getCtx(), Record_ID, trxName);
        }
        return null;
    }
    
    public PO getPO(final String tableName, final ResultSet rs, final String trxName) {
        if (tableName.equals("DD_NetworkDistribution")) {
            return new MDDNetworkDistribution(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("DD_NetworkDistributionLine")) {
            return new MDDNetworkDistributionLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Cost_Collector")) {
            return new MPPCostCollector(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Cost_CollectorMA")) {
            return new MPPCostCollectorMA(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_MRP")) {
            return new MPPMRP(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order")) {
            return (PO)new MPPOrder(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_BOM")) {
            return new MPPOrderBOM(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_BOMLine")) {
            return new MPPOrderBOMLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_Cost")) {
            return new MPPOrderCost(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_Node")) {
            return new MPPOrderNode(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_Node_Asset")) {
            return new MPPOrderNodeAsset(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_NodeNext")) {
            return new MPPOrderNodeNext(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_Node_Product")) {
            return new MPPOrderNodeProduct(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Order_Workflow")) {
            return new MPPOrderWorkflow(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_WF_Node_Asset")) {
            return new MPPWFNodeAsset(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_WF_Node_Product")) {
            return new MPPWFNodeProduct(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("QM_Specification")) {
            return new MQMSpecification(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("QM_SpecificationLine")) {
            return new MQMSpecificationLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("QM_SpecificationLine")) {
            return new MQMSpecificationLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Product_BOM")) {
            return (PO)new MPPProductBOM(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Product_BOMLine")) {
            return (PO)new MPPProductBOMLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("DD_Order")) {
            return (PO)new MDDOrder(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("DD_OrderLine")) {
            return (PO)new MDDOrderLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("PP_Product_Planning")) {
            return (PO)new MPPProductPlanning(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("C_OrderLine")) {
            return (PO)new MOrderLine(Env.getCtx(), rs, trxName);
        }
        if (tableName.equals("C_Order")) {
            return (PO)new MOrder(Env.getCtx(), rs, trxName);
        }
        return null;
    }
}
