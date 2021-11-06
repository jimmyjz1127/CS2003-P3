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
}
