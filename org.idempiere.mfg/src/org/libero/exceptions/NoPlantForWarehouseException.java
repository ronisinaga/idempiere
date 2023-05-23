// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.exceptions;

import org.compiere.model.MWarehouse;
import org.compiere.util.Env;
import org.adempiere.exceptions.AdempiereException;

public class NoPlantForWarehouseException extends AdempiereException
{
    private static final long serialVersionUID = 4986043215550031772L;
    
    public NoPlantForWarehouseException(final int M_Warehouse_ID) {
        super("@NoPlantForWarehouseException@ @M_Warehouse_ID@ : " + MWarehouse.get(Env.getCtx(), M_Warehouse_ID).getName());
    }
}
