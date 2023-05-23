package org.astina.mfg.plugin.model;

import java.math.BigDecimal;

public interface I_M_RequisitionLine_Mfg extends org.compiere.model.I_M_RequisitionLine
{	
	/** Column name Qty Remains PO */
    public static final String COLUMNNAME_QtyRemains_PO = "QtyRemains_PO";

	/** Set Quantity Remains PO.
	  * Quantity Remains PO
	  */
	public void setQtyRemains_PO (BigDecimal QtyRemains_PO);

	/** Get Quantity Remains PO.
	  * Quantity Remains PO
	  */
	public BigDecimal getQtyRemains_PO();
	
	/** Column name ProductType */
    public static final String COLUMNNAME_ProductType = "ProductType";

	/** Set Product Type.
	  * Type of product
	  */
	public void setProductType (String ProductType);

	/** Get Product Type.
	  * Type of product
	  */
	public String getProductType();
}
