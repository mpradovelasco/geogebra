package org.geogebra.web.full.gui.menubar;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.view.Views;
import org.geogebra.web.full.gui.view.Views.ViewType;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Timer;

/**
 * The "View" menu for the applet. For application use ViewMenuApplicationW
 * class
 */
public class ViewMenuW extends GMenuBar {

	/**
	 * Menuitem with checkbox for show algebra view
	 */
	HashMap<Integer, GCheckBoxMenuItem> items = new HashMap<>();
	/** app */
	AppW app;
	/** item for input */
	GCheckBoxMenuItem inputBarItem;
	/** item for sensor app */
	GCheckBoxMenuItem dataCollection;
	/** item for navigation bar */
	GCheckBoxMenuItem consProtNav;

	/**
	 * Constructs the "View" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ViewMenuW(AppW application) {
		super("view", application);
		this.app = application;
		if (app.isUnbundled()) {
			addStyleName("matStackPanelNoOpacity");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
	}

	/**
	 * Init actions for Refresh views, recompute objects
	 * 
	 * @param loc
	 *            localization
	 */
	protected void initRefreshActions(Localization loc) {
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				.getSafeUri().asString(), loc.getMenu("Refresh"), true), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.refreshViews();
					}
				});
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
				.getSafeUri().asString(), loc.getMenu("RecomputeAllViews"),
				true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
						app.getKernel().updateConstruction(true);
			}
		});
	}

	/**
	 * Initialize menu items
	 */
	protected void initActions() {
		for (final ViewType e : Views.getViews()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}
		addSeparator();
		for (final ViewType e : Views.getViewExtensions()) {
			if (!app.supportsView(e.getID())) {
				continue;
			}
			addToMenu(e);
		}
		Localization loc = app.getLocalization();
		inputBarItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("InputField"), true), new MenuCommand(app) {

					@Override
					public void doExecute() {
						app.persistWidthAndHeight();
						app.getArticleElement()
								.attr("showAlgebraInput", "true");

						app.setShowAlgebraInput(true, false);
						app.setInputPosition(
								app.getInputPosition() == InputPosition.algebraView
										? InputPosition.bottom
										: InputPosition.algebraView,
								true);
						app.updateSplitPanelHeight();

						app.updateCenterPanelAndViews();
						if (app.getGuiManager() != null
								&& app.getGuiManager().getLayout() != null) {
							app.getGuiManager().getLayout().getDockManager()
									.resizePanels();
						}
						inputBarItem.setSelected(app
								.getInputPosition() != InputPosition.algebraView,
								ViewMenuW.this);

						Timer timer = new Timer() {
							@Override
							public void run() {
								// false, because we have just closed the menu
								app.getGuiManager()
										.updateStyleBarPositions(false);
								app.updateCenterPanel();
							}
						};
						timer.schedule(0);
					}
				}, true, app);
		inputBarItem.setForceCheckbox(true);
		addItem(inputBarItem.getMenuItem());
		consProtNav = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("NavigationBar"), true), new MenuCommand(app) {

			@Override
			public void doExecute() {
				if (consProtNav.isSelected()) {
					app.setShowConstructionProtocolNavigation(false);
				} else {
					int id = app.getActiveEuclidianView().getViewID();
					app.setShowConstructionProtocolNavigation(true, id);
					app.getGuiManager()
							.updateCheckBoxesForShowConstructinProtocolNavigation(
									id);
				}
			}
				}, true, app);
		consProtNav.setForceCheckbox(true);
		addItem(consProtNav.getMenuItem());
		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.empty().getSafeUri().asString(), app
							.getLocalization().getMenu("Sensors"), true),
					new MenuCommand(app) {

						@Override
						public void execute() {
							app.getGuiManager().setShowView(
									!app.getGuiManager().showView(
											App.VIEW_DATA_COLLECTION),
									App.VIEW_DATA_COLLECTION);
							dataCollection.setSelected(app.getGuiManager()
									.showView(App.VIEW_DATA_COLLECTION),
									ViewMenuW.this);
							app.toggleMenu();
						}
					}, true, app);
			dataCollection.setForceCheckbox(true);
			if (!app.isExam()) {
			addItem(dataCollection.getMenuItem());
			}
		}
		addSeparator();
		initRefreshActions(loc);
		update();
	}

	private void addToMenu(final ViewType e) {
		final GCheckBoxMenuItem newItem = new GCheckBoxMenuItem(
				MainMenu.getMenuBarHtml(ImgResourceHelper.safeURI(e.getIcon()),
						app.getLocalization().getMenu(e.getKey()), true),
				true, app);
		newItem.setCommand(new MenuCommand(app) {

			@Override
			public void execute() {
				boolean shown = app.getGuiManager().showView(e.getID());

				if (e.getID() == App.VIEW_ALGEBRA && !shown) {
					app.setInputPosition(InputPosition.algebraView, true);
					((AlgebraViewW) app.getAlgebraView()).setDefaultUserWidth();
				}
				app.getGuiManager().setShowView(!shown, e.getID());
				newItem.setSelected(app.getGuiManager().showView(e.getID()),
						ViewMenuW.this);
				// reset activePerspective so that no perspective is
				// highlighted in apps picker when view is customized
				app.setActivePerspective(-1);

				Timer timer = new Timer() {
					@Override
					public void run() {
						// false, because we have just closed the menu
						app.getGuiManager().updateStyleBarPositions(false);
					}
				};
				timer.schedule(0);
			}
		});
		newItem.setForceCheckbox(true);
		items.put(e.getID(), newItem);
		addItem(newItem.getMenuItem());
	}

	/**
	 * Update menu items
	 */
	public void update() {
		for (Entry<Integer, GCheckBoxMenuItem> entry : this.items.entrySet()) {
			int viewID = entry.getKey();
			entry.getValue().setSelected(
					app.getGuiManager().showView(viewID), this);
		}
		boolean linearInput = app.showAlgebraInput()
				&& app.getInputPosition() != InputPosition.algebraView;
		inputBarItem.setSelected(linearInput, this);
		consProtNav.setSelected(app.showConsProtNavigation(), this);
		if (app.has(Feature.DATA_COLLECTION)) {
			dataCollection.setSelected(app.getGuiManager().showView(
					App.VIEW_DATA_COLLECTION), this);
		}
	}
}
