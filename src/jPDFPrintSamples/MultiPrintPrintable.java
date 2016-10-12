package jPDFPrintSamples;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Vector;

import com.qoppa.pdf.PDFException;
import com.qoppa.pdfPrint.PDFPrint;

/**
 * This sample class takes a list of file names and implements the Printable
 * interface such that it will print all the PDF files in the list in the same
 * PrinterJob. Each file is loaded on demand and only one document is open at
 * any time.
 * 
 * @author Qoppa Software
 * 
 */
public class MultiPrintPrintable implements Printable
{
	// List of file names to print
	private Vector m_FileNames;

	// Current PDFPrint
	private PDFPrint m_CurrentDoc;

	// Variables to track position
	private int m_DocIndex = 0;
	private int m_PageOffset = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Vector fileNames = new Vector();
		fileNames.add("sample01.pdf");
		fileNames.add("sample02.pdf");
		fileNames.add("sample03.pdf");
		fileNames.add("sample04.pdf");

		try
		{
			MultiPrintPrintable multiPrint = new MultiPrintPrintable(fileNames);
			multiPrint.printAll();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	public MultiPrintPrintable(Vector fileNames)
	{
		m_FileNames = fileNames;
	}

	private void printAll() throws PrinterException
	{
		// Create the printer job and show the print dialog
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerJob.printDialog())
		{
			printerJob.setPrintable(this);
			printerJob.print();
		}
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
	{
		if (m_CurrentDoc == null)
		{
			// Load first document
			m_CurrentDoc = loadDocument(m_DocIndex++);
		}
		else if (m_CurrentDoc.getPageCount() <= pageIndex - m_PageOffset)
		{
			// Increment page offset
			m_PageOffset += m_CurrentDoc.getPageCount();

			// Close the document
			m_CurrentDoc.close();

			// Load the next document
			m_CurrentDoc = loadDocument(m_DocIndex++);
		}

		if (m_CurrentDoc != null)
		{
			return m_CurrentDoc.print(graphics, pageFormat, pageIndex - m_PageOffset);
		}
		else
		{
			return Printable.NO_SUCH_PAGE;
		}
	}

	private PDFPrint loadDocument(int index)
	{
		if (index < m_FileNames.size())
		{
			try
			{
				return new PDFPrint((String) m_FileNames.get(index), null);
			}
			catch (PDFException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}
