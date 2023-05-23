// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.libero.form.crp.CRPDatasetFactory;
import java.math.BigDecimal;
import org.compiere.util.DB;
import org.jfree.data.category.DefaultCategoryDataset;
import org.compiere.model.MResourceType;
import java.util.GregorianCalendar;
import org.compiere.model.MResource;
import java.sql.Timestamp;
import org.compiere.util.Msg;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.JFreeChart;
import org.compiere.model.MUOM;
import org.jfree.data.category.CategoryDataset;
import org.compiere.util.Env;
import org.libero.form.crp.CRPModel;
import org.compiere.util.CLogger;

public class CRP
{
    public int m_WindowNo;
    public static CLogger log;
    public int AD_Client_ID;
    protected CRPModel model;
    
    static {
        CRP.log = CLogger.getCLogger((Class)CRP.class);
    }
    
    public CRP() {
        this.m_WindowNo = 0;
        this.AD_Client_ID = Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
    }
    
    protected JFreeChart createChart(final CategoryDataset dataset, final String title, final MUOM uom) {
        JFreeChart chart = ChartFactory.createBarChart3D(title, " ", " ", dataset, PlotOrientation.VERTICAL, true, true, false);
        if (uom == null || uom.isHour()) {
            chart = ChartFactory.createBarChart3D(title, Msg.translate(Env.getCtx(), "Days"), Msg.translate(Env.getCtx(), "Hours"), dataset, PlotOrientation.VERTICAL, true, true, false);
        }
        else {
            chart = ChartFactory.createBarChart3D(title, Msg.translate(Env.getCtx(), "Days"), Msg.translate(Env.getCtx(), "Kilo"), dataset, PlotOrientation.VERTICAL, true, true, false);
        }
        return chart;
    }
    
    protected CategoryDataset createDataset(final Timestamp start, final MResource resource) {
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTimeInMillis(start.getTime());
        gc1.clear(14);
        gc1.clear(13);
        gc1.clear(12);
        gc1.clear(11);
        Timestamp date = start;
        final String namecapacity = Msg.translate(Env.getCtx(), "Capacity");
        final String nameload = Msg.translate(Env.getCtx(), "Load");
        final String namesummary = Msg.translate(Env.getCtx(), "Summary");
        final MResourceType t = MResourceType.get(Env.getCtx(), resource.getS_ResourceType_ID());
        int days = 1;
        final long hours = t.getTimeSlotHours();
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final int C_UOM_ID = DB.getSQLValue((String)null, "SELECT C_UOM_ID FROM M_Product WHERE S_Resource_ID = ? ", resource.getS_Resource_ID());
        final MUOM uom = MUOM.get(Env.getCtx(), C_UOM_ID);
        if (!uom.isHour()) {
            return (CategoryDataset)dataset;
        }
        long summary = 0L;
        while (days < 32) {
            final String day = new String(new Integer(date.getDate()).toString());
            final long HoursLoad = this.getLoad(resource, date).longValue();
            final Long Hours = new Long(hours);
            switch (gc1.get(7)) {
                case 1: {
                    ++days;
                    if (t.isOnSunday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 2: {
                    ++days;
                    if (t.isOnMonday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 3: {
                    ++days;
                    if (t.isOnTuesday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 4: {
                    ++days;
                    if (t.isOnWednesday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 5: {
                    ++days;
                    if (t.isOnThursday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 6: {
                    ++days;
                    if (t.isOnFriday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                case 7: {
                    ++days;
                    if (t.isOnSaturday()) {
                        dataset.addValue((double)hours, (Comparable)namecapacity, (Comparable)day);
                        dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                        dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                        summary = summary + Hours.intValue() - HoursLoad;
                        gc1.add(5, 1);
                        date = new Timestamp(gc1.getTimeInMillis());
                        continue;
                    }
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)day);
                    dataset.addValue((double)HoursLoad, (Comparable)nameload, (Comparable)day);
                    dataset.addValue((double)summary, (Comparable)namesummary, (Comparable)day);
                    summary -= HoursLoad;
                    gc1.add(5, 1);
                    date = new Timestamp(gc1.getTimeInMillis());
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        return (CategoryDataset)dataset;
    }
    
    protected CategoryDataset createWeightDataset(final Timestamp start, final MResource rosource) {
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTimeInMillis(start.getTime());
        gc1.clear(14);
        gc1.clear(13);
        gc1.clear(12);
        gc1.clear(11);
        final String namecapacity = Msg.translate(Env.getCtx(), "Capacity");
        final String nameload = Msg.translate(Env.getCtx(), "Load");
        final String namesummary = Msg.translate(Env.getCtx(), "Summary");
        final String namepossiblecapacity = "Possible Capacity";
        final MResourceType t = MResourceType.get(Env.getCtx(), rosource.getS_ResourceType_ID());
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double currentweight = DB.getSQLValue((String)null, "SELECT SUM( (mo.qtyordered-mo.qtydelivered)*(SELECT mp.weight FROM M_Product mp WHERE  mo.m_product_id=mp.m_product_id )) FROM PP_Order mo WHERE AD_Client_ID=?", rosource.getAD_Client_ID());
        final double dailyCapacity = rosource.getDailyCapacity().doubleValue();
        final double utilization = rosource.getPercentUtilization().doubleValue();
        double summary = 0.0;
        int day = 0;
        while (day < 32) {
            ++day;
            switch (gc1.get(7)) {
                case 1: {
                    if (t.isOnSunday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 2: {
                    if (t.isOnMonday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 3: {
                    if (t.isOnTuesday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 4: {
                    if (t.isOnWednesday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 5: {
                    if (t.isOnThursday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 6: {
                    if (t.isOnFriday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
                case 7: {
                    if (t.isOnSaturday()) {
                        currentweight -= dailyCapacity * utilization / 100.0;
                        summary += dailyCapacity * utilization / 100.0;
                        dataset.addValue(dailyCapacity, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                        dataset.addValue(dailyCapacity * utilization / 100.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                        break;
                    }
                    dataset.addValue(0.0, (Comparable)namepossiblecapacity, (Comparable)new Integer(day));
                    dataset.addValue(0.0, (Comparable)namecapacity, (Comparable)new Integer(day));
                    break;
                }
            }
            dataset.addValue(currentweight, (Comparable)nameload, (Comparable)new Integer(day));
            dataset.addValue(summary, (Comparable)namesummary, (Comparable)new Integer(day));
            gc1.add(5, 1);
        }
        return (CategoryDataset)dataset;
    }
    
    private BigDecimal getLoad(final MResource resource, final Timestamp start) {
        this.model = CRPDatasetFactory.get(start, start, resource);
        return this.model.calculateLoad(start, resource, null);
    }
}
