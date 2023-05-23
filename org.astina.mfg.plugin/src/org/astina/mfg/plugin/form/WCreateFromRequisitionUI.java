package org.astina.mfg.plugin.form;

import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.form.WCreateFromWindow;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zul.Space;
import org.zkoss.zul.Vlayout;

public class WCreateFromRequisitionUI extends CreateFromRequisition implements EventListener<Event>, ValueChangeListener
{
	private WCreateFromWindow window;
	
	public WCreateFromRequisitionUI(GridTab tab) 
	{
		super(tab);
		log.info(getGridTab().toString());
		
		window = new WCreateFromWindow(this, getGridTab().getWindowNo());
		
		p_WindowNo = getGridTab().getWindowNo();

		try
		{
			if (!dynInit())
				return;
			zkInit();
			setInitOK(true);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
			setInitOK(false);
			throw new AdempiereException(e.getMessage());
		}
		AEnv.showWindow(window);
	}
	
	/** Window No               */
	private int p_WindowNo;

	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(getClass());
		
	protected Label organizationLabel = new Label();
	protected WTableDirEditor organizationField;
	
	protected Label orderLabel = new Label();
	protected WTableDirEditor orderField;
	
	protected Label productLabel = new Label();
	protected WEditor productField;
	
	protected Label partnerLabel = new Label();
	protected WEditor partnerField;
	
	protected Label requisitionLabel = new Label();
	//protected Listbox requisitionField = ListboxFactory.newDropdownListbox();
	protected WSearchEditor requisitionField;
	
	protected Checkbox supplierPO = new Checkbox();
	
	private Grid parameterStdLayout;

	private int noOfParameterColumn;
    
