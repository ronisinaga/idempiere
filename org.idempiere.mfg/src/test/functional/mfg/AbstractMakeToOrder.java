// 
// Decompiled by Procyon v0.5.36
// 

package test.functional.mfg;

import org.compiere.model.MOrder;
import org.eevolution.model.I_PP_Order;
import org.libero.model.MPPOrder;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;
import org.compiere.model.MOrderLine;
import java.math.BigDecimal;
import org.compiere.wf.MWorkflow;
import org.compiere.model.MBPartner;
import org.compiere.model.MProduct;
import org.eevolution.model.MPPProductBOM;
import java.sql.Timestamp;

public class AbstractMakeToOrder extends AdempiereTestCase
{
    String trxName;
    int M_Product_ID;
    int C_BPartner_ID;
    int AD_Org_ID;
    int AD_User_ID;
    int M_Warehouse_ID;
    int PP_Product_BOM_ID;
    int AD_Workflow_ID;
    int S_Resource_ID;
    int C_DocType_ID;
    Timestamp today;
    Timestamp promisedDeta;
    MPPProductBOM bom;
    MProduct product;
    MBPartner BPartner;
    MWorkflow workflow;
    BigDecimal Qty;
    MOrderLine oline;
    
    public AbstractMakeToOrder() {
        this.trxName = this.getTrxName();
        this.M_Product_ID = 145;
        this.C_BPartner_ID = 120;
        this.AD_Org_ID = 50000;
        this.AD_User_ID = 101;
        this.M_Warehouse_ID = 50001;
        this.PP_Product_BOM_ID = 145;
        this.AD_Workflow_ID = 50018;
        this.S_Resource_ID = 50005;
        this.C_DocType_ID = 132;
        this.today = TimeUtil.trunc(new Timestamp(System.currentTimeMillis()), "DD/MM/YYYY");
        this.promisedDeta = TimeUtil.addDays(this.today, 10);
        this.bom = null;
        this.product = null;
        this.BPartner = null;
        this.workflow = null;
        this.Qty = Env.ZERO;
        this.oline = null;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void test01() throws Exception {
        this.Qty = new BigDecimal(10);
        this.product = MProduct.get(this.getCtx(), this.M_Product_ID);
        this.BPartner = new MBPartner(this.getCtx(), this.C_BPartner_ID, this.trxName);
        final int PP_Product_BOM_ID = MPPProductBOM.getBOMSearchKey(this.product);
        if (PP_Product_BOM_ID <= 0) {
            throw new AdempiereException("@NotFound@ @PP_ProductBOM_ID@");
        }
        this.bom = new MPPProductBOM(this.getCtx(), PP_Product_BOM_ID, this.trxName);
        if (this.bom != null) {
            this.bom.setBOMType("O");
            this.bom.setBOMUse("M");
            this.bom.saveEx();
        }
        (this.workflow = new MWorkflow(this.getCtx(), this.AD_Workflow_ID, this.trxName)).setValue(this.product.getValue());
        this.workflow.saveEx();
        if (this.AD_Workflow_ID <= 0) {
            throw new AdempiereException("@NotFound@ @AD_Workflow_ID@");
        }
        this.workflow = MWorkflow.get(this.getCtx(), this.AD_Workflow_ID);
        this.createOrder();
        final MPPOrder expected = this.createPPOrder();
        final I_PP_Order actual = (I_PP_Order)MPPOrder.forC_OrderLine_ID(this.getCtx(), this.oline.get_ID(), this.trxName);
        if (actual == null) {
            throw new AdempiereException("@NotFound@ @PP_Order_ID@ not was generate");
        }
        this.assertEquals("Confirming Manufacturing Order", (I_PP_Order)expected, actual);
    }
    
    public MOrder createOrder() {
        final MOrder order = new MOrder(this.getCtx(), 0, this.trxName);
        order.setAD_Org_ID(this.AD_Org_ID);
        order.setDateOrdered(this.today);
        order.setDatePromised(this.promisedDeta);
        order.setIsSOTrx(true);
        order.setC_DocTypeTarget_ID(this.C_DocType_ID);
        order.setC_BPartner_ID(this.C_BPartner_ID);
        order.setAD_User_ID(this.AD_User_ID);
        order.setM_Warehouse_ID(this.M_Warehouse_ID);
        order.setDocStatus("IP");
        order.setDocAction("CO");
        order.saveEx();
        (this.oline = new MOrderLine(order)).setM_Product_ID(this.product.get_ID());
        this.oline.setQty(new BigDecimal(10));
        this.oline.saveEx();
        order.processIt("CO");
        return order;
    }
    
    public MPPOrder createPPOrder() {
        final MPPOrder expected = new MPPOrder(this.getCtx(), 0, this.trxName);
        expected.setAD_Org_ID(this.AD_Org_ID);
        expected.setM_Product_ID(this.product.getM_Product_ID());
        expected.setDateOrdered(this.today);
        expected.setDatePromised(this.promisedDeta);
        expected.setDateFinish(this.promisedDeta);
        expected.setPP_Product_BOM_ID(this.PP_Product_BOM_ID);
        expected.setAD_Workflow_ID(this.AD_Workflow_ID);
        expected.setS_Resource_ID(this.S_Resource_ID);
        expected.setM_Warehouse_ID(this.M_Warehouse_ID);
        expected.setDocStatus("IP");
        expected.setQty(this.Qty);
        return expected;
    }
    
    public void assertEquals(final String message, final I_PP_Order expected, final I_PP_Order actual) throws Exception {
        final boolean equals = expected.getAD_Client_ID() == actual.getAD_Client_ID() && expected.getAD_Org_ID() == actual.getAD_Org_ID() && expected.getM_Warehouse_ID() == actual.getM_Warehouse_ID() && expected.getM_Product_ID() == actual.getM_Product_ID() && expected.getQtyOrdered().equals(actual.getQtyOrdered()) && expected.getDocStatus().equals(actual.getDocStatus()) && expected.getDatePromised().equals(actual.getDatePromised()) && expected.getDateOrdered().equals(actual.getDateOrdered());
        final StringBuffer sb = new StringBuffer(message).append(": expected=" + expected).append(", actual=" + actual);
        assertTrue(sb.toString(), equals);
    }
}
