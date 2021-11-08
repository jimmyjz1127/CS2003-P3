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
    
    private static int serverPort;
    private static ServerSocket serverSocket;

    private static SimpleObjectQueue clientQ;//Queue for client connections
    private static SimpleObjectQueue pduQ;//Queue for the content of the transmission sent through client connections (date/message)
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
                            pduQ.add(pdu);//add message to queue
                        }
                        
                    }
                }
                //check transmission for each client
                while (!clientQ.isEmpty() && !pduQ.isEmpty())
                {
                    Socket c = (Socket) clientQ.remove();
                    String pdu = (String) pduQ.remove();
                    PrintWriter tx = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

                    String[] pduArr = pdu.split(" ", 3);
                    if (pduArr.length >= 2)//check transmission has proper format <::fetch/::from> <username> <message/date(optiona)>
                    {
                        if (pduArr[0].equals("::from") && pduArr.length == 3)//if client PDU is ::to command
                        {
                            TimeStamp timeStamp = (TimeStamp) timeStampQ.remove();
                            String date = timeStamp.getSimpleDateFormat();//Get the date part of timestamp YYYY-MM-DD
                            String dateAndTime = timeStamp.getSimpleDateTimeFormat();//Get both the date and time YYYY-MM-DD_HH-mm-ss.SSS
                            String username = pduArr[1];
                            String message = pduArr[0] + " " + pduArr[1] + " " + pduArr[2];//::from <username> <message>

                            //create directory and file for message
                            DirAndFile.createDirAndFile(date, dateAndTime, message);
                            tx.println("::received\n");
                        }
                        else if (pduArr[0].equals("::fetch"))//if PDU is ::fetch command
                        {
                            if (pduArr.length == 2)//if client did not specify a date
                            {
                                TimeStamp timeStamp = new TimeStamp();
                                String today = timeStamp.getSimpleDateFormat();//today's date
                                String response = DirAndFile.findMessages(today);//today's messages
                                tx.println(response);
                            }
                            else if (pduArr.length == 3)//if client did specify a date 
                            {
                                String date = pduArr[2];//the date specified by client
                                if (Pattern.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$", date))//check date matches proper format
                                {
                                    String response = DirAndFile.findMessages(date);
                                    System.out.println("Sending retrieved messages\n");
                                    tx.println(response);
                                }
                                else//if date is of incorrect format
                                {
                                    tx.println("::error\n");
                                }
                            }
                        }
                        else//If pdu from client is neither a message or fetch request
                        {
                            tx.println("Invalid Format! \n::to <username> <message>\n::fetch <username> <date>\n");
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

            serverPort = configuration.serverPort;
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
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server Started\n");
        }
        catch (IOException e){System.err.println("IOException: " + e.getMessage());}

        clientQ = new SimpleObjectQueue("ClientQ", maxClients);//for storing clients sending messages to server
        pduQ = new SimpleObjectQueue("pduQ", maxMessages);//for storing client messages
        timeStampQ = new SimpleObjectQueue("timeStampQ", maxMessages);//for storing message timestamps
    }//startServer
}