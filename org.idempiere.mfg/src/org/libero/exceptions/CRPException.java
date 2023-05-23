// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import org.compiere.process.DocAction;
import org.compiere.model.I_S_Resource;
import org.libero.tables.I_PP_Order_Node;
import org.eevolution.model.I_PP_Order;
import org.adempiere.exceptions.AdempiereException;

public class CRPException extends AdempiereException
{
    private I_PP_Order order;
    private I_PP_Order_Node node;
    private I_S_Resource resource;
    
    public CRPException(final String message) {
        super(message);
        this.order = null;
        this.node = null;
        this.resource = null;
    }
    
    public CRPException(final Exception e) {
        super((Throwable)e);
        this.order = null;
        this.node = null;
        this.resource = null;
    }
    
    public CRPException setPP_Order(final I_PP_Order order) {
        this.order = order;
        return this;
    }
    
    public CRPException setPP_Order_Node(final I_PP_Order_Node node) {
        this.node = node;
        return this;
    }
    
    public CRPException setS_Resource(final I_S_Resource resource) {
        this.resource = resource;
        return this;
    }
    
    public String getMessage() {
        final String msg = super.getMessage();
        final StringBuffer sb = new StringBuffer(msg);
        if (this.order != null) {
            String info;
            if (this.order instanceof DocAction) {
                info = ((DocAction)this.order).getSummary();
            }
            else {
                info = this.order.getDocumentNo() + "/" + this.order.getDatePromised();
            }
            sb.append(" @PP_Order_ID@:").append(info);
        }
        if (this.node != null) {
            sb.append(" @PP_Order_Node_ID@:").append(this.node.getValue()).append("_").append(this.node.getName());
        }
        if (this.resource != null) {
            sb.append(" @S_Resource_ID@:").append(this.resource.getValue()).append("_").append(this.resource.getName());
        }
        return sb.toString();
    }
}
