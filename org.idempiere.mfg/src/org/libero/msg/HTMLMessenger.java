// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.msg;

import org.compiere.model.MStorageReservation;
import org.compiere.model.MLocator;
import org.compiere.model.MWarehouse;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MAttributeValue;
import org.compiere.model.MAttribute;
import org.compiere.model.MAttributeInstance;
import org.libero.model.reasoner.StorageReasoner;
import org.compiere.model.MAttributeSet;
import org.libero.model.wrapper.BOMWrapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.compiere.model.MAttributeSetInstance;
import org.libero.model.wrapper.BOMLineWrapper;
import org.compiere.model.MProject;
import org.libero.model.MPPOrder;
import java.math.BigDecimal;
import java.text.MessageFormat;
import org.compiere.model.MProduct;
import org.compiere.util.Msg;
import org.compiere.util.Env;

public class HTMLMessenger
{
    protected final String PRODUCT_TOOLTIP;
    protected final String LENGTHTRANSFORM_INFO_PATTERN = "<html><table cellpadding=\"5\" cellspacing=\"5\"><tr><td><b>{0}</b></td></tr><tr><td>{1}</td></tr><tr><td>{2}</td></tr></table></html>";
    protected final String PP_ORDER_INFO_PATTERN;
    protected final String PP_ORDER_HEADER_INFO_PATTERN;
    protected final String PP_ORDER_LINE_INFO_PATTERN = "<html><table cellpadding=\"5\" cellspacing=\"5\"><tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td><td>{5}</td><td>{6}</td></tr></table></html>";
    protected final String BOM_INFO_PATTERN;
    protected final String BOM_HEADER_INFO_PATTERN;
    protected final String BOM_LINE_INFO_PATTERN = "<tr><td align=RIGHT>{0}</td><td align=RIGHT>{1}</td><td>{2}</td><td>{3}</td></tr>";
    protected final String BOMLINE_INFO_PATTERN;
    protected final String STORAGE_HEADER_INFO_PATTERN;
    protected final String STORAGE_LINE_INFO_PATTERN = "<tr><td>{0}</td><td>{1}</td><td align=RIGHT>{2}</td><td align=RIGHT>{3}</td><td align=RIGHT>{4}</td><td align=RIGHT>{5}</td></tr>";
    protected final String STORAGE_SUM_LINE_INFO_PATTERN = "<tr><td></td><td></td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{0}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{1}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{2}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{3}</td></tr>";
    protected final String STORAGE_NOINVENTORY_INFO_PATTERN;
    protected final String STORAGE_FOOTER_INFO_PATTERN = "</table>";
    protected final String ATTRIBUTE_INFO_PATTERN = "{0}&nbsp;=&nbsp;<i>{1}</i>";
    
