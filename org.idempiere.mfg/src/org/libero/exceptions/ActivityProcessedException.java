// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import org.libero.model.MPPOrderNode;
import org.adempiere.exceptions.AdempiereException;

public class ActivityProcessedException extends AdempiereException
{
    private static final long serialVersionUID = 1L;
    
    public ActivityProcessedException(final MPPOrderNode activity) {
        super("Order Activity Already Processed - " + activity);
    }
}
