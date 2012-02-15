// Place your Spring DSL code here
beans = {
	def props = new Properties()
	props.setProperty('storeDir', 'bookbook-neo4j-store')
	graphDb(org.neo4j.kernel.EmbeddedGraphDatabase, 'bookbook-neo4j-store') { }
}
