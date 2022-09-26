package org.geogebra.common.util;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.util.debug.Log;

public class SymbolicUtil {

	/**
	 * Check if symbolic is Solve/NSolve command
	 *
	 * @param symbolic
	 *        GeoSymbolic input
	 * @return true if symbolic is solve command
	 */
	public static boolean isSymbolicSolve(GeoSymbolic symbolic) {
		Command topLevelCommand = symbolic.getDefinition().getTopLevelCommand();
		return topLevelCommand != null
				&& (Commands.Solve.getCommand().equals(topLevelCommand.getName())
				|| Commands.NSolve.getCommand().equals(topLevelCommand.getName()));
	}

	public static boolean isNumericOfSolve(GeoSymbolic symbolic) {
		boolean isNumeric = Commands.Numeric.getCommand()
				.equals(symbolic.getDefinition().getTopLevelCommand().getName());
		Command secondCommand = (Command)(((Command) symbolic.getDefinition()
				.getLeft()).getArgument(0).getLeft());
		boolean hasSolveArgument = Commands.Solve.getCommand()
				.equals(secondCommand.getName());
		return isNumeric && hasSolveArgument;
	}

	/**
	 * Check if Solve and NSolve give different outputs
	 *
	 * @param symbolic
	 *        GeoSymbolic input
	 * @return true if outputs of symbolic are different
	 *
	 */
	public static boolean isSymbolicSolveDiffers(GeoSymbolic symbolic) {
		String textOriginal = getValueString(symbolic);
		String textOpposite = getOppositeValueString(symbolic);
		return isDefined(textOriginal) && isDefined(textOpposite)
				&& !textOriginal.equals(textOpposite);
	}

	private static String getValueString(GeoSymbolic symbolic) {
		return symbolic.toValueString(StringTemplate.defaultTemplate);
	}

	private static boolean isDefined(String valueString) {
		return !GeoFunction.isUndefined(valueString);
	}

	private static GeoSymbolic getOpposite(GeoSymbolic symbolic) {
		GeoSymbolic opposite = new GeoSymbolic(symbolic.getConstruction());
		opposite.setDefinition(symbolic.getDefinition().deepCopy(symbolic.getKernel()));
		toggleNumericSolve(opposite);
		return opposite;
	}

	private static String getOppositeValueString(GeoSymbolic symbolic) {
		return getValueString(getOpposite(symbolic));
	}

	/**
	 * Handles the showing/hiding of Solve/NSolve variants
	 * @param symbolic GeoSymbolic input
	 *
	 */
	public static void handleSolveNSolve(GeoSymbolic symbolic) {
		if (isSymbolicSolve(symbolic)) {
			if (!isDefined(getValueString(symbolic))
					&& isDefined(getOppositeValueString(symbolic))) {
				toggleNumericSolve(symbolic);
				if (Commands.Solve.name()
						.equals(symbolic.getDefinition().getTopLevelCommand().getName())) {
					symbolic.setWrapInNumeric(true);
				}
			}
		}
	}

	/**
	 * Toggles between symbolic and numeric versions of Solve
	 *
	 * @param symbolic
	 *            GeoSymbolic that we want to change
	 */
	public static void toggleNumericSolve(GeoSymbolic symbolic) {
		Commands opposite = Commands.NSolve.getCommand()
				.equals(symbolic.getDefinition().getTopLevelCommand().getName())
				? Commands.Solve : Commands.NSolve;

		symbolic.getDefinition().getTopLevelCommand().setName(opposite.getCommand());
		symbolic.computeOutput();
	}

	public static void toggleNumericWrap(GeoSymbolic symbolic) {
		boolean isNumeric = Commands.Numeric.getCommand()
				.equals(symbolic.getDefinition().getTopLevelCommand().getName());
		if (isNumeric) {
			unwrapFromNumeric(symbolic);
		} else {
			wrapInNumeric(symbolic);
		}
	}

	private static void wrapInNumeric(GeoSymbolic symbolic) {
		Command numeric = new Command(symbolic.getKernel(), "Numeric", false);
		numeric.addArgument(symbolic.getDefinition().deepCopy(symbolic.getKernel()));
		symbolic.setDefinition(numeric.wrap());
		symbolic.computeOutput();
	}

	private static void unwrapFromNumeric(GeoSymbolic symbolic) {
		symbolic.setDefinition(((Command) (symbolic.getDefinition().getLeft())).getArgument(0));
		symbolic.computeOutput();
	}

	/**
	 * Changes the symbolic flag of a geo or its parent algo
	 *
	 * @param geo
	 *            element that we want to change
	 * @return whether it's symbolic after toggle
	 */
	public static boolean toggleSymbolic(GeoElement geo) {
		if (geo instanceof HasSymbolicMode) {
			if (geo.getParentAlgorithm() instanceof AlgoSolve) {
				return !((AlgoSolve) geo.getParentAlgorithm()).toggleNumeric();
			}
			((HasSymbolicMode) geo).setSymbolicMode(
					!((HasSymbolicMode) geo).isSymbolicMode(), true);

			if (geo instanceof GeoSymbolic) {
				GeoSymbolic symbolic = (GeoSymbolic) geo;
				if (isSymbolicSolve(symbolic) || isNumericOfSolve(symbolic)) {
					if (symbolic.shouldWrapInNumeric()) {
						toggleNumericWrap(symbolic);
					} else {
						toggleNumericSolve(symbolic);
					}
					symbolic.setDescriptionNeedsUpdateInAV(true);
				}
			}

			geo.updateRepaint();
			return ((HasSymbolicMode) geo).isSymbolicMode();

		}
		return false;
	}
}
