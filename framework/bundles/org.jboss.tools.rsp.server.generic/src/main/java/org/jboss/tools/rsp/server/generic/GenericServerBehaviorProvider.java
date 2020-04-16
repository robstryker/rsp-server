/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.generic;

import org.jboss.tools.rsp.launching.memento.JSONMemento;
import org.jboss.tools.rsp.server.generic.servertype.GenericServerBehavior;
import org.jboss.tools.rsp.server.spi.servertype.IServer;

public class GenericServerBehaviorProvider implements IServerBehaviorProvider {
	private JSONMemento behaviorMemento;

	public GenericServerBehaviorProvider(JSONMemento behaviorMemento) {
		this.behaviorMemento = behaviorMemento;
	}

	@Override
	public GenericServerBehavior createServerDelegate(String typeId, IServer server) {
		return new GenericServerBehavior(server, behaviorMemento);
	}

}
