package ru.alexig;

public class WService implements Selected {
	private String code;
	private String name;
	private String selected;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public WService(String code, String name, String selected) {
		super();
		this.code = code;
		this.name = name;
		this.selected = selected;
	}
	
}
