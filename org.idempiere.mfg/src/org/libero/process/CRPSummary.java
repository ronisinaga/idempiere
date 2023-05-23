// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.libero.tables.X_T_MRP_CRP;
import org.compiere.model.MUOM;
import org.compiere.util.DB;
import java.util.ArrayList;
import org.compiere.model.MResource;
import org.compiere.model.MResourceType;
import java.util.Date;
import java.util.GregorianCalendar;
import org.compiere.process.ProcessInfoParameter;
import java.util.logging.Level;
import java.math.BigDecimal;
import org.compiere.util.Env;
import java.sql.Timestamp;
import org.compiere.process.SvrProcess;

public class CRPSummary extends SvrProcess
{
    private int p_S_Resource_ID;
    private Timestamp p_DateFrom;
    private Timestamp p_DateTo;
    private String p_FrequencyType;
    private int AD_Client_ID;
    private int AD_PInstance_ID;
    
    public CRPSummary() {
        this.p_S_Resource_ID = 0;
        this.p_DateFrom = null;
        this.p_DateTo = null;
        this.p_FrequencyType = null;
        this.AD_Client_ID = 0;
        this.AD_PInstance_ID = 0;
    }
    
    protected void prepare() {
        this.AD_Client_ID = Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
        final ProcessInfoParameter[] para = this.getParameter();
        this.AD_PInstance_ID = this.getAD_PInstance_ID();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("S_Resource_ID")) {
                    this.p_S_Resource_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("DateFrom")) {
                    this.p_DateFrom = (Timestamp)para[i].getParameter();
                }
                else if (name.equals("DateTo")) {
                    this.p_DateTo = (Timestamp)para[i].getParameter();
                }
                else if (name.equals("FrequencyType")) {
                    this.p_FrequencyType = (String)para[i].getParameter();
                }
                else {
                    this.log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                }
            }
        }
    }
    
    protected String doIt() throws Exception {
        return this.runCRP();
    }
    
    protected String runCRP() {
        return "";
    }
    
    public static Timestamp addSecond(Timestamp dateTime, final long offset) {
        if (dateTime == null) {
            dateTime = new Timestamp(System.currentTimeMillis());
        }
        if (offset == 0L) {
            return dateTime;
        }
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dateTime);
        cal.add(13, new Long(offset).intValue());
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public long getHoursAvailable(final Timestamp time1, final Timestamp time2) {
        final GregorianCalendar g1 = new GregorianCalendar();
        g1.setTimeInMillis(time1.getTime());
        final GregorianCalendar g2 = new GregorianCalendar();
        g1.setTimeInMillis(time2.getTime());
        final Date d1 = g1.getTime();
        final Date d2 = g2.getTime();
        final long l1 = d1.getTime();
        final long l2 = d2.getTime();
        final long difference = l2 - l1;
        System.out.println("Elapsed milliseconds: " + difference);
        return difference;
    }
    
    public int getNotAvailbleDays(final Timestamp start, final Timestamp end, final MResourceType t) {
        if (!t.isDateSlot()) {
            return 0;
        }
        final GregorianCalendar g1 = new GregorianCalendar();
        g1.setTimeInMillis(start.getTime());
        final GregorianCalendar g2 = new GregorianCalendar();
        g2.setTimeInMillis(end.getTime());
        GregorianCalendar gc2;
        GregorianCalendar gc3;
        if (g2.after(g1)) {
            gc2 = (GregorianCalendar)g2.clone();
            gc3 = (GregorianCalendar)g1.clone();
        }
        else {
            gc2 = (GregorianCalendar)g1.clone();
            gc3 = (GregorianCalendar)g2.clone();
        }
        gc3.clear(14);
        gc3.clear(13);
        gc3.clear(12);
        gc3.clear(11);
        gc2.clear(14);
        gc2.clear(13);
        gc2.clear(12);
        gc2.clear(11);
        int DaysNotAvialable = 0;
        while (gc3.before(gc2)) {
            gc3.add(5, 1);
            switch (gc3.get(7)) {
                case 1: {
                    if (!t.isOnSunday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 2: {
                    if (!t.isOnMonday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 3: {
                    if (!t.isOnTuesday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 4: {
                    if (!t.isOnWednesday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 5: {
                    if (!t.isOnThursday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 6: {
                    if (!t.isOnFriday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                case 7: {
                    if (!t.isOnSaturday()) {
                        ++DaysNotAvialable;
                        continue;
                    }
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        System.out.println("DaysNotAvialable" + DaysNotAvialable);
        return DaysNotAvialable;
    }
    
    public void Summary(final Timestamp start, final Timestamp finish, final MResource r) {
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTimeInMillis(start.getTime());
        gc1.clear(14);
        gc1.clear(13);
        gc1.clear(12);
        gc1.clear(11);
        final GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTimeInMillis(finish.getTime());
        gc2.clear(14);
        gc2.clear(13);
        gc2.clear(12);
        gc2.clear(11);
        final MResourceType t = MResourceType.get(Env.getCtx(), r.getS_ResourceType_ID());
        long hours = 0L;
        if (t.isTimeSlot()) {
            hours = this.getHoursAvailable(t.getTimeSlotStart(), t.getTimeSlotStart());
        }
        else {
            hours = 24L;
        }
        boolean available = false;
        final ArrayList<Col> list = new ArrayList<Col>();
        int col = 0;
        int summary = 0;
        Col cols = new Col();
        cols.setFrom("Past Due");
        cols.setTo(start.toString());
        cols.setDays(0);
        cols.setCapacity(0);
        cols.setLoad(0);
        cols.setSummary(0);
        list.add(0, cols);
        ++col;
        while (gc1.before(gc2)) {
            gc1.add(5, 1);
            switch (gc1.get(7)) {
                case 1: {
                    if (t.isOnSunday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (t.isOnMonday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (t.isOnTuesday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (t.isOnWednesday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 5: {
                    if (t.isOnThursday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 6: {
                    if (t.isOnFriday()) {
                        available = true;
                        break;
                    }
                    break;
                }
                case 7: {
                    if (t.isOnSaturday()) {
                        available = true;
                        break;
                    }
                    break;
                }
            }
            if (available) {
                cols = new Col();
                cols.setFrom(gc1.getTime().toString());
                cols.setTo(gc1.getTime().toString());
                cols.setDays(1);
                final Long Hours = new Long(hours);
                cols.setCapacity(Hours.intValue());
                final int C_UOM_ID = DB.getSQLValue((String)null, "SELECT C_UOM_ID FROM M_Product WHERE S_Resource_ID = ? ", r.getS_Resource_ID());
                final MUOM oum = MUOM.get(this.getCtx(), C_UOM_ID);
                if (oum.isHour()) {
                    final Timestamp date = new Timestamp(gc1.getTimeInMillis());
                    final int seconds = this.getLoad(r.getS_Resource_ID(), date, date);
                    cols.setLoad(seconds / 3600);
                }
                cols.setSummary(summary + cols.getDifference());
                summary = cols.getSummary();
                list.add(col, cols);
            }
        }
        col = 0;
        boolean newrow = true;
        final Col[] lines = new Col[list.size()];
        for (int z = 0; z <= lines.length; ++z) {}
        for (int i = 0; i <= lines.length; ++i) {
            if (newrow) {
                final X_T_MRP_CRP crp = new X_T_MRP_CRP(this.getCtx(), 0, null);
                crp.setDescription("CRP Resource" + r.getName());
            }
            switch (col) {
                case 0: {
                    ++col;
                }
                case 1: {
                    ++col;
                }
                case 2: {
                    ++col;
                }
                case 3: {
                    ++col;
                }
                case 4: {
                    ++col;
                }
                case 5: {
                    ++col;
                }
                case 6: {
                    ++col;
                }
                case 7: {
                    ++col;
                }
                case 8: {
                    ++col;
                }
                case 9: {
                    ++col;
                }
                case 10: {
                    ++col;
                }
                case 11: {
                    ++col;
                }
                case 12: {
                    col = 0;
                    newrow = true;
                    break;
                }
            }
            ++col;
        }
    }
    
    int getLoad(final int S_Resource_ID, final Timestamp start, final Timestamp end) {
        int load = 0;
        final String sql = "SELECT SUM( CASE WHEN ow.DurationUnit = 's'  THEN 1 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'm' THEN 60 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'h'  THEN 3600 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'Y'  THEN 31536000 *  (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'M' THEN 2592000 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'D' THEN 86400 END ) AS Load FROM PP_Order_Node onode INNER JOIN PP_Order_Workflow ow ON (ow.PP_Order_Workflow_ID =  onode.PP_Order_Workflow_ID) INNER JOIN PP_Order o ON (o.PP_Order_ID = onode.PP_Order_ID)  WHERE onode. = ?  AND onode.DateStartSchedule => ? AND onode.DateFinishSchedule =< ? AND onode.AD_Client_ID = ?";
        try {
            PreparedStatement pstmt = null;
            pstmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            pstmt.setInt(1, S_Resource_ID);
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            pstmt.setInt(3, this.AD_Client_ID);
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                load = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            return load;
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, "doIt - " + sql, (Throwable)e);
            return 0;
        }
    }
    
    private class Col
    {
        int Day;
        String From;
        String To;
        int Capacity;
        int Load;
        int Summary;
        
        public Col() {
            this.Day = 0;
            this.From = null;
            this.To = null;
            this.Capacity = 0;
            this.Load = 0;
            this.Summary = 0;
        }
        
        void setDays(final int day) {
            this.Day = day;
        }
        
        int getDays() {
            return this.Day;
        }
        
        void setCapacity(final int capacity) {
            this.Capacity = capacity;
        }
        
        int getCapacity() {
            return this.Capacity;
        }
        
        void setLoad(final int load) {
            this.Load = load;
        }
        
        int getLoad() {
            return this.Load;
        }
        
        int getDifference() {
            return this.Capacity - this.Load;
        }
        
        void setSummary(final int summary) {
            this.Summary = summary;
        }
        
        int getSummary() {
            return this.Summary;
        }
        
        void setFrom(final String from) {
            this.From = from;
        }
        
        String getFrom() {
            return this.From;
        }
        
        void setTo(final String to) {
            this.To = to;
        }
        
        String getTo() {
            return this.To;
        }
    }
}
