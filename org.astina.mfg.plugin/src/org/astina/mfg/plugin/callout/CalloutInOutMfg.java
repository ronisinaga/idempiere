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
package org.astina.mfg.plugin.callout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;


/**
 *	Shipment/Receipt Callouts
 *
 *  @author Jorg Janke
 *  @version $Id: CalloutInOut.java,v 1.7 2006/07/30 00:51:05 jjanke Exp $
 *  @author victor.perez@e-evolution.com www.e-evolution.com [ 1867464 ] https://sourceforge.net/p/adempiere/bugs/923/
 */
public class CalloutInOutMfg extends CalloutEngine
{
	/**
	 *	M_InOut - Defaults for BPartner.
	 *			- Location
	 *			- Contact
	 *	@param ctx
	 *	@param WindowNo
	 *	@param mTab
	 *	@param mField
	 *	@param value
	 *	@return error message or ""
	 */
	public String bpartner (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer C_BPartner_ID = (Integer)value;
		if (C_BPartner_ID == null || C_BPartner_ID.intValue() == 0)
			return "";

		String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
			+ "p.M_PriceList_ID,p.PaymentRule,p.POReference,"
			+ "p.SO_Description,p.IsDiscountPrinted,"
			+ "p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
			+ "(select max(l.C_BPartner_Location_ID) from C_BPartner_Location l where p.C_BPartner_ID=l.C_BPartner_ID AND l.IsActive='Y') as C_BPartner_Location_ID,"
			+ "(select max(c.AD_User_ID) from AD_User c where p.C_BPartner_ID=c.C_BPartner_ID AND c.IsActive='Y' AND IsShipTo='Y') as ShipTo_User_ID,"
			+ "(select max(c.AD_User_ID) from AD_User c where p.C_BPartner_ID=c.C_BPartner_ID AND c.IsActive='Y') as AD_User_ID "
			+ "FROM C_BPartner p "
			+ "WHERE p.C_BPartner_ID=?";		//	1

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_BPartner_ID.intValue());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				//[ 1867464 ]
				boolean IsSOTrx = "Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx"));
				if (!IsSOTrx)
				{
					//	Location
					Integer ii = Integer.valueOf(rs.getInt("C_BPartner_Location_ID"));
					if (rs.wasNull())
						mTab.setValue("C_BPartner_Location_ID", null);
					else
						mTab.setValue("C_BPartner_Location_ID", ii);
					//	Contact
					ii = Integer.valueOf(rs.getInt("AD_User_ID"));
					if (rs.wasNull())
						mTab.setValue("AD_User_ID", null);
					else {
						int ShipTo_User_ID = rs.getInt("ShipTo_User_ID");
						Integer userID = ShipTo_User_ID > 0 ? Integer.valueOf(ShipTo_User_ID) : ii;
						mTab.setValue("AD_User_ID", userID);
					}
				}

				//Bugs item #1679818: checking for SOTrx only
				if (IsSOTrx)
				{
					
					//Astina 060123
//					Location
					Integer ii = Integer.valueOf(rs.getInt("C_BPartner_Location_ID"));
					if (rs.wasNull())
						mTab.setValue("C_BPartner_Location_ID", null);
					else
						mTab.setValue("C_BPartner_Location_ID", ii);
					//	Contact
					ii = Integer.valueOf(rs.getInt("AD_User_ID"));
					if (rs.wasNull())
						mTab.setValue("AD_User_ID", null);
					else {
						int ShipTo_User_ID = rs.getInt("ShipTo_User_ID");
						Integer userID = ShipTo_User_ID > 0 ? Integer.valueOf(ShipTo_User_ID) : ii;
						mTab.setValue("AD_User_ID", userID);
					}
					
					//	CreditAvailable
					double CreditAvailable = rs.getDouble("CreditAvailable");
					if (!rs.wasNull() && CreditAvailable < 0)
						mTab.fireDataStatusEEvent("CreditLimitOver",
								DisplayType.getNumberFormat(DisplayType.Amount).format(CreditAvailable),
								false);
				}//
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return e.getLocalizedMessage();
		}
		finally
		{
			DB.close(rs, pstmt);
		}

		return "";
	}	//	bpartner
	
}	//	CalloutInOut