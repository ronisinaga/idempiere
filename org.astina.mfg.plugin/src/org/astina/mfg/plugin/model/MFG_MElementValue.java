/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.astina.mfg.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_Fact_Acct;
import org.compiere.model.MAccount;
import org.compiere.model.MElementValue;
import org.compiere.model.POResultSet;
import org.compiere.model.Query;
import org.compiere.util.DB;

/**
 * 	Natural Account
 *
 *  @author Jorg Janke
 *  @version $Id: MElementValue.java,v 1.3 2006/07/30 00:58:37 jjanke Exp $
 *  
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 			BF [ 1883533 ] Change to summary - valid combination issue
 * 			BF [ 2320411 ] Translate "Already posted to" message
 */
public class MFG_MElementValue extends MElementValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6352667759697380460L;
	
	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param C_ElementValue_ID ID or 0 for new
	 *	@param trxName transaction
	 */
	public MFG_MElementValue(Properties ctx, int C_ElementValue_ID, String trxName)
	{
		super(ctx, C_ElementValue_ID, trxName);
		if (C_ElementValue_ID == 0)
		{
			setIsSummary (false);
			setAccountSign (ACCOUNTSIGN_Natural);
			setAccountType (ACCOUNTTYPE_Expense);
			setIsDocControlled(false);
			setIsForeignCurrency(false);
			setIsBankAccount(false);
			//
			setPostActual (true);
			setPostBudget (true);
			setPostEncumbrance (true);
			setPostStatistical (true);
		}
	}	//	MElementValue

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MFG_MElementValue(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MElementValue
	
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		//Comment by Astina 08122022
		//if (getAD_Org_ID() != 0)
		//	setAD_Org_ID(0);
		//
		
		// Transform to summary level account
		if (!newRecord && isSummary() && is_ValueChanged(COLUMNNAME_IsSummary))
		{
			//
			// Check if we have accounting facts
			boolean match = new Query(getCtx(), I_Fact_Acct.Table_Name, I_Fact_Acct.COLUMNNAME_Account_ID+"=?", get_TrxName())
								.setParameters(getC_ElementValue_ID())
								.match();
			if (match)
			{
				throw new AdempiereException("@AlreadyPostedTo@");
			}
			//
			// Check Valid Combinations - teo_sarca FR [ 1883533 ]
			String whereClause = MAccount.COLUMNNAME_Account_ID+"=?";
			POResultSet<MAccount> rs = null;
			try {
				rs = new Query(getCtx(), I_C_ValidCombination.Table_Name, whereClause, get_TrxName())
				.setParameters(get_ID())
				.scroll();
				while(rs.hasNext()) {
					rs.next().deleteEx(true);
				}
			}
			finally {
				DB.close(rs);
				rs = null;
			}
		}
		return true;
	}	//	beforeSave

}	//	MElementValue
