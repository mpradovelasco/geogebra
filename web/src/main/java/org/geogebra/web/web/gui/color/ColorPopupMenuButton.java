package org.geogebra.web.web.gui.color;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * Color popup for stylebar
 *
 */
public class ColorPopupMenuButton extends PopupMenuButtonW
		implements ClickHandler {
	/** foreground */
	public static final int COLORSET_DEFAULT = 0;
	/** background */
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private GColor[] colorSet;
	private GColor defaultColor;
	private HashMap<String, Integer> lookupMap;
	private Label titleLabel;

	private boolean enableTable;
	private boolean hasSlider;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param colorSetType
	 *            {@code int}
	 * @param hasSlider
	 *            {@code boolean}
	 */
	public ColorPopupMenuButton(AppW app, int colorSetType, boolean hasSlider) {

		super(app, createDummyIcons(app.isUnbundled() ? 8 : 10), -1,
				app.isUnbundled() ? 4 : 5,
				SelectionTable.MODE_ICON);
		this.app = app;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;

		setColors();
		defaultColor = colorSet[0];

		lookupMap = new HashMap<String, Integer>();
		for (int i = 0; i < colorSet.length; i++) {
			if (colorSet[i] != null) {
				lookupMap.put(GColor.getColorString(colorSet[i]), i);
			}
		}

		getMySlider().setMinimum(0);
		getMySlider().setMaximum(100);
		getMySlider().setMajorTickSpacing(25);
		getMySlider().setMinorTickSpacing(5);
		setSliderValue(100);
		setSliderVisible(hasSlider);
		if (app.has(Feature.COLOR_FILLING_LINE) && hasSlider) {
			titleLabel = new Label(app.getLocalization().getMenu("Opacity"));
			titleLabel.addStyleName("opacityLabel");
			sliderPanel.insert(titleLabel, 0);
		}

		updateColorTable();
		setKeepVisible(false);
		getMyTable().removeDefaultStyle();
	}

	/**
	 * Sets the colors to choose from.
	 */
	protected void setColors() {
		if (app.isWhiteboardActive()) {
			colorSet = GeoGebraColorConstants.getMOWPopupArray();
		} else {
			colorSet = app.isUnbundled()
					? GeoGebraColorConstants.getUnbundledPopupArray()
					: GeoGebraColorConstants.getSimplePopupArray(colorSetType);
		}
	}
	/**
	 * @param visible
	 *            {@code boolean}
	 */
	protected void setSliderVisible(boolean visible) {
		hasSlider = visible;
		showSlider(hasSlider);
		if (app.has(Feature.COLOR_FILLING_LINE) && titleLabel != null) {
			titleLabel.setVisible(hasSlider);
			if (!hasSlider) {
				getMyPopup().setHeight("88px");
			}
			if (hasSlider) {
				getMyPopup().setHeight("150px");
			}

		} else {
			if (!hasSlider && app.isUnbundled()) {
				getMyPopup().setHeight("88px");
			}
			if (hasSlider && app.isUnbundled()) {
				getMyPopup().setHeight("118px");
			}
			if (!app.has(Feature.COLORPOPUP_IMPROVEMENTS)) {
				if (!hasSlider && app.isWhiteboardActive()) {
					getMyPopup().setHeight("38px");
				}
			}
		}
	}

	/**
	 * Update the table
	 */
	protected void updateColorTable() {
		getMyTable().populateModel(
				getColorSwatchIcons(colorSet, getSliderValue() / 100f));
	}

	@Override
	public ImageOrText getButtonIcon() {
		ImageOrText icon = super.getButtonIcon();
		if (icon == null) {
			icon = GeoGebraIconW.createColorSwatchIcon(getSliderValue() / 100f,
					defaultColor, null);
		}
		return icon;
	}

	/**
	 * @param color
	 *            {@link GColor}
	 * @return {@code int} the index of the given color
	 */
	protected int getColorIndex(GColor color) {
		int index = -1;

		if (color == null && colorSetType == COLORSET_BGCOLOR) {
			index = colorSet.length - 1;
			return index;
		}

		if (lookupMap.containsKey(GColor.getColorString(color))) {
			index = lookupMap.get(GColor.getColorString(color));
		}

		return index;
	}

	/**
	 * @return the selected {@link GColor color}
	 */
	public GColor getSelectedColor() {
		if (!enableTable) {
			return null;
		}
		int index = getSelectedIndex();
		if (index <= -1) {
			return defaultColor;
		} else if (colorSetType == COLORSET_BGCOLOR
				&& index > colorSet.length - 1) {
			return null;
		} else {
			return colorSet[index];
		}
	}

	/**
	 * @param alpha
	 *            opacity
	 * @param gc
	 *            base color
	 */
	protected void setDefaultColor(double alpha, GColor gc) {
		defaultColor = gc;
		if (gc != null) {
			if (app.has(Feature.COLOR_FILLING_LINE)) {
				this.setIcon(
						GeoGebraIconW.createColorSwatchIcon(1.0, gc, gc));
			} else {
				this.setIcon(
						GeoGebraIconW.createColorSwatchIcon(alpha, gc, null));
			}
			this.getElement().getStyle().setBorderColor(gc.toString());
		} else {
			this.setIcon(GeoGebraIconW.createNullSymbolIcon());
			this.getElement().getStyle()
					.setBorderColor(this.app.isUnbundled()
							? GColor.newColor(220, 220, 220, 255).toString()
							: GColor.BLACK.toString());
		}
	}

	private static ImageOrText[] getColorSwatchIcons(GColor[] colorArray,
			double alpha) {
		ImageOrText[] a = new ImageOrText[colorArray.length];
		for (int i = 0; i < colorArray.length; i++) {
			if (colorArray[i] != null) {
				a[i] = GeoGebraIconW.createColorSwatchIcon(alpha, colorArray[i],
						null);
			} else {
				a[i] = new ImageOrText(
						MaterialDesignResources.INSTANCE.add_black())
								.setClass("plusButton");
				// a[i] = new ImageOrText("+");
				// a[i] = new ImageOrText(AppResources.INSTANCE.more());
			}
		}
		return a;
	}

	private static ImageOrText[] createDummyIcons(int count) {

		ImageOrText[] a = new ImageOrText[count];
		for (int i = 0; i < count; i++) {
			a[i] = new ImageOrText();
		}
		return a;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();
			updateColorTable();
			setSelectedIndex(si);
		}
	}

	@Override
	protected void fireActionPerformed() {
		if (this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();
			updateColorTable();
			setSelectedIndex(si);
		}
	}

	/**
	 * @return whether table is enabled (otherwise just slider)
	 */
	public boolean isEnableTable() {
		return enableTable;
	}

	/**
	 * @param enableTable
	 *            whether table is enabled (otherwise just slider)
	 */
	public void setEnableTable(boolean enableTable) {
		this.enableTable = enableTable;
		getMyTable().setVisible(enableTable);
		if (!enableTable && app.isUnbundled()) {
			getMyPopup().setHeight("30px");
		}
	}

	public GColor[] getColorSet() {
		return colorSet;
	}

	public void setColorSet(GColor[] colorSet) {
		this.colorSet = colorSet;
	}

	@Override
	protected String getSliderPostfix() {
		if (app.has(Feature.COLOR_FILLING_LINE)) {
			return " %";
		}
		return "";
	}
}
