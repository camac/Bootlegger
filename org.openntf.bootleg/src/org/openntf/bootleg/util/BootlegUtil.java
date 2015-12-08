/*******************************************************************************
 * Copyright 2015 Cameron Gregor
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License
 *******************************************************************************/
package org.openntf.bootleg.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.openntf.bootleg.BootlegActivator;
import org.openntf.bootleg.builder.BootlegNature;
import org.openntf.bootleg.pref.BootlegPreferenceManager;
import org.openntf.bootleg.pref.BootlegPreferencePage;

import com.bdaum.overlayPages.FieldEditorOverlayPage;
import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.DominoResourcesPlugin;
import com.ibm.designer.domino.ide.resources.jni.NotesDesignElement;
import com.ibm.designer.domino.ide.resources.metamodel.IMetaModelConstants;
import com.ibm.designer.domino.ide.resources.project.IDominoDesignerProject;
import com.ibm.designer.domino.preferences.DominoPreferenceManager;
import com.ibm.designer.domino.team.util.SyncUtil;
import com.ibm.designer.prj.resources.commons.IMetaModelDescriptor;

public class BootlegUtil {

	public static LogMgr BOOTLEG_LOG = Log.load("org.openntf.bootleg", "Logger used for Bootleg");
	private static final String MARKER_TYPE = "org.openntf.bootleg.bootlegProblem";
	private static boolean loggingToFile = false;
	public static final String DEFAULT_CONFIGCLASSNAME = "BootleggedConfigs"; 
		
	public static String getPreferenceKey(IMetaModelDescriptor mmd) {
		return "bootleg.filter." + mmd.getID();
	}

	public static String getPreferenceKey(String id) {
		return "bootleg.filter." + id;
	}

	public static boolean shouldExport(IResource resource) {

		NotesDesignElement element = DominoResourcesPlugin.getNotesDesignElement(resource);

		if (element == null) {
			return false;
		}

		IMetaModelDescriptor mmd = element.getMetaModel();

		if (mmd == null) {
			return false;
		}

		String id = mmd.getID();

		if (id.equals(IMetaModelConstants.XSPCCS)) {

			if (StringUtil.equals(resource.getFileExtension(), "xsp-config")) {
				return false;
			} else {
				return true;
			}

		}
		
		return false;

	}

