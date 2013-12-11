package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellung;

public class InvalidBestellungIdException extends
		AbstractBestellungValidationException {
	private static final long serialVersionUID = 8553323515940121536L;
	private final Long bestellungId;
	private static final String MESSAGE_KEY = "bestellung.notFound.id";

	public InvalidBestellungIdException(Long bestellungId,
			Collection<ConstraintViolation<Bestellung>> violations) {
		super(violations);
		this.bestellungId = bestellungId;
	}

	public Long getBestellungId() {
		return bestellungId;
	}

	@Override
	public String getMessageKey() {

		return MESSAGE_KEY;
	}
}
