package edu.ucsf.mousedatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import edu.ucsf.mousedatabase.objects.MGIResult;

public class MGIConnectTest {


  @SuppressWarnings("serial")
  public static void main(String[] args)
  {
    init();
    if (args.length == 2)
    {

      if (args[0].equals("g"))
      {
        MGIResult r = MGIConnect.doMGIQuery(args[1], MGIConnect.MGI_MARKER, null);
        System.out.println("MGI online: " + !r.isMgiOffline());
        System.out.println("Valid: " + r.isValid());
        System.out.println(r.getName());
        System.out.println(r.getSymbol());
        System.out.println(r.getType());
      }
      else if (args[0].equals("r"))
      {
        MGIResult r = MGIConnect.doMGIQuery(args[1], MGIConnect.MGI_REFERENCE, null);
        System.out.println("MGI online: " + !r.isMgiOffline());
        System.out.println("Valid: " + r.isValid());
        System.out.println(r.getAuthors());
        System.out.println(r.getTitle());
      }
      else if(args[0].equals("p"))
      {
        final int id = Integer.parseInt(args[1]);
        ArrayList<Integer> ids = new ArrayList<Integer>(){{add(id);}};
        HashMap<Integer,Properties> props =  MGIConnect.getPropertiesFromAlleleMgiID(ids, 1);
        for(Properties prop : props.values())
        {
          for(Entry<Object,Object> entry : prop.entrySet())
          {
            System.out.println(entry.getKey() + ": " + entry.getValue());
          }
        }
      }

    }


  }

  private static void init()
  {
    Log.Initialize();
    //MGIConnect.verbose = true;
    //MGIConnect.Initialize("org.postgresql.Driver","jdbc:postgresql://adhoc.informatics.jax.org:5432/mgd?username=jonathan_scoles&password=sc0l3s@mgi");

  }
}
