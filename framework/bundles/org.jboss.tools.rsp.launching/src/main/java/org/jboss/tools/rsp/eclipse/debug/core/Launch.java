/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Pawel Piech - Bug 82003: The IDisconnect implementation by Launch module is too restrictive.
 *     Andrey Loskutov <loskutov@gmx.de> - Bug 506182 - Launch is not multi-thread safe
 *******************************************************************************/
package org.jboss.tools.rsp.eclipse.debug.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jboss.tools.rsp.eclipse.core.runtime.IStatus;
import org.jboss.tools.rsp.eclipse.core.runtime.MultiStatus;
import org.jboss.tools.rsp.eclipse.debug.core.model.IDisconnect;
import org.jboss.tools.rsp.eclipse.debug.core.model.IProcess;

/**
 * A launch is the result of launching a debug session
 * and/or one or more system processes. This class provides
 * a public implementation of <code>ILaunch</code> for client
 * use.
 * <p>
 * Clients may instantiate this class. Clients may subclass this class.
 * </p>
 * @see ILaunch
 * @see ILaunchManager
 */
public class Launch  implements ILaunch, IDisconnect  { //ILaunchListener, ILaunchConfigurationListener, IDebugEventSetListener {
	private static final String Launch_terminate_failed="Terminate failed";
	
	
	/**
	 * Lock object for controlling access to processes and targets
	 */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final Lock readLock = lock.readLock();

	private final Lock writeLock = lock.writeLock();

//	/**
//	 * The debug targets associated with this
//	 * launch (the primary target is the first one
//	 * in this collection), or empty if
//	 * there are no debug targets.
//	 */
//	private List<IDebugTarget> fTargets = new ArrayList<IDebugTarget>();
//
//	/**
//	 * The configuration that was launched, or null.
//	 */
//	private ILaunchConfiguration fConfiguration= null;

	/**
	 * The system processes associated with
	 * this launch, or empty if none.
	 */
	private List<IProcess> fProcesses = new ArrayList<IProcess>();

//	/**
//	 * The source locator to use in the debug session
//	 * or <code>null</code> if not supported.
//	 */
//	private ISourceLocator fLocator= null;

	/**
	 * The mode this launch was launched in.
	 */
	private String fMode;

	/**
	 * Table of client defined attributes
	 */
	private HashMap<String, String> fAttributes;

	/**
	 * Flag indicating that change notification should
	 * be suppressed. <code>true</code> until this
	 * launch has been initialized.
	 */
	private boolean fSuppressChange = true;

	/**
	 * Constructs a launch with the specified attributes.
	 *
	 * @param launchConfiguration the configuration that was launched
	 * @param mode the mode of this launch - run or debug (constants
	 *  defined by <code>ILaunchManager</code>)
	 * @param locator the source locator to use for this debug session, or
	 * 	<code>null</code> if not supported
	 */
	public Launch(/*ILaunchConfiguration*/ Object launchConfiguration, String mode, /*ISourceLocator*/ Object locator) {
		fMode = mode;
		fSuppressChange = false;
//		fConfiguration = launchConfiguration;
//		setSourceLocator(locator);
//		getLaunchManager().addLaunchListener(this);
//		getLaunchManager().addLaunchConfigurationListener(this);
	}

	/**
	 * Registers debug event listener.
	 */
	private void addEventListener() {
		// TODO Listen for events
		//DebugPlugin.getDefault().addDebugEventListener(this);
	}

	/**
	 * Removes debug event listener.
	 */
	private void removeEventListener() {
		// TODO Listen for events
		//DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	/**
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		 readLock.lock();
		try {
			for (IProcess process : getProcesses0()) {
				if (process.canTerminate()) {
					return true;
				}
			}
//			for (IDebugTarget target : getDebugTargets0()) {
//				if (target.canTerminate() || target.canDisconnect()) {
//					return true;
//				}
//			}
		} finally {
			 readLock.unlock();
		}
		return false;
	}

	/**
	 * @see ILaunch#getChildren()
	 */
	@Override
	public Object[] getChildren() {
		readLock.lock();
		ArrayList<Object> children;
		try {
			children = new ArrayList<Object>();
			//children.addAll(getDebugTargets0());
			children.addAll(getProcesses0());
		} finally {
			readLock.unlock();
		}
		return children.toArray();
	}

	/**
	 * @see ILaunch#getDebugTarget()
	 */
//	@Override
//	public IDebugTarget getDebugTarget() {
//		readLock.lock();
//		try {
//			if (!getDebugTargets0().isEmpty()) {
//				return getDebugTargets0().get(0);
//			}
//		} finally {
//			readLock.unlock();
//		}
//		return null;
//	}

