package ru.alexig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static java.util.concurrent.TimeUnit.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Servlet implementation class WeatherProviderServlet
 */
@WebServlet("/provider")
public class WeatherProviderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final long outdated = MILLISECONDS.convert(5, MINUTES);
	private static final String cookieName = "wpookie"; 
    
	private List<Selected> cities = Arrays.asList(
			(Selected) new City("msk","Москва","")
			,(Selected) new City("ekat","Екатеринбург","")
			,(Selected) new City("chel","Челябинск","")
			);
	
	private List<Selected> wServices = Arrays.asList(
			(Selected) new WService("gisMeteo", "Гисметео", "")
			,(Selected) new WService("yaWeather", "Яндекс", "")
			);
	
	private List<Url> urls = Arrays.asList(
			new Url("msk:gisMeteo","https://www.gismeteo.ru/city/daily/4368/")
			,new Url("ekat:gisMeteo","https://www.gismeteo.ru/city/daily/4517/")
			,new Url("chel:gisMeteo","https://www.gismeteo.ru/city/daily/4565/")
			,new Url("msk:yaWeather","https://pogoda.yandex.ru/moscow")
			,new Url("ekat:yaWeather","https://pogoda.yandex.ru/yekaterinburg")
			,new Url("chel:yaWeather","https://pogoda.yandex.ru/chelyabinsk")
			);
	
	private List<WeatherData> weatherDB = new ArrayList<>();

	private String currentCity = "msk";
	private String currentCityName = "Москва";
	private String currentService = "gisMeteo";
	private String currentServiceName = "Гисметео";
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println(request.getRemoteHost() + ":" + request.getRequestURI());

		boolean needSetCookie = false;
		
		request.setCharacterEncoding("UTF-8");
		
		Cookie cookie = getWeatherProviderCookie(request.getCookies());
		
		if (cookie != null) {
			currentCity = cookie.getValue().split(":")[0];
			currentService = cookie.getValue().split(":")[1];
		} else {
			needSetCookie = true;
		}
		
		String city = currentCity;
		if (request.getParameter("city") != null ) {
			city = request.getParameter("city");
			needSetCookie = true;
		}
		
		String weatherService = currentService;
		if (request.getParameter("wservice")!=null) {
			weatherService = request.getParameter("wservice");
			needSetCookie = true;
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		if (needSetCookie) {
			StringBuilder value = new StringBuilder()
					.append(city)
					.append(":")
					.append(weatherService);
			cookie = new Cookie(cookieName, value.toString());
			cookie.setMaxAge(60*60*24*365);
			response.addCookie(cookie);
		}
		
		setSelected((List<Selected>) cities, city);
		request.setAttribute("cities", cities);
		
		setSelected((List<Selected>) wServices, weatherService);
		request.setAttribute("wSvc", wServices);

		request.setAttribute("currCityName", currentCityName);
		request.setAttribute("currServiceName", currentServiceName);
		
		String keyCode = city + ":" + weatherService;
		
		WeatherData wData = getWeatherData(keyCode, weatherDB);
		request.setAttribute("wData", wData);
		
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/weatherProvider.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void setSelected(List<Selected> options, String param) {
		for (int i=0; i < options.size(); i++) {
			Selected c = options.get(i);
			if (options.get(i).getCode().equals(param)) {
				c.setSelected("selected");
				if (c instanceof City) {
					currentCityName = c.getName();
				}
				if (c instanceof WService) {
					currentServiceName = c.getName();
				}
				options.set(i, c);
			} else {
				c.setSelected("");
				options.set(i, c);
			}
		}
	}

	private WeatherData parseData(String url, String keyCode) {
		String temp = "";
		String humidity = "";
		String barp = "";
		try {
			Document doc = Jsoup.connect(url).get();
			if ("gisMeteo".equals(keyCode.split(":")[1])) {
				temp = doc.select("div#weather div.temp dd.value.m_temp.c").first().text();
				humidity = doc.select("div#weather div.wicon.hum").first().text();
				barp = doc.select("div#weather div.wicon.barp dd.value.m_press.torr").first().text();
			}
			if ("yaWeather".equals(keyCode.split(":")[1])) {
				temp = doc.select("div.current-weather__thermometer.current-weather__thermometer_type_now").first().text();
				humidity = doc.select("div.current-weather__info-row").get(2).ownText();
				barp = doc.select("div.current-weather__info-row").get(3).ownText();;
			} 
			WeatherData wd = new WeatherData(keyCode, temp, humidity, barp, new Date());
			return wd;
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
		}
		return null;
	}
	
	private WeatherData getWeatherData(String keyCode, List<WeatherData> wDataBase) {
		if (!wDataBase.isEmpty()) {
			for (WeatherData wd : wDataBase) {
				if (wd.getKeyCode().equals(keyCode)) {
					Date now = new Date();
					if (wd.getTimestamp().getTime() + outdated >= now.getTime() ) {
						System.out.println("From Local Object: " + wd.toString());
						return wd;
					}
				}
			}
		}
		WeatherData parsedNewData = parseData(getUrl(keyCode, urls), keyCode);
		insertOnDupUpdate(parsedNewData);
		System.out.println("From Service: " + parsedNewData.toString());
		return parsedNewData; 
	}
	
	private void insertOnDupUpdate(WeatherData parsedNewData) {
		if (!weatherDB.isEmpty()) {
			for (int i=0; i<weatherDB.size(); i++) {
				if (weatherDB.get(i).getKeyCode().equals(parsedNewData.getKeyCode())) {
					weatherDB.set(i, parsedNewData);
					return;
				}
			}
		} else {
			weatherDB.add(parsedNewData);
		}
	}

	private String getUrl(String key, List<Url> urlDB) {
		for (Url url  : urlDB) {
			if (url.getKeyCode().equals(key)) {
				return url.getUrl();
			}
		}
		return null;
	}
	
	private Cookie getWeatherProviderCookie(Cookie[] cookies) {
		Cookie cookie = null;
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (cookieName.equals(c.getName())) {
					cookie = c; 
					break;
				}
			}
		}
		return cookie;
	}

}

