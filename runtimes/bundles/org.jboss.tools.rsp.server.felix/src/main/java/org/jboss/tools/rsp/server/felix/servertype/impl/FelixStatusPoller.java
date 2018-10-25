package org.jboss.tools.rsp.server.felix.servertype.impl;

import org.jboss.tools.rsp.eclipse.debug.core.ILaunch;
import org.jboss.tools.rsp.server.spi.model.polling.AbstractPoller;
import org.jboss.tools.rsp.server.spi.servertype.IServer;

public class FelixStatusPoller extends AbstractPoller {

	private FelixServerDelegate felixServerDelegate;

	public FelixStatusPoller(FelixServerDelegate felixServerDelegate) {
		this.felixServerDelegate = felixServerDelegate;
	}

	@Override
	protected String getThreadName() {
		return "Apache Felix Poller";
	}

	@Override
	protected SERVER_STATE onePing(IServer server) {
		ILaunch[] all = felixServerDelegate.getLaunches();
		if( allTerminated(all))
			return SERVER_STATE.DOWN;
		return SERVER_STATE.UP;
	}

	private boolean allTerminated(ILaunch[] all) {
		for( int i = 0; i < all.length; i++ ) {
			if( !all[i].isTerminated())
				return false;
		}
		return true;
	}
	
}