// 
// Decompiled by Procyon v0.5.36
// 

package org.compiere.acct;

import java.util.ArrayList;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.libero.model.MPPOrder;
import java.sql.ResultSet;
import org.compiere.model.MAcctSchema;

public class Doc_PPOrder extends Doc
{
    public Doc_PPOrder(final MAcctSchema ass, final ResultSet rs, final String trxName) {
        super(ass, (Class)MPPOrder.class, rs, "MOP", trxName);
    }
    
    protected String loadDocumentDetails() {
        final MPPOrder order = (MPPOrder)this.getPO();
        this.setDateDoc(order.getDateOrdered());
        return "Y";
    }
    
    public BigDecimal getBalance() {
        final BigDecimal retValue = Env.ZERO;
        return retValue;
    }
    
    public ArrayList<Fact> createFacts(final MAcctSchema as) {
        return null;
    }
}
