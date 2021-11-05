import java.io.*;
import java.net.*;
import java.util.regex.*;

import exceptions.*;
import messageboard.*;
import auxiliary.*;

public class DMBServer1
{
    private static Configuration configuration;
    
    private static int port;
    private static ServerSocket serverSocket;

    private static SimpleObjectQueue clientQ;
    private static SimpleObjectQueue messageQ;
    private static SimpleObjectQueue timeStampQ;
    private static SimpleObjectQueue fetchClientQ;
    private static SimpleObjectQueue fetchDateQ;

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
                    clientQ.add(client);
                    TimeStamp timeStamp = new TimeStamp();
                    timeStampQ.add(timeStamp);
                }
                //Check transmissions from each client
                for (int i = 0; i < clientQ.size(); i++)
                {
                    Socket c = (Socket) clientQ.get(i);

                    if (c != null)
                    {
                        BufferedReader rx = new BufferedReader(new InputStreamReader(c.getInputStream()));
                       
                        String message = rx.readLine();
                        if (message != null)
                        {
                            String[] messageArr = message.split(" ", 2);
                            if (messageArr[0].equals("::fetch"))//if the client transmission is a message fetch request 
                            {
                                fetchClientQ.add(c);
                                String date = messageArr[1];
                                fetchDateQ.add(date);
                            }
                            else//if the client transmission is a message
                            {
                                messageQ.add("from " + message);
                            }
                            clientQ.delete(c);
                        }
                        
                    }
                }
                //iterate through all messages sent to server
                while (!messageQ.isEmpty())
                {
                    String message = (String) messageQ.remove();
                    TimeStamp timeStamp = (TimeStamp) timeStampQ.remove();

                    String date = timeStamp.getSimpleDateFormat();//Get the date part of timestamp YYYY-MM-DD
                    String dateAndTime = timeStamp.getSimpleDateTimeFormat();//Get both the date and time YYYY-MM-DD_HH-mm-ss.SSS
                    
                    //create directory and file for message
                    DirAndFile.createDirAndFile(date, dateAndTime, message);
                }
                //iterate through all fetch requests sent to server
                while (!fetchClientQ.isEmpty() && !fetchDateQ.isEmpty())
                {
                    Socket fetchClient = (Socket) fetchClientQ.remove();
                    String date = (String) fetchDateQ.remove();
                    PrintWriter tx = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            
                    if (Pattern.matches("^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$", date))
                    {
                        String response = MessageFinder.findMessages(date);
                        tx.println(response);
                    }
                    else
                    {
                        tx.println("Invalid Date Format! YYYY-MM-DD\n");
                    }
                    tx.flush();
                    tx.close();
                }

            }
            catch (QueueFullException e) { System.err.println(e); }
            catch (QueueEmptyException e) { System.err.println(e); }
            catch (IOException e) { System.err.println(e); }
        }
    }
    
}