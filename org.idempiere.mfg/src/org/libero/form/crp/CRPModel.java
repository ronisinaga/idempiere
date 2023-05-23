// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.form.crp;

import java.math.BigDecimal;
import org.compiere.model.MResource;
import java.sql.Timestamp;
import org.jfree.data.category.CategoryDataset;
import javax.swing.JTree;

public interface CRPModel
{
    JTree getTree();
    
    CategoryDataset getDataset();
    
    BigDecimal calculateLoad(final Timestamp p0, final MResource p1, final String p2);
}
