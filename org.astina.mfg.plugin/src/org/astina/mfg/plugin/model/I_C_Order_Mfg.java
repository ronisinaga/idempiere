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

public interface I_C_Order_Mfg extends org.compiere.model.I_C_Order
{

	/** Column name M_Requisition_ID */
    public static final String COLUMNNAME_M_Requisition_ID = "M_Requisition_ID";

	/** Set Requisition.
	  */
	public void setM_Requisition_ID (int M_Requisition_ID);

	/** Get Requisition.
	  */
	public int getM_Requisition_ID();

	public I_M_Requisition_Mfg getM_Requisition() throws RuntimeException;
	
	/** Column name PO Revision */
    public static final String COLUMNNAME_RevisionNo = "RevisionNo";

	/** Set PO Revision.
	  * Total of all PO Revision
	  */
	public void setRevisionNo (int RevisionNo);

	/** Get PO Revision.
	  * Total of PO Revision
	  */
	public int getRevisionNo();
	
	/** Column name ApprovalNote */
    public static final String COLUMNNAME_ApprovalNote = "ApprovalNote";

	/** Set ApprovalNote.
	  * Optional short ApprovalNote of the record
	  */
	public void setApprovalNote (String ApprovalNote);

	/** Get ApprovalNote.
	  * Optional short ApprovalNote of the record
	  */
	public String getApprovalNote();
	
	/** Column name C_PRRoute_ID */
    public static final String COLUMNNAME_C_PRRoute_ID = "C_PRRoute_ID";

	/** Set C_PRRoute_ID.
	  */
	public void setC_PRRoute_ID (int C_PRRoute_ID);

	/** Get C_PRRoute_ID.
	  */
	public int getC_PRRoute_ID();

	public I_C_PRRoute getC_PRRoute() throws RuntimeException;

}
