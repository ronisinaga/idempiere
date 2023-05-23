// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.io.File;
import org.compiere.util.Util;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import org.adempiere.exceptions.DBException;
import java.util.Calendar;
import java.math.RoundingMode;
import org.eevolution.model.I_PP_Product_Planning;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MLocator;
import org.eevolution.model.MDDOrderLine;
import org.compiere.model.MInOutLine;
import org.compiere.model.MDocType;
import org.compiere.model.X_M_Forecast;
import org.eevolution.model.MDDOrder;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.compiere.model.MResourceAssignment;
import org.compiere.model.MRequisition;
import org.compiere.model.MOrder;
import java.util.Collection;
import org.compiere.model.PO;
import org.compiere.util.TimeUtil;
import org.compiere.model.I_S_Resource;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.MResource;
import java.sql.Timestamp;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.model.MRefList;
import org.compiere.model.MOrderLine;
import org.adempiere.exceptions.AdempiereException;
import org.eevolution.model.MPPProductPlanning;
import org.libero.exceptions.NoPlantForWarehouseException;
import org.compiere.wf.MWorkflow;
import org.compiere.model.Query;
import org.eevolution.model.MPPProductBOM;
import org.compiere.util.DB;
import org.compiere.model.MProduct;
import java.math.BigDecimal;
import org.compiere.model.MForecastLine;
import java.util.HashMap;
import org.compiere.util.CLogger;
import org.compiere.process.DocAction;
import org.libero.tables.X_PP_MRP;

public class MPPMRP extends X_PP_MRP implements DocAction
{
    private static final long serialVersionUID = 6831223361306903297L;
    private static CLogger s_log;
    public static int C_Order_ID;
    public static int C_OrderLine_ID;
    private static HashMap<String, String[]> s_sourceColumnNames;
    
    static {
        MPPMRP.s_log = CLogger.getCLogger((Class)MPPMRP.class);
        MPPMRP.C_Order_ID = 0;
        MPPMRP.C_OrderLine_ID = 0;
        (MPPMRP.s_sourceColumnNames = new HashMap<String, String[]>()).put("C_Order", new String[] { "DatePromised", "DocStatus" });
        MPPMRP.s_sourceColumnNames.put("C_OrderLine", new String[] { "AD_Org_ID", "DateOrdered", "DatePromised", "C_BPartner_ID", "M_Warehouse_ID", "M_Product_ID", "C_UOM_ID", "QtyOrdered", "QtyDelivered" });
        MPPMRP.s_sourceColumnNames.put("M_Requisition", new String[] { "DateRequired", "M_Warehouse_ID" });
        MPPMRP.s_sourceColumnNames.put("M_RequisitionLine", new String[] { "AD_Org_ID", "M_Product_ID", "Qty", "C_OrderLine_ID" });
        MPPMRP.s_sourceColumnNames.put("M_Forecast", new String[0]);
        MPPMRP.s_sourceColumnNames.put("M_ForecastLine", new String[] { "AD_Org_ID", "DatePromised", "M_Warehouse_ID", "M_Product_ID", "Qty" });
        MPPMRP.s_sourceColumnNames.put("DD_Order", new String[] { "DocStatus", "C_BPartner_ID" });
        MPPMRP.s_sourceColumnNames.put("DD_OrderLine", new String[] { "AD_Org_ID", "M_Product_ID", "C_UOM_ID", "DatePromised", "QtyOrdered", "QtyDelivered", "ConfirmedQty", "M_Locator_ID", "M_LocatorTo_ID", "ConfirmedQty" });
        MPPMRP.s_sourceColumnNames.put("PP_Order", new String[] { "AD_Org_ID", "M_Product_ID", "C_UOM_ID", "DatePromised", "QtyOrdered", "QtyDelivered", "PP_Product_BOM_ID", "AD_Workflow_ID", "DocStatus" });
        MPPMRP.s_sourceColumnNames.put("PP_Order_BOMLine", new String[] { "M_Product_ID", "C_UOM_ID", "M_Warehouse_ID", "QtyEntered", "QtyDelivered" });
    }
    
    public static int createMOFromForecast(final MForecastLine ol, final BigDecimal qty) {
        final MProduct product = MProduct.get(ol.getCtx(), ol.getM_Product_ID());
        final MPPMRP mrp = new MPPMRP(ol.getCtx(), DB.getSQLValue(ol.get_TrxName(), "select pp_mrp_id from pp_mrp where m_forecastline_id = " + ol.getM_ForecastLine_ID()), ol.get_TrxName());
        if (!product.isBOM()) {
            return 0;
        }
        MPPProductBOM bom = (MPPProductBOM)new Query(ol.getCtx(), "PP_Product_BOM", "BOMType IN (?,?) AND BOMUse=? AND Value=?", ol.get_TrxName()).setClient_ID().setParameters(new Object[] { "O", "K", "M", product.getValue() }).firstOnly();
        MWorkflow workflow = null;
        final int workflow_id = MWorkflow.getWorkflowSearchKey(product);
        if (workflow_id > 0) {
            workflow = MWorkflow.get(ol.getCtx(), workflow_id);
        }
        final int plant_id = ol.get_ValueAsInt("S_Resource_ID");
        System.out.println("afafafa" + plant_id);
        MPPProductPlanning pp = null;
        if (bom == null || workflow == null) {
            if (plant_id <= 0) {
                throw new NoPlantForWarehouseException(ol.getM_Warehouse_ID());
            }
            pp = MPPProductPlanning.find(ol.getCtx(), ol.getAD_Org_ID(), ol.getM_Warehouse_ID(), plant_id, ol.getM_Product_ID(), ol.get_TrxName());
            if (pp == null) {
                throw new AdempiereException("@NotFound@ @PP_Product_Planning_ID@");
            }
        }
        if (bom == null && pp != null) {
            bom = new MPPProductBOM(ol.getCtx(), pp.getPP_Product_BOM_ID(), ol.get_TrxName());
            if (bom != null && !"O".equals(bom.getBOMType()) && !"K".equals(bom.getBOMType())) {
                if ("A".equals(bom.getBOMType())) {
                    return pp.getS_Resource_ID();
                }
                throw new AdempiereException("@NotFound@ @PP_ProductBOM_ID@");
            }
        }
        if (workflow == null && pp != null) {
            workflow = new MWorkflow(ol.getCtx(), pp.getAD_Workflow_ID(), ol.get_TrxName());
            if (workflow == null) {
                throw new AdempiereException("@NotFound@ @AD_Workflow_ID@");
            }
        }
        if (plant_id > 0 && workflow != null) {
            pp = new MPPProductPlanning(ol.getCtx(), 0, ol.get_TrxName());
            pp.setAD_Org_ID(ol.getAD_Org_ID());
            pp.setM_Product_ID(product.getM_Product_ID());
            pp.setPlanner_ID(0);
            pp.setPP_Product_BOM_ID(bom.getPP_Product_BOM_ID());
            pp.setAD_Workflow_ID(workflow.getAD_Workflow_ID());
            pp.setM_Warehouse_ID(ol.getM_Warehouse_ID());
            pp.setS_Resource_ID(plant_id);
            createMOForecast(mrp, pp, ol.getM_ForecastLine_ID(), 0, qty, ol.getDatePromised(), ol.getDatePromised(), "");
        }
        return 0;
    }
    
