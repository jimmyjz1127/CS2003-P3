import java.io.*;
import java.net.*;

public class DMBServer
{
    private static int port = 22054;
    private static ServerSocket serverSocket;
    private static SimpleObjectQueue clientQ;
    private static SimpleMessageQueue messageQ;
}