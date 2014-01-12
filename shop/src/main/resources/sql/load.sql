-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================
--Insert Tabelle Artikel
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (300,0,'Tisch ''Oval''',80,0,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (301,0,'Stuhl ''Sitz bequem''',10,0,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (302,0,'Tür ''Hoch und breit''',300,0,'03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (303,0,'Fenster ''Glasklar''',150,0,'04.08.2006 00:00:00','04.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (304,0,'Spiegel ''Mach mich schöner''',60,0,'05.08.2006 00:00:00','05.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (305,0,'Kleiderschrank ''Viel Platz''',500,0,'06.08.2006 00:00:00','06.08.2006 00:00:00');
INSERT INTO artikel (id, version, bezeichnung, preis, ausgesondert, erzeugt, aktualisiert) VALUES (306,0,'Bett ''Mit Holzwurm''',600,0,'07.08.2006 00:00:00','07.08.2006 00:00:00');

-- Insert Tabelle Kunde
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (1,'Admin','Admin','01.01.2001','P',NULL,NULL,1,'0,1',0,'admin@hska.de','1','01.08.2006 00:00:00','01.08.2006 00:00:00',0);
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (101,'Alpha','Adrian','31.01.2001','P',1,'M',1,'0,1','1500,5','101@hska.de','Ftw2iom0KLJIVIQxO6Z6ORLKA/KytCQpF0pPiz3ITkQ=','01.08.2006 00:00:00','01.08.2006 00:00:00',0);
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (102,'Alpha','Alfred','28.02.2002','P',0,'M',1,0,'500,5','102@hska.de','N4NPLyV2LyPh90pTHL5EXbc9Z2Xr5gh4p9++zX1K9uE=','02.08.2006 00:00:00','02.08.2006 00:00:00',0);
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (103,'Alpha','Anton','15.09.2003','P',NULL,NULL,0,'0,1','0,5','103@hska.de','RU9jrDDIMimX7wJe3/ar0j4NvnuKPVEmqJTkoWjBtZs=','03.08.2006 00:00:00','03.08.2006 00:00:00',0);
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (104,'Delta','Dirk','30.04.2004','P',NULL,NULL,1,'0,15','1500,5','104@hska.de','Xvb98yUTqnzRH3K+zPEyuSJNM/JxRx//QCdCiHoXHt8=','04.08.2006 00:00:00','04.08.2006 00:00:00',0);
INSERT INTO kunde (id, nachname, vorname, seit, art, familienstand_fk, geschlecht_fk, newsletter, rabatt, umsatz, email, password, erzeugt, aktualisiert,version) VALUES (105,'Epsilon','Emil','31.03.2005','P',2,'M',0,'0,0','1500,5','105@hska.de','105','05.08.2006 00:00:00','05.08.2006 00:00:00',0);


CALL insert_file_kunde(101,1,0,'image.png','Privatkunde_101.png','png','I','01.01.2007 01:00:00','01.01.2007 01:00:00');
CALL insert_file_kunde(102,2,0,'video.mp4','Privatkunde_102.mp4','mp4','V','01.01.2007 01:00:00','01.01.2007 01:00:00');

INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (1,'admin');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (1,'mitarbeiter');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (1,'abteilungsleiter');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (1,'kunde');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (101,'admin');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (101,'mitarbeiter');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (101,'kunde');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (102,'mitarbeiter');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (102,'kunde');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (103,'mitarbeiter');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (103,'kunde');
INSERT INTO kunde_rolle (kunde_fk, rolle) VALUES (104,'kunde');

--Insert für Aderesse
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (200,'76133','Karlsruhe','Moltkestraße','30',1,'01.08.2006 00:00:00','01.08.2006 00:00:00',0);
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (201,'76133','Karlsruhe','Moltkestraße','31',101,'02.08.2006 00:00:00','02.08.2006 00:00:00',0);
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (202,'76133','Karlsruhe','Moltkestraße','32',102,'03.08.2006 00:00:00','03.08.2006 00:00:00',0);
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (203,'76133','Karlsruhe','Moltkestraße','33',103,'04.08.2006 00:00:00','04.08.2006 00:00:00',0);
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (204,'76133','Karlsruhe','Moltkestraße','34',104,'05.08.2006 00:00:00','05.08.2006 00:00:00',0);
INSERT INTO adresse (id, plz, ort, strasse, hausnr, kunde_fk, erzeugt, aktualisiert,version) VALUES (205,'76133','Karlsruhe','Moltkestraße','35',105,'06.08.2006 00:00:00','06.08.2006 00:00:00',0);

--Insert Tabelle Bestellung
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert,version) VALUES (400,101,0,'01.08.2006 00:00:00','01.08.2006 00:00:00',0);
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert,version) VALUES (401,101,1,'02.08.2006 00:00:00','02.08.2006 00:00:00',0);
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert,version) VALUES (402,102,0,'03.08.2006 00:00:00','03.08.2006 00:00:00',0);
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert,version) VALUES (403,102,1,'04.08.2006 00:00:00','04.08.2006 00:00:00',0);
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert,version) VALUES (404,104,0,'05.08.2006 00:00:00','05.08.2006 00:00:00',0);