    public static int createMOMakeTo(final MPPMRP mrp, final MOrderLine ol, final BigDecimal qty) {
        final MProduct product = MProduct.get(ol.getCtx(), ol.getM_Product_ID());
        if (!product.isBOM()) {
            return 0;
        }
        MPPProductBOM bom = (MPPProductBOM)new Query(ol.getCtx(), "PP_Product_BOM", "BOMType IN (?,?) AND BOMUse=? AND Value=?", ol.get_TrxName()).setClient_ID().setParameters(new Object[] { "O", "K", "M", product.getValue() }).firstOnly();
        MWorkflow workflow = null;
        final int workflow_id = MWorkflow.getWorkflowSearchKey(product);
        if (workflow_id > 0) {
            workflow = MWorkflow.get(ol.getCtx(), workflow_id);
        }
        final int plant_id = MPPProductPlanning.getPlantForWarehouse(ol.getM_Warehouse_ID());
        MPPProductPlanning pp = null;
        if (bom == null || workflow == null) {
            if (plant_id <= 0) {
                throw new NoPlantForWarehouseException(ol.getM_Warehouse_ID());
            }
            pp = MPPProductPlanning.find(ol.getCtx(), ol.getAD_Org_ID(), ol.getM_Warehouse_ID(), plant_id, ol.getM_Product_ID(), ol.get_TrxName());
            if (pp == null) {
                throw new AdempiereException("@NotFound@ @PP_Product_Planning_ID@");
            }
        }
        if (bom == null && pp != null) {
            bom = new MPPProductBOM(ol.getCtx(), pp.getPP_Product_BOM_ID(), ol.get_TrxName());
            if (bom != null && !"O".equals(bom.getBOMType()) && !"K".equals(bom.getBOMType())) {
                if ("A".equals(bom.getBOMType())) {
                    return pp.getS_Resource_ID();
                }
                throw new AdempiereException("@NotFound@ @PP_ProductBOM_ID@");
            }
        }
        MPPOrder order = MPPOrder.forC_OrderLine_ID(ol.getCtx(), ol.get_ID(), ol.get_TrxName());
        if (workflow == null && pp != null) {
            workflow = new MWorkflow(ol.getCtx(), pp.getAD_Workflow_ID(), ol.get_TrxName());
            if (workflow == null) {
                throw new AdempiereException("@NotFound@ @AD_Workflow_ID@");
            }
        }
        if (order == null) {
            if (plant_id > 0 && workflow != null) {
                String description = String.valueOf(Msg.translate(ol.getCtx(), MRefList.getListName(ol.getCtx(), 347, bom.getBOMType()))) + " " + Msg.translate(ol.getCtx(), "C_Order_ID") + " : " + ol.getParent().getDocumentNo();
                pp = new MPPProductPlanning(ol.getCtx(), 0, ol.get_TrxName());
                pp.setAD_Org_ID(ol.getAD_Org_ID());
                pp.setM_Product_ID(product.getM_Product_ID());
                pp.setPlanner_ID(ol.getParent().getSalesRep_ID());
                pp.setPP_Product_BOM_ID(bom.getPP_Product_BOM_ID());
                pp.setAD_Workflow_ID(workflow.getAD_Workflow_ID());
                pp.setM_Warehouse_ID(ol.getM_Warehouse_ID());
                pp.setS_Resource_ID(plant_id);
                order = createMO(mrp, pp, ol.getC_OrderLine_ID(), ol.getM_AttributeSetInstance_ID(), qty, ol.getDateOrdered(), ol.getDatePromised(), description);
                description = "";
                if (ol.getDescription() != null) {
                    description = ol.getDescription();
                }
                description = String.valueOf(description) + " " + Msg.translate(ol.getCtx(), MRefList.getListName(ol.getCtx(), 347, bom.getBOMType())) + " " + Msg.translate(ol.getCtx(), "PP_Order_ID") + " : " + order.getDocumentNo();
                ol.setDescription(description);
                ol.saveEx();
            }
        }
        else {
            boolean isNoEdit = false;
            isNoEdit = ("CO".equals(order.getDocStatus()) || "CL".equals(order.getDocStatus()));
            if (!order.isProcessed() && !isNoEdit) {
                if (order.getM_Product_ID() != ol.getM_Product_ID()) {
                    order.setDescription("");
                    order.setQtyEntered(Env.ZERO);
                    order.setC_OrderLine_ID(0);
                    order.voidIt();
                    order.setDocStatus("VO");
                    order.setDocAction("--");
                    order.save();
                    ol.setDescription("");
                    ol.saveEx();
                }
                if (order.getQtyEntered().compareTo(ol.getQtyEntered()) != 0) {
                    order.setQty(ol.getQtyEntered());
                    order.saveEx();
                }
                if (order.getDatePromised().compareTo(ol.getDatePromised()) != 0) {
                    order.setDatePromised(ol.getDatePromised());
                    order.saveEx();
                }
            }
        }
        return 0;
    }
    
    public static MPPOrder createMO(final MPPMRP mrp, final MPPProductPlanning pp, final int C_OrderLine_ID, final int M_AttributeSetInstance_ID, final BigDecimal qty, final Timestamp dateOrdered, final Timestamp datePromised, final String description) {
        final MPPProductBOM bom = pp.getPP_Product_BOM();
        final MWorkflow wf = pp.getAD_Workflow();
        if (pp.getS_Resource_ID() > 0 && bom != null && wf != null) {
            final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(pp.getCtx());
            final MPPOrder order = new MPPOrder(pp.getCtx(), 0, pp.get_TrxName());
            order.setAD_Org_ID(pp.getAD_Org_ID());
            order.setDescription(description);
            order.setC_OrderLine_ID(C_OrderLine_ID);
            order.setS_Resource_ID(pp.getS_Resource_ID());
            order.setM_Warehouse_ID(pp.getM_Warehouse_ID());
            order.setM_Product_ID(pp.getM_Product_ID());
            order.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
            order.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
            order.setAD_Workflow_ID(pp.getAD_Workflow_ID());
            order.setPlanner_ID(pp.getPlanner_ID());
            order.setLine(10);
            order.setDateOrdered(dateOrdered);
            order.setDatePromised(datePromised);
            final int duration = routingService.calculateDuration(mrp, (I_AD_Workflow)wf, (I_S_Resource)MResource.get(pp.getCtx(), pp.getS_Resource_ID()), qty, datePromised).intValueExact();
            final Timestamp startTime = routingService.getStartAssignTime();
            order.setDateStartSchedule(startTime);
            order.setDateFinishSchedule(TimeUtil.addMinutess(startTime, duration));
            order.setC_UOM_ID(pp.getM_Product().getC_UOM_ID());
            order.setQty(qty);
            order.setPriorityRule("3");
            order.saveEx();
            order.setDocAction("CO");
            order.saveEx(pp.get_TrxName());
            if (!materialDemandOfMO(mrp)) {
                mrp.setPP_Order_ID(order.get_ID());
            }
            mrp.setDocStatus("IP");
            order.saveEx(pp.get_TrxName());
            return order;
        }
        return null;
    }
    
