/*
 * Created on Nov 16, 2007
 *
 */
package jPDFPrintSamples;

import java.awt.Frame;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.qoppa.pdf.IPassword;
import com.qoppa.pdf.PasswordDialog;
import com.qoppa.pdfPrint.PDFPrint;

public class AutoPrintApplet extends JApplet implements IPassword
{
    public void start ()
    {
        // Get the URL of the file to load
        String url = getParameter("url");
        if (url != null && url.trim ().length() > 0)
        {
            try
            {
                // Load the file
                PDFPrint pdfPrint = new PDFPrint (new URL (url), this);
                pdfPrint.print (null);
            }
            catch (Throwable t)
            {
                if (t.getMessage() != null && t.getMessage().length() > 0)
                {
                    JOptionPane.showMessageDialog(AutoPrintApplet.this, t.getMessage());
                }
                else
                {
                    JOptionPane.showMessageDialog(AutoPrintApplet.this, "Error loading PDF document: " + url);
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
                    JOptionPane.showMessageDialog(AutoPrintApplet.this, t.getMessage());
                }
                else
                {
                    JOptionPane.showMessageDialog(AutoPrintApplet.this, "Error redirecting browser to " + redirectURL);
                }
            }
        }
    }
    
    public String [] getPasswords()
    {
        // Create and show the password dialog
        Frame parentFrame = (Frame)SwingUtilities.windowForComponent(this);
        return PasswordDialog.showAndGetPassword(null, parentFrame);
    }
}
