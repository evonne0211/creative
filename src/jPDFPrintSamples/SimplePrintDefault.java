/*
 * Created on Sep 23, 2008
 *
 */
package jPDFPrintSamples;

import com.qoppa.pdfPrint.PDFPrint;

public class SimplePrintDefault
{
    public static void main (String [] args)
    {
        try
        {
            PDFPrint pdfPrint = new PDFPrint("input.pdf", null);
            pdfPrint.print ("my printer", null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }


}
