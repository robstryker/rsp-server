/*******************************************************************************
 * Copyright (c) 2026 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.server.spi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves Eclipse-style variable references in strings.
 * Currently supports:
 *   ${env_var:NAME} - resolved against the current process environment
 */
public class VariableResolver {

	private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{env_var:([^}]+)\\}");

	public static String resolve(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		Matcher m = ENV_VAR_PATTERN.matcher(input);
		if (!m.find()) {
			return input;
		}
		StringBuffer sb = new StringBuffer();
		m.reset();
		while (m.find()) {
			String varName = m.group(1);
			String value = System.getenv(varName);
			m.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : m.group(0)));
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
