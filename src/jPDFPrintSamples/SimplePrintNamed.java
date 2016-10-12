/*
 * Created on Sep 23, 2008
 *
 */
package jPDFPrintSamples;

import com.qoppa.pdfPrint.PDFPrint;

public class SimplePrintNamed
{
    public static void main (String [] args)
    {
        try
        {
            PDFPrint pdfPrint = new PDFPrint("input.pdf", null);
            pdfPrint.printToDefaultPrinter(null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }


}