--
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (500,400,300,1,0,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (501,400,301,4,1,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (502,401,302,5,0,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (503,402,303,3,0,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (504,402,304,2,1,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (505,403,305,1,0,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (506,404,300,5,0,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (507,404,301,2,1,0);
INSERT INTO bestellposition (id, bestellung_fk, artikel_fk, anzahl, idx,version) VALUES (508,404,302,8,2,0);
--
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (101,'L');
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (101,'S');
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (102,'S');
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (102,'R');
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (105,'L');
INSERT INTO kunde_hobby (kunde_fk, hobby_fk) VALUES (105,'R');


INSERT INTO lieferung (id, liefernr, transport_art_fk, erzeugt, aktualisiert) VALUES (600,'20051005-001',0,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO lieferung (id, liefernr, transport_art_fk, erzeugt, aktualisiert) VALUES (601,'20051005-002',1,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO lieferung (id, liefernr, transport_art_fk, erzeugt, aktualisiert) VALUES (602,'20051005-003',2,'03.08.2006 00:00:00','03.08.2006 00:00:00');
INSERT INTO lieferung (id, liefernr, transport_art_fk, erzeugt, aktualisiert) VALUES (603,'20051008-001',3,'04.08.2006 00:00:00','04.08.2006 00:00:00');

--
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (400,600);
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (401,600);
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (402,601);
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (402,602);
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (403,602);
INSERT INTO bestellung_lieferung (bestellung_fk, lieferung_fk) VALUES (404,603);

INSERT INTO geschlecht VALUES (0, 'MAENNLICH');
INSERT INTO geschlecht VALUES (1, 'WEIBLICH');

--
INSERT INTO familienstand VALUES(0, 'LEDIG');
INSERT INTO familienstand VALUES(1, 'VERHEIRATET');
INSERT INTO familienstand VALUES(2, 'GESCHIEDEN');
INSERT INTO familienstand VALUES(3, 'VERWITWET');

--
INSERT INTO hobby VALUES (0, 'SPORT');
INSERT INTO hobby VALUES (1, 'LESEN');
INSERT INTO hobby VALUES (2, 'REISEN');

-- 
INSERT INTO transport_art VALUES (0, 'STRASSE');
INSERT INTO transport_art VALUES (1, 'SCHIENE');
INSERT INTO transport_art VALUES (2, 'LUFT');
INSERT INTO transport_art VALUES (3, 'WASSER');

INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (1,'31.01.2005','Wartungsvertrag_1_K101',101,0,'01.08.2006 00:00:00','01.08.2006 00:00:00');
INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (2,'31.01.2006','Wartungsvertrag_2_K101',101,1,'02.08.2006 00:00:00','02.08.2006 00:00:00');
INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (3,'30.06.2006','Wartungsvertrag_1_K102',102,0,'03.08.2006 00:00:00','03.08.2006 00:00:00');

