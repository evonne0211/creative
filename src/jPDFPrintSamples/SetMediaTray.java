/*
 * Created on Sep 17, 2009
 *
 */
package jPDFPrintSamples;

import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.PrinterName;

import com.qoppa.pdf.PrintSettings;
import com.qoppa.pdfPrint.PDFPrint;

public class SetMediaTray
{
    public static void main (String [] args)
    {
        try
        {
            // Get a named printer
            PrintServiceAttributeSet pSet = new HashPrintServiceAttributeSet ();
            pSet.add (new PrinterName ("Printer Name", null));
            PrintService [] pServices = PrintServiceLookup.lookupPrintServices (null, pSet);
            if (pServices.length > 0)
            {
                // Get media tray
                MediaTray mt = getMediaTray (pServices [0], "Front Tray");
                if (mt != null)
                {
                    // Get the printer job
                    PrinterJob pJob = PrinterJob.getPrinterJob();
                    pJob.setPrintService(pServices [0]);
                    
                    // Attribute set
                    PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet (mt);
                    
                    // PDFPrint
                    PDFPrint pdfPrint = new PDFPrint("input.pdf", null);
                    pdfPrint.setPrintSettings(new PrintSettings (false, false, false, false));
                    
                    // print
                    pJob.setPrintable (pdfPrint);
                    pJob.print(attrs);
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    private static MediaTray getMediaTray (PrintService ps, String trayName)
    {
        // Get supported media
        Media [] media = (Media [])ps.getSupportedAttributeValues(Media.class, null, null); 
        for (int count = 0; count < media.length; ++count) 
        {
            if (media [count] instanceof MediaTray)
            {
                if (trayName.equals (media [count].toString()))
                {
                    return (MediaTray)media [count];
                }
            }
        }
        
        return null;
    }
}
