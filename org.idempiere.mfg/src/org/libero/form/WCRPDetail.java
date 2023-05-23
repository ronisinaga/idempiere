// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.adempiere.webui.panel.ADForm;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import org.zkoss.image.AImage;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.ChartRenderingInfo;
import java.util.Iterator;
import java.util.List;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeitemRenderer;
import org.adempiere.webui.component.SimpleTreeModel;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import java.util.Collection;
import org.zkoss.zul.DefaultTreeNode;
import java.util.ArrayList;
import org.jfree.chart.JFreeChart;
import org.compiere.model.MResource;
import java.sql.Timestamp;
import org.libero.form.crp.CRPDatasetFactory;
import org.compiere.model.MLookup;
import java.util.Properties;
import org.compiere.model.Lookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MColumn;
import org.adempiere.webui.component.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.North;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Label;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Rows;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.libero.form.crp.CRPModel;
import org.libero.tools.worker.SingleWorker;
import org.adempiere.webui.component.Borderlayout;
import org.zkoss.zul.West;
import org.zkoss.zul.Center;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Image;
import org.zkoss.zul.Hbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.panel.CustomForm;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.IFormController;

public class WCRPDetail extends CRPDetail implements IFormController, EventListener
{
    CustomForm m_frame;
    private WSearchEditor resource;
    private WDateEditor dateFrom;
    private WDateEditor dateTo;
    private Hbox chartPanel;
    private Image chart;
    private Hbox treePanel;
    private Tree tree;
    private Center center;
    private West west;
    private Borderlayout mainLayout;
    private SingleWorker worker;
    protected CRPModel model;
    
    public void onEvent(final Event event) throws Exception {
        final String cmd = event.getTarget().getId();
        if (cmd.equals("Ok")) {
            this.handleActionEvent(event);
        }
        if (cmd.equals("Cancel")) {
            this.dispose();
        }
    }
    
    public WCRPDetail() {
        this.m_frame = new CustomForm();
        this.chartPanel = new Hbox();
        this.chart = new Image();
        this.treePanel = new Hbox();
        this.tree = new Tree();
        this.center = new Center();
        this.west = new West();
        this.mainLayout = new Borderlayout();
        this.m_frame.setWidth("99%");
        this.m_frame.setHeight("100%");
        this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
        this.m_frame.appendChild((Component)this.mainLayout);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("100%");
        this.mainLayout.setStyle("position: absolute");
        this.init();
    }
    
    public void init() {
        this.fillPicks();
        this.jbInit();
    }
    
    private void jbInit() {
        this.dateFrom = new WDateEditor("DateFrom", true, false, true, "DateFrom");
        this.dateTo = new WDateEditor("DateTo", true, false, true, "DateTo");
        final Rows rows = new Rows();
        Row row = null;
        new GridFactory();
        final Grid northPanel = GridFactory.newGridLayout();
        rows.setParent((Component)northPanel);
        row = (Row)rows.newRow();
        row.appendChild(new Label(Msg.translate(Env.getCtx(), "S_Resource_ID")).rightAlign());
        row.appendChild((Component)this.resource.getComponent());
        row.appendChild(new Label(Msg.translate(Env.getCtx(), "DateFrom")).rightAlign());
        row.appendChild((Component)this.dateFrom.getComponent());
        row.appendChild(new Label(Msg.translate(Env.getCtx(), "DateTo")).rightAlign());
        row.appendChild((Component)this.dateTo.getComponent());
        final ConfirmPanel confirmPanel = new ConfirmPanel(true);
        confirmPanel.addActionListener((EventListener)this);
        final North north = new North();
        north.appendChild((Component)northPanel);
        this.mainLayout.appendChild((Component)north);
        final South south = new South();
        south.appendChild((Component)confirmPanel);
        this.mainLayout.appendChild((Component)south);
    }
    
    private void fillPicks() {
        final Properties ctx = Env.getCtx();
        final MLookup resourceL = MLookupFactory.get(ctx, 0, 0, MColumn.getColumn_ID("S_Resource", "S_Resource_ID"), 19);
        this.resource = new WSearchEditor("S_Resource_ID", false, false, true, (Lookup)resourceL);
    }
    
    private void handleActionEvent(final Event e) {
        final Timestamp df = this.getDateFrom();
        final Timestamp dt = this.getDateTo();
        final MResource r = this.getResource(this.resource.getValue());
        if (df != null && dt != null && r != null) {
            this.model = CRPDatasetFactory.get(df, dt, r);
            final JFreeChart jfreechart = this.createChart(this.model.getDataset(), this.getChartTitle(), this.getSourceUOM(this.resource.getValue()));
            this.renderChart(jfreechart);
            this.tree = this.getTree();
            this.mainLayout.removeChild((Component)this.center);
            (this.treePanel = new Hbox()).appendChild((Component)this.tree);
            this.tree.setStyle("border: none");
            (this.center = new Center()).appendChild((Component)this.treePanel);
            this.center.setAutoscroll(true);
            this.mainLayout.appendChild((Component)this.center);
        }
    }
    
    private Tree getTree() {
        final Tree tree = new Tree();
        final List<String> nodes = (List<String>)this.model.getDataset().getColumnKeys();
        final DefaultTreeNode root = new DefaultTreeNode((Object)this.getResource(this.resource.getValue()).getName(), (Collection)new ArrayList());
        for (final String node : nodes) {
            root.getChildren().add(new DefaultTreeNode((Object)node, (Collection)new ArrayList()));
        }
        final Treecols treeCols = new Treecols();
        tree.appendChild((Component)treeCols);
        final Treecol treeCol = new Treecol();
        treeCols.appendChild((Component)treeCol);
        final SimpleTreeModel model = new SimpleTreeModel(root);
        tree.setPageSize(-1);
        tree.setTreeitemRenderer((TreeitemRenderer)model);
        tree.setModel((TreeModel)model);
        return tree;
    }
    
    private String getChartTitle() {
        final MResource r = this.getResource(this.resource.getValue());
        String title = (r.getName() != null) ? r.getName() : "";
        title = ((String.valueOf(title) + " " + r.getDescription() != null) ? r.getDescription() : "");
        return title;
    }
    
    public Timestamp getDateFrom() {
        Timestamp t = null;
        if (this.dateFrom.getValue() != null) {
            t = this.dateFrom.getValue();
        }
        return t;
    }
    
    public Timestamp getDateTo() {
        Timestamp t = null;
        if (this.dateTo.getValue() != null) {
            t = this.dateTo.getValue();
        }
        return t;
    }
    
    private void renderChart(final JFreeChart jchart) {
        final BufferedImage bi = jchart.createBufferedImage(700, 500, 3, (ChartRenderingInfo)null);
        try {
            final byte[] bytes = EncoderUtil.encode(bi, "png", true);
            final AImage image = new AImage("", bytes);
            this.mainLayout.removeChild((Component)this.west);
            this.chartPanel = new Hbox();
            (this.chart = new Image()).setContent((org.zkoss.image.Image)image);
            this.chartPanel.appendChild((Component)this.chart);
            (this.west = new West()).appendChild((Component)this.chartPanel);
            this.west.setSplittable(true);
            this.west.setSize("70%");
            this.west.setAutoscroll(true);
            this.west.setOpen(true);
            this.mainLayout.appendChild((Component)this.west);
        }
        catch (Exception e) {
            WCRPDetail.log.log(Level.SEVERE, "WCRP.init", (Object)e.getMessage());
        }
    }
    
    public void dispose() {
        this.m_frame.dispose();
    }
    
    public CustomForm getForm() {
        return this.m_frame;
    }
}
