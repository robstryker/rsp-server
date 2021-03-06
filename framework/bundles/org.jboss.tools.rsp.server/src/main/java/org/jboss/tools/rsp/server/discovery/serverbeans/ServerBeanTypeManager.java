/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.discovery.serverbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.tools.rsp.server.spi.discovery.IServerBeanTypeManager;
import org.jboss.tools.rsp.server.spi.discovery.IServerBeanTypeProvider;
import org.jboss.tools.rsp.server.spi.discovery.ServerBeanType;

public class ServerBeanTypeManager implements IServerBeanTypeManager {

	private List<IServerBeanTypeProvider> typeProviders;
	
	public ServerBeanTypeManager() {
		typeProviders = new ArrayList<>();
	}
	
	public void addTypeProvider(IServerBeanTypeProvider provider) {
		typeProviders.add(provider);
	}
	
	public void removeTypeProvider(IServerBeanTypeProvider provider) {
		typeProviders.remove(provider);
	}

	public ServerBeanType[] getAllRegisteredTypes() {
		return typeProviders.stream()
			.map(IServerBeanTypeProvider::getServerBeanTypes)
			.flatMap(Arrays::stream)
			.toArray(size -> new ServerBeanType[size]);
	}
}
