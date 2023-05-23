// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.impl;

import org.compiere.model.I_C_UOM;
import org.compiere.model.MUOM;
import org.compiere.wf.MWFProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.model.MProcess;
import org.compiere.wf.MWFActivity;
import org.compiere.util.TimeUtil;
import java.util.Date;
import org.compiere.model.Query;
import org.compiere.model.MResourceAssignment;
import java.util.Properties;
import org.compiere.model.MResource;
import org.libero.exceptions.CRPException;
import org.compiere.wf.MWFNode;
import java.util.ArrayList;
import org.libero.process.CRP;
import org.compiere.wf.MWorkflow;
import org.libero.model.reasoner.CRPReasoner;
import org.compiere.model.I_S_Resource;
import org.libero.model.MPPMRP;
import org.compiere.model.I_AD_Workflow;
import org.adempiere.exceptions.AdempiereException;
import java.math.RoundingMode;
import org.libero.model.MPPOrderNode;
import org.compiere.util.Env;
import org.compiere.model.PO;
import org.libero.tables.I_PP_Cost_Collector;
import org.libero.tables.I_PP_Order_Node;
import java.math.BigDecimal;
import org.compiere.model.I_AD_WF_Node;
import java.sql.Timestamp;
import org.compiere.util.CLogger;
import org.libero.model.RoutingService;

public class DefaultRoutingServiceImpl implements RoutingService
{
    private final CLogger log;
    private Timestamp startAssignTime;
    
    public DefaultRoutingServiceImpl() {
        this.log = CLogger.getCLogger((Class)this.getClass());
    }
    
    @Override
    public BigDecimal estimateWorkingTime(final I_AD_WF_Node node) {
        double duration;
        if (node.getUnitsCycles().signum() == 0) {
            duration = node.getDuration();
        }
        else {
            duration = node.getDuration() / node.getUnitsCycles().doubleValue();
        }
        return BigDecimal.valueOf(duration);
    }
    
    @Override
    public BigDecimal estimateWorkingTime(final I_PP_Order_Node node, final BigDecimal qty) {
        final double unitDuration = node.getDuration();
        final double cycles = this.calculateCycles(node.getUnitsCycles(), qty);
        final BigDecimal duration = BigDecimal.valueOf(unitDuration * cycles);
        return duration;
    }
    
    public BigDecimal estimateWorkingTime(final I_AD_WF_Node node, final BigDecimal qty) {
        final double unitDuration = node.getDuration();
        final double cycles = this.calculateCycles(node.getUnitsCycles().intValue(), qty);
        final BigDecimal duration = BigDecimal.valueOf(unitDuration * cycles);
        return duration;
    }
    
    @Override
    public BigDecimal estimateWorkingTime(final I_PP_Cost_Collector cc) {
        final String trxName = (cc instanceof PO) ? ((PO)cc).get_TrxName() : null;
        final BigDecimal qty = cc.getMovementQty();
        final MPPOrderNode node = MPPOrderNode.get(Env.getCtx(), cc.getPP_Order_Node_ID(), trxName);
        return this.estimateWorkingTime(node, qty);
    }
    
    protected int calculateCycles(final int unitsCycle, final BigDecimal qty) {
        BigDecimal cycles = qty;
        final BigDecimal unitsCycleBD = BigDecimal.valueOf(unitsCycle);
        if (unitsCycleBD.signum() > 0) {
            cycles = qty.divide(unitsCycleBD, 0, RoundingMode.UP);
        }
        return cycles.intValue();
    }
    
