// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import java.sql.Timestamp;
import org.eevolution.model.I_PP_Product_BOM;
import org.adempiere.exceptions.AdempiereException;

public class BOMExpiredException extends AdempiereException
{
    private static final long serialVersionUID = -3084324343550833077L;
    
    public BOMExpiredException(final I_PP_Product_BOM bom, final Timestamp date) {
        super(buildMessage(bom, date));
    }
    
    private static final String buildMessage(final I_PP_Product_BOM bom, final Timestamp date) {
        return "@NotValid@ @PP_Product_BOM_ID@:" + bom.getValue() + " - @Date@:" + date;
    }
}
