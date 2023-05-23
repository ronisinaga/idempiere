// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.util.Msg;
import java.math.BigDecimal;
import org.compiere.model.MDocType;
import org.libero.tables.I_PP_Order_Node;
import org.adempiere.exceptions.AdempiereException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import org.compiere.model.Query;
import org.compiere.wf.MWorkflow;
import java.sql.ResultSet;
import org.compiere.util.Env;
import org.compiere.model.MClient;
import java.util.Properties;
import java.util.List;
import org.compiere.util.CCache;
import org.libero.tables.X_PP_Order_Workflow;

public class MPPOrderWorkflow extends X_PP_Order_Workflow
{
    private static final long serialVersionUID = 1L;
    private static CCache<Integer, MPPOrderWorkflow> s_cache;
    private List<MPPOrderNode> m_nodes;
    private MPPOrder m_order;
    
    static {
        MPPOrderWorkflow.s_cache = (CCache<Integer, MPPOrderWorkflow>)new CCache("PP_Order_Workflow", 20);
    }
    
    public static MPPOrderWorkflow get(final Properties ctx, final int PP_Order_Workflow_ID) {
        if (PP_Order_Workflow_ID <= 0) {
            return null;
        }
        MPPOrderWorkflow retValue = (MPPOrderWorkflow)MPPOrderWorkflow.s_cache.get((Object)PP_Order_Workflow_ID);
        if (retValue != null) {
            return retValue;
        }
        retValue = new MPPOrderWorkflow(ctx, PP_Order_Workflow_ID, null);
        if (retValue.get_ID() != 0) {
            MPPOrderWorkflow.s_cache.put(PP_Order_Workflow_ID, retValue);
        }
        return retValue;
    }
    
    public MPPOrderWorkflow(final Properties ctx, final int PP_Order_Workflow_ID, final String trxName) {
        super(ctx, PP_Order_Workflow_ID, trxName);
        this.m_nodes = null;
        this.m_order = null;
        if (PP_Order_Workflow_ID == 0) {
            this.setAccessLevel("1");
            this.setAuthor(MClient.get(ctx).getName());
            this.setDurationUnit("D");
            this.setDuration(1);
            this.setEntityType("U");
            this.setIsDefault(false);
            this.setPublishStatus("U");
            this.setVersion(0);
            this.setCost(Env.ZERO);
            this.setWaitingTime(0);
            this.setWorkingTime(0);
        }
    }
    
    public MPPOrderWorkflow(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_nodes = null;
        this.m_order = null;
    }
    
    public MPPOrderWorkflow(final MWorkflow workflow, final int PP_Order_ID, final String trxName) {
        this(workflow.getCtx(), 0, trxName);
        this.setPP_Order_ID(PP_Order_ID);
        this.setValue(workflow.getValue());
        this.setWorkflowType(workflow.getWorkflowType());
        this.setQtyBatchSize(workflow.getQtyBatchSize());
        this.setName(workflow.getName());
        this.setAccessLevel(workflow.getAccessLevel());
        this.setAuthor(workflow.getAuthor());
        this.setDurationUnit(workflow.getDurationUnit());
        this.setDuration(workflow.getDuration());
        this.setEntityType(workflow.getEntityType());
        this.setIsDefault(workflow.isDefault());
        this.setPublishStatus(workflow.getPublishStatus());
        this.setVersion(workflow.getVersion());
        this.setCost(workflow.getCost());
        this.setWaitingTime(workflow.getWaitingTime());
        this.setWorkingTime(workflow.getWorkingTime());
        this.setAD_WF_Responsible_ID(workflow.getAD_WF_Responsible_ID());
        this.setAD_Workflow_ID(workflow.getAD_Workflow_ID());
        this.setLimit(workflow.getLimit());
        this.setPriority(workflow.getPriority());
        this.setS_Resource_ID(workflow.getS_Resource_ID());
        this.setQueuingTime(workflow.getQueuingTime());
        this.setSetupTime(workflow.getSetupTime());
        this.setMovingTime(workflow.getMovingTime());
        this.setProcessType(workflow.getProcessType());
        this.setAD_Table_ID(workflow.getAD_Table_ID());
        this.setAD_WF_Node_ID(workflow.getAD_WF_Node_ID());
        this.setAD_WorkflowProcessor_ID(workflow.getAD_WorkflowProcessor_ID());
        this.setDescription(workflow.getDescription());
        this.setValidFrom(workflow.getValidFrom());
        this.setValidTo(workflow.getValidTo());
    }
    
    public List<MPPOrderNode> getNodes(final boolean requery) {
        if (this.m_nodes == null || requery) {
            this.m_nodes = new Query(this.getCtx(), "PP_Order_Node", "PP_Order_Workflow_ID=?", this.get_TrxName()).setParameters(new Object[] { this.get_ID() }).setOnlyActiveRecords(true).list();
            this.log.fine("#" + this.m_nodes.size());
        }
        return this.m_nodes;
    }
    
