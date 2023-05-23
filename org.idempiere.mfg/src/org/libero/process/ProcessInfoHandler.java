// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.process;

import java.util.Enumeration;
import org.compiere.model.MPInstancePara;
import org.compiere.util.Msg;
import org.compiere.util.Env;
import org.compiere.model.MProcess;
import java.util.Hashtable;
import org.compiere.model.MPInstance;
import org.compiere.process.ProcessInfo;

public class ProcessInfoHandler
{
    protected ProcessInfo pi;
    protected MPInstance pinstance;
    protected Hashtable param;
    protected MProcess process;
    
    public ProcessInfoHandler(final int processID) {
        this.init(processID);
    }
    
    private void init(final int processID) {
        this.process = new MProcess(Env.getCtx(), processID, (String)null);
        if (this.process != null) {
            this.pi = this.getProcessInfo(Msg.translate(Env.getCtx(), this.process.getName()), this.process.get_ID());
            this.pinstance = this.getProcessInstance(this.pi);
            this.pi.setAD_PInstance_ID(this.pinstance.getAD_PInstance_ID());
        }
    }
    
    protected ProcessInfo getProcessInfo(final String name, final int id) {
        final ProcessInfo info = new ProcessInfo(name, id);
        info.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
        info.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
        return info;
    }
    
    protected MPInstance getProcessInstance(final ProcessInfo info) {
        final MPInstance instance = new MPInstance(Env.getCtx(), info.getAD_Process_ID(), info.getRecord_ID());
        if (!instance.save()) {
            info.setSummary(Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
            info.setError(true);
            return null;
        }
        return instance;
    }
    
    protected int countParams() {
        return (this.process != null) ? this.process.getParameters().length : 0;
    }
    
    protected Hashtable extractParameters() {
        final Hashtable param = new Hashtable();
        MPInstancePara p = null;
        for (int i = 0, b = this.countParams(); i < b; ++i) {
            p = new MPInstancePara(this.getProcessInstance(), i);
            p.load((String)null, new String[0]);
            param.put(p.getParameterName(), this.getValueFrom(p));
        }
        return param;
    }
    
    protected Object getValueFrom(final MPInstancePara p) {
        Object o = null;
        o = ((o == null) ? p.getP_Date() : o);
        o = ((o == null) ? p.getP_Date_To() : o);
        o = ((o == null) ? p.getP_Number() : o);
        o = ((o == null) ? p.getP_Number_To() : o);
        o = ((o == null) ? p.getP_String() : o);
        o = ((o == null) ? p.getP_String_To() : o);
        return o;
    }
    
    public void setProcessError() {
        this.pi.setSummary(Msg.getMsg(Env.getCtx(), "ProcessCancelled"));
        this.pi.setError(true);
    }
    
    public MPInstance getProcessInstance() {
        return this.pinstance;
    }
    
    public ProcessInfo getProcessInfo() {
        return this.pi;
    }
    
    public Object getParameterValue(final String param) {
        if (this.param == null) {
            this.param = this.extractParameters();
        }
        return this.param.get(param);
    }
    
    public Enumeration getParameters() {
        if (this.param == null) {
            this.param = this.extractParameters();
        }
        return this.param.keys();
    }
}
