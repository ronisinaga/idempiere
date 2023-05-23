// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import java.sql.Timestamp;
import org.compiere.model.I_AD_Workflow;
import org.adempiere.exceptions.AdempiereException;

public class RoutingExpiredException extends AdempiereException
{
    private static final long serialVersionUID = -7522979292063177848L;
    
    public RoutingExpiredException(final I_AD_Workflow wf, final Timestamp date) {
        super(buildMessage(wf, date));
    }
    
    private static final String buildMessage(final I_AD_Workflow wf, final Timestamp date) {
        return "@NotValid@ @AD_Workflow_ID@:" + wf.getValue() + " - @Date@:" + date;
    }
}
