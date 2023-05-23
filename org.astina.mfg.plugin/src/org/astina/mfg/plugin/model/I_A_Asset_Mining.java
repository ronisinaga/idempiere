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

import java.sql.Timestamp;

import org.compiere.model.I_A_Asset;

public interface I_A_Asset_Mining extends I_A_Asset
{

	/** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;
	
	/** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User Element List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User Element List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException;
	
	public static final String COLUMNNAME_OperationStartDate = "OperationStartDate";

	public void setOperationStartDate (Timestamp OperationStartDate);

	public Timestamp getOperationStartDate();
	
	public static final String COLUMNNAME_MovementDate = "MovementDate";

	public void setMovementDate (Timestamp MovementDate);

	public Timestamp getMovementDate();
	
	/** Column name AssetUnit */
    public static final String COLUMNNAME_AssetUnit = "AssetUnit";

	/** Set Asset Unit	  */
	public void setAssetUnit (String AssetUnit);

	/** Get Asset Unit	  */
	public String getAssetUnit();
	
	/** Column name IsWorkOrder */
    public static final String COLUMNNAME_isWorkOrder = "isWorkOrder";

	/** Set isWorkOrder.
	  * The record is active in the system
	  */
	public void setisWorkOrder (boolean isWorkOrder);

	/** Get isWorkOrder.
	  * The record is active in the system
	  */
	public boolean isWorkOrder();
	
	/** Column name A_Asset_Model_ID */
    public static final String COLUMNNAME_A_Asset_Model_ID = "A_Asset_Model_ID";

	/** Set Asset_Model.
	  * Business Asset_Model
	  */
	public void setA_Asset_Model_ID (int A_Asset_Model_ID);

	/** Get Asset_Model.
	  * Business Asset_Model
	  */
	public int getA_Asset_Model_ID();

	public I_A_Asset_Model getA_Asset_Model() throws RuntimeException;
}
