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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for M_Polybox
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="M_Polybox")
public class X_M_Polybox extends PO implements I_M_Polybox, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20230210L;

    /** Standard Constructor */
    public X_M_Polybox (Properties ctx, int M_Polybox_ID, String trxName)
    {
      super (ctx, M_Polybox_ID, trxName);
      /** if (M_Polybox_ID == 0)
        {
			setcolorrule (null);
			setM_Polybox_ID (0);
			setName (null);
			setpolyboxstatus (null);
// Standby
			setpositionrule (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_M_Polybox (Properties ctx, int M_Polybox_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, M_Polybox_ID, trxName, virtualColumns);
      /** if (M_Polybox_ID == 0)
        {
			setcolorrule (null);
			setM_Polybox_ID (0);
			setName (null);
			setpolyboxstatus (null);
// Standby
			setpositionrule (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_M_Polybox (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_M_Polybox[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Abu-abu = Abu-abu */
	public static final String COLORRULE_Abu_Abu = "Abu-abu";
	/** Biru = Biru */
	public static final String COLORRULE_Biru = "Biru";
	/** Kuning = Kuning */
	public static final String COLORRULE_Kuning = "Kuning";
	/** Set Color.
		@param colorrule Color
	*/
	public void setcolorrule (String colorrule)
	{

		set_Value (COLUMNNAME_colorrule, colorrule);
	}

	/** Get Color.
		@return Color	  */
	public String getcolorrule()
	{
		return (String)get_Value(COLUMNNAME_colorrule);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Discontinued At.
		@param DiscontinuedAt Discontinued At indicates Date when product was discontinued
	*/
	public void setDiscontinuedAt (Timestamp DiscontinuedAt)
	{
		set_Value (COLUMNNAME_DiscontinuedAt, DiscontinuedAt);
	}

	/** Get Discontinued At.
		@return Discontinued At indicates Date when product was discontinued
	  */
	public Timestamp getDiscontinuedAt()
	{
		return (Timestamp)get_Value(COLUMNNAME_DiscontinuedAt);
	}

	/** Set Polybox.
		@param M_Polybox_ID Polybox
	*/
	public void setM_Polybox_ID (int M_Polybox_ID)
	{
		if (M_Polybox_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Polybox_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Polybox_ID, Integer.valueOf(M_Polybox_ID));
	}

	/** Get Polybox.
		@return Polybox	  */
	public int getM_Polybox_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Polybox_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set M_Polybox_UU.
		@param M_Polybox_UU M_Polybox_UU
	*/
	public void setM_Polybox_UU (String M_Polybox_UU)
	{
		set_ValueNoCheck (COLUMNNAME_M_Polybox_UU, M_Polybox_UU);
	}

	/** Get M_Polybox_UU.
		@return M_Polybox_UU	  */
	public String getM_Polybox_UU()
	{
		return (String)get_Value(COLUMNNAME_M_Polybox_UU);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Broken = Broken */
	public static final String POLYBOXSTATUS_Broken = "Broken";
	/** In Use = In Use */
	public static final String POLYBOXSTATUS_InUse = "In Use";
	/** Lost = Lost */
	public static final String POLYBOXSTATUS_Lost = "Lost";
	/** Scrap = Scrap */
	public static final String POLYBOXSTATUS_Scrap = "Scrap";
	/** Sold = Sold */
	public static final String POLYBOXSTATUS_Sold = "Sold";
	/** Standby = Standby */
	public static final String POLYBOXSTATUS_Standby = "Standby";
	/** Set Polybox Status.
		@param polyboxstatus Polybox Status
	*/
	public void setpolyboxstatus (String polyboxstatus)
	{

		set_Value (COLUMNNAME_polyboxstatus, polyboxstatus);
	}

	/** Get Polybox Status.
		@return Polybox Status	  */
	public String getpolyboxstatus()
	{
		return (String)get_Value(COLUMNNAME_polyboxstatus);
	}

	/** Left = Left */
	public static final String POSITIONRULE_Left = "Left";
	/** Netral = Netral */
	public static final String POSITIONRULE_Netral = "Netral";
	/** Right = Right */
	public static final String POSITIONRULE_Right = "Right";
	/** Set Position.
		@param positionrule Position
	*/
	public void setpositionrule (String positionrule)
	{

		set_Value (COLUMNNAME_positionrule, positionrule);
	}

	/** Get Position.
		@return Position	  */
	public String getpositionrule()
	{
		return (String)get_Value(COLUMNNAME_positionrule);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}