/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.minishift.discovery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.jboss.tools.rsp.foundation.core.launchers.CommandTimeoutException;
import org.jboss.tools.rsp.foundation.core.launchers.ProcessUtility;

public class MinishiftVersionLoader {

	public static final String ERROR_KEY = "properties.load.error";
	public static final String VERSION_KEY = "Minishift version";
	public static final String VERSION_KEY2 = "minishift";
	public static final String CDK_VERSION_KEY = "CDK";
	public static final String CDK_VERSION_KEY_OLD = "CDK Version";

	private MinishiftVersionLoader() {
		// inhibit instantiation
	}
	
	public static MinishiftVersions getVersionProperties(String commandPath) {
		Properties ret = new Properties();
		try {
			String wd = new File(commandPath).getParentFile().getAbsolutePath();
			String[] lines = new ProcessUtility().call(commandPath, new String[] { "version" },
					wd, new HashMap<String, String>(), 5000);

			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().isEmpty())
					continue;
				if (lines[i].contains(":")) {
					// Old format, Some Key: 1.2.3
					String[] split = lines[i].split(":");
					if (split.length == 2)
						ret.put(split[0], split[1]);
				} else if (lines[i].contains(" ")) {
					String[] split = lines[i].split(" ");
					if (split.length == 2)
						ret.put(split[0], split[1]);
				}
			}
		} catch (IOException | CommandTimeoutException e) {
			ret.put(ERROR_KEY, e.getMessage());
		}
		return new MinishiftVersions(ret);
	}

	public static class MinishiftVersions {
		private Properties p;

		public MinishiftVersions(Properties p) {
			this.p = p;
		}

		public String getError() {
			return p.getProperty(ERROR_KEY);
		}

		public String getMinishiftVersion() {
			String v = p.getProperty(VERSION_KEY);
			if (v == null) {
				v = p.getProperty(VERSION_KEY2);
			}
			return cleanVersion(v);
		}

		private String cleanVersion(String v) {
			if (v == null)
				return null;
			if (v.trim().startsWith("v")) {
				return v.trim().substring(1);
			}
			return v.trim();
		}

		public String getCDKVersion() {
			// CDK Version
			String v = p.getProperty(CDK_VERSION_KEY);
			if (v == null) {
				v = p.getProperty(CDK_VERSION_KEY_OLD);
			}
			return cleanVersion(v);
		}

		public boolean isValid() {
			return getMinishiftVersion() != null;
		}
	}
}
