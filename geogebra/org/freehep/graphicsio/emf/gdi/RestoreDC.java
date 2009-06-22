// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * RestoreDC TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: RestoreDC.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class RestoreDC extends EMFTag {

    private int savedDC = -1;

    public RestoreDC() {
        super(34, 1);
    }

    public RestoreDC(int savedDC) {
        this();
        this.savedDC = savedDC;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new RestoreDC(emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(savedDC);
    }

    public String toString() {
        return super.toString() + "\n  savedDC: " + savedDC;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.retoreDC();
    }
}
