package com.ozonics.api;
import java.util.Base64;
import java.util.ResourceBundle;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSms {

//    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
//    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    
	
		static ResourceBundle resource = ResourceBundle.getBundle("resources/web");
		 public static final String ACCOUNT_SID = resource.getString("ACCOUNT_SID");
		 public static final String AUTH_TOKEN = resource.getString("AUTH_TOKEN");

    public String sendMsgViaTwilio(String otp, String phone_num) {
    	System.out.println(System.getenv("TWILIO_ACCOUNT_SID"));
    
    	String decodedID = new String(Base64.getDecoder().decode(ACCOUNT_SID));
		String decodedToken =new String(Base64.getDecoder().decode(AUTH_TOKEN));
		
    	System.out.println("Twilio Running");
    	System.out.println(phone_num);
    	System.out.println("id:"+decodedID+"   token:"+decodedToken);
    Twilio.init(decodedID, decodedToken);
    try {
    	String text ="Your Ozonics Account login OTP is "+otp;
    	System.out.println("message:"+text);
    Message message = Message
            .creator(new PhoneNumber("+"+phone_num), // to
                    new PhoneNumber("+17147865716"), // from
                    text)
            .create();
    
    System.out.println(message.getSid());
    return "1";
    }
    catch(Exception e) {
    	e.printStackTrace();
    	return "0";
    }
//    Service service = Service.creator("My First Verify Service").create();
//    System.out.println(service.getSid());

    
    }
    
}
