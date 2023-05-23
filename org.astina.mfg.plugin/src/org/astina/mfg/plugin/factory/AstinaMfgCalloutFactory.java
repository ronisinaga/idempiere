package org.astina.mfg.plugin.factory;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;


public class AstinaMfgCalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {
		// TODO Auto-generated method stub
		
		List<IColumnCallout> list = new ArrayList<IColumnCallout>();
		
		return list != null ? list.toArray(new IColumnCallout[0]) : new IColumnCallout[0];
	}
}
