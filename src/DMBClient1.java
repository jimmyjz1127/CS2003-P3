import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashMap;


import auxiliary.*;
import exceptions.*;

/**
  * Daily Message Board Client
  *
  * based on code by Saleem Bhatti, 28 Aug 2019
  *
  */
public class DMBClient1 
{
    static HashMap<String,Integer> userMap = new HashMap<String, Integer>();

    static int maxTextLen_ = 256;
    static Configuration c_;

    // from configuration file
    static String server; // FQDN
    static int port; //server port

    public static void main(String[] args)
    {
        //Socket       connection;
        PrintWriter  ptx;
        OutputStream tx;
        InputStream  rx;
        byte[]       buffer;
        String       line;
        String       s = new String("");
        String       quit = new String("quit");
        int          r;

        try 
        {
            setupUsers();
            if (args.length == 0)
            {
                boolean keepConnection = true;
                while (true)//CTRL-C to close connection
                {
                    Scanner user = new Scanner(System.in);
                    line = user.nextLine();
                    if (line.substring(0,4).equals("::to "))
                    {
                        
                        int usernameIndex = line.indexOf(" ");//index of the start of username of input string
                        String usernameAndMessage = line.substring(usernameIndex + 1);
                        int messageIndex = usernameAndMessage.indexOf(" ") + usernameIndex;
                        String username = line.substring(usernameIndex + 1, messageIndex + 1);//username only
                        String message = line.substring(messageIndex+1);//message only 
                        String fullMessage = System.getProperty("user.name") + " " + message;//append username to message 
                        
                        int userPort = 0;
                       
                        if (userMap.containsKey(username))//if username is a valid username
                        {
                            userPort = userMap.get(username);//retrive port number from map
                        }
                        else
                        {
                            System.out.println("Invalid Username!");
                            continue;
                        }

                        server = username + ".host.cs.st-andrews.ac.uk";

                        Socket connection = startClient(server, userPort);
                        ptx = new PrintWriter(connection.getOutputStream(), true);
                        rx = connection.getInputStream();
                    
                        r = message.length();
                        if (r > maxTextLen_) 
                        {
                            System.out.println("++ You entered more than " + maxTextLen_ + "bytes ... truncating.");
                            r = maxTextLen_;
                        }
                        System.out.println("Sending " + r + " bytes");

                        ptx.println(fullMessage); //send message to server
                        connection.close();
                    }
                    else
                    {
                        System.out.println("Invalid Message! ::to <username> <message>");
                        continue;
                    }
                }
            }
        
        }
        catch (IOException e) 
        {
            System.err.println("IO Exception: " + e.getMessage());
        }
    } // main

    //sends request to server for connection
    static Socket startClient(String hostname, int portnumber)
    {
        Socket connection = null;

        try 
        {
            InetAddress address;
            int         port;

            address = InetAddress.getByName(hostname);
            port = portnumber;

            connection = new Socket(address, port); // make a socket

        System.out.println("++ Connecting to " + hostname + ":" + port + " -> " + connection);
        }

        catch (UnknownHostException e) 
        {
        System.err.println("UnknownHost Exception: " + hostname + " "
                            + e.getMessage());
        }
        catch (IOException e) 
        {
        System.err.println("IO Exception: " + e.getMessage());
        }

        return connection;
    } // startClient

    /**
    * Reads CS2003-usernames-2021.csv file and saves the usernames and port numbers as key value pairs in userMap Hashmap
    */
    public static void setupUsers() 
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("CS2003-usernames-2021.csv"));
            String line = br.readLine();
            while ((line = br.readLine())  != null)
            {
                String[] keyValue = line.split(",");
                userMap.put((String) keyValue[0], Integer.parseInt(keyValue[1]));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

} // DMBClient
