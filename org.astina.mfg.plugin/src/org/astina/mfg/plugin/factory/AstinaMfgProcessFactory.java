package org.astina.mfg.plugin.factory;

import org.adempiere.base.IProcessFactory;
import org.astina.mfg.plugin.process.A_Depreciation_Workfile_Build;
import org.astina.mfg.plugin.process.AssetTransfer;
import org.astina.mfg.plugin.process.BankStatementGenPayment;
import org.astina.mfg.plugin.process.CopyOrderMfg;
import org.compiere.process.ProcessCall;

public class AstinaMfgProcessFactory implements IProcessFactory{

	@Override
	public ProcessCall newProcessInstance(String className) {
		
		if (className.equalsIgnoreCase("org.astina.mfg.plugin.process.BankStatementGenPayment"))
			 return new BankStatementGenPayment();
		
		if (className.equalsIgnoreCase("org.astina.mfg.plugin.process.AssetTransfer"))
			 return new AssetTransfer();
		
		if (className.equalsIgnoreCase("org.astina.mfg.plugin.process.A_Depreciation_Workfile_Build"))
			 return new A_Depreciation_Workfile_Build();
		
		if (className.equalsIgnoreCase("org.astina.mfg.plugin.process.CopyOrderMfg"))
			 return new CopyOrderMfg();
		
		return null;
	}

}
