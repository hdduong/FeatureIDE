/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.core.mpl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.mpl.io.FileLoader;
import de.ovgu.featureide.core.mpl.job.AChainJob;
import de.ovgu.featureide.core.mpl.job.FujiJob;
import de.ovgu.featureide.core.mpl.signature.ProjectSignatures;
import de.ovgu.featureide.core.mpl.signature.ViewTag;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;

/**
 * Holds all relevant information about the interface project.
 * 
 * @author Sebastian Krieter
 * @author Reimar Schroeter
 */
public class InterfaceProject {
	private final IProject projectReference;
	private final IFeatureProject featureProject;
	
	private ProjectSignatures projectSignatures = null;
	
//	private final ViewTagPool viewTagPool = new ViewTagPool();
//	private final AbstractStringProvider stringProvider = new JavaStringProvider();
	
	private ViewTag filterViewTag = null;
	private int configLimit = 1000;
	
	private Configuration configuration;

	private final FeatureModel featureModel;
	private final String[] featureNames;
	
	private AChainJob lastJob = null;
	private FujiJob loadJob = null;
	private boolean loadAgain = false;
	
	public synchronized void addJob(AChainJob newJob) {
		if (lastJob == null) {
			lastJob = newJob;
			lastJob.schedule();
		} else {
			lastJob.setNextJob(newJob);
			lastJob = newJob;
		}
	}
	
	public InterfaceProject(IFeatureProject featureProject) {
		this(null, featureProject);
	}
	
	public InterfaceProject(IProject projectReference) {
		this(projectReference, null);
	}
	
	private class FeaturePropertyChangeListener implements PropertyChangeListener {
		private final int id;
		
		public FeaturePropertyChangeListener(int id) {
			 this.id = id;
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			String prop = event.getPropertyName();
			if (Feature.LOCATION_CHANGED.equals(prop)) {
				
			} else if (Feature.CHILDREN_CHANGED.equals(prop)) {
				
			} else if (Feature.NAME_CHANGED.equals(prop)) {
				featureNames[id] = ((Feature)event.getSource()).getName();
			} else if (Feature.ATTRIBUTE_CHANGED.equals(prop)) {
				
			}
		}
	}
	
	public InterfaceProject(IProject projectReference, IFeatureProject featureProject) {
		this.projectReference = projectReference;
		this.featureProject = featureProject;
		
		if (featureProject != null) {
			featureModel = FileLoader.loadFeatureModel(featureProject.getProject());
		} else if (projectReference != null) {
			featureModel = FileLoader.loadFeatureModel(projectReference);
		} else {
			featureModel = null;
		}
		if (featureModel != null) {
			featureNames = new String[featureModel.getNumberOfFeatures()];
			int i = 0;
			Collection<Entry<String, Feature>> x = featureModel.getFeatureTable().entrySet();
			for (Entry<String, Feature> entry : x) {
				entry.getValue().addListener(new FeaturePropertyChangeListener(i));
				featureNames[i++] = entry.getKey();
			}
//			Arrays.sort(featureNames);
		} else {
			featureNames = null;
		}
	}
	public int[] getFeatureIDs(Collection<String> featureNames) {
		int[] ids = new int[featureNames.size()];
		int i = -1;
		for (String featureName : featureNames) {
			ids[++i] = getFeatureID(featureName);
		}
		return ids;
	}

	
	public int getFeatureID(String featureName) {
		for (int i = 0; i < featureNames.length; ++i) {
			if (featureNames[i].equals(featureName)) {
				return i;
			}
		}
		return -1;
//		return Arrays.binarySearch(featureNames, featureName);
	}
	
	public String getFeatureName(int id) {
		return featureNames[id];
	}
	
	public void loadSignatures(boolean again) {
		if (loadJob == null) {
			loadJob = new FujiJob(featureProject);
			loadJob.setInterfaceProject(this);
			loadJob.schedule();
		} else if (again) {
			loadAgain = true;
		}
	}
	
	public synchronized void loadSignaturesJob(boolean force) {
		if (projectSignatures == null || force) {
			FujiJob job = new FujiJob(featureProject);
			job.setInterfaceProject(this);
			addJob(job);
		}
	}
	
	public ProjectSignatures getProjectSignatures() {
		return projectSignatures;
	}

	public IProject getProjectReference() {
		return projectReference;
	}
	
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public int getConfigLimit() {
		return configLimit;
	}
	
	public Configuration getConfiguration() {
		if (configuration == null) {
			configuration = FileLoader.loadConfiguration(this);
		}
		return configuration;
	}
	
	public ViewTag getFilterViewTag() {
		return filterViewTag;
	}
	
//	public ViewTagPool getViewTagPool() {
//		return viewTagPool;
//	}

//	public AbstractStringProvider getStringProvider() {
//		return stringProvider;
//	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setProjectSignatures(ProjectSignatures projectSignatures) {
		this.projectSignatures = projectSignatures;
		loadJob = null;
		if (loadAgain) {
			loadSignatures(false);
			loadAgain = false;
		}
	}

	public void setConfigLimit(int configLimit) {
		this.configLimit = configLimit;
	}
	
	public void setFilterViewTag(ViewTag filterViewTag) {
		this.filterViewTag = filterViewTag;
	}
	
	public void setFilterViewTag(String viewName, int viewLevel) {
		if (viewName != null) {
			this.filterViewTag = new ViewTag(viewName, viewLevel);
		}
	}
	
	public void clearFilterViewTag() {
		this.filterViewTag = null;
	}

	public IFeatureProject getFeatureProjectReference() {
		return featureProject;
	}
	
//	public void scaleUpViewTag(String name, int level) {
//		viewTagPool.scaleUpViewTags(name, level);
//	}
//	
//	public void removeViewTag(String name) {
//		viewTagPool.removeViewTag(name);
//	}
}
