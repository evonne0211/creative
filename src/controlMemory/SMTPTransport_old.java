package com.sun.mail.smtp;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;

public class SMTPTransport
  extends Transport
{
  private MimeMessage message;
  private Address[] addresses;
  private Address[] validSentAddr;
  private Address[] validUnsentAddr;
  private Address[] invalidAddr;
  boolean sendPartiallyFailed = false;
  MessagingException exception;
  private Hashtable extMap;
  private boolean noAuth = false;
  private boolean quitWait = false;
  private String saslRealm = "UNKNOWN";
  private String name = "smtp";
  private PrintStream out;
  private static String localHostName;
  private static final String[] ignoreList = { "Bcc", "Content-Length" };
  private static final byte[] CRLF = { 13, 10 };
  private static final String UNKNOWN = "UNKNOWN";
  private DigestMD5 md5support;
  private BufferedInputStream serverInput;
  private LineInputStream lineInputStream;
  private OutputStream serverOutput;
  private String lastServerResponse;
  private Socket serverSocket;
  
  public SMTPTransport(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName);
    this.out = paramSession.getDebugOut();
    if (paramURLName != null) {
      this.name = paramURLName.getProtocol();
    }
    String str = paramSession.getProperty("mail." + this.name + ".quitwait");
    this.quitWait = ((str != null) && (str.equalsIgnoreCase("true")));
  }
  
  private String getLocalHost()
  {
    try
    {
      if ((localHostName == null) || (localHostName.length() <= 0)) {
        localHostName = 
          this.session.getProperty("mail." + this.name + ".localhost");
      }
      if ((localHostName == null) || (localHostName.length() <= 0)) {
        localHostName = InetAddress.getLocalHost().getHostName();
      }
    }
    catch (UnknownHostException localUnknownHostException) {}
    return localHostName;
  }
  
  public synchronized void connect()
    throws MessagingException
  {
    try
    {
      this.noAuth = true;
      super.connect();
    }
    finally
    {
      this.noAuth = false;
    }
  }
  
  public String getSASLRealm()
  {
    if (this.saslRealm == "UNKNOWN") {
      this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm");
    }
    return this.saslRealm;
  }
  
  public void setSASLRealm(String paramString)
  {
    this.saslRealm = paramString;
  }
  
  private synchronized DigestMD5 getMD5()
  {
    if (this.md5support == null) {
      this.md5support = new DigestMD5(this.debug ? this.out : null);
    }
    return this.md5support;
  }
  
  protected boolean protocolConnect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    String str1 = this.session.getProperty("mail." + this.name + ".ehlo");
    boolean bool1 = (str1 == null) || (!str1.equalsIgnoreCase("false"));
    
    String str2 = this.session.getProperty("mail." + this.name + ".auth");
    boolean bool2 = (str2 != null) && (str2.equalsIgnoreCase("true"));
    if (this.debug) {
      this.out.println("DEBUG SMTP: useEhlo " + bool1 + 
        ", useAuth " + bool2);
    }
    if ((bool2) && ((paramString2 == null) || (paramString3 == null))) {
      return false;
    }
    if (paramInt == -1)
    {
      String str3 = this.session.getProperty("mail." + this.name + ".port");
      if (str3 != null) {
        paramInt = Integer.parseInt(str3);
      } else {
        paramInt = 25;
      }
    }
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      paramString1 = "localhost";
    }
    boolean bool3 = false;
    
    openServer(paramString1, paramInt);
    if (bool1) {
      bool3 = ehlo(getLocalHost());
    }
    if ((bool3) && (bool2) && (
      (supportsExtension("AUTH")) || (supportsExtension("AUTH=LOGIN"))))
    {
      if (this.debug)
      {
        this.out.println("DEBUG SMTP: Attempt to authenticate");
        if ((!supportsAuthentication("LOGIN")) && 
          (supportsExtension("AUTH=LOGIN"))) {
          this.out.println("DEBUG SMTP: use AUTH=LOGIN hack");
        }
      }
      int i;
      Object localObject7;
      BASE64EncoderStream localBASE64EncoderStream;
      if ((supportsAuthentication("LOGIN")) || 
        (supportsExtension("AUTH=LOGIN")))
      {
        i = simpleCommand("AUTH LOGIN");
        try
        {
          localObject7 = new ByteArrayOutputStream();
          localBASE64EncoderStream = 
            new BASE64EncoderStream((OutputStream)localObject7, Integer.MAX_VALUE);
          if (i == 334)
          {
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString2));
            localBASE64EncoderStream.flush();
            
            i = simpleCommand(((ByteArrayOutputStream)localObject7).toByteArray());
            ((ByteArrayOutputStream)localObject7).reset();
          }
          if (i == 334)
          {
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString3));
            localBASE64EncoderStream.flush();
            
            i = simpleCommand(((ByteArrayOutputStream)localObject7).toByteArray());
            ((ByteArrayOutputStream)localObject7).reset();
          }
        }
        catch (IOException localIOException1) {}finally
        {
          if (i != 235)
          {
            closeConnection();
            return false;
          }
        }
      }
      if (supportsAuthentication("PLAIN"))
      {
        i = simpleCommand("AUTH PLAIN");
        try
        {
          localObject7 = new ByteArrayOutputStream();
          localBASE64EncoderStream = 
            new BASE64EncoderStream((OutputStream)localObject7, Integer.MAX_VALUE);
          if (i == 334)
          {
            localBASE64EncoderStream.write(0);
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString2));
            localBASE64EncoderStream.write(0);
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString3));
            localBASE64EncoderStream.flush();
            
            i = simpleCommand(((ByteArrayOutputStream)localObject7).toByteArray());
          }
        }
        catch (IOException localIOException2) {}finally
        {
          if (i != 235)
          {
            closeConnection();
            return false;
          }
        }
      }
      DigestMD5 localDigestMD5;
      if ((supportsAuthentication("DIGEST-MD5")) && 
        ((localDigestMD5 = getMD5()) != null))
      {
        i = simpleCommand("AUTH DIGEST-MD5");
        try
        {
          if (i == 334)
          {
            localObject7 = localDigestMD5.authClient(paramString1, paramString2, paramString3, this.saslRealm, 
              this.lastServerResponse);
            i = simpleCommand((byte[])localObject7);
            if (i == 334) {
              if (!localDigestMD5.authServer(this.lastServerResponse)) {
                i = -1;
              } else {
                i = simpleCommand(new byte[0]);
              }
            }
          }
        }
        catch (Exception localException)
        {
          if (this.debug) {
            this.out.println("DEBUG SMTP: DIGEST-MD5: " + localException);
          }
        }
        finally
        {
          if (i != 235)
          {
            closeConnection();
            return false;
          }
        }
      }
    }
    if (!bool3) {
      helo(getLocalHost());
    }
    return true;
  }
  
  public synchronized void sendMessage(Message paramMessage, Address[] paramArrayOfAddress)
    throws MessagingException, SendFailedException
  {
    checkConnected();
    if (!(paramMessage instanceof MimeMessage))
    {
      if (this.debug) {
        this.out.println("DEBUG SMTP: Can only send RFC822 msgs");
      }
      throw new MessagingException("SMTP can only send RFC822 messages");
    }
    for (int i = 0; i < paramArrayOfAddress.length; i++) {
      if (!(paramArrayOfAddress[i] instanceof InternetAddress)) {
        throw new MessagingException(paramArrayOfAddress[i] + 
          " is not an InternetAddress");
      }
    }
    this.message = ((MimeMessage)paramMessage);
    this.addresses = paramArrayOfAddress;
    expandGroups();
    
    boolean bool = false;
    if ((paramMessage instanceof SMTPMessage)) {
      bool = ((SMTPMessage)paramMessage).getAllow8bitMIME();
    }
    if (!bool)
    {
      String str = 
        this.session.getProperty("mail." + this.name + ".allow8bitmime");
      bool = (str != null) && (str.equalsIgnoreCase("true"));
    }
    if (this.debug) {
      this.out.println("DEBUG SMTP: use8bit " + bool);
    }
    if ((bool) && (supportsExtension("8BITMIME"))) {
      convertTo8Bit(this.message);
    }
    try
    {
      mailFrom();
      rcptTo();
      this.message.writeTo(data(), ignoreList);
      finishData();
      if (this.sendPartiallyFailed)
      {
        if (this.debug) {
          this.out.println("DEBUG SMTP: Sending partially failed because of invalid destination addresses");
        }
        notifyTransportListeners(
          3, 
          this.validSentAddr, this.validUnsentAddr, this.invalidAddr, 
          this.message);
        
        throw new SendFailedException("Message partially delivered", 
          this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
      }
      notifyTransportListeners(1, 
        this.validSentAddr, this.validUnsentAddr, 
        this.invalidAddr, this.message);
    }
    catch (IOException localIOException)
    {
      if (this.debug) {
        localIOException.printStackTrace(this.out);
      }
      try
      {
        closeConnection();
      }
      catch (MessagingException localMessagingException) {}
      notifyTransportListeners(2, 
        this.validSentAddr, this.validUnsentAddr, 
        this.invalidAddr, this.message);
      
      throw new MessagingException("IOException while sending message", 
        localIOException);
    }
    finally
    {
      this.validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
      this.addresses = null;
      this.message = null;
      this.exception = null;
      this.sendPartiallyFailed = false;
    }
  }
  
  public synchronized void close()
    throws MessagingException
  {
    if (!super.isConnected()) {
      return;
    }
    try
    {
      if (this.serverSocket != null)
      {
        sendCommand("QUIT");
        if (this.quitWait)
        {
          int i = readServerResponse();
          if ((i != 221) && (i != -1)) {
            this.out.println("DEBUG SMTP: QUIT failed with " + i);
          }
        }
      }
    }
    finally
    {
      closeConnection();
    }
  }
  
  private void closeConnection()
    throws MessagingException
  {
    try
    {
      if (this.serverSocket != null) {
        this.serverSocket.close();
      }
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("Server Close Failed", localIOException);
    }
    finally
    {
      this.serverSocket = null;
      this.serverOutput = null;
      this.serverInput = null;
      this.lineInputStream = null;
      if (super.isConnected()) {
        super.close();
      }
    }
  }
  
  public synchronized boolean isConnected()
  {
    if (!super.isConnected()) {
      return false;
    }
    try
    {
      sendCommand("NOOP");
      int i = readServerResponse();
      if ((i >= 0) && (i != 421)) {
        return true;
      }
      try
      {
        closeConnection();
      }
      catch (MessagingException localMessagingException1) {}
      return false;
    }
    catch (Exception localException)
    {
      try
      {
        closeConnection();
      }
      catch (MessagingException localMessagingException2) {}
    }
    return false;
  }
  
  private void expandGroups()
  {
    Vector localVector = null;
    Object localObject;
    for (int i = 0; i < this.addresses.length; i++)
    {
      localObject = (InternetAddress)this.addresses[i];
      if (((InternetAddress)localObject).isGroup())
      {
        if (localVector == null)
        {
          localVector = new Vector();
          for (int j = 0; j < i; j++) {
            localVector.addElement(this.addresses[j]);
          }
        }
        try
        {
          InternetAddress[] arrayOfInternetAddress = ((InternetAddress)localObject).getGroup(true);
          if (arrayOfInternetAddress != null) {
            for (int k = 0; k < arrayOfInternetAddress.length; k++) {
              localVector.addElement(arrayOfInternetAddress[k]);
            }
          } else {
            localVector.addElement(localObject);
          }
        }
        catch (ParseException localParseException)
        {
          localVector.addElement(localObject);
        }
      }
      else if (localVector != null)
      {
        localVector.addElement(localObject);
      }
    }
    if (localVector != null)
    {
      localObject = new InternetAddress[localVector.size()];
      localVector.copyInto((Object[])localObject);
      this.addresses = ((Address[])localObject);
    }
  }
  
  private void convertTo8Bit(MimePart paramMimePart)
  {
    label116:
    try
    {
      Object localObject;
      if (paramMimePart.isMimeType("text/*"))
      {
        localObject = paramMimePart.getEncoding();
        if ((((String)localObject).equalsIgnoreCase("quoted-printable")) || 
          (((String)localObject).equalsIgnoreCase("base64")))
        {
          InputStream localInputStream = paramMimePart.getInputStream();
          if (!is8Bit(localInputStream)) {
            break label116;
          }
          paramMimePart.setHeader("Content-Transfer-Encoding", "8bit");
        }
      }
      else
      {
        if (!paramMimePart.isMimeType("multipart/*")) {
          return;
        }
        localObject = (MimeMultipart)paramMimePart.getContent();
        int i = ((MimeMultipart)localObject).getCount();
        for (int j = 0; j < i; j++) {
          convertTo8Bit((MimePart)((MimeMultipart)localObject).getBodyPart(j));
        }
      }
    }
    catch (IOException localIOException) {}catch (MessagingException localMessagingException) {}
  }
  
  private boolean is8Bit(InputStream paramInputStream)
  {
    int j = 0;
    boolean bool = false;
    try
    {
      int i;
      while ((i = paramInputStream.read()) >= 0)
      {
        i &= 0xFF;
        if ((i == 13) || (i == 10))
        {
          j = 0;
        }
        else
        {
          if (i == 0) {
            return false;
          }
          j++;
          if (j > 998) {
            return false;
          }
        }
        if (i > 127) {
          bool = true;
        }
      }
    }
    catch (IOException localIOException)
    {
      return false;
    }
    if ((this.debug) && (bool)) {
      this.out.println("DEBUG SMTP: found an 8bit part");
    }
    return bool;
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    try
    {
      closeConnection();
    }
    catch (MessagingException localMessagingException) {}
  }
  
  private void helo(String paramString)
    throws MessagingException
  {
    if (paramString != null) {
      issueCommand("HELO " + paramString, 250);
    } else {
      issueCommand("HELO", 250);
    }
  }
  
  private boolean ehlo(String paramString)
    throws MessagingException
  {
    String str1;
    if (paramString != null) {
      str1 = "EHLO " + paramString;
    } else {
      str1 = "EHLO";
    }
    sendCommand(str1);
    int i = readServerResponse();
    if (i == 250)
    {
      BufferedReader localBufferedReader = 
        new BufferedReader(new StringReader(this.lastServerResponse));
      
      this.extMap = new Hashtable();
      try
      {
        int j = 1;
        String str2;
        while ((str2 = localBufferedReader.readLine()) != null) {
          if (j != 0)
          {
            j = 0;
          }
          else if (str2.length() >= 5)
          {
            str2 = str2.substring(4);
            int k = str2.indexOf(' ');
            String str3 = "";
            if (k > 0)
            {
              str3 = str2.substring(k + 1);
              str2 = str2.substring(0, k);
            }
            if (this.debug) {
              this.out.println("DEBUG SMTP: Found extension \"" + 
                str2 + "\", arg \"" + str3 + "\"");
            }
            this.extMap.put(str2.toUpperCase(), str3);
          }
        }
      }
      catch (IOException localIOException) {}
    }
    return i == 250;
  }
  
  private void mailFrom()
    throws MessagingException
  {
    String str = null;
    if ((this.message instanceof SMTPMessage)) {
      str = ((SMTPMessage)this.message).getEnvelopeFrom();
    }
    if ((str == null) || (str.length() <= 0)) {
      str = this.session.getProperty("mail." + this.name + ".from");
    }
    Object localObject2;
    if ((str == null) || (str.length() <= 0))
    {
      if ((this.message != null) && ((localObject1 = this.message.getFrom()) != null) && 
        (localObject1.length > 0)) {
        localObject2 = localObject1[0];
      } else {
        localObject2 = InternetAddress.getLocalAddress(this.session);
      }
      if (localObject2 != null) {
        str = ((InternetAddress)localObject2).getAddress();
      } else {
        throw new MessagingException(
          "can't determine local email address");
      }
    }
    Object localObject1 = "MAIL FROM:" + normalizeAddress(str);
    if (supportsExtension("DSN"))
    {
      localObject2 = null;
      if ((this.message instanceof SMTPMessage)) {
        localObject2 = ((SMTPMessage)this.message).getDSNRet();
      }
      if (localObject2 == null) {
        localObject2 = this.session.getProperty("mail." + this.name + ".dsn.ret");
      }
      if (localObject2 != null) {
        localObject1 = localObject1 + " RET=" + (String)localObject2;
      }
    }
    issueCommand((String)localObject1, 250);
  }
  
  private void rcptTo()
    throws MessagingException
  {
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    Vector localVector3 = new Vector();
    int i = -1;
    Object localObject = null;
    int j = 0;
    SendFailedException localSendFailedException = null;
    this.validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
    boolean bool = false;
    if ((this.message instanceof SMTPMessage)) {
      bool = ((SMTPMessage)this.message).getSendPartial();
    }
    if (!bool)
    {
      String str1 = this.session.getProperty("mail." + this.name + ".sendpartial");
      bool = (str1 != null) && (str1.equalsIgnoreCase("true"));
    }
    int k = 0;
    String str2 = null;
    if (supportsExtension("DSN"))
    {
      if ((this.message instanceof SMTPMessage)) {
        str2 = ((SMTPMessage)this.message).getDSNNotify();
      }
      if (str2 == null) {
        str2 = this.session.getProperty("mail." + this.name + ".dsn.notify");
      }
      if (str2 != null) {
        k = 1;
      }
    }
    for (int m = 0; m < this.addresses.length; m++)
    {
      localSendFailedException = null;
      String str3 = "RCPT TO:" + 
        normalizeAddress(((InternetAddress)this.addresses[m]).getAddress());
      if (k != 0) {
        str3 = str3 + " NOTIFY=" + str2;
      }
      sendCommand(str3);
      
      i = readServerResponse();
      switch (i)
      {
      case 250: 
      case 251: 
        localVector1.addElement(this.addresses[m]);
        break;
      case 501: 
      case 503: 
      case 550: 
      case 551: 
      case 553: 
        if (!bool) {
          j = 1;
        }
        localVector3.addElement(this.addresses[m]);
        
        localSendFailedException = new SendFailedException(this.lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else {
          ((MessagingException)localObject).setNextException(localSendFailedException);
        }
        break;
      case 450: 
      case 451: 
      case 452: 
      case 552: 
        if (!bool) {
          j = 1;
        }
        localVector2.addElement(this.addresses[m]);
        
        localSendFailedException = new SendFailedException(this.lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else {
          ((MessagingException)localObject).setNextException(localSendFailedException);
        }
        break;
      default: 
        if ((i >= 400) && (i <= 499))
        {
          localVector2.addElement(this.addresses[m]);
        }
        else if ((i >= 500) && (i <= 599))
        {
          localVector3.addElement(this.addresses[m]);
        }
        else
        {
          if (this.debug) {
            this.out.println("DEBUG SMTP: got response code " + i + 
              ", with response: " + this.lastServerResponse);
          }
          String str4 = this.lastServerResponse;
          if (this.serverSocket != null) {
            issueCommand("RSET", 250);
          }
          throw new SendFailedException(str4);
        }
        if (!bool) {
          j = 1;
        }
        localSendFailedException = new SendFailedException(this.lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else {
          ((MessagingException)localObject).setNextException(localSendFailedException);
        }
        break;
      }
    }
    if ((bool) && (localVector1.size() == 0)) {
      j = 1;
    }
    int n;
    if (j != 0)
    {
      this.invalidAddr = new Address[localVector3.size()];
      localVector3.copyInto(this.invalidAddr);
      
      this.validUnsentAddr = new Address[localVector1.size() + localVector2.size()];
      n = 0;
      for (int i1 = 0; i1 < localVector1.size(); i1++) {
        this.validUnsentAddr[(n++)] = ((Address)localVector1.elementAt(i1));
      }
      for (int i2 = 0; i2 < localVector2.size(); i2++) {
        this.validUnsentAddr[(n++)] = ((Address)localVector2.elementAt(i2));
      }
    }
    else if ((bool) && (
      (localVector3.size() > 0) || (localVector2.size() > 0)))
    {
      this.sendPartiallyFailed = true;
      this.exception = ((MessagingException)localObject);
      
      this.invalidAddr = new Address[localVector3.size()];
      localVector3.copyInto(this.invalidAddr);
      
      this.validUnsentAddr = new Address[localVector2.size()];
      localVector2.copyInto(this.validUnsentAddr);
      
      this.validSentAddr = new Address[localVector1.size()];
      localVector1.copyInto(this.validSentAddr);
    }
    else
    {
      this.validSentAddr = this.addresses;
    }
    if (this.debug)
    {
      if ((this.validSentAddr != null) && (this.validSentAddr.length > 0))
      {
        this.out.println("DEBUG SMTP: Verified Addresses");
        for (n = 0; n < this.validSentAddr.length; n++) {
          this.out.println("DEBUG SMTP:   " + this.validSentAddr[n]);
        }
      }
      if ((this.validUnsentAddr != null) && (this.validUnsentAddr.length > 0))
      {
        this.out.println("DEBUG SMTP: Valid Unsent Addresses");
        for (n = 0; n < this.validUnsentAddr.length; n++) {
          this.out.println("DEBUG SMTP:   " + this.validUnsentAddr[n]);
        }
      }
      if ((this.invalidAddr != null) && (this.invalidAddr.length > 0))
      {
        this.out.println("DEBUG SMTP: Invalid Addresses");
        for (n = 0; n < this.invalidAddr.length; n++) {
          this.out.println("DEBUG SMTP:   " + this.invalidAddr[n]);
        }
      }
    }
    if (j != 0)
    {
      if (this.debug) {
        this.out.println("DEBUG SMTP: Sending failed because of invalid destination addresses");
      }
      notifyTransportListeners(2, 
        this.validSentAddr, this.validUnsentAddr, 
        this.invalidAddr, this.message);
      try
      {
        if (this.serverSocket != null) {
          issueCommand("RSET", 250);
        }
      }
      catch (MessagingException localMessagingException2)
      {
        try
        {
          close();
        }
        catch (MessagingException localMessagingException1)
        {
          if (this.debug) {
            localMessagingException1.printStackTrace(this.out);
          }
        }
      }
      throw new SendFailedException("Invalid Addresses", (Exception)localObject, 
        this.validSentAddr, 
        this.validUnsentAddr, this.invalidAddr);
    }
  }
  
  private OutputStream data()
    throws MessagingException
  {
    issueCommand("DATA", 354);
    return new SMTPOutputStream(this.serverOutput);
  }
  
  private void finishData()
    throws MessagingException
  {
    issueCommand("\r\n.", 250);
  }
  
  private void openServer(String paramString, int paramInt)
    throws MessagingException
  {
    if (this.debug) {
      this.out.println("DEBUG SMTP: trying to connect to host \"" + 
        paramString + "\", port " + paramInt + "\n");
    }
    try
    {
      Properties localProperties = this.session.getProperties();
      PrintStream localPrintStream = this.session.getDebugOut();
      boolean bool1 = this.session.getDebug();
      
      this.serverSocket = SocketFetcher.getSocket(paramString, paramInt, 
        localProperties, "mail." + this.name);
      
      paramInt = this.serverSocket.getPort();
      
      String str = localProperties.getProperty("mail.debug.quote");
      boolean bool2 = (str != null) && (str.equalsIgnoreCase("true"));
      
      TraceInputStream localTraceInputStream = 
        new TraceInputStream(this.serverSocket.getInputStream(), localPrintStream);
      localTraceInputStream.setTrace(bool1);
      localTraceInputStream.setQuote(bool2);
      
      TraceOutputStream localTraceOutputStream = 
        new TraceOutputStream(this.serverSocket.getOutputStream(), localPrintStream);
      localTraceOutputStream.setTrace(bool1);
      localTraceOutputStream.setQuote(bool2);
      
      this.serverOutput = 
        new BufferedOutputStream(localTraceOutputStream);
      this.serverInput = 
        new BufferedInputStream(localTraceInputStream);
      this.lineInputStream = new LineInputStream(this.serverInput);
      
      int i = -1;
      if ((i = readServerResponse()) != 220)
      {
        this.serverSocket.close();
        this.serverSocket = null;
        this.serverOutput = null;
        this.serverInput = null;
        this.lineInputStream = null;
        if (bool1) {
          localPrintStream.println("DEBUG SMTP: could not connect to host \"" + 
            paramString + "\", port: " + paramInt + 
            ", response: " + i + "\n");
        }
        throw new MessagingException(
          "Could not connect to SMTP host: " + paramString + 
          ", port: " + paramInt + 
          ", response: " + i);
      }
      if (bool1) {
        localPrintStream.println("DEBUG SMTP: connected to host \"" + 
          paramString + "\", port: " + paramInt + "\n");
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw new MessagingException("Unknown SMTP host: " + paramString, localUnknownHostException);
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("Could not connect to SMTP host: " + 
        paramString + ", port: " + paramInt, localIOException);
    }
  }
  
  private void issueCommand(String paramString, int paramInt)
    throws MessagingException
  {
    sendCommand(paramString);
    if (readServerResponse() != paramInt) {
      throw new MessagingException(this.lastServerResponse);
    }
  }
  
  private int simpleCommand(String paramString)
    throws MessagingException
  {
    sendCommand(paramString);
    return readServerResponse();
  }
  
  private int simpleCommand(byte[] paramArrayOfByte)
    throws MessagingException
  {
    sendCommand(paramArrayOfByte);
    return readServerResponse();
  }
  
  private void sendCommand(String paramString)
    throws MessagingException
  {
    sendCommand(ASCIIUtility.getBytes(paramString));
  }
  
  private void sendCommand(byte[] paramArrayOfByte)
    throws MessagingException
  {
    try
    {
      this.serverOutput.write(paramArrayOfByte);
      this.serverOutput.write(CRLF);
      this.serverOutput.flush();
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("Can't send command to SMTP host", localIOException);
    }
  }
  
  private int readServerResponse()
    throws MessagingException
  {
    String str1 = "";
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer(100);
    try
    {
      String str2 = null;
      do
      {
        str2 = this.lineInputStream.readLine();
        if (str2 == null)
        {
          str1 = localStringBuffer.toString();
          if (str1.length() == 0) {
            str1 = "[EOF]";
          }
          this.lastServerResponse = str1;
          if (this.debug) {
            this.out.println("DEBUG SMTP: EOF: " + str1);
          }
          return -1;
        }
        localStringBuffer.append(str2);
        localStringBuffer.append("\n");
      } while (isNotLastLine(str2));
      str1 = localStringBuffer.toString();
    }
    catch (IOException localIOException)
    {
      if (this.debug) {
        this.out.println("DEBUG SMTP: exception reading response: " + localIOException);
      }
      this.lastServerResponse = "";
      throw new MessagingException("Exception reading response", localIOException);
    }
    if ((str1 != null) && (str1.length() >= 3)) {
      try
      {
        i = Integer.parseInt(str1.substring(0, 3));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        try
        {
          close();
        }
        catch (MessagingException localMessagingException1)
        {
          if (this.debug) {
            localMessagingException1.printStackTrace(this.out);
          }
        }
        i = -1;
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
      {
        try
        {
          close();
        }
        catch (MessagingException localMessagingException2)
        {
          if (this.debug) {
            localMessagingException2.printStackTrace(this.out);
          }
        }
        i = -1;
      }
    } else {
      i = -1;
    }
    if ((i == -1) && (this.debug)) {
      this.out.println("DEBUG SMTP: bad server response: " + str1);
    }
    this.lastServerResponse = str1;
    return i;
  }
  
  private void checkConnected()
  {
    if (!super.isConnected()) {
      throw new IllegalStateException("Not connected");
    }
  }
  
  private boolean isNotLastLine(String paramString)
  {
    return (paramString != null) && (paramString.length() >= 4) && (paramString.charAt(3) == '-');
  }
  
  private String normalizeAddress(String paramString)
  {
    if ((!paramString.startsWith("<")) && (!paramString.endsWith(">"))) {
      return "<" + paramString + ">";
    }
    return paramString;
  }
  
  private boolean supportsExtension(String paramString)
  {
    return (this.extMap != null) && (this.extMap.get(paramString.toUpperCase()) != null);
  }
  
  private boolean supportsAuthentication(String paramString)
  {
    if (this.extMap == null) {
      return false;
    }
    String str1 = (String)this.extMap.get("AUTH");
    if (str1 == null) {
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      if (str2.equalsIgnoreCase(paramString)) {
        return true;
      }
    }
    return false;
  }
}
