// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.model.MResourceAssignment;
import java.util.Properties;
import java.sql.Timestamp;
import org.compiere.model.I_S_Resource;
import org.compiere.model.I_AD_Workflow;
import org.libero.tables.I_PP_Cost_Collector;
import org.libero.tables.I_PP_Order_Node;
import java.math.BigDecimal;
import org.compiere.model.I_AD_WF_Node;

public interface RoutingService
{
    BigDecimal estimateWorkingTime(final I_AD_WF_Node p0);
    
    BigDecimal estimateWorkingTime(final I_PP_Order_Node p0, final BigDecimal p1);
    
    BigDecimal estimateWorkingTime(final I_PP_Cost_Collector p0);
    
    BigDecimal calculateDuration(final MPPMRP p0, final I_AD_Workflow p1, final I_S_Resource p2, final BigDecimal p3, final Timestamp p4);
    
    MResourceAssignment createResourceAssign(final MPPMRP p0, final Properties p1, final BigDecimal p2, final I_AD_WF_Node p3, final Timestamp p4, final Timestamp p5);
    
    long calculateMillisFor(final MPPOrderNode p0, final long p1);
    
    long calculateMillisFor(final I_AD_WF_Node p0, final long p1, final BigDecimal p2);
    
    BigDecimal getResourceBaseValue(final int p0, final I_PP_Cost_Collector p1);
    
    BigDecimal getResourceBaseValue(final int p0, final I_AD_WF_Node p1);
    
    Timestamp getStartAssignTime();
}
