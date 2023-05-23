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

/** Generated Interface for M_Product
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
public interface I_M_MovementLineConfirm_Mfg extends org.compiere.model.I_M_MovementLineConfirm
{

	/** Column name M_LocatorTo_ID */
    public static final String COLUMNNAME_M_LocatorTo_ID = "M_LocatorTo_ID";

	/** Set Locator.
	  * Locator
	  */
	public void setM_LocatorTo_ID (int M_LocatorTo_ID);

	/** Get Locator.
	  * Locator
	  */
	public int getM_LocatorTo_ID();

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException;
	
	/** Column name Qty Reject */
    public static final String COLUMNNAME_QtyReject = "QtyReject";

	/** Set Quantity Reject.
	  * Quantity reject
	  */
	public void setQtyQtyReject (BigDecimal QtyReject);

	/** Get Quantity Reject.
	  * Quantity Reject
	  */
	public BigDecimal getQtyReject();
}
