package org.astina.mfg.plugin.model;

import java.sql.Timestamp;

import org.compiere.model.I_M_PackageMPS;

public interface I_M_PackageMPS_Mfg extends I_M_PackageMPS
{
	
	/** Column name M_Polybox_ID */
    public static final String COLUMNNAME_M_Polybox_ID = "M_Polybox_ID";

	/** Set Polybox
	  */
	public void setM_Polybox_ID (int M_Polybox_ID);

	/** Get Polybox
	  */
	public int getM_Polybox_ID();

	public I_M_Polybox getM_Polybox() throws RuntimeException;
	
	
	/** Column name DateReturned */
    public static final String COLUMNNAME_DateReturned = "DateReturned";

	/** Set DateReturned.
	  */
	public void setDateReturned (Timestamp DateReturned);

	/** Get DateReturned.
	  */
	public Timestamp getDateReturned();

    /** Column name IsReturn */
    public static final String COLUMNNAME_IsReturn = "IsReturn";

	/** Set Return.
	  * The record is Return in the system
	  */
	public void setIsReturn (boolean IsReturn);

	/** Get Return.
	  * The record is Return in the system
	  */
	public boolean IsReturn();
}
