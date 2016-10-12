package createPDF;

import java.io.FileOutputStream;

import java.io.IOException;

 

import com.itextpdf.text.DocumentException;

import com.itextpdf.text.pdf.BaseFont;

import com.itextpdf.text.pdf.PdfContentByte;

import com.itextpdf.text.pdf.PdfReader;

import com.itextpdf.text.pdf.PdfStamper;


public class createPDF {
	public static String fileToBeRead="D:/0222224699.工號2016080819偉立糊盒.pdf";
	public static void main(String[] args) throws Exception {
	/*FileOutputStream fos = new FileOutputStream(new File("D:/123456.pdf"));
	Document document = new Document(PageSize.A4, 10, 20, 30, 40);
	//設定要輸出的Stream
	PdfWriter.getInstance(document, fos);
	document.open();
	//設定作者
	document.addAuthor("Author");
	//設定建立者
	document.addCreator("createor");
	//設定主題
	document.addSubject("subject");
	//設定標題
	document.addTitle("title");
	//設定建立時間(為當下時間)
	document.addCreationDate();

	document.add(new Phrase("1233421566\n"));
	document.add(new Phrase("1233421566\n"));
	document.add(new Phrase("1233421566\n"));
	document.close();*/

	
	
	/* PdfReader pdfReader = new PdfReader(fileToBeRead);
	 System.out.println("版本"+pdfReader.getPdfVersion());
	 PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("D:/testPrint.pdf"));
	 Map<String, String> info = pdfReader.getInfo();
	    info.put("Title", "Hello World stamped");
	    info.put("Subject", "Hello World with changed metadata");
	    info.put("Keywords", "iText in Action, PdfStamper");
	    info.put("Creator", "Silly standalone example");
	    info.put("Author", "Also Bruno Lowagie");
	    pdfStamper.setMoreInfo(info);
	    pdfStamper.close();*/
	    //pdfStamper.getReader();
	    //pdfStamper.getWriter();
	    
	   // System.out.println("pdfStamper.getReader():"+pdfStamper.getReader());
	    //System.out.println("pdfStamper.getWriter():"+pdfStamper.getWriter());
	 
		try
		
		                {
		
		                    PdfReader pdfReader = new PdfReader(fileToBeRead);
		
		                    PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("D:/testPrint.pdf"));
		
		                    PdfContentByte content = pdfStamper.getUnderContent(1);//1 for the first page
		
		                    BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ITALIC, BaseFont.CP1250, BaseFont.EMBEDDED);
		
		                    content.beginText();
		
		                    content.setFontAndSize(bf, 18);
		
		                    content.showTextAligned(PdfContentByte.ALIGN_CENTER, "JavaCodeGeeks", 250,650,0);
		
		                    content.endText();
		
		             
		
		                    pdfStamper.close();
		
		                    pdfReader.close();
		
		                }
		
		                catch (IOException e)
		
		                {
		
		                       e.printStackTrace();
		
		                }
		
		                catch (DocumentException e)
		
		                {
		
		                        e.printStackTrace();
		
		                }

	}
	
}
