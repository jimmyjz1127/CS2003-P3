/**
  * Simple example creating a directory and a file.
  *
  * Saleem Bhatti, https://saleem.host.cs.st-andrews.ac.uk/
  * 28 Aug 2019
  *
  */

package messageboard;
import java.io.*;

public class DirAndFile 
{

  public static void createDirAndFile(String dirName, String fileName, String text)
  {
    dirName = (String) ("messageboard/" + dirName);
    File dir = new File(dirName);

    if (!dir.exists()) 
    {
      dir.mkdir();
      System.out.println("++ Created directory: " + dirName);
    }

    fileName = dirName + File.separator + fileName;
    File file = new File(fileName);

    if (file.exists()) 
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
    catch (IOException e) 
    {
      System.out.println("IOException - write(): " + e.getMessage());
    }

    System.out.println("++ Wrote \"" + text + "\" to file: " + fileName);
  }

  public static void main(String[] args)
  {
    createDirAndFile("testDirectory", "testFile", "testMessage");
  }
}
