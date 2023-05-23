package org.astina.mfg.plugin.factory;

import java.util.logging.Level;
import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.util.CLogger;

/**
 * @author Alex
 *
 */
public class AstinaMfgFormFactory implements IFormFactory {

	private static final CLogger log = CLogger.getCLogger(AstinaMfgFormFactory.class); 
			
	/* (non-Javadoc)
	 * @see org.adempiere.webui.factory.IFormFactory#newFormInstance(java.lang.String)
	 */
	@Override
	public ADForm newFormInstance(String formName) {
		
		if(formName.startsWith("org.astina.mfg.plugin.form"))
		{
			Object form = null;
			Class<?> clazz = null;
			ClassLoader loader = getClass().getClassLoader();
			try {
				clazz = loader.loadClass(formName);
			}catch (Exception e) {
			    if (log.isLoggable(Level.INFO))
				       log.log(Level.INFO, e.getLocalizedMessage(), e);
			            return null;
			}
			if(clazz != null)
			{
				try {
					form = clazz.getDeclaredConstructor().newInstance();;
				}catch(Exception e) {
					if (log.isLoggable(Level.INFO))
				       log.log(Level.INFO, e.getLocalizedMessage(), e);
			            return null;
				}
			}
			if (form != null)
			{
				if (form instanceof ADForm) {
					return (ADForm) form;
				} else if (form instanceof IFormController) {
					IFormController controller = (IFormController) form;
					ADForm adForm = controller.getForm();
					adForm.setICustomForm(controller);
					return adForm;
				}
			}
		}
		return null;
	}
}