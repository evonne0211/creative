package jPDFPrintSamples;

import java.awt.print.PrinterJob;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

import com.qoppa.pdfPrint.PDFPrint;

public class PrintJobListenerSample implements PrintJobListener
{
	public static void main (String [] args)
	{
		try
		{
			// Create a printer job and get its PrintService
			PrinterJob pJob = PrinterJob.getPrinterJob();
			PrintService ps = pJob.getPrintService();
			
			// Load the PDF document and create SimpleDoc from it
			PDFPrint pdfPrint = new PDFPrint ("input.pdf", null);
			SimpleDoc printDoc = new SimpleDoc (pdfPrint, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

			// Get a doc print job from the service and set the listener and Printable 
			DocPrintJob docPrint = ps.createPrintJob();
			docPrint.addPrintJobListener(new PrintJobListenerSample());
			docPrint.print (printDoc, null);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	public void printDataTransferCompleted(PrintJobEvent pje) 
	{
		System.out.println ("printDataTransferCompleted");
	}
	
	public void printJobCanceled(PrintJobEvent pje)
	{
		System.out.println ("printJobCanceled");
	}
	
	public void printJobCompleted(PrintJobEvent pje) 
	{
		System.out.println ("printJobCompleted");
	}
	
	public void printJobFailed(PrintJobEvent pje) 
	{
		System.out.println ("printJobFailed");
	}
	
	public void printJobNoMoreEvents(PrintJobEvent pje) 
	{
		System.out.println ("printJobNoMoreEvents");
	}
	
	public void printJobRequiresAttention(PrintJobEvent pje) 
	{
		System.out.println ("printJobRequiresAttention");
	}
}
