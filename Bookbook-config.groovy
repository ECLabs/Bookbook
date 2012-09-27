// Note: this file goes into a location on the server... it is just here for reference.
// see config.groovy for more information
grails {
	mail {
		host = "smtp.gmail.com"
		port = 465
		username = "username@gmail.com"
		password = "password"
		props = ["mail.smtp.auth":"true",
				 "mail.smtp.socketFactory.port":"465",
				 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
				 "mail.smtp.socketFactory.fallback":"false"]
	}
}