    protected List<MPPOrderNode> getNodes() {
        return this.getNodes(false);
    }
    
    public int getNodeCount() {
        return this.getNodes().size();
    }
    
    public MPPOrderNode[] getNodes(final boolean ordered, final int AD_Client_ID) {
        if (ordered) {
            return this.getNodesInOrder(AD_Client_ID);
        }
        final ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
        for (final MPPOrderNode node : this.getNodes()) {
            if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID) {
                list.add(node);
            }
        }
        return list.toArray(new MPPOrderNode[list.size()]);
    }
    
    public MPPOrderNode getFirstNode() {
        return this.getNode(this.getPP_Order_Node_ID());
    }
    
    private MPPOrderNode getNode(final int PP_Order_Node_ID, final int AD_Client_ID) {
        if (PP_Order_Node_ID <= 0) {
            return null;
        }
        for (final MPPOrderNode node : this.getNodes()) {
            if (node.getPP_Order_Node_ID() == PP_Order_Node_ID) {
                if (AD_Client_ID < 0) {
                    return node;
                }
                if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID) {
                    return node;
                }
                return null;
            }
        }
        return null;
    }
    
    public MPPOrderNode getNode(final int PP_Order_Node_ID) {
        return this.getNode(PP_Order_Node_ID, -1);
    }
    
    public MPPOrderNode[] getNextNodes(final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode node = this.getNode(PP_Order_Node_ID);
        if (node == null || node.getNextNodeCount() == 0) {
            return null;
        }
        final ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
        MPPOrderNodeNext[] transitions;
        for (int length = (transitions = node.getTransitions(AD_Client_ID)).length, i = 0; i < length; ++i) {
            final MPPOrderNodeNext nextTr = transitions[i];
            final MPPOrderNode next = this.getNode(nextTr.getPP_Order_Next_ID(), AD_Client_ID);
            if (next != null) {
                list.add(next);
            }
        }
        return list.toArray(new MPPOrderNode[list.size()]);
    }
    
    private MPPOrderNode[] getNodesInOrder(final int AD_Client_ID) {
        final ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
        this.addNodesSF(list, this.getPP_Order_Node_ID(), AD_Client_ID);
        if (this.getNodeCount() != list.size()) {
            for (final MPPOrderNode node : this.getNodes()) {
                if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID) {
                    boolean found = false;
                    for (final MPPOrderNode existing : list) {
                        if (existing.getPP_Order_Node_ID() == node.getPP_Order_Node_ID()) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        continue;
                    }
                    this.log.log(Level.WARNING, "Added Node w/o transition: " + node);
                    list.add(node);
                }
            }
        }
        final MPPOrderNode[] nodeArray = new MPPOrderNode[list.size()];
        list.toArray(nodeArray);
        return nodeArray;
    }
    
    private void addNodesSF(final Collection<MPPOrderNode> list, final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode node = this.getNode(PP_Order_Node_ID, AD_Client_ID);
        if (node != null) {
            if (!list.contains(node)) {
                list.add(node);
            }
            final ArrayList<Integer> nextNodes = new ArrayList<Integer>();
            MPPOrderNodeNext[] transitions;
            for (int length = (transitions = node.getTransitions(AD_Client_ID)).length, i = 0; i < length; ++i) {
                final MPPOrderNodeNext next = transitions[i];
                final MPPOrderNode child = this.getNode(next.getPP_Order_Next_ID(), AD_Client_ID);
                if (child != null) {
                    if (!list.contains(child)) {
                        list.add(child);
                        nextNodes.add(next.getPP_Order_Next_ID());
                    }
                    else {
                        this.log.saveError("Error", "Cyclic transition found - " + node + " -> " + child);
                    }
                }
            }
            for (final int pp_Order_Next_ID : nextNodes) {
                this.addNodesSF(list, pp_Order_Next_ID, AD_Client_ID);
            }
        }
    }
    
    public int getNext(final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        int i = 0;
        while (i < nodes.length) {
            if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID) {
                final MPPOrderNodeNext[] nexts = nodes[i].getTransitions(AD_Client_ID);
                if (nexts.length > 0) {
                    return nexts[0].getPP_Order_Next_ID();
                }
                return 0;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    public MPPOrderNodeNext[] getNodeNexts(final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        for (int i = 0; i < nodes.length; ++i) {
            if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID) {
                return nodes[i].getTransitions(AD_Client_ID);
            }
        }
        return new MPPOrderNodeNext[0];
    }
    
    public int getPrevious(final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        int i = 0;
        while (i < nodes.length) {
            if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID) {
                if (i > 0) {
                    return nodes[i - 1].getPP_Order_Node_ID();
                }
                return 0;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    public int getNodeLastID(final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        if (nodes.length > 0) {
            return nodes[nodes.length - 1].getPP_Order_Node_ID();
        }
        return 0;
    }
    
    public MPPOrderNode getLastNode(final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        if (nodes.length > 0) {
            return nodes[nodes.length - 1];
        }
        return null;
    }
    
    public boolean isFirst(final int PP_Order_Node_ID, final int AD_Client_ID) {
        return PP_Order_Node_ID == this.getPP_Order_Node_ID();
    }
    
    public boolean isLast(final int PP_Order_Node_ID, final int AD_Client_ID) {
        final MPPOrderNode[] nodes = this.getNodesInOrder(AD_Client_ID);
        return PP_Order_Node_ID == nodes[nodes.length - 1].getPP_Order_Node_ID();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MPPOrderWorkflow[");
        sb.append(this.get_ID()).append("-").append(this.getName()).append("]");
        return sb.toString();
    }
    
    protected boolean afterSave(final boolean newRecord, final boolean success) {
        this.log.fine("Success=" + success);
        if (success && newRecord) {
            final MPPOrderNode[] nodes = this.getNodesInOrder(0);
            for (int i = 0; i < nodes.length; ++i) {
                nodes[i].saveEx(this.get_TrxName());
            }
        }
        return success;
    }
    
    public long getDurationBaseSec() {
        if (this.getDurationUnit() == null) {
            return 0L;
        }
        if ("s".equals(this.getDurationUnit())) {
            return 1L;
        }
        if ("m".equals(this.getDurationUnit())) {
            return 60L;
        }
        if ("h".equals(this.getDurationUnit())) {
            return 3600L;
        }
        if ("D".equals(this.getDurationUnit())) {
            return 86400L;
        }
        if ("M".equals(this.getDurationUnit())) {
            return 2592000L;
        }
        if ("Y".equals(this.getDurationUnit())) {
            return 31536000L;
        }
        return 0L;
    }
    
    public int getDurationCalendarField() {
        if (this.getDurationUnit() == null) {
            return 12;
        }
        if ("s".equals(this.getDurationUnit())) {
            return 13;
        }
        if ("m".equals(this.getDurationUnit())) {
            return 12;
        }
        if ("h".equals(this.getDurationUnit())) {
            return 10;
        }
        if ("D".equals(this.getDurationUnit())) {
            return 6;
        }
        if ("M".equals(this.getDurationUnit())) {
            return 2;
        }
        if ("Y".equals(this.getDurationUnit())) {
            return 1;
        }
        return 12;
    }
    
    public void closeActivities(final MPPOrderNode activity, final Timestamp movementDate, final boolean milestone) {
        if (activity.getPP_Order_Workflow_ID() != this.get_ID()) {
            throw new AdempiereException("Activity and Order Workflow not matching (" + activity + ", PP_Order_Workflow_ID=" + this.get_ID() + ")");
        }
        final MPPOrder order = this.getMPPOrder();
        for (int nodeId = activity.get_ID(); nodeId != 0; nodeId = this.getPrevious(nodeId, this.getAD_Client_ID())) {
            final MPPOrderNode node = this.getNode(nodeId);
            if (milestone && node.isMilestone() && node.get_ID() != activity.get_ID()) {
                break;
            }
            if ("DR".equals(node.getDocStatus())) {
                final BigDecimal qtyToDeliver = node.getQtyToDeliver();
                if (qtyToDeliver.signum() > 0) {
                    final int setupTimeReal = node.getSetupTimeRequired() - node.getSetupTimeReal();
                    final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(node.getAD_Client_ID());
                    final BigDecimal durationReal = routingService.estimateWorkingTime(node, qtyToDeliver);
                    MPPCostCollector.createCollector(order, order.getM_Product_ID(), order.getM_Locator_ID(), order.getM_AttributeSetInstance_ID(), node.getS_Resource_ID(), 0, node.get_ID(), MDocType.getDocType("MCC"), "160", movementDate, qtyToDeliver, Env.ZERO, Env.ZERO, setupTimeReal, durationReal, Env.ZERO, Env.ZERO, Env.ZERO);
                    node.load(order.get_TrxName(), new String[0]);
                    node.closeIt();
                    node.saveEx();
                }
            }
            else if ("CO".equals(node.getDocStatus()) || "IP".equals(node.getDocStatus())) {
                node.closeIt();
                node.saveEx();
            }
        }
        this.m_nodes = null;
    }
    
    public void voidActivities() {
        MPPOrderNode[] nodes = null;
        for (int length = (nodes = this.getNodes((boolean)(1 != 0), this.getAD_Client_ID())).length, i = 0; i < length; ++i) {
            final MPPOrderNode node = nodes[i];
            final BigDecimal old = node.getQtyRequired();
            if (old.signum() != 0) {
                node.addDescription(String.valueOf(Msg.getMsg(this.getCtx(), "Voided")) + " (" + old + ")");
                node.voidIt();
                node.saveEx();
            }
        }
    }
    
    public MPPOrder getMPPOrder() {
        if (this.m_order == null) {
            this.m_order = new MPPOrder(this.getCtx(), this.getPP_Order_ID(), this.get_TrxName());
        }
        return this.m_order;
    }
}
