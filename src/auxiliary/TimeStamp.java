package auxiliary;

/**
  * Simple example of a timestamp.
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
  private Date d;
  private String dtf;
  private String df;
  private SimpleDateFormat sdtf;
  private SimpleDateFormat sdf;
  private String dateTime;
  private String date;

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

  public String getSimpleDateFormat()
  {
    return date;
  }

  public String getSimpleDateTimeFormat()
  {
    return dateTime;
  }
}
