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
/** Generated Model - DO NOT CHANGE */
package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for A_Asset_Model
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_A_Asset_Model extends PO implements I_A_Asset_Model, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200925L;

    /** Standard Constructor */
    public X_A_Asset_Model (Properties ctx, int A_Asset_Model_ID, String trxName)
    {
      super (ctx, A_Asset_Model_ID, trxName);
      /** if (A_Asset_Model_ID == 0)
        {
			setA_Asset_Model_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_A_Asset_Model (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_A_Asset_Model[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Asset Model.
		@param A_Asset_Model_ID Asset Model	  */
	public void setA_Asset_Model_ID (int A_Asset_Model_ID)
	{
		if (A_Asset_Model_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Model_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Model_ID, Integer.valueOf(A_Asset_Model_ID));
	}

	/** Get Asset Model.
		@return Asset Model	  */
	public int getA_Asset_Model_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_Model_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set A_Asset_Model_UU.
		@param A_Asset_Model_UU A_Asset_Model_UU	  */
	public void setA_Asset_Model_UU (String A_Asset_Model_UU)
	{
		set_ValueNoCheck (COLUMNNAME_A_Asset_Model_UU, A_Asset_Model_UU);
	}

	/** Get A_Asset_Model_UU.
		@return A_Asset_Model_UU	  */
	public String getA_Asset_Model_UU () 
	{
		return (String)get_Value(COLUMNNAME_A_Asset_Model_UU);
	}

	public org.compiere.model.I_A_Asset_Type getA_Asset_Type() throws RuntimeException
    {
		return (org.compiere.model.I_A_Asset_Type)MTable.get(getCtx(), org.compiere.model.I_A_Asset_Type.Table_Name)
			.getPO(getA_Asset_Type_ID(), get_TrxName());	}

	/** Set Asset Type.
		@param A_Asset_Type_ID Asset Type	  */
	public void setA_Asset_Type_ID (int A_Asset_Type_ID)
	{
		if (A_Asset_Type_ID < 1) 
			set_Value (COLUMNNAME_A_Asset_Type_ID, null);
		else 
			set_Value (COLUMNNAME_A_Asset_Type_ID, Integer.valueOf(A_Asset_Type_ID));
	}

	/** Get Asset Type.
		@return Asset Type	  */
	public int getA_Asset_Type_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_Type_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}
}