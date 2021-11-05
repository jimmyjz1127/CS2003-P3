package messageboard;
import java.io.*;


public class MessageFinder
{
    public static String findMessages(String date)
    {
        String messages = "";
        File directory = new File("messageboard/" + date);
        if (!directory.exists())
        {
            return "No Messages for " + date;
        }
        else
        {
            File[] files = directory.listFiles();
            if (files.length != 0)
            {
                messages += "::messages " + date + "\n";
                for (File file : files)
                {
                    try
                    {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String message = bufferedReader.readLine();

                        messages += "  " + file.getName() + " " + message + "\n";
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Retrieved messages from: " + date + "\n");
        return messages;
    }
}