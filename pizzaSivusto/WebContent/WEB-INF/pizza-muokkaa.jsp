<%@page import="bean.Kayttaja"%>
<%@page import="login.KayttajaLista"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Castello E Fiori Pizzojen muokkaus</title>
<jsp:include page="head-include.jsp"></jsp:include>
</head>
<body>
	<jsp:include page="header.jsp"></jsp:include>

	<div class="row">
		<div class="col s10 offset-s1">

			<c:if test="${not empty virhe }">
				<h1>Virhe</h1>
				<p class="flow-text center-align" style="color: red;">${virhe }<br>
					<br> <a class="btn waves-effect waves-light btn-large"
						href="${pathi }/hallinta">Takaisin hallintasivulle</a>
				</p>
			</c:if>

			<c:if test="${not empty success }">
				<h1>Onnistui</h1>
				<p class="flow-text center-align" style="color: green;">${success }<br>
					<br> <a class="btn waves-effect waves-light btn-large"
						href="${pathi }/hallinta">Takaisin hallintasivulle</a>
				</p>
			</c:if>
		</div>
	</div>

	<div class="row">
		<form action="hallinta" method="post">
			<div class="col s12">
				<h2>Muokkaa pizzaa</h2>
				<br>
				<div class="row">
					<div class="col s12 m12 l10 offset-l1">
						<div class="row">
							<div class="input-field col s4 m2 l2">
								<input type="text" name="pizzaid" id="pizzaid"
									value="${pizza.id }" disabled> <label for="pizzaid">Pizzan
									ID</label>
							</div>
							<div class="input-field col s8 m7 l7">
								<input type="hidden" name=pizzaid value="${pizza.id }">
								<input type="text" name="pizzanimi" id="pizzanimi"
									autocomplete="off" value="${pizza.nimi }"> <label
									for="pizzanimi">Pizzan nimi</label>
							</div>
							<div class="input-field col s12 m3 l3">
								<input type="number" step="0.05" name="pizzahinta"
									class="validate" id="pizzahinta" min="0" autocomplete="off"
									value="${pizza.hinta }"> <label for="pizzahinta"
									data-error="Virhe">Pizzan hinta</label>
							</div>
						</div>
						<!-- Täytevalikko -->
						<div class="row hide-on-small-only" id="pizza-taytteet">
							<label id="taytteet-label">Täytteet</label>
							<table class="taytetaulu" id="pizzataulu">
								<tr>
									<c:forEach items="${taytteet}" var="tayte"
										varStatus="loopCount">
										<c:forEach items="${pizza.tayteIdt }" var="pizzantaytteet">
											<c:if test="${pizzantaytteet == tayte.id }">
												<c:set var="ontayte" value="1"></c:set>
											</c:if>
										</c:forEach>
										<c:if
											test="${loopCount.index % 4 == 0 && loopCount.index != 0}">
								</tr>
								<tr>
									</c:if>
									<td><input type="checkbox" id="${tayte.id }"
										name="pizzatayte" value="${tayte.id }"
										<c:if test="${ontayte == 1 }"> checked</c:if>><label
										for="${tayte.id }">${tayte.nimi }</label></td>
									<c:set var="ontayte" value="0"></c:set>
									<c:if
										test="${fn:length(taytteet) == loopCount.count && fn:length(taytteet) % 4 != 0}">
										<td colspan="${4 - fn:length(taytteet) % 4 }"></td>
									</c:if>
									</c:forEach>
								</tr>
							</table>
							<script src="js/tayte-input-limit.js"></script>
						</div>
						<!-- Mobiilille täytevalikko -->
						<div class="row">
							<div class="input-field col s12 hide-on-med-and-up">
								<select multiple>
									<c:forEach items="${taytteet}" var="tayte"
										varStatus="loopCount">
										<c:forEach items="${pizza.tayteIdt }" var="pizzantaytteet">
											<c:if test="${pizzantaytteet == tayte.id }">
												<c:set var="ontayte" value="1"></c:set>
											</c:if>
										</c:forEach>
										<option value="${tayte.id }"
											<c:if test="${ontayte == 1 }"> selected</c:if>>${tayte.nimi }</option>
										<c:set var="ontayte" value="0"></c:set>
									</c:forEach>
								</select> <label>Täytteet</label>
							</div>
						</div>
						<div class="row">
							<a class="btn waves-effect waves-light btn-large red lighten-2"
								href="${pathi }/hallinta">Peruuta</a>
							<button class="btn waves-effect waves-light btn-large"
								type="submit" name="action" value="paivitapizza">Päivitä
								tiedot</button>

						</div>
					</div>
				</div>
			</div>
		</form>
	</div>

	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>