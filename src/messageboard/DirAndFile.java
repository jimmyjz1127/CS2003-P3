package messageboard;
/**
  * Simple example creating a directory and a file.
  *
  * based on code by Saleem Bhatti, 28 Aug 2019
  *
  */

import java.io.*;

public class DirAndFile 
{
    /**
     * Creates directory and files for messages sent to server
     * @param dirName : the name of the directory containing containing messages for a particular day
     * @param fileName : name of message file
     * @param text : the message content
    */
    public static void createDirAndFile(String dirName, String fileName, String text)
    {
        dirName = (String) ("messageboard/" + dirName);
        File dir = new File(dirName);

        //if the directory doesn't exist i.e. no messages for a given day
        if (!dir.exists()) 
        {
            dir.mkdir();
            System.out.println("++ Created directory: " + dirName);
        }

        fileName = dirName + File.separator + fileName;
        File file = new File(fileName);

        if (file.exists()) //if the message file already exists
        {
            System.out.println("++ File already exists: " + fileName);
            System.exit(0);
        }

        try 
        {
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.flush();
            fw.close();
        }
        catch (IOException e) {System.out.println("IOException - write(): " + e.getMessage());}

        System.out.println("++ Wrote \"" + text + "\" to file: " + fileName + "\n");
    }//createDirAndFile

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
