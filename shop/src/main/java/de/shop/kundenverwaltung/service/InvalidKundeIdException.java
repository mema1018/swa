package de.shop.kundenverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.kundenverwaltung.domain.AbstractKunde;

@ApplicationException(rollback = true)
public class InvalidKundeIdException extends AbstractKundeValidationException {
	private static final long serialVersionUID = -8973151010781329074L;
	private static final String MESSAGE_KEY = "kunde.IdInvalid";

	private final Long kundeId;

	public InvalidKundeIdException(Long kundeId,
			Collection<ConstraintViolation<AbstractKunde>> violations) {
		super(violations);
		this.kundeId = kundeId;
	}

	public Long getKundeId() {
		return kundeId;
	}

	@Override
	public String getMessageKey() {

		return MESSAGE_KEY;
	}
}
