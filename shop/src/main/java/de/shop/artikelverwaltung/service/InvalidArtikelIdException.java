package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

public class InvalidArtikelIdException extends
		AbstractArtikelValidationException {
	private static final long serialVersionUID = 8553323515940121536L;
	private final Long artikelId;
	private static final String MESSAGE_KEY = "artikel.IdInvalid";

	public InvalidArtikelIdException(Long artikelId,
			Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	@Override
	public String getMessageKey() {

		return MESSAGE_KEY;
	}
}
