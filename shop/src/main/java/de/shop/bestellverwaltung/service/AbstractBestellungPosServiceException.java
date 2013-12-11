package de.shop.bestellverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractBestellungPosServiceException extends
		AbstractShopException {
	private static final long serialVersionUID = -626920099480136224L;

	public AbstractBestellungPosServiceException(String msg) {
		super(msg);
	}

	public AbstractBestellungPosServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