	/**
	 * @see ILaunch#getProcesses()
	 */
	@Override
	public IProcess[] getProcesses() {
		readLock.lock();
		try {
			return getProcesses0().toArray(new IProcess[getProcesses0().size()]);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Returns the processes associated with this
	 * launch, in its internal form - a list.
	 *
	 * @return list of processes
	 */
	protected List<IProcess> getProcesses0() {
		return fProcesses;
	}

//	/**
//	 * @see ILaunch#getSourceLocator()
//	 */
//	@Override
//	public ISourceLocator getSourceLocator() {
//		return fLocator;
//	}
//
//	/**
//	 * @see ILaunch#setSourceLocator(ISourceLocator)
//	 */
//	@Override
//	public void setSourceLocator(ISourceLocator sourceLocator) {
//		fLocator = sourceLocator;
//	}

	/**
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		readLock.lock();
		try {
			if (getProcesses0().isEmpty()  ) { // && getDebugTargets0().isEmpty()) {
				return false;
			}
			for (IProcess process : getProcesses0()) {
				if (!process.isTerminated()) {
					return false;
				}
			}
//			for (IDebugTarget target : getDebugTargets0()) {
//				if (!(target.isTerminated() || target.isDisconnected())) {
//					return false;
//				}
//			}
		} finally {
			readLock.unlock();
		}
		return true;
	}

	/**
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		MultiStatus status=
			new MultiStatus(DebugPluginConstants.DEBUG_CORE_ID, DebugException.REQUEST_FAILED, Launch_terminate_failed, null);
		//stop targets first to free up and sockets, etc held by the target
		// terminate or disconnect debug target if it is still alive
//		IDebugTarget[] targets = getDebugTargets();
//		for (int i = 0; i < targets.length; i++) {
//			IDebugTarget target= targets[i];
//			if (target != null) {
//				if (target.canTerminate()) {
//					try {
//						target.terminate();
//					} catch (DebugException e) {
//						status.merge(e.getStatus());
//					}
//				} else {
//					if (target.canDisconnect()) {
//						try {
//							target.disconnect();
//						} catch (DebugException de) {
//							status.merge(de.getStatus());
//						}
//					}
//				}
//			}
//		}
		//second kill the underlying process
		// terminate the system processes
		IProcess[] processes = getProcesses();
		for (int i = 0; i < processes.length; i++) {
			IProcess process = processes[i];
			if (process.canTerminate()) {
				try {
					process.terminate();
				} catch (DebugException e) {
					status.merge(e.getStatus());
				}
			}
		}
		if (status.isOK()) {
			return;
		}
		IStatus[] children= status.getChildren();
		if (children.length == 1) {
			throw new DebugException(children[0]);
		}
		throw new DebugException(status);
	}

	/**
	 * @see ILaunch#getLaunchMode()
	 */
	@Override
	public String getLaunchMode() {
		return fMode;
	}

//	/**
//	 * @see ILaunch#getLaunchConfiguration()
//	 */
//	@Override
//	public ILaunchConfiguration getLaunchConfiguration() {
//		return fConfiguration;
//	}

	/**
	 * @see ILaunch#setAttribute(String, String)
	 */
	@Override
	public void setAttribute(String key, String value) {
		if (fAttributes == null) {
			fAttributes = new HashMap<String, String>(5);
		}
		fAttributes.put(key, value);
	}

	/**
	 * @see ILaunch#getAttribute(String)
	 */
	@Override
	public String getAttribute(String key) {
		if (fAttributes == null) {
			return null;
		}
		return fAttributes.get(key);
	}

	/**
	 * @see ILaunch#addProcess(IProcess)
	 */
	@Override
	public void addProcess(IProcess process) {
		if (process != null) {
			writeLock.lock();
			boolean changed = false;
			try {
				if (!getProcesses0().contains(process)) {
					addEventListener();
					changed = getProcesses0().add(process);
				}
			} finally {
				writeLock.unlock();
				if (changed) {
					fireChanged();
				}
			}
		}
	}

	/**
	 * @see ILaunch#removeProcess(IProcess)
	 */
	@Override
	public void removeProcess(IProcess process) {
		if (process != null) {
			writeLock.lock();
			boolean changed = false;
			try {
				changed = getProcesses0().remove(process);
			} finally {
				writeLock.unlock();
				if (changed) {
					fireChanged();
				}
			}
		}
	}

	/**
	 * Adds the given processes to this launch.
	 *
	 * @param processes processes to add
	 */
	protected void addProcesses(IProcess[] processes) {
		if (processes != null) {
			for (int i = 0; i < processes.length; i++) {
				addProcess(processes[i]);
				fireChanged();
			}
		}
	}

	/**
	 * Notifies listeners that this launch has changed.
	 * Has no effect of this launch has not yet been
	 * properly created/initialized.
	 */
	protected void fireChanged() {
		if (!fSuppressChange) {
			// TODO Fire events?
//			((LaunchManager)getLaunchManager()).fireUpdate(this, LaunchManager.CHANGED);
//			((LaunchManager)getLaunchManager()).fireUpdate(new ILaunch[] {this}, LaunchManager.CHANGED);
		}
	}

	/**
	 * Notifies listeners that this launch has terminated.
	 * Has no effect of this launch has not yet been
	 * properly created/initialized.
	 */
	protected void fireTerminate() {
		if (!fSuppressChange) {
			// TODO
//			((LaunchManager)getLaunchManager()).fireUpdate(this, LaunchManager.TERMINATE);
//			((LaunchManager)getLaunchManager()).fireUpdate(new ILaunch[] {this}, LaunchManager.TERMINATE);
		}
		removeEventListener();
	}

	/**
	 * @see ILaunch#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return getProcesses0().size() > 0; // || (getDebugTargets0().size() > 0);
	}

	/**
     * Returns whether any processes or targets can be disconnected.
     * Ones that are already terminated or disconnected are ignored.
     *
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	@Override
	public boolean canDisconnect() {
		readLock.lock();
		try {
			for (IProcess process : getProcesses0()) {
				if (process instanceof IDisconnect) {
					if (((IDisconnect) process).canDisconnect()) {
						return true;
					}
				}
			}
//			for (IDebugTarget target : getDebugTargets0()) {
//				if (target.canDisconnect()) {
//					return true;
//				}
//			}
		} finally {
			readLock.unlock();
		}
        return false;
	}

	/**
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	@Override
	public void disconnect() throws DebugException {
		readLock.lock();
		try {
			for (IProcess process : getProcesses0()) {
				if (process instanceof IDisconnect) {
					IDisconnect dis = (IDisconnect) process;
					if (dis.canDisconnect()) {
						dis.disconnect();
					}
				}
			}
//			for (IDebugTarget target : getDebugTargets0()) {
//				if (target.canDisconnect()) {
//					target.disconnect();
//				}
//			}
		} finally {
			readLock.unlock();
		}
	}

	/**
     * Returns whether all of the contained targets and processes are
     * disconnected. Processes that don't support disconnecting are not
     * counted.
     *
	 * @see org.jboss.tools.rsp.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	@Override
	public boolean isDisconnected() {
		readLock.lock();
		try {
			for (IProcess process : getProcesses0()) {
				if (process instanceof IDisconnect) {
					if (!((IDisconnect) process).isDisconnected()) {
						return false;
					}
				}
			}
//			for (IDebugTarget target : getDebugTargets0()) {
//				if (!target.isDisconnected()) {
//					return false;
//				}
//			}
		} finally {
			readLock.unlock();
		}
        // only return true if there are processes or targets that are disconnected
        return hasChildren();
	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
//	 */
//	@Override
//	public void launchRemoved(ILaunch launch) {
//		if (this.equals(launch)) {
//			removeEventListener();
//			getLaunchManager().removeLaunchListener(this);
//			getLaunchManager().removeLaunchConfigurationListener(this);
//		}
//	}
//
//	/**
//	 * Returns the launch manager.
//	 *
//	 * @return the launch manager.
//	 */
//	protected ILaunchManager getLaunchManager() {
//		return DebugPlugin.getDefault().getLaunchManager();
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
//	 */
//	@Override
//	public void launchAdded(ILaunch launch) {
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
//	 */
//	@Override
//	public void launchChanged(ILaunch launch) {
//	}
//
//	/* (non-Javadoc)
//	 *
//	 * If the launch configuration this launch is associated with is
//	 * moved, update the underlying handle to the new location.
//	 *
//	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationAdded(org.eclipse.debug.core.ILaunchConfiguration)
//	 */
//	@Override
//	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
//		ILaunchConfiguration from = getLaunchManager().getMovedFrom(configuration);
//		if (from != null && from.equals(getLaunchConfiguration())) {
//			fConfiguration = configuration;
//			fireChanged();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationChanged(org.eclipse.debug.core.ILaunchConfiguration)
//	 */
//	@Override
//	public void launchConfigurationChanged(ILaunchConfiguration configuration) {}
//
//	/* (non-Javadoc)
//	 *
//	 * Update the launch configuration associated with this launch if the
//	 * underlying configuration is deleted.
//	 *
//	 * @see org.eclipse.debug.core.ILaunchConfigurationListener#launchConfigurationRemoved(org.eclipse.debug.core.ILaunchConfiguration)
//	 */
//	@Override
//	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
//		if (configuration.equals(getLaunchConfiguration())) {
//			if (getLaunchManager().getMovedTo(configuration) == null) {
//				fConfiguration = null;
//				fireChanged();
//			}
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
//	 */
//	@Override
//	public void handleDebugEvents(DebugEvent[] events) {
//		for (int i = 0; i < events.length; i++) {
//			DebugEvent event = events[i];
//			if (event.getKind() == DebugEvent.TERMINATE) {
//				Object object = event.getSource();
//				ILaunch launch = null;
//				if (object instanceof IProcess) {
//					launch = ((IProcess)object).getLaunch();
////				} else if (object instanceof IDebugTarget) {
////					launch = ((IDebugTarget)object).getLaunch();
//				}
//				if (this.equals(launch)) {
//					if (isTerminated()) {
//						fireTerminate();
//					}
//				}
//			}
//		}
//	}

}
