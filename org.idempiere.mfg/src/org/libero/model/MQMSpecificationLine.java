// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.libero.tables.X_QM_SpecificationLine;

public class MQMSpecificationLine extends X_QM_SpecificationLine
{
    private static final long serialVersionUID = 1L;
    
    public MQMSpecificationLine(final Properties ctx, final int QM_SpecificationLine_ID, final String trxName) {
        super(ctx, QM_SpecificationLine_ID, trxName);
    }
    
    public MQMSpecificationLine(final Properties ctx, final ResultSet rs, final String trxName) {
        super(ctx, rs, trxName);
    }
    
    public boolean evaluate(final Object valueObj, final String value1) {
        boolean result = false;
        if (valueObj instanceof Number) {
            result = this.compareNumber((Number)valueObj, value1, this.getValue());
        }
        else {
            result = this.compareString(valueObj, value1, this.getValue());
        }
        return result;
    }
    
    private boolean compareNumber(final Number valueObj, final String value1, final String value2) {
        BigDecimal valueObjB = null;
        BigDecimal value1B = null;
        BigDecimal value2B = null;
        try {
            if (valueObj instanceof BigDecimal) {
                valueObjB = (BigDecimal)valueObj;
            }
            else if (valueObj instanceof Integer) {
                valueObjB = new BigDecimal((int)valueObj);
            }
            else {
                valueObjB = new BigDecimal(String.valueOf(valueObj));
            }
        }
        catch (Exception e) {
            this.log.fine("compareNumber - valueObj=" + valueObj + " - " + e.toString());
            return this.compareString(valueObj, value1, value2);
        }
        try {
            value1B = new BigDecimal(value1);
        }
        catch (Exception e) {
            this.log.fine("compareNumber - value1=" + value1 + " - " + e.toString());
            return this.compareString(valueObj, value1, value2);
        }
        final String op = this.getOperation();
        if ("==".equals(op)) {
            return valueObjB.compareTo(value1B) == 0;
        }
        if (">>".equals(op)) {
            return valueObjB.compareTo(value1B) > 0;
        }
        if (">=".equals(op)) {
            return valueObjB.compareTo(value1B) >= 0;
        }
        if ("<<".equals(op)) {
            return valueObjB.compareTo(value1B) < 0;
        }
        if ("<=".equals(op)) {
            return valueObjB.compareTo(value1B) <= 0;
        }
        if ("~~".equals(op)) {
            return valueObjB.compareTo(value1B) == 0;
        }
        if ("!=".equals(op)) {
            return valueObjB.compareTo(value1B) != 0;
        }
        if ("SQ".equals(op)) {
            throw new IllegalArgumentException("SQL not Implemented");
        }
        if ("AB".equals(op)) {
            if (valueObjB.compareTo(value1B) < 0) {
                return false;
            }
            try {
                value2B = new BigDecimal(String.valueOf(value2));
                return valueObjB.compareTo(value2B) <= 0;
            }
            catch (Exception e2) {
                this.log.fine("compareNumber - value2=" + value2 + " - " + e2.toString());
                return false;
            }
        }
        throw new IllegalArgumentException("Unknown Operation=" + op);
    }
    
    private boolean compareString(final Object valueObj, final String value1S, final String value2S) {
        final String valueObjS = String.valueOf(valueObj);
        final String op = this.getOperation();
        if ("==".equals(op)) {
            return valueObjS.compareTo(value1S) == 0;
        }
        if (">>".equals(op)) {
            return valueObjS.compareTo(value1S) > 0;
        }
        if (">=".equals(op)) {
            return valueObjS.compareTo(value1S) >= 0;
        }
        if ("<<".equals(op)) {
            return valueObjS.compareTo(value1S) < 0;
        }
        if ("<=".equals(op)) {
            return valueObjS.compareTo(value1S) <= 0;
        }
        if ("~~".equals(op)) {
            return valueObjS.compareTo(value1S) == 0;
        }
        if ("!=".equals(op)) {
            return valueObjS.compareTo(value1S) != 0;
        }
        if ("SQ".equals(op)) {
            throw new IllegalArgumentException("SQL not Implemented");
        }
        if ("AB".equals(op)) {
            return valueObjS.compareTo(value1S) >= 0 && valueObjS.compareTo(value2S) <= 0;
        }
        throw new IllegalArgumentException("Unknown Operation=" + op);
    }
}
