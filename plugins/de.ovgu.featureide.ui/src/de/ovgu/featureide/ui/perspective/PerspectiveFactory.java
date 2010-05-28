/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2010  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ovgu.featureide.fm.ui.views.FeatureModelEditView;
import de.ovgu.featureide.ui.UIPlugin;
import de.ovgu.featureide.ui.ahead.actions.StartJakFileWizard;
import de.ovgu.featureide.ui.ahead.wizards.NewJakFileWizard;
import de.ovgu.featureide.ui.views.collaboration.CollaborationView;
import de.ovgu.featureide.ui.wizards.NewEquationFileWizard;
import de.ovgu.featureide.ui.wizards.NewFeatureProjectWizard;

/**
 * The factory for the FeatureIDE perspective.
 * 
 * @author Christian Becker
 * @author Thomas Thuem
 */
public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = UIPlugin.PLUGIN_ID
			+ ".FeatureIDEperspective";

	private IPageLayout layout;

	@SuppressWarnings("deprecation")
	public void createInitialLayout(IPageLayout layout) {
		this.layout = layout;
		String editorArea = layout.getEditorArea();

		layout.addNewWizardShortcut(NewEquationFileWizard.ID);
		layout.addNewWizardShortcut(NewFeatureProjectWizard.ID);
		layout.addNewWizardShortcut(NewJakFileWizard.ID);
		// layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.25f,
		// editorArea);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				(float) 0.23, editorArea);
		IFolderLayout down = layout.createFolder("down", IPageLayout.BOTTOM,
				(float) 0.80, editorArea);
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				(float) 0.75, editorArea);

		down.addView(IPageLayout.ID_PROBLEM_VIEW);
		down.addView("org.eclipse.ui.console.ConsoleView");
		down.addView("de.ovgu.featureide.ui.views.collaboration.Collaboration");
		down.addView("de.ovgu.featureide.fm.ui.views.FeatureModelEditView");

		right.addView(IPageLayout.ID_OUTLINE);

		left.addView("org.eclipse.jdt.ui.PackageExplorer");
		// left.addView(IPageLayout.ID_RES_NAV);

		layout.addShowViewShortcut(FeatureModelEditView.ID);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		// layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(CollaborationView.ID);

		addActions();
	}

	private void addActions() {
		layout.addActionSet(StartJakFileWizard.ID);
		layout.addActionSet(UIPlugin.PLUGIN_ID + ".NewFiles");
		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
	}
}