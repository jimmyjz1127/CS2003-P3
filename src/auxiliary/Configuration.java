package auxiliary;

import exceptions.*;
import java.io.*;
import java.net.*;
import java.util.Properties;

public class Configuration
{
    //Setup
    public Properties properties;
    public String propertiesFile = "propertiesFiles/DMBProperties.properties";
    public LogFileWriter logFileWriter;


    //Attributes
    public String serverAddress;
    public int serverPort;
    public String directoryFile;
    public String boardDirectory;
    public String logFile = "cs2003-net2.log";
    public int maxClients;
    public int maxMessages;

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

                if ((property = properties.getProperty("logFile")) != null)
                {
                    logFile = new String(property);
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
            }

            logFileWriter = new LogFileWriter(logFile);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
}
