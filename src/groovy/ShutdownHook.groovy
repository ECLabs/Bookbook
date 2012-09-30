import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

public class ShutdownHook implements ApplicationContextAware
{
  public static final Log log = LogFactory.getLog(ShutdownHook.class)

  public void setApplicationContext(ApplicationContext applicationContext)
  {
	  // Registers a shutdown hook for the Neo4j and index service instances
	  // so that it shuts down nicely when the VM exits (even if you
	  // "Ctrl-C" the running example before it's completed)
	  Runtime.getRuntime().addShutdownHook( new Thread()
	  {
		  @Override
		  public void run()	{
			  applicationContext.graphDb.shutdown();
			  applicationContext.close();
		  }
	  } );

	log.info("Shutdown hook setup...")
  }
}