    public static MPPOrder createMOForecast(final MPPMRP mrp, final MPPProductPlanning pp, final int C_OrderLine_ID, final int M_AttributeSetInstance_ID, final BigDecimal qty, final Timestamp dateOrdered, final Timestamp datePromised, final String description) {
        final MPPProductBOM bom = pp.getPP_Product_BOM();
        final MWorkflow wf = pp.getAD_Workflow();
        if (pp.getS_Resource_ID() <= 0 || bom == null || wf == null) {
            return null;
        }
        final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(pp.getCtx());
        final int idforecast = DB.getSQLValue((String)null, "select coalesce(pp_order_id,0) from pp_order where m_forecastline_id = ?", C_OrderLine_ID);
        if (idforecast > 0) {
            final MPPOrder order = new MPPOrder(pp.getCtx(), idforecast, pp.get_TrxName());
            order.setAD_Org_ID(pp.getAD_Org_ID());
            order.setDescription(description);
            order.set_CustomColumn("M_ForecastLine_ID", (Object)C_OrderLine_ID);
            order.setS_Resource_ID(pp.getS_Resource_ID());
            order.setM_Warehouse_ID(pp.getM_Warehouse_ID());
            order.setM_Product_ID(pp.getM_Product_ID());
            order.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
            order.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
            order.setAD_Workflow_ID(pp.getAD_Workflow_ID());
            order.setPlanner_ID(pp.getPlanner_ID());
            order.setLine(10);
            order.setDateOrdered(dateOrdered);
            order.setDatePromised(datePromised);
            final int duration = routingService.calculateDuration(mrp, (I_AD_Workflow)wf, (I_S_Resource)MResource.get(pp.getCtx(), pp.getS_Resource_ID()), qty, datePromised).intValueExact();
            final Timestamp startTime = routingService.getStartAssignTime();
            order.setDateStartSchedule(startTime);
            order.setDateFinishSchedule(TimeUtil.addMinutess(startTime, duration));
            order.setC_UOM_ID(pp.getM_Product().getC_UOM_ID());
            order.setQty(qty);
            order.setPriorityRule("3");
            order.saveEx();
            order.setDocAction("CO");
            order.saveEx(pp.get_TrxName());
            if (!materialDemandOfMO(mrp)) {
                mrp.setPP_Order_ID(order.get_ID());
            }
            mrp.setDocStatus("IP");
            order.saveEx(pp.get_TrxName());
            return order;
        }
        final MPPOrder order = new MPPOrder(pp.getCtx(), 0, pp.get_TrxName());
        order.setAD_Org_ID(pp.getAD_Org_ID());
        order.setDescription(description);
        order.set_CustomColumn("M_ForecastLine_ID", (Object)C_OrderLine_ID);
        order.setS_Resource_ID(pp.getS_Resource_ID());
        order.setM_Warehouse_ID(pp.getM_Warehouse_ID());
        order.setM_Product_ID(pp.getM_Product_ID());
        order.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
        order.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
        order.setAD_Workflow_ID(pp.getAD_Workflow_ID());
        order.setPlanner_ID(pp.getPlanner_ID());
        order.setLine(10);
        order.setDateOrdered(dateOrdered);
        order.setDatePromised(datePromised);
        final int duration = routingService.calculateDuration(mrp, (I_AD_Workflow)wf, (I_S_Resource)MResource.get(pp.getCtx(), pp.getS_Resource_ID()), qty, datePromised).intValueExact();
        final Timestamp startTime = routingService.getStartAssignTime();
        order.setDateStartSchedule(startTime);
        order.setDateFinishSchedule(TimeUtil.addMinutess(startTime, duration));
        order.setC_UOM_ID(pp.getM_Product().getC_UOM_ID());
        order.setQty(qty);
        order.setPriorityRule("3");
        order.saveEx();
        order.setDocAction("CO");
        order.saveEx(pp.get_TrxName());
        if (!materialDemandOfMO(mrp)) {
            mrp.setPP_Order_ID(order.get_ID());
        }
        mrp.setDocStatus("IP");
        order.saveEx(pp.get_TrxName());
        return order;
    }
    
    public static boolean isChanged(final PO po) {
        final String[] columnNames = MPPMRP.s_sourceColumnNames.get(po.get_TableName());
        if (columnNames == null || columnNames.length == 0) {
            return false;
        }
        if (po.is_new() || po.is_ValueChanged("IsActive")) {
            return true;
        }
        String[] array;
        for (int length = (array = columnNames).length, i = 0; i < length; ++i) {
            final String columnName = array[i];
            if (po.is_ValueChanged(columnName)) {
                return true;
            }
        }
        return false;
    }
    
    public static Collection<String> getSourceTableNames() {
        return MPPMRP.s_sourceColumnNames.keySet();
    }
    
