/*
 * Created on May 31, 2005
 *
 */
package jPDFPrintSamples;

import javax.swing.JApplet;

/**
 * @author Gerald Holmann
 *
 */
public class PDFPrintApplet extends JApplet
{
    /**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() 
	{
        // look and feel
        PDFPrintSample.setLookAndFeel();

        // Set the size
        setSize(650, 500);

        // Set the content pane
        PDFPrintSample contentPanel = new PDFPrintSample();
        contentPanel.getjpAPI().setVisible(false);
        setContentPane (contentPanel);
	}
}
