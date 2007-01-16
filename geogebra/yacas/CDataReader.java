package yacas;

import java.io.DataInputStream;
import java.net.URL;

class CDataReader
{
  
  public CDataReader()
  {
  }
  public int Open(URL source)
  {
    String mark = null;
    in = null;
    try
    {
//      URL source = new URL(getCodeBase(), fileName);
      in = new DataInputStream(source.openStream());
      mark = in.readLine();
//      while(null != (aLine = in.readLine()))
//        System.out.println(aLine);
    }
    catch(Exception e) 
    {
      in = null;
//       e.printStackTrace();
    }

//System.out.println("File type: "+mark+" version "+dataFormatVersion);
    if (in != null)
      return 1;
    return 0;
  }
  public String ReadLine()
  {
    try
    {
      String mark = in.readLine();
      return mark;
    }
    catch (Exception e)
    {
    }
    return null;
  }
  
  public void Close()
  {
    try
    {
      if (in != null)
      {
        in.close();
      }
    }
    catch (Exception e)
    {
    }
    in = null;
  }
  DataInputStream in;
};
