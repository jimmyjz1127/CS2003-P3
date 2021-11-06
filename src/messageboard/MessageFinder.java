package messageboard;
import java.io.*;

public class MessageFinder
{
    /**
     * Locates message files for particular day and returns them as a string 
     * @param date : the date of the desired message files (the directory name containing desired message files)
     */
    public static String findMessages(String date)
    {
        String messages = "";
        File directory = new File("messageboard/" + date);
        if (!directory.exists())//if there are no messages for given date
        {
            return "::none\n";
        }
        else//if there are messages for a given date
        {
            File[] files = directory.listFiles();
            if (files.length != 0)//if there are message files for a particular date directory
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
                }//for
            }//if
        }//else
        System.out.println("Retrieved messages from: " + date + "\n");
        return messages;
    }//findMessages
}