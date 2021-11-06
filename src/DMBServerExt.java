/**
  * Daily Message Board Server 
  *
  * based on code by Saleem Bhatti, 01 Oct 2019
 */
import java.io.*;
import java.net.*;
import java.util.regex.*;

import exceptions.*;
import messageboard.*;
import auxiliary.*;

public class DMBServerExt
{
    private static Configuration configuration;
    
    private static int port;
    private static ServerSocket serverSocket;

    private static SimpleObjectQueue clientQ;//Queue for client connections
    private static SimpleObjectQueue messageQ;//Queue for transmission sent through client connections
    private static SimpleObjectQueue timeStampQ;//Queue for time stamps of client connections

    private static int maxClients;//max number of clients that can be connected to server at any time
    private static int maxMessages;//max number of messages that the server can receive from clients at any time

    /**
     * Start server and run protocol
     */
    public static void main(String[] args)
    {
        setupConfiguration();
        startServer();

        while(true)// CTRL-C to quit server 
        {
            Socket client = null;
            try
            {
                client = serverSocket.accept();//client connection
                System.out.println("Connection Accepted : " + client);
            }
            catch (SocketTimeoutException e){e.printStackTrace();}
            catch (IOException e){e.printStackTrace();}

            try
            {
                if (client != null)
                {
                    clientQ.add(client);//add client connection to queue
                    TimeStamp timeStamp = new TimeStamp();
                    timeStampQ.add(timeStamp);//add timestamp of client connection to queue
                }
                //Check transmissions from each client
                for (int i = 0; i < clientQ.size(); i++)
                {
                    Socket c = (Socket) clientQ.get(i);

                    if (c != null)
                    {
                        BufferedReader rx = new BufferedReader(new InputStreamReader(c.getInputStream()));
                       
                        String pdu = rx.readLine();
                        if (pdu != null)
                        {
                            messageQ.add(pdu);//add message to queue
                        }
                        
                    }
                }
                //check transmission for each client
                while (!clientQ.isEmpty() && !messageQ.isEmpty())
                {
                    Socket c = (Socket) clientQ.remove();
                    String pdu = (String) messageQ.remove();
                    PrintWriter tx = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

                    String[] pduArr = pdu.split(" ", 3);
                    if (pduArr.length == 3)//check transmission has proper format <::fetch/::to> <username> <message/date>
                    {
                        if (pduArr[0].equals("::to"))//if pdu from client is a message
                        {
                            TimeStamp timeStamp = (TimeStamp) timeStampQ.remove();
                            String date = timeStamp.getSimpleDateFormat();//Get the date part of timestamp YYYY-MM-DD
                            String dateAndTime = timeStamp.getSimpleDateTimeFormat();//Get both the date and time YYYY-MM-DD_HH-mm-ss.SSS
                            String username = pduArr[1];
                            String message = "::from " + pduArr[1] + " " + pduArr[2];

                            //create directory and file for message
                            DirAndFile.createDirAndFile(date, dateAndTime, message);
                            tx.println("::received\n");
                        }
                        else if (pduArr[0].equals("::fetch"))//if pdu from client is a fetch request 
                        {
                            String date = pduArr[2];
                            if (Pattern.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$", date))//check date matches proper format
                            {
                                String response = MessageFinder.findMessages(date);
                                System.out.println("Sending retrieved messages\n");
                                tx.println(response);
                            }
                            else//if date is of incorrect format
                            {
                                tx.println("::error\n");
                            }
                        }
                        else//If pdu from client is neither a message or fetch request
                        {
                            tx.println("Invalid Format! Invalid Message! \n::to <username> <message>\n::fetch <username> <date>\n");
                        }
                        tx.flush();
                        tx.close();
                    }//if
                }//while
            }//try
            catch (QueueFullException e) {System.err.println("QueueFullException: " + e.getMessage());}
            catch (QueueEmptyException e) {System.err.println("QueueEmptyException: " + e.getMessage());}
            catch (IOException e) {System.err.println("IOExcpetion: " + e.getMessage());}
        }//while
    }//main

    /**
     * Imports all necessary configuration details and initializes respective class attributes
     */
    public static void setupConfiguration()
    {
        try
        {
            configuration = new Configuration("propertiesFiles/DMBProperties.properties");

            port = configuration.serverPort;
            maxClients = configuration.maxClients;
            maxMessages = configuration.maxMessages;
        }
        catch (Exception e){System.err.println(e.getMessage());}
    } //setupConfiguration

    /**
     * Starts server by opening server socket on port
     */
    public static void startServer()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started\n");
        }
        catch (IOException e){System.err.println("IOException: " + e.getMessage());}

        clientQ = new SimpleObjectQueue("ClientQ", maxClients);//for storing clients sending messages to server
        messageQ = new SimpleObjectQueue("MessageQ", maxMessages);//for storing client messages
        timeStampQ = new SimpleObjectQueue("timeStampQ", maxMessages);//for storing message timestamps
    }//startServer
}