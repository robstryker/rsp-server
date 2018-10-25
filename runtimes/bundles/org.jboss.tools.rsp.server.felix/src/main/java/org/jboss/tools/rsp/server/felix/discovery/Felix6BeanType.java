/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.felix.discovery;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.tools.rsp.server.felix.servertype.impl.FelixServerTypes;
import org.jboss.tools.rsp.server.spi.discovery.ServerBeanType;

public class Felix6BeanType extends ServerBeanType {
	
	protected Felix6BeanType() {
		super("FELIX", "Apache Felix 6.0.x");
	}

	public Felix6BeanType(String id, String name) {
		super(id, name);
	}

	@Override
	public boolean isServerRoot(File location) {
		if( location == null)
			return false;
		if( location.isFile()) 
			return false;
		if( !folderExists(location, "bin"))
			return false;
		if( !folderExists(location, "bundle"))
			return false;
		if( !folderExists(location, "conf"))
			return false;
		if( !folderExists(location, "felix-cache"))
			return false;
		File bin = new File(location, "bin");
		File felixJar = new File(bin, "felix.jar");
		if( !felixJar.exists() || !felixJar.isFile())
			return false;
		return true;
		
	}
	
	private boolean folderExists(File root, String sub) {
		return root != null && new File(root, sub).exists() && new File(root, sub).isDirectory();
	}
	

	@Override
	public String getFullVersion(File root) {
		if( isServerRoot(root)) {
			File bin = new File(root, "bin");
			File felixJar = new File(bin, "felix.jar");
			String vers = getJarProperty(felixJar, "Bundle-Version");
			return vers;
		}
		return null;
	}

	@Override
	public String getUnderlyingTypeId(File root) {
		if( isServerRoot(root)) {
			return "FELIX";
		}
		return null;
	}

	@Override
	public String getServerAdapterTypeId(String version) {
		return FelixServerTypes.FELIX_6_ID;
	}
	

	/**
	 * This method will check a jar file for a manifest, and, if it has it, 
	 * find the value for the given property. 
	 * 
	 * If either the jar, manifest or the property are not found, 
	 * return null.
	 * 
	 * @param systemJarFile
	 * @param propertyName
	 * @return
	 */
	public static String getJarProperty(File systemJarFile, String propertyName) {
		if (systemJarFile.canRead()) {
			ZipFile jar = null;
			try {
				jar = new ZipFile(systemJarFile);
				ZipEntry manifest = jar.getEntry("META-INF/MANIFEST.MF");//$NON-NLS-1$
				Properties props = new Properties();
				props.load(jar.getInputStream(manifest));
				String value = (String) props.get(propertyName);
				return value;
			} catch (IOException e) {
				// Intentionally empty
				return null; 
			} finally {
				if (jar != null) {
					try {
						jar.close();
					} catch (IOException e) {
						// Intentionally empty
						return null;
					}
				}
			}
		} 
		return null;
	}
	
}