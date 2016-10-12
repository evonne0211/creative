/*
 * Created on Jan 2, 2007
 *
 */
package jPDFPrintSamples;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import com.qoppa.pdf.PDFException;
import com.qoppa.pdfPrint.PDFPrint;

/**
 * This sample class takes a list of PDFPrint objects and implements the Pageable
 * interface such that it will print all the PDF files in the list.
 * 
 * @author Qoppa Software
 *
 */
public class MultiPrintPageable implements Pageable, Printable
{
    private Vector m_URLList;
    private Vector m_PDFList;
    private PrinterJob m_PrinterJob;
    
    public static void main (String [] args)
    {
        Vector urlList = new Vector();
        urlList.add ("http://www.qoppa.com/demo/sample01.pdf");
        urlList.add ("http://www.qoppa.com/demo/sample02.pdf");
        urlList.add ("http://www.qoppa.com/demo/sample03.pdf");
        urlList.add ("http://www.qoppa.com/demo/sample04.pdf");
        
        try
        {
        	
            MultiPrintPageable multiPrint = new MultiPrintPageable (urlList);
            multiPrint.printAll();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
    private static class PDFPrintInfo
    {
        public PDFPrint m_PDFPrint;
        public int m_PageOffset;
        
        PDFPrintInfo (PDFPrint pdfPrint, int pageOffset)
        {
            m_PDFPrint = pdfPrint;
            m_PageOffset = pageOffset;
        }
    }

    public MultiPrintPageable (Vector urlList)
    {
        m_URLList = urlList;
    }
    
    public void printAll () throws PDFException, MalformedURLException, PrinterException
    {
        // Load the documents
        m_PDFList = new Vector();
        for (int count = 0; count < m_URLList.size(); ++count)
        {
            // Add the next document to the list
            PDFPrint oneDocument = new PDFPrint (new URL ((String)m_URLList.get (count)), null);
            m_PDFList.add (oneDocument);
        }
        
        // Create the printer job and show the print dialog
        m_PrinterJob = PrinterJob.getPrinterJob();
        if (m_PrinterJob.printDialog())
        {
            m_PrinterJob.setPageable(this);
            m_PrinterJob.print();
        }
    }
    
    public int getNumberOfPages()
    {
        int totalPages = 0;
        for (int count = 0; count < m_PDFList.size(); ++count)
        {
            totalPages += ((PDFPrint)m_PDFList.get (count)).getPageCount();
        }

        return totalPages;
    }
    
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
    {
        PDFPrintInfo info = getPDFPrint(pageIndex);
        if (info != null)
        {
            return info.m_PDFPrint.getPageable(m_PrinterJob).getPageFormat(pageIndex - info.m_PageOffset);
        }
        
        return null;
    }
    
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
    {
        return this;
    }
    
    private PDFPrintInfo getPDFPrint (int pageIndex)
    {
        int pageOffset = 0;
        for (int count = 0; count < m_PDFList.size(); ++count)
        {
            PDFPrint currentPDF = (PDFPrint)m_PDFList.get (count); 
            if (pageIndex < pageOffset + currentPDF.getPageCount())
            {
                return new PDFPrintInfo(currentPDF, pageOffset);                
            }
            pageOffset += currentPDF.getPageCount();
        }
        
        return null;
    }
    
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
    {
        PDFPrintInfo info = getPDFPrint(pageIndex);
        if (info != null)
        {
            info.m_PDFPrint.print (graphics, pageFormat, pageIndex - info.m_PageOffset);
            return Printable.PAGE_EXISTS;
        }
        else
        {
            return Printable.NO_SUCH_PAGE;
        }
    }
}
