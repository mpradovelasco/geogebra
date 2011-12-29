package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.algos.AlgoElementInterface;

public interface EuclidianViewInterfaceSlim extends View{

	boolean isDefault2D();
	public ArrayList<GeoPoint2> getFreeInputPoints(AlgoElementInterface algoParent);
	boolean isMoveable(GeoElement geoElement);
	int getWidth();
	int getHeight();
	double toRealWorldCoordX(double i);
	double toRealWorldCoordY(double i);
	void updateBounds();
	void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric);
	EuclidianController getEuclidianController();
	double[] getGridDistances();
	double getXmax();		
	double getYmax();
	double getXmin();
	double getYmin();
	double getXscale();
	double getYscale();
	DrawableND getDrawableND(GeoElement listElement);
	DrawableND createDrawableND(GeoElement listElement);
}
