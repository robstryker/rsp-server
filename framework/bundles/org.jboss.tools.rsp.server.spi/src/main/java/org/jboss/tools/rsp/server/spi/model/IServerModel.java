/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.spi.model;

import java.util.List;
import java.util.Map;

import org.jboss.tools.rsp.api.dao.Attributes;
import org.jboss.tools.rsp.api.dao.CreateServerResponse;
import org.jboss.tools.rsp.api.dao.DeployableReference;
import org.jboss.tools.rsp.api.dao.DeployableState;
import org.jboss.tools.rsp.api.dao.ServerHandle;
import org.jboss.tools.rsp.api.dao.ServerLaunchMode;
import org.jboss.tools.rsp.api.dao.ServerState;
import org.jboss.tools.rsp.api.dao.ServerType;
import org.jboss.tools.rsp.eclipse.core.runtime.CoreException;
import org.jboss.tools.rsp.eclipse.core.runtime.IStatus;
import org.jboss.tools.rsp.secure.model.ISecureStorageProvider;
import org.jboss.tools.rsp.server.spi.servertype.IServer;
import org.jboss.tools.rsp.server.spi.servertype.IServerType;

public interface IServerModel {
	
	public static final String SECURE_ATTRIBUTE_PREFIX = ":secure:server:";
	
	ISecureStorageProvider getSecureStorageProvider();
	
	ServerType[] getServerTypes();
	
	ServerType[] getAccessibleServerTypes();

	IServer getServer(String id);
	
	IServerType getIServerType(String typeId);
	
	Map<String, IServer> getServers();

	ServerHandle[] getServerHandles();

	Attributes getRequiredAttributes(IServerType serverType);

	Attributes getOptionalAttributes(IServerType serverType);

	List<ServerLaunchMode> getLaunchModes(IServerType serverType);
	
	Attributes getRequiredLaunchAttributes(IServerType serverType);

	Attributes getOptionalLaunchAttributes(IServerType serverType);

	CreateServerResponse createServer(String serverType, String id, Map<String, Object> attributes);

	boolean removeServer(IServer server);

	void fireServerStateChanged(IServer server, ServerState state);

	void fireServerProcessTerminated(IServer server, String processId);

	void fireServerProcessCreated(IServer server, String processId);

	void fireServerStreamAppended(IServer server2, String processId, int streamType, String text);

	void addServerModelListener(IServerModelListener listener);

	void addServerType(IServerType serverType);
	void addServerTypes(IServerType[] serverTypes);

	void removeServerType(IServerType serverType);
	void removeServerTypes(IServerType[] serverTypes);
	
	void loadServers() throws CoreException;

	void saveServers() throws CoreException;
	
	List<DeployableState> getDeployables(IServer server);
	
	IStatus addDeployable(IServer server, DeployableReference reference);
	IStatus removeDeployable(IServer server, DeployableReference reference);

	IStatus publish(IServer server, int kind) throws CoreException;
}
