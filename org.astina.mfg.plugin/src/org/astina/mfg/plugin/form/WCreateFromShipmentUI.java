package org.astina.mfg.plugin.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WLocatorEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MLocatorLookup;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MProduct;

import static org.compiere.model.SystemIDs.*;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Vlayout;

public class WCreateFromShipmentUI extends CreateFromShipment implements IFormController,EventListener<Event>, ValueChangeListener
{
	private CustomForm form = new CustomForm();
	private WCreateFromWindow window;
	
	public WCreateFromShipmentUI(GridTab tab) 
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
		
	protected Label bPartnerLabel = new Label();
	protected WEditor bPartnerField;
	
	protected Label orderLabel = new Label();
	protected Listbox orderField = ListboxFactory.newDropdownListbox();

    /** Label for the rma selection */
    protected Label rmaLabel = new Label();
    /** Combo box for selecting RMA document */
    protected Listbox rmaField = ListboxFactory.newDropdownListbox();
	
    protected Label invoiceLabel = new Label();
    protected Listbox invoiceField = ListboxFactory.newDropdownListbox();
	protected Checkbox sameWarehouseCb = new Checkbox();
	protected Label locatorLabel = new Label();
	protected WLocatorEditor locatorField = new WLocatorEditor();
	protected Label upcLabel = new Label();
	protected WStringEditor upcField = new WStringEditor();
	
	protected Label warehouseLabel = new Label();
	protected WTableDirEditor warehouseField;
	
	protected Label organizationLabel = new Label();
	protected WTableDirEditor organizationField;
	
	protected Label shipToLabel = new Label();
	protected WTableDirEditor shipToField;
	
	protected Label receiptLabel = new Label();
	protected WTableDirEditor receiptField;
	
	protected WEditor DocType;

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
		
		String docStatus = Env.getContext(Env.getCtx(), p_WindowNo, "DocStatus");
		
		if(docStatus.equals("CO"))
		{
		
			window.setTitle("Split Receipt");

			MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 3523, DisplayType.TableDir); //AD_Org_ID
			organizationField = new WTableDirEditor ("AD_Org_ID",false,false,false,lookup);
			
