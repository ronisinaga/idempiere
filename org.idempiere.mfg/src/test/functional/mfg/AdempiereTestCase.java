// 
// Decompiled by Procyon v0.5.36
// 

package test.functional.mfg;

import org.compiere.util.CLogMgt;
import org.compiere.Adempiere;
import org.compiere.util.Ini;
import org.compiere.util.Env;
import java.io.InputStream;
import java.io.FileInputStream;
import java.awt.Component;
import javax.swing.JFileChooser;
import java.io.File;
import org.compiere.util.Trx;
import java.util.Random;
import org.compiere.util.CLogger;
import java.util.logging.Level;
import java.util.Properties;
import junit.framework.TestCase;

public class AdempiereTestCase extends TestCase
{
    protected Properties testProperties;
    protected String testPropertiesFileName;
    private Properties m_Ctx;
    public final String fileName_Key = "AdempiereProperties";
    private String fileName_DefaultValue;
    private String fileName_Value;
    public final String isClient_Key = "isClient";
    private String isClient_DefaultValue;
    private boolean isClient_Value;
    public final String AD_User_ID_Key = "AD_User_ID";
    private String AD_User_ID_DefaultValue;
    private int AD_User_ID_Value;
    public final String AD_Client_ID_Key = "AD_Client_ID";
    private String AD_Client_ID_DefaultValue;
    private int AD_Client_ID_Value;
    public final String LogLevel_Key = "LogLevel";
    private String LogLevel_DefaultValue;
    private Level LogLevel_Value;
    protected final CLogger log;
    private String trxName;
    private Random m_randGenerator;
    
    public AdempiereTestCase() {
        this.testProperties = null;
        this.testPropertiesFileName = "test.properties";
        this.m_Ctx = null;
        this.fileName_DefaultValue = "idempiere.properties";
        this.fileName_Value = "";
        this.isClient_DefaultValue = "Y";
        this.isClient_Value = true;
        this.AD_User_ID_DefaultValue = "100";
        this.AD_User_ID_Value = 0;
        this.AD_Client_ID_DefaultValue = "11";
        this.AD_Client_ID_Value = 11;
        this.LogLevel_DefaultValue = Level.FINEST.toString();
        this.LogLevel_Value = Level.FINEST;
        this.log = CLogger.getCLogger((Class)this.getClass());
        this.trxName = Trx.createTrxName(String.valueOf(this.getClass().getName()) + "_");
        this.m_randGenerator = new Random(System.currentTimeMillis());
    }
    
    public Properties getCtx() {
        return this.m_Ctx;
    }
    
    public String getTrxName() {
        return this.trxName;
    }
    
    public int getAD_Client_ID() {
        return this.AD_Client_ID_Value;
    }
    
    public int getAD_User_ID() {
        return this.AD_User_ID_Value;
    }
    
    public boolean isClient() {
        return this.isClient_Value;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.testProperties = new Properties();
        File file = new File(this.testPropertiesFileName);
        if (!file.isFile()) {
            this.log.warning("File not found - " + file.getAbsolutePath());
            final JFileChooser chooser = new JFileChooser();
            final int returnVal = chooser.showOpenDialog(null);
            if (returnVal == 0) {
                file = chooser.getSelectedFile();
            }
            else {
                file = null;
            }
        }
        this.testProperties.load(new FileInputStream(file));
        this.fileName_Value = this.testProperties.getProperty("AdempiereProperties", this.fileName_DefaultValue);
        this.isClient_Value = "Y".equals(this.testProperties.getProperty("isClient", this.isClient_DefaultValue));
        this.AD_User_ID_Value = Integer.parseInt(this.testProperties.getProperty("AD_User_ID", this.AD_User_ID_DefaultValue));
        this.AD_Client_ID_Value = Integer.parseInt(this.testProperties.getProperty("AD_Client_ID", this.AD_Client_ID_DefaultValue));
        try {
            this.LogLevel_Value = Level.parse(this.testProperties.getProperty("LogLevel", this.LogLevel_DefaultValue));
        }
        catch (Exception ex) {}
        (this.m_Ctx = Env.getCtx()).setProperty("#AD_User_ID", new Integer(this.AD_User_ID_Value).toString());
        this.m_Ctx.setProperty("#AD_Client_ID", new Integer(this.AD_Client_ID_Value).toString());
        if (this.fileName_Value.length() < 1) {
            assertEquals("Please specify path to idempiere.properties file!", true, false);
        }
        System.setProperty("PropertyFile", this.fileName_Value);
        Ini.setClient(this.isClient_Value);
        Adempiere.startup(this.isClient_Value);
        CLogMgt.setLevel(this.LogLevel_Value);
    }
    
    protected void commit() throws Exception {
        Trx trx = null;
        if (this.trxName != null) {
            trx = Trx.get(this.trxName, false);
        }
        if (trx != null && trx.isActive()) {
            trx.commit(true);
        }
    }
    
    protected void rollback() {
        Trx trx = null;
        if (this.trxName != null) {
            trx = Trx.get(this.trxName, false);
        }
        if (trx != null && trx.isActive()) {
            trx.rollback();
        }
    }
    
    protected void close() {
        Trx trx = null;
        if (this.trxName != null) {
            trx = Trx.get(this.trxName, false);
        }
        if (trx != null) {
            trx.close();
        }
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        Trx trx = null;
        if (this.trxName != null) {
            trx = Trx.get(this.trxName, false);
        }
        if (trx != null && trx.isActive()) {
            trx.rollback();
        }
        if (trx != null) {
            trx.close();
        }
        trx = null;
        this.testProperties = null;
        this.m_Ctx = null;
    }
    
    public int randomInt(final int max) {
        return this.m_randGenerator.nextInt(max);
    }
    
    public void assertExceptionThrown(final String message, final Class<? extends Exception> exceptionType, final Runnable runnable) throws Exception {
        Exception ex = null;
        try {
            runnable.run();
        }
        catch (Exception e) {
            ex = e;
        }
        assertNotNull("No exception was throwed : " + message, (Object)ex);
        if (exceptionType != null && !exceptionType.isAssignableFrom(ex.getClass())) {
            throw ex;
        }
    }
}
