package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.ArtikelResource;
import de.shop.util.Mock;

@Path("/artikelKatalog")
@Produces(APPLICATION_JSON)
@Consumes
// Spezifizert eine Liste von Mediatypen, die komsumiert werden können.
@RequestScoped
// Bei jedem Request 1 neues Objekt
public class ArtikelKatalogResource {

	@Context
	private UriInfo uriInfo; 
	
	@Inject
	private UriHelperArtikelKatalog uriHelperArtikelKatalog;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private ArtikelResource artikelResource;
	
	@GET
	@Path("artikelId/{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id) { 
		
	final Artikel artikel = artikelResource.findArtikelById(id);
	
	return artikel;
	}
	
	@GET
	public Collection<Artikel> findArtikelByBezeichnung(
	@QueryParam("bezeichnung") @DefaultValue("") String bezeichnung){
		
		Collection<Artikel> artikelPlural = artikelResource.findArtikelByBezeichnung(bezeichnung);
	
		return artikelPlural;
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Path("artikelId/")
	@Produces
	public Response updateArtikel(Artikel artikel) {

		Mock.updateArtikel(artikel);
		return Response.noContent().build();
	}
	@DELETE
	@Path("artikelId/")
	@Produces
	public Response deleteArtikel(@PathParam("id") Long artikelId) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteArtikel(artikelId);
		return Response.noContent().build();
	}
	
}
	
	
	

