package org.astina.mfg.plugin.model;

import java.math.BigDecimal;

public interface I_C_OrderLine_Mfg extends org.compiere.model.I_C_OrderLine
{
	/** Column name M_Requisitionline_ID */
    public static final String COLUMNNAME_M_Requisitionline_ID = "M_Requisitionline_ID";

	/** Set M_Requisitionline.
	  * Requisition Line
	  */
	public void setM_Requisitionline_ID (Object a);

	/** Get M_Requisitionline.
	  * Requisition Line
	  */
	public int getM_Requisitionline_ID ();

	public I_M_RequisitionLine_Mfg getM_RequisitionLine() throws RuntimeException;
		
	public static final String COLUMNNAME_QtyRequisition = "QtyRequisition";

	/** Set Requisition Quantity.
	  * Requisition Quantity
	  */
	public void setQtyRequisition (BigDecimal QtyRequisition);

	/** Get Requisition Quantity.
	  * Requisition Quantity
	  */
	public BigDecimal getQtyRequisition();
	
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
	
	/** Column name PriceLastPO */
    public static final String COLUMNNAME_PriceLastPO = "PriceLastPO";

	/** Set Last PO Price.
	  * Last PO Price 
	  */
	public void setPriceLastPO (BigDecimal PriceLastPO);

	/** Get Last PO Price.
	  * Last PO Price 
	  */
	public BigDecimal getPriceLastPO();
	
	/** Column name LCO_WithholdingCategory_ID */
    public static final String COLUMNNAME_LCO_WithholdingCategory_ID = "LCO_WithholdingCategory_ID";

	/** Set Withholding Category	  */
	public void setLCO_WithholdingCategory_ID (int LCO_WithholdingCategory_ID);

	/** Get Withholding Category	  */
	public int getLCO_WithholdingCategory_ID();
	
	public static final String COLUMNNAME_QtyRemains_SO = "QtyRemains_SO";

	/** Set SO Quantity.
	  * SO Quantity
	  */
	public void setQtyRemains_SO (BigDecimal QtyRemains_SO);

	/** Get SO Quantity.
	  * SO Quantity
	  */
	public BigDecimal getQtyRemains_SO();
	
	/** Column name Quote_OrderLine_ID */
    public static final String COLUMNNAME_Quote_OrderLine_ID = "Quote_OrderLine_ID";

	/** Set Quote Order Line.
	  * Quote to corresponding Sales
	  */
	public void setQuote_OrderLine_ID (int Quote_OrderLine_ID);

	/** Get Quote Order Line.
	  * Quote to corresponding Sales
	  */
	public int getQuote_OrderLine_ID();

	public org.compiere.model.I_C_OrderLine Quote_OrderLine() throws RuntimeException;

}
