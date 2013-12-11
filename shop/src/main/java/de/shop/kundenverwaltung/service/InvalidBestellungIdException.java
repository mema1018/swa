package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.AbstractKunde;

@ApplicationException(rollback = true)
public class InvalidBestellungIdException extends
		AbstractKundeValidationException {
	private static final long serialVersionUID = -8973151010781329074L;
	private static final String MESSAGE_KEY = "kunde.notFound.bestellung.id";

	private final Long bestellungId;

	public InvalidBestellungIdException(Long bestellungId,
			Collection<ConstraintViolation<AbstractKunde>> violations) {
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
