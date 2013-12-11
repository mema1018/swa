package de.shop.util;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Log
public class OptimisticLockExceptionMapper implements
		ExceptionMapper<OptimisticLockException> {
	private static final String MESSAGE_KEY = "persistence.concurrentUpdate";

	@Context
	private HttpHeaders headers;

	@Inject
	private Messages messages;

	@Inject
	private EntityManager em;

	@Override
	public Response toResponse(OptimisticLockException e) {
		final Object id = em.getEntityManagerFactory().getPersistenceUnitUtil()
				.getIdentifier(e.getEntity());
		final String msg = messages.getMessage(headers, MESSAGE_KEY, id);
		final Response response = Response.status(CONFLICT).type(TEXT_PLAIN)
				.entity(msg).build();
		return response;
	}
}
