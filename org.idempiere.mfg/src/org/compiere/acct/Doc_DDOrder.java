// 
// Decompiled by Procyon v0.5.36
// 

package org.compiere.acct;

import java.util.ArrayList;
import org.compiere.util.Env;
import java.math.BigDecimal;
import org.eevolution.model.MDDOrder;
import java.sql.ResultSet;
import org.compiere.model.MAcctSchema;

public class Doc_DDOrder extends Doc
{
    public Doc_DDOrder(final MAcctSchema ass, final ResultSet rs, final String trxName) {
        super(ass, (Class)MDDOrder.class, rs, "DOO", trxName);
    }
    
    protected String loadDocumentDetails() {
        final MDDOrder order = (MDDOrder)this.getPO();
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
