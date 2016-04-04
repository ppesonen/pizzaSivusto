// Palauttaa yksittäisen täytetaulukon rivin
var Tayte = React.createClass({
	render: function() {
		var saatavilla = "";
		if (this.props.saatavilla == true) {
			saatavilla = "Saatavilla";
			return (
				<tr>
				<td>{this.props.nimi }</td>
				<td>{saatavilla }</td>
				<td><a className="waves-effect waves-light btn tooltipped right" href={"?tayte-edit=" + this.props.id }><i className="material-icons">edit</i></a></td>
				</tr>
			);
		}
		else {
			saatavilla = "Ei saatavilla";
			return (
				<tr className="red lighten-5">
				<td>{this.props.nimi }</td>
				<td>{saatavilla }</td>
				<td><a className="waves-effect waves-light btn tooltipped right" href={"?tayte-edit=" + this.props.id }><i className="material-icons">edit</i></a></td>
				</tr>
			);
		}
	}
});

// Palauttaa kaikkien täytteiden taulukon
var Taytelista = React.createClass({
	render: function() {
		return (
			<div className="col s12 m12 l6 pull-l5">
			<h2>Täytteet</h2>
			<table id="taytelista" className="bordered">
			<thead>
			<tr>
			<th>Täyte</th>
			<th>Saatavuus</th>
			<th></th>
			</tr>
			</thead>
			<tbody>
			{this.props.taytteet.map((o, i) => <Tayte key={o.id} id={o.id} nimi={o.nimi} saatavilla={o.saatavilla} />)}
			</tbody>
			</table>
			</div>
		);
	}
});

// Täytteen lisäys formi
var TaytteenLisays = React.createClass({
	getInitialState: function() {
		return ({taytenimi: ""});
	},
	componentDidMount: function() {
		$("#submittayte").attr("disabled", true);
	},
	paivitanimi: function(e) {
		this.setState({taytenimi: e.target.value }, function() { this.paivitanappi() });
	},
	paivitanappi: function() {
		if (this.state.taytenimi.length > 2) {
			$("#submittayte").attr("disabled", false);
		}
		else {
			$("#submittayte").attr("disabled", true);
		}
	},
	render: function() {
		return (
			<div className="col s12 m12 l5 push-l7 " id="taytel">
			<h2>Lisää täyte</h2>
			<div className="row">
			<form id="tayteformi">
			<div className="row">
			<div className="col s12 input-field">
			<input type="text" name="taytenimi" id="taytenimi"
			className="fieldi" value={this.state.taytenimi } onChange={this.paivitanimi }/> <label
			htmlFor="taytenimi">Täytteen nimi</label>
			</div>
			</div>
			<div className="row">
			<div className="col s6">
			<input name="taytesaatavilla" type="radio" id="saatavilla"
			value="1" defaultChecked />
			<label htmlFor="saatavilla">Saatavilla</label>
			</div>
			<div className="col s6">
			<input name="taytesaatavilla" type="radio" id="eisaatavilla"
			value="0" />
			<label htmlFor="eisaatavilla">Ei
			saatavilla</label>
			</div>
			</div>
			<div className="row">
			<div className="col s12">
			<button className="btn waves-effect waves-light btn-large"
			type="button" id="submittayte" onClick={this.props.lahetaLisays }>Lisää
			täyte</button>
			</div>
			</div>
			</form>
			</div>
			</div>
		);
	}
});

// Palauttaa yksittäisen pizzataulukon rivin
var Pizza = React.createClass({
	pizzanPoisto: function() {
		var id = this.props.id;
		this.props.palautaPizza({"pizza-poista": id });
	},
	pizzanPalautus: function() {
		var id = this.props.id;
		this.props.palautaPizza({"pizza-palauta": id });
	},
	render: function() {
		var taytteet = "";
		for (var int = 0; int < this.props.taytteet.length; int++) {
			taytteet += this.props.taytteet[int].nimi;
			if ((int+1) != this.props.taytteet.length) {
				taytteet+= ", ";
			}
		}
		if (this.props.poistomerkinta == null) {
			return (
				<tr>
				<td>{this.props.nimi }</td>
				<td className="pienifontti hide-on-small-only">{taytteet }</td>
				<td className="hide-on-small-only">{parseFloat(this.props.hinta).toFixed(2).replace(".",",") } €</td>
				<td className="right-align">
				<a className="waves-effect waves-light btn tooltipped" href={"?pizza-edit=" + this.props.id } data-position="left" data-delay="500" data-tooltip="Muokkaa"><i className="material-icons">edit</i></a> <button className="waves-effect waves-light btn red lighten-2 tooltipped" type="button" onClick={this.pizzanPoisto } data-position="right" data-delay="500" data-tooltip="Poista"> <i className="material-icons large">delete</i></button></td>
				</tr>
			);
		}
		else {
			return (
				<tr className="red lighten-5">
				<td>{this.props.nimi }<br/><span className="pienifontti hide-on-small-only">Poistettu {this.props.poistomerkinta }</span></td>
				<td className="pienifontti hide-on-small-only">{taytteet }</td>
				<td className="hide-on-small-only">{parseFloat(this.props.hinta).toFixed(2) } €</td>
				<td className="right-align">
				<a className="waves-effect waves-light btn tooltipped" href={"?pizza-edit=" + this.props.id } data-position="left" data-delay="500" data-tooltip="Muokkaa"><i className="material-icons">edit</i></a> <button className="waves-effect waves-light btn red lighten-2 tooltipped" type="button" onClick={this.pizzanPalautus } data-position="right" data-delay="500" data-tooltip="Palauta"> <i className="material-icons large">visibility_off</i></button></td>
				</tr>
			);
		}
	}
});

