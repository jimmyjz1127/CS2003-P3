/**
  * Daily Message Board Server 
  *
  * based on code by Saleem Bhatti, 01 Oct 2019
 */
import java.io.*;
import java.net.*;

import exceptions.*;
import messageboard.*;
import auxiliary.*;

public class DMBServer
{
    private static Configuration configuration;

    private static int port;
    private static ServerSocket serverSocket;

    private static SimpleObjectQueue clientQ;//Queue for storing client connections
    private static SimpleObjectQueue messageQ;//Queue for storing transmission from client connections
    private static SimpleObjectQueue timeStampQ;//Queue for storing time stamps of client connections
    private static int maxClients;//Max number of clients that can be connected to server at any time
    private static int maxMessages;//Max number of messages that can be sent to server at any time

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
        catch (Exception e){e.printStackTrace();}
    } 

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
        catch (IOException e) {e.printStackTrace();}

        clientQ = new SimpleObjectQueue("ClientQ", maxClients);
        messageQ = new SimpleObjectQueue("MessageQ", maxMessages);
        timeStampQ = new SimpleObjectQueue("timeStampQ", maxMessages);
    }

    /**
     * start server and run protocol
     */
    public static void main(String[] args)
    {
        setupConfiguration();
        startServer();

        while(true) //CTRL-C to quit server
        {
            Socket client = null;
            try
            {
                client = serverSocket.accept();
                System.out.println("Connection Accepted : " + client);
            }
            catch (SocketTimeoutException e){System.err.println("SocketTimeoutException: " + e.getMessage());}
            catch (IOException e) {System.err.println("IO Exception: " + e.getMessage());}

            try
            {
                if (client != null)
                {
                    clientQ.add(client);//add client to queue
                    TimeStamp timeStamp = new TimeStamp();//time stamp of client connection
                    timeStampQ.add(timeStamp);//add timestamp of client connection to queue
                }
                //check transmission for each client
                for (int i = 0; i < clientQ.size(); i++)
                {
                    Socket c = (Socket) clientQ.get(i);

                    if (c != null)
                    {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        String message = bufferedReader.readLine();
                        if (message != null)
                        {
                            messageQ.add(message);//add message to queue
                            clientQ.delete(c);//delete client from queue (won't need it anymore)
                        }
                    }
                }
                while (!messageQ.isEmpty())
                {
                    String message = (String) messageQ.remove();
                    TimeStamp timeStamp = (TimeStamp) timeStampQ.remove();
                    
                    String date = timeStamp.getSimpleDateFormat();
                    String dateAndTime = timeStamp.getSimpleDateTimeFormat();
                    
                    DirAndFile.createDirAndFile(date, dateAndTime, message);//create message text file
                    System.out.println("Message File Created: " + date + "/" + dateAndTime + "\n\n");
                }

            }//try
            catch (QueueFullException e) { System.err.println("QueueFullException: " + e.getMessage());}
            catch (QueueEmptyException e) { System.err.println("QueueEmptyException: " + e.getMessage());}
            catch (IOException e) { System.err.println("IOException: " + e.getMessage());}
        }//while
    }//main
    
}