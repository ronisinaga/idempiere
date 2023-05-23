// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.compiere.model.MRole;
import org.compiere.model.MQuery;
import org.compiere.minigrid.IDColumn;
import org.adempiere.webui.event.WTableModelEvent;
import org.zkoss.zul.event.ListDataEvent;
import org.adempiere.webui.apps.AEnv;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.compiere.util.KeyNamePair;
import org.compiere.model.MUOM;
import java.sql.Statement;
import java.sql.SQLException;
import org.adempiere.exceptions.DBException;
import org.compiere.util.DB;
import org.compiere.model.MRefList;
import org.eevolution.model.MPPProductPlanning;
import java.sql.Timestamp;
import org.adempiere.webui.panel.ADForm;
import org.zkoss.zul.Div;
import org.zkoss.zul.South;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;
import org.adempiere.webui.window.WPAttributeInstance;
import org.compiere.model.MWarehouse;
import org.compiere.model.MProduct;
import org.adempiere.webui.component.Row;
import org.compiere.model.MLookup;
import org.zkoss.zul.Space;
import org.zkoss.zk.ui.Component;
import org.adempiere.webui.component.Rows;
import org.zkoss.zk.ui.event.Event;
import org.compiere.model.Lookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MColumn;
import java.util.logging.Level;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.component.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.event.WTableModelListener;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zk.ui.event.EventListener;
import org.adempiere.webui.panel.IFormController;

public class WMRPDetailed extends MRPDetailed implements IFormController, EventListener, ListDataListener, WTableModelListener
{
    private CustomForm m_frame;
    private StatusBarPanel statusBar;
    protected WListbox p_table;
    private Panel panel;
    private Panel southPanel;
    private Borderlayout southLayout;
    ConfirmPanel confirmPanel;
    private Grid parameterPanel;
    private Menupopup popup;
    private Menuitem calcMenu;
    private Label lProduct_ID;
    private WSearchEditor fProduct_ID;
    private Label lAttrSetInstance_ID;
    private Button fAttrSetInstance_ID;
    private Label lResource_ID;
    private WSearchEditor fResource_ID;
    private Label lWarehouse_ID;
    private WSearchEditor fWarehouse_ID;
    private Label lPlanner_ID;
    private WSearchEditor fPlanner_ID;
    private Label lorder;
    private WSearchEditor forder_id;
    private Label lMO_ID;
    private WSearchEditor fMO_ID;
    private Tabbox OrderPlanning;
    private Panel PanelBottom;
    private Panel PanelCenter;
    private Panel PanelFind;
    private Tab PanelOrder;
    private Tab Results;
    private Borderlayout mainLayout;
    private Label lDateFrom;
    private WDateEditor fDateFrom;
    private Label lDateTo;
    private WDateEditor fDateTo;
    private Label lType;
    private Textbox fType;
    private Label lUOM;
    private Textbox fUOM;
    private Label lOrderPeriod;
    private WNumberEditor fOrderPeriod;
    private Label lTimefence;
    private WNumberEditor fTimefence;
    private Label lLeadtime;
    private WNumberEditor fLeadtime;
    private Label lReplenishMin;
    private WNumberEditor fReplenishMin;
    private Label lMinOrd;
    private WNumberEditor fMinOrd;
    private Label lMaxOrd;
    private WNumberEditor fMaxOrd;
    private Label lOrdMult;
    private WNumberEditor fOrdMult;
    private Label lOrderQty;
    private WNumberEditor fOrderQty;
    private Label lYield;
    private WNumberEditor fYield;
    private Label lOnhand;
    private WNumberEditor fOnhand;
    private Label lSafetyStock;
    private WNumberEditor fSafetyStock;
    private Label lOrdered;
    private WNumberEditor fOrdered;
    private Label lReserved;
    private WNumberEditor fReserved;
    private Label lAvailable;
    private WNumberEditor fAvailable;
    private Label lSupplyType;
    private WSearchEditor fSupplyType;
    private Checkbox fMaster;
    private Checkbox fMRPReq;
    private Checkbox fCreatePlan;
    private int ASI_ID;
    private boolean isBaseLanguage;
    
