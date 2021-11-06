------------------------------------------------
FILE DESCRIPTIONS:

    src/ : contains the primary client-server files 
      (I have included a Basic and Extended version of the DMBClient and DMBServer application)
        - DMBSimpleServer.java & DMBSimpleClient.java :
            - Constitute a Basic DMB client-server application that satisfy practical requirements (R1 - R6) of practical instructions
        - DMBServerExt.java & DMBClientExt.java :
            - Constitute an Extended DMB client-server application that satisfy all requirements (R1 - R10) of practical instructions
        - CS2003-usernames-2021.csv : csv containing all usernames in CS2003 and respective port numbers

    auxiliary/ : contains auxiliary files used by the client-server files
        - Configuration.java : configuration object for accessing properties in properties file 
        - SimpleObjectQueue.java : queue object for storing java Objects
        - TimeStamp.java : time stamp object  
    
    messageboard/ : contains all files pertaining to the message board creation and data retrieval (used by client-server files)
        - DirAndFile.java : Used by server to create directories and Files for messages sent to server 
        - MessageFinder.java : Used by server to retrieve messages from their files and directories
        - index.php : generates and updates the message board webpage
        - messageBoard/ also contains all directories containing message files

    exceptions/ : contains all custom exceptions thrown by application
        - QueueFullException.java : thrown by SimpleObjectQueue.java
        - QueueEmptyException.java : thrown by SimpleObjectQueue.java

    properties/ : contains all properties files
        - DMBProperties.properties : properties file for DMB Client and Server files

------------------------------------------------
HOW TO RUN:

    CS2003-P3 parent folder must be moved to nginx_default directory

    properties/DMBProperties.properties file must be edited : 
        - serverAddress must be changed to your host server
        - serverPort must be changed to your port number
    
    To run DMBSimpleClient.java & DMBSimpleServer.java:
        - DMBSimpleServer.java 
            - Must be run from your host server 
            - Control-C to terminate
        - DMBSimpleClient.java 
            - can be run from any other lab machine or host server 
            - takes 1 argument, the message to the DMBSimpleServer

    To run DMBClientExt.java & DMBServerExt.java:
        - DMBServerExt.java 
            - Must be run from you host server
            - Control-C to terminate
        - DMBClientExt.java: can be run from any other lab machine or host server
            - Takes no arguments
            - after executing, client uses scanner to wait for client requests 
            - User must type PDU (e.g. ::to <username> <message>) and click enter on their keyboard to send PDU to server
            - Control-C to terminate
    

------------------------------------------------
FILE TREE:

CS2003-P3/
├── cs2003-net2.odt
├── TestingScreenShots/
├── README.md
└── src/
    ├── CS2003-usernames-2021.csv
    ├── DMBClient.class
    ├── DMBClientExt.class
    ├── DMBClientExt.java
    ├── DMBClient.java
    ├── DMBServer.class
    ├── DMBServerExt.class
    ├── DMBServerExt.java
    ├── DMBServer.java
    |
    ├── exceptions/
    │   ├── QueueEmptyException.class
    │   ├── QueueEmptyException.java
    │   ├── QueueFullException.class
    │   └── QueueFullException.java
    ├── auxiliary/
    │   ├── Configuration.class
    │   ├── Configuration.java
    │   ├── SimpleObjectQueue.class
    │   ├── SimpleObjectQueue.java
    │   ├── TimeStamp.class
    │   └── TimeStamp.java
    ├── messageboard/
    │   ├── DirAndFile.class
    │   ├── DirAndFile.java
    │   ├── index.php
    │   ├── MessageFinder.class
    │   └── MessageFinder.java
    └── propertiesFiles/
        └── DMBProperties.properties