import java.io.*;
import java.util.*;
import java.text.*;

public class LogFileWriter
{
    public FileWriter fileWriter;
    public SimpleDateFormat sdf;

    public LogFileWriter(String fileName)
    {
        File logFile = new File(filename);
        sdf = new SimpleDateFormat(new String("yyyyMMdd-HHmmss.SSS"));

        try
        {
            if (logFile.exists())
            {
                fileWriter = new FileWriter(fileName, true);
            }
            else
            {
                logFile.createNewFile();
                fileWriter = new FileWriter(filename, true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void writeLog(String logRequest)
    {
        writeLog(logRequest, false);
    }

    public void writeLog(String logRequest, boolean stdout)
    {
        try
        {
            String now = sdf.format(new Date());
            String logEntry = new String(now.toString() + "| " + logRequest + "\n");
            fileWriter.write(logEntry, 0 logEntry.length());
            fileWriter.flush();
            if (stdout)
            {
                System.out.println(logEntry);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}