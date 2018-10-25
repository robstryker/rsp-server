/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.felix.impl;

import org.jboss.tools.rsp.server.felix.discovery.FelixBeanTypeProvider;
import org.jboss.tools.rsp.server.felix.servertype.impl.FelixServerTypes;
import org.jboss.tools.rsp.server.spi.model.IServerManagementModel;
import org.jboss.tools.rsp.server.spi.servertype.IServerType;

public class ExtensionHandler {

	private static final IServerType[] TYPES = {
			FelixServerTypes.FELIX_6_SERVER_TYPE
	};

	private ExtensionHandler() {
		// inhibit instantionation
	}

	public static void addExtensions(IServerManagementModel model) {
		model.getServerBeanTypeManager().addTypeProvider(new FelixBeanTypeProvider());

		model.getServerModel().addServerTypes(TYPES);
	}
	
	public static void removeExtensions(IServerManagementModel model) {
		model.getServerBeanTypeManager().removeTypeProvider(new FelixBeanTypeProvider());

		model.getServerModel().removeServerTypes(TYPES);
	}
}