	public static String getPluginSourceFolder() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_PLUGIN_SOURCEFOLDER, false);
	}
	
	public static String getPluginSourceFolder(IResource resource) {

		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_PLUGIN_SOURCEFOLDER);

	}

	public static String getComponentPackage() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_PACKAGE_COMPONENT, false);
	}
	
	public static String getComponentPackage(IResource resource) {

		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_PACKAGE_COMPONENT);

	}

	public static String getConfigPackage() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_PACKAGE_CONFIG, false);
	}
	
	public static String getConfigPackage(IResource resource) {

		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_PACKAGE_CONFIG);

	}
	public static String getTargetPrefix() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_PREFIX, false);
	}
	
	public static String getTargetPrefix(IResource resource) {
		
		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_PREFIX);
		
	}
	public static String getTargetNamespace() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_NAMESPACE, false);
	}
	
	public static String getTargetNamespace(IResource resource) {
		
		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_NAMESPACE);
		
	}

	public static String getConfigClassName() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_CONFIGCLASSNAME, false);
	}
	
	public static String getConfigClassName(IResource resource) {
		
		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_CONFIGCLASSNAME);
		
	}

	public static String getCustomControlRegex() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_CC_EXPORT_REGEX, false);
	}
	
	public static String getCustomControlRegex(IResource resource) {
		
		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_CC_EXPORT_REGEX);
		
	}
	
	public static String getTargetCategory() {
		return BootlegPreferenceManager.getInstance().getValue(BootlegPreferencePage.PREF_CATEGORY, false);
	}
	
	public static String getTargetCategory(IResource resource) {
		
		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;
		return getOverlayedPreferenceValue(store, resource, pageId, BootlegPreferencePage.PREF_CATEGORY);
		
	}
	
	public static Boolean isAutoExport() {
		return BootlegPreferenceManager.getInstance().getBooleanValue(BootlegPreferencePage.PREF_AUTOEXPORT, false);
	}

	public static Boolean isAutoExport(IResource resource) {

		IPreferenceStore store = BootlegPreferenceManager.getInstance().getPreferenceStore();
		String pageId = BootlegPreferencePage.PAGE_ID;

		String stringValue = getOverlayedPreferenceValue(store, resource, pageId,
				BootlegPreferencePage.PREF_AUTOEXPORT);

		return StringUtil.equalsIgnoreCase(Boolean.TRUE.toString(), stringValue);

	}

	public static boolean isLoggingToFile() {
		return loggingToFile;
	}
	
	public static void startLoggingToFile() {

		logInfo("Starting Logging to File");
		
		Handler handler = BootlegActivator.getDefault().getFileHandler();		
		BOOTLEG_LOG.getLogger().addHandler(handler);
		BOOTLEG_LOG.getLogger().setLevel(Level.ALL);
		loggingToFile = true;
			
	}
	
	public static void stopLoggingToFile() {

		logInfo("Stopping Logging to File");
		
		Handler handler = BootlegActivator.getDefault().getFileHandler();		
		BOOTLEG_LOG.getLogger().removeHandler(handler);
		BOOTLEG_LOG.getLogger().setLevel(Level.INFO);
		
		loggingToFile = false;

		BootlegActivator.getDefault().closeFileHandler();

		
	}
	
	public static void logInfo(String message) {
		if (BOOTLEG_LOG.isInfoEnabled()) {
			BOOTLEG_LOG.infop("BootlegUtil", "", "Bootleg: " + message, new Object[0]);
		}
	}

	public static void logInfo(String message, Object... args) {
		if (BOOTLEG_LOG.isInfoEnabled()) {
			BOOTLEG_LOG.infop("BootlegUtil", "", "Bootleg: " + message, args);
		}
	}

	public static void logTrace(String message) {
		BOOTLEG_LOG.traceDebug("Bootleg: " + message);
	}

	public static void logError(String message) {
		BOOTLEG_LOG.error(message);
	}

	public static void addNature(IProject project) {

		BootlegUtil.logTrace("Attempt to Add Nature");

		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {

				if (BootlegNature.NATURE_ID.equals(natures[i])) {
					BootlegUtil.logInfo("Bootleg Nature already exists");
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = BootlegNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);

			logInfo("Added Bootleg Nature to " + project.getName());

		} catch (CoreException e) {

			BootlegUtil.logError(e.getMessage());
			e.printStackTrace();

		} catch (Exception e) {

			BootlegUtil.logError(e.getMessage());
			e.printStackTrace();

		}

	}

	public static void removeNature(IProject project) {

		BootlegUtil.logTrace("Attempt to Remove Nature");

		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (BootlegNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);

					logInfo("Removed Bootleg Nature from " + project.getName());

					return;
				}
			}

		} catch (CoreException e) {

			BootlegUtil.logInfo(e.getMessage());
			e.printStackTrace();

		} catch (Exception e) {

			BootlegUtil.logInfo(e.getMessage());
			e.printStackTrace();

		}
	}

	public static void addMarker(IProject project, String message, int severity) {

		try {
			IMarker marker = project.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch (CoreException e) {

		}

	}

	public static void addMarker(IResource resource, String message, int severity) {

		try {
			IMarker marker = resource.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch (CoreException e) {

		}

	}

	public static void addMarker(IFolder folder, String message, int severity) {

		try {
			IMarker marker = folder.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch (CoreException e) {

		}

	}

	public static void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	public static void cleanMarkers(IProject project) throws CoreException {

		project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);

	}

	public static void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	public static String getOverlayedPreferenceValue(IPreferenceStore store, IResource resource, String pageId,
			String name) {

		IProject project = resource.getProject();
		String value = null;
		if (useProjectSettings(project, pageId)) {
			value = getProperty(resource, pageId, name);
		}
		if (value != null)
			return value;
		return store.getString(name);

	}

	private static boolean useProjectSettings(IResource resource, String pageId) {
		String use = getProperty(resource, pageId, FieldEditorOverlayPage.USEPROJECTSETTINGS);
		return "true".equals(use);
	}

	private static String getProperty(IResource resource, String pageId, String key) {

		try {
			return resource.getPersistentProperty(new QualifiedName(pageId, key));
		} catch (CoreException e) {

		}
		return null;

	}

}
