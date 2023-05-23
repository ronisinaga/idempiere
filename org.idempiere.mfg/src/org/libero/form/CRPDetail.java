// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.compiere.model.MResource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer3D;
import java.awt.Paint;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import org.jfree.chart.JFreeChart;
import org.compiere.model.MUOM;
import org.jfree.data.category.CategoryDataset;
import org.compiere.util.CLogger;
import org.libero.tools.worker.SingleWorker;

public class CRPDetail
{
    public SingleWorker worker;
    public static CLogger log;
    
    static {
        CRPDetail.log = CLogger.getCLogger((Class)CRPDetail.class);
    }
    
    public JFreeChart createChart(final CategoryDataset dataset, final String title, final MUOM uom) {
        final JFreeChart chart = ChartFactory.createBarChart3D(title, Msg.translate(Env.getCtx(), "Day"), Msg.translate(Env.getCtx(), (uom == null) ? "" : uom.getName()), dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint((Paint)Color.WHITE);
        chart.setAntiAlias(true);
        chart.setBorderVisible(true);
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint((Paint)Color.GRAY);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint((Paint)Color.GRAY);
        final BarRenderer3D barrenderer = (BarRenderer3D)plot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator)new LabelGenerator());
        barrenderer.setBaseItemLabelsVisible(true);
        barrenderer.setSeriesPaint(0, (Paint)new Color(10, 80, 150, 128));
        barrenderer.setSeriesPaint(1, (Paint)new Color(180, 60, 50, 128));
        final ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER);
        barrenderer.setPositiveItemLabelPosition(itemlabelposition);
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235987755982988));
        return chart;
    }
    
    public MUOM getSourceUOM(final Object value) {
        final MResource r = this.getResource(value);
        final int uom_id = r.getResourceType().getC_UOM_ID();
        return (uom_id > 0) ? MUOM.get(Env.getCtx(), uom_id) : null;
    }
    
    public MResource getResource(final Object value) {
        MResource r = null;
        if (value != null) {
            r = MResource.get(Env.getCtx(), (int)value);
        }
        return r;
    }
    
    public MUOM getTargetUOM(final Object value) {
        MUOM u = null;
        if (value != null) {
            u = MUOM.get(Env.getCtx(), (int)value);
        }
        return u;
    }
    
    class LabelGenerator extends StandardCategoryItemLabelGenerator
    {
        public String generateItemLabel(final CategoryDataset categorydataset, final int i, final int j) {
            return categorydataset.getRowKey(i).toString();
        }
    }
}
