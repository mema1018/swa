package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static de.shop.util.Constants.KEINE_ID;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.InvalidArtikelIdException;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.UriHelper;


@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
@Log
@RequestScoped
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String NOT_FOUND_ID = "artikel.notFound.id";
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	@Inject
	private ArtikelService as;
	
	@Context
	private HttpHeaders headers;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findArtikel(@PathParam("id") Long id) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			
			throw new NotFoundException(NOT_FOUND_ID, id);
		}

		return Response.ok(artikel)
				.links(getTransitionalLinks(artikel, uriInfo))
				.build();
	}
	
	private Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
                              		.rel(SELF_LINK)
                              		.build();
		final Link add = Link.fromUri(getUriArtikel(artikel, uriInfo))
                					.rel(ADD_LINK)
                					.build();
		final Link update = Link.fromUri(getUriArtikel(artikel, uriInfo))
                					.rel(UPDATE_LINK)
                					.build();
		final Link list = Link.fromUri(getUriArtikel(artikel, uriInfo))
									.rel("LIST_LINK")
									.build();
		final Link remove = Link.fromUri(getUriArtikel(artikel, uriInfo))
									.rel("REMOVE_LINK")
									.build();


		return new Link[] { self,add,update,list,remove };
	}
	
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikel", artikel.getId(), uriInfo);
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public Response createArtikel(@Valid Artikel artikel) {
		
		artikel.setId(KEINE_ID);
		LOGGER.tracef("Artikel: %s", artikel);
		artikel = as.createArtikel(artikel);
		LOGGER.tracef("Angelegt Artikel: ", artikel);		
		return Response.created(getUriArtikel(artikel,uriInfo))
				.build();

	}
	
	    @PUT
		@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
		@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
		@Transactional
	    public Response updateArtikel(@Valid Artikel artikel) {
	    	
	    	
	        // Vorhandenen Artikel ermitteln
	        final Artikel orginalArtikel = as.findArtikelById(artikel.getId());
	        if (orginalArtikel == null) {
	        	
	        	throw new NotFoundException(NOT_FOUND_ID,artikel.getId());

	        }
	        LOGGER.tracef("Artikel vorher: %s", orginalArtikel);
	    
	        // Daten des vorhandenen Artikels ueberschreiben
	        orginalArtikel.setValues(artikel);
	        LOGGER.tracef("Artikel nachher: %s", orginalArtikel);
	        
	        // Update durchfuehren
	        artikel = as.updateArtikel(orginalArtikel);
	        
	        artikel = as.findArtikelById(artikel.getId());
			return Response.ok(artikel).build();
				 
	   }
}
