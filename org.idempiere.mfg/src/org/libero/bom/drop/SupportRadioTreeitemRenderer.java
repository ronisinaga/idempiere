// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.bom.drop;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Radiogroup;
import java.util.UUID;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Treeitem;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Space;
import org.zkoss.util.Utils;
import org.zkoss.zul.Treecell;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.TreeitemRenderer;

public class SupportRadioTreeitemRenderer implements TreeitemRenderer<ISupportRadioNode>, EventListener<Event>
{
    public static final String PROPERTIE_NAME_RADIO_GROUP = "gp_name";
    public static final String DATA_ITEM = "REF_DATA_MODEL";
    public static final String TREE_ITEM = "REF_TREE_ITEM";
    private Boolean needFixIndent;
    private Map<String, String> mGroupID;
    private EventListener<Event> listenerSelection;
    private IRendererListener rendererListener;
    public boolean isOpen;
    
    public SupportRadioTreeitemRenderer() {
        this.needFixIndent = null;
        this.mGroupID = new HashMap<String, String>();
        this.isOpen = false;
    }
    
    public void setCheckedListener(final EventListener<Event> listenerSelection) {
        this.listenerSelection = listenerSelection;
    }
    
    public void setRendererListener(final IRendererListener rendererListener) {
        this.rendererListener = rendererListener;
    }
    
    protected void fixIndent(final ISupportRadioNode data, final Treecell cell, final boolean firstLevel) {
        if (this.needFixIndent == null) {
            final int[] currentVersion = Utils.parseVersion("9.6.3");
            final int[] correctVersion = Utils.parseVersion("8.0.0");
            this.needFixIndent = (Utils.compareVersion(currentVersion, correctVersion) < 0);
        }
        if (!firstLevel && data.isLeaf() && this.needFixIndent) {
            cell.appendChild((Component)new Space());
            cell.appendChild((Component)new Space());
        }
    }
    
    public void render(final Treeitem item, final ISupportRadioNode data, final int index) throws Exception {
        final Treerow row = new Treerow();
        final Treecell cell = new Treecell();
        cell.setSpan(2);
        item.appendChild((Component)row);
        row.appendChild((Component)cell);
        item.setAttribute("REF_DATA_MODEL", (Object)data);
        Checkbox selectionCtr = null;
        if (data.isRadio()) {
            final Radio radioCtr = new Radio();
            Component groupContainer = null;
            if (item.getParentItem() != null) {
                groupContainer = item.getParentItem().getTreerow().getFirstChild();
            }
            else {
                groupContainer = item.getTree().getParent();
            }
            final String uniqueGroupName = String.valueOf(groupContainer.hashCode()) + data.getGroupName();
            String groupId = this.mGroupID.get(uniqueGroupName);
            if (groupId == null) {
                final UUID groupUUID = UUID.randomUUID();
                groupId = groupUUID.toString();
                this.mGroupID.put(uniqueGroupName, groupId);
                data.setIsChecked(true);
            }
            Component radioGroup = groupContainer.getFellowIfAny(groupId);
            if (radioGroup == null) {
                radioGroup = (Component)new Radiogroup();
                radioGroup.setId(groupId);
                groupContainer.appendChild(radioGroup);
            }
            radioCtr.setRadiogroup((Radiogroup)radioGroup);
            selectionCtr = (Checkbox)radioCtr;
        }
        else {
            selectionCtr = new Checkbox();
        }
        if (this.rendererListener != null) {
            this.rendererListener.render(item, row, data, index);
        }
        selectionCtr.setAttribute("REF_DATA_MODEL", (Object)data);
        selectionCtr.setAttribute("REF_TREE_ITEM", (Object)item);
        selectionCtr.setLabel(data.getLabel());
        this.fixIndent(data, cell, item.getParentItem() == null);
        cell.appendChild((Component)selectionCtr);
        selectionCtr.setDisabled(data.isDisable());
        selectionCtr.setChecked(data.isChecked());
        selectionCtr.addEventListener("onCheck", (EventListener)this);
        item.setOpen(this.isOpen);
    }
    
    public void onEvent(final Event event) throws Exception {
        this.defaultHandleEvent(event);
        if (this.listenerSelection != null) {
            this.listenerSelection.onEvent(event);
        }
    }
    
    public void defaultHandleEvent(final Event event) throws Exception {
        final Object targetObj = event.getTarget();
        if (!(targetObj instanceof Checkbox)) {
            return;
        }
        final Checkbox chkBox = (Checkbox)targetObj;
        final ISupportRadioNode dataItem = (ISupportRadioNode)chkBox.getAttribute("REF_DATA_MODEL");
        dataItem.setIsChecked(chkBox.isChecked());
        final Treeitem curentTreeItem = (Treeitem)chkBox.getAttribute("REF_TREE_ITEM");
        if (this.rendererListener != null) {
            this.rendererListener.onchecked(curentTreeItem, dataItem, true);
        }
        if (targetObj instanceof Radio) {
            for (Treeitem nextSiblingTreeItem = (Treeitem)curentTreeItem.getNextSibling(); nextSiblingTreeItem != null; nextSiblingTreeItem = (Treeitem)nextSiblingTreeItem.getNextSibling()) {
                final ISupportRadioNode dataNodeNext = (ISupportRadioNode)nextSiblingTreeItem.getAttribute("REF_DATA_MODEL");
                if (dataNodeNext.getGroupName().equals(dataItem.getGroupName())) {
                    dataNodeNext.setIsChecked(false);
                    if (this.rendererListener != null) {
                        this.rendererListener.onchecked(nextSiblingTreeItem, dataNodeNext, false);
                    }
                }
            }
            for (Treeitem prevSiblingTreeItem = (Treeitem)curentTreeItem.getPreviousSibling(); prevSiblingTreeItem != null; prevSiblingTreeItem = (Treeitem)prevSiblingTreeItem.getPreviousSibling()) {
                final ISupportRadioNode dataNodePrev = (ISupportRadioNode)prevSiblingTreeItem.getAttribute("REF_DATA_MODEL");
                if (dataNodePrev.getGroupName().equals(dataItem.getGroupName())) {
                    dataNodePrev.setIsChecked(false);
                    if (this.rendererListener != null) {
                        this.rendererListener.onchecked(prevSiblingTreeItem, dataNodePrev, false);
                    }
                }
            }
        }
    }
}