    public static void deleteMRP(final PO po) {
        final String tableName = po.get_TableName();
        if (po instanceof MPPOrder || (po instanceof MOrder && ((MOrder)po).isSOTrx())) {
            String whereClause = String.valueOf(tableName) + "_ID=? AND AD_Client_ID=? AND DocStatus=? AND M_Requisition_ID is not null";
            List<MPPMRP> mrpset = new Query(po.getCtx(), "PP_MRP", whereClause, po.get_TrxName()).setParameters(new Object[] { po.get_ID(), po.getAD_Client_ID(), "DR" }).list();
            for (final MPPMRP mrp : mrpset) {
                final MRequisition req = new MRequisition(po.getCtx(), mrp.getM_Requisition_ID(), po.get_TrxName());
                if (req != null && req.getDocStatus().compareTo("DR") == 0) {
                    req.setDocStatus("VO");
                    req.voidIt();
                }
            }
            mrpset.clear();
            whereClause = String.valueOf(tableName) + "_ID=? AND AD_Client_ID=? AND OrderType = ?";
            mrpset = new Query(po.getCtx(), "PP_MRP", whereClause, po.get_TrxName()).setParameters(new Object[] { po.get_ID(), po.getAD_Client_ID(), "SOO" }).list();
            if (mrpset.size() == 0) {
                whereClause = String.valueOf(whereClause) + " AND " + "TypeMRP" + "=?";
                mrpset = new Query(po.getCtx(), "PP_MRP", whereClause, po.get_TrxName()).setParameters(new Object[] { po.get_ID(), po.getAD_Client_ID(), "MOP", "S" }).list();
            }
            for (final MPPMRP mrp : mrpset) {
                if (po instanceof MPPOrder) {
                    mrp.setPP_Order_ID(0);
                }
                else if (po instanceof MOrder) {
                    mrp.setC_Order_ID(0);
                    mrp.setC_OrderLine_ID(0);
                }
                mrp.saveEx(po.get_TrxName());
                final String m_name = "MRP:" + mrp.get_ID() + '%';
                final List<MResourceAssignment> resourceschedule_list = new Query(po.getCtx(), "S_ResourceAssignment", "AD_Client_ID=? AND Name like ?", (String)null).setParameters(new Object[] { mrp.getAD_Client_ID(), m_name }).list();
                for (final MResourceAssignment ra : resourceschedule_list) {
                    ra.deleteEx(true, po.get_TrxName());
                }
            }
        }
        if (po instanceof MOrderLine) {
            final MOrderLine ol = (MOrderLine)po;
            final MPPOrder order = MPPOrder.forC_OrderLine_ID(ol.getCtx(), ol.get_ID(), ol.get_TrxName());
            if (order != null && !order.isProcessed()) {
                order.deleteEx(true);
            }
        }
        int no = DB.executeUpdateEx("DELETE FROM PP_MRP WHERE " + tableName + "_ID=? AND AD_Client_ID=? AND ( DocStatus=? OR DocStatus=? )", new Object[] { po.get_ID(), po.getAD_Client_ID(), "DR", "VO" }, po.get_TrxName());
        MPPMRP.s_log.finest("Deleted " + tableName + " #" + no);
        if (po instanceof MOrder) {
            final MOrder void_order = (MOrder)po;
            if (void_order.isSOTrx() && void_order.getDocStatus().compareTo("VO") == 0) {
                no = DB.executeUpdateEx("DELETE FROM PP_MRP WHERE " + tableName + "_ID=? AND AD_Client_ID=? AND OrderType = ?", new Object[] { po.get_ID(), po.getAD_Client_ID(), "SOO" }, po.get_TrxName());
                MPPMRP.s_log.finest("Deleted " + tableName + " #" + no);
            }
        }
        else if (po instanceof MPPOrder) {
            no = DB.executeUpdateEx("DELETE FROM PP_MRP WHERE " + tableName + "_ID=? AND AD_Client_ID=? AND DocStatus=? AND Description like ?", new Object[] { po.get_ID(), po.getAD_Client_ID(), "IP", "** Voided Ordered Quantity%" }, po.get_TrxName());
            MPPMRP.s_log.finest("Deleted " + tableName + " #" + no);
        }
    }
    
    private static Query getQuery(final PO po, final String typeMRP, final String orderType) {
        final ArrayList<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer();
        whereClause.append("AD_Client_ID=?");
        params.add(po.getAD_Client_ID());
        whereClause.append(" AND ").append(po.get_TableName()).append("_ID=?");
        params.add(po.get_ID());
        if (typeMRP != null) {
            whereClause.append(" AND ").append("TypeMRP").append("=?");
            params.add(typeMRP);
        }
        if (orderType != null) {
            whereClause.append(" AND ").append("OrderType").append("=?");
            params.add(orderType);
        }
        if (po instanceof MPPOrder && "S".equals(typeMRP)) {
            whereClause.append(" AND ").append("PP_Order_BOMLine_ID").append(" IS NULL");
        }
        return new Query(po.getCtx(), "PP_MRP", whereClause.toString(), po.get_TrxName()).setParameters((List)params);
    }
    
    public MPPMRP(final Properties ctx, final int PP_MRP_ID, final String trxName) {
        super(ctx, PP_MRP_ID, trxName);
        if (PP_MRP_ID == 0) {
            this.setValue("MRP");
            this.setName("MRP");
            this.setDateSimulation(new Timestamp(System.currentTimeMillis()));
            this.setIsAvailable(false);
            if (MPPMRP.C_Order_ID > 0) {
                this.setC_Order_ID(MPPMRP.C_Order_ID);
                this.setC_OrderLine_ID(MPPMRP.C_OrderLine_ID);
                final MPPOrder mo = (MPPOrder)new Query(Env.getCtx(), "PP_Order", "C_OrderLine_ID=?", trxName).setParameters(new Object[] { MPPMRP.C_OrderLine_ID }).first();
                if (mo != null) {
                    this.setPP_Order_ID(mo.getPP_Order_ID());
                }
            }
        }
    }
    
