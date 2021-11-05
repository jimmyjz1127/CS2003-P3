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
        byte[]       buffer;
        String       line;
        String       quit = new String("quit");
        int          r;

        try 
        {
            setupUsers();
            if (args.length == 0)
            {
                while (true)//CTRL-C to close connection
                {
                    Scanner user = new Scanner(System.in);
                    line = user.nextLine();
                    String[] lineArr = line.split(" ", 3);
                    
                    if (lineArr.length != 3)
                    {
                        System.out.println("Invalid Message! \n::to <username> <message>\n::fetch <username> <date>\n");
                        continue;
                    }
                    else if (lineArr[0].equals("::to"))//send message
                    {
                        String username = lineArr[1];
                        String message = lineArr[2];
                        String pdu = System.getProperty("user.name") + " " + message;

                        int userPort = 0;
                        if (userMap.containsKey(username))//if username is a valid username
                        {
                            userPort = userMap.get(username);//retrive port number from map
                        }
                        else
                        {
                            System.out.println("Invalid Username!\n");
                            continue;
                        }

                        server = username + ".host.cs.st-andrews.ac.uk";

                        Socket connection = startClient(server, userPort);
                        PrintWriter tx = new PrintWriter(connection.getOutputStream(), true);
                        BufferedReader rx = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    
                        r = message.length();
                        if (r > maxTextLen_) 
                        {
                            System.out.println("++ You entered more than " + maxTextLen_ + "bytes ... truncating.");
                            r = maxTextLen_;
                        }
                        System.out.println("Sending " + r + " bytes");

                        tx.println(pdu); //send message to server
                        connection.close();
                    }
                    else if (lineArr[0].equals("::fetch"))//send fetch request 
                    {
                        String username = lineArr[1];
                        String date = lineArr[2];
                        String pdu = lineArr[0] + " " + date;

                        int userPort = 0;
                        if (userMap.containsKey(username))
                        {
                            userPort = userMap.get(username);
                        }
                        else
                        {
                            System.out.println("Invalid Username!\n");
                            continue;
                        }

                        server = username + ".host.cs.st-andrews.ac.uk"; 
                        
                        Socket connection = startClient(server, userPort);
                        PrintWriter tx = new PrintWriter(connection.getOutputStream(), true);
                        BufferedReader rx = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        
                        r = pdu.length();
                        System.out.println("Sending " + r + " bytes");
                        tx.println(pdu);
                        
                        String response;
                        while ((response = rx.readLine()) != null)
                        {
                            System.out.println(response);
                        }
                    }
                    else
                    {
                        System.out.println("Invalid Message! \n::to <username> <message>\n::fetch <username> <date>\n");
                        continue;
                    }
                }
            }
        
        }
        catch (IOException e) {e.printStackTrace();}
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
        catch (UnknownHostException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

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
        catch (IOException e){e.printStackTrace();}
    }

} // DMBClient
