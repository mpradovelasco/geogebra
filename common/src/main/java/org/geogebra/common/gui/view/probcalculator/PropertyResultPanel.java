package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_LEFT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_RIGHT;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.AbstractResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.IntervalResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.LeftResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.RightResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.TwoTailedResultModel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class PropertyResultPanel implements ResultPanel {

	private ProbabilityCalculatorView view;
	private AbstractResultModel currentModel;

	private LeftResultModel leftResultModel;
	private RightResultModel rightResultModel;
	private TwoTailedResultModel twoTailedResultModel;
	private IntervalResultModel intervalResultModel;

	public PropertyResultPanel(ProbabilityCalculatorView view, Localization localization) {
		this.view = view;
		leftResultModel = new LeftResultModel(localization);
		rightResultModel = new RightResultModel(localization);
		twoTailedResultModel = new TwoTailedResultModel(localization);
		intervalResultModel = new IntervalResultModel(localization);

		currentModel = leftResultModel;
	}

	public ResultModel getModel() {
		return currentModel;
	}

	@Override
	public void showInterval() {
		currentModel = intervalResultModel;
	}

	@Override
	public void showTwoTailed() {
		currentModel = twoTailedResultModel;
	}

	@Override
	public void showTwoTailedOnePoint() {
		showTwoTailed();
		twoTailedResultModel.setGreaterThan();
	}

	@Override
	public void showLeft() {
		currentModel = leftResultModel;
	}

	@Override
	public void showRight() {
		currentModel = rightResultModel;
	}

	@Override
	public void setResultEditable(boolean value) {
		// models know whether they should have editable results
	}

	@Override
	public void updateResult(String text) {
		currentModel.setResult(text);
	}

	@Override
	public void updateLowHigh(String low, String high) {
		currentModel.setLow(low);
		currentModel.setHigh(high);
	}

	@Override
	public void updateTwoTailedResult(String low, String high) {
		// the model updates the two tailed result when values change
	}

	@Override
	public boolean isFieldLow(Object source) {
		return false;
	}

	@Override
	public boolean isFieldHigh(Object source) {
		return false;
	}

	@Override
	public boolean isFieldResult(Object source) {
		return false;
	}

	@Override
	public void setGreaterThan() {
		twoTailedResultModel.setGreaterThan();
	}

	@Override
	public void setGreaterOrEqualThan() {
		twoTailedResultModel.setGreaterThanOrEqualTo();
	}

	public void setValue(EditableResultEntry entry, GeoNumberValue value) {
		double numberValue = value.getDouble();
		if (currentModel.getLow() == entry) {
			if (view.isValidInterval(numberValue, view.getHigh())) {
				view.setLow(value);
				view.setXAxisPoints();
			} else {
				view.updateGUI();
			}
		} else if (currentModel.getHigh() == entry) {
			if (view.isValidInterval(view.getLow(), numberValue)) {
				view.setHigh(value);
				view.setXAxisPoints();
			} else {
				view.updateGUI();
			}
		} else if (currentModel.getResult() == entry) {
			if (numberValue < 0 || numberValue > 1) {
				view.updateGUI();
			} else {
				int probMode = view.getProbMode();
				if (probMode == PROB_LEFT) {
					view.setHigh(view.inverseProbability(numberValue));
				}
				if (probMode == PROB_RIGHT) {
					view.setLow(view.inverseProbability(1 - numberValue));
				}
				view.setXAxisPoints();
			}
		} else {
			Log.warn("Unknown result entry, ignoring.");
		}
	}
}
