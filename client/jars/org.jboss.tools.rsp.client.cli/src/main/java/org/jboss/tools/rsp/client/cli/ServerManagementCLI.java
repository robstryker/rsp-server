/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.client.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.tools.rsp.api.ICapabilityKeys;
import org.jboss.tools.rsp.api.dao.ClientCapabilitiesRequest;
import org.jboss.tools.rsp.api.dao.ServerCapabilitiesResponse;
import org.jboss.tools.rsp.client.bindings.IClientConnectionClosedListener;
import org.jboss.tools.rsp.client.bindings.ServerManagementClientLauncher;

public class ServerManagementCLI implements InputProvider, IClientConnectionClosedListener {
	public static void main(String[] args) {
		defaultMain(args);
		//programaticMain(args);
	}
	
	private static void programaticMain(String[] args) {
		// This is an example
		String toRun = "list servers\ndelay 30\nexit\n";
		String[] split = toRun.split("\n");
		
		ByteArrayOutputStream cliOut = new ByteArrayOutputStream();
		PipedOutputStream cliInNested = new PipedOutputStream();
		PipedInputStream cliIn = null;
		try {
			cliIn = new PipedInputStream(cliInNested);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		ServerManagementCLI cli = new ServerManagementCLI(null, cliIn, new PrintStream(cliOut));
		try {
			cli.connect(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		new Thread("Testing") {
			public void run() {
				for( String i : split ) {
					if( i.startsWith("delay " )) {
						String suffix = i.substring("delay ".length());
						try {
							int delayTime = Integer.parseInt(suffix.trim());
							try {
								Thread.sleep(delayTime*1000);
							} catch(InterruptedException ie) {
								ie.printStackTrace();
							}
						} catch(NumberFormatException nfe) {
							nfe.printStackTrace();
						}
					} else {
						try {
							cliInNested.write(i.getBytes());
							cliInNested.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		cli.readInput();
	}
	
	private static void defaultMain(String[] args) {
		ServerManagementCLI cli = new ServerManagementCLI(null, System.in, System.out);
		try {
			cli.connect(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		cli.readInput();
	}
	

	private Scanner scanner = null;
	private ServerManagementClientLauncher launcher;
	private ConcurrentLinkedQueue<InputHandler> q = new ConcurrentLinkedQueue<>();
	private StandardCommandHandler defaultHandler;
	private IClientShutdownHandler shutdownHandler;
	private InputStream inputStream;
	private PrintStream os;

	public ServerManagementCLI(IClientShutdownHandler shutdown, InputStream in, PrintStream os) {
		this.shutdownHandler = shutdown;
		this.inputStream = in;
		this.os = os;
	}
	public IClientShutdownHandler getShutdownHandler() {
		if( shutdownHandler == null ) {
			return new IClientShutdownHandler() {
				@Override
				public void shutdown() {
					System.exit(0);
				}
				
			};
		}
		return this.shutdownHandler;
	}
	
	public void connect(String host, String port) throws Exception {
		if (host == null) {
			os.print("Enter server host: ");
			host = nextLine();
		}
		if (port == null) {
			os.print("Enter server port: ");
			port = nextLine();
		}

		launcher = new ServerManagementClientLauncher(host, Integer.parseInt(port), this);
		launcher.launch();
		launcher.setListener(this);
		
		Map<String, String> clientCap2 = new HashMap<String, String>();
		clientCap2.put(ICapabilityKeys.STRING_PROTOCOL_VERSION, ICapabilityKeys.PROTOCOL_VERSION_0_10_0);
		clientCap2.put(ICapabilityKeys.BOOLEAN_STRING_PROMPT, Boolean.toString(true));
		ClientCapabilitiesRequest clientCap = new ClientCapabilitiesRequest(clientCap2);
		ServerCapabilitiesResponse rsp = launcher.getServerProxy().registerClientCapabilities(clientCap).get();
		defaultHandler = new StandardCommandHandler(launcher, this, getShutdownHandler());

		os.println("Connected to: " + host + ":" + port);

	}

	public void addInputRequest(InputHandler handler) {
		if (q.peek() == null) {
			String prompt = handler.getPrompt();
			if (prompt != null) {
				os.println(prompt);
			}
		}
		q.add(handler);
	}

	protected String nextLine() {
		if (scanner == null) {
			scanner = new Scanner(inputStream);
		}
		return scanner.nextLine();
	}

	private void readInput() {
		while (true) {
			if (q.peek() != null) {
				InputHandler handler = q.peek();
				String prompt = handler.getPrompt();
				if (prompt != null) {
					os.println(prompt);
				}
			}
			String content = nextLine();
			InputHandler h = null;
			if (q.peek() == null) {
				h = defaultHandler;
			} else {
				h = q.remove();
			}
			if (h != null) {
				if( !launcher.isConnectionActive()) {
					initShutdown();
					return;
				}
				
				final InputHandler h2 = h;
				new Thread("Handle input") {
					public void run() {
						try {
							h2.handleInput(content);
						} catch (Exception e) {
							e.printStackTrace();
							// Try to recover
						}
					}
				}.start();
			}
		}
	}

	@Override
	public void connectionClosed() {
		initShutdown();
	}
	
	private synchronized void initShutdown() {
		os.println("Connection with remote server has terminated.");
		getShutdownHandler().shutdown();
	}
	@Override
	public void output(String out) {
		os.println(out);
	}
}