// Palauttaa taulukon kaikista pizzoista
var Pizzalista = React.createClass({
	avaaModal: function() {
		$("#poistomodal").openModal();
	},
	poistaValitut: function() {
		this.props.poistaValitut({ "poista-pizzat": "true" });
		$("#poistomodal").closeModal();
	},
	render: function() {
		return (
			<div className="col s12">
			<table className="bordered">
			<thead>
			<tr>
			<th>Nimi</th>
			<th className="hide-on-small-only">Täytteet</th>
			<th className="hide-on-small-only">Hinta</th>
			<th></th>
			</tr>
			</thead>
			<tbody>
			{this.props.pizzat.map((o, i) => <Pizza key={o.id} id={o.id} nimi={o.nimi} hinta={o.hinta } taytteet={o.taytteet } poistomerkinta={o.poistomerkinta} palautaPizza={this.props.palautaPizza } poistaPizza={this.props.poistaPizza }/>)}
			</tbody>
			</table>
			<br />
			<div className="col s12 m6 l6 push-m6 push-l6 small-centteri right-align">
			<button className="waves-effect waves-light btn modal-trigger red lighten-2 tooltipped" type="button" onClick={this.avaaModal } data-position="bottom" data-delay="500" data-tooltip="Poista pizzat pysyvästi"><i className="material-icons left">delete</i> Poista merkityt</button>
			<div id="poistomodal" className="modal">
			<div className="modal-content center-align">
			<h4>Oletko varma?</h4>
			<p>Poistettavaksi merkityt pizzat ({this.props.poistettavia }kpl) poistetaan tietokannasta pysyvästi.</p>
			<a href="#!" className="modal-action modal-close waves-effect waves-light btn red lighten-2">Peruuta</a> <button onClick={this.poistaValitut } className="modal-action waves-effect waves-light btn"><i className="material-icons left">delete</i> Poista</button>
			</div>
			</div>
			</div>
			</div>
		);
	}
});

// Palauttaa yksittäisen täytteen checkbox rivin pizzan lisäystä varten
var TayteCheckbox = React.createClass({
	render: function() {
		if (this.props.saatavilla == true) {
		return (
			<div className="col s6 m4 l3 taytediv">
			<input type="checkbox" id={this.props.id } value={this.props.id} name="pizzatayte" onChange={this.props.laskeValitut }/>
			<label htmlFor={this.props.id }>{this.props.nimi }</label>
			</div>
		);
		}
		else {
			return (
				<div className="col s6 m4 l3 taytediv">
				<input type="checkbox" id={this.props.id } value={this.props.id} name="pizzatayte" onChange={this.props.laskeValitut }/>
				<label className="errori-light" htmlFor={this.props.id }>{this.props.nimi }</label>
				</div>
			);
		}
	}
});

