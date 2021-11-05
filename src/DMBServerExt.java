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

    private static SimpleObjectQueue clientQ;
    private static SimpleObjectQueue messageQ;
    private static SimpleObjectQueue timeStampQ;
    private static SimpleObjectQueue fetchClientQ;
    private static SimpleObjectQueue fetchDateQ;
    private static SimpleObjectQueue errorClientQ;
    private static SimpleObjectQueue errorResponseQ;

    private static int maxClients;
    private static int maxMessages;
    private static LogFileWriter logFileWriter;
    private static String logFile;
    private static String boardDirectory;
    private static String directoryFile;

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
            logFileWriter = configuration.logFileWriter;
            logFile = configuration.logFile;
            boardDirectory = configuration.boardDirectory;
            directoryFile = configuration.directoryFile;
        }
        catch (Exception e){e.printStackTrace();}
    } 

    public static void startServer()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started\n");
        }
        catch (IOException e){e.printStackTrace();}

        clientQ = new SimpleObjectQueue("ClientQ", maxClients);//for storing clients sending messages to server
        messageQ = new SimpleObjectQueue("MessageQ", maxMessages);//for storing client messages
        timeStampQ = new SimpleObjectQueue("timeStampQ", maxMessages);//for storing message timestamps
        fetchClientQ = new SimpleObjectQueue("fetchClientQ", maxClients);//for storing clients making a message fetch request
        fetchDateQ = new SimpleObjectQueue("fetchDateQ", maxClients);//for the dates of client fetch requests 
        errorClientQ = new SimpleObjectQueue("errorClientQ", maxClients);//for clients who made invalid requests 
        errorResponseQ = new SimpleObjectQueue("errorResponseQ", maxClients);//for response messagse to clients who made bad requests
    }

    //start server and execute protocol
    public static void main(String[] args)
    {
        setupConfiguration();
        startServer();

        while(true)// CTRL-C to quit server 
        {
            Socket client = null;
            try
            {
                client = serverSocket.accept();
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
                //iterate through all clients and messages
                while (!clientQ.isEmpty() && !messageQ.isEmpty())
                {
                    Socket c = (Socket) clientQ.remove();
                    String pdu = (String) messageQ.remove();
                    PrintWriter tx = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

                    String[] pduArr = pdu.split(" ", 3);
                    if (pduArr.length == 3)
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
                            tx.println("::received");
                        }
                        else if (pduArr[0].equals("::fetch"))//if pdu from client is a fetch request 
                        {
                            String date = pduArr[2];
                            if (Pattern.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$", date))
                            {
                                String response = MessageFinder.findMessages(date);
                                System.out.println("Sending retrieved messages");
                                tx.println(response);
                            }
                            else
                            {
                                tx.println("::error");
                            }
                        }
                        else//If pdu from client is neither a message or fetch request
                        {
                            tx.println("Invalid Format! Invalid Message! \n::to <username> <message>\n::fetch <username> <date>\n");
                        }
                        tx.flush();
                        tx.close();
                    }
                }
            }
            catch (QueueFullException e) { System.err.println(e); }
            catch (QueueEmptyException e) { System.err.println(e); }
            catch (IOException e) { System.err.println(e); }
        }
    }
    
}