    protected BigDecimal calculateDuration(I_AD_WF_Node node, final I_PP_Cost_Collector cc) {
        if (node == null) {
            node = cc.getPP_Order_Node().getAD_WF_Node();
        }
        if (node == null) {
            throw new AdempiereException("calculateDuration not supported using Node null!!!");
        }
        final I_AD_Workflow workflow = node.getAD_Workflow();
        final double batchSize = workflow.getQtyBatchSize().doubleValue();
        BigDecimal batchS = Env.ONE;
        double queuingTime = 0.0;
        double waitingTime = 0.0;
        double movingTime = 0.0;
        if (node != null && cc == null) {
            queuingTime = node.getQueuingTime();
            waitingTime += node.getWaitingTime();
            movingTime += node.getMovingTime();
        }
        else if (cc != null) {
            queuingTime = cc.getPP_Order_Node().getQueuingTime();
            waitingTime += cc.getPP_Order_Node().getWaitingTime();
            movingTime += cc.getPP_Order_Node().getMovingTime();
        }
        double totalDuration;
        if (cc != null) {
            final double setupTime = cc.getSetupTimeReal().doubleValue();
            final double duration = cc.getDurationReal().doubleValue();
            batchS = cc.getPP_Order().getQtyBatchs();
            if (batchSize > 1.0) {
                totalDuration = (queuingTime + waitingTime + movingTime) * batchS.doubleValue() + setupTime + duration;
            }
            else {
                totalDuration = queuingTime + waitingTime + movingTime + setupTime + duration;
            }
        }
        else {
            final double setupTime = node.getSetupTime();
            final double duration = this.estimateWorkingTime(node).doubleValue();
            if (batchSize > 1.0) {
                totalDuration = (setupTime + queuingTime + waitingTime + movingTime) / batchSize + duration;
            }
            else {
                totalDuration = setupTime + queuingTime + waitingTime + movingTime + duration;
            }
        }
        return BigDecimal.valueOf(totalDuration);
    }
    
    @Override
    public BigDecimal calculateDuration(final MPPMRP mrp, final I_AD_Workflow wf, final I_S_Resource plant, final BigDecimal qty, final Timestamp DemandDateStartSchedule) {
        if (plant == null) {
            return Env.ZERO;
        }
        final Properties ctx = mrp.getCtx();
        final CRPReasoner reasoner = new CRPReasoner();
        BigDecimal duration = Env.ZERO;
        final MWFNode[] nodes = ((MWorkflow)wf).getNodes(true, Env.getAD_Client_ID(ctx));
        Timestamp counter = DemandDateStartSchedule;
        final CRP crp = new CRP();
        final ArrayList<MWFNode> list = new ArrayList<MWFNode>();
        for (int n = nodes.length - 1; n >= 0; --n) {
            list.add(nodes[n]);
        }
        list.toArray(nodes);
        final ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
        MWFNode[] array;
        for (int length = (array = nodes).length, i = 0; i < length; ++i) {
            final I_AD_WF_Node node = (I_AD_WF_Node)array[i];
            final int nodeId = node.getAD_WF_Node_ID();
            if (visitedNodes.contains(nodeId)) {
                throw new CRPException("Cyclic transition found " + node.getName());
            }
            visitedNodes.add(nodeId);
            this.log.info(("PP_Order Node:" + node.getName() != null) ? node.getName() : ((" Description:" + node.getDescription() != null) ? node.getDescription() : ""));
            final MResource resource = MResource.get(ctx, node.getS_Resource_ID());
            if (resource != null) {
                if (!reasoner.isAvailable((I_S_Resource)resource)) {
                    throw new CRPException("@ResourceNotInSlotDay@").setS_Resource((I_S_Resource)resource);
                }
                final long nodeMillis = this.calculateMillisFor(node, this.getDurationBaseSec(wf.getDurationUnit()), qty);
                final Timestamp dateStart = crp.scheduleBackward(counter, nodeMillis, resource, Env.getAD_Client_ID(ctx));
                this.createWFActivity(mrp, wf);
                final BigDecimal durationRealThisNode = BigDecimal.valueOf(nodeMillis / 1000L / 60L);
                final BigDecimal durationThisNodeBG = BigDecimal.valueOf((counter.getTime() - dateStart.getTime()) / 1000L / 60L);
                this.createResourceAssign(mrp, ctx, durationThisNodeBG, node, dateStart, durationRealThisNode);
                duration = duration.add(durationThisNodeBG);
                mrp.setDateStartSchedule(dateStart);
                mrp.setDateFinishSchedule(DemandDateStartSchedule);
                mrp.saveEx(mrp.get_TrxName());
                counter = dateStart;
            }
        }
        return duration;
    }
    
