class BootStrap {

    def init = { 
		println "Good morning!"
    }
    def destroy = {
		println "Goodnight!"
		// shut down the graph database - initated in resources.groovy
		def ctx = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getAttribute(org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.APPLICATION_CONTEXT);
		ctx.graphDb.shutdown();
    }
}
