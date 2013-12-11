package de.shop.artikelverwaltung.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.Provider;

import de.shop.util.AbstractShopException;
import de.shop.util.Log;

@Provider
@ApplicationScoped
@Log
public abstract class AbstractArtikelServiceException extends
		AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractArtikelServiceException(String msg) {
		super(msg);
	}

	public AbstractArtikelServiceException(String msg, Throwable t) {
		super(msg, t);
	}

}
