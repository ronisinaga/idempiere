// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import org.compiere.model.MOrg;
import org.adempiere.exceptions.AdempiereException;

public class NoBPartnerLinkedforOrgException extends AdempiereException
{
    private static final long serialVersionUID = -8354155558569979580L;
    
    public NoBPartnerLinkedforOrgException(final MOrg org) {
        super("@NotExistsBPLinkedforOrgError@ " + org.getName());
    }
}
