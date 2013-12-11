-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================

-- ===============================================================================
-- Tabellen fuer Enum-Werte *einmalig* anlegen und jeweils Werte einfuegen
-- Beim ALLERERSTEN Aufruf die Zeilen mit "DROP TABLE ..." durch -- auskommentieren
-- ===============================================================================
--DROP TABLE bestellposition;
-- CREATE TABLE bestellposition(id NUMBER(4) NOT NULL PRIMARY KEY, bestellung_fk NUMBER(4) NOT NULL, artikel_fk NUMBER(4) NOT NULL, anzahl NUMBER(4) NOT NULL, idx NUMBER(1))CACHE;

--DROP TABLE bestellung_lieferung;
-- CREATE TABLE bestellung_lieferung(bestellung_fk NUMBER(4), lieferung_fk NUMBER(4))CACHE;

--DROP TABLE bestellung
 --CREATE TABLE bestellung (id NUMBER(4) NOT NULL PRIMARY KEY, ausgeliefert NUMBER(1), kunde_fk NUMBER(4), idx NUMBER(2), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP NOT NULL) CACHE;

--DROP TABLE artikel;
-- CREATE TABLE artikel(id NUMBER(4) NOT NULL PRIMARY KEY,version NUMBER(4) NOT NULL,bezeichnung VARCHAR2(50), preis NUMBER(4), ausgesondert VARCHAR2(5), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP NOT NULL) CACHE;

--DROP TABLE kunde_hobby;
-- CREATE TABLE kunde_hobby(kunde_fk NUMBER(4), hobby_fk NUMBER(4)) CACHE;

--DROP TABLE wartungsvertrag;
-- CREATE TABLE wartungsvertrag(nr NUMBER(4)NOT NULL PRIMARY KEY, datum DATE, inhalt VARCHAR2(30), kunde_fk NUMBER(4) NOT NULL, idx NUMBER(1), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP)CACHE;

--DROP TABLE adresse;
-- CREATE TABLE adresse (id NUMBER(4) NOT NULL PRIMARY KEY, plz NUMBER(5) NOT NULL, ort VARCHAR2(30) NOT NULL, strasse VARCHAR2(30) NOT NULL, hausnr VARCHAR2(10) NOT NULL, kunde_fk NUMBER(4), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP NOT NULL)CACHE;

DROP TABLE geschlecht;
CREATE TABLE geschlecht(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(10) NOT NULL UNIQUE) CACHE;

DROP TABLE familienstand;
CREATE TABLE familienstand(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(12) NOT NULL UNIQUE) CACHE;

DROP TABLE hobby;
CREATE TABLE hobby(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(16) NOT NULL UNIQUE) CACHE;

--DROP TABLE kunde;
-- CREATE TABLE kunde(id NUMBER(4)NOT NULL PRIMARY KEY, nachname VARCHAR2(50) NOT NULL, vorname VARCHAR2(50) NOT NULL,seit DATE , art VARCHAR2(1), familienstand_fk NUMBER(1), geschlecht_fk NUMBER(1), newsletter VARCHAR2(5), rabatt DECIMAL(10), umsatz DECIMAL(10), email VARCHAR2(100), password VARCHAR2(50), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP NOT NULL) CACHE;

--DROP TABLE lieferung;
-- CREATE TABLE lieferung(id NUMBER(4) NOT NULL PRIMARY KEY, liefernr VARCHAR2(12), transport_art_fk NUMBER(1), erzeugt TIMESTAMP NOT NULL, aktualisiert TIMESTAMP NOT NULL)CACHE;

DROP TABLE transport_art;
CREATE TABLE transport_art(id NUMBER(1) NOT NULL PRIMARY KEY, txt VARCHAR2(8) NOT NULL UNIQUE) CACHE;

DROP SEQUENCE hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 5000;

--CREATE INDEX adresse__kunde_index ON adresse(kunde_fk);
--CREATE INDEX kunde_hobby__kunde_index ON kunde_hobby(kunde_fk);
--CREATE INDEX wartungsvertrag__kunde_index ON wartungsvertrag(kunde_fk);
--CREATE INDEX bestellung__kunde_index ON bestellung(kunde_fk);
--CREATE INDEX bestpos__bestellung_index ON bestellposition(bestellung_fk);
--CREATE INDEX bestpos__artikel_index ON bestellposition(artikel_fk);

-- ===============================================================================
-- Fremdschluessel in den bereits *generierten* Tabellen auf die obigen "Enum-Tabellen" anlegen
-- ===============================================================================
--ALTER TABLE kunde ADD CONSTRAINT kunde__geschlecht_fk FOREIGN KEY (geschlecht_fk) REFERENCES geschlecht;
--ALTER TABLE kunde ADD CONSTRAINT kunde__familienstand_fk FOREIGN KEY (familienstand_fk) REFERENCES familienstand;
--ALTER TABLE kunde_hobby ADD CONSTRAINT kunde_hobby__hobby_fk FOREIGN KEY (hobby_fk) REFERENCES hobby;
--ALTER TABLE lieferung ADD CONSTRAINT lieferung__transport_art_fk FOREIGN KEY (transport_art_fk) REFERENCES transport_art on delete cascade;

--ALTER TABLE kunde_hobby ADD CONSTRAINT kunde_hobby__kunde_fk FOREIGN KEY (kunde_fk) REFERENCES kunde on delete cascade;
--ALTER TABLE adresse ADD CONSTRAINT adresse__kunde_fk FOREIGN KEY (kunde_fk) REFERENCES kunde on delete cascade;
--ALTER TABLE wartungsvertrag ADD CONSTRAINT wartungsvertrag__kunde_fk FOREIGN KEY (kunde_fk) REFERENCES kunde;
--ALTER TABLE bestellposition ADD CONSTRAINT bestellposition__bestellung_fk FOREIGN KEY (bestellung_fk) REFERENCES bestellung;
--ALTER TABLE bestellposition ADD CONSTRAINT bestellposition__artikel_fk FOREIGN KEY (artikel_fk) REFERENCES artikel;
--ALTER TABLE bestellung_lieferung ADD CONSTRAINT bl__bestellung_fk FOREIGN KEY (bestellung_fk) REFERENCES bestellung;
--ALTER TABLE bestellung_lieferung ADD CONSTRAINT bl__lieferung_fk FOREIGN KEY (lieferung_fk) REFERENCES lieferung;

