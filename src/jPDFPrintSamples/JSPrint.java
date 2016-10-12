/*
 * Created on Mar 25, 2010
 *
 */
package jPDFPrintSamples;

import java.awt.print.PageFormat;
import java.net.URL;
import java.security.AccessController;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JApplet;

import com.qoppa.pdf.PrintSettings;
import com.qoppa.pdfPrint.PDFPrint;

/**
 * This class can be used as an applet to be able to print PDFs from Javascript.  The applet
 * is invisible and makes a few methods accesible to Javascript to print a PDF from a URL, with or
 * without options and to show a print dialog to the end user.
 * 
 * @author Qoppa Software
 *
 */
public class JSPrint extends JApplet
{
    public void init()
    {
        setSize (0,0);
    }

    /**
     * Print a PDF document from a URL to the default printer.  This method can be called from Javascript.
     * 
     */
    public String printToDefault (final String pdfURL)
    {
        // We need to enclose this in a privileged action so that it
        // can execute properly when called from Javascript
        Object rc = AccessController.doPrivileged(new java.security.PrivilegedAction() 
        {
            public Object run()
            {
                try
                {
                    PDFPrint pdf = new PDFPrint (new URL(pdfURL), null);
                    pdf.printToDefaultPrinter(null);
                    
                    return "OK";
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    return "ERROR: " + t.getMessage();
                }
            }
        });
        
        // Return value
        return rc.toString();
    }

    /**
     * Print a PDF document from a URL.  This method first shows the standard printer dialog to let the
     * user choose a printer.  This method can be called from Javascript.
     * 
     */
    public String print (final String pdfURL)
    {
        // We need to enclose this in a privileged action so that it
        // can execute properly when called from Javascript
        Object rc = AccessController.doPrivileged(new java.security.PrivilegedAction() 
        {
            public Object run()
            {
                try
                {
                    PDFPrint pdf = new PDFPrint (new URL(pdfURL), null);
                    pdf.print(null);
                    
                    return "OK";
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    return "ERROR: " + t.getMessage();
                }
            }
        });
        
        // Return value
        return rc.toString();
    }

    /**
     * Print a PDF document from a URL to the default printer and take arguments to pass on in PrintSettings.  This method can be called from Javascript.
     * 
     */
    public String printToDefaultPS(final String pdfURL, final boolean autoRotate, final boolean shrinkToMargins, final boolean expandToMargins, final boolean centerOutput, final int orient)
    {
        // We need to enclose this in a privileged action so that it
        // can execute properly when called from Javascript
        Object rc = AccessController.doPrivileged(new java.security.PrivilegedAction() 
        {
            public Object run()
            {
                try
                {
                	// Load the document
                    PDFPrint pdf = new PDFPrint (new URL(pdfURL), null);
                    
                    // Orientation
                    HashPrintRequestAttributeSet attrSet = new HashPrintRequestAttributeSet();
                    if (orient == PageFormat.LANDSCAPE)
                    {
                    	attrSet.add(OrientationRequested.LANDSCAPE);
                    }
                    else
                    {
                    	attrSet.add(OrientationRequested.PORTRAIT);
                    }

                    // Print the document
                    pdf.print(null, new PrintSettings(autoRotate, shrinkToMargins, expandToMargins, centerOutput), attrSet);
                    
                    return "OK";
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    return "ERROR: " + t.getMessage();
                }
            }
        });
        
        // Return value
        return rc.toString();
    }
}
