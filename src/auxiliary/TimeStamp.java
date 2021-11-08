package auxiliary;
/**
  * TimeStamp object
  *
  * Saleem Bhatti, https://saleem.host.cs.st-andrews.ac.uk/
  * 28 Aug 2019
  *
  */
import exceptions.*;
import java.util.*;
import java.text.*;

public class TimeStamp 
{
  private Date d;//date object
  private String dtf;//date and time format (full timestamp format)
  private String df;//date format
  private SimpleDateFormat sdtf;
  private SimpleDateFormat sdf;
  private String dateTime;//full timestamp
  private String date;//date 

  /**
   * Constructor
   */
  public TimeStamp()
  {
    d = new Date();
    dtf = new String("yyyy-MM-dd_HH-mm-ss.SSS");
    df = new String("yyyy-MM-dd");
    sdtf = new SimpleDateFormat(dtf);
    sdf = new SimpleDateFormat(df);
    dateTime = sdtf.format(d);
    date = sdf.format(d);
  }

  /**
   * returns the date portion of the timestamp YYYY-mm-dd
   */
  public String getSimpleDateFormat()
  {
    return date;
  }

  /**
   * returns the full timestamp YYYY-mm-dd_HH-mm-ss.SSS
   */
  public String getSimpleDateTimeFormat()
  {
    return dateTime;
  }
}
