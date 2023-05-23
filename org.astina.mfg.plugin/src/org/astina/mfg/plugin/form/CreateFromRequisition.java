package org.astina.mfg.plugin.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.POWrapper;
import org.astina.mfg.plugin.model.I_C_OrderLine_Mfg;
import org.astina.mfg.plugin.model.I_C_Order_Mfg;
import org.astina.mfg.plugin.model.I_M_Product_Mfg;
import org.astina.mfg.plugin.model.I_M_RequisitionLine_Mfg;
import org.astina.mfg.plugin.model.I_M_Requisition_Mfg;
import org.astina.mfg.plugin.model.MFG_MOrder;
import org.astina.mfg.plugin.model.MFG_MOrderLine;
import org.astina.mfg.plugin.model.MFG_MRequisition;
import org.astina.mfg.plugin.model.MFG_MRequisitionLine;
import org.compiere.apps.IStatusBar;
import org.compiere.grid.CreateFrom;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.GridTab;
import org.compiere.model.MBPartner;
import org.compiere.model.MCost;
import org.compiere.model.MUOM;
import org.compiere.model.MUOMConversion;
import org.compiere.process.DocAction;
import org.compiere.model.MOrg;
import org.compiere.model.MProduct;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

/**
 *  Create Invoice Transactions from PO Orders or Receipt
 *
 *  @author Jorg Janke
 *  @version  $Id: VCreateFromShipment.java,v 1.4 2006/07/30 00:51:28 jjanke Exp $
 * 
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 			<li>BF [ 1896947 ] Generate invoice from Order error
 * 			<li>BF [ 2007837 ] VCreateFrom.save() should run in trx
 */
public abstract class CreateFromRequisition extends CreateFrom
{
	/**  Loaded Order             */
	protected MFG_MRequisition	m_requisition = null;
	protected MOrg	ad_org = null;
	protected MBPartner Bpartner = null;

	/**
	 *  Protected Constructor
	 *  @param mTab MTab
	 */
	public CreateFromRequisition(GridTab mTab)
	{
		super(mTab);
		if (log.isLoggable(Level.INFO)) log.info(mTab.toString());
	}   //  VCreateFromShipment

	/**
	 *  Dynamic Init
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		setTitle(Msg.getElement(Env.getCtx(), "C_Order_ID", false) + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));
		return true;
	}   //  dynInit
	
	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 */
	protected Vector<Vector<Object>> getRequisitionData (int M_Requisition_ID, int AD_Org_ID, boolean supplierPO)
	{
		/**
		 *  Selected        - 0
		 *  Qty             - 1
		 *  C_UOM_ID        - 2
		 *  M_Product_ID    - 3
		 *  Product Name    - 4
		 *  PR Line Description - 5
		 *  Requisition     - 6
		 *  Unit Price      - 7
		 *  Supplier     	- 8
		 */
		if (log.isLoggable(Level.CONFIG)) log.config("M_Requisition_ID=" + M_Requisition_ID);
		m_requisition = new MFG_MRequisition(Env.getCtx(), M_Requisition_ID, null);      //  save
		int C_Order_ID = ((Integer) getGridTab().getValue("C_Order_ID")).intValue();
		MFG_MOrder corder = new MFG_MOrder(Env.getCtx(), C_Order_ID, null);

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT "
				+ "COALESCE(l.QtyRemains_PO,0), " //1
				+ " l.C_UOM_ID, COALESCE(uom.UOMSymbol,uom.Name),"			//	2..3
				+ " po.sku,"  //4
				+ " COALESCE(l.M_Product_ID,0), COALESCE(po.sku,l.description), " //	5..6
				+ " po.name, " //	7
				+ " l.description, " //	8
				+ " M.DocumentNo, " //	9
				+ " l.priceactual, "	//	10
				+ " l.M_RequisitionLine_ID, l.Line, ");	//	11..12
				if(supplierPO)
				{
					sql = sql.append(corder.getC_BPartner_ID() + " c_bpartner_id, " + "'"+corder.getC_BPartner().getName()+"'" + " supplier, " ); //12..13
				}else
				{
					sql = sql.append(" l.C_Bpartner_ID, cbp.Name, "); // 12..13
				}				
				sql = sql.append(" po.producttype, po.LCO_WithholdingCategory_ID, lco.Name, "); //14..16
				sql = sql.append(" mw.M_Warehouse_ID, mw.Value "); // 17..18
				sql = sql.append( "FROM M_RequisitionLine l"
						+ " JOIN M_Requisition m ON l.M_Requisition_ID = m.M_Requisition_ID "
						+ " JOIN M_Warehouse mw ON mw.M_Warehouse_ID = m.M_Warehouse_ID "
						+ " LEFT JOIN M_Product po ON l.M_Product_ID = po.M_Product_ID "
					    + " LEFT JOIN C_UOM uom ON l.C_UOM_ID=uom.C_UOM_ID "
					    + " LEFT JOIN C_BPartner cbp ON cbp.C_Bpartner_ID=l.C_Bpartner_ID "
				 		+ " LEFT JOIN LCO_WithholdingCategory lco ON lco.LCO_WithholdingCategory_ID=po.LCO_WithholdingCategory_ID ");
				//
				sql.append(" WHERE m.M_Requisition_ID=? and m.AD_Org_ID=? and l.qtyRemains_PO>0 ");
				sql = sql.append("ORDER BY l.M_Requisition_ID Desc, l.Line");
		
