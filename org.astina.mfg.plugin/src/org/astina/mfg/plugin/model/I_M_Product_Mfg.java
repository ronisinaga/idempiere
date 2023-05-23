/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.astina.mfg.plugin.model;

/** Generated Interface for M_Product
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
public interface I_M_Product_Mfg extends org.compiere.model.I_M_Product
{

	/** Column name LCO_WithholdingCategory_ID */
    public static final String COLUMNNAME_LCO_WithholdingCategory_ID = "LCO_WithholdingCategory_ID";

	/** Set Withholding Category	  */
	public void setLCO_WithholdingCategory_ID (int LCO_WithholdingCategory_ID);

	/** Get Withholding Category	  */
	public int getLCO_WithholdingCategory_ID();
	
	/** Column name C_UOM_To_ID */
    public static final String COLUMNNAME_C_UOM_To_ID = "C_UOM_To_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_To_ID (int C_UOM_To_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_To_ID();

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException;
	
}
