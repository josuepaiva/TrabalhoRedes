/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoredes;
import java.net.*;
import java.io.*;
import java.util.*;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Lavid
 */
public class SMTPConnection {
	/* The socket to the server */
	private Socket connection;
	/* Streams for reading and writing the socket */
	private BufferedReader fromServer;
	private DataOutputStream toServer;
	private static final int SMTP_PORT = 587;
	private static final String CRLF = "\r\n";
        private Properties props = new Properties();
	
	/* Are we connected? Used in close() to determine what to do. */
	private boolean isConnected = false;
	
	/* Create an SMTPConnection object. Create the socket and the
    associated streams. Initialize SMTP connection. */
	public SMTPConnection(Envelope envelope) throws IOException {
                
		connection = new Socket(envelope.DestAddr, SMTP_PORT);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());

		if(parseReply(fromServer.readLine()) != 220){
			throw new IOException("The reply code received by the server is not 220.");
		}
		
		/* SMTP handshake. We need the name of the local machine.
        Send the appropriate SMTP handshake command. */
		String localhost = InetAddress.getLocalHost().getHostAddress();
		sendCommand("HELO " + localhost, 250);
		isConnected = true;
	}
        
        public SMTPConnection(Envelope envelope, ParamsEmail params, Usuario user) throws MessagingException {
            
            this.props = params.getProperties();
            
            // Get the Session object.
            Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(user.getEmail(), user.getSenha());
               }
            });
            
            session.setDebug(true);
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(envelope.Message.getFrom()));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(envelope.Message.getTo()));

            // Set Subject: header field
            message.setSubject(envelope.Message.subject);

            // Now set the actual message
            message.setText(envelope.Message.Body);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");
        }
	
	/* Send the message. Write the correct SMTP-commands in the
        correct order. No checking for errors, just throw them to the
        caller. */
	public void send(Envelope envelope) throws IOException {
		sendCommand("MAIL FROM:<" + envelope.Sender + ">", 250);
		sendCommand("RCPT TO:<" + envelope.Recipient + ">", 250);
		sendCommand("DATA", 354);
		sendCommand(envelope.Message.toString() + CRLF + ".", 250);
		/* Send all the necessary commands to send a message. Call
    sendCommand() to do the dirty work. Do _not_ catch the
    exception thrown from sendCommand(). */
		/* Fill in */
	}

	/* Close the connection. First, terminate on SMTP level, then
close the socket. */
	public void close() {
		isConnected = false;
		try {
		sendCommand("QUIT", 221);
			connection.close();
		}catch (IOException e) {
			System.out.println("Unable to close connection: " + e);
			isConnected = true;
		}
	}
	
	/* Send an SMTP command to the server. Check that the reply code is
    what is is supposed to be according to RFC 821. */
	private void sendCommand(String command, int rc) throws IOException {
		toServer.writeBytes(command + CRLF);
		String response = fromServer.readLine();
		int code = parseReply(response);
		System.out.println("Received code " + code);
		/* Check that the server's reply code is the same as the parameter
    rc. If not, throw an IOException. */
		if(code != rc){
			throw new IOException("The code Received is different from sent one. " + command);
		}
	}
	
	/* Parse the reply line from the server. Returns the reply code. */
	private int parseReply(String reply) {
		StringTokenizer argv = new StringTokenizer(reply);
		int i = Integer.parseInt(argv.nextToken());
		return i;
	}
	/* Destructor. Closes the connection if something bad happens. */
	protected void finalize() throws Throwable {
		if(isConnected) {
			close();
		}
		super.finalize();
	}
}
