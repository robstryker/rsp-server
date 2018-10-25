package org.jboss.tools.rsp.server.felix.servertype.impl;

import org.jboss.tools.rsp.api.ServerManagementAPIConstants;
import org.jboss.tools.rsp.eclipse.core.runtime.IPath;
import org.jboss.tools.rsp.eclipse.core.runtime.Path;
import org.jboss.tools.rsp.eclipse.jdt.launching.IVMInstallRegistry;
import org.jboss.tools.rsp.server.LauncherSingleton;
import org.jboss.tools.rsp.server.spi.launchers.AbstractJavaLauncher;
import org.jboss.tools.rsp.server.spi.launchers.IStartLauncher;
import org.jboss.tools.rsp.server.spi.servertype.IServerDelegate;

public class FelixStartLauncher extends AbstractJavaLauncher implements IStartLauncher {

	public FelixStartLauncher(IServerDelegate serverDelegate) {
		super(serverDelegate);
	}

	@Override
	protected IVMInstallRegistry getDefaultRegistry() {
		return LauncherSingleton.getDefault().getLauncher().getModel().getVMInstallModel();
	}

	@Override
	protected String getWorkingDirectory() {
		String serverHome = getDelegate().getServer().getAttribute(
				ServerManagementAPIConstants.SERVER_HOME_FILE, (String) null);
		return serverHome;
	}

	@Override
	protected String getMainTypeName() {
		return "org.apache.felix.main.Main";
	}

	@Override
	protected String getVMArguments() {
		return "";
	}

	@Override
	protected String getProgramArguments() {
		return "";
	}

	@Override
	protected String[] getClasspath() {
		String serverHome = getDelegate().getServer().getAttribute(
				ServerManagementAPIConstants.SERVER_HOME_DIR, (String) null);
		IPath home = new Path(serverHome).append("bin").append("felix.jar");
		return new String[] { home.toOSString() };
	}

}
