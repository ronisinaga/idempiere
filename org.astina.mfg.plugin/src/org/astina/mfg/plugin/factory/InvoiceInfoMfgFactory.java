package org.astina.mfg.plugin.factory;

import org.adempiere.webui.factory.IInfoFactory;
import org.adempiere.webui.info.InfoInvoiceWindow;
import org.adempiere.webui.info.InfoWindow;
import org.adempiere.webui.panel.InfoGeneralPanel;
import org.adempiere.webui.panel.InfoPanel;
import org.compiere.model.GridField;
import org.compiere.model.Lookup;
import org.compiere.model.MEntityType;
import org.compiere.model.MInfoWindow;
import org.compiere.model.Query;
import org.compiere.util.Env;

public class InvoiceInfoMfgFactory implements IInfoFactory {

	@Override
	public InfoPanel create(int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection,
			String whereClause, int AD_InfoWindow_ID, boolean lookup) {
		
		InfoPanel info = null;
		info = new InfoInvoiceWindow(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, AD_InfoWindow_ID, lookup);
    	if (!info.loadedOK()) {
            /*info = new InfoGeneralPanel (value, WindowNo,
                tableName, keyColumn,
                Boolean.TRUE, whereClause, lookup);
        	if (!info.loadedOK()) {
        		info.dispose(false);
        		info = null;
        	}*/
    	}
    	MInfoWindow checkEntity = new Query(Env.getCtx(),MInfoWindow.Table_Name,MInfoWindow.COLUMNNAME_AD_InfoWindow_ID+"=?",null)
    		.setParameters(AD_InfoWindow_ID)
    		.first();
    	if (checkEntity!=null && !checkEntity.getEntityType().equals(MEntityType.ENTITYTYPE_Dictionary))
    		return info;

		return null;
	}

	@Override
	public InfoPanel create(Lookup lookup, GridField field, String tableName, String keyColumn, String value,
			boolean multiSelection, String whereClause, int AD_InfoWindow_ID) {
		
		if(tableName.equalsIgnoreCase("C_Invoice"))
		{
			InfoPanel info = null;
			info = new InfoInvoiceWindow(lookup.getWindowNo(), tableName, keyColumn, value, Boolean.TRUE, whereClause, AD_InfoWindow_ID, Boolean.TRUE);
	    	if (!info.loadedOK()) {
	            /*info = new InfoGeneralPanel (value, -1,
	                tableName, keyColumn,
	                Boolean.TRUE, whereClause, Boolean.TRUE);*/
	        	if (!info.loadedOK()) {
	        		info.dispose(false);
	        		info = null;
	        	}
	    	}
	    	MInfoWindow checkEntity = new Query(Env.getCtx(),MInfoWindow.Table_Name,MInfoWindow.COLUMNNAME_AD_InfoWindow_ID+"=?",null)
	    		.setParameters(AD_InfoWindow_ID)
	    		.first();
	    	if (checkEntity!=null)
	    		return info;
		}
		return null;
	}

	@Override
	public InfoWindow create(int AD_InfoWindow_ID) {
		MInfoWindow infoWindow = new MInfoWindow(Env.getCtx(), AD_InfoWindow_ID, (String)null);
		String tableName = infoWindow.getAD_Table().getTableName();
		String keyColumn = tableName + "_ID";
		InfoPanel info = create(-1, tableName, keyColumn, null, false, null, AD_InfoWindow_ID, false);
		if (info instanceof InfoWindow)
			return (InfoWindow) info;
		else
			return null;
	}

	@Override
	public InfoPanel create(int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection,
			String whereClause, int AD_InfoWindow_ID, boolean lookup, GridField field) {
		// TODO Auto-generated method stub
		return null;
	}
 
}