    @Override
    public long calculateMillisFor(final MPPOrderNode node, final long commonBase) {
        final BigDecimal qty = node.getQtyToDeliver();
        long totalDuration = node.getQueuingTime() + node.getSetupTime() + node.getMovingTime() + node.getWaitingTime();
        final BigDecimal workingTime = this.estimateWorkingTime(node, qty);
        final BigDecimal qtyBatchSize = node.getPP_Order_Workflow().getQtyBatchSize();
        if (qtyBatchSize.compareTo(Env.ONE) == 1) {
            final BigDecimal qtyBatchs = qty.divide(qtyBatchSize, 0, 0);
            totalDuration *= qtyBatchs.longValue();
        }
        totalDuration += (long)workingTime.doubleValue();
        return totalDuration * commonBase * 1000L;
    }
    
    @Override
    public long calculateMillisFor(final I_AD_WF_Node node, final long commonBase, final BigDecimal qty) {
        long totalDuration = node.getQueuingTime() + node.getSetupTime() + node.getMovingTime() + node.getWaitingTime();
        final BigDecimal workingTime = this.estimateWorkingTime(node, qty);
        final BigDecimal qtyBatchSize = node.getWorkflow().getQtyBatchSize();
        if (qtyBatchSize.compareTo(Env.ONE) == 1) {
            final BigDecimal qtyBatchs = qty.divide(qtyBatchSize, 0, 0);
            totalDuration *= qtyBatchs.longValue();
        }
        totalDuration += (long)workingTime.doubleValue();
        return totalDuration * commonBase * 1000L;
    }
    
    private MResourceAssignment createResourceAssign(final MPPMRP mrp, final Properties ctx, final BigDecimal duration, final I_AD_WF_Node node) {
        final MResourceAssignment resourceschedule = (MResourceAssignment)new Query(Env.getCtx(), "S_ResourceAssignment", "S_Resource_ID=?", (String)null).setParameters(new Object[] { node.getS_Resource_ID() }).first();
        final Date date = new Date();
        this.startAssignTime = new Timestamp(date.getTime());
        MResourceAssignment ra;
        if (resourceschedule != null) {
            if (resourceschedule.getName().equals("MRP:" + mrp.get_ID() + " Order:" + mrp.getC_Order().getDocumentNo())) {
                ra = resourceschedule;
            }
            else if (resourceschedule.getName().equals("MRP:" + mrp.get_ID() + " MO:" + mrp.getPP_Order().getDocumentNo())) {
                ra = resourceschedule;
            }
            else {
                this.startAssignTime = resourceschedule.getAssignDateTo();
                ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
            }
        }
        else {
            ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
        }
        ra.setAD_Org_ID(mrp.getAD_Org_ID());
        ra.setName("MRP:" + mrp.get_ID() + " Order:" + mrp.getC_Order().getDocumentNo());
        ra.setAssignDateFrom(this.startAssignTime);
        ra.setAssignDateTo(TimeUtil.addMinutess(this.startAssignTime, duration.intValueExact()));
        ra.setS_Resource_ID(node.getS_Resource_ID());
        ra.setDescription(String.valueOf(mrp.getC_OrderLine().getM_Product().getName()) + " " + mrp.getC_OrderLine().getQtyOrdered());
        ra.saveEx(mrp.get_TrxName());
        return ra;
    }
    
