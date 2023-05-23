// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import java.util.List;
import org.compiere.util.Util;
import java.util.ArrayList;
import org.compiere.model.MTable;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.Query;
import org.compiere.util.Env;
import java.util.Properties;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MProduct;

public class CostDimension
{
    public static final int ANY = -10;
    private int AD_Client_ID;
    private int AD_Org_ID;
    private int M_Product_ID;
    private int S_Resource_ID;
    private int M_AttributeSetInstance_ID;
    private int M_CostType_ID;
    private int C_AcctSchema_ID;
    private int M_CostElement_ID;
    
    public CostDimension(final MProduct product, final MAcctSchema as, final int M_CostType_ID, final int AD_Org_ID, final int M_ASI_ID, final int M_CostElement_ID) {
        this.AD_Client_ID = as.getAD_Client_ID();
        this.AD_Org_ID = AD_Org_ID;
        this.M_Product_ID = ((product != null) ? product.get_ID() : -10);
        this.M_AttributeSetInstance_ID = M_ASI_ID;
        this.M_CostType_ID = M_CostType_ID;
        this.C_AcctSchema_ID = as.get_ID();
        this.M_CostElement_ID = M_CostElement_ID;
        this.updateForProduct(product, as);
    }
    
    public CostDimension(final int client_ID, final int org_ID, final int product_ID, final int attributeSetInstance_ID, final int costType_ID, final int acctSchema_ID, final int costElement_ID) {
        this.AD_Client_ID = client_ID;
        this.AD_Org_ID = org_ID;
        this.M_Product_ID = product_ID;
        this.M_AttributeSetInstance_ID = attributeSetInstance_ID;
        this.M_CostType_ID = costType_ID;
        this.C_AcctSchema_ID = acctSchema_ID;
        this.M_CostElement_ID = costElement_ID;
        this.updateForProduct(null, null);
    }
    
    public CostDimension(final CostDimension costDimension) {
        this.AD_Client_ID = costDimension.AD_Client_ID;
        this.AD_Org_ID = costDimension.AD_Org_ID;
        this.M_Product_ID = costDimension.M_Product_ID;
        this.M_AttributeSetInstance_ID = costDimension.M_AttributeSetInstance_ID;
        this.M_CostType_ID = costDimension.M_CostType_ID;
        this.C_AcctSchema_ID = costDimension.C_AcctSchema_ID;
        this.M_CostElement_ID = costDimension.M_CostElement_ID;
    }
    
    private Properties getCtx() {
        return Env.getCtx();
    }
    
    private void updateForProduct(MProduct product, MAcctSchema as) {
        if (product == null) {
            product = MProduct.get(this.getCtx(), this.M_Product_ID);
        }
        if (product == null) {
            return;
        }
        if (as == null) {
            as = MAcctSchema.get(this.getCtx(), this.C_AcctSchema_ID);
        }
        final String CostingLevel = product.getCostingLevel(as);
        if ("C".equals(CostingLevel)) {
            this.AD_Org_ID = 0;
            this.M_AttributeSetInstance_ID = 0;
        }
        else if ("O".equals(CostingLevel)) {
            this.M_AttributeSetInstance_ID = 0;
        }
        else if ("B".equals(CostingLevel)) {
            this.AD_Org_ID = 0;
        }
        this.S_Resource_ID = product.getS_Resource_ID();
    }
    
    public int getAD_Client_ID() {
        return this.AD_Client_ID;
    }
    
    public int getAD_Org_ID() {
        return this.AD_Org_ID;
    }
    
    public int getM_Product_ID() {
        return this.M_Product_ID;
    }
    
    public int getS_Resource_ID() {
        return this.S_Resource_ID;
    }
    
    public CostDimension setM_Product_ID(final int M_Product_ID) {
        final CostDimension d = new CostDimension(this);
        d.M_Product_ID = M_Product_ID;
        d.updateForProduct(null, null);
        return d;
    }
    
    public CostDimension setM_Product(final MProduct product) {
        final CostDimension d = new CostDimension(this);
        d.M_Product_ID = product.get_ID();
        d.updateForProduct(product, null);
        return d;
    }
    
    public int getM_AttributeSetInstance_ID() {
        return this.M_AttributeSetInstance_ID;
    }
    
    public int getM_CostType_ID() {
        return this.M_CostType_ID;
    }
    
    public int getC_AcctSchema_ID() {
        return this.C_AcctSchema_ID;
    }
    
    public int getM_CostElement_ID() {
        return this.M_CostElement_ID;
    }
    
    public Query toQuery(final Class<?> clazz, final String trxName) {
        return this.toQuery(clazz, null, null, trxName);
    }
    
    public Query toQuery(final Class<?> clazz, final String whereClause, final Object[] params, final String trxName) {
        String tableName;
        try {
            tableName = (String)clazz.getField("Table_Name").get(null);
        }
        catch (Exception e) {
            throw new AdempiereException((Throwable)e);
        }
        final Properties ctx = Env.getCtx();
        final MTable table = MTable.get(ctx, tableName);
        final ArrayList<Object> finalParams = new ArrayList<Object>();
        final StringBuffer finalWhereClause = new StringBuffer();
        finalWhereClause.append("AD_Client_ID=?");
        finalParams.add(this.AD_Client_ID);
        finalWhereClause.append(" AND AD_Org_ID=?");
        finalParams.add(this.AD_Org_ID);
        finalWhereClause.append(" AND M_Product_ID=?");
        finalParams.add(this.M_Product_ID);
        finalWhereClause.append(" AND M_AttributeSetInstance_ID=?");
        finalParams.add(this.M_AttributeSetInstance_ID);
        finalWhereClause.append(" AND C_AcctSchema_ID=?");
        finalParams.add(this.C_AcctSchema_ID);
        if (this.M_CostElement_ID != -10) {
            finalWhereClause.append(" AND M_CostElement_ID=?");
            finalParams.add(this.M_CostElement_ID);
        }
        if (this.M_CostType_ID != -10 && table.getColumn("M_CostType_ID") != null) {
            finalWhereClause.append(" AND M_CostType_ID=?");
            finalParams.add(this.M_CostType_ID);
        }
        if (!Util.isEmpty(whereClause, true)) {
            finalWhereClause.append(" AND (").append(whereClause).append(")");
            if (params != null && params.length > 0) {
                for (final Object p : params) {
                    finalParams.add(p);
                }
            }
        }
        return new Query(ctx, tableName, finalWhereClause.toString(), trxName).setParameters((List)finalParams);
    }
    
    @Override
    protected Object clone() {
        return new CostDimension(this);
    }
    
    @Override
    public String toString() {
        String retValue = "";
        retValue = "CostDimension{AD_Client_ID = " + this.AD_Client_ID + ";" + "AD_Org_ID = " + this.AD_Org_ID + ";" + "M_Product_ID = " + this.M_Product_ID + ";" + "M_AttributeSetInstance_ID = " + this.M_AttributeSetInstance_ID + ";" + "M_CostType_ID = " + this.M_CostType_ID + ";" + "C_AcctSchema_ID = " + this.C_AcctSchema_ID + ";" + "M_CostElement_ID = " + this.M_CostElement_ID + ";" + "}";
        return retValue;
    }
}
