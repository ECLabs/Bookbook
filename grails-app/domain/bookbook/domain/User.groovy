package bookbook.domain

import org.neo4j.graphdb.Node;

class User {

	static constraints = {
	}
	
	Long userId
	String firstName
	String middleName
	String lastName
	String userName
	String email
	String password
	String photoUrl
	String createDate
	String endDate
	String updateDate
	String lastLoginDate
	String userTypeCode // i.e. user, superuser, author, guest, auditor, placeholder (used for popular reviewers)
	String aboutMe // 140 characters of text about the user
	String activationMethod // i.e. facebook, openid, native
	Long numberOfFollowers // from index
	Long numberFollowing // from index
	Node underlyingNode
	
	CheckIn[] recentCheckIns // TODO: All checkins paged
	BookList[] bookLists // TODO: Standard lists: "want to read", "have read", "currently reading" - dynamically generated based on recent checkins
	
	
	User(node) {
		this.underlyingNode = node
	}
	
	public Long getUserId() { underlyingNode.getProperty("id", null) }
	public String getFirstName() { underlyingNode.getProperty("firstName", null) }
	public String getLastName() { underlyingNode.getProperty("lastName", null) }
	public String getMiddleName() { underlyingNode.getProperty("middleName", null) }
	public String getUserName() { underlyingNode.getProperty("userName", null) }
	public String getEmail() { underlyingNode.getProperty("email", null) }
	public String getPassword() { underlyingNode.getProperty("password", null) }
	public String getPhotoUrl() { underlyingNode.getProperty("photoUrl", null) }
	public String getCreateDate() { underlyingNode.getProperty("createDate", null) }
	public String getEndDate() { underlyingNode.getProperty("endDate", null) }
	public String getUpdateDate() { underlyingNode.getProperty("upateDate", null) }
	public String getLastLoginDate() { underlyingNode.getProperty("lastLoginDate", null) }
	public String getUserTypeCode() { underlyingNode.getProperty("userTypeCode", null) }
	public String getAboutMe() { underlyingNode.getProperty("aboutMe", null) }
	public String getActivationMethod() { underlyingNode.getProperty("activationMethod", null) }
	public String getNumberOfFollowers() { underlyingNode.getProperty("numberOfFollowers", null) }
	public String getNumberFollowing() { underlyingNode.getProperty("numberFollowing", null) }
	
	public void setUserId(Long value) { underlyingNode.setProperty("id", value) }
	public void setFirstName(String value) { underlyingNode.setProperty("firstName", value) }
	public void setLastName(String value) { underlyingNode.setProperty("lastName", value) }
	public void setMiddleName(String value) { if(value != null) underlyingNode.setProperty("middleName", value) }
	public void setUserName(String value) { underlyingNode.setProperty("userName", value) }
	public void setEmail(String value) { underlyingNode.setProperty("email", value) }
	public void setPassword(String value) { underlyingNode.setProperty("password", value) }
	public void setPhotoUrl(String value) { underlyingNode.setProperty("photoUrl", value) }
	public void setCreateDate(String value) { underlyingNode.setProperty("createDate", value) }
	public void setEndDate(String value) { underlyingNode.setProperty("endDate", value) }
	public void setUpdateDate(String value) { underlyingNode.setProperty("updateDate", value) }
	public void setLastLoginDate(String value) { underlyingNode.setProperty("lastLoginDate", value) }
	public void setUserTypeCode(String value) { underlyingNode.setProperty("userTypeCode", value) }
	public void setAboutMe(String value) { underlyingNode.setProperty("aboutMe", value) }
	public void setActivationMethod(String value) { underlyingNode.setProperty("activationMethod", value) }
	public void setNumberOfFollowers(String value) { underlyingNode.setProperty("numberOfFollowers", value) }
	public void setNumberFollowing(String value) { underlyingNode.setProperty("numberFollowing", value) }
	
	static transients = ["underlyingNode", "recentCheckIns","bookLists","numberOfFollowers","numberFollowing"]
}

class User2 {
	Long userId
	String firstName
	String middleName
	String lastName
	String userName
	String email
	String password
	String photoUrl
	String createDate
	String endDate
	String updateDate
	String lastLoginDate
	String userTypeCode // i.e. user, superuser, author, guest, auditor, placeholder (used for popular reviewers)
	String aboutMe // 140 characters of text about the user
	String activationMethod // i.e. facebook, openid, native
	Long numberOfFollowers // from index
	Long numberFollowing // from index
}