		//
		if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, M_Requisition_ID);
			pstmt.setInt(2, AD_Org_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.FALSE);           //  0-Selection
				line.add(rs.getBigDecimal(1));  // 1- Qty Remains
				KeyNamePair pp = new KeyNamePair(rs.getInt(2), rs.getString(3).trim());
				line.add(pp);                           //  2-UOM
				line.add(rs.getString(4)); // 4-Barcode ID
				pp = new KeyNamePair(rs.getInt(5), rs.getString(6));
				line.add(pp);                           //  5-Product, SKU
				line.add(rs.getString(7)); // 7-product name
				line.add(rs.getString(8)); // 8-PR line Desc
				pp = new KeyNamePair(rs.getInt(11), rs.getString(12));
				line.add(pp);   //11-PR Line no
				line.add(rs.getBigDecimal(10));  // 9- unit price
				line.add(rs.getString(9));   //9-PR No
				line.add(rs.getString(15)); // 15-product type
				pp = new KeyNamePair(rs.getInt(16), rs.getString(17));
				line.add(pp);   //16-Wht
				pp = new KeyNamePair(rs.getInt(18), rs.getString(19));
				line.add(pp);   //18-warehouse
				line.add(rs.getBigDecimal(1));  // 19- Qty 
				data.add(line);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
			//throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return data;
	}   //  LoadRequisition
	
	protected Vector<Vector<Object>> getRequisitionDatabyProduct (int M_Product_ID, int AD_Org_ID, boolean supplierPO)
	{
		/**
		 *  Selected        - 0
		 *  Qty             - 1
		 *  C_UOM_ID        - 2
		 *  M_Product_ID    - 3
		 *  Product Name    - 4
		 *  PR Line Description - 5
		 *  Requisition     - 6
		 *  Unit Price      - 7
		 *  Supplier     	- 8
		 */
		if (log.isLoggable(Level.CONFIG)) log.config("M_Product_ID=" + M_Product_ID);
		//m_requisition = new MRequisition(Env.getCtx(), M_Product_ID, null);      //  save
		int C_Order_ID = ((Integer) getGridTab().getValue("C_Order_ID")).intValue();
		MFG_MOrder corder = new MFG_MOrder(Env.getCtx(), C_Order_ID, null);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT "
				+ "COALESCE(l.QtyRemains_PO,0), " //1
				+ " l.C_UOM_ID, COALESCE(uom.UOMSymbol,uom.Name)," //	2..3
				+ " po.sku,"  //4
				+ " COALESCE(l.M_Product_ID,0), COALESCE(po.sku,l.description), " //	5..6
				+ " po.name, " //	7
				+ " l.description, " //	8
				+ " M.DocumentNo, " //	9
				+ " l.priceactual, "	//	10
				+ " l.M_RequisitionLine_ID, l.Line, ");	//	11..12
				if(supplierPO)
				{
					sql = sql.append(corder.getC_BPartner_ID() + " c_bpartner_id, " + "'"+corder.getC_BPartner().getName()+"'" + " supplier, " ); //12..13
				}else
				{
					sql = sql.append(" l.C_Bpartner_ID, cbp.Name, "); // 12..13
				}
				sql = sql.append(" po.producttype, po.LCO_WithholdingCategory_ID, lco.Name, "); // 14..15..16
				sql = sql.append(" mw.M_Warehouse_ID, mw.Value "); // 17..18
				sql.append( "FROM M_RequisitionLine l"
				+ " JOIN M_Requisition m ON l.M_Requisition_ID = m.M_Requisition_ID "
				+ " JOIN M_Product po ON l.M_Product_ID = po.M_Product_ID "
				+ " JOIN M_Warehouse mw ON mw.M_Warehouse_ID = m.M_Warehouse_ID "
			    + " LEFT JOIN C_UOM uom ON l.C_UOM_ID=uom.C_UOM_ID "
			    + " LEFT JOIN C_BPartner cbp ON cbp.C_Bpartner_ID=l.C_Bpartner_ID "
			    + " LEFT JOIN LCO_WithholdingCategory lco ON lco.LCO_WithholdingCategory_ID=po.LCO_WithholdingCategory_ID ");
				//
				sql.append(" WHERE po.M_Product_ID=? and m.AD_Org_ID=? and l.qtyRemains_PO>0 "			//	#1
						+ "ORDER BY l.M_Requisition_ID Desc, l.Line");
		//
		if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, M_Product_ID);
			pstmt.setInt(2, AD_Org_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.FALSE);           //  0-Selection
				line.add(rs.getBigDecimal(1));  // 1- Qty Remains
				KeyNamePair pp = new KeyNamePair(rs.getInt(2), rs.getString(3).trim());
				line.add(pp);                           //  2-UOM
				line.add(rs.getString(4)); // 4-Barcode ID
				pp = new KeyNamePair(rs.getInt(5), rs.getString(6));
				line.add(pp);                           //  5-Product
				line.add(rs.getString(7)); // 7-product name
				line.add(rs.getString(8)); // 8-PR line Desc
				pp = new KeyNamePair(rs.getInt(11), rs.getString(12));
				line.add(pp);   //11-PR Line no
				line.add(rs.getBigDecimal(10));  // 9- unit price
				line.add(rs.getString(9));   //9-PR No
				line.add(rs.getString(15)); // 15-product type
				pp = new KeyNamePair(rs.getInt(16), rs.getString(17));
				line.add(pp);   //16-Wht
				pp = new KeyNamePair(rs.getInt(18), rs.getString(19));
				line.add(pp);   //18-warehouse
				line.add(rs.getBigDecimal(1));  // 19- Qty 
				data.add(line);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
			//throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return data;
	}   //  LoadOrder
	
	/**
	 *  List number of rows selected
	 */
	public void info(IMiniTable miniTable, IStatusBar statusBar)
	{

	}   //  infoInvoice

	protected void configureMiniTable (IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);     //  Selection
		miniTable.setColumnClass(1, BigDecimal.class, false);      //  Qty
		miniTable.setColumnClass(2, String.class, true);          //  UOM
		miniTable.setColumnClass(3, String.class, false);  //  Barcode ID 
		miniTable.setColumnClass(3, String.class, false);  //  Product 
		miniTable.setColumnClass(4, String.class, true);   //  Product name 
		miniTable.setColumnClass(5, String.class, true);   //  PR Line Desc 
		miniTable.setColumnClass(6, String.class, true);     //  Line No
		miniTable.setColumnClass(7, BigDecimal.class, true);   //  Unit Cost
		miniTable.setColumnClass(8, String.class, true);   //  PR No
		miniTable.setColumnClass(9, String.class, true);     //  Product Type
		miniTable.setColumnClass(10, BigDecimal.class, true);   //  Wht
		miniTable.setColumnClass(11, BigDecimal.class, true);   //  Warehouse
		miniTable.setColumnClass(12, BigDecimal.class, true);      //  Qty
		
		//  Table UI
		miniTable.autoSize();
	}
	
	protected void configureMiniTableShipment (IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);     //  Selection
		miniTable.setColumnClass(1, String.class, true);          //  Barge
		miniTable.setColumnClass(2, String.class, true);  //  Document
		miniTable.setColumnClass(3, String.class, true);  //  Fleet
		miniTable.setColumnClass(4, BigDecimal.class, true);  //  PriceList 
		miniTable.setColumnClass(5, BigDecimal.class, true);   //  Qty 
		miniTable.setColumnClass(6, Timestamp.class, true);   //  Date Loading
		miniTable.setColumnClass(7, Timestamp.class, true);     //  Date Discharge
		miniTable.setColumnClass(8, String.class, true);   // POD
		miniTable.setColumnClass(9, String.class, true);     //  Contract No
		miniTable.setColumnClass(10, String.class, true);   //  Description of Goods
		miniTable.setColumnClass(11, Integer.class, true);      //  Ship ID
		
		//  Table UI
		miniTable.autoSize();
		
	}

	/**
	 *  Save - Create Order Lines
	 *  @return true if saved
	 */
	public boolean save(IMiniTable miniTable, String trxName)
	{
		
		// Get Requisition
		int M_Requisition_ID = 0;
		int C_BPartner_ID = 0;
		int C_Order_ID = ((Integer) getGridTab().getValue("C_Order_ID")).intValue();
		MFG_MOrder corder = new MFG_MOrder(Env.getCtx(), C_Order_ID, trxName);
		
		//Check Qty Input
		for (int j = 0; j < miniTable.getRowCount(); j++)
		{
			if (((Boolean)miniTable.getValueAt(j, 0)).booleanValue()) {
				// variable values
				BigDecimal QtyEntered = (BigDecimal) miniTable.getValueAt(j, 1); // Qty
				BigDecimal QtyChek = (BigDecimal) miniTable.getValueAt(j, 13); // Qty
				if(QtyEntered.compareTo(QtyChek)>0)
				{
					String msg = "Quantity Entered Over Quantity Outstanding Order. Product=" + miniTable.getValueAt(j, 3);
					throw new AdempiereException(msg);
					//return false;
				}
				
				if(QtyEntered.compareTo(BigDecimal.valueOf(0))<=0)
				{
					String msg = "Quantity Entered must greater than 0. Product=" + miniTable.getValueAt(j, 3);
					throw new AdempiereException(msg);
					//return false;
				}
			}
		}
				
		// Lines
		for (int i = 0; i < miniTable.getRowCount(); i++)
		{
			if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue()) {
				// variable values
				BigDecimal Qty = (BigDecimal) miniTable.getValueAt(i, 1); // Qty
				KeyNamePair pp = (KeyNamePair) miniTable.getValueAt(i, 2); // UOM
				int C_UOM_ID = pp.getKey();
				
				pp = (KeyNamePair) miniTable.getValueAt(i, 4); // Product
				int M_Product_ID = pp.getKey();
				int M_RequisitionLine_ID = 0;
				pp = (KeyNamePair) miniTable.getValueAt(i, 7); // RequisitionLine
				if (pp != null)
					M_RequisitionLine_ID = pp.getKey();
				
				//	Precision of Qty UOM
				int precision = 2;
				MProduct product = MProduct.get(Env.getCtx(), M_Product_ID);
				I_M_Product_Mfg item = POWrapper.create(product, I_M_Product_Mfg.class);
				precision = product.getUOMPrecision();
				
				Qty = Qty.setScale(precision, RoundingMode.HALF_DOWN);
				//
				if (log.isLoggable(Level.FINE)) log.fine("Line QtyEntered=" + Qty
						+ ", Product=" + M_Product_ID 
						+ ", OrderLine=" + M_RequisitionLine_ID);
				
				//
				MFG_MRequisitionLine ol = null;
				if (M_RequisitionLine_ID != 0)
				{
					ol = new MFG_MRequisitionLine (Env.getCtx(), M_RequisitionLine_ID, trxName);
					//find by m_requisitionline_id, m_product_id, dan c_order_id, jika ketemu hanya update qty
					String sql = "SELECT C_Orderline_ID FROM C_OrderLine "
							+ "WHERE M_Product_ID=? AND C_Order_ID=? AND M_Requisitionline_ID=?";
					int orderLine_ID = org.compiere.util.DB.getSQLValue(null,sql, M_Product_ID , C_Order_ID, M_RequisitionLine_ID);
					
					if(orderLine_ID > 0)
					{
						MFG_MOrderLine orderline = new MFG_MOrderLine(Env.getCtx(), orderLine_ID, trxName);
						orderline.setQtyEntered(orderline.getQtyEntered().add(Qty));
						orderline.setQtyOrdered(orderline.getQtyOrdered().add(Qty));
						I_C_OrderLine_Mfg corderlines = POWrapper.create(orderline, I_C_OrderLine_Mfg.class);
						corderlines.setQtyRequisition(corderlines.getQtyRequisition().add(Qty));
						
						orderline.saveEx();
					}else
					{
						//Create new InOut Line
						MFG_MOrderLine iol = new MFG_MOrderLine (corder);
						iol.setM_Product_ID(M_Product_ID, C_UOM_ID);	//	Line UOM
						iol.setQty(Qty);							//	Qty Requisition
						I_C_OrderLine_Mfg corderline = POWrapper.create(iol, I_C_OrderLine_Mfg.class);
						corderline.setQtyRequisition(Qty);
						corderline.setProductType(product.getProductType());
						MFG_MRequisition Mreq = new MFG_MRequisition (Env.getCtx(), ol.getM_Requisition_ID(), trxName);
						
						if ((product.getC_UOM_ID() != ol.getC_UOM_ID()) && product.getProductType().equalsIgnoreCase("I"))
						{
							//int C_UOM_To_ID = ((Integer)value).intValue();
							BigDecimal QtyEntered = Qty;
							BigDecimal QtyEntered1 = Qty.setScale(MUOM.getPrecision(Env.getCtx(), ol.getC_UOM_ID()), RoundingMode.HALF_UP);
							if (Qty.compareTo(QtyEntered1) != 0)
							{
								if (log.isLoggable(Level.FINE)) log.fine("Corrected QtyEntered Scale UOM=" + ol.getC_UOM_ID()
									+ "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
								QtyEntered = QtyEntered1;
								iol.setQtyEntered(QtyEntered);
							}
							BigDecimal QtyOrdered = MUOMConversion.convertProductFrom (Env.getCtx(), M_Product_ID,
									ol.getC_UOM_ID(), QtyEntered);
							if (QtyOrdered == null)
								QtyOrdered = QtyEntered;
							boolean conversion = QtyEntered.compareTo(QtyOrdered) != 0;
							//PriceActual = (BigDecimal)mTab.getValue("PriceActual");
							BigDecimal PriceEntered = MUOMConversion.convertProductFrom (Env.getCtx(), M_Product_ID,
									ol.getC_UOM_ID(), ol.getPriceActual());
							if (PriceEntered == null)
								PriceEntered = ol.getPriceActual();
							if (log.isLoggable(Level.FINE)) log.fine("UOM=" + ol.getC_UOM_ID()
								+ ", QtyEntered/PriceActual=" + QtyEntered + "/" + ol.getPriceActual()
								+ " -> " + conversion
								+ " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered);
							Env.setContext(Env.getCtx(), 1, "UOMConversion", conversion ? "Y" : "N");
							iol.setQtyOrdered(QtyOrdered);
							
							iol.setPriceEntered(ol.getPriceActual());
							
							MUOMConversion[] rates = MUOMConversion.getProductConversions(Env.getCtx(), M_Product_ID);
							BigDecimal ratemultiple = Env.ZERO;
							for (int j = 0; j < rates.length; j++)
							{
								MUOMConversion rate = rates[j];
								if (rate.getC_UOM_To_ID() == ol.getC_UOM_ID())
								{
									ratemultiple = rate.getMultiplyRate();
									j=rates.length;
								}
							}
							if(ratemultiple != null)
							{
								//iol.setPriceActual(ol.getPriceActual().multiply(ratemultiple));
								//iol.setPriceList(ol.getPriceActual());
								//iol.setPriceCost(ol.getPriceActual());
								//iol.setDiscount(Env.ZERO);
							}
							else
							{
								//iol.setPriceActual(PriceEntered);
								//iol.setPriceCost(PriceEntered);
								//iol.setPriceList(ol.getPriceActual());
								//iol.setDiscount(Env.ZERO);
							}
							
						}else
						{
							iol.setQty(Qty);
							iol.setPrice(ol.getPriceActual());
							iol.setPriceCost(ol.getPriceActual());
							iol.setPriceList(ol.getPriceActual());
							iol.setDiscount(Env.ZERO);
						}
						
						iol.setC_UOM_ID(ol.getC_UOM_ID());						
						
						iol.setDescription(ol.getDescription());
						
						iol.setDatePromised(ol.getDateRequired());
						iol.setAD_Org_ID(corder.getAD_Org_ID());
						I_M_Requisition_Mfg mreqMining = POWrapper.create(Mreq, I_M_Requisition_Mfg.class);
						iol.setUser1_ID(mreqMining.getUser1_ID());
						corderline.setM_Requisitionline_ID(ol.getM_RequisitionLine_ID());	
						
						if (mreqMining.getC_Activity_ID()>0)
						{
							iol.setC_Activity_ID(mreqMining.getC_Activity_ID());
						}
						
						if (item.getLCO_WithholdingCategory_ID()>0)
						{
							corderline.setLCO_WithholdingCategory_ID(item.getLCO_WithholdingCategory_ID());
						}
						
						//pp = (KeyNamePair) miniTable.getValueAt(i, 9); // Supplier
						//if (pp != null)
						//{
						//	C_BPartner_ID = pp.getKey();
						//	iol.setC_BPartner_ID(C_BPartner_ID);
						//}
						
						BigDecimal retValue = MCost.getLastPOPrice (product, 0, corder.getAD_Org_ID(), corder.getC_Currency_ID());
						corderline.setPriceLastPO(retValue);
						iol.saveEx();
					}
					
				}
				I_M_RequisitionLine_Mfg mreqline = POWrapper.create(ol, I_M_RequisitionLine_Mfg.class);
				if(mreqline.getQtyRemains_PO().subtract(Qty).compareTo(BigDecimal.valueOf(0)) <= 0)
				{
					mreqline.setQtyRemains_PO(BigDecimal.valueOf(0));
				}else {
					mreqline.setQtyRemains_PO(mreqline.getQtyRemains_PO().subtract(Qty));
				}
				ol.saveEx();
				M_Requisition_ID = ol.getM_Requisition_ID();
			}   //   if selected
		}   //  for all rows
		
		String sql = "SELECT SUM(QtyRemains_PO) FROM M_RequisitionLine "
				+ "WHERE M_Requisition_ID=?";
		BigDecimal countQtyPO = org.compiere.util.DB.getSQLValueBD(null,sql, M_Requisition_ID);
		
		if(countQtyPO.compareTo(BigDecimal.valueOf(0))<=0)
		{
			MFG_MRequisition Mreq1 = new MFG_MRequisition (Env.getCtx(), M_Requisition_ID, trxName);
			Mreq1.setDocAction(DocAction.ACTION_None);
			Mreq1.setDocStatus(DocAction.STATUS_Closed);
			Mreq1.saveEx();
		}
		
		/**
		 *  Update Header PO
		 */
		m_requisition = new MFG_MRequisition (Env.getCtx(), M_Requisition_ID, trxName);
		I_M_Requisition_Mfg mreqmining = POWrapper.create(m_requisition, I_M_Requisition_Mfg.class);
		corder.setDatePromised(m_requisition.getDateRequired());
		Bpartner = MBPartner.get(Env.getCtx(), C_BPartner_ID);
		corder.setBPartner(Bpartner);
		//corder.setM_PriceList_ID(m_requisition.getM_PriceList_ID());
		corder.setDescription(m_requisition.getDescription());
		corder.setUser1_ID(mreqmining.getUser1_ID());
		I_C_Order_Mfg cor = POWrapper.create(corder, I_C_Order_Mfg.class);
		cor.setM_Requisition_ID(m_requisition.getM_Requisition_ID());
		cor.setC_PRRoute_ID(mreqmining.getC_PRRoute_ID());
		
		if(mreqmining.getC_Activity_ID()>0)
		{
			cor.setC_Activity_ID(mreqmining.getC_Activity_ID());
		}
		corder.setIsSOTrx(false);
		corder.saveEx();
		
		return true;

	}   //  saveOrder

	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
	    Vector<String> columnNames = new Vector<String>(14);
	    columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Quantity"));
	    columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Item ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Name"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Description"));
	    columnNames.add(Msg.getElement(Env.getCtx(), "Line"));
	    columnNames.add(Msg.translate(Env.getCtx(), "PriceList"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Requisition"));
	    columnNames.add(Msg.translate(Env.getCtx(), "ProductType"));
	    columnNames.add(Msg.translate(Env.getCtx(), "LCO_WithholdingCategory_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "QtyOrdered"));
	    
	    return columnNames;
	}

}