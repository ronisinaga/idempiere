package org.astina.mfg.plugin.factory;

import org.astina.mfg.plugin.form.WCreateFromRequisitionUI;
import org.astina.mfg.plugin.form.WCreateFromShipmentUI;
import org.astina.mfg.plugin.form.WCreateFromStatementUI;
import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_M_InOut;

/**
 * @author Alex
 *
 */
public class AstinaMfgCreateFromFactory implements ICreateFromFactory {
		
	@Override
	public ICreateFrom create(GridTab mTab) 
	{
		String tableName = mTab.getTableName();
		
		if (tableName.equals(I_C_Order.Table_Name))
			return new WCreateFromRequisitionUI(mTab);
		
		if (tableName.equals(I_M_InOut.Table_Name))
			return new WCreateFromShipmentUI(mTab);
		
		if (tableName.equals(I_C_BankStatement.Table_Name))
			return new WCreateFromStatementUI(mTab);
		
		return null;
	}
}