    public WMRPDetailed() {
        this.m_frame = new CustomForm();
        this.statusBar = new StatusBarPanel();
        this.p_table = new WListbox();
        this.panel = new Panel();
        this.southPanel = new Panel();
        this.southLayout = new Borderlayout();
        this.confirmPanel = new ConfirmPanel(true, true, true, true, true, true, true);
        this.parameterPanel = GridFactory.newGridLayout();
        this.popup = new Menupopup();
        this.calcMenu = new Menuitem(Msg.getMsg(Env.getCtx(), "Calculator"), "/images/Calculator16.png");
        this.lProduct_ID = new Label(Msg.translate(this.getCtx(), "M_Product_ID"));
        this.lAttrSetInstance_ID = new Label(Msg.translate(this.getCtx(), "M_AttributeSetInstance_ID"));
        final Button button = new Button();
        this.fAttrSetInstance_ID = button;
        this.fAttrSetInstance_ID = button;
        this.lResource_ID = new Label(Msg.translate(this.getCtx(), "S_Resource_ID"));
        this.lWarehouse_ID = new Label(Msg.translate(this.getCtx(), "M_Warehouse_ID"));
        this.lPlanner_ID = new Label(Msg.translate(this.getCtx(), "Planner_ID"));
        this.lorder = new Label(Msg.translate(this.getCtx(), "C_Order_ID"));
        this.lMO_ID = new Label(Msg.translate(this.getCtx(), "PP_Order_ID"));
        this.mainLayout = new Borderlayout();
        this.lDateFrom = new Label(Msg.translate(this.getCtx(), "DateFrom"));
        this.fDateFrom = new WDateEditor();
        this.lDateTo = new Label(Msg.translate(this.getCtx(), "DateTo"));
        this.fDateTo = new WDateEditor();
        this.lType = new Label();
        this.fType = new Textbox();
        this.lUOM = new Label();
        this.fUOM = new Textbox();
        this.lOrderPeriod = new Label();
        this.fOrderPeriod = new WNumberEditor();
        this.lTimefence = new Label();
        this.fTimefence = new WNumberEditor();
        this.lLeadtime = new Label();
        this.fLeadtime = new WNumberEditor();
        this.lReplenishMin = new Label();
        this.fReplenishMin = new WNumberEditor();
        this.lMinOrd = new Label();
        this.fMinOrd = new WNumberEditor();
        this.lMaxOrd = new Label();
        this.fMaxOrd = new WNumberEditor();
        this.lOrdMult = new Label();
        this.fOrdMult = new WNumberEditor();
        this.lOrderQty = new Label();
        this.fOrderQty = new WNumberEditor();
        this.lYield = new Label();
        this.fYield = new WNumberEditor();
        this.lOnhand = new Label();
        this.fOnhand = new WNumberEditor();
        this.lSafetyStock = new Label();
        this.fSafetyStock = new WNumberEditor();
        this.lOrdered = new Label();
        this.fOrdered = new WNumberEditor();
        this.lReserved = new Label();
        this.fReserved = new WNumberEditor();
        this.lAvailable = new Label();
        this.fAvailable = new WNumberEditor();
        this.lSupplyType = new Label(Msg.translate(this.getCtx(), "TypeMRP"));
        this.fSupplyType = null;
        this.fMaster = new Checkbox();
        this.fMRPReq = new Checkbox();
        this.fCreatePlan = new Checkbox();
        this.ASI_ID = 0;
        Env.getLanguage(Env.getCtx());
        this.isBaseLanguage = (Language.getBaseAD_Language().compareTo(Env.getLoginLanguage(Env.getCtx()).getAD_Language()) == 0);
        this.init();
    }
    
    private void init() {
        try {
            this.statInit();
            this.fillPicks();
            this.jbInit();
        }
        catch (Exception e) {
            WMRPDetailed.log.log(Level.SEVERE, "VMRPDetailed.init", (Throwable)e);
        }
    }
    
