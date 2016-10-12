/*
 * Created on Feb 29, 2008
 *
 */
package jPDFPrintSamples;

import java.awt.print.PrinterJob;

import javax.print.PrintService;

public class ListPrinters
{
    public static void main (String [] args)
    {
        // List printers
        PrintService [] ps = PrinterJob.lookupPrintServices();
        for (int count = 0; count < ps.length; ++count)
        {
            System.out.println (ps [count].getName());
        }
        System.out.println ();
    }
}
