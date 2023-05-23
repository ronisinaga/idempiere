// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.model.wrapper;

import org.compiere.model.MAttachment;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.PO;

public abstract class AbstractPOWrapper
{
    protected PO po;
    
    protected abstract PO receivePO(final Properties p0, final int p1, final String p2, final String p3);
    
    public AbstractPOWrapper(final Properties ctx, final int id, final String trxName, final String type) {
        this.po = this.receivePO(ctx, id, trxName, type);
    }
    
    public PO get() {
        return this.po;
    }
    
    @Override
    public String toString() {
        return this.po.toString();
    }
    
    @Override
    public boolean equals(final Object cmp) {
        return this.po.equals(cmp);
    }
    
    public int compare(final Object o1, final Object o2) {
        return this.po.compare(o1, o2);
    }
    
    public String get_TableName() {
        return this.po.get_TableName();
    }
    
    public int getID() {
        return this.po.get_ID();
    }
    
    public int getIDOld() {
        return this.po.get_IDOld();
    }
    
    public Properties getCtx() {
        return this.po.getCtx();
    }
    
    public Object get_Value(final int index) {
        return this.po.get_Value(index);
    }
    
    public Object get_Value(final String columnName) {
        return this.po.get_Value(columnName);
    }
    
    public String get_ValueAsString(final String variableName) {
        return this.po.get_ValueAsString(variableName);
    }
    
    public Object get_ValueOfColumn(final int AD_Column_ID) {
        return this.po.get_ValueOfColumn(AD_Column_ID);
    }
    
    public Object get_ValueOld(final int index) {
        return this.po.get_ValueOld(index);
    }
    
    public Object get_ValueOld(final String columnName) {
        return this.po.get_ValueOld(columnName);
    }
    
    public boolean is_ValueChanged(final int index) {
        return this.po.is_ValueChanged(index);
    }
    
    public boolean is_ValueChanged(final String columnName) {
        return this.po.is_ValueChanged(columnName);
    }
    
    public Object get_ValueDifference(final int index) {
        return this.po.get_ValueDifference(index);
    }
    
    public Object get_ValueDifference(final String columnName) {
        return this.po.get_ValueDifference(columnName);
    }
    
    public void set_ValueOfColumn(final int AD_Column_ID, final Object value) {
        this.po.set_ValueOfColumn(AD_Column_ID, value);
    }
    
    public void set_CustomColumn(final String columnName, final Object value) {
        this.po.set_CustomColumn(columnName, value);
    }
    
    public int get_ColumnIndex(final String columnName) {
        return this.po.get_ColumnIndex(columnName);
    }
    
    public boolean load(final String trxName) {
        return this.po.load(trxName, new String[0]);
    }
    
    public int getAD_Client_ID() {
        return this.po.getAD_Client_ID();
    }
    
    public int getAD_Org_ID() {
        return this.po.getAD_Org_ID();
    }
    
    public void setIsActive(final boolean active) {
        this.po.setIsActive(active);
    }
    
    public boolean isActive() {
        return this.po.isActive();
    }
    
    public Timestamp getCreated() {
        return this.po.getCreated();
    }
    
    public Timestamp getUpdated() {
        return this.po.getUpdated();
    }
    
    public int getCreatedBy() {
        return this.po.getCreatedBy();
    }
    
    public int getUpdatedBy() {
        return this.po.getUpdatedBy();
    }
    
    public boolean save() {
        return this.po.save();
    }
    
    public boolean save(final String trxName) {
        return this.po.save(trxName);
    }
    
    public boolean is_Changed() {
        return this.po.is_Changed();
    }
    
    public String get_WhereClause(final boolean withValues) {
        return this.po.get_WhereClause(withValues);
    }
    
    public boolean delete(final boolean force) {
        return this.po.delete(force);
    }
    
    public boolean delete(final boolean force, final String trxName) {
        return this.po.delete(force, trxName);
    }
    
    public boolean lock() {
        return this.po.lock();
    }
    
    public boolean unlock(final String trxName) {
        return this.po.unlock(trxName);
    }
    
    public void set_TrxName(final String trxName) {
        this.po.set_TrxName(trxName);
    }
    
    public String get_TrxName() {
        return this.po.get_TrxName();
    }
    
    public MAttachment getAttachment() {
        return this.po.getAttachment();
    }
    
    public MAttachment getAttachment(final boolean requery) {
        return this.po.getAttachment(requery);
    }
    
    public MAttachment createAttachment() {
        return this.po.createAttachment();
    }
    
    public boolean isAttachment(final String extension) {
        return this.po.isAttachment(extension);
    }
    
    public byte[] getAttachmentData(final String extension) {
        return this.po.getAttachmentData(extension);
    }
    
    public boolean isPdfAttachment() {
        return this.po.isPdfAttachment();
    }
    
    public byte[] getPdfAttachment() {
        return this.po.getPdfAttachment();
    }
    
    public void dump() {
        this.po.dump();
    }
    
    public void dump(final int index) {
        this.po.dump(index);
    }
}
