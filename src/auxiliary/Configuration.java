package auxiliary;
/**
 * Configuration Information
 *
 * based on code written by Saleem Bhatti, Sep 2019
 */

import exceptions.*;
import java.io.*;
import java.net.*;
import java.util.Properties;

public class Configuration
{
    //Setup
    public Properties properties;
    public String propertiesFile = "propertiesFiles/DMBProperties.properties";


    //Attributes
    public String serverAddress;
    public int serverPort;
    public String directoryFile;
    public String boardDirectory;
    public int maxClients;
    public int maxMessages;

    /**
     * Sets up configuration information from properties file
     * @param file : file path to properties file
     */
    public Configuration(String file)
    {
        //if a different properties file is passed to constructor
        if (file != null)
        {
            propertiesFile = file; 
        }

        try
        {
            properties = new Properties();
            InputStream p = getClass().getClassLoader().getResourceAsStream(propertiesFile);
            
            if (p != null)
            {
                //load data from properties file
                properties.load(p);
                String property;
                
                //initialize property attributes
                if((property = properties.getProperty("serverAddress")) != null)
                {
                    serverAddress = new String(property);
                }

                if ((property = properties.getProperty("serverPort")) != null)
                {
                    serverPort = Integer.parseInt(property);
                }

                if ((property = properties.getProperty("directoryFile")) != null)
                {
                    directoryFile = new String(property);
                }

                if ((property = properties.getProperty("boardDirectory")) != null)
                {
                    boardDirectory = new String(property);
                }

                if ((property = properties.getProperty("maxClients")) != null)
                {
                    maxClients = Integer.parseInt(property);
                }

                if ((property = properties.getProperty("maxMessages")) != null)
                {
                    maxMessages = Integer.parseInt(property);
                }

                p.close();
            }//if

        }//try
        catch (NumberFormatException e) {System.err.println("NumberFormatException: " + e.getMessage());}
        catch (IOException e) {System.err.println("IOException: " + e.getMessage());}
        
    }//Configuration constructor
}
