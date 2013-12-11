package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellposition;

public class InvalidAnzahlException extends
		AbstractBestellungPosValidationException {
	private static final long serialVersionUID = 8553323515940121536L;
	private final short anzahl;
	private static final String MESSAGE_KEY = "bestellung.anzahl.Min";

	public InvalidAnzahlException(short anzahl,
			Collection<ConstraintViolation<Bestellposition>> violations) {
		super(violations);
		this.anzahl = anzahl;
	}

	public short getAnzahl() {
		return anzahl;
	}

	@Override
	public String getMessageKey() {

		return MESSAGE_KEY;
	}
	
}
