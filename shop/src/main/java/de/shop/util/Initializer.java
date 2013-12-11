package de.shop.util;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

import org.jboss.logging.Logger;

public class Initializer {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	// @Transactional
	public void onStartup(
			@Observes @Initialized(ApplicationScoped.class) ServletContext ctx) {
		LOGGER.infof(
				"Der Web-Container %s unterstuetzt die Servlet-Spezifikation %s.%s",
				ctx.getServerInfo(), ctx.getMajorVersion(),
				ctx.getMinorVersion());

		LOGGER.infof("Default Charset: %s", Charset.defaultCharset()
				.displayName());

		// Eigene Initialisierungen, z.B initiale Daten fuer die DB
	}
}
