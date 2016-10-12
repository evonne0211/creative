package jPDFPrintSamples;


import java.io.File;

import com.pamconsult.pamfax.sdk.FaxJobHelper;
import com.pamconsult.pamfax.sdk.SessionHelper;
import com.pamconsult.pamfax.sdk.SessionListener;
import com.pamconsult.pamfax.sdk.constants.RequestType;
import com.pamconsult.pamfax.sdk.constants.ResultCode;
import com.pamconsult.pamfax.sdk.data.FaxContainer;
import com.pamconsult.pamfax.sdk.data.FaxFileContainer;
import com.pamconsult.pamfax.sdk.data.FaxRecipient;
import com.pamconsult.pamfax.sdk.data.ResponseData;
import com.pamconsult.pamfax.sdk.data.UserInfo;

public class pamFax {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
        {
        	// Pre-define a session listener for post-requests work
       	 	SessionListener sessionListener = new SessionListener() {
       	        public void onRequestComplete( RequestType requestType, int resultCode, ResponseData responseData ) {
       	                // Checking for successful request result
       	                if ( resultCode == ResultCode.SUCCESS )
       	                        
       	                
       	                        
       	 
       	                // To do some additional things depending on request type
       	                switch ( requestType ) {
       	                        case FAXJOB_ADD_FILE:
       	                                
       	                                FaxFileContainer fileContainer = responseData.getFaxFileContainer();
       	                                
       	                                break;
       	                        case FAXJOB_ADD_RECIPIENT:
       	                                
       	                                FaxRecipient recipient = responseData.getFaxRecipient();

       	                                break;
       	                        case FAXJOB_CREATE_FAX:
       	                                
       	                                FaxContainer faxContainer = responseData.getFaxContainer();
       	                                
       	                                break;
       	                        case FAXJOB_SEND_FAX:
       	                                
       	                                break;
       	                        case SESSION_VERIFY_USER:
       	                                
       	                                UserInfo userInfo = responseData.getUserInfo();
       	                                
       	                                break;
       	                        default:
       	                                break;
       	                }
       	        }
       	 };
       	 
       	 // Creating SessionHelper object to automate some API session work
       	 SessionHelper sessionHelper = new SessionHelper( "chenI-fan1", "decasoregodujer3793", sessionListener );
       	 
       	 // Starting API user session
       	 sessionHelper.startSession( "evonne@dinyi.com.tw", "decasoregodujer3793", true );
       	 
       	 // Creating FaxJobHelper object to automate some fax job tasks
       	 FaxJobHelper faxJobHelper = new FaxJobHelper( sessionListener, 1000, 2);
       	 
       	 // Creating new fax container
       	 faxJobHelper.createFax();
       	 
       	 // Adding fax recipient
       	 faxJobHelper.addRecipient( "+18656745318", "Evonne Chen" );
       	 
       	 // Adding file which one of supported formats
       	 File faxFile = new File( "D:/1110222421114.工號2016051093鼎創.pdf" );
       	 faxJobHelper.addFile( "0222421114.工號2016051093鼎創.pdf", faxFile );
       	 
       	 // Sending fax. If fax yet not ready, FaxJobHelper will retry sending
       	 // with specified SEND_REPEAT_TIMEOUT and SEND_REPEATS_COUNT
       	 faxJobHelper.sendFax();
           
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
	}

}
