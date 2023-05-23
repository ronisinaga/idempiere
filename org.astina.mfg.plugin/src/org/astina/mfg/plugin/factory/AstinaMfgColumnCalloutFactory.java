package org.astina.mfg.plugin.factory;

import org.adempiere.base.AnnotationBasedColumnCalloutFactory;
import org.adempiere.base.IColumnCalloutFactory;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = IColumnCalloutFactory.class)
public class AstinaMfgColumnCalloutFactory extends AnnotationBasedColumnCalloutFactory {

	public AstinaMfgColumnCalloutFactory() {
	}
	
	@Override
	protected String[] getPackages() {
		return new String[] {"org.astina.mfg.plugin.callout","org.astina.mfg.plugin.model"};
	}
}
