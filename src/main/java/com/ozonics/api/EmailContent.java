package com.ozonics.api;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailContent {
	public String emailFormatwithAttachment(final String sender_id, final String sender_password, String email_content,
			String subject, String receiver_email, String path, String file_name) throws IOException {
		System.out.println("2");

		String result = null;
		try {

			Properties props = System.getProperties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.host", "smtp.gmail.com");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			props.put("mail.debug", "false");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
			props.put("mail.smtp.starttls.enable", "true");

			Session emailSession = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sender_id, sender_password);
				}
			});
			emailSession.setDebug(false);
			System.out.println("3");

			Message msg = new MimeMessage(emailSession);

			msg.setFrom(new InternetAddress(sender_id));
			InternetAddress[] iAdressArray = InternetAddress.parse(receiver_email);

			msg.setRecipients(Message.RecipientType.TO, iAdressArray);

			msg.setSubject(subject);
			Multipart multipart = new MimeMultipart();
			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(email_content, "text/html; charset=UTF-8");
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachBodyPart = new MimeBodyPart();
			attachBodyPart.attachFile(path);

			attachBodyPart.setFileName(file_name);
			multipart.addBodyPart(attachBodyPart);
			msg.setContent(multipart);
			System.out.println("3");

			emailSession.getTransport().send(msg);
//			Transport.send(msg);
			System.out.println("5");

			System.out.println("Successfully sent email");
			result = "1";
			return result;

		} catch (MessagingException e) {
			System.out.println(e);

			System.out.println("0");
			result = "0";
			return result;
			// return email;
		}

	}

	public String emailFormat(final String sender_id, final String sender_password, String email_message, String subject,
			String receiver_email, String file_name) {

		String result = null;
		try {

			Properties props = System.getProperties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.host", "smtp.gmail.com");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			props.put("mail.debug", "true");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");

			Session emailSession = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sender_id, sender_password);
				}
			});

			emailSession.setDebug(true);
			Message msg = new MimeMessage(emailSession);

			msg.setFrom(new InternetAddress(sender_id));
			InternetAddress[] toAddresses = { new InternetAddress(receiver_email) };

			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(email_message, "text/html");
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);
			Transport.send(msg);

			System.out.println("Successfully sent email");
			result = "1";
			return result;

		} catch (MessagingException e) {
			System.out.println(e);

			System.out.println("0");
			result = "0";
			return result;
		}

	}

}
