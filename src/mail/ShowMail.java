package mail;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.pop3.POP3Folder;

public class ShowMail extends Thread {
		ShowMail() {
		start();
		}
		Session session = null;
     public static void main(String args[]) throws Exception { 
    	 new ShowMail();
     }
     public void run() {
    	 String holdDate;
    	 System.out.println("ReadMessage : ");
    	 try{
    	 do { 
    		 Store store = session.getStore("pop-mail.outlook.com");
    		 store.connect("pop-mail.outlook.com", 995, "dinyimis@hotmail.com","Dinyiwindows");

    		 POP3Folder folder = (POP3Folder)store.getFolder("INBOX");
    	 folder.open(Folder.READ_ONLY);
    	 System.out.println(folder.getMessageCount());
    	 if(folder.getMessageCount() > 0){
    	 Message message[] = folder.getMessages();
    	 int i = message.length;
    	 i--;

    	 holdDate = message[i].getSentDate().toString();

    	 System.out.println(new Date().toString());
    	 System.out.println(holdDate);

    	 if(message[i].getSentDate().equals(new Date().toString())){
    	 System.out.println("Tested.........");
    	 }

    	 Part messagePart = message[i];
    	 Object content = messagePart.getContent();
    	 InputStream is = messagePart.getInputStream();

    	 BufferedReader read = new BufferedReader(new InputStreamReader(is));
    	 String thisLine;
    	 while((thisLine = read.readLine()) != null) {
    	 System.out.println(thisLine);
    	 }
    	 message[i].setFlag(Flags.Flag.SEEN, true);
    	 System.out.println(message[i].isSet(Flags.Flag.SEEN));
    	 folder.close(true);
    	 store.close();
    	 }
    	 }while(true);
    	 }catch(Exception e){
    	 System.out.println("ReadMessage : ");
    	 e.printStackTrace();
    	 }
    }  
}
    