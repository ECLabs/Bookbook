class BootStrap {

    def init = { servletContext ->
		println "**** Good morning! ****"
		println "Initing custom domain marshaller to remove ['class','id','metaClass'] from JSON renderings."
		grails.converters.JSON.registerObjectMarshaller(new CustomDomainMarshaller())
		
		
		
    }
    def destroy = {
		println "**** Goodnight! ****"
		// shut down the graph database - initated in resources.groovy
		def ctx = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getAttribute(org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.APPLICATION_CONTEXT);
		ctx.graphDb.shutdown();
    
	}
}
