class BootStrap {

    def init = { 
		println "Good morning!"
    }
    def destroy = {
		println "Goodnight!"
		// shut down the graph database - initated in resources.groovy
		graphDb.shutdown();
    }
}