    private MResourceAssignment createResourceAssign(final MPPMRP mrp, final Properties ctx, final BigDecimal duration, final I_AD_WF_Node node, final Timestamp startDateTime, final BigDecimal durationRealMinutes) {
        String m_name = "MRP:" + mrp.get_ID();
        if (mrp.getC_Order().getDocumentNo() != null) {
            m_name = m_name.concat(" Order:" + mrp.getC_Order().getDocumentNo());
        }
        else if (mrp.getPP_Order().getDocumentNo() != null) {
            m_name = m_name.concat(" MO:" + mrp.getPP_Order().getDocumentNo());
        }
        final MResourceAssignment resourceschedule = (MResourceAssignment)new Query(Env.getCtx(), "S_ResourceAssignment", "S_Resource_ID=? AND Name=?", (String)null).setParameters(new Object[] { node.getS_Resource_ID(), m_name }).first();
        this.startAssignTime = new Timestamp(startDateTime.getTime());
        MResourceAssignment ra;
        if (resourceschedule != null) {
            if (resourceschedule.getName().equals(m_name)) {
                ra = resourceschedule;
            }
            else {
                ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
            }
        }
        else {
            ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
        }
        ra.setAD_Org_ID(mrp.getAD_Org_ID());
        ra.setName(m_name);
        ra.setAssignDateFrom(this.startAssignTime);
        ra.setAssignDateTo(TimeUtil.addMinutess(this.startAssignTime, duration.intValueExact()));
        ra.setS_Resource_ID(node.getS_Resource_ID());
        if (mrp.getC_OrderLine().getM_Product() != null && mrp.getC_OrderLine().getQtyOrdered().compareTo(Env.ZERO) != 0) {
            ra.setDescription(String.valueOf(mrp.getC_OrderLine().getM_Product().getName()) + " " + mrp.getC_OrderLine().getQtyOrdered());
        }
        else {
            ra.setDescription(String.valueOf(mrp.getM_Product().getName()) + " " + mrp.getQty());
        }
        final String baseUOMResource = node.getS_Resource().getS_ResourceType().getC_UOM().getUOMSymbol().trim();
        final double durationBaseSec = (double)this.getDurationBaseSec(baseUOMResource);
        if (durationBaseSec == 0.0) {
            throw new AdempiereException("@NotSupported@ @C_UOM_ID@ - " + baseUOMResource);
        }
        final BigDecimal durationRealUOM = durationRealMinutes.multiply(BigDecimal.valueOf(60L)).divide(BigDecimal.valueOf(durationBaseSec), 8, RoundingMode.UP);
        ra.setQty(durationRealUOM);
        ra.saveEx(mrp.get_TrxName());
        return ra;
    }
    
