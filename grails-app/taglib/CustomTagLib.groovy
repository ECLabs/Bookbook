class CustomTagLib {

	def truncate = { attrs, body ->
	        if (body().length() > attrs.maxlength.toInteger()) {
	            out << """<abbr title="${body()}">${body()[0..attrs.maxlength.toInteger() - 1]}...(truncated)</abbr>"""
	
	        } else {
	            out << body()
	        }
	    }
}