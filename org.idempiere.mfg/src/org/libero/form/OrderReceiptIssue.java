// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form;

import org.compiere.model.MAttributeSetInstance;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import org.adempiere.exceptions.DBException;
import java.math.RoundingMode;
import org.compiere.model.MStorageOnHand;
import org.libero.model.MPPOrderBOMLine;
import org.compiere.util.DB;
import org.compiere.model.MProduct;
import java.util.ArrayList;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.IMiniTable;
import org.compiere.util.Env;
import org.libero.model.MPPOrder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.util.CLogger;
import org.compiere.apps.form.GenForm;

public class OrderReceiptIssue extends GenForm
{
    private static CLogger log;
    String m_sql;
    private boolean m_isOnlyReceipt;
    private boolean m_OnlyIssue;
    protected boolean m_IsBackflush;
    protected Timestamp m_movementDate;
    protected BigDecimal m_orderedQty;
    protected BigDecimal m_DeliveredQty;
    protected BigDecimal m_toDeliverQty;
    protected BigDecimal m_scrapQty;
    protected BigDecimal m_rejectQty;
    protected BigDecimal m_openQty;
    protected BigDecimal m_qtyBatchs;
    protected BigDecimal m_qtyBatchSize;
    protected int m_M_AttributeSetInstance_ID;
    protected int m_M_Locator_ID;
    private int m_PP_Order_ID;
    private MPPOrder m_PP_order;
    
    static {
        OrderReceiptIssue.log = CLogger.getCLogger((Class)OrderReceiptIssue.class);
    }
    
    public OrderReceiptIssue() {
        this.m_sql = "";
        this.m_isOnlyReceipt = false;
        this.m_OnlyIssue = false;
        this.m_IsBackflush = false;
        this.m_movementDate = null;
        this.m_orderedQty = Env.ZERO;
        this.m_DeliveredQty = Env.ZERO;
        this.m_toDeliverQty = Env.ZERO;
        this.m_scrapQty = Env.ZERO;
        this.m_rejectQty = Env.ZERO;
        this.m_openQty = Env.ZERO;
        this.m_qtyBatchs = Env.ZERO;
        this.m_qtyBatchSize = Env.ZERO;
        this.m_M_AttributeSetInstance_ID = 0;
        this.m_M_Locator_ID = 0;
        this.m_PP_Order_ID = 0;
        this.m_PP_order = null;
    }
    
