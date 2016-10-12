/*
 * Created on May 22, 2008
 *
 */
package jPDFPrintSamples;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.qoppa.pdf.IWatermark;
import com.qoppa.pdfPrint.PDFPrint;

public class Watermark
{
	public static void main(String[] args)
	{
		try
		{
			// Load PDF document
			PDFPrint pdfPrint = new PDFPrint("input.pdf", null);

			// Create the watermark
			IWatermark watermark = new IWatermark() {
				public void drawWatermark(Graphics2D g, int type, int pageIndex, int pageWidth, int pageHeight)
				{
					// Watermark color (this can have transparency, but Java then makes the spool file very large)
					g.setColor(new Color(128, 128, 128));

					// Set the font
					g.setFont(new Font("sansserif", Font.PLAIN, 100));

					// Draw the watermark string, rotated 45 degrees
					g.rotate(Math.toRadians(45));
					g.drawString("Watermark", 150, g.getFontMetrics().getMaxDescent());
				}
			};

			// Set the watermark on the document
			pdfPrint.setWatermark(watermark);

			// Print to default printer
			pdfPrint.printToDefaultPrinter(null);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
