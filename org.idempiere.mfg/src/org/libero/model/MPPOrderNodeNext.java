// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.util.DB;
import org.compiere.wf.MWFNodeNext;
import org.compiere.model.PO;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_PP_Order_NodeNext;

public class MPPOrderNodeNext extends X_PP_Order_NodeNext
{
    private static final long serialVersionUID = 1L;
    public Boolean m_fromSplitAnd;
    public Boolean m_toJoinAnd;
    
    public MPPOrderNodeNext(final Properties ctx, final int PP_OrderNodeNext_ID, final String trxName) {
        super(ctx, PP_OrderNodeNext_ID, trxName);
        this.m_fromSplitAnd = null;
        this.m_toJoinAnd = null;
        if (PP_OrderNodeNext_ID == 0) {
            this.setEntityType("U");
            this.setIsStdUserWorkflow(false);
            this.setSeqNo(10);
        }
    }
    
    public MPPOrderNodeNext(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_fromSplitAnd = null;
        this.m_toJoinAnd = null;
    }
    
    public MPPOrderNodeNext(final MPPOrderNode parent, final int PP_Order_Next_ID) {
        this(parent.getCtx(), 0, parent.get_TrxName());
        this.setClientOrg((PO)parent);
        this.setPP_Order_ID(parent.getPP_Order_ID());
        this.setPP_Order_Node_ID(parent.get_ID());
        this.setPP_Order_Next_ID(PP_Order_Next_ID);
    }
    
    public MPPOrderNodeNext(final MWFNodeNext wfNodeNext, final MPPOrderNode parent) {
        this(parent, 0);
        this.setAD_WF_Node_ID(wfNodeNext.getAD_WF_Node_ID());
        this.setAD_WF_Next_ID(wfNodeNext.getAD_WF_Next_ID());
        this.setDescription(wfNodeNext.getDescription());
        this.setEntityType(wfNodeNext.getEntityType());
        this.setIsStdUserWorkflow(wfNodeNext.isStdUserWorkflow());
        this.setSeqNo(wfNodeNext.getSeqNo());
        this.setTransitionCode(wfNodeNext.getTransitionCode());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MPPOrderNodeNext[");
        sb.append(this.getSeqNo()).append(":Node=").append(this.getPP_Order_Node_ID()).append("->Next=").append(this.getPP_Order_Next_ID());
        if (this.getDescription() != null && this.getDescription().length() > 0) {
            sb.append(",").append(this.getDescription());
        }
        sb.append("]");
        return sb.toString();
    }
    
    public boolean isFromSplitAnd() {
        return this.m_fromSplitAnd != null && this.m_fromSplitAnd;
    }
    
    public void setFromSplitAnd(final boolean fromSplitAnd) {
        this.m_fromSplitAnd = new Boolean(fromSplitAnd);
    }
    
    public boolean isToJoinAnd() {
        if (this.m_toJoinAnd == null && this.getPP_Order_Next_ID() != 0) {
            final MPPOrderNode next = MPPOrderNode.get(this.getCtx(), this.getPP_Order_Next_ID(), this.get_TrxName());
            this.setToJoinAnd("A".equals(next.getJoinElement()));
        }
        return this.m_toJoinAnd != null && this.m_toJoinAnd;
    }
    
    private void setToJoinAnd(final boolean toJoinAnd) {
        this.m_toJoinAnd = new Boolean(toJoinAnd);
    }
    
    public void setPP_Order_Next_ID() {
        final int id = DB.getSQLValueEx(this.get_TrxName(), "SELECT PP_Order_Node_ID FROM PP_Order_Node  WHERE PP_Order_ID=? AND AD_WF_Node_ID=? AND AD_Client_ID=?", new Object[] { this.getPP_Order_ID(), this.getAD_WF_Next_ID(), this.getAD_Client_ID() });
        this.setPP_Order_Next_ID((id > 0) ? id : 0);
    }
}