    public HTMLMessenger() {
        this.PRODUCT_TOOLTIP = "<html><H1 align=\"CENTER\">" + Msg.translate(Env.getCtx(), "M_Product_ID") + "</H1>" + "<table cellpadding=\"5\" cellspacing=\"5\">" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "Description") + ":</b></td><td>{0}</td></tr>" + "</table></html>";
        this.PP_ORDER_INFO_PATTERN = "<html><H1 align=\"CENTER\">" + Msg.translate(Env.getCtx(), "PP_Order_ID") + "</H1>" + "<table cellpadding=\"5\" cellspacing=\"5\">" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "DocumentNo") + ":</b></td><td>{0}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "DateStartSchedule") + ":</b></td><td>{1}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "DateFinishSchedule") + ":</b></td><td>{2}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "C_Project_ID") + ":</b></td><td>{3}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "M_Product_ID") + ":</b></td><td>{4}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "QtyOrdered") + ":</b></td><td>{5}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "QtyDelivered") + ":</b></td><td>{6}</td></tr>" + "</table></html>";
        this.PP_ORDER_HEADER_INFO_PATTERN = "<html><H1 align=\"LEFT\">{0}</H1><table cellpadding=\"5\" cellspacing=\"5\"><tr><td><b>" + Msg.translate(Env.getCtx(), "DocumentNo") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "DateStartSchedule") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "DateFinishSchedule") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "C_Project_ID") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "M_Product_ID") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyOrdered") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyDelivered") + "</b></td>" + "<tr>" + "</table></html>";
        this.BOM_INFO_PATTERN = "<html><H1 align=\"CENTER\">" + Msg.translate(Env.getCtx(), "PP_Product_BOM_ID") + "</H1>" + "<table cellpadding=\"5\" cellspacing=\"5\">" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "DocumentNo") + ":</b></td><td>{0}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "PP_Product_BOM_ID") + ":</b></td><td>{1}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "ValidFrom") + ":</b></td><td>{2} - {3}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "Value") + ":</b></td><td>{4}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "M_Product_ID") + ":</b></td><td>{5}</td></tr>" + "<tr><td></td><td>{6}</td></tr>" + "</table>" + "<p>{7}</p>" + "</html>";
        this.BOM_HEADER_INFO_PATTERN = "<table align=\"CENTER\" cellpadding=\"5\" cellspacing=\"5\"><tr><td><b>" + Msg.translate(Env.getCtx(), "Line") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "Qty") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "M_Product_ID") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID") + "</b></td>" + "</tr>";
        this.BOMLINE_INFO_PATTERN = "<html><H1 align=\"CENTER\">" + Msg.translate(Env.getCtx(), "Line") + ":&nbsp;{0}</H1>" + "<table cellpadding=\"5\" cellspacing=\"5\">" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "ComponentType") + ":</b></td><td>{1}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "ValidFrom") + ":</b></td><td>{2} - {3}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "Qty") + ":</b></td><td>{4}</td></tr>" + "<tr><td><b>" + Msg.translate(Env.getCtx(), "M_Product_ID") + ":</b></td><td>{5}</td></tr>" + "<tr><td></td><td>{6}</td></tr>" + "</table>" + "<p>{7}</p>" + "</html>";
        this.STORAGE_HEADER_INFO_PATTERN = "<table align=\"CENTER\" cellpadding=\"5\" cellspacing=\"5\"><tr><td><b>" + Msg.translate(Env.getCtx(), "M_Locator_ID") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "M_Warehouse_ID") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyOnHand") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyReserved") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyOrdered") + "</b></td>" + "<td><b>" + Msg.translate(Env.getCtx(), "QtyAvailable") + "</b></td>" + "</tr>";
        this.STORAGE_NOINVENTORY_INFO_PATTERN = "<tr><td align=\"CENTER\" colspan=\"6\">" + Msg.translate(Env.getCtx(), Msg.getMsg(Env.getCtx(), "NoQtyAvailable")) + "</td>" + "</tr>";
    }
    
    public String getProductInfo(final MProduct p) {
        final Object[] obj = { (p.getDescription() == null) ? "" : p.getDescription() };
        return MessageFormat.format(this.PRODUCT_TOOLTIP, obj);
    }
    
    public String getLengthTransformInfo(final MProduct p, final BigDecimal srcLength, final BigDecimal tgtLength, final BigDecimal pieces) {
        final BigDecimal scrapLength = srcLength.subtract(tgtLength.multiply(pieces));
        final Object[] obj = { String.valueOf(p.getName()) + " (" + p.getValue() + ")", "1 x " + srcLength.setScale(2, 5) + " &#8594; " + pieces + " x " + tgtLength.setScale(2, 5), String.valueOf(Msg.translate(Env.getCtx(), "Scrap")) + ": 1 x " + scrapLength.setScale(2, 5) };
        return MessageFormat.format("<html><table cellpadding=\"5\" cellspacing=\"5\"><tr><td><b>{0}</b></td></tr><tr><td>{1}</td></tr><tr><td>{2}</td></tr></table></html>", obj);
    }
    
    public String getMfcOrderInfo(final MPPOrder o) {
        final MProject pj = new MProject(Env.getCtx(), o.getC_Project_ID(), (String)null);
        final MProduct pd = new MProduct(Env.getCtx(), o.getM_Product_ID(), (String)null);
        final Object[] obj = { o.getDocumentNo(), o.getDateStartSchedule(), o.getDateFinishSchedule(), String.valueOf((pj.getName() == null) ? "-" : pj.getName()) + ((pj.getValue() == null) ? "" : (" (" + pj.getValue() + ")")), String.valueOf(pd.getName()) + " (" + pd.getValue() + ")", o.getQtyOrdered(), o.getQtyDelivered() };
        return MessageFormat.format(this.PP_ORDER_INFO_PATTERN, obj);
    }
    
    public String getBOMLinesInfo(final BOMLineWrapper[] lines) {
        MProduct p = null;
        MAttributeSetInstance asi = null;
        final StringBuffer sb = new StringBuffer(this.BOM_HEADER_INFO_PATTERN);
        for (int i = 0; i < lines.length; ++i) {
            p = new MProduct(Env.getCtx(), lines[i].getM_Product_ID(), "M_Product");
            asi = new MAttributeSetInstance(Env.getCtx(), lines[i].getM_AttributeSetInstance_ID(), "M_AttributeSetInstance");
            final Object[] obj = { new Integer(lines[i].getPo()), lines[i].getQtyBOM(), p.getName(), this.getAttributeSetInstanceInfo(asi, true) };
            sb.append(MessageFormat.format("<tr><td align=RIGHT>{0}</td><td align=RIGHT>{1}</td><td>{2}</td><td>{3}</td></tr>", obj));
        }
        return sb.toString();
    }
    
    public String getBOMLineInfo(final BOMLineWrapper mpbl) {
        final SimpleDateFormat df = Env.getLanguage(Env.getCtx()).getDateFormat();
        final MProduct p = new MProduct(Env.getCtx(), mpbl.getM_Product_ID(), "M_Product");
        final MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), mpbl.getM_AttributeSetInstance_ID(), "M_AttributeSetInstance");
        final Object[] obj = { new Integer(mpbl.getPo()), mpbl.getComponentType(), (mpbl.getValidFrom() == null) ? "" : df.format(mpbl.getValidFrom()), (mpbl.getValidTo() == null) ? "" : df.format(mpbl.getValidTo()), mpbl.getQtyBOM(), p.getName(), this.getAttributeSetInstanceInfo(asi, false), this.getStorageInfo(p, asi) };
        return MessageFormat.format(this.BOMLINE_INFO_PATTERN, obj);
    }
    
    public String getBOMInfo(final BOMWrapper pb) {
        final SimpleDateFormat df = Env.getLanguage(Env.getCtx()).getDateFormat();
        final MProduct p = new MProduct(Env.getCtx(), pb.getM_Product_ID(), "M_Product");
        final MAttributeSetInstance asi = new MAttributeSetInstance(Env.getCtx(), pb.getM_AttributeSetInstance_ID(), "M_AttributeSetInstance");
        final Object[] obj = { pb.getDocumentNo(), pb.getName(), (pb.getValidFrom() == null) ? "" : df.format(pb.getValidFrom()), (pb.getValidTo() == null) ? "" : df.format(pb.getValidTo()), pb.getValue(), p.getName(), this.getAttributeSetInstanceInfo(asi, false), this.getBOMLinesInfo(pb.getLines()) };
        return MessageFormat.format(this.BOM_INFO_PATTERN, obj);
    }
    
    public String getAttributeSetInstanceInfo(final MAttributeSetInstance asi, final boolean singleRow) {
        new MAttributeSet(Env.getCtx(), asi.getM_AttributeSet_ID(), (String)null);
        final StorageReasoner mr = new StorageReasoner();
        final int[] ids = mr.getAttributeIDs(asi);
        MAttributeInstance ai = null;
        MAttribute a = null;
        MAttributeValue av = null;
        final StringBuffer sb = new StringBuffer();
        String value = null;
        Object[] obj = null;
        for (int i = 0; i < ids.length; ++i) {
            ai = new MAttributeInstance(Env.getCtx(), ids[i], asi.get_ID(), (String)null, (String)null);
            ai.load((String)null, new String[0]);
            a = new MAttribute(Env.getCtx(), ai.getM_Attribute_ID(), (String)null);
            av = new MAttributeValue(Env.getCtx(), ai.getM_AttributeValue_ID(), (String)null);
            if (ai.getValue() == null) {
                value = av.getValue();
            }
            else if ("N".equals(a.getAttributeValueType())) {
                final BigDecimal number = ai.getValueNumber();
                value = number.setScale(2, 4).toString();
            }
            else {
                value = ai.getValue();
            }
            obj = new Object[] { a.getName(), value };
            sb.append(MessageFormat.format("{0}&nbsp;=&nbsp;<i>{1}</i>", obj));
            if (singleRow) {
                sb.append("&nbsp;");
            }
            else {
                sb.append("<br>");
            }
        }
        return sb.toString();
    }
    
    public String getStorageInfo(final MProduct p, final MAttributeSetInstance asi) {
        final StorageReasoner mr = new StorageReasoner();
        final int[] ids = mr.getPOIDs("M_Locator", null, null);
        MWarehouse warehouse = null;
        MStorageOnHand storage = null;
        MLocator locator = null;
        final StringBuffer sb = new StringBuffer(this.STORAGE_HEADER_INFO_PATTERN);
        Object[] obj = null;
        BigDecimal sumQtyOnHand = BigDecimal.ZERO;
        BigDecimal sumQtyAvailable = BigDecimal.ZERO;
        int count = 0;
        for (int i = 0; i < ids.length; ++i) {
            storage = MStorageOnHand.get(Env.getCtx(), ids[i], p.get_ID(), asi.get_ID(), (String)null);
            if (storage != null) {
                ++count;
                warehouse = new MWarehouse(Env.getCtx(), storage.getM_Warehouse_ID(), (String)null);
                locator = new MLocator(Env.getCtx(), storage.getM_Locator_ID(), (String)null);
                final BigDecimal available = MStorageReservation.getQtyAvailable(storage.getM_Warehouse_ID(), p.get_ID(), asi.get_ID(), (String)null);
                sumQtyOnHand = sumQtyOnHand.add(storage.getQtyOnHand());
                sumQtyAvailable = sumQtyAvailable.add(available);
                obj = new Object[] { String.valueOf(locator.getX()) + " - " + locator.getY() + " - " + locator.getZ(), warehouse.getName(), storage.getQtyOnHand(), storage.getQtyOnHand().subtract(sumQtyAvailable), sumQtyAvailable };
                sb.append(MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td align=RIGHT>{2}</td><td align=RIGHT>{3}</td><td align=RIGHT>{4}</td><td align=RIGHT>{5}</td></tr>", obj));
            }
        }
        if (count > 1) {
            obj = new Object[] { sumQtyOnHand, storage.getQtyOnHand().subtract(sumQtyAvailable), sumQtyAvailable };
            sb.append(MessageFormat.format("<tr><td></td><td></td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{0}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{1}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{2}</td><td align=RIGHT><hr size=\"1\" noshade=\"NOSHADE\">{3}</td></tr>", obj));
        }
        final double available2 = sumQtyAvailable.setScale(2, 4).doubleValue();
        if (count == 0 || available2 <= 0.0) {
            sb.append(MessageFormat.format(this.STORAGE_NOINVENTORY_INFO_PATTERN, obj));
        }
        sb.append("</table>");
        return sb.toString();
    }
}
