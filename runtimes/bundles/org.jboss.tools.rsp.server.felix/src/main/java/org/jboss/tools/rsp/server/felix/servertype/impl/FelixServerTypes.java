/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.felix.servertype.impl;

public class FelixServerTypes {
	public static final String FELIX_6_ID = "org.jboss.tools.rsp.server.felix.6";
	public static final String FELIX_6_NAME = "Apache Felix 6.0.x";
	public static final String FELIX_6_DESC = "A server adapter capable of controlling and publishing to an Apache Felix 6.0.x container.";

	public static final FelixServerType FELIX_6_SERVER_TYPE = 
			new FelixServerType(FELIX_6_ID, FELIX_6_NAME, FELIX_6_DESC);
}
