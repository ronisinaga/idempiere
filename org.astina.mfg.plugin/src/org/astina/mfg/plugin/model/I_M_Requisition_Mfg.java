package org.astina.mfg.plugin.model;

public interface I_M_Requisition_Mfg extends org.compiere.model.I_M_Requisition
{

	/** Alex Sembiring 
	 /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;
	
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
	
	/** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException;
	
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
