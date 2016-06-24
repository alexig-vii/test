package ru.alexig;

public class Url {
	private String keyCode;
	private String url;

	public Url(String keyCode, String url) {
		super();
		this.keyCode = keyCode;
		this.url = url;
	}

	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
