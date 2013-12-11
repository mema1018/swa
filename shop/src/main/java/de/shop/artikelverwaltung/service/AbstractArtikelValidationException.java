package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.ws.rs.ext.Provider;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;

@Provider
@ApplicationScoped
@Log
public abstract class AbstractArtikelValidationException extends
		AbstractArtikelServiceException {
	private static final long serialVersionUID = -6924234959157503601L;
	private final Collection<ConstraintViolation<Artikel>> violations;

	public AbstractArtikelValidationException(
			Collection<ConstraintViolation<Artikel>> violations) {
		super("Violations: " + violations);
		this.violations = violations;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
