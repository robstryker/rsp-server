/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jboss.tools.rsp.api.dao.ServerHandle;
import org.jboss.tools.rsp.api.dao.ServerType;
import org.jboss.tools.rsp.eclipse.core.runtime.CoreException;
import org.jboss.tools.rsp.server.spi.model.ServerModelListenerAdapter;
import org.jboss.tools.rsp.server.spi.servertype.IServer;
import org.jboss.tools.rsp.server.spi.servertype.IServerDelegate;
import org.jboss.tools.rsp.server.spi.servertype.IServerType;
import org.junit.Test;

public class ServerModelTest {
	
	@Test
	public void testGibberishFile() {
		ServerModel sm = new ServerModel();
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = "this is not xml at all";
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 0);
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}

	@Test
	public void testMissingServerTypeInFile() {
		ServerModel sm = new ServerModel();
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = getServerStringNoType("abc123"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 0);
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}

	@Test
	public void testMissingServerTypeInModel() {
		ServerModel sm = new ServerModel();
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = getServerString("abc123", "wonka5"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 0);
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}

	

	@Test
	public void testLoadServer() {
		ServerModel sm = new ServerModel();
		sm.addServerType(mockServerType("wonka5"));
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = getServerString("abc123", "wonka5"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 1);
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}
	

	@Test
	public void testLoadServerHandles() {
		ServerModel sm = new ServerModel();
		sm.addServerType(mockServerType("wonka5"));
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = getServerString("abc123", "wonka5"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 1);
			
			assertNotNull(sm.getServerHandles());
			assertEquals(sm.getServerHandles().length, 1);
			ServerHandle sh = sm.getServerHandles()[0];
			assertNotNull(sh);
			assertEquals(sh.getId(), "abc123");
			assertNotNull(sh.getType());
			ServerType st = sh.getType();
			assertEquals(st.getId(), "wonka5");
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}
	
	@Test
	public void testAddAndRemoveServerType() {
		ServerModel sm = new ServerModel();
		assertNotNull(sm.getServerTypes());
		assertEquals(0, sm.getServerTypes().length);
		assertNull(sm.getIServerType("wonka5"));
		
		IServerType mock = mockServerType("wonka5");
		sm.addServerType(mock);
		assertNotNull(sm.getServerTypes());
		assertEquals(1, sm.getServerTypes().length);
		assertNotNull(sm.getIServerType("wonka5"));

		sm.removeServerType(mock);
		assertNotNull(sm.getServerTypes());
		assertNull(sm.getIServerType("wonka5"));
		assertEquals(0, sm.getServerTypes().length);
	}
	
	@Test
	public void testLoadAndRemoveServerWithListener() {
		ServerModel sm = new ServerModel();
		final Boolean[] added = new Boolean[] {new Boolean(false)};
		final Boolean[] removed = new Boolean[] {new Boolean(false)};
		ServerModelListenerAdapter smla = new ServerModelListenerAdapter() {
			@Override
			public void serverAdded(ServerHandle server) {
				added[0] = Boolean.TRUE;
			}
			@Override
			public void serverRemoved(ServerHandle server) {
				removed[0] = Boolean.TRUE;
			}
		}; 
		sm.addServerModelListener(smla);
		sm.addServerType(mockServerType("wonka5"));
		Path dir = null;
		Path s1 = null;
		try {
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			String contents = getServerString("abc123", "wonka5"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 1);
			assertNotNull(sm.getServer("abc123"));
			assertTrue(added[0].booleanValue());
			sm.removeServer("abc123");
			assertEquals(sm.getServers().size(), 0);
			assertNull(sm.getServer("abc123"));
			assertTrue(removed[0].booleanValue());
			assertFalse(s1.toFile().exists());
			
			sm.removeServerModelListener(smla);
			added[0] = false;
			removed[0] = false;
			
			dir = Files.createTempDirectory("servermodeltest");
			s1 = dir.resolve("s1");
			contents = getServerString("abc123", "wonka5"); 
			Files.write(s1, contents.getBytes());
			sm.loadServers(dir.toFile());
			assertEquals(sm.getServers().size(), 1);
			assertNotNull(sm.getServer("abc123"));
			assertFalse(added[0].booleanValue());
			sm.removeServer("abc123");
			assertEquals(sm.getServers().size(), 0);
			assertNull(sm.getServer("abc123"));
			assertFalse(removed[0].booleanValue());
			assertFalse(s1.toFile().exists());
		} catch(IOException | CoreException e) {
			if( s1 != null && s1.toFile().exists()) {
				s1.toFile().delete();
				s1.toFile().getParentFile().delete();
			}
			fail();
		}
	}

	
	private String getServerString(String name, String type) {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + 
				"<server id=\"" + name + "\" id-set=\"true\" " + 
				"org.jboss.tools.rsp.server.typeId=\"" + type + "\"/>\n";
		return contents;
	}

	private String getServerStringNoType(String name) {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + 
				"<server id=\"" + name + "\" id-set=\"true\"/>\n";
		return contents;
	}
	
	private IServerType mockServerType(String typeId) {
		IServerType ist = mock(IServerType.class);
		doReturn(typeId).when(ist).getId();
		IServerDelegate isd = mock(IServerDelegate.class);
		when(ist.createServerDelegate(any(IServer.class))).thenReturn(isd);
		return ist;
	}

}
