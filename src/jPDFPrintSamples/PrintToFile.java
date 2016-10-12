/*
 * Created on Sep 25, 2009
 *
 */
package jPDFPrintSamples;

import java.io.File;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;

import com.qoppa.pdfPrint.PDFPrint;

public class PrintToFile
{

    public static void main (String [] args)
    {
        try
        {
            // Load the PDF document
            PDFPrint pdfPrint = new PDFPrint("D:\\Downloads\\55555.pdf", null);
            
            // Create attribute set to print to a file
            File outputFile = new File ("D:\\Downloads\\4567.pdf");
            PrintRequestAttributeSet attrSet = new HashPrintRequestAttributeSet (new Destination (outputFile.toURI()));

            // Send the PDF to the printer
            pdfPrint.print ("Microsoft Print to PDF", null, attrSet);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }


}
