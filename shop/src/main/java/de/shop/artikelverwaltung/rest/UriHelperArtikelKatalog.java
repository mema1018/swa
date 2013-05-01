package de.shop.artikelverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.ArtikelKatalog;


@ApplicationScoped
public class UriHelperArtikelKatalog {

	public URI getUriArtikelKatalog(ArtikelKatalog artikelKatalog,
			UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
				.path(ArtikelKatalog.class)
				.path(ArtikelKatalog.class, "findArtikelById");
		final URI uri = ub.build(artikelKatalog.getArtikel());
		return uri;
	}
	
}
