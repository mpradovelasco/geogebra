package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;

/**
 * Spreadsheet data conversion.
 *
 *  (This class is an adapter between the Spreadsheet core and the Kernel.)
 */
public final class DefaultSpreadsheetCellDataSerializer implements SpreadsheetCellDataSerializer {

	/**
	 * @param data Spreadsheet cell content.
	 * @param hasError Whether cell has error.
	 * @return A string representation of the content suitable for a cell editor.
	 */
	@Override
	public String getStringForEditor(Object data, boolean hasError) {
		if (data instanceof String) {
			return (String) data;
		}
		if (data == null) {
			return "";
		}
		GeoElement geo = (GeoElement) data;
		String redefineString = geo.getRedefineString(true, false);
		return geo.isGeoText() && !hasError ? redefineString : "=" + redefineString;
	}
}
