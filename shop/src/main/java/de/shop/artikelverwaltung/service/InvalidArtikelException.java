package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

public class InvalidArtikelException extends AbstractArtikelValidationException {

	private static final long serialVersionUID = 2428491133712963949L;
	private final Artikel artikel;
	private static final String MESSAGE_KEY = "artikel.InvalidArtikel";

	public InvalidArtikelException(Artikel artikel,
			Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.artikel = artikel;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	@Override
	public String getMessageKey() {

		return MESSAGE_KEY;
	}
}