    @Override
    public MResourceAssignment createResourceAssign(final MPPMRP mrp, final Properties ctx, final BigDecimal durationRealMinutes, final I_AD_WF_Node node, final Timestamp startDateTime, final Timestamp finishDateTime) {
        final String m_prefix;
        String m_name = m_prefix = "MRP:" + mrp.get_ID();
        if (mrp.getPP_Order().getDocumentNo() != null && mrp.getC_Order().getDocumentNo() != null) {
            m_name = m_name.concat(" MO:" + mrp.getPP_Order().getDocumentNo() + " Order:" + mrp.getC_Order().getDocumentNo());
        }
        else if (mrp.getC_Order().getDocumentNo() != null) {
            m_name = m_name.concat(" Order:" + mrp.getC_Order().getDocumentNo());
        }
        else if (mrp.getPP_Order().getDocumentNo() != null) {
            m_name = m_name.concat(" MO:" + mrp.getPP_Order().getDocumentNo());
        }
        MResourceAssignment resourceschedule = (MResourceAssignment)new Query(Env.getCtx(), "S_ResourceAssignment", "S_Resource_ID=? AND Name=?", (String)null).setParameters(new Object[] { node.getS_Resource_ID(), m_name }).first();
        if (resourceschedule == null) {
            resourceschedule = (MResourceAssignment)new Query(Env.getCtx(), "S_ResourceAssignment", "S_Resource_ID=? AND Name like ?", (String)null).setParameters(new Object[] { node.getS_Resource_ID(), String.valueOf(m_prefix) + '%' }).first();
        }
        final Timestamp startAssignTime = new Timestamp(startDateTime.getTime());
        MResourceAssignment ra;
        if (resourceschedule != null) {
            if (resourceschedule.getName().startsWith(m_prefix)) {
                ra = resourceschedule;
            }
            else {
                ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
            }
        }
        else {
            ra = new MResourceAssignment(ctx, 0, mrp.get_TrxName());
        }
        ra.setAD_Org_ID(mrp.getAD_Org_ID());
        ra.setName(m_name);
        ra.setAssignDateFrom(startAssignTime);
        ra.setAssignDateTo(finishDateTime);
        ra.setS_Resource_ID(node.getS_Resource_ID());
        if (mrp.getC_OrderLine().getM_Product() != null && mrp.getC_OrderLine().getQtyOrdered().compareTo(Env.ZERO) != 0) {
            ra.setDescription(String.valueOf(mrp.getC_OrderLine().getM_Product().getName()) + " " + mrp.getC_OrderLine().getQtyOrdered());
        }
        else {
            ra.setDescription(String.valueOf(mrp.getM_Product().getName()) + " " + mrp.getQty());
        }
        final String baseUOMResource = node.getS_Resource().getS_ResourceType().getC_UOM().getUOMSymbol().trim();
        final double durationBaseSec = (double)this.getDurationBaseSec(baseUOMResource);
        if (durationBaseSec == 0.0) {
            throw new AdempiereException("@NotSupported@ @C_UOM_ID@ - " + baseUOMResource);
        }
        final BigDecimal durationRealUOM = durationRealMinutes.multiply(BigDecimal.valueOf(60L)).divide(BigDecimal.valueOf(durationBaseSec), 8, RoundingMode.UP);
        ra.setQty(durationRealUOM);
        ra.saveEx(mrp.get_TrxName());
        return ra;
    }
    
    private void createWFActivity(final MPPMRP mrp, final I_AD_Workflow wf) {
        if (wf != null) {
            try {
                final int Record_ID = mrp.get_ID();
                final MWFActivity act = (MWFActivity)new Query(Env.getCtx(), "AD_WF_Activity", "Record_ID=?", mrp.get_TrxName()).setParameters(new Object[] { Record_ID }).first();
                if (act != null) {
                    if (act.getWFState().equals("OS")) {
                        act.delete(true);
                    }
                    else {
                        this.log.severe("Workflow Activity Was Created and Processed Before This!");
                    }
                }
                final int Table_ID = 53027;
                final int AD_Process_ID = MProcess.getProcess_ID("MFG_WF_Activity", mrp.get_TrxName());
                final PO po = mrp;
                wf.setAD_Table_ID(po.get_Table_ID());
                final ProcessInfo pi = new ProcessInfo(wf.getName(), AD_Process_ID, Table_ID, Record_ID);
                pi.setTransactionName(mrp.get_TrxName());
                pi.setPO(po);
                final MWFProcess wfProcess = new MWFProcess((MWorkflow)wf, pi, mrp.get_TrxName());
                wfProcess.startWork();
            }
            catch (Exception ex) {
                this.log.warning("Workflow Activity failed to work");
            }
        }
    }
    
    protected BigDecimal convertDurationToResourceUOM(final BigDecimal duration, final int S_Resource_ID, final I_AD_WF_Node node) {
        final MResource resource = MResource.get(Env.getCtx(), S_Resource_ID);
        final I_AD_Workflow wf = (I_AD_Workflow)MWorkflow.get(Env.getCtx(), node.getAD_Workflow_ID());
        final I_C_UOM resourceUOM = (I_C_UOM)MUOM.get(Env.getCtx(), resource.getC_UOM_ID());
        return this.convertDuration(duration, wf.getDurationUnit(), resourceUOM);
    }
    
