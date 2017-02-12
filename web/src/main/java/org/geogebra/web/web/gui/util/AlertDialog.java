package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author gabor
 *
 *	General Alert Dialog
 */
public class AlertDialog extends DialogBoxW {
	
	/**
	 * Creates an AlertDialog
	 */
	
	private VerticalPanel container;
	private Label msg;
	private Button ok;
	
	/**
	 * @param app
	 *            application
	 */
	public AlertDialog(App app) {
		super(((AppW) app).getPanel());
		if (app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
			setOverlapFeature(true);
		}
		setWidget(container = new VerticalPanel());
		HorizontalPanel textPanel = new HorizontalPanel();
		textPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		textPanel.add(msg = new Label(""));
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(ok = new Button(app.getLocalization().getMenu("OK")));
		buttonPanel.addStyleName("buttonPanel");
		final AlertDialog _this = this;
		ok.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_this.hide();
			}
		});
		container.add(textPanel);
		container.add(buttonPanel);
		addStyleName("GeoGebraFileChooser");
		center();
	}
	
	/**
	 * @param text text
	 * @return sets the text of the dialog
	 */
	public AlertDialog get(String text) {
		msg.setText(text);
		return this;		
	}

}
