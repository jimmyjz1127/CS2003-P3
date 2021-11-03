import java.io.*;
import java.net.*;

import exceptions.*;
import webpage.*;
import auxiliary.*;

public class DMBServer1
{
    private static Configuration configuration;

    private static int port;
    private static ServerSocket serverSocket;
    private static SimpleObjectQueue clientQ;
    private static SimpleObjectQueue messageQ;
    private static SimpleObjectQueue timeStampQ;
    private static int maxClients;
    private static int maxMessages;
    private static LogFileWriter logFileWriter;
    private static String logFile;
    private static String boardDirectory;
    private static String directoryFile;

    private static final String terminationPhrase = "!bye!";
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } 

    public static void startServer()
    {
        // logFileWriter = new LogFileWriter(logFile);
        // logFileWriter.writeLog("Logging Started.");

        try
        {
            serverSocket = new ServerSocket(port);
            // logFileWriter.writeLog("Server socket started : " + serverSocket, true);
            System.out.println("Server Started");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        clientQ = new SimpleObjectQueue("ClientQ", maxClients);
        messageQ = new SimpleObjectQueue("MessageQ", maxMessages);
        timeStampQ = new SimpleObjectQueue("timeStampQ", maxMessages);
    }

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
            catch (SocketTimeoutException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

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
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                       
                        String message = bufferedReader.readLine();
                        
                        if (message != null)
                        {
                            System.out.println(message);
                            messageQ.add("from " + message);

                            clientQ.delete(c);
                        }
                        
                    }
                }
                //iterate through all messages sent to server
                while (!messageQ.isEmpty())
                {
                    String message = (String) messageQ.remove();
                    
                    TimeStamp timeStamp = (TimeStamp) timeStampQ.remove();
                    String date = timeStamp.getSimpleDateFormat();
                    String dateAndTime = timeStamp.getSimpleDateTimeFormat();
                    
                    //create directory and file for message
                    DirAndFile.createDirAndFile(date, dateAndTime, message);
                    System.out.println("Message File Created: " + date + "/" + dateAndTime + "\n\n");
                }

            }
            catch (QueueFullException e) { System.err.println(e); }
            catch (QueueEmptyException e) { System.err.println(e); }
            catch (IOException e) { System.err.println(e); }
        }
    }
    
}