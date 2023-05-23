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

/** Generated Interface for C_PaySelection
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */

public interface I_C_PaySelectionLine_Mining extends org.compiere.model.I_C_PaySelectionLine
{
	
	 /** Column name C_InvoicePaySchedule_ID */
    public static final String COLUMNNAME_C_InvoicePaySchedule_ID = "C_InvoicePaySchedule_ID";

	public void setC_InvoicePaySchedule_ID (int C_InvoicePaySchedule_ID);

	public int getC_InvoicePaySchedule_ID();

	public org.compiere.model.I_C_InvoicePaySchedule getC_InvoicePaySchedule() throws RuntimeException;
}