    @Override
    public BigDecimal getResourceBaseValue(final int S_Resource_ID, final I_PP_Cost_Collector cc) {
        return this.getResourceBaseValue(S_Resource_ID, null, cc);
    }
    
    @Override
    public BigDecimal getResourceBaseValue(final int S_Resource_ID, final I_AD_WF_Node node) {
        return this.getResourceBaseValue(S_Resource_ID, node, null);
    }
    
    protected BigDecimal getResourceBaseValue(final int S_Resource_ID, I_AD_WF_Node node, final I_PP_Cost_Collector cc) {
        if (node == null) {
            node = cc.getPP_Order_Node().getAD_WF_Node();
        }
        final Properties ctx = (node instanceof PO) ? ((PO)node).getCtx() : Env.getCtx();
        final MResource resource = MResource.get(ctx, S_Resource_ID);
        final MUOM resourceUOM = MUOM.get(ctx, resource.getC_UOM_ID());
        if (this.isTime((I_C_UOM)resourceUOM)) {
            final BigDecimal duration = this.calculateDuration(node, cc);
            final I_AD_Workflow wf = (I_AD_Workflow)MWorkflow.get(ctx, node.getAD_Workflow_ID());
            final BigDecimal convertedDuration = this.convertDuration(duration, wf.getDurationUnit(), (I_C_UOM)resourceUOM);
            return convertedDuration;
        }
        throw new AdempiereException("@NotSupported@ @C_UOM_ID@ - " + resourceUOM);
    }
    
    protected I_AD_WF_Node getAD_WF_Node(final I_PP_Cost_Collector cc) {
        final I_PP_Order_Node activity = cc.getPP_Order_Node();
        return activity.getAD_WF_Node();
    }
    
    public long getDurationBaseSec(final String durationUnit) {
        if (durationUnit == null) {
            return 0L;
        }
        if ("s".equals(durationUnit)) {
            return 1L;
        }
        if ("m".equals(durationUnit)) {
            return 60L;
        }
        if ("h".equals(durationUnit)) {
            return 3600L;
        }
        if ("D".equals(durationUnit)) {
            return 86400L;
        }
        if ("M".equals(durationUnit)) {
            return 2592000L;
        }
        if ("Y".equals(durationUnit)) {
            return 31536000L;
        }
        return 0L;
    }
    
    public long getDurationBaseSec(final I_C_UOM uom) {
        final MUOM uomImpl = (MUOM)uom;
        if (uomImpl.isWeek()) {
            return 604800L;
        }
        if (uomImpl.isDay()) {
            return 86400L;
        }
        if (uomImpl.isHour()) {
            return 3600L;
        }
        if (uomImpl.isMinute()) {
            return 60L;
        }
        if (uomImpl.isSecond()) {
            return 1L;
        }
        throw new AdempiereException("@NotSupported@ @C_UOM_ID@=" + uom.getName());
    }
    
    public boolean isTime(final I_C_UOM uom) {
        final String x12de355 = uom.getX12DE355();
        return "03".equals(x12de355) || "MJ".equals(x12de355) || "HR".equals(x12de355) || "DA".equals(x12de355) || "WD".equals(x12de355) || "WK".equals(x12de355) || "MO".equals(x12de355) || "WM".equals(x12de355) || "YR".equals(x12de355);
    }
    
    public BigDecimal convertDuration(final BigDecimal duration, final String fromDurationUnit, final I_C_UOM toUOM) {
        final double fromMult = (double)this.getDurationBaseSec(fromDurationUnit);
        final double toDiv = (double)this.getDurationBaseSec(toUOM);
        final BigDecimal convertedDuration = BigDecimal.valueOf(duration.doubleValue() * fromMult / toDiv);
        return convertedDuration;
    }
    
    @Override
    public Timestamp getStartAssignTime() {
        return this.startAssignTime;
    }
}
