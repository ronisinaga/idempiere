// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form.crp;

import org.jfree.data.category.CategoryDataset;
import java.math.RoundingMode;
import org.libero.model.MPPOrderWorkflow;
import java.text.SimpleDateFormat;
import org.compiere.model.MProduct;
import javax.swing.tree.MutableTreeNode;
import org.libero.model.MPPOrderNode;
import org.libero.model.MPPOrder;
import java.text.DateFormat;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import org.compiere.util.TimeUtil;
import org.compiere.model.I_S_Resource;
import java.util.Map;
import java.util.Date;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;

import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;
import org.compiere.model.MUOMConversion;
import org.compiere.model.MUOM;
import org.compiere.model.MResourceType;
import org.compiere.util.Env;
import org.compiere.model.MResource;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.sql.RowSet;
import javax.swing.JTree;
import org.libero.model.reasoner.CRPReasoner;

public abstract class CRPDatasetFactory extends CRPReasoner implements CRPModel
{
    protected JTree tree;
    protected DefaultCategoryDataset dataset;
    
    protected abstract BigDecimal convert(final BigDecimal p0);
    
    public static CRPModel get(final Timestamp start, final Timestamp end, final MResource r) {
        final MResourceType t = MResourceType.get(Env.getCtx(), r.getS_ResourceType_ID());
        final MUOM uom1 = MUOM.get(Env.getCtx(), MUOM.getMinute_UOM_ID(Env.getCtx()));
        final MUOM uom2 = MUOM.get(Env.getCtx(), t.getC_UOM_ID());
        final CRPDatasetFactory factory = new CRPDatasetFactory() {
            @Override
            protected BigDecimal convert(final BigDecimal minutes) {
                return MUOMConversion.convert(Env.getCtx(), uom1.get_ID(), uom2.get_ID(), minutes);
            }
        };
        factory.generate(start, end, r);
        return factory;
    }
    
