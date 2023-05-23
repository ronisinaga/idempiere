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

import java.math.BigDecimal;

import org.compiere.model.I_A_Asset_Disposed;

public interface I_A_Asset_Change_Mining extends I_A_Asset_Disposed
{
	/** Alex Sembiring 
		
	 /** Column name A_Sold_Amt */
    public static final String COLUMNNAME_A_Sold_Amt = "A_Sold_Amt";

	/** Set Sold Amount	  */
	public void setA_Sold_Amt (BigDecimal A_Sold_Amt);

	/** Get Sold Amount	  */
	public BigDecimal getA_Sold_Amt();
	
	 /** Column name A_Sold_Profit_Amt */
    public static final String COLUMNNAME_A_Sold_Profit_Amt = "A_Sold_Profit_Amt";

	/** Set Sold Profit Amount	  */
	public void setA_Sold_Profit_Amt (BigDecimal A_Sold_Profit_Amt);

	/** Get Sold Profit Amount	  */
	public BigDecimal getA_Sold_Profit_Amt();
	
	/** Column name C_Charge_ID */
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";

	/** Set Business Charge .
	  * Identifies a Charge
	  */
	public void setC_Charge_ID (int C_Charge_ID);

	/** Get Business Charge .
	  * Identifies a Charge
	  */
	public int getC_Charge_ID();

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException;
	
}