	/**
	 *  Dynamic Init
	 *  @throws Exception if Lookups cannot be initialized
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		
		super.dynInit();
		
		window.setTitle(getTitle());
		
		//supplierPO.setSelected(true);
		//supplierPO.addActionListener(this);

		//  load Product
		int AD_Column_ID = 11501;        //  M_RequisitionLine
		MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		productField = new WSearchEditor ("M_Product_ID", false, false, true, lookup);
		productField.addValueChangeListener(this);
		
		lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 2163, DisplayType.TableDir); //AD_Org_ID
		organizationField = new WTableDirEditor ("AD_Org_ID",false,false,false,lookup);
		organizationField.addValueChangeListener(this);
		
		int AD_Org_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "AD_Org_ID");
		organizationField.setValue(Integer.valueOf(AD_Org_ID));
		organizationField.addValueChangeListener(this);
		organizationField.setReadWrite(false);
		
		lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 2161, DisplayType.TableDir); //C_Order_ID
		orderField = new WTableDirEditor ("C_Order_ID",false,false,false,lookup);
		
		int C_Order_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_Order_ID");
		orderField.setValue(Integer.valueOf(C_Order_ID));
		orderField.addValueChangeListener(this);
		orderField.setReadWrite(false);
		
		MLookup lookup1 = MLookupFactory.get(Env.getCtx(), p_WindowNo, 11499, DisplayType.Search, Env.getLanguage(Env.getCtx()), 
				null, 1000005, true, "M_Requisition.AD_Org_ID=@AD_Org_ID@"); //M_Requisition_ID /1000195
		requisitionField = new WSearchEditor ("M_Requisition_ID",false,false,false,lookup1);
			
		requisitionField.addValueChangeListener(this);
				
		//initRequisition(AD_Org_ID,C_Order_ID);
		
		return true;
	}   //  dynInit
	
	protected void zkInit() throws Exception
	{
    	productLabel.setText(Msg.getElement(Env.getCtx(), "M_Product_ID"));
		organizationLabel.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		orderLabel.setText(Msg.translate(Env.getCtx(), "C_Order_ID"));
    	requisitionLabel.setText(Msg.getElement(Env.getCtx(), "M_Requisition_ID"));
    	partnerLabel.setText(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
    	
    	//supplierPO.setText(Msg.getMsg(Env.getCtx(), "Using PO's Supplier", true));
    	//supplierPO.setTooltiptext(Msg.getMsg(Env.getCtx(), "Using PO's Supplier", false));

		Vlayout vlayout = new Vlayout();
		ZKUpdateUtil.setVflex(vlayout, "min");
		ZKUpdateUtil.setWidth(vlayout, "100%");
    	Panel parameterPanel = window.getParameterPanel();
		parameterPanel.appendChild(vlayout);
		
		parameterStdLayout = GridFactory.newGridLayout();
    	vlayout.appendChild(parameterStdLayout);
    	ZKUpdateUtil.setVflex(vlayout, "parameterStdLayout");
    	
    	setupColumns(parameterStdLayout);
		
		Rows rows = (Rows) parameterStdLayout.newRows();
		Row row = rows.newRow();
		
		row.appendChild(organizationLabel.rightAlign());
		row.appendChild(organizationField.getComponent());
		row.appendChild(orderLabel.rightAlign());
		row.appendChild(orderField.getComponent());
		orderField.fillHorizontal();
		
		//row.appendChild(new Space());
		//row.appendChild(supplierPO);
		//ZKUpdateUtil.setHflex(supplierPO, "1");
		int C_Order_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_Order_ID");
		MOrder corder = new MOrder (Env.getCtx(), C_Order_ID, null);
		MDocType docType = MDocType.get(Env.getCtx(), corder.getC_DocTypeTarget_ID());
		
		if(docType.getName().equalsIgnoreCase("Purchase Order Shipment"))
		{
			row = rows.newRow();
			row.appendChild(partnerLabel.rightAlign());
			row.appendChild(partnerField.getComponent());
			partnerField.fillHorizontal();
		}else
		{
			row = rows.newRow();
			row.appendChild(requisitionLabel.rightAlign());
			row.appendChild(requisitionField.getComponent());
			requisitionField.fillHorizontal();
			//row.appendChild(requisitionField);
			//ZKUpdateUtil.setHflex(requisitionField, "1");
			row.appendChild(productLabel.rightAlign());
			row.appendChild(productField.getComponent());
			productField.fillHorizontal();
		}
			
    	if (ClientInfo.isMobile()) {    		
    		if (noOfParameterColumn == 2)
				LayoutUtils.compactTo(parameterStdLayout, 2);		
			ClientInfo.onClientInfo(window, this::onClientInfo);
		}
	}

	private boolean m_actionActive = false;
	
	/**
	 *  Action Listener
	 *  @param e event
	 * @throws Exception 
	 */
	public void onEvent(Event e) throws Exception
	{
		if (m_actionActive)
			return;
		m_actionActive = true;
		
		if (e.getTarget().equals(supplierPO))
		{
			window.getWListbox().clear();
			//requisitionField.setSelectedIndex(-1);
			//productField.setValue(null);

		}else
		{
			//  Requisition
			if (requisitionField.getValue() == null)
			{
				window.getWListbox().clear();
				//KeyNamePair pp = requisitionField.getSelectedItem().toKeyNamePair();
				//if (pp == null || pp.getKey() == 0)
				//	window.getWListbox().clear();
			}else {
				//{
					//int M_Requisition_ID = pp.getKey();
					//  set Product to null
					productField.setValue(null);
					//loadRequisition(M_Requisition_ID, (Integer)organizationField.getValue(), supplierPO.isSelected());
					loadRequisition((Integer)requisitionField.getValue(), (Integer)organizationField.getValue(), supplierPO.isSelected());
				}
		
				if (productField.getValue() == null)
					window.getWListbox().clear();
				else
				{
					//  set Requisition to null
					//requisitionField.setSelectedIndex(-1);
					requisitionField.setValue(null);
					loadRequisitionbyProduct((Integer)productField.getValue(), (Integer)organizationField.getValue(), supplierPO.isSelected());
				}		
		}
		
		m_actionActive = false;
	}
	
		
	/**
	 *  Change Listener
	 *  @param e event
	 */
	public void valueChange (ValueChangeEvent e)
	{
		if (log.isLoggable(Level.CONFIG)) log.config(e.getPropertyName() + "=" + e.getNewValue());

		if (e.getPropertyName().equals("M_Product_ID"))
		{
			int M_Product_ID = 0; 
			if (e.getNewValue() != null){
				M_Product_ID = ((Integer)e.getNewValue()).intValue();
			}
			requisitionField.setValue(null);
			loadRequisitionbyProduct(M_Product_ID, (Integer)organizationField.getValue(), supplierPO.isSelected());
		}
		
		if (e.getPropertyName().equals("M_Requisition_ID"))
		{
			int M_Requisition_ID = 0; 
			if (e.getNewValue() != null){
				M_Requisition_ID = ((Integer)e.getNewValue()).intValue();
			}
			productField.setValue(null);
			loadRequisition(M_Requisition_ID, (Integer)organizationField.getValue(), supplierPO.isSelected());
		}else
		{
			productField.removeValuechangeListener(this);
			requisitionField.removeValuechangeListener(this);
			window.getWListbox().clear();
		}
		//window.tableChanged(null);
	}   //  vetoableChange
	
