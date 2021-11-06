/**
  * Daily Message Board Client
  *
  * based on code by Saleem Bhatti, 28 Aug 2019
  *
  */
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.HashMap;

import auxiliary.*;
import exceptions.*;

public class DMBClientExt
{
    static HashMap<String,Integer> userMap = new HashMap<String, Integer>();//store username and ports
    static int maxTextLen = 256;
    static String server; // FQDN
    static int port; //server port

    public static void main(String[] args)
    {
        String pdu;//full transmission text
        BufferedReader rx = null;
        PrintWriter tx = null;
        Socket connection = null;

        try 
        {
            setupUsers();//set up username and port number map
            
            while (true)//CTRL-C to close connection
            {
                Scanner user = new Scanner(System.in);
                pdu = user.nextLine();
                String[] pduArr = pdu.split(" ", 3);
                
                if (pduArr.length == 3)
                {
                    String username = pduArr[1];
                    int port = 0;
                    if (!userMap.containsKey(username))//if the given username is valid
                    {
                        System.out.println("Invalid Username!\n");
                        continue;
                    }
                    else
                    {
                        port = userMap.get(username);
                    }

                    String hostname = username + ".host.cs.st-andrews.ac.uk";//fqdn
                    connection = startClient(hostname, port);//initiate connection with server
                    if (connection == null)//if the server is closed 
                    {
                            System.out.println("Server Connction Closed!");
                            continue;
                    }
                    tx = new PrintWriter(connection.getOutputStream(), true);
                    rx = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    //::to <client username> <message>
                    String fullPDU = pduArr[0] + " " + System.getProperty("user.name") + " " + pduArr[2];

                    //Check if pdu doesn't exceed the max length
                    if (fullPDU.length() > maxTextLen) 
                    {
                        System.out.println("++ You entered more than " + maxTextLen + "bytes ... truncating.");
                        fullPDU = fullPDU.substring(0,maxTextLen-1);
                    }
                    
                    //Send pdu to server
                    System.out.println("Sending " + fullPDU.length() + " bytes");
                    tx.println(fullPDU);

                    //Read response from server
                    String response;
                    while ((response = rx.readLine()) != null)//read and print response from server
                    {
                        System.out.println(response);
                    }
                }
                else//if pdu is of incorrect format
                {
                    System.out.println("Invalid Format! \n::to <username> <message>\n::fetch <username> <date>\n");
                    continue;
                }
            }//while
        }//try
        catch (IOException e) {System.out.println("IOException: " + e.getMessage());}
    } // main

    /**
     * Initiates connection with server by opening client socket
     * @param hostname : the fqdn of server
     * @param portnumber : port that server is running on
     */
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
        catch (ConnectException e) {}//if server is closed - exception handling is done in main()
        catch (UnknownHostException e) {System.out.println("UnknownHostException: " + e.getMessage());}
        catch (IOException e) {System.out.println("IOException: " + e.getMessage());}

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
        catch (IOException e){System.err.println("IOException: " + e.getMessage());}
    }//setupUsers

} // DMBClient
