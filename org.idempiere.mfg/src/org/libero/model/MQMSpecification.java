// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.math.BigDecimal;
import org.compiere.model.MAttributeInstance;
import org.compiere.model.MAttribute;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MAttributeSetInstance;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import org.compiere.util.DB;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_QM_Specification;

public class MQMSpecification extends X_QM_Specification
{
    private MQMSpecificationLine[] m_lines;
    
    public MQMSpecification(final Properties ctx, final int QM_Specification_ID, final String trxName) {
        super(ctx, QM_Specification_ID, trxName);
        this.m_lines = null;
    }
    
    public MQMSpecification(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
        this.m_lines = null;
    }
    
    public MQMSpecificationLine[] getLines(final String where) {
        if (this.m_lines != null) {
            return this.m_lines;
        }
        final ArrayList<MQMSpecificationLine> list = new ArrayList<MQMSpecificationLine>();
        final String sql = "SELECT * FROM QM_SpecificationLine WHERE QM_SpecificationLine_ID=? AND " + where + " ORDER BY Line";
        PreparedStatement pstmt = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, this.get_TrxName());
            pstmt.setInt(1, this.getQM_Specification_ID());
            final ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MQMSpecificationLine(this.getCtx(), rs, this.get_TrxName()));
            }
            rs.close();
            pstmt.close();
            pstmt = null;
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, "getLines", (Throwable)e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = null;
        }
        catch (Exception ex) {
            pstmt = null;
        }
        list.toArray(this.m_lines = new MQMSpecificationLine[list.size()]);
        return this.m_lines;
    }
    
    public boolean isValid(final int M_AttributeSetInstance_ID) {
        final MAttributeSetInstance asi = new MAttributeSetInstance(this.getCtx(), M_AttributeSetInstance_ID, this.get_TrxName());
        final MAttributeSet as = MAttributeSet.get(this.getCtx(), asi.getM_AttributeSet_ID());
        final MAttribute[] attributes = as.getMAttributes(false);
        for (int i = 0; i < attributes.length; ++i) {
            final MAttributeInstance instance = attributes[i].getMAttributeInstance(M_AttributeSetInstance_ID);
            final MQMSpecificationLine[] lines = this.getLines(" M_Attribute_ID=" + attributes[i].getM_Attribute_ID());
            final int s = 0;
            while (s < lines.length) {
                final MQMSpecificationLine line = lines[s];
                if ("N".equals(attributes[i].getAttributeValueType())) {
                    final BigDecimal objValue = instance.getValueNumber();
                    if (!line.evaluate(objValue, instance.getValue())) {}
                    return false;
                }
                final String objValue2 = instance.getValue();
                if (!line.evaluate(objValue2, instance.getValue())) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
}
