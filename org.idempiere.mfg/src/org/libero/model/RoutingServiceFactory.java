// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import org.compiere.util.Env;
import java.util.Properties;
import org.adempiere.exceptions.AdempiereException;
import java.util.HashMap;

public class RoutingServiceFactory
{
    public static final String DEFAULT_ServiceName = "org.libero.model.impl.DefaultRoutingServiceImpl";
    public static RoutingServiceFactory s_instance;
    private static final HashMap<Integer, String> s_serviceClassnames;
    private static final HashMap<Integer, RoutingService> s_services;
    
    static {
        RoutingServiceFactory.s_instance = null;
        s_serviceClassnames = new HashMap<Integer, String>(5);
        s_services = new HashMap<Integer, RoutingService>(5);
    }
    
    public static RoutingServiceFactory get() {
        if (RoutingServiceFactory.s_instance == null) {
            RoutingServiceFactory.s_instance = new RoutingServiceFactory();
        }
        return RoutingServiceFactory.s_instance;
    }
    
    public static void registerServiceClassname(final int AD_Client_ID, final String serviceClassname) {
        RoutingServiceFactory.s_serviceClassnames.put((AD_Client_ID > 0) ? AD_Client_ID : 0, serviceClassname);
    }
    
    private RoutingServiceFactory() {
    }
    
    private final String getRoutingServiceClassname(final int AD_Client_ID) {
        String classname = RoutingServiceFactory.s_serviceClassnames.get(AD_Client_ID);
        if (classname == null && AD_Client_ID != 0) {
            classname = RoutingServiceFactory.s_serviceClassnames.get(0);
        }
        if (classname == null) {
            classname = "org.libero.model.impl.DefaultRoutingServiceImpl";
        }
        return classname;
    }
    
    public RoutingService getRoutingService(final int AD_Client_ID) {
        RoutingService service = RoutingServiceFactory.s_services.get(AD_Client_ID);
        if (service != null) {
            return service;
        }
        final String classname = this.getRoutingServiceClassname(AD_Client_ID);
        try {
            final Class<? extends RoutingService> cl = (Class<? extends RoutingService>)this.getClass().getClassLoader().loadClass(classname);
            service = (RoutingService)cl.newInstance();
            RoutingServiceFactory.s_services.put(AD_Client_ID, service);
        }
        catch (Exception e) {
            throw new AdempiereException((Throwable)e);
        }
        return service;
    }
    
    public RoutingService getRoutingService(final Properties ctx) {
        return this.getRoutingService(Env.getAD_Client_ID(ctx));
    }
    
    public RoutingService getRoutingService() {
        return this.getRoutingService(Env.getCtx());
    }
}