    public void configureMiniTable(final IMiniTable issue) {
        issue.addColumn("PP_Order_BOMLine_ID");
        issue.addColumn("IsCritical");
        issue.addColumn("Value");
        issue.addColumn("M_Product_ID");
        issue.addColumn("C_UOM_ID");
        issue.addColumn("M_AttributeSetInstance_ID");
        issue.addColumn("QtyRequired");
        issue.addColumn("QtyDelivered");
        issue.addColumn("QtyToDeliver");
        issue.addColumn("QtyScrap");
        issue.addColumn("QtyOnHand");
        issue.addColumn("QtyReserved");
        issue.addColumn("QtyAvailable");
        issue.addColumn("M_Locator_ID");
        issue.addColumn("M_Warehouse_ID");
        issue.addColumn("QtyBOM");
        issue.addColumn("IsQtyPercentage");
        issue.addColumn("QtyBatch");
        issue.setMultiSelection(true);
        issue.setColumnClass(0, (Class)IDColumn.class, false, " ");
        issue.setColumnClass(1, (Class)Boolean.class, true, Msg.translate(Env.getCtx(), "IsCritical"));
        issue.setColumnClass(2, (Class)String.class, true, Msg.translate(Env.getCtx(), "Value"));
        issue.setColumnClass(3, (Class)KeyNamePair.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));
        issue.setColumnClass(4, (Class)KeyNamePair.class, true, Msg.translate(Env.getCtx(), "C_UOM_ID"));
        issue.setColumnClass(5, (Class)String.class, true, Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
        issue.setColumnClass(6, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyRequired"));
        issue.setColumnClass(7, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyDelivered"));
        issue.setColumnClass(8, (Class)BigDecimal.class, false, Msg.translate(Env.getCtx(), "QtyToDeliver"));
        issue.setColumnClass(9, (Class)BigDecimal.class, false, Msg.translate(Env.getCtx(), "QtyScrap"));
        issue.setColumnClass(10, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyOnHand"));
        issue.setColumnClass(11, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyReserved"));
        issue.setColumnClass(12, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyAvailable"));
        issue.setColumnClass(13, (Class)String.class, true, Msg.translate(Env.getCtx(), "M_Locator_ID"));
        issue.setColumnClass(14, (Class)KeyNamePair.class, true, Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
        issue.setColumnClass(15, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyBom"));
        issue.setColumnClass(16, (Class)Boolean.class, true, Msg.translate(Env.getCtx(), "IsQtyPercentage"));
        issue.setColumnClass(17, (Class)BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyBatch"));
        issue.autoSize();
        issue.setRowCount(0);
        this.m_sql = "SELECT obl.PP_Order_BOMLine_ID,obl.IsCritical,p.Value,obl.M_Product_ID,p.Name,p.C_UOM_ID,u.Name,obl.QtyRequired,obl.QtyReserved,bomQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID,0 ) AS QtyAvailable,bomQtyOnHand(obl.M_Product_ID,obl.M_Warehouse_ID,0) AS QtyOnHand,p.M_Locator_ID,obl.M_Warehouse_ID,w.Name,obl.QtyBom,obl.isQtyPercentage,obl.QtyBatch,obl.ComponentType,obl.QtyRequired - QtyDelivered AS QtyOpen,obl.QtyDelivered FROM PP_Order_BOMLine obl INNER JOIN M_Product p ON (obl.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (p.C_UOM_ID = u.C_UOM_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = obl.M_Warehouse_ID)  WHERE obl.PP_Order_ID = ? ORDER BY obl.Line";
    }
    
    private String createHTMLTable(final String[][] table) {
        final StringBuffer html = new StringBuffer("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        for (int i = 0; i < table.length; ++i) {
            if (table[i] != null) {
                html.append("<tr>");
                for (int j = 0; j < table[i].length; ++j) {
                    html.append("<td>");
                    if (table[i][j] != null) {
                        html.append(table[i][j]);
                    }
                    html.append("</td>");
                }
                html.append("</tr>");
            }
        }
        html.append("</table>");
        return html.toString();
    }
    
    public void createIssue(final MPPOrder order, final IMiniTable issue) {
        final Timestamp minGuaranteeDate;
        final Timestamp movementDate = minGuaranteeDate = this.getMovementDate();
        final ArrayList[][] m_issue = new ArrayList[issue.getRowCount()][1];
        int row = 0;
        for (int i = 0; i < issue.getRowCount(); ++i) {
            final ArrayList<Object> data = new ArrayList<Object>();
            final IDColumn id = (IDColumn)issue.getValueAt(i, 0);
            final KeyNamePair key = new KeyNamePair((int)id.getRecord_ID(), id.isSelected() ? "Y" : "N");
            data.add(key);
            data.add(issue.getValueAt(i, 1));
            data.add(issue.getValueAt(i, 2));
            data.add(issue.getValueAt(i, 3));
            data.add(this.getValueBigDecimal(issue, i, 8));
            data.add(this.getValueBigDecimal(issue, i, 9));
            m_issue[row][0] = data;
            ++row;
        }
        MPPOrder.isQtyAvailable(order, m_issue, minGuaranteeDate);
        for (int i = 0; i < m_issue.length; ++i) {
            final KeyNamePair key2 = (KeyNamePair) m_issue[i][0].get(0);
            final boolean isSelected = key2.getName().equals("Y");
            if (key2 != null) {
                if (isSelected) {
                    final Boolean b = (Boolean) m_issue[i][0].get(1);
                    final String value = (String) m_issue[i][0].get(2);
                    final KeyNamePair productkey = (KeyNamePair) m_issue[i][0].get(3);
                    final int M_Product_ID = productkey.getKey();
                    MPPOrderBOMLine orderbomLine = null;
                    int PP_Order_BOMLine_ID = 0;
                    int M_AttributeSetInstance_ID = 0;
                    final BigDecimal qtyToDeliver = (BigDecimal) m_issue[i][0].get(4);
                    final BigDecimal qtyScrapComponent = (BigDecimal) m_issue[i][0].get(5);
                    final MProduct product = MProduct.get(order.getCtx(), M_Product_ID);
                    if (product != null && product.get_ID() != 0 && product.isStocked()) {
                        if (DB.getSQLValueString(order.get_TrxName(), "select coalesce(mpc.costingmethod,'') from M_Product_Category_Acct mpc where mpc.m_product_category_id = " + product.getM_Product_Category_ID(), new Object[0]).equals("F") && value == null && isSelected) {
                            M_AttributeSetInstance_ID = key2.getKey();
                            orderbomLine = MPPOrderBOMLine.forM_Product_ID(Env.getCtx(), order.get_ID(), M_Product_ID, order.get_TrxName());
                            if (orderbomLine != null) {
                                PP_Order_BOMLine_ID = orderbomLine.get_ID();
                            }
                            final MStorageOnHand[] storages = MPPOrder.getStorages(Env.getCtx(), M_Product_ID, order.getM_Warehouse_ID(), M_AttributeSetInstance_ID, minGuaranteeDate, order.get_TrxName());
                            MPPOrder.createIssue(order, PP_Order_BOMLine_ID, movementDate, qtyToDeliver, qtyScrapComponent, Env.ZERO, storages, false);
                        }
                        if (!DB.getSQLValueString(order.get_TrxName(), "select coalesce(mpc.costingmethod,'') from M_Product_Category_Acct mpc where mpc.m_product_category_id = " + product.getM_Product_Category_ID(), new Object[0]).equals("F") && value != null && isSelected) {
                            PP_Order_BOMLine_ID = key2.getKey();
                            if (PP_Order_BOMLine_ID > 0) {
                                orderbomLine = new MPPOrderBOMLine(order.getCtx(), PP_Order_BOMLine_ID, order.get_TrxName());
                                M_AttributeSetInstance_ID = orderbomLine.getM_AttributeSetInstance_ID();
                            }
                            final MStorageOnHand[] storages = MPPOrder.getStorages(Env.getCtx(), M_Product_ID, order.getM_Warehouse_ID(), M_AttributeSetInstance_ID, minGuaranteeDate, order.get_TrxName());
                            MPPOrder.createIssue(order, PP_Order_BOMLine_ID, movementDate, qtyToDeliver, qtyScrapComponent, Env.ZERO, storages, false);
                        }
                    }
                }
            }
        }
    }
    
    public void executeQuery(final IMiniTable issue) {
        int row = 0;
        issue.setRowCount(row);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement("SELECT obl.PP_Order_BOMLine_ID,obl.IsCritical,p.Value,obl.M_Product_ID,p.Name,p.C_UOM_ID,u.Name,obl.QtyRequired,obl.QtyReserved,bomQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID,0 ) AS QtyAvailable,bomQtyOnHand(obl.M_Product_ID,obl.M_Warehouse_ID,0) AS QtyOnHand,p.M_Locator_ID,obl.M_Warehouse_ID,w.Name,obl.QtyBom,obl.isQtyPercentage,obl.QtyBatch,obl.ComponentType,obl.QtyRequired - QtyDelivered AS QtyOpen,obl.QtyDelivered FROM PP_Order_BOMLine obl INNER JOIN M_Product p ON (obl.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (p.C_UOM_ID = u.C_UOM_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = obl.M_Warehouse_ID)  INNER JOIN M_Product_Category_Acct mpc on (mpc.M_Product_Category_ID = p.M_Product_Category_ID) WHERE obl.PP_Order_ID = ?   ORDER BY obl.Line", (String)null);
            pstmt.setInt(1, this.getPP_Order_ID());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                issue.setRowCount(row + 1);
                final IDColumn id = new IDColumn(rs.getInt(1));
                final BigDecimal qtyBom = rs.getBigDecimal(15);
                final Boolean isQtyPercentage = rs.getString(16).equals("Y");
                final Boolean isCritical = rs.getString(2).equals("Y");
                final BigDecimal qtyBatch = rs.getBigDecimal(17);
                final BigDecimal qtyRequired = rs.getBigDecimal(8);
                final BigDecimal qtyOnHand = rs.getBigDecimal(11);
                final BigDecimal qtyOpen = rs.getBigDecimal(19);
                final BigDecimal qtyDelivered = rs.getBigDecimal(20);
                final String componentType = rs.getString(18);
                final BigDecimal toDeliverQty = this.getToDeliverQty();
                final BigDecimal openQty = this.getOpenQty();
                final BigDecimal scrapQty = this.getScrapQty();
                BigDecimal componentToDeliverQty = Env.ZERO;
                BigDecimal componentScrapQty = Env.ZERO;
                BigDecimal componentQtyReq = Env.ZERO;
                BigDecimal componentQtyToDel = Env.ZERO;
                id.setSelected(this.isOnlyReceipt());
                issue.setValueAt((Object)id, row, 0);
                issue.setValueAt((Object)isCritical, row, 1);
                issue.setValueAt((Object)rs.getString(3), row, 2);
                issue.setValueAt((Object)new KeyNamePair(rs.getInt(4), rs.getString(5)), row, 3);
                issue.setValueAt((Object)new KeyNamePair(rs.getInt(6), rs.getString(7)), row, 4);
                issue.setValueAt((Object)qtyRequired, row, 6);
                issue.setValueAt((Object)qtyDelivered, row, 7);
                issue.setValueAt((Object)qtyOnHand, row, 10);
                issue.setValueAt((Object)rs.getBigDecimal(9), row, 11);
                issue.setValueAt((Object)rs.getBigDecimal(10), row, 12);
                issue.setValueAt((Object)new KeyNamePair(rs.getInt(13), rs.getString(14)), row, 14);
                issue.setValueAt((Object)qtyBom, row, 15);
                issue.setValueAt((Object)isQtyPercentage, row, 16);
                issue.setValueAt((Object)qtyBatch, row, 17);
                if (componentType.equals("CO") || componentType.equals("PK")) {
                    id.setSelected(qtyOnHand.signum() > 0 && qtyRequired.signum() > 0);
                    issue.setValueAt((Object)id, row, 0);
                    if (isQtyPercentage) {
                        final BigDecimal qtyBatchPerc = qtyBatch.divide(Env.ONEHUNDRED, 8, RoundingMode.HALF_UP);
                        if (this.isBackflush()) {
                            if (qtyRequired.signum() == 0 || qtyOpen.signum() == 0) {
                                componentToDeliverQty = Env.ZERO;
                            }
                            else {
                                componentToDeliverQty = toDeliverQty.multiply(qtyBatchPerc);
                                if (qtyRequired.subtract(qtyDelivered).signum() < 0 | componentToDeliverQty.signum() == 0) {
                                    componentToDeliverQty = qtyRequired.subtract(qtyDelivered);
                                }
                            }
                            if (componentToDeliverQty.signum() != 0) {
                                componentQtyToDel = componentToDeliverQty.setScale(4, 4);
                                issue.setValueAt((Object)componentToDeliverQty, row, 8);
                            }
                        }
                        else {
                            componentToDeliverQty = qtyOpen;
                            if (componentToDeliverQty.signum() != 0) {
                                componentQtyReq = openQty.multiply(qtyBatchPerc);
                                componentQtyToDel = componentToDeliverQty.setScale(4, 4);
                                issue.setValueAt((Object)componentToDeliverQty.setScale(8, 4), row, 8);
                                issue.setValueAt((Object)openQty.multiply(qtyBatchPerc), row, 6);
                            }
                        }
                        if (scrapQty.signum() != 0) {
                            componentScrapQty = scrapQty.multiply(qtyBatchPerc);
                            if (componentScrapQty.signum() != 0) {
                                issue.setValueAt((Object)componentScrapQty, row, 9);
                            }
                        }
                        else {
                            issue.setValueAt((Object)componentScrapQty, row, 9);
                        }
                    }
                    else {
                        if (this.isBackflush()) {
                            componentToDeliverQty = toDeliverQty.multiply(qtyBom);
                            if (componentToDeliverQty.signum() != 0) {
                                componentQtyReq = toDeliverQty.multiply(qtyBom);
                                componentQtyToDel = componentToDeliverQty;
                                issue.setValueAt((Object)componentQtyReq, row, 6);
                                issue.setValueAt((Object)componentToDeliverQty, row, 8);
                            }
                        }
                        else {
                            componentToDeliverQty = qtyOpen;
                            if (componentToDeliverQty.signum() != 0) {
                                componentQtyReq = openQty.multiply(qtyBom);
                                componentQtyToDel = componentToDeliverQty;
                                issue.setValueAt((Object)componentQtyReq, row, 6);
                                issue.setValueAt((Object)componentToDeliverQty, row, 8);
                            }
                        }
                        if (scrapQty.signum() != 0) {
                            componentScrapQty = scrapQty.multiply(qtyBom);
                            if (componentScrapQty.signum() != 0) {
                                issue.setValueAt((Object)componentScrapQty, row, 9);
                            }
                        }
                        else {
                            issue.setValueAt((Object)componentScrapQty, row, 9);
                        }
                    }
                }
                else if (componentType.equals("TL")) {
                    componentToDeliverQty = qtyBom;
                    if (componentToDeliverQty.signum() != 0) {
                        componentQtyReq = qtyBom;
                        componentQtyToDel = componentToDeliverQty;
                        issue.setValueAt((Object)qtyBom, row, 6);
                        issue.setValueAt((Object)componentToDeliverQty, row, 8);
                    }
                }
                else {
                    issue.setValueAt((Object)Env.ZERO, row, 6);
                }
                ++row;
                if (this.isOnlyIssue() || this.isBackflush()) {
                    final int warehouse_id = rs.getInt(13);
                    final int product_id = rs.getInt(4);
                    row += this.lotes(row, id, warehouse_id, product_id, componentQtyReq, componentQtyToDel, issue);
                }
            }
        }
        catch (SQLException e) {
            throw new DBException((Exception)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        issue.autoSize();
    }
    
    public String generateSummaryTable(final IMiniTable issue, final String productField, final String uomField, final String attribute, final String toDeliverQty, final String deliveredQtyField, final String scrapQtyField, final boolean isBackflush, final boolean isOnlyIssue, final boolean isOnlyReceipt) {
        final StringBuffer iText = new StringBuffer();
        iText.append("<b>");
        iText.append(Msg.translate(Env.getCtx(), "IsShipConfirm"));
        iText.append("</b>");
        iText.append("<br />");
        if (isOnlyReceipt || isBackflush) {
            final String[][] table = { { Msg.translate(Env.getCtx(), "Name"), Msg.translate(Env.getCtx(), "C_UOM_ID"), Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"), Msg.translate(Env.getCtx(), "QtyToDeliver"), Msg.translate(Env.getCtx(), "QtyDelivered"), Msg.translate(Env.getCtx(), "QtyScrap") }, { productField, uomField, attribute, toDeliverQty, deliveredQtyField, scrapQtyField } };
            iText.append(this.createHTMLTable(table));
        }
        if (isBackflush || isOnlyIssue) {
            iText.append("<br /><br />");
            final ArrayList<String[]> table2 = new ArrayList<String[]>();
            table2.add(new String[] { Msg.translate(Env.getCtx(), "Value"), Msg.translate(Env.getCtx(), "Name"), Msg.translate(Env.getCtx(), "C_UOM_ID"), Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"), Msg.translate(Env.getCtx(), "QtyToDeliver"), Msg.translate(Env.getCtx(), "QtyDelivered"), Msg.translate(Env.getCtx(), "QtyScrap") });
            for (int i = 0; i < issue.getRowCount(); ++i) {
                final IDColumn id = (IDColumn)issue.getValueAt(i, 0);
                if (id != null && id.isSelected()) {
                    final KeyNamePair m_productkey = (KeyNamePair)issue.getValueAt(i, 3);
                    final int m_M_Product_ID = m_productkey.getKey();
                    final KeyNamePair m_uomkey = (KeyNamePair)issue.getValueAt(i, 4);
                    if (issue.getValueAt(i, 5) == null) {
                        final Timestamp minGuaranteeDate;
                        final Timestamp m_movementDate = minGuaranteeDate = this.getMovementDate();
                        final MStorageOnHand[] storages = MPPOrder.getStorages(Env.getCtx(), m_M_Product_ID, this.getPP_Order().getM_Warehouse_ID(), 0, minGuaranteeDate, null);
                        final BigDecimal todelivery = this.getValueBigDecimal(issue, i, 8);
                        final BigDecimal scrap = this.getValueBigDecimal(issue, i, 9);
                        BigDecimal toIssue = todelivery.add(scrap);
                        MStorageOnHand[] array;
                        for (int length = (array = storages).length, j = 0; j < length; ++j) {
                            final MStorageOnHand storage = array[j];
                            if (storage.getQtyOnHand().signum() != 0) {
                                BigDecimal issueact = toIssue;
                                if (issueact.compareTo(storage.getQtyOnHand()) > 0) {
                                    issueact = storage.getQtyOnHand();
                                }
                                toIssue = toIssue.subtract(issueact);
                                final String desc = new MAttributeSetInstance(Env.getCtx(), storage.getM_AttributeSetInstance_ID(), (String)null).getDescription();
                                final String[] row = { "", "", "", "", "0.00", "0.00", "0.00" };
                                row[0] = ((issue.getValueAt(i, 2) != null) ? issue.getValueAt(i, 2).toString() : "");
                                row[1] = m_productkey.toString();
                                row[2] = ((m_uomkey != null) ? m_uomkey.toString() : "");
                                row[3] = ((desc != null) ? desc : "");
                                row[4] = issueact.setScale(2, 4).toString();
                                row[5] = this.getValueBigDecimal(issue, i, 7).setScale(2, 4).toString();
                                row[6] = this.getValueBigDecimal(issue, i, 9).toString();
                                table2.add(row);
                                if (toIssue.signum() <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        final String[] row2 = { "", "", "", "", "0.00", "0.00", "0.00" };
                        row2[0] = ((issue.getValueAt(i, 2) != null) ? issue.getValueAt(i, 2).toString() : "");
                        row2[1] = m_productkey.toString();
                        row2[2] = ((m_uomkey != null) ? m_uomkey.toString() : "");
                        row2[3] = ((issue.getValueAt(i, 5) != null) ? issue.getValueAt(i, 5).toString() : "");
                        row2[4] = this.getValueBigDecimal(issue, i, 8).toString();
                        row2[5] = this.getValueBigDecimal(issue, i, 7).toString();
                        row2[6] = this.getValueBigDecimal(issue, i, 9).toString();
                        table2.add(row2);
                    }
                }
            }
            final String[][] tableArray = table2.toArray(new String[table2.size()][]);
            iText.append(this.createHTMLTable(tableArray));
        }
        return iText.toString();
    }
    
    protected BigDecimal getDeliveredQty() {
        return this.m_DeliveredQty;
    }
    
    protected int getM_AttributeSetInstance_ID() {
        return this.m_M_AttributeSetInstance_ID;
    }
    
    protected int getM_Locator_ID() {
        return this.m_M_Locator_ID;
    }
    
    protected Timestamp getMovementDate() {
        return this.m_movementDate;
    }
    
    protected BigDecimal getOpenQty() {
        return this.m_openQty;
    }
    
    protected BigDecimal getOrderedQty() {
        return this.m_orderedQty;
    }
    
    protected MPPOrder getPP_Order() {
        final int id = this.getPP_Order_ID();
        if (id <= 0) {
            return this.m_PP_order = null;
        }
        if (this.m_PP_order == null || this.m_PP_order.get_ID() != id) {
            this.m_PP_order = new MPPOrder(Env.getCtx(), id, null);
        }
        return this.m_PP_order;
    }
    
    protected int getPP_Order_ID() {
        return this.m_PP_Order_ID;
    }
    
    protected BigDecimal getQtyBatchs() {
        return this.m_qtyBatchs;
    }
    
    protected BigDecimal getQtyBatchSize() {
        return this.m_qtyBatchSize;
    }
    
    protected BigDecimal getRejectQty() {
        return this.m_rejectQty;
    }
    
    protected BigDecimal getScrapQty() {
        return this.m_scrapQty;
    }
    
    protected BigDecimal getToDeliverQty() {
        return this.m_toDeliverQty;
    }
    
    private BigDecimal getValueBigDecimal(final IMiniTable issue, final int row, final int col) {
        final BigDecimal bd = (BigDecimal)issue.getValueAt(row, col);
        return (bd == null) ? Env.ZERO : bd;
    }
    
    protected boolean isBackflush() {
        return this.m_IsBackflush;
    }
    
    protected boolean isOnlyIssue() {
        return this.m_OnlyIssue;
    }
    
    protected boolean isOnlyReceipt() {
        return this.m_isOnlyReceipt;
    }
    
    private int lotes(int row, final IDColumn id, final int Warehouse_ID, final int M_Product_ID, final BigDecimal qtyRequired, final BigDecimal qtyToDelivery, final IMiniTable issue) {
        int linesNo = 0;
        BigDecimal qtyRequiredActual = qtyRequired;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement("SELECT s.M_Product_ID , s.QtyOnHand, s.M_AttributeSetInstance_ID, p.Name, masi.Description, l.Value, w.Value, w.M_warehouse_ID,p.Value  FROM M_Storage s  INNER JOIN M_Product p ON (s.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (u.C_UOM_ID = p.C_UOM_ID)  INNER JOIN M_AttributeSetInstance masi ON (masi.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = ?)  INNER JOIN M_Locator l ON(l.M_Warehouse_ID=w.M_Warehouse_ID and s.M_Locator_ID=l.M_Locator_ID)  WHERE s.M_Product_ID = ? and s.QtyOnHand > 0  and s.M_AttributeSetInstance_ID <> 0  ORDER BY s.Created ", (String)null);
            pstmt.setInt(1, Warehouse_ID);
            pstmt.setInt(2, M_Product_ID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                issue.setRowCount(row + 1);
                final BigDecimal qtyOnHand = rs.getBigDecimal(2);
                final IDColumn id2 = new IDColumn(rs.getInt(3));
                id2.setSelected(false);
                issue.setValueAt((Object)id2, row, 0);
                final KeyNamePair productkey = new KeyNamePair(rs.getInt(1), rs.getString(4));
                issue.setValueAt((Object)productkey, row, 3);
                issue.setValueAt((Object)qtyOnHand, row, 10);
                issue.setValueAt((Object)rs.getString(5), row, 5);
                issue.setValueAt((Object)rs.getString(6), row, 13);
                final KeyNamePair m_warehousekey = new KeyNamePair(rs.getInt(8), rs.getString(7));
                issue.setValueAt((Object)m_warehousekey, row, 14);
                issue.setValueAt((Object)qtyRequired, row, 6);
                issue.setValueAt((Object)Env.ZERO, row, 9);
                if (qtyRequiredActual.compareTo(qtyOnHand) < 0) {
                    issue.setValueAt((Object)((qtyRequiredActual.signum() > 0) ? qtyRequiredActual : Env.ZERO), row, 6);
                }
                else {
                    issue.setValueAt((Object)qtyOnHand, row, 6);
                }
                if (qtyRequiredActual.compareTo(qtyOnHand) < 0) {
                    issue.setValueAt((Object)((qtyRequiredActual.signum() > 0) ? qtyRequiredActual : Env.ZERO), row, 8);
                }
                else {
                    issue.setValueAt((Object)qtyOnHand, row, 8);
                }
                qtyRequiredActual = qtyRequiredActual.subtract(qtyOnHand);
                ++linesNo;
                ++row;
            }
        }
        catch (SQLException e) {
            throw new DBException((Exception)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        DB.close(rs, (Statement)pstmt);
        rs = null;
        pstmt = null;
        return linesNo;
    }
    
    public void saveSelection(final IMiniTable miniTable) {
        OrderReceiptIssue.log.info("");
        final ArrayList<Integer> results = new ArrayList<Integer>();
        this.setSelection((ArrayList)null);
        for (int rows = miniTable.getRowCount(), i = 0; i < rows; ++i) {
            final IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);
            if (id != null && id.isSelected()) {
                results.add(id.getRecord_ID());
            }
        }
        if (results.size() == 0) {
            return;
        }
        OrderReceiptIssue.log.config("Selected #" + results.size());
        this.setSelection((ArrayList)results);
    }
    
    protected void setDeliveredQty(final BigDecimal qty) {
        this.m_DeliveredQty = qty;
    }
    
    protected void setIsBackflush(final boolean IsBackflush) {
        this.m_IsBackflush = IsBackflush;
    }
    
    protected void setIsOnlyIssue(final boolean onlyIssue) {
        this.m_OnlyIssue = onlyIssue;
    }
    
    protected void setIsOnlyReceipt(final boolean isOnlyReceipt) {
        this.m_isOnlyReceipt = isOnlyReceipt;
    }
    
    protected void setM_AttributeSetInstance_ID(final int M_AttributeSetInstance_ID) {
        this.m_M_AttributeSetInstance_ID = M_AttributeSetInstance_ID;
    }
    
    protected void setM_Locator_ID(final int M_Locator_ID) {
        this.m_M_Locator_ID = M_Locator_ID;
    }
    
    protected void setMovementDate(final Timestamp date) {
        this.m_movementDate = date;
    }
    
    protected void setOpenQty(final BigDecimal qty) {
        this.m_openQty = qty;
    }
    
    protected void setOrderedQty(final BigDecimal qty) {
        this.m_orderedQty = qty;
    }
    
    protected void setPP_Order_ID(final int PP_Order_ID) {
        this.m_PP_Order_ID = PP_Order_ID;
    }
    
    protected void setQtyBatchs(final BigDecimal qty) {
        this.m_qtyBatchs = qty;
    }
    
    protected void setQtyBatchSize(final BigDecimal qty) {
        this.m_qtyBatchSize = qty;
    }
    
    protected void setRejectQty(final BigDecimal qty) {
        this.m_rejectQty = qty;
    }
    
    protected void setScrapQty(final BigDecimal qty) {
        this.m_scrapQty = qty;
    }
    
    protected void setToDeliverQty(final BigDecimal qty) {
        this.m_toDeliverQty = qty;
    }
    
    public void showMessage(final String message, final boolean error) {
    }
}
