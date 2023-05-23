// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import org.compiere.model.MResourceType;
import org.compiere.util.DB;
import org.compiere.util.TimeUtil;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.libero.model.MPPOrderNode;

import java.sql.SQLException;
import java.sql.Timestamp;
import org.libero.model.MPPOrderWorkflow;
import java.math.BigDecimal;
import org.compiere.model.I_S_Resource;
import org.compiere.model.MResource;
import org.compiere.model.MResourceAssignment;
import org.libero.tables.I_PP_Order_Node;
import org.compiere.model.Query;
import org.libero.model.MPPMRP;
import java.util.ArrayList;
import java.util.Iterator;
import org.eevolution.model.I_PP_Order;
import org.libero.exceptions.CRPException;
import org.libero.model.MPPOrder;
import org.libero.model.RoutingServiceFactory;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.model.POResultSet;

import java.util.logging.Level;

import javax.sql.RowSet;

import org.libero.model.reasoner.CRPReasoner;
import org.libero.model.RoutingService;
import org.compiere.process.SvrProcess;

public class CRP extends SvrProcess
{
    public static final String FORWARD_SCHEDULING = "F";
    public static final String BACKWARD_SCHEDULING = "B";
    private int p_S_Resource_ID;
    private String p_ScheduleType;
    private int p_MaxIterationsNo;
    public static final String SYSCONFIG_MaxIterationsNo = "CRP.MaxIterationsNo";
    public static final int DEFAULT_MaxIterationsNo = 1000;
    public RoutingService routingService;
    private CRPReasoner reasoner;
    int mTotalPPOrder;
    int mSkippedPPOrder;
    int mProcessedPPOrder;
    
    public CRP() {
        this.p_MaxIterationsNo = -1;
        this.routingService = null;
    }
    
    protected void prepare() {
        ProcessInfoParameter[] parameter;
        for (int length = (parameter = this.getParameter()).length, i = 0; i < length; ++i) {
            final ProcessInfoParameter para = parameter[i];
            final String name = para.getParameterName();
            if (para.getParameter() == null) {}
            if (name.equals("S_Resource_ID")) {
                this.p_S_Resource_ID = para.getParameterAsInt();
            }
            else if (name.equals("ScheduleType")) {
                this.p_ScheduleType = (String)para.getParameter();
            }
            else {
                this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
            }
        }
        this.p_MaxIterationsNo = MSysConfig.getIntValue("CRP.MaxIterationsNo", 1000, this.getAD_Client_ID());
    }
    
    protected String doIt() throws Exception {
        this.reasoner = new CRPReasoner();
        this.routingService = RoutingServiceFactory.get().getRoutingService(this.getAD_Client_ID());
        return this.runCRP();
    }
    