    private void statInit() throws Exception {
        final Language language = Language.getLoginLanguage();
        final MLookup resourceL = MLookupFactory.get(this.getCtx(), this.p_WindowNo, MColumn.getColumn_ID("S_Resource", "S_Resource_ID"), 19, language, "S_Resource_ID", 0, false, "S_Resource.ManufacturingResourceType= 'PT'");
        this.fResource_ID = new WSearchEditor("S_Resource_ID", false, false, true, resourceL) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.fPlanner_ID = new WSearchEditor("Planner_ID", false, false, true, MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("PP_Product_Planning", "Planner_ID"), 18)) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.fWarehouse_ID = new WSearchEditor("M_Warehouse_ID", false, false, true, MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("M_Warehouse", "M_Warehouse_ID"), 19)) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.fMaster.setSelected(false);
        this.fMaster.setEnabled(false);
        this.fMRPReq.setSelected(false);
        this.fMRPReq.setEnabled(false);
        this.fCreatePlan.setSelected(false);
        this.fCreatePlan.setEnabled(false);
        this.lUOM.setText(Msg.translate(this.getCtx(), "C_UOM_ID"));
        this.fUOM.setReadonly(true);
        this.lType.setText(Msg.translate(this.getCtx(), "Order_Policy"));
        this.fType.setReadonly(true);
        this.lOrderPeriod.setText(Msg.translate(this.getCtx(), "Order_Period"));
        this.fOrderPeriod.setReadWrite(false);
        this.lTimefence.setText(Msg.translate(this.getCtx(), "TimeFence"));
        this.fTimefence.setReadWrite(false);
        this.lLeadtime.setText(Msg.translate(this.getCtx(), "DeliveryTime_Promised"));
        this.fLeadtime.setReadWrite(false);
        this.lMinOrd.setText(Msg.translate(this.getCtx(), "Order_Min"));
        this.fMinOrd.setReadWrite(false);
        this.lMaxOrd.setText(Msg.translate(this.getCtx(), "Order_Max"));
        this.fMaxOrd.setReadWrite(false);
        this.lOrdMult.setText(Msg.translate(this.getCtx(), "Order_Pack"));
        this.fOrdMult.setReadWrite(false);
        this.lOrderQty.setText(Msg.translate(this.getCtx(), "Order_Qty"));
        this.fOrderQty.setReadWrite(false);
        this.lYield.setText(Msg.translate(this.getCtx(), "Yield"));
        this.fYield.setReadWrite(false);
        this.lOnhand.setText(Msg.translate(this.getCtx(), "QtyOnHand"));
        this.fOnhand.setReadWrite(false);
        this.lSafetyStock.setText(Msg.translate(this.getCtx(), "SafetyStock"));
        this.fSafetyStock.setReadWrite(false);
        this.lReserved.setText(Msg.translate(this.getCtx(), "Qty"));
        this.fReserved.setReadWrite(false);
        this.lAvailable.setText(Msg.translate(this.getCtx(), "QtyAvailable"));
        this.fAvailable.setReadWrite(false);
        this.lOrdered.setText(Msg.translate(this.getCtx(), "QtyOrdered"));
        this.fOrdered.setReadWrite(false);
        this.fProduct_ID = new WSearchEditor("M_Product_ID", true, false, true, MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("M_Product", "M_Product_ID"), 30)) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.forder_id = new WSearchEditor("C_Order_ID", true, false, true, MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("C_Order", "C_Order_ID"), 30)) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.fMO_ID = new WSearchEditor("PP_Order_ID", true, false, true, MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("PP_Order", "PP_Order_ID"), 30)) {
            private final long serialVersionUID = 1L;
            
            public void setValue(final Object arg0) {
                super.setValue(arg0);
            }
        };
        this.fMaster.setText(Msg.translate(this.getCtx(), "IsMPS"));
        this.fMRPReq.setText(Msg.translate(this.getCtx(), "IsRequiredMRP"));
        this.fCreatePlan.setText(Msg.translate(this.getCtx(), "IsCreatePlan"));
        (this.fAttrSetInstance_ID = new Button() {
            private final long serialVersionUID = 1L;
            private Object m_value;
            
            public void setLabel(String text) {
                if (text == null) {
                    text = "---";
                }
                if (text.length() > 23) {
                    text = String.valueOf(text.substring(0, 20)) + "...";
                }
                super.setLabel(text);
            }
            
            public void setValue(final Object arg0) {
                this.m_value = arg0;
                final int i = (int)((arg0 instanceof Integer) ? arg0 : 0);
                if (i == 0) {
                    this.setLabel(null);
                }
            }
            
            public Object getValue() {
                return this.m_value;
            }
        }).addActionListener((EventListener)new EventListener() {
            public void onEvent(final Event event) throws Exception {
                WMRPDetailed.this.selectAttributeSetInstance();
            }
        });
        this.fDateFrom.getComponent().setTooltiptext(Msg.translate(this.getCtx(), "DateFrom"));
        this.fDateTo.getComponent().setTooltiptext(Msg.translate(this.getCtx(), "DateTo"));
        this.fSupplyType = new WSearchEditor("TypeMRP", false, false, true, (Lookup)MLookupFactory.get(this.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("PP_MRP", "TypeMRP"), 17));
        Rows rows = null;
        Row row = null;
        rows = new Rows();
        rows.setParent((Component)this.parameterPanel);
        row = rows.newRow();
        row.appendChild(this.lProduct_ID.rightAlign());
        row.appendChild((Component)this.fProduct_ID.getComponent());
        row.appendChild(this.lUOM.rightAlign());
        row.appendChild((Component)this.fUOM);
        row.appendChild(this.lType.rightAlign());
        row.appendChild((Component)this.fType);
        row = rows.newRow();
        row.appendChild(this.lAttrSetInstance_ID.rightAlign());
        row.appendChild((Component)this.fAttrSetInstance_ID);
        row.appendChild(this.lOnhand.rightAlign());
        row.appendChild((Component)this.fOnhand.getComponent());
        row.appendChild(this.lOrderPeriod.rightAlign());
        row.appendChild((Component)this.fOrderPeriod.getComponent());
        row = rows.newRow();
        row.appendChild(this.lPlanner_ID.rightAlign());
        row.appendChild((Component)this.fPlanner_ID.getComponent());
        row.appendChild(this.lSafetyStock.rightAlign());
        row.appendChild((Component)this.fSafetyStock.getComponent());
        row.appendChild(this.lMinOrd.rightAlign());
        row.appendChild((Component)this.fMinOrd.getComponent());
        row = rows.newRow();
        row.appendChild(this.lWarehouse_ID.rightAlign());
        row.appendChild((Component)this.fWarehouse_ID.getComponent());
        row.appendChild(this.lReserved.rightAlign());
        row.appendChild((Component)this.fReserved.getComponent());
        row.appendChild(this.lMaxOrd.rightAlign());
        row.appendChild((Component)this.fMaxOrd.getComponent());
        row = rows.newRow();
        row.appendChild(this.lResource_ID.rightAlign());
        row.appendChild((Component)this.fResource_ID.getComponent());
        row.appendChild(this.lAvailable.rightAlign());
        row.appendChild((Component)this.fAvailable.getComponent());
        row.appendChild(this.lOrdMult.rightAlign());
        row.appendChild((Component)this.fOrdMult.getComponent());
        row = rows.newRow();
        row.appendChild(this.lDateFrom.rightAlign());
        row.appendChild((Component)this.fDateFrom.getComponent());
        row.appendChild(this.lOrdered.rightAlign());
        row.appendChild((Component)this.fOrdered.getComponent());
        row.appendChild(this.lOrderQty.rightAlign());
        row.appendChild((Component)this.fOrderQty.getComponent());
        row = rows.newRow();
        row.appendChild(this.lDateTo.rightAlign());
        row.appendChild((Component)this.fDateTo.getComponent());
        row.appendChild(this.lorder.rightAlign());
        row.appendChild((Component)this.forder_id.getComponent());
        row.appendChild(this.lTimefence.rightAlign());
        row.appendChild((Component)this.fTimefence.getComponent());
        row = rows.newRow();
        row.appendChild((Component)new Space());
        row.appendChild((Component)this.fMaster);
        row.appendChild((Component)new Space());
        row.appendChild((Component)this.fCreatePlan);
        row.appendChild(this.lLeadtime.rightAlign());
        row.appendChild((Component)this.fLeadtime.getComponent());
        row = rows.newRow();
        row.appendChild(this.lMO_ID.rightAlign());
        row.appendChild((Component)this.fMO_ID.getComponent());
        row.appendChild((Component)new Space());
        row.appendChild((Component)this.fMRPReq);
        row.appendChild(this.lYield.rightAlign());
        row.appendChild((Component)this.fYield.getComponent());
    }
    
    private void selectAttributeSetInstance() {
        final int m_warehouse_id = 0;
        final int m_product_id = 0;
        if (m_product_id <= 0) {
            return;
        }
        final MProduct product = MProduct.get(this.getCtx(), m_product_id);
        final MWarehouse wh = MWarehouse.get(this.getCtx(), m_warehouse_id);
        final String title = String.valueOf(product.get_Translation("Name")) + " - " + wh.get_Translation("Name");
        final WPAttributeInstance pai = new WPAttributeInstance(title, m_warehouse_id, 0, m_product_id, 0);
        if (pai.getM_AttributeSetInstance_ID() != -1) {
            this.fAttrSetInstance_ID.setLabel(pai.getM_AttributeSetInstanceName());
            this.ASI_ID = new Integer(pai.getM_AttributeSetInstance_ID());
        }
        else {
            this.ASI_ID = 0;
        }
    }
    
    private boolean isAttributeSetInstance() {
        return this.getM_AttributeSetInstance_ID() > 0;
    }
    
    private void initComponents() {
        this.OrderPlanning = new Tabbox();
        this.PanelOrder = new Tab();
        this.PanelFind = new Panel();
        this.PanelCenter = new Panel();
        this.PanelBottom = new Panel();
        this.Results = new Tab();
        final Borderlayout PanelOrderLayout = new Borderlayout();
        this.PanelOrder.appendChild((Component)PanelOrderLayout);
        final North north = new North();
        PanelOrderLayout.appendChild((Component)north);
        north.appendChild((Component)this.PanelFind);
        final Center center = new Center();
        PanelOrderLayout.appendChild((Component)center);
        center.appendChild((Component)this.PanelCenter);
        final South south = new South();
        PanelOrderLayout.appendChild((Component)south);
        south.appendChild((Component)this.PanelBottom);
        this.OrderPlanning.appendChild((Component)this.PanelOrder);
        this.OrderPlanning.appendChild((Component)this.Results);
        this.PanelOrder.setLabel("Order");
        this.Results.setLabel("Result");
        final Center center2 = new Center();
        this.mainLayout.appendChild((Component)center2);
        center2.appendChild((Component)this.OrderPlanning);
        this.m_frame.setWidth("99%");
        this.m_frame.setHeight("100%");
        this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
        this.m_frame.appendChild((Component)this.mainLayout);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("100%");
        this.mainLayout.setStyle("position: absolute");
    }
    
    protected void jbInit() throws Exception {
        this.m_frame.setWidth("99%");
        this.m_frame.setHeight("100%");
        this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
        this.m_frame.appendChild((Component)this.mainLayout);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("100%");
        this.mainLayout.setStyle("position: absolute");
        final North north = new North();
        north.appendChild((Component)this.parameterPanel);
        this.mainLayout.appendChild((Component)north);
        final Center center = new Center();
        center.appendChild((Component)this.p_table);
        this.mainLayout.appendChild((Component)center);
        this.p_table.setVflex(true);
        this.p_table.setFixedLayout(true);
        center.setFlex(true);
        final Div div = new Div();
        div.appendChild((Component)this.confirmPanel);
        div.appendChild((Component)this.statusBar);
        final South south = new South();
        south.appendChild((Component)div);
        this.mainLayout.appendChild((Component)south);
        this.confirmPanel.addActionListener((EventListener)this);
        this.confirmPanel.setVisible("Reset", this.hasReset());
        this.confirmPanel.setVisible("Customize", this.hasCustomize());
        this.confirmPanel.setVisible("History", this.hasHistory());
        this.confirmPanel.setVisible("Zoom", this.hasZoom());
        final South southPanel = new South();
        southPanel.appendChild((Component)this.southLayout);
        final Button print = this.confirmPanel.createButton("Print");
        print.addActionListener((EventListener)this);
        this.confirmPanel.addButton(print);
        this.popup.appendChild((Component)this.calcMenu);
        this.calcMenu.addEventListener("onClick", (EventListener)this);
        this.p_table.getModel().addListDataListener((ListDataListener)this);
        this.enableButtons();
    }
    
    private void fillPicks() throws Exception {
        this.m_keyColumnIndex = 0;
        this.m_sqlMain = this.p_table.prepareTable(this.m_layout, this.getTableName(), this.getWhereClause(this.getSQLWhere()), false, "RV_PP_MRP", false);
    }
    
    public ADForm getForm() {
        return (ADForm)this.m_frame;
    }
    
    public void onEvent(final Event event) throws Exception {
        final String cmd = event.getTarget().getId();
        if (cmd.equals("Ok")) {
            this.m_frame.dispose();
        }
        else if (cmd.equals("Cancel")) {
            this.m_cancel = true;
            this.m_frame.dispose();
        }
        else if (cmd.equals("Zoom")) {
            this.zoom();
        }
        else if (cmd.equals("Refresh")) {
            this.executeQuery();
        }
    }
    
    public void dispose() {
        if (this.m_frame != null) {
            this.m_frame.dispose();
        }
        this.m_frame = null;
    }
    
    private String getSQLWhere() {
        final StringBuffer sql = new StringBuffer();
        if (this.fProduct_ID.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".M_Product_ID=?");
            sql.append(" AND ((" + this.getTableName() + ".OrderType IN ('SOO','MOP','POO','POR','STK','DOO')) OR (" + this.getTableName() + ".OrderType='FCT' AND " + this.getTableName() + ".DatePromised >= SYSDATE))");
            this.fillHead();
            this.setMRP();
        }
        if (this.isAttributeSetInstance()) {
            sql.append(" AND " + this.getTableName() + ".M_AttributeSetInstance_ID=?");
            this.fillHead();
            this.setMRP();
        }
        if (this.fResource_ID.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".S_Resource_ID=?");
        }
        if (this.fPlanner_ID.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".Planner_ID=?");
        }
        if (this.fWarehouse_ID.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".M_Warehouse_ID=?");
        }
        if (this.fDateFrom.getValue() != null || this.fDateFrom.getValue() != null) {
            final Timestamp from = this.fDateFrom.getValue();
            final Timestamp to = this.fDateTo.getValue();
            if (from == null && to != null) {
                sql.append(" AND TRUNC(" + this.getTableName() + ".DatePromised) <= ?");
            }
            else if (from != null && to == null) {
                sql.append(" AND TRUNC(" + this.getTableName() + ".DatePromised) >= ?");
            }
            else if (from != null && to != null) {
                sql.append(" AND TRUNC(" + this.getTableName() + ".DatePromised) BETWEEN ? AND ?");
            }
        }
        if (this.forder_id.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".C_Order_ID=?");
        }
        if (this.fMO_ID.getValue() != null) {
            sql.append(" AND " + this.getTableName() + ".idmoref=?");
        }
        WMRPDetailed.log.fine("MRP Info.setWhereClause=" + sql.toString());
        return sql.toString();
    }
    
    private void fillHead() {
        MPPProductPlanning pp = MPPProductPlanning.find(this.getCtx(), this.getAD_Org_ID(), this.getM_Warehouse_ID(), this.getS_Resource_ID(), this.getM_Product_ID(), (String)null);
        if (pp == null) {
            pp = new MPPProductPlanning(this.getCtx(), 0, (String)null);
        }
        this.fMaster.setSelected(pp.isMPS());
        this.fMRPReq.setSelected(pp.isRequiredMRP());
        this.fCreatePlan.setSelected(pp.isCreatePlan());
        this.fOrderPeriod.setValue((Object)pp.getOrder_Period());
        this.fLeadtime.setValue((Object)pp.getDeliveryTime_Promised());
        this.fTimefence.setValue((Object)pp.getTimeFence());
        this.fMinOrd.setValue((Object)pp.getOrder_Min());
        this.fMaxOrd.setValue((Object)pp.getOrder_Max());
        this.fOrdMult.setValue((Object)pp.getOrder_Pack());
        this.fOrderQty.setValue((Object)pp.getOrder_Qty());
        this.fYield.setValue((Object)pp.getYield());
        this.fType.setText(MRefList.getListName(this.getCtx(), 53228, pp.getOrder_Policy()));
        this.fSafetyStock.setValue((Object)pp.getSafetyStock());
    }
    
    private void setMRP() {
        final int M_Product_ID = this.getM_Product_ID();
        this.getM_AttributeSetInstance_ID();
        final int M_Warehouse_ID = this.getM_Warehouse_ID();
        if (M_Product_ID <= 0) {
            return;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuffer sql = new StringBuffer("SELECT ").append("BOMQtyOnHandASI(M_Product_ID,?,?,?) as qtyonhand, ").append("BOMQtyReservedASI(M_Product_ID,?,?,?) as qtyreserved, ").append("BOMQtyAvailableASI(M_Product_ID,?,?,?) as qtyavailable, ").append("BOMQtyOrderedASI(M_Product_ID,?,?,?) as qtyordered").append(" FROM M_Product WHERE M_Product_ID=?");
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), (String)null);
            DB.setParameters(pstmt, new Object[] { this.getM_AttributeSetInstance_ID(), this.getM_Warehouse_ID(), 0, this.getM_AttributeSetInstance_ID(), this.getM_Warehouse_ID(), 0, this.getM_AttributeSetInstance_ID(), this.getM_Warehouse_ID(), 0, this.getM_AttributeSetInstance_ID(), this.getM_Warehouse_ID(), 0, this.getM_Product_ID() });
            rs = pstmt.executeQuery();
            while (rs.next()) {
                this.fOnhand.setValue((Object)rs.getBigDecimal(1));
                this.fReserved.setValue((Object)rs.getBigDecimal(2));
                this.fAvailable.setValue((Object)rs.getBigDecimal(3));
                this.fOrdered.setValue((Object)rs.getBigDecimal(4));
            }
        }
        catch (SQLException ex) {
            throw new DBException((Exception)ex);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        final int uom_id = MProduct.get(this.getCtx(), M_Product_ID).getC_UOM_ID();
        final MUOM um = MUOM.get(this.getCtx(), uom_id);
        final KeyNamePair kum = new KeyNamePair(um.getC_UOM_ID(), um.get_Translation("Name"));
        this.fUOM.setText(kum.toString());
        BigDecimal replenishLevelMin = Env.ZERO;
        if (this.getM_Warehouse_ID() > 0) {
            final String sql2 = "SELECT Level_Min FROM M_Replenish WHERE AD_Client_ID=? AND M_Product_ID=? AND M_Warehouse_ID=?";
            replenishLevelMin = DB.getSQLValueBD((String)null, sql2, new Object[] { this.AD_Client_ID, M_Product_ID, M_Warehouse_ID });
        }
        this.fReplenishMin.setValue((Object)replenishLevelMin);
    }
    
    @Override
    public void zoom() {
        super.zoom();
        AEnv.zoom(this.AD_Window_ID, this.query);
    }
    
    void enableButtons() {
        final boolean enable = true;
        this.confirmPanel.getOKButton().setEnabled(true);
        if (this.hasHistory()) {
            this.confirmPanel.getButton("History").setEnabled(enable);
        }
        if (this.hasZoom()) {
            this.confirmPanel.getButton("Zoom").setEnabled(enable);
        }
    }
    
    void executeQuery() {
        this.work();
    }
    
    protected void setParameters(final PreparedStatement pstmt, final boolean forCount) throws SQLException {
        int index = 1;
        if (this.getM_Product_ID() > 0) {
            final int product_id = this.getM_Product_ID();
            pstmt.setInt(index++, product_id);
            WMRPDetailed.log.fine("Product=" + product_id);
        }
        if (this.isAttributeSetInstance()) {
            final int asi = this.getM_AttributeSetInstance_ID();
            pstmt.setInt(index++, asi);
            WMRPDetailed.log.fine("AttributeSetInstance=" + asi);
        }
        if (this.getS_Resource_ID() > 0) {
            final int resource_id = this.getS_Resource_ID();
            pstmt.setInt(index++, resource_id);
            WMRPDetailed.log.fine("Resource=" + resource_id);
        }
        if (this.getOrderID() > 0) {
            final int order_id = this.getOrderID();
            pstmt.setInt(index++, order_id);
            WMRPDetailed.log.fine("Resource=" + order_id);
        }
        if (this.getMoID() > 0) {
            final int order_id = this.getMoID();
            pstmt.setInt(index++, order_id);
            WMRPDetailed.log.fine("Resource=" + order_id);
        }
        if (this.getM_Warehouse_ID() > 0) {
            final int warehouse_id = this.getM_Warehouse_ID();
            pstmt.setInt(index++, this.getM_Warehouse_ID());
            WMRPDetailed.log.fine("Warehouse=" + warehouse_id);
        }
        if (this.getPlanner_ID() > 0) {
            final int planner_id = this.getPlanner_ID();
            pstmt.setInt(index++, planner_id);
            WMRPDetailed.log.fine("Planner=" + planner_id);
        }
        if (this.getDueStart() != null || this.getDueEnd() != null) {
            final Timestamp from = this.getDueStart();
            final Timestamp to = this.getDueEnd();
            WMRPDetailed.log.fine("Date From=" + from + ", Date To=" + to);
            if (from == null && to != null) {
                pstmt.setTimestamp(index++, to);
            }
            else if (from != null && to == null) {
                pstmt.setTimestamp(index++, from);
            }
            else if (from != null && to != null) {
                pstmt.setTimestamp(index++, from);
                pstmt.setTimestamp(index++, to);
            }
        }
    }
    
    protected int getM_Product_ID() {
        final Object o = this.fProduct_ID.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    protected int getM_AttributeSetInstance_ID() {
        return this.ASI_ID;
    }
    
    protected int getAD_Org_ID() {
        final int warehouse_id = this.getM_Warehouse_ID();
        if (warehouse_id <= 0) {
            return 0;
        }
        return MWarehouse.get(this.getCtx(), warehouse_id).getAD_Org_ID();
    }
    
    protected int getM_Warehouse_ID() {
        final Object o = this.fWarehouse_ID.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    protected int getS_Resource_ID() {
        final Object o = this.fResource_ID.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    protected int getPlanner_ID() {
        final Object o = this.fPlanner_ID.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    protected Timestamp getDueStart() {
        return this.fDateFrom.getValue();
    }
    
    protected Timestamp getDueEnd() {
        return this.fDateTo.getValue();
    }
    
    protected int getOrderID() {
        final Object o = this.forder_id.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    protected BigDecimal getQtyOnHand() {
        final BigDecimal bd = this.fOnhand.getValue();
        return (bd != null) ? bd : Env.ZERO;
    }
    
    protected int getMoID() {
        final Object o = this.fMO_ID.getValue();
        return (int)((o != null && o instanceof Integer) ? o : Integer.valueOf(0));
    }
    
    public void onChange(final ListDataEvent event) {
    }
    
    public void tableChanged(final WTableModelEvent event) {
    }
    
    protected Integer getSelectedRowKey() {
        final int row = this.p_table.getSelectedRow();
        if (row != -1 && this.m_keyColumnIndex != -1) {
            Object data = this.p_table.getModel().getValueAt(row, this.m_keyColumnIndex);
            if (data instanceof IDColumn) {
                data = ((IDColumn)data).getRecord_ID();
            }
            if (data instanceof Integer) {
                return (Integer)data;
            }
        }
        return null;
    }
    
    @Override
    void zoom(final int AD_Window_ID, final MQuery zoomQuery) {
    }
    
    public void work() {
        WMRPDetailed.log.fine("Info.Worker.run");
        final StringBuffer sql = new StringBuffer(this.m_sqlMain);
        final String dynWhere = this.getSQLWhere();
        if (dynWhere.length() > 0) {
            sql.append(dynWhere);
        }
        String xSql = Msg.parseTranslation(this.getCtx(), sql.toString());
        xSql = MRole.getDefault().addAccessSQL(xSql, this.getTableName(), true, false);
        try {
            final PreparedStatement pstmt = (PreparedStatement)DB.prepareStatement(xSql, (String)null);
            WMRPDetailed.log.fine("SQL=" + xSql);
            this.setParameters(pstmt, false);
            final ResultSet rs = pstmt.executeQuery();
            this.p_table.loadTable(rs);
            rs.close();
            pstmt.close();
        }
        catch (SQLException e) {
            WMRPDetailed.log.log(Level.SEVERE, "Info.Worker.run - " + xSql, (Throwable)e);
        }
        if (this.getM_Product_ID() > 0) {
            BigDecimal OnHand = this.getQtyOnHand();
            for (int row = 0; row < this.p_table.getRowCount(); ++row) {
                final Timestamp datepromised = (Timestamp)this.p_table.getValueAt(row, 5);
                final Timestamp today = new Timestamp(System.currentTimeMillis());
                final IDColumn id = (IDColumn)this.p_table.getValueAt(row, 0);
                final String TypeMRP = DB.getSQLValueString((String)null, "SELECT TypeMRP FROM " + this.getTableName() + " WHERE PP_MRP_ID=?", (int)id.getRecord_ID());
                final String OrderType = (String)this.p_table.getValueAt(row, 11);
                if ("D".equals(TypeMRP) || ("FCT".equals(OrderType) && datepromised.after(today))) {
                    final BigDecimal QtyGrossReqs = (BigDecimal)this.p_table.getValueAt(row, 6);
                    OnHand = OnHand.subtract(QtyGrossReqs);
                    this.p_table.setValueAt((Object)OnHand, row, 9);
                }
                if ("S".equals(TypeMRP)) {
                    BigDecimal QtyScheduledReceipts = (BigDecimal)this.p_table.getValueAt(row, 7);
                    BigDecimal QtyPlan = (BigDecimal)this.p_table.getValueAt(row, 8);
                    if (QtyPlan == null) {
                        QtyPlan = Env.ZERO;
                    }
                    if (QtyScheduledReceipts == null) {
                        QtyScheduledReceipts = Env.ZERO;
                    }
                    OnHand = OnHand.add(QtyScheduledReceipts.add(QtyPlan));
                    this.p_table.setValueAt((Object)OnHand, row, 9);
                }
            }
        }
    }
}
