// 
// Decompiled by Procyon v0.5.36
// 

package org.adempiere.model.engines;

import org.compiere.model.MWarehouse;
import java.util.List;
import org.compiere.model.Query;
import java.util.Properties;
import org.compiere.model.MTable;
import org.compiere.model.MTransaction;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MProduct;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.compiere.util.CLogger;

public class StorageEngine
{
    protected static transient CLogger log;
    static BigDecimal qtyparsial;
    
    static {
        StorageEngine.log = CLogger.getCLogger((Class)StorageEngine.class);
        StorageEngine.qtyparsial = BigDecimal.ZERO;
    }
    
    public static void createTransaction(final IDocumentLine docLine, final String MovementType, final Timestamp MovementDate, BigDecimal Qty, final boolean isReversal, final int M_Warehouse_ID, final int o_M_AttributeSetInstance_ID, final int o_M_Warehouse_ID, final boolean isSOTrx) {
        final MProduct product = MProduct.get(docLine.getCtx(), docLine.getM_Product_ID());
        final boolean incomingTrx = MovementType.charAt(1) == '+';
        if (product != null && product.isStocked()) {
            if (!isReversal) {
                checkMaterialPolicy(docLine, MovementType, MovementDate, M_Warehouse_ID);
            }
            if (docLine.getM_AttributeSetInstance_ID() == 0) {
                final IInventoryAllocation[] mas = getMA(docLine);
                for (int j = 0; j < mas.length; ++j) {
                    final IInventoryAllocation ma = mas[j];
                    BigDecimal QtyMA = ma.getMovementQty();
                    if (!incomingTrx) {
                        QtyMA = QtyMA.negate();
                    }
                    for (int i = 0; i < MStorageOnHand.getWarehouse(docLine.getCtx(), o_M_Warehouse_ID, product.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), (Timestamp)null, true, true, docLine.getM_Locator_ID(), docLine.get_TrxName(), false).length; ++i) {
                        final MStorageOnHand mStorageOnHand = MStorageOnHand.getWarehouse(docLine.getCtx(), o_M_Warehouse_ID, product.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), (Timestamp)null, true, true, docLine.getM_Locator_ID(), docLine.get_TrxName(), false)[i];
                        StorageEngine.qtyparsial = mStorageOnHand.getQtyOnHand();
                        if (!MStorageOnHand.add(docLine.getCtx(), M_Warehouse_ID, docLine.getM_Locator_ID(), docLine.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), Qty.negate(), mStorageOnHand.getDateMaterialPolicy(), docLine.get_TrxName())) {
                            throw new AdempiereException();
                        }
                        create(docLine, MovementType, MovementDate, ma.getM_AttributeSetInstance_ID(), Qty.negate());
                    }
                }
            }
            else {
                if (!incomingTrx) {
                    Qty = Qty.negate();
                }
                final Timestamp datematerialpolicy = DB.getSQLValueTS((String)null, "select datematerialpolicy from m_storage where qtyonhand > 0 and m_product_id = " + docLine.getM_Product_ID() + " and m_locator_id = " + docLine.getM_Locator_ID() + " and M_AttributeSetInstance_ID = " + docLine.getM_AttributeSetInstance_ID(), new Object[0]);
                if (!MStorageOnHand.add(docLine.getCtx(), M_Warehouse_ID, docLine.getM_Locator_ID(), docLine.getM_Product_ID(), docLine.getM_AttributeSetInstance_ID(), Qty, datematerialpolicy, docLine.get_TrxName())) {
                    throw new AdempiereException();
                }
                create(docLine, MovementType, MovementDate, docLine.getM_AttributeSetInstance_ID(), Qty);
            }
        }
    }
    
    private static void checkMaterialPolicy(final IDocumentLine line, final String MovementType, final Timestamp MovementDate, final int M_Warehouse_ID) {
        deleteMA(line);
        final boolean incomingTrx = MovementType.charAt(1) == '+';
        final MProduct product = MProduct.get(line.getCtx(), line.getM_Product_ID());
        line.getM_Locator_ID();
        if (line.getM_AttributeSetInstance_ID() == 0) {
            if (incomingTrx) {
                MAttributeSetInstance asi = null;
                final MStorageOnHand[] storages = MStorageOnHand.getWarehouse(line.getCtx(), M_Warehouse_ID, line.getM_Product_ID(), 0, (Timestamp)null, "F".equals(product.getMMPolicy()), false, line.getM_Locator_ID(), line.get_TrxName());
                MStorageOnHand[] array;
                for (int length = (array = storages).length, i = 0; i < length; ++i) {
                    final MStorageOnHand storage = array[i];
                    if (storage.getQtyOnHand().signum() < 0) {
                        asi = new MAttributeSetInstance(line.getCtx(), storage.getM_AttributeSetInstance_ID(), line.get_TrxName());
                        break;
                    }
                }
                if (asi == null) {
                    asi = MAttributeSetInstance.create(line.getCtx(), product, line.get_TrxName());
                }
                line.setM_AttributeSetInstance_ID(asi.getM_AttributeSetInstance_ID());
                StorageEngine.log.config("New ASI=" + line);
                createMA(line, line.getM_AttributeSetInstance_ID(), line.getMovementQty());
            }
            else {
                final String MMPolicy = product.getMMPolicy();
                final Timestamp minGuaranteeDate = MovementDate;
                final MStorageOnHand[] storages2 = MStorageOnHand.getWarehouse(line.getCtx(), M_Warehouse_ID, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), minGuaranteeDate, "F".equals(MMPolicy), true, line.getM_Locator_ID(), line.get_TrxName());
                BigDecimal qtyToDeliver = line.getMovementQty();
                MStorageOnHand[] array2;
                for (int length2 = (array2 = storages2).length, j = 0; j < length2; ++j) {
                    final MStorageOnHand storage2 = array2[j];
                    if (storage2.getQtyOnHand().compareTo(qtyToDeliver) >= 0) {
                        createMA(line, storage2.getM_AttributeSetInstance_ID(), qtyToDeliver);
                        qtyToDeliver = Env.ZERO;
                    }
                    else {
                        createMA(line, storage2.getM_AttributeSetInstance_ID(), storage2.getQtyOnHand());
                        qtyToDeliver = qtyToDeliver.subtract(storage2.getQtyOnHand());
                        StorageEngine.log.fine("QtyToDeliver=" + qtyToDeliver);
                    }
                    if (qtyToDeliver.signum() == 0) {
                        break;
                    }
                }
                if (qtyToDeliver.signum() != 0) {
                    final MAttributeSetInstance asi2 = MAttributeSetInstance.create(line.getCtx(), product, line.get_TrxName());
                    createMA(line, asi2.getM_AttributeSetInstance_ID(), qtyToDeliver);
                }
            }
        }
        else if (!incomingTrx) {
            createMA(line, line.getM_AttributeSetInstance_ID(), line.getMovementQty());
        }
        save(line);
    }
    
    private static String getTableNameMA(final IDocumentLine model) {
        return String.valueOf(model.get_TableName()) + "MA";
    }
    
    private static int deleteMA(final IDocumentLine model) {
        final String sql = "DELETE FROM " + getTableNameMA(model) + " WHERE " + model.get_TableName() + "_ID=?";
        final int no = DB.executeUpdateEx(sql, new Object[] { model.get_ID() }, model.get_TrxName());
        if (no > 0) {
            StorageEngine.log.config("Delete old #" + no);
        }
        return no;
    }
    
    private static void saveMA(final IInventoryAllocation ma) {
        ((PO)ma).saveEx();
    }
    
    private static void save(final IDocumentLine line) {
        ((PO)line).saveEx(line.get_TrxName());
    }
    
    private static void create(final IDocumentLine model, final String MovementType, final Timestamp MovementDate, final int M_AttributeSetInstance_ID, final BigDecimal Qty) {
        final MTransaction mtrx = new MTransaction(model.getCtx(), model.getAD_Org_ID(), MovementType, model.getM_Locator_ID(), model.getM_Product_ID(), M_AttributeSetInstance_ID, Qty, MovementDate, model.get_TrxName());
        setReferenceLine_ID((PO)mtrx, model);
        mtrx.saveEx(model.get_TrxName());
        CostEngineFactory.getCostEngine(model.getAD_Client_ID()).createCostDetail(model, mtrx);
    }
    
    private static IInventoryAllocation createMA(final IDocumentLine model, final int M_AttributeSetInstance_ID, final BigDecimal MovementQty) {
        final Properties ctx = model.getCtx();
        final String tableName = getTableNameMA(model);
        final String trxName = model.get_TrxName();
        final IInventoryAllocation ma = (IInventoryAllocation)MTable.get(ctx, tableName).getPO(0, trxName);
        ma.setAD_Org_ID(model.getAD_Org_ID());
        setReferenceLine_ID((PO)ma, model);
        ma.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
        ma.setMovementQty(MovementQty);
        saveMA(ma);
        StorageEngine.log.fine("##: " + ma);
        return ma;
    }
    
    private static IInventoryAllocation[] getMA(final IDocumentLine model) {
        final Properties ctx = model.getCtx();
        final String IDColumnName = String.valueOf(model.get_TableName()) + "_ID";
        final String tableName = getTableNameMA(model);
        final String trxName = model.get_TrxName();
        final String whereClause = String.valueOf(IDColumnName) + "=?";
        final List<PO> list = (List<PO>)new Query(ctx, tableName, whereClause, trxName).setParameters(new Object[] { model.get_ID() }).setOrderBy(IDColumnName).list();
        final IInventoryAllocation[] arr = new IInventoryAllocation[list.size()];
        return list.toArray(arr);
    }
    
    private static void setReferenceLine_ID(final PO model, final IDocumentLine ref) {
        final String refColumnName = String.valueOf(ref.get_TableName()) + "_ID";
        if (model.get_ColumnIndex(refColumnName) < 0) {
            throw new AdempiereException("Invalid inventory document line " + ref);
        }
        model.set_ValueOfColumn(refColumnName, (Object)ref.get_ID());
    }
    
    public static int getM_Locator_ID(final Properties ctx, final int M_Warehouse_ID, final int M_Product_ID, final int M_AttributeSetInstance_ID, final BigDecimal Qty, final String trxName) {
        int M_Locator_ID = MStorageOnHand.getM_Locator_ID(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, Qty, trxName);
        if (M_Locator_ID == 0) {
            final MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID);
            M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
        }
        return M_Locator_ID;
    }
}
