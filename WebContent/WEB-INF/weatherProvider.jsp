<%@page import="ru.alexig.WeatherData"%>
<%@page import="ru.alexig.WService"%>
<%@page import="ru.alexig.City"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Задание "Погодный сервис"</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wp.css">
</head>
<body>
<div class="form" >
	<form action="provider" method="post">
	<div class="box">
	Город:<select name="city">
<% 
	List<City> cities = (List<City>) request.getAttribute("cities");
	for (City c: cities) { 
%>
		<option <%=c.getSelected() %> value="<%=c.getCode() %>"><%=c.getName() %></option>
<% 
	} 
%>
			</select>
	</div>
	<div class="box">
	Сервис:<select name="wservice">
<%
	List<WService> wsvc = (List<WService>) request.getAttribute("wSvc");
	for (WService s: wsvc) {
%>
			<option <%=s.getSelected() %> value="<%=s.getCode() %>"><%=s.getName() %></option>
<%
	}
%>
			</select>
	</div>
			<div class="box"><input type="submit" value="Мне повезёт"></div>
	</form>
</div>
<div class="content">
	<div class="header">
		<h1>Погода от <%=(String) request.getAttribute("currServiceName") %></h1>
	</div>
	<div class="section higher">
		<h2 class="typeT"><%=(String) request.getAttribute("currCityName") %></h2>
<% WeatherData wData = (WeatherData) request.getAttribute("wData"); %>
		<div class="temp">
			<div class="label">Температура воздуха: </div>  
			<div class="value"><%=wData.getTemp() %></div>
		</div>
		<div class="barp">
			<div class="label">Атмосферное давление: </div>
			<div class="value"><%=wData.getBar() %></div>
		</div>
		<div class="humidity">
			<div class="label">Относительная влажность: </div>
			<div class="value"><%=wData.getHumidity() %></div>
		</div>
	</div>
</div>
</body>
</html>