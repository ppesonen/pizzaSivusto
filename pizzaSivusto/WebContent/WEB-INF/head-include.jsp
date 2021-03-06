<%@page import="fi.softala.pizzeria.bean.Kayttaja"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link href="http://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<c:set var="url">${pageContext.request.contextPath}</c:set>
<link type="text/css" rel="stylesheet"
	href="${url }/css/materialize.css" />
<link type="text/css" rel="stylesheet" href="${url }/css/style.css" />
<link href='https://fonts.googleapis.com/css?family=Pacifico'
	rel='stylesheet' type='text/css'>
<link href="${url }/img/favicon.ico" rel="shortcut icon" type="image/x-icon">
<link href="${url }/img/favicon.ico" rel="icon" type="image/x-icon">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<script type="text/javascript"
	src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="${url }/js/materialize.js"></script>
<script type="text/javascript" src="${url }/js/ostoskori.js"></script>
<script type="text/javascript">
	var naytaVirhe = function(viesti) {
		$(document).ready(function() {
			viesti = $("<div />").text(viesti).html()
			Materialize.toast(viesti, 3000, 'red lighten-2');
			Materialize.toast();
		});
	}
	
	var naytaSuccess = function(viesti) {
		$(document).ready(function() {
			viesti = $("<div />").text(viesti).html()
			Materialize.toast(viesti, 3000, 'green darken-1');
			Materialize.toast();
		});
	}
	
	var loginModal = function() {
		$("#loginmodal").openModal({ready: function() { $('#kayttajanimi-modal').focus().select() }});
	}
	
	$(document).ready(function() {
		$(".button-collapse").sideNav();
		$('.modal-trigger').leanModal();
		$('select').material_select();
	});
	$(window).scroll(function() {
		if ($(document).scrollTop() > 30) {
			$('nav').addClass('nav-scrolled');

		} else {
			$('nav').removeClass('nav-scrolled');
		}
	});
</script>
<c:if test="${not empty success }">
	<script type="text/javascript">
			naytaSuccess("${success }");
	</script>
</c:if>
<c:if test="${not empty virhe }">
	<script type="text/javascript">
			naytaVirhe("${virhe }");
	</script>
</c:if>
<c:if test="${not empty param.loggedin && param.loggedin == \"true\" }">
<script type="text/javascript">naytaSuccess('Olet kirjautunut sisään onnistuneesti!');</script>
</c:if>
<c:if test="${not empty param.error && param.error == \"ostoskorityhja\" }">
<script type="text/javascript">naytaVirhe('Et voi siirtyä tilaukseen, ostoskorisi on tyhjä!');</script>
</c:if>
<c:if test="${not empty param.error && param.error == \"nologin\" }">
<script type="text/javascript">naytaVirhe('Kirjaudu sisään siirtyäksesi tilaukseen!');</script>
</c:if>