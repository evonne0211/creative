/*
 * Created on Jun 26, 2008
 *
 */
package jPDFPrintSamples;

import java.awt.Frame;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.Vector;

import javax.print.PrintService;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.qoppa.pdf.IPassword;
import com.qoppa.pdf.PasswordDialog;
import com.qoppa.pdfPrint.PDFPrint;

public class MultiPrintApplet extends JApplet implements IPassword
{
    public void start ()
    {
        // Build the list of PDF files
        Vector pdfList = getPDFList ();
        if (pdfList != null && pdfList.size() > 0)
        {
            // Create printer job and show the printer dialog
            PrinterJob pJob = PrinterJob.getPrinterJob();
            boolean rc = pJob.printDialog();
            if (rc)
            {
                // Save the print service
                PrintService ps = pJob.getPrintService();
                
                // Loop through the PDF's creating a new printer job for each PDF
                try
                {
                    for (int urlIndex = 0; urlIndex < pdfList.size(); ++urlIndex)
                    {
                        pJob = PrinterJob.getPrinterJob();
                        pJob.setPrintService(ps);
                        
                        // Load the deocumnet and print it
                        PDFPrint pdfPrint = new PDFPrint (new URL ((String)pdfList.get (urlIndex)), this);
                        pJob.setPrintable(pdfPrint);
                        pJob.print();
                    }
                }
                catch (Throwable t)
                {
                    if (t.getMessage() != null && t.getMessage().length() > 0)
                    {
                        JOptionPane.showMessageDialog(MultiPrintApplet.this, t.getMessage());
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(MultiPrintApplet.this, "Error loading PDF document.");
                    }
                }
            }
        }
        
        // Redirect Page
        String redirectURL = getParameter("RedirectURL");
        if (redirectURL != null && redirectURL.trim().length() > 0)
        {
            try
            {
                getAppletContext().showDocument (new URL (redirectURL));
            }
            catch (Throwable t)
            {
                if (t.getMessage() != null && t.getMessage().length() > 0)
                {
                    JOptionPane.showMessageDialog(MultiPrintApplet.this, t.getMessage());
                }
                else
                {
                    JOptionPane.showMessageDialog(MultiPrintApplet.this, "Error redirecting browser to " + redirectURL);
                }
            }
        }
    }
    
    private Vector getPDFList ()
    {
        Vector pdfList = new Vector();
        
        // Loop until we stop find url parameters
        int urlIndex = 1;
        String url = getParameter("url" + urlIndex);
        while (url != null)
        {
            if (url.trim().length() > 0)
            {
                pdfList.add (url.trim());
            }
            ++urlIndex;
            url = getParameter("url" + urlIndex);
        }
        
        return pdfList;
    }
    
    public String [] getPasswords()
    {
        // Create and show the password dialog
        Frame parentFrame = (Frame)SwingUtilities.windowForComponent(this);
        return PasswordDialog.showAndGetPassword(null, parentFrame);
    }
}
