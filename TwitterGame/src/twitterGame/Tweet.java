package twitterGame;

public class Tweet {
	private String userId;
	private String text;
	
	Tweet() {
		userId = "";
		text = "";
	}
	
	Tweet(String userId, String text) {
		this.userId = userId;
		this.text = text;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return userId + " : " + text;
	}
}