			int AD_Org_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "AD_Org_ID");
			organizationField.setValue(Integer.valueOf(AD_Org_ID));
			organizationField.setReadWrite(false);
			
			lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 3521, DisplayType.TableDir); //M_InOut_ID
			receiptField = new WTableDirEditor ("M_InOut_ID",false,false,false,lookup);
			
			int M_InOut_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "M_InOut_ID");
			receiptField.setValue(Integer.valueOf(M_InOut_ID));
			receiptField.setReadWrite(false);
			
			if(M_InOut_ID>0)
			{	
				loadReceipt((Integer)receiptField.getValue(), (Integer)organizationField.getValue());
			}
		}else
			if(docStatus.equals("DR") || docStatus.equals("IP")){
				window.setTitle(getTitle());
				
				sameWarehouseCb.setSelected(true);
				sameWarehouseCb.addActionListener(this);
				sameWarehouseCb.isDisabled();
				//  load Locator
				MLocatorLookup locator = new MLocatorLookup(Env.getCtx(), p_WindowNo);
				locatorField = new WLocatorEditor ("M_Locator_ID", true, false, true, locator, p_WindowNo);
				
				//int C_DocType_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_DocType_ID");
				//MDocType doctype = MDocType.get(Env.getCtx(), C_DocType_ID);
				
				MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 3523, DisplayType.TableDir); //AD_Org_ID
				organizationField = new WTableDirEditor ("AD_Org_ID",false,false,false,lookup);
				
				int AD_OrgTrx_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "AD_OrgTrx_ID");
				int AD_Org_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "AD_Org_ID");
				organizationField.setValue(Integer.valueOf(AD_Org_ID));
				organizationField.addValueChangeListener(this);
				organizationField.setReadWrite(false);
				
				MLookup shipTolookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 9586, DisplayType.TableDir); //AD_OrgTrx_ID
				shipToField = new WTableDirEditor ("AD_OrgTrx_ID",false,false,false,shipTolookup);
				shipToField.setValue(Integer.valueOf(AD_OrgTrx_ID));
				shipToField.addValueChangeListener(this);
				shipToField.setReadWrite(false);
				
				initSameWarehouse();
				initBPartner(false);
				bPartnerField.addValueChangeListener(this);
				bPartnerField.setReadWrite(false);
				//locatorLabel.setMandatory(true);
		
				upcField = new WStringEditor ("UPC", false, false, true, 10, 30, null, null);
				upcField.getComponent().addEventListener(Events.ON_CHANGE, this);
			}

		return true;
	}   //  dynInit
	
	protected void zkInit() throws Exception
	{
		String docStatus = Env.getContext(Env.getCtx(), p_WindowNo, "DocStatus");
		
		if(docStatus.equals("CO"))
		{
			receiptLabel.setText(Msg.translate(Env.getCtx(), "M_InOut_ID"));
			organizationLabel.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
			
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
			if (organizationField != null) {
				row.appendChild(organizationField.getComponent());
				organizationField.fillHorizontal();
			}
			row.appendChild(receiptLabel.rightAlign());
			if (receiptField != null) {
	    		row.appendChild(receiptField.getComponent());
	    	}
		
		}else
			if(docStatus.equals("DR") || docStatus.equals("IP")){
		    	boolean isRMAWindow = ((getGridTab().getAD_Window_ID() == 1000203) || (getGridTab().getAD_Window_ID() == WINDOW_RETURNTOVENDOR) || (getGridTab().getAD_Window_ID() == WINDOW_CUSTOMERRETURN)); 
		
		    	bPartnerLabel.setText(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				orderLabel.setText(Msg.getElement(Env.getCtx(), "C_Order_ID", false));
				//invoiceLabel.setText(Msg.getElement(Env.getCtx(), "C_Invoice_ID", false));
		        rmaLabel.setText(Msg.translate(Env.getCtx(), "M_RMA_ID"));
				locatorLabel.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
				
				int AD_Column_ID = 3792;        //  M_InOut.C_DocType_ID
				MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
				DocType = new WSearchEditor ("C_DocType_ID", true, false, true, lookup);
				
				shipToLabel.setText(Msg.translate(Env.getCtx(), "Ship To"));
				organizationLabel.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
				
				warehouseLabel.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
		        sameWarehouseCb.setText(Msg.getMsg(Env.getCtx(), "FromSameWarehouseOnly", true));
		        sameWarehouseCb.setTooltiptext(Msg.getMsg(Env.getCtx(), "FromSameWarehouseOnly", false));
		        sameWarehouseCb.isDisabled();
		        //upcLabel.setText(Msg.getElement(Env.getCtx(), "UPC", false));
		
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
				row.appendChild(bPartnerLabel.rightAlign());
				if (bPartnerField != null) {
					row.appendChild(bPartnerField.getComponent());
					bPartnerField.fillHorizontal();
				}
		    	if (! isRMAWindow) {
		    		row.appendChild(orderLabel.rightAlign());
		    		row.appendChild(orderField);
		    		ZKUpdateUtil.setHflex(orderField, "1");
		    	}
				
		    	if (isRMAWindow) {
		            // Add RMA document selection to panel
		            row.appendChild(rmaLabel.rightAlign());
		            row.appendChild(rmaField);
		            ZKUpdateUtil.setHflex(rmaField, "1");
		    	}
		    	
				row = rows.newRow();
				//row.appendChild(warehouseLabel.rightAlign());
				//row.appendChild(warehouseField.getComponent());
				
				row.appendChild(organizationLabel.rightAlign());
				row.appendChild(organizationField.getComponent());
				
				if (! isRMAWindow) {
					row.appendChild(shipToLabel.rightAlign());
					row.appendChild(shipToField.getComponent());
				}
		    	if (! isRMAWindow) {
		    		//row.appendChild(invoiceLabel.rightAlign());
		    		//row.appendChild(invoiceField);
		    		//ZKUpdateUtil.setHflex(invoiceField, "1");
		    	}
				
				//row.appendChild(new Space());
				//row.appendChild(sameWarehouseCb);
				
				//row = rows.newRow();
				//row.appendChild(upcLabel.rightAlign());
				//row.appendChild(upcField.getComponent());
				//ZKUpdateUtil.setHflex(upcField.getComponent(), "1");
			}
    	
    	if (ClientInfo.isMobile()) {    		
    		if (noOfParameterColumn == 2)
				LayoutUtils.compactTo(parameterStdLayout, 2);		
			ClientInfo.onClientInfo(window, this::onClientInfo);
		}
	}

	private boolean 	m_actionActive = false;
	
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
		
		//  Order
		if (e.getTarget().equals(orderField))
		{
			KeyNamePair pp = orderField.getSelectedItem().toKeyNamePair();
			if (pp == null || pp.getKey() == 0)
				;
			else
			{
				int C_Order_ID = pp.getKey();
				//  set Invoice and Shipment to Null
				invoiceField.setSelectedIndex(-1);
                rmaField.setSelectedIndex(-1);
                
                loadOrderItem(C_Order_ID, false, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
                
			}
		}
		//  Invoice
		else if (e.getTarget().equals(invoiceField))
		{
			KeyNamePair pp = invoiceField.getSelectedItem().toKeyNamePair();
			if (pp == null || pp.getKey() == 0)
				;
			else
			{
				int C_Invoice_ID = pp.getKey();
				//  set Order and Shipment to Null
				orderField.setSelectedIndex(-1);
                rmaField.setSelectedIndex(-1);
				loadInvoice(C_Invoice_ID, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
			}
		}
		// RMA
        else if (e.getTarget().equals(rmaField))
        {
            KeyNamePair pp = rmaField.getSelectedItem().toKeyNamePair();
            if (pp == null || pp.getKey() == 0)
                ;
            else
            {
                int M_RMA_ID = pp.getKey();
                //  set Order and Shipment to Null
                orderField.setSelectedIndex(-1);
                invoiceField.setSelectedIndex(-1);
                loadRMA(M_RMA_ID, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
            }
        }
		//sameWarehouseCb
        else if (e.getTarget().equals(sameWarehouseCb))
        {
        	int bpId = bPartnerField.getValue() == null?0:((Integer)bPartnerField.getValue()).intValue();
        	initBPOrderDetails(((Integer) getGridTab().getValue("M_Warehouse_ID")).intValue(), bpId, false);
        }	
		else if (e.getTarget().equals(upcField.getComponent()))
		{
			checkProductUsingUPC();
		}
		
		m_actionActive = false;
	}
	
	/**
	 * Checks the UPC value and checks if the UPC matches any of the products in the
	 * list.
	 */
	private void checkProductUsingUPC()
	{
		String upc = upcField.getDisplay();
		//DefaultTableModel model = (DefaultTableModel) dialog.getMiniTable().getModel();
		ListModelTable model = (ListModelTable) window.getWListbox().getModel();
		
		// Lookup UPC
		List<MProduct> products = MProduct.getByUPC(Env.getCtx(), upc, null);
		for (MProduct product : products)
		{
			int row = findProductRow(product.get_ID());
			if (row >= 0)
			{
				BigDecimal qty = (BigDecimal)model.getValueAt(row, 1);
				model.setValueAt(qty, row, 1);
				model.setValueAt(Boolean.TRUE, row, 0);
				model.updateComponent(row, row);
			}
		}
		upcField.setValue("");
	}

	/**
	 * Finds the row where a given product is. If the product is not found
	 * in the table -1 is returned.
	 * @param M_Product_ID
	 * @return  Row of the product or -1 if non existing.
	 * 
	 */
	private int findProductRow(int M_Product_ID)
	{
		//DefaultTableModel model = (DefaultTableModel)dialog.getMiniTable().getModel();
		ListModelTable model = (ListModelTable) window.getWListbox().getModel();
		KeyNamePair kp;
		for (int i=0; i<model.getRowCount(); i++) {
			kp = (KeyNamePair)model.getValueAt(i, 4);
			if (kp.getKey()==M_Product_ID) {
				return(i);
			}
		}
		return(-1);
	}
		
	/**
	 *  Change Listener
	 *  @param e event
	 */
	public void valueChange (ValueChangeEvent e)
	{
		if (log.isLoggable(Level.CONFIG)) log.config(e.getPropertyName() + "=" + e.getNewValue());

		//  BPartner - load Order/Invoice/Shipment
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			int C_BPartner_ID = 0; 
			if (e.getNewValue() != null){
				C_BPartner_ID = ((Integer)e.getNewValue()).intValue();
			}
			initBPOrderDetails (((Integer) getGridTab().getValue("M_Warehouse_ID")).intValue(), C_BPartner_ID, false);
		}
		window.tableChanged(null);
	}   //  vetoableChange
	
	/**************************************************************************
	 *  Load BPartner Field
	 *  @param forInvoice true if Invoices are to be created, false receipts
	 *  @throws Exception if Lookups cannot be initialized
	 */
	protected void initBPartner (boolean forInvoice) throws Exception
	{
		//  load BPartner
		int AD_Column_ID = 3499;        //  C_Invoice.C_BPartner_ID
		MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		bPartnerField = new WSearchEditor ("C_BPartner_ID", true, false, true, lookup);
		//
		int C_BPartner_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
		bPartnerField.setValue(Integer.valueOf(C_BPartner_ID));
    	
		//  initial loading
		initBPOrderDetails(((Integer) getGridTab().getValue("M_Warehouse_ID")).intValue(), C_BPartner_ID, forInvoice);
	}   //  initBPartner
	
	protected void initSameWarehouse () throws Exception
	{
		//  load Doc Type
		int AD_Column_ID = 3792;        //  M_InOut.C_DocType_ID
		MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		DocType = new WSearchEditor ("C_DocType_ID", true, false, true, lookup);
		//
		int C_DocType_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_DocType_ID");
		MDocType doctype = MDocType.get(Env.getCtx(), C_DocType_ID);
		
		if (doctype.getName().equals("Material Receipt to BO"))
		{
			sameWarehouseCb.setSelected(true);
		}

	}   //  initBPartner

	/**
	 * Init Details - load invoices not shipped
	 * @param C_BPartner_ID BPartner
	 */
	private void initBPInvoiceDetails(int C_BPartner_ID)
	{
		if (log.isLoggable(Level.CONFIG)) log.config("C_BPartner_ID" + C_BPartner_ID);

		//  load Shipments (Receipts) - Completed, Closed
		invoiceField.removeActionListener(this);
		invoiceField.removeAllItems();
		//	None
		KeyNamePair pp = new KeyNamePair(0,"");
		invoiceField.addItem(pp);
		
		ArrayList<KeyNamePair> list = loadInvoiceData(C_BPartner_ID);
		for(KeyNamePair knp : list)
			invoiceField.addItem(knp);
		
		invoiceField.setSelectedIndex(0);
		invoiceField.addActionListener(this);
		upcField.addValueChangeListener(this);
	}
	
	/**
	 *  Load PBartner dependent Order/Invoice/Shipment Field.
	 *  @param C_BPartner_ID BPartner
	 *  @param forInvoice for invoice
	 */
	protected void initBPOrderDetails (int M_Warehouse_ID, int C_BPartner_ID, boolean forInvoice)
	{
		if (log.isLoggable(Level.CONFIG)) log.config("C_BPartner_ID=" + C_BPartner_ID);
		KeyNamePair pp = new KeyNamePair(0,"");
		//  load PO Orders - Closed, Completed
		orderField.removeActionListener(this);
		orderField.removeAllItems();
		orderField.addItem(pp);
		
		ArrayList<KeyNamePair> list;
		
		list = loadOrderData(M_Warehouse_ID, C_BPartner_ID, forInvoice, (Integer)organizationField.getValue());
		
		for(KeyNamePair knp : list)
			orderField.addItem(knp);
		
		orderField.setSelectedIndex(0);
		orderField.addActionListener(this);

		initBPDetails(C_BPartner_ID);
	}   //  initBPOrderDetails
	
	public void initBPDetails(int C_BPartner_ID) 
	{
		initBPInvoiceDetails(C_BPartner_ID);
		initBPRMADetails(C_BPartner_ID);
	}

	
	/**
	 * Load RMA that are candidates for shipment
	 * @param C_BPartner_ID BPartner
	 */
	private void initBPRMADetails(int C_BPartner_ID)
	{
	    rmaField.removeActionListener(this);
	    rmaField.removeAllItems();
	    //  None
	    KeyNamePair pp = new KeyNamePair(0,"");
	    rmaField.addItem(pp);
	    
	    ArrayList<KeyNamePair> list = loadRMAData(C_BPartner_ID);
		for(KeyNamePair knp : list)
			rmaField.addItem(knp);
		
	    rmaField.setSelectedIndex(0);
	    rmaField.addActionListener(this);
	}

	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 */
/*	protected void loadOrder (int C_Order_ID, boolean forInvoice)
	{
		loadTableOIS(getOrderData(C_Order_ID, forInvoice));
	}   //  LoadOrder
	
	protected void loadRMA (int M_RMA_ID)
	{
		loadTableOIS(getRMAData(M_RMA_ID));
	}
	
	protected void loadShipment (int M_InOut_ID)
	{
		loadTableOIS(getShipmentData(M_InOut_ID));
	}*/
	
	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 *  @param M_Locator_ID
	 */
	protected void loadOrder (int C_Order_ID, boolean forInvoice, int M_Locator_ID)
	{
		loadTableOIS(getOrderData(C_Order_ID, forInvoice, M_Locator_ID));
	}   //  LoadOrder
	
	protected void loadOrderItem (int C_Order_ID, boolean forInvoice, int M_Locator_ID)
	{
		loadTableOIS(getOrderDataItem(C_Order_ID, forInvoice, M_Locator_ID));
	}   //  LoadOrder
	
	protected void loadReceipt (int M_InOut_ID, int AD_Org_ID)
	{
		loadTableReturn(getReceiptData(M_InOut_ID, AD_Org_ID));
	}   //  LoadReceiptReturn
	
	/**
	 *  Load Data - RMA
	 *  @param M_RMA_ID RMA
	 *  @param M_Locator_ID
	 */
	protected void loadRMA (int M_RMA_ID, int M_Locator_ID)
	{
		loadTableOIS(getRMAData(M_RMA_ID, M_Locator_ID));
	}
		
	/**
	 *  Load Data - Invoice
	 *  @param C_Invoice_ID Invoice
	 *  @param M_Locator_ID
	 */
	protected void loadInvoice (int C_Invoice_ID, int M_Locator_ID)
	{
		loadTableOIS(getInvoiceData(C_Invoice_ID, M_Locator_ID));
	}
	
	protected ArrayList<KeyNamePair> loadOrderDataItem (int M_Warehouse_ID, int C_BPartner_ID, boolean forInvoice, int AD_Org_ID, int AD_OrgTrx_ID)
	{
		return loadOrderDataItem(M_Warehouse_ID, C_BPartner_ID, forInvoice, AD_Org_ID, AD_OrgTrx_ID, false);
	}
	
	protected ArrayList<KeyNamePair> loadOrderDataItem (int M_Warehouse_ID, int C_BPartner_ID, boolean forInvoice, int AD_Org_ID, int AD_OrgTrx_ID, boolean forCreditMemo)
	{
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();

		String isSOTrxParam = isSOTrx ? "Y":"N";
		//	Display
		StringBuilder display = new StringBuilder("o.DocumentNo||' - ' ||")
			.append(DB.TO_CHAR("o.DateOrdered", DisplayType.Date, Env.getAD_Language(Env.getCtx())))
			.append("||' - '||")
			.append(DB.TO_CHAR("o.GrandTotal", DisplayType.Amount, Env.getAD_Language(Env.getCtx())));
		//
		String column = "ol.QtyDelivered";
		String colBP = "o.C_BPartner_ID";
		if (forInvoice)
		{
			column = "ol.QtyInvoiced";
			colBP = "o.Bill_BPartner_ID";
		}
		StringBuilder sql = new StringBuilder("SELECT o.C_Order_ID,")
			.append(display)
			.append(" FROM C_Order o WHERE ")
			.append(colBP)
			.append("=? AND o.IsSOTrx=? AND o.DocStatus IN ('CL','CO') AND o.C_Order_ID IN (SELECT ol.C_Order_ID FROM C_OrderLine ol WHERE ");
		if (forCreditMemo)
			sql.append(column).append(">0 AND (CASE WHEN ol.QtyDelivered>=ol.QtyOrdered THEN ol.QtyDelivered-ol.QtyInvoiced!=0 ELSE 1=1 END)) ");
		else
			sql.append("ol.QtyOrdered-").append(column).append("!=0) ");
		sql = sql.append(" AND o.AD_Org_ID=? AND o.AD_OrgTrx_ID=? AND o.M_Warehouse_ID=? ");
		
		if (forCreditMemo)
			sql = sql.append("ORDER BY o.DateOrdered DESC,o.DocumentNo DESC");
		else
			sql = sql.append("ORDER BY o.DateOrdered,o.DocumentNo");
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, C_BPartner_ID);
			pstmt.setString(2, isSOTrxParam);
			pstmt.setInt(3, AD_Org_ID);
			pstmt.setInt(4, AD_OrgTrx_ID);
			pstmt.setInt(5, M_Warehouse_ID);
			
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list;
	}   //  initBPartnerOIS
	
	/**
	 *  Load PBartner dependent Order/Invoice/Shipment Field.
	 *  @param C_BPartner_ID BPartner
	 *  @param forInvoice for invoice
	 */
	protected ArrayList<KeyNamePair> loadOrderData (int M_Warehouse_ID, int C_BPartner_ID, boolean forInvoice, int AD_Org_ID)
	{
		return loadOrderData(M_Warehouse_ID, C_BPartner_ID, forInvoice, AD_Org_ID, false);
	}
	
	protected ArrayList<KeyNamePair> loadOrderData (int M_Warehouse_ID, int C_BPartner_ID, boolean forInvoice, int AD_Org_ID, boolean forCreditMemo)
	{
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();

		String isSOTrxParam = isSOTrx ? "Y":"N";
		//	Display
		StringBuilder display = new StringBuilder("o.DocumentNo||' - ' ||")
			.append(DB.TO_CHAR("o.DateOrdered", DisplayType.Date, Env.getAD_Language(Env.getCtx())))
			.append("||' - '||")
			.append(DB.TO_CHAR("o.GrandTotal", DisplayType.Amount, Env.getAD_Language(Env.getCtx())));
		//
		String column = "ol.QtyDelivered";
		String colBP = "o.C_BPartner_ID";
		if (forInvoice)
		{
			column = "ol.QtyInvoiced";
			colBP = "o.Bill_BPartner_ID";
		}
		StringBuilder sql = new StringBuilder("SELECT o.C_Order_ID,")
			.append(display)
			.append(" FROM C_Order o WHERE ")
			.append(colBP)
			.append("=? AND o.IsSOTrx=? AND o.DocStatus IN ('CL','CO') AND o.C_Order_ID IN (SELECT ol.C_Order_ID FROM C_OrderLine ol WHERE ");
		if (forCreditMemo)
			sql.append(column).append(">0 AND (CASE WHEN ol.QtyDelivered>=ol.QtyOrdered THEN ol.QtyDelivered-ol.QtyInvoiced!=0 ELSE 1=1 END)) ");
		else
			sql.append("ol.QtyOrdered-").append(column).append("!=0) ");
		sql = sql.append(" AND o.AD_Org_ID=? AND o.M_Warehouse_ID=? ");
		
		if (forCreditMemo)
			sql = sql.append("ORDER BY o.DateOrdered DESC,o.DocumentNo DESC");
		else
			sql = sql.append("ORDER BY o.DateOrdered,o.DocumentNo");
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, C_BPartner_ID);
			pstmt.setString(2, isSOTrxParam);
			pstmt.setInt(3, AD_Org_ID);
			pstmt.setInt(4,  M_Warehouse_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list;
	}   //  initBPartnerOIS
		
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
		//
		
		configureMiniTable(window.getWListbox());
	}   //  loadOrder
	
	/**
	 *  Load Order/Invoice/Shipment data into Table
	 *  @param data data
	 */
	protected void loadTableReturn (Vector<?> data)
	{
		window.getWListbox().clear();
		
		//  Remove previous listeners
		window.getWListbox().getModel().removeTableModelListener(window);
		//  Set Model
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(window);
		window.getWListbox().setData(model, getReturnColumnNames());

		configureMiniTableReturn(window.getWListbox());
	}   //  loadReceiptReturn
	
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
	
	public void dispose()
	{
		SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose
	
	public ADForm getForm() {
		return form;
	}
}
