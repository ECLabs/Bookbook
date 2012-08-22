// Place your Spring DSL code here
beans = {
	def props = new Properties()
	props.setProperty('storeDir', 'bookbook-neo4j-store')
	graphDb(org.neo4j.kernel.EmbeddedGraphDatabase, 'bookbook-neo4j-store') { }
	
	multipartResolver(org.springframework.web.multipart.commons.CommonsMultipartResolver) {
		// Max in memory 100kbytes
		maxInMemorySize=10240

		//1Gb Max upload size
		maxUploadSize=1024000000
		
		//uploadTempDir="/tmp"
	}
}
