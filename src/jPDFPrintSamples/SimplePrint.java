package jPDFPrintSamples;

import com.qoppa.pdfPrint.PDFPrint;

public class SimplePrint
{
    public static void main (String [] args)
    {
        try
        {
            PDFPrint pdfPrint = new PDFPrint("C:\\one.pdf", null);
            pdfPrint.print (null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

}
