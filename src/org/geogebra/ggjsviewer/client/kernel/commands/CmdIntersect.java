package org.geogebra.ggjsviewer.client.kernel.commands;

import org.geogebra.ggjsviewer.client.kernel.GeoConic;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoList;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Command;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;
import org.geogebra.ggjsviewer.client.main.MyError;






/*
 * Intersect[ <GeoLine>, <GeoLine> ] Intersect[ <GeoLine>, <GeoConic> ]
 * Intersect[ <GeoConic>, <GeoLine> ] Intersect[ <GeoConic>, <GeoConic> ]
 * Intersect[ <GeoFunction>, <GeoFunction> ] Intersect[ <GeoFunction>, <GeoLine> ]
 */
public class CmdIntersect extends CommandProcessor {
	
	public CmdIntersect(Kernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            // Line - Line
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLines(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }
            // Line - Conic
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[1],
                    (GeoConic) arg[0]);
            // Line - Cubic
            /*else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoCubic())))
				return kernel.IntersectLineCubic(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoCubic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoCubic()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.IntersectLineCubic(
                    c.getLabels(),
                    (GeoLine) arg[1],
                    (GeoCubic) arg[0]);*/
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.IntersectConics(
                    c.getLabels(),
                    (GeoConic) arg[0],
                    (GeoConic) arg[1]);
			/*AGelse if (
                (ok[0] = (arg[0].isGeoFunctionable()))
                    && (ok[1] = (arg[1].isGeoFunctionable())))
				return kernel.IntersectPolynomials(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction(),
                    ((GeoFunctionable) arg[1]).getGeoFunction());
			else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction(),
                    (GeoLine) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable())))
				return kernel.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[1]).getGeoFunction(),
                    (GeoLine) arg[0]);
			*/
			// intersection of two lists
			else if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} 
            
			else {
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else
                    throw argErr(app, "Intersect", arg[1]);
            }

        case 3 : // only one of the intersection points: the third argument
					 // states wich one
            arg = resArgs(c);
            // Line - Conic
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoConic()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Conic - Line
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[1],
                            (GeoConic) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Conic - Conic
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectConicsSingle(
                            c.getLabel(),
                            (GeoConic) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Polynomial with index of point
           /*AG else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialsSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Line with index of point
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Line - Polynomial with index of point
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Function - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctions(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoPoint) arg[2])};
                return ret;
            }
            // Function - Line with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }
            // Line - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (GeoPoint) arg[2])};
                return ret;
            }*/
            // Syntax Error
            else {
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Intersect", arg[1]);
                else
                    throw argErr(app, "Intersect", arg[2]);
            }

        default :
            throw argNumErr(app, "Intersect", n);
    }
}
}