// Pizzan lisäysformi
var PizzanLisays = React.createClass({
	getInitialState: function() {
		return ({valittuja: 0, pizzanimi: "", pizzahinta: "", pizzakuvaus: ""});
	},
	componentDidMount: function() {
		$('textarea#pizzakuvaus').characterCounter();
		$("#submitpizza").attr("disabled", true);
	},
	laskeValitut: function() {
		var valittuja = $("#pizza-taytteet input[name='pizzatayte']:checked").length;
		this.setState({valittuja: valittuja }, function(){ this.paivitanappi() });
		if (valittuja > 4) {
			$("#pizza-taytteet input:checkbox:not(:checked)")
			.each(function() {
				$(this).attr("disabled", true);
			});
		} else {
			$("#pizza-taytteet input:checkbox").each(
				function() {
					$(this).attr("disabled", false);
				});
			}
		},
		paivitanimi: function(e) {
			this.setState({ pizzanimi: e.target.value }, function(){ this.paivitanappi() });
		},
		paivitahinta: function(e) {
			this.setState({ pizzahinta: e.target.value }, function(){ this.paivitanappi() });
		},
		paivitakuvaus: function(e) {
			this.setState({ pizzakuvaus: e.target.value }, function(){ this.paivitanappi() });
		},
		paivitanappi: function() {
			if (this.state.pizzanimi.length > 2 && this.state.pizzahinta > 0 && this.state.pizzakuvaus.length > 0 && this.state.valittuja > 0) {
				$("#submitpizza").attr("disabled", false);
			}
			else {
				$("#submitpizza").attr("disabled", true);
			}
		},
		render: function() {
			return (
				<div className="col s12">
				<div className="row">
				<h2>Lisää pizza</h2>
				<form id="lisaysformi">
				<div className="col s12 m12 l10 offset-l1">
				<div className="row">
				<div className="input-field col s12 m9 l9">
				<input type="text" name="pizzanimi" id="pizzanimi" value={this.state.pizzanimi } onChange={this.paivitanimi }/>
				<label htmlFor="pizzanimi">Pizzan nimi</label>
				</div>
				<div className="input-field col s12 m3 l3">
				<input type="number" className="validate" min="0" step="0.05" name="pizzahinta" id="pizzahinta" value={this.state.pizzahinta } onChange={this.paivitahinta }/>
				<label htmlFor="pizzahinta" data-error="Virhe">Pizzan hinta</label>
				</div>
				<div className="input-field col s12">
				<textarea className="materialize-textarea" name="pizzakuvaus" id="pizzakuvaus" length="255" value={this.state.pizzakuvaus } onChange={this.paivitakuvaus }></textarea>
				<label htmlFor="pizzakuvaus">Pizzan kuvaus</label>
				</div>
				</div>
				<div className="row" id="pizza-taytteet">
				<label id="taytteet-label">Täytteet {this.state.valittuja } / 5</label><br/><br/>
				<div className="row">
				{this.props.taytteet.map((o, i) => <TayteCheckbox key={o.id} id={o.id} nimi={o.nimi} saatavilla={o.saatavilla } laskeValitut={this.laskeValitut } />)}
				</div>
				</div>
				<button className="btn waves-effect waves-light btn-large" id="submitpizza" type="button" onClick={this.props.lisaaPizza }>Lisää pizza</button>
				</div>
				</form>
				</div>
				</div>
			);
		}
	});

	// Hallintasivun navigaatio
	var Navigaatio = React.createClass({
		render: function() {
			return (
				<div>
				<div className="row hide-on-small-only">
				<div className="col s12 m12 l10 offset-l1">
				<ul className="tabs">
				<li className="tab col s12"><a href="#pizza-h" className="active">Pizzojen
				hallinta</a></li>
				<li className="tab col s12"><a href="#pizza-l">Pizzan lisäys</a></li>
				<li className="tab col s12"><a href="#tayte-h">Täytteiden
				hallinta</a></li>
				</ul>
				</div>
				</div>
				<div className="row hide-on-med-and-up">
				<div className="col s12">
				<ul className="tabs">
				<li className="tab col s12"><a href="#pizza-h" className="active"><img
				src="img/pizza_gear.png" alt="P" /> </a></li>
				<li className="tab col s12"><a href="#pizza-l"><img
				src="img/pizza_add.png" alt="L" /></a></li>
				<li className="tab col s12"><a href="#tayte-h"><img
				src="img/pizza_zoom.png" alt="T" /> </a></li>
				</ul>
				</div>
				</div>
				</div>
			);
		}
	});

	// Hallintasivun renderointi ja funktiot
	var Hallintasivu = React.createClass({
		getInitialState: function() {
			return { pizzat: [], taytteet: [], poistettavat: 0 };
		},
		componentDidMount: function() {
			this.haePizzat();
			this.haeTaytteet();
			$('ul.tabs').tabs();
		},
		haePizzat: function() {
			return $.post("hallinta", {action: "haepizzat"}).done(
				function(json) {
					console.log("Haettiin pizzat, pituus " + json.length);
					var poistettavat = 0;
					for (var i = 0; i < json.length; i++) {
						if (json[i].poistomerkinta != null) {
							poistettavat++;
						}
					}
					this.setState({ pizzat: json, poistettavat: poistettavat })
				}.bind(this)).fail(
					function(jqxhr, textStatus, error) {
						var errori = textStatus + ", " + error;
						console.log("Error pizzoja hakiessa: " + errori);
					});
				},
				kasittelePizza: function(toiminto) {
					console.log("Käsitellään: " + JSON.stringify(toiminto));
					$.get("hallinta", toiminto).done(
						function(json) {
							var vastaus = json[0];
							if (vastaus.virhe != null) {
								console.log(vastaus.virhe);
								naytaVirhe(vastaus.virhe);
							}
							else if (vastaus.success != null) {
								console.log(vastaus.success);
								naytaSuccess(vastaus.success);
								this.haePizzat();
							}
							else {
								console.log(JSON.stringify(json));
								naytaVirhe("Virhe JSON vastauksessa!")
							}
						}.bind(this)).fail(
							function(jqxhr, textStatus, error) {
								var errori = textStatus + ", " + error;
								console.log("Faili: " + errori);
								console.log(JSON.stringify(json));
								naytaVirhe("Virhe javascriptissa!")
							});
						},
						haeTaytteet: function() {
							return $.post("hallinta", {action: "haetaytteet"}).done(
								function(json) {
									console.log("Haettiin taytteet, pituus " + json.length);
									this.setState({ taytteet: json })
								}.bind(this)).fail(
									function(jqxhr, textStatus, error) {
										var errori = textStatus + ", " + error;
										console.log("Error taytteita hakiessa: " + errori);
									});
								},
								lisaaTayte: function() {
									var submitdata = $("#tayteformi" ).serializeArray();
									submitdata.push({name: "action", value: "lisaatayte"});
									$.post("hallinta", submitdata).done(
										function(json) {
											var vastaus = json[0];
											if (vastaus.virhe != null) {
												naytaVirhe(vastaus.virhe);
											}
											else if (vastaus.success != null) {
												naytaSuccess(vastaus.success);
												$("#taytenimi").val("");
												this.haeTaytteet();
											}
											else {
												console.log(JSON.stringify(json));
												naytaVirhe("Virhe JSON vastauksessa!")
											}
										}.bind(this)).fail(
											function(jqxhr, textStatus, error) {
												var errori = textStatus + ", " + error;
												console.log("Faili: " + errori);
												console.log(JSON.stringify(json));
												naytaVirhe("Virhe javascriptissa!")
											});
										},
										lisaaPizza: function() {
											var submitdata = $("#lisaysformi" ).serializeArray();
											submitdata.push({name: "action", value: "lisaapizza"});
											$.post("hallinta", submitdata).done(
												function(json) {
													var vastaus = json[0];
													if (vastaus.virhe != null) {
														naytaVirhe(vastaus.virhe);
													}
													else if (vastaus.success != null) {
														naytaSuccess(vastaus.success);
														$("#pizzanimi, #pizzahinta, #pizzakuvaus").val("");
														$("#pizza-taytteet input:checkbox").each(
															function() {
																$(this).attr("checked", false);
															});
															this.haePizzat();
														}
														else {
															console.log(JSON.stringify(json));
															naytaVirhe("Virhe JSON vastauksessa!")
														}
													}.bind(this)).fail(
														function(jqxhr, textStatus, error) {
															var errori = textStatus + ", " + error;
															console.log("Faili: " + errori);
															console.log(JSON.stringify(json));
															naytaVirhe("Virhe javascriptissa!")
														});
													},
													render: function() {
														return(
															<div>
															<div className="row headertext">
															<h1>Hallinta</h1>
															<p className="flow-text">Tietokannassa on yhteensä {this.state.pizzat.length } pizzaa ja {this.state.taytteet.length } täytettä!
															<br />Poistomerkittyja pizzoja {this.state.poistettavat } kpl</p>
															</div>
															<div id="main-content">
															<Navigaatio />
															<div className="row" id="pizza-h">
															<Pizzalista pizzat={this.state.pizzat } palautaPizza={this.kasittelePizza } poistaPizza={this.kasittelePizza } poistaValitut={this.kasittelePizza } poistettavia={this.state.poistettavat }/>
															</div>
															<div className="row" id="pizza-l">
															<PizzanLisays taytteet={this.state.taytteet } lisaaPizza={this.lisaaPizza }/>
															</div>
															<div className="row" id="tayte-h">
															<TaytteenLisays lahetaLisays={this.lisaaTayte } />
															<Taytelista taytteet={this.state.taytteet } />
															</div>
															</div>
															</div>
														);
													}
												});

												ReactDOM.render(
													<Hallintasivu />,
													document.getElementById("hallintasisalto")
												)
