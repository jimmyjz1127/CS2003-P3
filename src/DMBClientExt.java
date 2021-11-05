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
    static int maxTextLen_ = 256;
    static Configuration c_;
    static String server; // FQDN
    static int port; //server port

    public static void main(String[] args)
    {
        byte[] buffer;
        String pdu;
        int r;
        BufferedReader rx = null;
        PrintWriter tx = null;
        Socket connection = null;

        try 
        {
            setupUsers();
            
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

                    String hostname = username + ".host.cs.st-andrews.ac.uk";
                    connection = startClient(hostname, port);
                    if (connection == null)//if the server is closed 
                    {
                            System.out.println("Server Connction Closed!");
                            continue;
                    }
                    tx = new PrintWriter(connection.getOutputStream(), true);
                    rx = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    //::to <client username> <message>
                    String fullPDU = pduArr[0] + " " + System.getProperty("user.name") + " " + pduArr[2];

                    r = fullPDU.length();
                    if (r > maxTextLen_) 
                    {
                        System.out.println("++ You entered more than " + maxTextLen_ + "bytes ... truncating.");
                        r = maxTextLen_;
                    }
                    System.out.println("Sending " + r + " bytes");
                    tx.println(fullPDU);

                    String response;
                    while ((response = rx.readLine()) != null)//read and print response from server
                    {
                        System.out.println(response);
                    }
                }
                else
                {
                    System.out.println("Invalid Format! \n::to <username> <message>\n::fetch <username> <date>\n");
                    continue;
                }
            }
        }
        catch (IOException e) {System.out.println(e);}
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
        catch (ConnectException e) {}
        catch (UnknownHostException e) {System.out.println(e);}
        catch (IOException e) {System.out.println(e);}

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
