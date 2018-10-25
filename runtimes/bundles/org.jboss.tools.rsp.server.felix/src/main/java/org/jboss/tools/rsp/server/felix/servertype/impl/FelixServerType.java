/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.felix.servertype.impl;

import org.jboss.tools.rsp.api.ServerManagementAPIConstants;
import org.jboss.tools.rsp.api.dao.Attributes;
import org.jboss.tools.rsp.api.dao.ServerLaunchMode;
import org.jboss.tools.rsp.api.dao.util.CreateServerAttributesUtility;
import org.jboss.tools.rsp.launching.java.ILaunchModes;
import org.jboss.tools.rsp.server.spi.servertype.IServer;
import org.jboss.tools.rsp.server.spi.servertype.IServerDelegate;
import org.jboss.tools.rsp.server.spi.servertype.IServerType;

public class FelixServerType implements IServerType {

	protected Attributes required = null;
	protected Attributes optional = null;

	private String id;
	private String name;
	private String desc;

	public FelixServerType(String id, String name, String desc) {
		this.name = name;
		this.id = id;
		this.desc = desc;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public Attributes getRequiredAttributes() {
		if (required == null) {
			CreateServerAttributesUtility attrs = new CreateServerAttributesUtility();
			
			attrs.addAttribute(ServerManagementAPIConstants.SERVER_HOME_DIR, 
					ServerManagementAPIConstants.ATTR_TYPE_STRING,
					"A filesystem path pointing to an Apache Felix home directory.", null);
			required = attrs.toPojo();
		}
		return required;
	}
	

	@Override
	public Attributes getOptionalAttributes() {
		if (optional == null) {
			CreateServerAttributesUtility attrs = new CreateServerAttributesUtility();
			this.optional = attrs.toPojo();
		}
		return optional;
	}

	@Override
	public Attributes getRequiredLaunchAttributes() {
		CreateServerAttributesUtility attrs = new CreateServerAttributesUtility();
		return attrs.toPojo();
	}

	@Override
	public Attributes getOptionalLaunchAttributes() {
		CreateServerAttributesUtility attrs = new CreateServerAttributesUtility();
		return attrs.toPojo();
	}

	@Override
	public ServerLaunchMode[] getLaunchModes() {
		return new ServerLaunchMode[] { new ServerLaunchMode(ILaunchModes.RUN, ILaunchModes.RUN_DESC), };
	}

	@Override
	public IServerDelegate createServerDelegate(IServer server) {
		return new FelixServerDelegate(server);
	}
}
