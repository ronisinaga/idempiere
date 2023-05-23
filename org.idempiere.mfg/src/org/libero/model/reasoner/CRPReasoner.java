// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.reasoner;

import org.compiere.model.POResultSet;
import org.compiere.util.DB;
import org.compiere.util.TimeUtil;
import org.compiere.model.MResourceUnAvailable;
import org.libero.model.MPPOrderNode;
import org.libero.model.MPPOrder;
import java.util.ArrayList;
import org.compiere.model.Query;
import org.compiere.model.MResourceType;
import java.util.List;
import org.compiere.model.I_S_Resource;
import java.sql.Timestamp;
import org.compiere.util.Env;
import org.compiere.model.PO;
import java.util.Properties;

public class CRPReasoner
{
    public Properties getCtx() {
        return this.getCtx(null);
    }
    
    private Properties getCtx(final Object o) {
        if (o instanceof PO) {
            return ((PO)o).getCtx();
        }
        return Env.getCtx();
    }
    
    private String getSQLDayRestriction(final Timestamp dateTime, final I_S_Resource r, final List<Object> params) {
        final MResourceType rt = MResourceType.get(this.getCtx(), r.getS_ResourceType_ID());
        final Timestamp dayStart = rt.getDayStart(dateTime);
        final Timestamp dayEnd = rt.getDayEnd(dateTime);
        String whereClause = "(DateStartSchedule<=? AND DateFinishSchedule>=? AND DateFinishSchedule<=?)";
        params.add(dayStart);
        params.add(dayStart);
        params.add(dayEnd);
        whereClause = String.valueOf(whereClause) + " OR (DateStartSchedule>=? AND DateStartSchedule<=? AND DateFinishSchedule>=? AND DateFinishSchedule<=?)";
        params.add(dayStart);
        params.add(dayEnd);
        params.add(dayStart);
        params.add(dayEnd);
        whereClause = String.valueOf(whereClause) + " OR (DateStartSchedule>=? AND DateStartSchedule<=? AND DateFinishSchedule>=?)";
        params.add(dayStart);
        params.add(dayEnd);
        params.add(dayEnd);
        whereClause = String.valueOf(whereClause) + " OR (DateStartSchedule<=? AND DateFinishSchedule>=?)";
        params.add(dayStart);
        params.add(dayEnd);
        return "(" + whereClause + ")";
    }
    
    public Query getPPOrdersNotCompletedQuery(final int S_Resource_ID, final String trxName) {
        final ArrayList<Object> params = new ArrayList<Object>();
        final StringBuffer whereClause = new StringBuffer();
        whereClause.append("AD_Client_ID=?");
        params.add(Env.getAD_Client_ID(this.getCtx()));
        whereClause.append(" AND ").append("DocStatus").append(" NOT IN (?,?,?)");
        params.add("VO");
        params.add("RE");
        params.add("CL");
        if (S_Resource_ID > 0) {
            whereClause.append(" AND ").append("S_Resource_ID").append("=?");
            params.add(S_Resource_ID);
        }
        return new Query(this.getCtx(), "PP_Order", whereClause.toString(), trxName).setParameters((List)params).setOnlyActiveRecords(true).setOrderBy("DatePromised");
    }
    
    public MPPOrder[] getPPOrders(final Timestamp dateTime, final I_S_Resource r) {
        if (!this.isAvailable(r, dateTime)) {
            return new MPPOrder[0];
        }
        final ArrayList<Object> params = new ArrayList<Object>();
        params.add(r.getS_Resource_ID());
        final String whereClause = "EXISTS (SELECT 1 FROM PP_Order_Node WHERE  PP_Order_Node.PP_Order_ID=PP_Order.PP_Order_ID AND S_Resource_ID=? AND " + this.getSQLDayRestriction(dateTime, r, params) + ")" + " AND AD_Client_ID=?";
        params.add(r.getAD_Client_ID());
        final List<MPPOrder> list = new Query(this.getCtx(r), "PP_Order", whereClause, (String)null).setParameters((List)params).list();
        return list.toArray(new MPPOrder[list.size()]);
    }
    
    public MPPOrderNode[] getPPOrderNodes(final Timestamp dateTime, final I_S_Resource r) {
        if (!this.isAvailable(r, dateTime)) {
            return new MPPOrderNode[0];
        }
        final ArrayList<Object> params = new ArrayList<Object>();
        String whereClause = "S_Resource_ID=? AND AD_Client_ID=?";
        params.add(r.getS_Resource_ID());
        params.add(r.getAD_Client_ID());
        whereClause = String.valueOf(whereClause) + " AND " + this.getSQLDayRestriction(dateTime, r, params);
        final List<MPPOrderNode> list = new Query(this.getCtx(r), "PP_Order_Node", whereClause, (String)null).setParameters((List)params).list();
        return list.toArray(new MPPOrderNode[list.size()]);
    }
    
    public boolean isAvailable(final I_S_Resource r, final Timestamp dateTime) {
        final MResourceType t = MResourceType.get(this.getCtx(r), r.getS_ResourceType_ID());
        return t.isDayAvailable(dateTime) && !MResourceUnAvailable.isUnAvailable(r, dateTime);
    }
    
    public boolean isAvailable(final I_S_Resource r) {
        final MResourceType t = MResourceType.get(this.getCtx(r), r.getS_ResourceType_ID());
        return t.isAvailable();
    }
    
    private Timestamp getAvailableDate(final MResourceType t, final Timestamp dateTime, final boolean isScheduleBackward) {
        Timestamp date = dateTime;
        final int direction = isScheduleBackward ? -1 : 1;
        int i = 0;
        while (!t.isDayAvailable(date)) {
            date = TimeUtil.addDays(date, direction);
            if (++i >= 7) {
                return date;
            }
        }
        return date;
    }
    
    public Timestamp getAvailableDate(final I_S_Resource r, final Timestamp dateTime, final boolean isScheduleBackward) {
        final MResourceType t = MResourceType.get(this.getCtx(r), r.getS_ResourceType_ID());
        Timestamp date = dateTime;
        final ArrayList<Object> params = new ArrayList<Object>();
        String whereClause;
        String orderByClause;
        int direction;
        if (isScheduleBackward) {
            whereClause = "DateFrom <= ?";
            params.add(date);
            orderByClause = "DateFrom DESC";
            direction = 1;
        }
        else {
            whereClause = "DateTo >= ?";
            params.add(date);
            orderByClause = "DateTo";
            direction = -1;
        }
        whereClause = String.valueOf(whereClause) + " AND S_Resource_ID=? AND AD_Client_ID=?";
        params.add(r.getS_Resource_ID());
        params.add(r.getAD_Client_ID());
        final POResultSet<MResourceUnAvailable> rs = new Query(this.getCtx(r), "S_ResourceUnAvailable", whereClause, (String)null).setOrderBy(orderByClause).setParameters((List)params).scroll();
        try {
            while (rs.hasNext()) {
                final MResourceUnAvailable rua = (MResourceUnAvailable)rs.next();
                if (rua.isUnAvailable(date)) {
                    date = TimeUtil.addDays(rua.getDateTo(), 1 * direction);
                }
                date = this.getAvailableDate(t, dateTime, isScheduleBackward);
            }
        }
        finally {
            DB.close((POResultSet)rs);
        }
        DB.close((POResultSet)rs);
        date = this.getAvailableDate(t, dateTime, isScheduleBackward);
        return date;
    }
}
