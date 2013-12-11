package de.shop.auth.service;

//import static de.shop.kundenverwaltung.domain.AbstractKunde.FIND_USERNAME_BY_USERNAME_PREFIX;
//import static de.shop.kundenverwaltung.domain.AbstractKunde.PARAM_USERNAME_PREFIX;
import static de.shop.util.Constants.HASH_ALGORITHM;
import static de.shop.util.Constants.HASH_CHARSET;
import static de.shop.util.Constants.HASH_ENCODING;
import static de.shop.util.Constants.SECURITY_DOMAIN;
import static org.jboss.security.auth.spi.Util.createPasswordHash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;

import com.google.common.base.Strings;

import de.shop.auth.domain.RolleType;
import de.shop.kundenverwaltung.domain.AbstractKunde;

import java.io.InputStreamReader;

import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;

@ApplicationScoped
@Log
public class AuthService implements Serializable {

	private static final long serialVersionUID = 5719521298774482673L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());
	private static final String LOCALHOST = "localhost";
	private static final int MANAGEMENT_PORT = 9990; // JBossAS hatte den
														// Management-Port 9999

	@Inject
	private KundeService ks;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDesytroy() {
		LOGGER.debugf("CDI faehiges Bean %s wurde gelöscht", this);
	}

	public String verschluesseln(String password) {
		if (password == null)
			return null;

		return createPasswordHash(HASH_ALGORITHM, HASH_ENCODING, HASH_CHARSET,
				null, password);
	}

	public void addRollen(Long kundeId, Collection<RolleType> rollen) {
		if (rollen == null || rollen.isEmpty()) {
			return;
		}

		ks.findKundeById(kundeId, FetchType.NUR_KUNDE).addRollen(rollen);
		flushSecurityCache(kundeId.toString());
	}

	public boolean validatePassword(AbstractKunde kunde, String password) {
		if (kunde == null)
			return false;

		final String verschluesselt = verschluesseln(password);
		return verschluesselt.equals(kunde.getPassword());

	}

	public void removeRollen(Long kundeId, Collection<RolleType> rollen) {
		if (rollen == null || rollen.isEmpty()) {
			return;
		}

//		 ks.findKundeById(kundeId, FetchType.NUR_KUNDE)
//		 .removeRollen(rollen);
		flushSecurityCache(kundeId.toString());
	}

	private static void flushSecurityCache(String username) {
		// ModelControllerClient ist abgeleitet vom Interface Autoclosable
		try (ModelControllerClient client = ModelControllerClient.Factory
				.create(LOCALHOST, MANAGEMENT_PORT)) {
			final ModelNode address = new ModelNode();
			address.add("subsystem", "security");
			address.add("security-domain", SECURITY_DOMAIN);

			final ModelNode operation = new ModelNode();
			operation.get("address").set(address);
			operation.get("operation").set("flush-cache");
			operation.get("principal").set(username);

			final ModelNode result = client.execute(operation);
			final String resultString = result.get("outcome").asString();
			if (!"success".equals(resultString)) {
				throw new IllegalStateException(
						"FEHLER bei der Operation \"flush-cache\" fuer den Security-Cache: "
								+ resultString);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<RolleType> getEigeneRollen() {
		final List<RolleType> rollen = new LinkedList<>();

		// Authentifiziertes Subject ermitteln
		Subject subject = null;
		try {
			subject = Subject.class.cast(PolicyContext
					.getContext("javax.security.auth.Subject.container"));
		} catch (PolicyContextException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		if (subject == null) {
			return null;
		}

		// Gruppe "Roles" ermitteln
		final Set<Principal> principals = subject
				.getPrincipals(Principal.class);
		for (Principal p : principals) {
			if (!(p instanceof SimpleGroup)) {
				continue;
			}

			final SimpleGroup sg = SimpleGroup.class.cast(p);
			if (!"Roles".equals(sg.getName())) {
				continue;
			}

			// Rollen ermitteln
			final Enumeration<Principal> members = sg.members();
			while (members.hasMoreElements()) {
				final String rolle = members.nextElement().toString();
				if (rolle != null) {
					rollen.add(RolleType.valueOf(rolle.toUpperCase(Locale
							.getDefault())));
				}
			}
		}
		return rollen;
	}

	public static void main(String[] args) throws IOException {
		for (;;) {
			System.out.print("Password (Abbruch durch <Return>): ");
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(System.in, Charset.defaultCharset()));
			final String password = reader.readLine();
			if (Strings.isNullOrEmpty(password)) {
				break;
			}
			final String passwordHash = createPasswordHash(HASH_ALGORITHM,
					HASH_ENCODING, HASH_CHARSET, null, password);
			System.out.println("Verschluesselt: " + passwordHash
					+ System.getProperty("line.separator"));
		}

		System.out.println("FERTIG");
	}
	
	public String findKundeByUserName(String Name)
	{
		 AbstractKunde k = ks.findKundeByUserName(Name);
		 return Long.toString(k.getId());
	}



}
