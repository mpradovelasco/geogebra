package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for graphing 3D app
 */
public class Graphing3DActivity extends BaseActivity {

	/**
	 * Graphing 3D activity
	 */
	public Graphing3DActivity() {
		super(new AppConfigGraphing3D());
	}

	@Override
	public SVGResource getIcon() {
		return SvgPerspectiveResources.INSTANCE.menu_icon_graphics3D_transparent();
	}

}
