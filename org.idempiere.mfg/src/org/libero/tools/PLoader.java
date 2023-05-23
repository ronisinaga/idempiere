// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tools;

import java.io.InputStream;
import java.util.Properties;

public class PLoader
{
    protected Properties properties;
    
    public PLoader(final String properties) {
        this.init(this.getClass(), properties);
    }
    
    public PLoader(final Class clazz, final String properties) {
        this.init(clazz, properties);
    }
    
    protected void init(final Class clazz, final String name) {
        this.properties = new Properties();
        final InputStream is = clazz.getResourceAsStream(name);
        try {
            if (is != null) {
                this.properties.load(is);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
            return;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public Properties getProperties() {
        return this.properties;
    }
}
