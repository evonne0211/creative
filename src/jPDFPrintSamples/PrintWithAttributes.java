/*
 * Created on Nov 16, 2009
 *
 */
package jPDFPrintSamples;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.PrintQuality;

import com.qoppa.pdfPrint.PDFPrint;

public class PrintWithAttributes
{
    public static void main (String [] args)
    {
        try
        {
            // Load the PDF document
            PDFPrint pdfPrint = new PDFPrint("input.pdf", null);
            
            // Create attribute set to print to a file
            HashPrintRequestAttributeSet attrSet = new HashPrintRequestAttributeSet();
            attrSet.add(PrintQuality.HIGH);
            attrSet.add(Chromaticity.MONOCHROME);

            // Send the PDF to the printer
            pdfPrint.print ("printer name", null, attrSet);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }



}
