// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.adempiere.webui.panel.ADForm;
import java.awt.image.BufferedImage;
import org.zkoss.image.AImage;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.ChartRenderingInfo;
import java.sql.Timestamp;
import org.compiere.model.MUOM;
import org.compiere.model.MResource;
import org.zkoss.zk.ui.event.Event;
import org.jfree.chart.JFreeChart;
import org.zkoss.zul.Row;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.adempiere.webui.component.Rows;
import org.compiere.util.Msg;
import java.util.logging.Level;
import org.zkoss.zul.South;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;
import org.compiere.model.MLookup;
import java.util.Properties;
import org.compiere.model.Lookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MColumn;
import org.compiere.util.Env;
import org.adempiere.webui.session.SessionManager;
import org.zkoss.zk.ui.Component;
import org.adempiere.webui.component.GridFactory;
import org.libero.form.crp.CRPModel;
import org.zkoss.zul.Image;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.editor.WSearchEditor;
import java.util.Hashtable;
import org.adempiere.webui.component.ConfirmPanel;
import org.zkoss.zul.Hbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.panel.CustomForm;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.IFormController;

public class WCRP extends CRP implements IFormController, EventListener
{
    CustomForm m_frame;
    Borderlayout mainLayout;
    private Grid northPanel;
    private Hbox centerPanel;
    private Borderlayout centerLayout;
    private ConfirmPanel confirmPanel;
    private Hashtable hash;
    private WSearchEditor resource;
    private Label resourceLabel;
    private WDateEditor dateFrom;
    private Label dateFromLabel;
    private Hbox chartPanel;
    private Image chart;
    protected CRPModel model;
    
    public WCRP() {
        this.m_frame = new CustomForm();
        this.mainLayout = new Borderlayout();
        this.northPanel = GridFactory.newGridLayout();
        this.centerPanel = new Hbox();
        this.centerLayout = new Borderlayout();
        this.confirmPanel = new ConfirmPanel(true);
        this.hash = new Hashtable();
        this.resource = null;
        this.resourceLabel = new Label();
        this.dateFrom = new WDateEditor("DateFrom", true, false, true, "DateFrom");
        this.dateFromLabel = new Label();
        this.chartPanel = new Hbox();
        this.chart = new Image();
        this.m_frame.setWidth("99%");
        this.m_frame.setHeight("100%");
        this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
        this.m_frame.appendChild((Component)this.mainLayout);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("100%");
        this.mainLayout.setStyle("position: absolute");
        this.init();
    }
    
    public void dispose() {
        SessionManager.getAppDesktop().closeWindow(this.m_WindowNo);
    }
    
    private void fillPicks() throws Exception {
        final Properties ctx = Env.getCtx();
        final MLookup resourceL = MLookupFactory.get(ctx, this.m_WindowNo, 0, MColumn.getColumn_ID("M_Product", "S_Resource_ID"), 19);
        this.resource = new WSearchEditor("S_Resource_ID", false, false, true, (Lookup)resourceL);
    }
    
    public CustomForm getForm() {
        return this.m_frame;
    }
    
    public void init() {
        try {
            this.fillPicks();
            this.jbInit();
            final North north = new North();
            north.appendChild((Component)this.northPanel);
            this.mainLayout.appendChild((Component)north);
            final Center center = new Center();
            center.appendChild((Component)this.centerPanel);
            this.mainLayout.appendChild((Component)center);
            final South south = new South();
            south.appendChild((Component)this.confirmPanel);
            this.mainLayout.appendChild((Component)south);
        }
        catch (Exception e) {
            WCRP.log.log(Level.SEVERE, "VCRP.init", (Throwable)e);
        }
    }
    
    private void jbInit() throws Exception {
        this.resourceLabel.setText(Msg.translate(Env.getCtx(), "S_Resource_ID"));
        this.dateFromLabel.setText(Msg.translate(Env.getCtx(), "DateFrom"));
        final Rows rows = new Rows();
        Row row = null;
        rows.setParent((Component)this.northPanel);
        row = (Row)rows.newRow();
        row.appendChild(this.resourceLabel.rightAlign());
        row.appendChild((Component)this.resource.getComponent());
        row.appendChild(this.dateFromLabel.rightAlign());
        row.appendChild((Component)this.dateFrom.getComponent());
        this.centerPanel.appendChild((Component)this.chartPanel);
        final JFreeChart jchart = ChartFactory.createBarChart3D("", Msg.translate(Env.getCtx(), "Days"), Msg.translate(Env.getCtx(), "Hours"), (CategoryDataset)new DefaultCategoryDataset(), PlotOrientation.VERTICAL, true, true, false);
        this.renderChart(jchart);
        this.confirmPanel.addActionListener((EventListener)this);
    }
    
    public void onEvent(final Event event) throws Exception {
        final String cmd = event.getTarget().getId();
        if (cmd.equals("Ok")) {
            Timestamp date = null;
            if (this.dateFrom.getValue() != null) {
                date = this.dateFrom.getValue();
            }
            int S_Resource_ID = 0;
            if (this.resource.getValue() != null) {
                S_Resource_ID = (int)this.resource.getValue();
            }
            if (date != null && S_Resource_ID != 0) {
                final MResource r = MResource.get(Env.getCtx(), S_Resource_ID);
                final int uom_id = r.getResourceType().getC_UOM_ID();
                final MUOM uom = MUOM.get(Env.getCtx(), uom_id);
                CategoryDataset dataset = null;
                if (uom.isHour()) {
                    dataset = this.createDataset(date, r);
                }
                else {
                    dataset = this.createWeightDataset(date, r);
                }
                String title = (r.getName() != null) ? r.getName() : "";
                title = ((String.valueOf(title) + " " + r.getDescription() != null) ? r.getDescription() : "");
                final JFreeChart jfreechart = this.createChart(dataset, title, uom);
                this.renderChart(jfreechart);
            }
        }
        if (cmd.equals("Cancel")) {
            this.dispose();
        }
    }
    
    private void renderChart(final JFreeChart jchart) {
        final BufferedImage bi = jchart.createBufferedImage(700, 500, 3, (ChartRenderingInfo)null);
        try {
            final byte[] bytes = EncoderUtil.encode(bi, "png", true);
            final AImage image = new AImage("", bytes);
            this.chartPanel.removeChild((Component)this.chart);
            (this.chart = new Image()).setContent((org.zkoss.image.Image)image);
            this.chartPanel.appendChild((Component)this.chart);
            this.chartPanel.setVisible(true);
        }
        catch (Exception ex) {}
    }
}