    private void generate(final Timestamp start, final Timestamp end, final MResource r) {
        if (start == null || end == null || r == null) {
            return;
        }
        final String labelActCap = Msg.translate(Env.getCtx(), "DailyCapacity");
        final String labelLoadAct = Msg.translate(Env.getCtx(), "ActualLoad");
        final DateFormat formatter = DisplayType.getDateFormat(16, Env.getLanguage(Env.getCtx()));
        final BigDecimal dailyCapacity = this.getMaxRange(r);
        this.dataset = new DefaultCategoryDataset();
        final HashMap<DefaultMutableTreeNode, String> names = new HashMap<DefaultMutableTreeNode, String>();
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(r);
        names.put(root, this.getTreeNodeRepresentation(null, root, r));
        
        SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        String sql = "SELECT * FROM pp_crpinfo ("+ r.get_ID() + ", '" + dmyFormat.format(start) + "', '" + dmyFormat.format(end) + "')";
        RowSet rs2 = DB.getRowSet(sql);
        
        //>>astina
        try {
			while (rs2.next())
			{
	            this.dataset.addValue((Number)(rs2.getDouble(1)), (Comparable)labelActCap, (Comparable)rs2.getString(3));
	            this.dataset.addValue((Number)(rs2.getDouble(2)), (Comparable)labelLoadAct, (Comparable)rs2.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
//        for (Timestamp dateTime = start; end.after(dateTime); dateTime = TimeUtil.addDays(dateTime, 1)) {
//            final String label = formatter.format(dateTime);
//            names.putAll(this.addTreeNodes(dateTime, root, r));
//            final boolean available = this.isAvailable((I_S_Resource)r, dateTime);
//            this.dataset.addValue((Number)(available ? dailyCapacity : BigDecimal.ZERO), (Comparable)labelActCap, (Comparable)label);
//            this.dataset.addValue((Number)(available ? this.calculateLoad(dateTime, r, null) : BigDecimal.ZERO), (Comparable)labelLoadAct, (Comparable)label);
//        }
      //<<astina
        (this.tree = new JTree(root)).setCellRenderer(new DiagramTreeCellRenderer(names));
    }
    
    @Override
    public BigDecimal calculateLoad(final Timestamp dateTime, final MResource r, final String docStatus) {
        final MResourceType t = MResourceType.get(Env.getCtx(), r.getS_ResourceType_ID());
        MUOM.get(Env.getCtx(), t.getC_UOM_ID());
        long millis = 0L;
        MPPOrderNode[] ppOrderNodes;
        for (int length = (ppOrderNodes = this.getPPOrderNodes(dateTime, (I_S_Resource)r)).length, i = 0; i < length; ++i) {
            final MPPOrderNode node = ppOrderNodes[i];
            if (docStatus != null) {
                final MPPOrder o = new MPPOrder(node.getCtx(), node.getPP_Order_ID(), node.get_TrxName());
                if (!o.getDocStatus().equals(docStatus)) {
                    continue;
                }
            }
            millis += this.calculateMillisForDay(dateTime, node, t);
        }
        final BigDecimal scale = new BigDecimal(60000);
        final BigDecimal minutes = new BigDecimal(millis).divide(scale, 2, 4);
        return this.convert(minutes);
    }
    
    private Timestamp[] getDayBorders(final Timestamp dateTime, final MPPOrderNode node, final MResourceType t) {
        Timestamp endDayTime = t.getDayEnd(dateTime);
        endDayTime = (endDayTime.before(node.getDateFinishSchedule()) ? endDayTime : node.getDateFinishSchedule());
        Timestamp startDayTime = t.getDayStart(dateTime);
        startDayTime = (startDayTime.after(node.getDateStartSchedule()) ? startDayTime : node.getDateStartSchedule());
        return new Timestamp[] { startDayTime, endDayTime };
    }
    
    private long calculateMillisForDay(final Timestamp dateTime, final MPPOrderNode node, final MResourceType t) {
        final Timestamp[] borders = this.getDayBorders(dateTime, node, t);
        return borders[1].getTime() - borders[0].getTime();
    }
    
    private HashMap<DefaultMutableTreeNode, String> addTreeNodes(final Timestamp dateTime, final DefaultMutableTreeNode root, final MResource r) {
        final HashMap<DefaultMutableTreeNode, String> names = new HashMap<DefaultMutableTreeNode, String>();
        final DefaultMutableTreeNode parent = new DefaultMutableTreeNode(dateTime);
        names.put(parent, this.getTreeNodeRepresentation(null, parent, r));
        root.add(parent);
        MPPOrder[] ppOrders;
        for (int length = (ppOrders = this.getPPOrders(dateTime, (I_S_Resource)r)).length, i = 0; i < length; ++i) {
            final MPPOrder order = ppOrders[i];
            final DefaultMutableTreeNode childOrder = new DefaultMutableTreeNode(order);
            parent.add(childOrder);
            names.put(childOrder, this.getTreeNodeRepresentation(dateTime, childOrder, r));
            MPPOrderNode[] ppOrderNodes;
            for (int length2 = (ppOrderNodes = this.getPPOrderNodes(dateTime, (I_S_Resource)r)).length, j = 0; j < length2; ++j) {
                final MPPOrderNode node = ppOrderNodes[j];
                final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
                childOrder.add(childNode);
                names.put(childNode, this.getTreeNodeRepresentation(dateTime, childNode, r));
            }
        }
        return names;
    }
    
    private String getTreeNodeRepresentation(final Timestamp dateTime, final DefaultMutableTreeNode node, final MResource r) {
        String name = null;
        if (node.getUserObject() instanceof MResource) {
            final MResource res = (MResource)node.getUserObject();
            name = res.getName();
        }
        else if (node.getUserObject() instanceof Timestamp) {
            final Timestamp d = (Timestamp)node.getUserObject();
            final SimpleDateFormat df = Env.getLanguage(Env.getCtx()).getDateFormat();
            name = df.format(d);
            if (!this.isAvailable((I_S_Resource)r, d)) {
                name = "{" + name + "}";
            }
        }
        else if (node.getUserObject() instanceof MPPOrder) {
            final MPPOrder o = (MPPOrder)node.getUserObject();
            final MProduct p = MProduct.get(Env.getCtx(), o.getM_Product_ID());
            name = String.valueOf(o.getDocumentNo()) + " (" + p.getName() + ")";
        }
        else if (node.getUserObject() instanceof MPPOrderNode) {
            final MPPOrderNode on = (MPPOrderNode)node.getUserObject();
            final MPPOrderWorkflow owf = on.getMPPOrderWorkflow();
            final MResourceType rt = MResourceType.get(Env.getCtx(), r.getS_ResourceType_ID());
            final SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
            final Timestamp[] interval = this.getDayBorders(dateTime, on, rt);
            name = String.valueOf(df2.format(interval[0])) + " - " + df2.format(interval[1]) + " " + on.getName() + " (" + owf.getName() + ")";
        }
        return name;
    }
    
    private BigDecimal getMaxRange(final MResource r) {
        final BigDecimal utilizationDec = r.getPercentUtilization().divide(Env.ONEHUNDRED, 2, RoundingMode.HALF_UP);
        final int precision = 2;
        return r.getDailyCapacity().multiply(utilizationDec).setScale(precision, RoundingMode.HALF_UP);
    }
    
    @Override
    public CategoryDataset getDataset() {
        return (CategoryDataset)this.dataset;
    }
    
    @Override
    public JTree getTree() {
        return this.tree;
    }
}
