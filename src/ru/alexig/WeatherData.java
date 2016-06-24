package ru.alexig;

import java.util.Date;

public class WeatherData {
	private String keyCode;
	private String temp;
	private String humidity;
	private String bar;
	private Date timestamp;
	
	public WeatherData(String keyCode, String temp, String humidity, String bar, Date timestamp) {
		super();
		this.keyCode = keyCode;
		this.temp = temp;
		this.humidity = humidity;
		this.bar = bar;
		this.timestamp = timestamp;
	}

	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public String getCurrentTemp() {
		return temp;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getBar() {
		return bar;
	}
	public void setBar(String bar) {
		this.bar = bar;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "WeatherData [keyCode=" + keyCode + ", temp=" + temp + ", humidity=" + humidity + ", bar=" + bar
				+ ", timestamp=" + timestamp.toString() + "]";
	}
}