	/**
	 *  Load Data - Requisition
	 *  @param M_Requisition_ID Order
	 */
	protected void loadRequisition (int M_Requisition_ID, int AD_Org_ID, boolean supplierPO)
	{
		//Astina
		loadTableOIS(getRequisitionData(M_Requisition_ID, AD_Org_ID, supplierPO));
	}   //  LoadOrder
	
	protected void loadRequisitionbyProduct (int M_Product_ID, int AD_Org_ID, boolean supplierPO)
	{
		loadTableOIS(getRequisitionDatabyProduct(M_Product_ID, AD_Org_ID, supplierPO));
	}   //  LoadOrder
		
	/**
	 *  Load Order/Invoice/Shipment data into Table
	 *  @param data data
	 */
	protected void loadTableOIS (Vector<?> data)
	{
		window.getWListbox().clear();
		
		//  Remove previous listeners
		window.getWListbox().getModel().removeTableModelListener(window);
		//  Set Model
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(window);
		
		window.getWListbox().setData(model, getOISColumnNames());
		configureMiniTable(window.getWListbox());
		
	}   //  loadOrder
	
	public void showWindow()
	{
		window.setVisible(true);
	}
	
	public void closeWindow()
	{
		window.dispose();
	}

	@Override
	public Object getWindow() {
		return window;
	}
	
	protected void setupColumns(Grid parameterGrid) {
		noOfParameterColumn = ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2) ? 2 : 4;
		Columns columns = new Columns();
		parameterGrid.appendChild(columns);
		if (ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2))
		{
			Column column = new Column();
			ZKUpdateUtil.setWidth(column, "35%");
			columns.appendChild(column);
			column = new Column();
			ZKUpdateUtil.setWidth(column, "65%");
			columns.appendChild(column);
		}
		else
		{
			Column column = new Column();
			columns.appendChild(column);		
			column = new Column();
			ZKUpdateUtil.setWidth(column, "15%");
			columns.appendChild(column);
			ZKUpdateUtil.setWidth(column, "35%");
			column = new Column();
			ZKUpdateUtil.setWidth(column, "15%");
			columns.appendChild(column);
			column = new Column();
			ZKUpdateUtil.setWidth(column, "35%");
			columns.appendChild(column);
		}
	}
	
	protected void onClientInfo()
	{
		if (ClientInfo.isMobile() && parameterStdLayout != null && parameterStdLayout.getRows() != null)
		{
			int nc = ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2) ? 2 : 4;
			int cc = noOfParameterColumn;
			if (nc == cc)
				return;
			
			parameterStdLayout.getColumns().detach();
			setupColumns(parameterStdLayout);
			if (cc > nc)
			{
				LayoutUtils.compactTo(parameterStdLayout, nc);
			}
			else
			{
				LayoutUtils.expandTo(parameterStdLayout, nc, false);
			}
			
			ZKUpdateUtil.setCSSHeight(window);
			ZKUpdateUtil.setCSSWidth(window);
			window.invalidate();			
		}
	}
}