    private String runCRP() {
        this.mTotalPPOrder = 0;
        this.mSkippedPPOrder = 0;
        this.mProcessedPPOrder = 0;
        
        String sql;
        try {
        	sql = "delete from s_timeexpenseline"
        			+ " where s_resourceassignment_id in "
        			+ "(select s_resourceassignment_id from s_resourceassignment sr where not exists (select 1 from pp_order po where po.documentno = right(sr.name,5) and po.docstatus = 'IP'))";
        	DB.executeUpdateEx(sql, get_TrxName());
        	
        	sql = "delete from s_resourceassignment sr"
            		+ " where not exists (select 1 from pp_order po where po.documentno = right(sr.name,5) and po.docstatus = 'IP')";				
    		DB.executeUpdateEx(sql, get_TrxName());
			commitEx();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        final Iterator<MPPOrder> it = this.reasoner.getPPOrdersNotCompletedQuery(this.p_S_Resource_ID, this.get_TrxName()).iterate();
        while (it.hasNext()) {
        	final MPPOrder order = it.next();
            try {
                this.runCRP(order);
            }
            catch (Exception e) {
                if (e instanceof CRPException) {
                    final CRPException crpEx = (CRPException)e;
                    crpEx.setPP_Order((I_PP_Order)order);
                    throw crpEx;
                }
                final CRPException crpEx = new CRPException(e);
                throw crpEx;
            }
        }
        
        //>>astina
        try {
        	sql = "SELECT pp_setleadtime()";
        	int result = DB.executeUpdate(sql, get_TrxName());
			commitEx();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      //<<astina
        
        return "Total Orders: " + Integer.toString(this.mTotalPPOrder) + " Processed: " + Integer.toString(this.mProcessedPPOrder) + " Skip: " + Integer.toString(this.mSkippedPPOrder);
    }
    
    public void runCRP(final MPPOrder order) {
        ++this.mTotalPPOrder;
        this.log.info("PP_Order DocumentNo:" + order.getDocumentNo());
        final MPPOrderWorkflow owf = order.getMPPOrderWorkflow();
        if (owf == null) {
            this.addLog("WARNING: No workflow found - " + order);
            return;
        }
        this.log.info("PP_Order Workflow:" + owf.getName());
        final ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
        String whereClause = "PP_Order_ID=? AND AD_Client_ID=? AND OrderType = ?";
        final RoutingService routingService = RoutingServiceFactory.get().getRoutingService(this.getCtx());
        MPPMRP mrp = (MPPMRP)new Query(this.getCtx(), "PP_MRP", whereClause, this.get_TrxName()).setParameters(new Object[] { order.get_ID(), order.getAD_Client_ID(), "SOO" }).firstOnly();
        if (mrp == null) {
            whereClause = String.valueOf(whereClause) + " AND " + "TypeMRP" + "=?";
            mrp = (MPPMRP)new Query(this.getCtx(), "PP_MRP", whereClause, this.get_TrxName()).setParameters(new Object[] { order.get_ID(), order.getAD_Client_ID(), "MOP", "S" }).firstOnly();
        }
        if (mrp == null) {
            this.log.info("MRP Order of PP Order " + order.getDocumentNo() + " not found !!!");
            ++this.mSkippedPPOrder;
            return;
        }
        ++this.mProcessedPPOrder;
        
        //-->astina
        int custom = 1;
        //0-ori
        //1-custom astina
        //--<
        
        if (this.p_ScheduleType.equals("F")) {
            Timestamp date = order.getDateStartSchedule();
            int nodeId = owf.getPP_Order_Node_ID();
            MPPOrderNode node = null;
            while (nodeId != 0) {
                node = owf.getNode(nodeId);
                if (visitedNodes.contains(nodeId)) {
                    throw new CRPException("Cyclic transition found").setPP_Order_Node(node);
                }
                visitedNodes.add(nodeId);
                this.log.info(("PP_Order Node:" + node.getName() != null) ? node.getName() : ((" Description:" + node.getDescription() != null) ? node.getDescription() : ""));
                final MResource resource = MResource.get(this.getCtx(), node.getS_Resource_ID());
                if (resource == null) {
                    nodeId = owf.getNext(nodeId, this.getAD_Client_ID());
                }
                else {
                    if (!this.reasoner.isAvailable((I_S_Resource)resource)) {
                        throw new CRPException("@ResourceNotInSlotDay@").setS_Resource((I_S_Resource)resource);
                    }
                    final long nodeMillis = routingService.calculateMillisFor(node, owf.getDurationBaseSec());
                    final Timestamp dateFinish = this.scheduleForward(date, nodeMillis, resource);
                    node.setDateStartSchedule(date);
                    node.setDateFinishSchedule(dateFinish);
                    node.saveEx();
                    final BigDecimal duration = BigDecimal.valueOf(nodeMillis * 1000L * 60L);
                    routingService.createResourceAssign(mrp, this.getCtx(), duration, node.getAD_WF_Node(), date, dateFinish);
                    date = node.getDateFinishSchedule();
                    nodeId = owf.getNext(nodeId, this.getAD_Client_ID());
                }
            }
            if (node != null && node.getDateFinishSchedule() != null) {
                order.setDateFinishSchedule(node.getDateFinishSchedule());
            }
        }
        //>>astina
        else if (this.p_ScheduleType.equals("B") && custom == 1)
        {
        	if (!this.p_ScheduleType.equals("B")) {
                throw new CRPException("Unknown scheduling method - " + this.p_ScheduleType);
            }
        	
        	int test;
        	if (order.get_ID()==1001236)
        		test = 0;
        	
        	RowSet rs = DB.getRowSet("SELECT * FROM pp_schedulebackward ("+ order.get_ID() + ")");
        	MPPOrderNode node = null;        	
        	try {
				while (rs.next())
				{
					node = owf.getNode(rs.getInt(1));
					node.setDateStartSchedule(rs.getTimestamp(3));
                    node.setDateFinishSchedule(rs.getTimestamp(4));
                    node.saveEx();
                    
                    double durationtotal = 0;
                    double runningcapacity = 0;
                    double duration = 0;
                    Timestamp assigndateto;
                    
                    String sql = "SELECT * FROM pp_resourceassign ("+ order.get_ID() + ", " + rs.getInt(1) + ")";
                    RowSet rs2 = DB.getRowSet(sql);
                 
                    while (rs2.next())
    				{
                    	runningcapacity = runningcapacity + rs2.getDouble(5);
                    	durationtotal = rs2.getDouble(7);
                    	
                    	if (runningcapacity < durationtotal)
                    	{
                    		duration = rs2.getDouble(5);
                    		assigndateto = rs2.getTimestamp(4);
                    	}
                    	else
                    	{
                    		duration = durationtotal - (runningcapacity - rs2.getDouble(5));
                    		assigndateto = TimeUtil.addMinutess(rs2.getTimestamp(3), (int)duration);
                    	}
                    	
                    	MResourceAssignment ra = routingService.createResourceAssign(mrp, getCtx(), BigDecimal.valueOf(duration), 
                        		node.getAD_WF_Node(), rs2.getTimestamp(3), assigndateto);	
    				}
                    
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, "", e);
			}
        	
        	if (node != null && node.getDateStartSchedule() != null) {
                order.setDateStartSchedule(node.getDateStartSchedule());
            }
        }
        //<<astina
        else {
            if (!this.p_ScheduleType.equals("B")) {
                throw new CRPException("Unknown scheduling method - " + this.p_ScheduleType);
            }
            Timestamp date = order.getDateFinishSchedule();
            int nodeId = owf.getNodeLastID(this.getAD_Client_ID());
            MPPOrderNode node = null;
            while (nodeId != 0) {
                node = owf.getNode(nodeId);
                if (visitedNodes.contains(nodeId)) {
                    throw new CRPException("Cyclic transition found - ").setPP_Order_Node(node);
                }
                visitedNodes.add(nodeId);
                this.log.info(("PP_Order Node:" + node.getName() != null) ? node.getName() : ((" Description:" + node.getDescription() != null) ? node.getDescription() : ""));
                final MResource resource = MResource.get(this.getCtx(), node.getS_Resource_ID());
                if (resource == null) {
                    nodeId = owf.getPrevious(nodeId, this.getAD_Client_ID());
                }
                else {
                    if (!this.reasoner.isAvailable((I_S_Resource)resource)) {
                        throw new CRPException("@ResourceNotInSlotDay@").setS_Resource((I_S_Resource)resource);
                    }
                    final long nodeMillis = routingService.calculateMillisFor(node, owf.getDurationBaseSec());
                    
                    final Timestamp dateStart = this.scheduleBackward(date, nodeMillis, resource);
                    node.setDateStartSchedule(dateStart);
                    node.setDateFinishSchedule(date);
                    node.saveEx();
                    
	    			date = node.getDateStartSchedule();
                    nodeId = owf.getPrevious(nodeId, this.getAD_Client_ID());
                }
            }
            if (node != null && node.getDateStartSchedule() != null) {
                order.setDateStartSchedule(node.getDateStartSchedule());
            }
        }
        order.saveEx(this.get_TrxName());
        whereClause = "PP_Order_ID=? AND AD_Client_ID=? AND ( DocStatus=? OR DocStatus=? ) AND ( OrderType=? OR OrderType=?)";
        final List<MPPMRP> mrpset = new Query(this.getCtx(), "PP_MRP", whereClause, this.get_TrxName()).setParameters(new Object[] { order.get_ID(), order.getAD_Client_ID(), "DR", "IP", "SOO", "MOP" }).list();
        for (final MPPMRP mrps : mrpset) {
            mrps.setDateStartSchedule(order.getDateStartSchedule());
            mrps.setDateFinishSchedule(order.getDateFinishSchedule());
            mrps.saveEx(this.get_TrxName());
        }
    }
    
    private long getAvailableDurationMillis(final Timestamp dayStart, final Timestamp dayEnd, final I_S_Resource resource) {
        final long availableDayDuration = dayEnd.getTime() - dayStart.getTime();
        this.log.info("--> availableDayDuration  " + availableDayDuration);
        if (availableDayDuration < 0L) {
            throw new CRPException("@TimeSlotStart@ > @TimeSlotEnd@ (" + dayEnd + " > " + dayStart + ")").setS_Resource(resource);
        }
        return availableDayDuration;
    }
    
    private Timestamp scheduleForward(final Timestamp start, final long nodeDurationMillis, final MResource r) {
        final Calendar cal = Calendar.getInstance();
        final MResourceType t = r.getResourceType();
        int iteration = 0;
        Timestamp currentDate = start;
        Timestamp end = null;
        long remainingMillis = nodeDurationMillis;
        do {
            cal.setTimeInMillis(currentDate.getTime());
            final int hour = cal.get(11);
            final int minute = cal.get(12);
            final int second = cal.get(13);
            currentDate = this.reasoner.getAvailableDate((I_S_Resource)r, currentDate, false);
            cal.setTimeInMillis(currentDate.getTime());
            cal.set(11, hour);
            cal.set(12, minute);
            cal.set(13, second);
            currentDate.setTime(cal.getTimeInMillis());
            Timestamp dayStart = t.getDayStart(currentDate);
            if (iteration == 0 && currentDate.compareTo(dayStart) > 0) {
                dayStart = currentDate;
            }
            final Timestamp dayEnd = t.getDayEnd(currentDate);
            if (currentDate.after(dayStart)) {
                currentDate.before(dayEnd);
            }
            final long availableDayDuration = this.getAvailableDurationMillis(dayStart, dayEnd, (I_S_Resource)r);
            if (availableDayDuration >= remainingMillis) {
                end = new Timestamp(dayStart.getTime() + remainingMillis);
                remainingMillis = 0L;
                break;
            }
            currentDate = TimeUtil.addDays(TimeUtil.getDayBorder(currentDate, (Timestamp)null, false), 1);
            remainingMillis -= availableDayDuration;
            if (++iteration > this.p_MaxIterationsNo) {
                throw new CRPException("Maximum number of iterations exceeded (" + this.p_MaxIterationsNo + ")" + " - Date:" + currentDate + ", RemainingMillis:" + remainingMillis);
            }
        } while (remainingMillis > 0L);
        return end;
    }
    
    private Timestamp scheduleBackward(final Timestamp end, final long nodeDurationMillis, final MResource r) {
        final Calendar cal = Calendar.getInstance();
        final MResourceType t = r.getResourceType();
        this.log.info("--> ResourceType " + t);
        Timestamp start = null;
        Timestamp currentDate = end;
        long remainingMillis = nodeDurationMillis;
        int iteration = 0;
        do {
            this.log.info("--> end=" + currentDate);
            this.log.info("--> nodeDuration=" + remainingMillis);
            cal.setTimeInMillis(currentDate.getTime());
            final int hour = cal.get(11);
            final int minute = cal.get(12);
            final int second = cal.get(13);
            currentDate = this.reasoner.getAvailableDate((I_S_Resource)r, currentDate, true);
            cal.setTimeInMillis(currentDate.getTime());
            cal.set(11, hour);
            cal.set(12, minute);
            cal.set(13, second);
            currentDate.setTime(cal.getTimeInMillis());
            this.log.info("--> end(available)=" + currentDate);
            Timestamp dayEnd = t.getDayEnd(currentDate);
            if (iteration == 0 && currentDate.compareTo(dayEnd) < 0) {
                dayEnd = currentDate;
            }
            final Timestamp dayStart = t.getDayStart(currentDate);
            this.log.info("--> dayStart=" + dayStart + ", dayEnd=" + dayEnd);
            if (currentDate.before(dayEnd)) {
                currentDate.after(dayStart);
            }
            final long availableDayDuration = this.getAvailableDurationMillis(dayStart, dayEnd, (I_S_Resource)r);
            if (availableDayDuration >= remainingMillis) {
                this.log.info("--> availableDayDuration >= nodeDuration true " + availableDayDuration + "|" + remainingMillis);
                start = new Timestamp(dayEnd.getTime() - remainingMillis);
                remainingMillis = 0L;
                break;
            }
            this.log.info("--> availableDayDuration >= nodeDuration false " + availableDayDuration + "|" + remainingMillis);
            this.log.info("--> nodeDuration-availableDayDuration " + (remainingMillis - availableDayDuration));
            currentDate = TimeUtil.addDays(TimeUtil.getDayBorder(currentDate, (Timestamp)null, true), -1);
            remainingMillis -= availableDayDuration;
            if (++iteration > this.p_MaxIterationsNo) {
                throw new CRPException("Maximum number of iterations exceeded (" + this.p_MaxIterationsNo + ")" + " - Date:" + start + ", RemainingMillis:" + remainingMillis);
            }
        } while (remainingMillis > 0L);
        this.log.info("         -->  start=" + start + " <---------------------------------------- ");
        return start;
    }
    
    public Timestamp scheduleBackward(final Timestamp end, final long nodeDurationMillis, final MResource r, final int AD_Client_ID) {
        if (this.reasoner == null) {
            this.reasoner = new CRPReasoner();
        }
        this.p_MaxIterationsNo = MSysConfig.getIntValue("CRP.MaxIterationsNo", 1000, AD_Client_ID);
        return this.scheduleBackward(end, nodeDurationMillis, r);
    }
    
    private Timestamp scheduleBackward1(final Timestamp end, final long nodeDurationMillis, final MResource r) {
        final Calendar cal = Calendar.getInstance();
        final MResourceType t = r.getResourceType();
        this.log.info("--> ResourceType " + t);
        Timestamp start = null;
        Timestamp currentDate = end;
        long remainingMillis = nodeDurationMillis;
        int iteration = 0;
        do {
            this.log.info("--> end=" + currentDate);
            this.log.info("--> nodeDuration=" + remainingMillis);
            cal.setTimeInMillis(currentDate.getTime());
            final int hour = cal.get(11);
            final int minute = cal.get(12);
            final int second = cal.get(13);
            currentDate = this.reasoner.getAvailableDate((I_S_Resource)r, currentDate, true);
            cal.setTimeInMillis(currentDate.getTime());
            cal.set(11, hour);
            cal.set(12, minute);
            cal.set(13, second);
            currentDate.setTime(cal.getTimeInMillis());
            this.log.info("--> end(available)=" + currentDate);
            Timestamp dayEnd = t.getDayEnd(currentDate);
            if (iteration == 0 && currentDate.compareTo(dayEnd) < 0) {
                dayEnd = currentDate;
            }
            final Timestamp dayStart = t.getDayStart(currentDate);
            this.log.info("--> dayStart=" + dayStart + ", dayEnd=" + dayEnd);
            if (currentDate.before(dayEnd)) {
                currentDate.after(dayStart);
            }
            final long availableDayDuration = this.getAvailableDurationMillis(dayStart, dayEnd, (I_S_Resource)r);
            if (availableDayDuration >= remainingMillis) {
                this.log.info("--> availableDayDuration >= nodeDuration true " + availableDayDuration + "|" + remainingMillis);
                start = new Timestamp(dayEnd.getTime() - remainingMillis);
                remainingMillis = 0L;
                break;
            }
            this.log.info("--> availableDayDuration >= nodeDuration false " + availableDayDuration + "|" + remainingMillis);
            this.log.info("--> nodeDuration-availableDayDuration " + (remainingMillis - availableDayDuration));
            currentDate = TimeUtil.addDays(TimeUtil.getDayBorder(currentDate, (Timestamp)null, true), -1);
            remainingMillis -= availableDayDuration;
            if (++iteration > this.p_MaxIterationsNo) {
                throw new CRPException("Maximum number of iterations exceeded (" + this.p_MaxIterationsNo + ")" + " - Date:" + start + ", RemainingMillis:" + remainingMillis);
            }
        } while (remainingMillis > 0L);
        this.log.info("         -->  start=" + start + " <---------------------------------------- ");
        return start;
    }
}