    public MPPMRP(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public void setPP_Order(final MPPOrder o) {
        this.setPP_Order_ID(o.getPP_Order_ID());
        this.setOrderType("MOP");
        this.set_CustomColumn("pp_orderref_id", (Object)o.getPP_Order_ID());
        this.setName(o.getDocumentNo());
        this.setDescription(o.getDescription());
        this.setDatePromised(o.getDatePromised());
        this.setDateOrdered(o.getDateOrdered());
        this.setDateStartSchedule(o.getDateStartSchedule());
        this.setDateFinishSchedule(o.getDateFinishSchedule());
        this.setS_Resource_ID(o.getS_Resource_ID());
        if (o.getDocStatus().compareTo("CL") == 0) {
            this.setDocStatus("CO");
        }
        else if (o.getDocStatus().compareTo("CO") == 0) {
            this.setDocStatus("IP");
        }
        else {
            this.setDocStatus(o.getDocStatus());
        }
    }
    
    public void setC_Order(final MOrder o) {
        this.setC_Order_ID(o.get_ID());
        this.setC_BPartner_ID(o.getC_BPartner_ID());
        this.setDocStatus(o.getDocStatus());
        if (o.isSOTrx()) {
            this.setOrderType("SOO");
            this.setTypeMRP("D");
        }
        else {
            this.setOrderType("POO");
            this.setTypeMRP("S");
        }
    }
    
    public void setDD_Order(final MDDOrder o) {
        this.setDD_Order_ID(o.get_ID());
        this.setC_BPartner_ID(o.getC_BPartner_ID());
        this.setDocStatus(o.getDocStatus());
    }
    
    public void setM_Requisition(final MRequisition r) {
        this.setM_Requisition_ID(r.get_ID());
        this.setOrderType("POR");
        this.setTypeMRP("S");
        this.setDateOrdered(r.getDateDoc());
        this.setDatePromised(r.getDateRequired());
        this.setDateStartSchedule(r.getDateDoc());
        this.setDateFinishSchedule(r.getDateRequired());
        this.setM_Warehouse_ID(r.getM_Warehouse_ID());
    }
    
    public void setM_Forecast(final X_M_Forecast f) {
        this.setOrderType("FCT");
        this.setTypeMRP("D");
        this.setM_Forecast_ID(f.getM_Forecast_ID());
        this.setDescription(f.getDescription());
    }
    
    public boolean isReleased() {
        final String docStatus = this.getDocStatus();
        return docStatus != null && ("IP".equals(docStatus) || "CO".equals(docStatus));
    }
    
    public static void M_Forecast(final X_M_Forecast f) {
        final List<MPPMRP> list = getQuery((PO)f, null, null).list();
        for (final MPPMRP mrp : list) {
            mrp.setM_Forecast(f);
        }
    }
    
    public static void M_ForecastLine(final MForecastLine fl) {
        final String trxName = fl.get_TrxName();
        final Properties ctx = fl.getCtx();
        final X_M_Forecast f = new X_M_Forecast(ctx, fl.getM_Forecast_ID(), trxName);
        MPPMRP mrp = (MPPMRP)getQuery((PO)fl, null, null).firstOnly();
        if (mrp == null) {
            mrp = new MPPMRP(ctx, 0, trxName);
            mrp.setM_ForecastLine_ID(fl.getM_ForecastLine_ID());
        }
        mrp.setM_Forecast(f);
        mrp.setName("MRP");
        mrp.setAD_Org_ID(fl.getAD_Org_ID());
        mrp.setDatePromised(fl.getDatePromised());
        mrp.setDateStartSchedule(fl.getDatePromised());
        mrp.setDateFinishSchedule(fl.getDatePromised());
        mrp.setDateOrdered(fl.getDatePromised());
        mrp.setM_Warehouse_ID(fl.getM_Warehouse_ID());
        mrp.setM_Product_ID(fl.getM_Product_ID());
        mrp.setQty(fl.getQty());
        mrp.setDocStatus("IP");
        mrp.saveEx(trxName);
    }
    
    public static void C_Order(final MOrder o) {
        final MDocType dt = MDocType.get(o.getCtx(), o.getC_DocTypeTarget_ID());
        final String DocSubTypeSO = dt.getDocSubTypeSO();
        if ("SO".equals(DocSubTypeSO) || !o.isSOTrx()) {
            if (o.getDocStatus().equals("IP") || o.getDocStatus().equals("CO") || !o.isSOTrx()) {
                MOrderLine[] lines;
                for (int length = (lines = o.getLines()).length, i = 0; i < length; ++i) {
                    final MOrderLine line = lines[i];
                    C_OrderLine(line);
                }
            }
            if (o.is_ValueChanged("DocStatus") || o.is_ValueChanged("C_BPartner_ID")) {
                final List<MPPMRP> list = getQuery((PO)o, null, null).list();
                for (final MPPMRP mrp : list) {
                    mrp.setC_Order_ID(o.get_ID());
                    mrp.setC_BPartner_ID(o.getC_BPartner_ID());
                    if ((mrp.getDocStatus().compareTo("CO") != 0 && mrp.getDocStatus().compareTo("CL") != 0) || mrp.getQty().compareTo(Env.ZERO) != 0 || mrp.getOrderType().compareTo("SOO") == 0) {
                        if (o.getDocStatus().equals("IP") || o.getDocStatus().equals("CO")) {
                            mrp.setDocStatus("IP");
                        }
                        else {
                            mrp.setDocStatus("DR");
                        }
                    }
                    mrp.saveEx(o.get_TrxName());
                }
            }
        }
    }
    
    public static void C_OrderLine(final MOrderLine ol) {
        boolean isReleased = false;
        final MOrder ord = ol.getParent();
        isReleased = ("IP".equals(ord.getDocStatus()) || "CO".equals(ord.getDocStatus()));
        MPPMRP mrp;
        if (ord.isSOTrx() && isReleased) {
            mrp = (MPPMRP)getQuery((PO)ol, "D", "SOO").firstOnly();
        }
        else {
            mrp = (MPPMRP)getQuery((PO)ol, null, null).firstOnly();
        }
        if (mrp == null) {
            mrp = new MPPMRP(ol.getCtx(), 0, ol.get_TrxName());
        }
        mrp.setAD_Org_ID(ol.getAD_Org_ID());
        mrp.setC_Order(ol.getParent());
        mrp.setC_OrderLine_ID(ol.getC_OrderLine_ID());
        mrp.set_CustomColumn("pp_orderref_id", (Object)ol.get_ValueAsInt("pp_orderref_id"));
        MPPMRP.C_Order_ID = ol.getC_Order_ID();
        MPPMRP.C_OrderLine_ID = ol.getC_OrderLine_ID();
        mrp.setDescription(ol.getDescription());
        mrp.setName("OrderLine");
        mrp.setDatePromised(ol.getDatePromised());
        mrp.setDateOrdered(ol.getDateOrdered());
        mrp.setM_Warehouse_ID(ol.getM_Warehouse_ID());
        mrp.setM_Product_ID(ol.getM_Product_ID());
        mrp.setQty(ol.getQtyOrdered().subtract(ol.getQtyDelivered()));
        mrp.setDocStatus("IP");
        mrp.saveEx(ol.get_TrxName());
        final MOrder o = ol.getParent();
        final MDocType dt = MDocType.get(o.getCtx(), o.getC_DocTypeTarget_ID());
        final String DocSubTypeSO = dt.getDocSubTypeSO();
        if ("SO".equals(DocSubTypeSO)) {
            final int res = createMOMakeTo(mrp, ol, ol.getQtyOrdered());
            mrp.setS_Resource_ID(res);
            mrp.saveEx(ol.get_TrxName());
        }
    }
    
    public static void updateStatusMRPOrder(final MInOutLine outline) {
        MPPMRP mrp = null;
        final MOrderLine orderLine = new MOrderLine(outline.getCtx(), outline.getC_OrderLine_ID(), outline.get_TrxName());
        final MOrder order = new MOrder(outline.getCtx(), orderLine.getC_Order_ID(), outline.get_TrxName());
        if (order != null && orderLine != null && (orderLine.getQtyDelivered().compareTo(Env.ZERO) > 0 || order.getDocStatus().compareTo("CL") == 0)) {
            String docStatus;
            if (order.getDocStatus().compareTo("CL") == 0) {
                docStatus = "CO";
            }
            else if (orderLine.getQtyOrdered().compareTo(orderLine.getQtyDelivered()) <= 0) {
                docStatus = "CO";
            }
            else {
                docStatus = "IP";
            }
            if (order.isSOTrx()) {
                mrp = (MPPMRP)getQuery((PO)orderLine, "D", "SOO").firstOnly();
            }
            else {
                mrp = (MPPMRP)getQuery((PO)orderLine, "S", "POO").firstOnly();
            }
            if (mrp == null) {
                MPPMRP.s_log.finest("MRP SO/PO " + order.getDocumentNo() + " not found");
            }
            else {
                mrp.setDocStatus(docStatus);
                mrp.saveEx(outline.get_TrxName());
            }
        }
    }
    
    public static void PP_Order(final MPPOrder o) {
        final Properties ctx = o.getCtx();
        final String trxName = o.get_TrxName();
        MPPMRP mrpSupply = (MPPMRP)getQuery((PO)o, "S", "MOP").firstOnly();
        if (mrpSupply == null) {
            mrpSupply = new MPPMRP(ctx, 0, trxName);
            mrpSupply.setAD_Org_ID(o.getAD_Org_ID());
            mrpSupply.setTypeMRP("S");
        }
        mrpSupply.setPP_Order(o);
        mrpSupply.setM_Product_ID(o.getM_Product_ID());
        mrpSupply.setM_Warehouse_ID(o.getM_Warehouse_ID());
        mrpSupply.setQty(o.getQtyOrdered().subtract(o.getQtyDelivered()));
        if (MPPMRP.C_Order_ID != 0) {
            final MOrder order = new MOrder(ctx, MPPMRP.C_Order_ID, trxName);
            if (order == null || !order.isSOTrx()) {
                MPPMRP.C_Order_ID = 0;
            }
        }
        if (MPPMRP.C_OrderLine_ID != 0) {
            final MOrderLine ol = new MOrderLine(ctx, MPPMRP.C_OrderLine_ID, trxName);
            if (ol == null || !ol.getC_Order().isSOTrx()) {
                MPPMRP.C_OrderLine_ID = 0;
            }
        }
        mrpSupply.setC_Order_ID(MPPMRP.C_Order_ID);
        mrpSupply.setC_OrderLine_ID(MPPMRP.C_OrderLine_ID);
        mrpSupply.saveEx(trxName);
        final List<MPPMRP> mrpDemandList = getQuery((PO)o, "D", "MOP").list();
        for (final MPPMRP mrpDemand : mrpDemandList) {
            mrpDemand.setPP_Order(o);
            mrpDemand.saveEx(trxName);
        }
    }
    
    public static void PP_Order_BOMLine(final MPPOrderBOMLine obl) {
        final String trxName = obl.get_TrxName();
        final Properties ctx = obl.getCtx();
        String typeMRP = "D";
        BigDecimal qty = obl.getQtyRequired().subtract(obl.getQtyDelivered());
        if (obl.isCoProduct() || obl.isByProduct()) {
            typeMRP = "S";
            qty = qty.negate();
        }
        MPPMRP mrp = (MPPMRP)getQuery(obl, null, "MOP").firstOnly();
        if (mrp == null) {
            mrp = new MPPMRP(ctx, 0, trxName);
            mrp.setPP_Order_BOMLine_ID(obl.getPP_Order_BOMLine_ID());
        }
        if (MPPMRP.C_Order_ID != 0) {
            final MOrder order = new MOrder(ctx, MPPMRP.C_Order_ID, trxName);
            if (order == null || !order.isSOTrx()) {
                MPPMRP.C_Order_ID = 0;
            }
        }
        if (MPPMRP.C_OrderLine_ID != 0) {
            final MOrderLine ol = new MOrderLine(ctx, MPPMRP.C_OrderLine_ID, trxName);
            if (ol == null || !ol.getC_Order().isSOTrx()) {
                MPPMRP.C_OrderLine_ID = 0;
            }
        }
        mrp.setC_Order_ID(MPPMRP.C_Order_ID);
        mrp.setC_OrderLine_ID(MPPMRP.C_OrderLine_ID);
        mrp.setAD_Org_ID(obl.getAD_Org_ID());
        mrp.setTypeMRP(typeMRP);
        mrp.setPP_Order(obl.getParent());
        mrp.setM_Warehouse_ID(obl.getM_Warehouse_ID());
        mrp.setM_Product_ID(obl.getM_Product_ID());
        mrp.setQty(qty);
        mrp.set_CustomColumn("pp_orderref_id", (Object)obl.getParent().getPP_Order_ID());
        mrp.saveEx(trxName);
    }
    
    public static void DD_Order(final MDDOrder o) {
        if ("IP".equals(o.getDocStatus()) || "CO".equals(o.getDocStatus())) {
            MDDOrderLine[] lines;
            for (int length = (lines = o.getLines()).length, i = 0; i < length; ++i) {
                final MDDOrderLine line = lines[i];
                DD_OrderLine(line);
            }
        }
        if (o.is_ValueChanged("DocStatus") || o.is_ValueChanged("C_BPartner_ID")) {
            final List<MPPMRP> list = getQuery((PO)o, null, null).list();
            for (final MPPMRP mrp : list) {
                mrp.setDD_Order(o);
                mrp.saveEx(o.get_TrxName());
            }
        }
    }
    
    public static void DD_OrderLine(final MDDOrderLine ol) {
        final String trxName = ol.get_TrxName();
        final Properties m_ctx = ol.getCtx();
        MPPMRP mrp = (MPPMRP)getQuery((PO)ol, "D", "DOO").firstOnly();
        final MLocator source = MLocator.get(m_ctx, ol.getM_Locator_ID());
        final MLocator target = MLocator.get(m_ctx, ol.getM_LocatorTo_ID());
        if (mrp != null) {
            mrp.setAD_Org_ID(source.getAD_Org_ID());
            mrp.setName("DemandDistOrder");
            mrp.setDescription(ol.getDescription());
            mrp.setDatePromised(ol.getDatePromised());
            mrp.setDateOrdered(ol.getDateOrdered());
            mrp.setM_Warehouse_ID(source.getM_Warehouse_ID());
            mrp.setM_Product_ID(ol.getM_Product_ID());
            mrp.setQty(ol.getQtyOrdered().subtract(ol.getQtyDelivered()));
            mrp.setDocStatus(ol.getParent().getDocStatus());
            mrp.saveEx(trxName);
        }
        else {
            mrp = new MPPMRP(m_ctx, 0, trxName);
            mrp.setAD_Org_ID(source.getAD_Org_ID());
            mrp.setName("NewDemandDistOrder");
            mrp.setDescription(ol.getDescription());
            mrp.setDD_Order_ID(ol.getDD_Order_ID());
            mrp.setDD_OrderLine_ID(ol.getDD_OrderLine_ID());
            mrp.setDatePromised(ol.getDatePromised());
            mrp.setDateOrdered(ol.getDateOrdered());
            mrp.setM_Warehouse_ID(source.getM_Warehouse_ID());
            mrp.setM_Product_ID(ol.getM_Product_ID());
            mrp.setQty(ol.getQtyOrdered().subtract(ol.getQtyDelivered()));
            mrp.setDocStatus(ol.getParent().getDocStatus());
            mrp.setOrderType("DOO");
            mrp.setTypeMRP("D");
            mrp.saveEx(trxName);
        }
        mrp = (MPPMRP)getQuery((PO)ol, "S", "DOO").firstOnly();
        if (mrp != null) {
            mrp.setAD_Org_ID(target.getAD_Org_ID());
            mrp.setName("SupplyDistOrder");
            mrp.setDescription(ol.getDescription());
            mrp.setDatePromised(ol.getDatePromised());
            mrp.setDateOrdered(ol.getDateOrdered());
            mrp.setM_Product_ID(ol.getM_Product_ID());
            mrp.setM_Warehouse_ID(target.getM_Warehouse_ID());
            mrp.setQty(ol.getQtyOrdered().subtract(ol.getQtyDelivered()));
            mrp.setDocStatus(ol.getParent().getDocStatus());
            mrp.saveEx(trxName);
        }
        else {
            mrp = new MPPMRP(m_ctx, 0, trxName);
            mrp.setAD_Org_ID(target.getAD_Org_ID());
            mrp.setName("NewSupplyDistOrder");
            mrp.setDescription(ol.getDescription());
            mrp.setDD_Order_ID(ol.getDD_Order_ID());
            mrp.setDD_OrderLine_ID(ol.getDD_OrderLine_ID());
            mrp.setDatePromised(ol.getDatePromised());
            mrp.setDateOrdered(ol.getDateOrdered());
            mrp.setM_Product_ID(ol.getM_Product_ID());
            mrp.setM_Warehouse_ID(target.getM_Warehouse_ID());
            mrp.setQty(ol.getQtyOrdered().subtract(ol.getQtyDelivered()));
            mrp.setDocStatus(ol.getParent().getDocStatus());
            mrp.setOrderType("DOO");
            mrp.setTypeMRP("S");
            mrp.saveEx(trxName);
        }
    }
    
    public static void M_Requisition(final MRequisition r) {
        final List<MPPMRP> mrpList = getQuery((PO)r, null, null).list();
        for (final MPPMRP mrp : mrpList) {
            mrp.setM_Requisition(r);
            mrp.saveEx(r.get_TrxName());
        }
    }
    
    public static void M_RequisitionLine(final MRequisitionLine rl) {
        MPPMRP mrp = (MPPMRP)getQuery((PO)rl, null, null).firstOnly();
        final MRequisition r = rl.getParent();
        if (mrp == null) {
            mrp = new MPPMRP(rl.getCtx(), 0, rl.get_TrxName());
            mrp.setM_Requisition_ID(rl.getM_Requisition_ID());
            mrp.setM_RequisitionLine_ID(rl.getM_RequisitionLine_ID());
        }
        mrp.setM_Requisition(r);
        mrp.setAD_Org_ID(rl.getAD_Org_ID());
        mrp.setName("MRP");
        mrp.setDescription(rl.getDescription());
        mrp.setM_Product_ID(rl.getM_Product_ID());
        mrp.setQty(rl.getQty().subtract(rl.getQtyOrdered()));
        if (r.getDocStatus().compareTo("CL") == 0) {
            mrp.setDocStatus("CO");
            mrp.setQty(Env.ZERO);
        }
        else if (r.getDocStatus().compareTo("VO") == 0) {
            mrp.setDocStatus("VO");
            mrp.setQty(Env.ZERO);
        }
        else if (mrp.getQty().compareTo(Env.ZERO) == 0) {
            mrp.setDocStatus("CO");
        }
        else if (rl.getQtyOrdered().compareTo(Env.ZERO) > 0) {
            mrp.setDocStatus("IP");
        }
        else {
            mrp.setDocStatus("DR");
        }
        mrp.saveEx(rl.get_TrxName());
    }
    
    public static boolean hasProductRecords(final MProduct product) {
        return new Query(product.getCtx(), "PP_MRP", "M_Product_ID=? AND Qty<>0", product.get_TrxName()).setParameters(new Object[] { product.getM_Product_ID() }).match();
    }
    
    public static BigDecimal getQtyOnHand(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final String trxName) {
        return DB.getSQLValueBDEx(trxName, "SELECT COALESCE(bomQtyOnHand (M_Product_ID,?,0),0) FROM M_Product WHERE AD_Client_ID=? AND M_Product_ID=?", new Object[] { M_Warehouse_ID, Env.getAD_Client_ID(ctx), M_Product_ID });
    }
    
    public static BigDecimal getQtyReserved(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final Timestamp To, final String trxName) {
        final BigDecimal qty = DB.getSQLValueBDEx(trxName, "SELECT SUM(Qty) FROM PP_MRP WHERE  TypeMRP=? AND DocStatus IN ('IP','CO') AND AD_Client_ID=? AND M_Warehouse_ID =? AND M_Product_ID=? AND DatePromised <=?", new Object[] { "D", Env.getAD_Client_ID(ctx), M_Warehouse_ID, M_Product_ID, To });
        if (qty == null) {
            return Env.ZERO;
        }
        return qty;
    }
    
    public static BigDecimal getQtyReserved(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final String trxName) {
        return getQtyReserved(ctx, M_Warehouse_ID, M_Product_ID, new Timestamp(System.currentTimeMillis()), trxName);
    }
    
    public static BigDecimal getQtyOrdered(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final Timestamp To, final String trxName) {
        final BigDecimal qty = DB.getSQLValueBDEx(trxName, "SELECT SUM(Qty) FROM PP_MRP WHERE  TypeMRP='S' AND DocStatus IN ('IP','CO') AND AD_Client_ID=? AND DatePromised <=? AND M_Warehouse_ID =? AND M_Product_ID=?", new Object[] { Env.getAD_Client_ID(ctx), To, M_Warehouse_ID, M_Product_ID });
        if (qty == null) {
            return Env.ZERO;
        }
        return qty;
    }
    
    public static BigDecimal getQtyOrdered(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final String trxName) {
        return getQtyOrdered(ctx, M_Warehouse_ID, M_Product_ID, new Timestamp(System.currentTimeMillis()), trxName);
    }
    
    public static int getMaxLowLevel(final Properties ctx, final String trxName) {
        final int AD_Client_ID = Env.getAD_Client_ID(ctx);
        final int LowLevel = DB.getSQLValueEx(trxName, "SELECT MAX(LowLevel) FROM M_Product WHERE AD_Client_ID=? AND LowLevel IS NOT NULL", new Object[] { AD_Client_ID });
        return LowLevel + 1;
    }
    
    public static int getDurationDays(final MPPMRP mrp, final BigDecimal qty, final I_PP_Product_Planning pp) {
        final BigDecimal DayMinutes = BigDecimal.valueOf(1440L);
        final BigDecimal duration = BigDecimal.valueOf(getDurationMinutes(mrp, qty, pp, null)).divide(DayMinutes, 0, RoundingMode.UP);
        return duration.intValue();
    }
    
    public static int getDurationMinutes(final MPPMRP mrp, final BigDecimal qty, final I_PP_Product_Planning pp, final Timestamp DemandDateStartSchedule) {
        final BigDecimal DayMinutes = BigDecimal.valueOf(1440L);
        Properties ctx = null;
        if (pp instanceof PO) {
            ctx = ((PO)pp).getCtx();
        }
        else {
            ctx = Env.getCtx();
        }
        final MProduct product = MProduct.get(ctx, pp.getM_Product_ID());
        BigDecimal leadtime = pp.getDeliveryTime_Promised().multiply(DayMinutes);
        if (leadtime.signum() == 0) {
            if (!product.isPurchased()) {
                if (pp.getS_Resource_ID() <= 0 || pp.getAD_Workflow_ID() <= 0) {
                    throw new AdempiereException("Cannot calculate leadtime for " + pp);
                }
                final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(ctx);
                leadtime = routingService.calculateDuration(mrp, pp.getAD_Workflow(), pp.getS_Resource(), qty, DemandDateStartSchedule);
            }
        }
        return leadtime.add(pp.getTransfertTime().multiply(DayMinutes)).intValue();
    }
    
    public static void clearStatic() {
        MPPMRP.C_Order_ID = 0;
        MPPMRP.C_OrderLine_ID = 0;
    }
    
    public static BigDecimal getNetQtyForecast(final Properties ctx, final MPPMRP mrp, final int AD_Client_ID, final String trxName) {
        final String sql = "SELECT PP_MRP_ID FROM PP_MRP WHERE TypeMRP=? AND AD_Client_ID=? AND AD_Org_ID=?  AND M_Warehouse_ID=? AND M_Product_ID=? AND DatePromised>=? AND DatePromised<=? AND PP_MRP_ID<>? AND Qty <>0 ORDER BY DatePromised";
        BigDecimal Qty = mrp.getQty();
        final Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(mrp.getDatePromised().getTime());
        calEnd.add(2, 1);
        calEnd.set(5, 1);
        calEnd.set(11, 0);
        calEnd.set(12, 0);
        calEnd.set(13, 0);
        calEnd.add(13, -1);
        final Timestamp datePromisedEnd = new Timestamp(calEnd.getTimeInMillis());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, trxName);
            pstmt.setString(1, "D");
            pstmt.setInt(2, AD_Client_ID);
            pstmt.setInt(3, mrp.getAD_Org_ID());
            pstmt.setInt(4, mrp.getM_Warehouse_ID());
            pstmt.setInt(5, mrp.getM_Product_ID());
            pstmt.setTimestamp(6, mrp.getDatePromised());
            pstmt.setTimestamp(7, datePromisedEnd);
            pstmt.setInt(8, mrp.getPP_MRP_ID());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final int PP_MRP_ID = rs.getInt("PP_MRP_ID");
                final MPPMRP mrpDemand = new MPPMRP(ctx, PP_MRP_ID, trxName);
                if (mrpDemand.getOrderType().equals("FCT")) {
                    break;
                }
                if (Qty.compareTo(Env.ZERO) <= 0) {
                    break;
                }
                Qty = Qty.subtract(mrpDemand.getQty());
            }
            Qty = Qty.max(Env.ZERO);
        }
        catch (SQLException ex) {
            throw new DBException((Exception)ex);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return Qty;
    }
    
    public static boolean materialDemandOfMO(final MPPMRP mrp) {
        return mrp.getTypeMRP().equals("D") && mrp.getOrderType().equals("MOP");
    }
    
    public static String getDocumentNo(final int PP_MRP_ID) {
        return DB.getSQLValueStringEx((String)null, "SELECT documentNo(PP_MRP_ID) AS DocumentNo FROM PP_MRP WHERE PP_MRP_ID = ?", new Object[] { PP_MRP_ID });
    }
    
    @Override
    public String toString() {
        final String description = this.getDescription();
        return String.valueOf(this.getClass().getSimpleName()) + "[" + ", TypeMRP=" + this.getTypeMRP() + ", DocStatus=" + this.getDocStatus() + ", Qty=" + this.getQty() + ", DatePromised=" + this.getDatePromised() + ", Schedule=" + this.getDateStartSchedule() + "/" + this.getDateFinishSchedule() + ", IsAvailable=" + this.isAvailable() + (Util.isEmpty(description, true) ? "" : (", Description=" + description)) + ", ID=" + this.get_ID() + "]";
    }
    
    public boolean processIt(final String action) throws Exception {
        return false;
    }
    
    public boolean unlockIt() {
        return false;
    }
    
    public boolean invalidateIt() {
        return false;
    }
    
    public String prepareIt() {
        return null;
    }
    
    public boolean approveIt() {
        return false;
    }
    
    public boolean rejectIt() {
        return false;
    }
    
    public String completeIt() {
        return null;
    }
    
    public boolean voidIt() {
        return false;
    }
    
    public boolean closeIt() {
        return false;
    }
    
    public boolean reverseCorrectIt() {
        return false;
    }
    
    public boolean reverseAccrualIt() {
        return false;
    }
    
    public boolean reActivateIt() {
        return false;
    }
    
    public String getSummary() {
        return null;
    }
    
    public String getDocumentNo() {
        return null;
    }
    
    public String getDocumentInfo() {
        return null;
    }
    
    public File createPDF() {
        return null;
    }
    
    public String getProcessMsg() {
        return null;
    }
    
    public int getDoc_User_ID() {
        return 0;
    }
    
    public int getC_Currency_ID() {
        return 0;
    }
    
    public BigDecimal getApprovalAmt() {
        return null;
    }
    
    public String getDocAction() {
        return null;
    }
}
