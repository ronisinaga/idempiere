// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model;

import java.math.BigDecimal;

public class TampungDataBom
{
    private int productparent;
    private BigDecimal qtyrequired;
    private int lowlwvel;
    
    public int getProductparent() {
        return this.productparent;
    }
    
    public void setProductparent(final int productparent) {
        this.productparent = productparent;
    }
    
    public BigDecimal getQtyrequired() {
        return this.qtyrequired;
    }
    
    public void setQtyrequired(final BigDecimal qtyrequired) {
        this.qtyrequired = qtyrequired;
    }
    
    public int getLowlwvel() {
        return this.lowlwvel;
    }
    
    public void setLowlwvel(final int lowlwvel) {
        this.lowlwvel = lowlwvel;
    }
}
