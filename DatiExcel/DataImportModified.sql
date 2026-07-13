-- AI-generated PostgreSQL seed import for VetPortal.
-- Source workbook: DatiExcel/Dati_file_unico.xlsx
-- Target model: Spring Boot JPA entities under ProgettoSpring/LBP-App-Vet/src/main/java/com/WW/entities.
-- Generated: 2026-07-11
--
-- Notes:
-- 1. Identifiers use Hibernate/Spring physical naming style: lowercase snake_case.
-- 2. Explicit IDs keep workbook relationships stable. Sequence values are reset at the end.
-- 3. Seed users share BCrypt password hash for the plain password: password.
-- 4. The Visite sheet has no Doctor column. Veterinarian users are generated and assigned deterministically.
-- 5. Visit type IDs for Visite are deterministic pseudo-random choices from the Tipi di Visita sheet.

BEGIN;

-- ============================================================
-- 1. Lookup tables: roles, species, breeds
-- Import order: ruoli -> specie -> razze.
-- ============================================================

INSERT INTO ruoli (id, ruolo) VALUES
    (1, 'CLIENTE'),
    (2, 'VETERINARIO'),
    (3, 'RECEPTIONIST'),
    (4, 'ADMIN')
ON CONFLICT (id) DO UPDATE SET ruolo = EXCLUDED.ruolo;

INSERT INTO specie (id, nome, is_deleted) VALUES
    (1, 'Cane', FALSE),
    (2, 'Gatto', FALSE),
    (3, 'Coniglio', FALSE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, is_deleted = EXCLUDED.is_deleted;

-- Sheet breeds from Animali plus 30 searched/common additional breeds.
INSERT INTO razze (id, nome, id_specie, is_deleted) VALUES
    (1, 'Labrador', 1, FALSE),
    (2, 'Pastore Tedesco', 1, FALSE),
    (3, 'Europeo Comune', 2, FALSE),
    (4, 'Bassotto', 1, FALSE),
    (5, 'Persiano', 2, FALSE),
    (6, 'Golden Retriever', 1, FALSE),
    (7, 'Siamese', 2, FALSE),
    (8, 'Beagle', 1, FALSE),
    (9, 'Maine Coon', 2, FALSE),
    (10, 'Boxer', 1, FALSE),
    (11, 'Nano Olandese', 3, FALSE),
    (12, 'Rottweiler', 1, FALSE),
    (13, 'Ragdoll', 2, FALSE),
    (14, 'Border Collie', 1, FALSE),
    (15, 'Birmano', 2, FALSE),
    (16, 'Barboncino', 1, FALSE),
    (17, 'Cocker Spaniel', 1, FALSE),
    (18, 'Ariete', 3, FALSE),
    (19, 'Bouledogue Francese', 1, FALSE),
    (20, 'Bulldog', 1, FALSE),
    (21, 'Bracco Tedesco', 1, FALSE),
    (22, 'Yorkshire Terrier', 1, FALSE),
    (23, 'Pastore Australiano', 1, FALSE),
    (24, 'Cavalier King Charles Spaniel', 1, FALSE),
    (25, 'Cane Corso', 1, FALSE),
    (26, 'Welsh Corgi Pembroke', 1, FALSE),
    (27, 'Dobermann', 1, FALSE),
    (28, 'Schnauzer Nano', 1, FALSE),
    (29, 'Bovaro del Bernese', 1, FALSE),
    (30, 'Shih Tzu', 1, FALSE),
    (31, 'Alano', 1, FALSE),
    (32, 'Volpino di Pomerania', 1, FALSE),
    (33, 'Boston Terrier', 1, FALSE),
    (34, 'Havanese', 1, FALSE),
    (35, 'Siberian Husky', 1, FALSE),
    (36, 'Chihuahua', 1, FALSE),
    (37, 'Springer Spaniel Inglese', 1, FALSE),
    (38, 'Shetland Sheepdog', 1, FALSE),
    (39, 'British Shorthair', 2, FALSE),
    (40, 'Bengal', 2, FALSE),
    (41, 'Sphynx', 2, FALSE),
    (42, 'Abissino', 2, FALSE),
    (43, 'Siberiano', 2, FALSE),
    (44, 'Exotic Shorthair', 2, FALSE),
    (45, 'Holland Lop', 3, FALSE),
    (46, 'Mini Lop', 3, FALSE),
    (47, 'Mini Rex', 3, FALSE),
    (48, 'Lionhead', 3, FALSE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, id_specie = EXCLUDED.id_specie, is_deleted = EXCLUDED.is_deleted;


-- ============================================================
-- 2. Users
-- Proprietari sheet -> utenti with CLIENTE role.
-- Additional generated users provide VETERINARIO, RECEPTIONIST, and ADMIN seed accounts.
-- ============================================================

INSERT INTO utenti (id, nome, cognome, email, password, codice_fiscale, telefono, indirizzo, citta, data_registrazione, id_azienda, id_ruolo, is_deleted) VALUES
    (1, 'Marco', 'Rossi', 'marco.rossi@email.it', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SEEDCLIENTE0001', '335 1234567', 'Via Roma 12', 'Bergamo', '2019-03-15 09:00:00', NULL, 1, FALSE),
    (2, 'Laura', 'Verdi', 'laura.verdi@clinicazampetti.it', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'VRDLRA85A41A794K', '035 1110001', 'Via Zambonate 1', 'Bergamo', '2024-01-01 09:00:00', NULL, 2, FALSE),
    (3, 'Camillo', 'Zampetti', 'camillo.zampetti@clinicazampetti.it', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ZMPCLL80A01A794Z', '035 1110004', 'Via Zambonate 1', 'Bergamo', '2024-01-01 09:00:00', NULL, 3, FALSE),
    (4, 'Admin', 'Zampetti', 'admin@clinicazampetti.it', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ZMPDMN80A01A794Z', '035 1110000', 'Via Zambonate 1', 'Bergamo', '2024-01-01 09:00:00', NULL, 4, FALSE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, cognome = EXCLUDED.cognome, email = EXCLUDED.email, password = EXCLUDED.password, codice_fiscale = EXCLUDED.codice_fiscale, telefono = EXCLUDED.telefono, indirizzo = EXCLUDED.indirizzo, citta = EXCLUDED.citta, data_registrazione = EXCLUDED.data_registrazione, id_azienda = EXCLUDED.id_azienda, id_ruolo = EXCLUDED.id_ruolo, is_deleted = EXCLUDED.is_deleted;
-- password hashata è 'password'

-- ============================================================
-- 3. Animals
-- Animali sheet -> animali. Species and breed are stored as strings in the current entity.
-- ============================================================

INSERT INTO animali (id, nome, specie, razza, sesso, data_nascita, peso, microchip, note, is_deleted, id_utente) VALUES
    (1, 'Briciola', 'Cane', 'Labrador', 'F', '2017-05-12', 28.5, '380260040123456', NULL, FALSE, 1),
    (2, 'Rex', 'Cane', 'Pastore Tedesco', 'M', '2018-09-03', 34.0, '380260040234567', 'Allergia al pollame', FALSE, 2),
    (3, 'Luna', 'Gatto', 'Europeo Comune', 'F', '2020-02-17', 4.2, '380260040345678', NULL, FALSE, 2),
    (4, 'Pallina', 'Cane', 'Bassotto', 'F', '2016-11-28', 9.1, '380260040456789', 'Problemi alla schiena', FALSE, 3),
    (5, 'Micio', 'Gatto', 'Persiano', 'M', '2019-04-06', 5.8, '380260040567890', NULL, FALSE, 4),
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, specie = EXCLUDED.specie, razza = EXCLUDED.razza, sesso = EXCLUDED.sesso, data_nascita = EXCLUDED.data_nascita, peso = EXCLUDED.peso, microchip = EXCLUDED.microchip, note = EXCLUDED.note, is_deleted = EXCLUDED.is_deleted, id_utente = EXCLUDED.id_utente;


-- ============================================================
-- 4. Visit catalog
-- Tipi di Visita sheet -> categorie_visite and tipi_visite.
-- ============================================================

INSERT INTO categorie_visite (id, nome, is_deleted) VALUES
    (1, 'Routine', FALSE),
    (2, 'Prevenzione', FALSE),
    (3, 'Diagnostica', FALSE),
    (4, 'Specialistica', FALSE),
    (5, 'Odontoiatria', FALSE),
    (6, 'Chirurgia', FALSE),
    (7, 'Piccoli interventi', FALSE),
    (8, 'Urgenze', FALSE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, is_deleted = EXCLUDED.is_deleted;

INSERT INTO tipi_visite (id, nome, durata, id_categoria, prezzo, id_dottore, is_deleted, attivo) VALUES
    (1, 'Prima visita (nuovo paziente)', 30, 1, 55.0, 16, FALSE, TRUE),
    (2, 'Visita di controllo', 20, 1, 40.0, 17, FALSE, TRUE),
    (3, 'Visita clinica generale', 30, 1, 50.0, 18, FALSE, TRUE),
    (4, 'Visita geriatrica (animali > 7 anni)', 45, 1, 65.0, 16, FALSE, TRUE),
    (5, 'Vaccinazione singola dose', 15, 2, 35.0, 17, FALSE, TRUE),
    (6, 'Vaccinazione polivalente cane (DHPPI+L)', 20, 2, 55.0, 18, FALSE, TRUE),
    (7, 'Vaccinazione antirabbica', 15, 2, 40.0, 16, FALSE, TRUE),
    (8, 'Richiamo annuale vaccinazioni', 20, 2, 50.0, 17, FALSE, TRUE),
    (9, 'Sverminazione', 10, 2, 20.0, 18, FALSE, TRUE),
    (10, 'Antiparassitario topico (applicazione)', 10, 2, 20.0, 16, FALSE, TRUE),
    (11, 'Microchippatura', 15, 2, 30.0, 17, FALSE, TRUE),
    (12, 'Prelievo ematico', 15, 3, 25.0, 18, FALSE, TRUE),
    (13, 'Ecografia addominale', 30, 3, 90.0, 16, FALSE, TRUE),
    (14, 'Ecografia cardiaca', 45, 3, 110.0, 17, FALSE, TRUE),
    (15, 'Radiografia (fino a 2 proiezioni)', 20, 3, 80.0, 18, FALSE, TRUE),
    (16, 'Misurazione pressione arteriosa', 15, 3, 25.0, 16, FALSE, TRUE),
    (17, 'Elettrocardiogramma (ECG)', 20, 3, 60.0, 17, FALSE, TRUE),
    (18, 'Esame citologico cutaneo', 20, 3, 45.0, 18, FALSE, TRUE),
    (19, 'Otoscopia ed esame auricolare', 20, 3, 35.0, 16, FALSE, TRUE),
    (20, 'Visita dermatologica', 30, 4, 65.0, 17, FALSE, TRUE),
    (21, 'Visita oculistica', 30, 4, 70.0, 18, FALSE, TRUE),
    (22, 'Visita ortopedica', 45, 4, 75.0, 16, FALSE, TRUE),
    (23, 'Consulenza comportamentale', 45, 4, 70.0, 17, FALSE, TRUE),
    (24, 'Visita nutrizionale e dietetica', 30, 4, 45.0, 18, FALSE, TRUE),
    (25, 'Visita odontoiatrica', 20, 5, 40.0, 16, FALSE, TRUE),
    (26, 'Detartrasi (pulizia tartaro in anestesia)', 60, 5, 130.0, 17, FALSE, TRUE),
    (27, 'Sterilizzazione gatta', 60, 6, 180.0, 18, FALSE, TRUE),
    (28, 'Castrazione gatto maschio', 45, 6, 120.0, 16, FALSE, TRUE),
    (29, 'Sterilizzazione cagna (≤ 10 kg)', 75, 6, 250.0, 17, FALSE, TRUE),
    (30, 'Sterilizzazione cagna (10–25 kg)', 90, 6, 320.0, 18, FALSE, TRUE),
    (31, 'Sterilizzazione cagna (> 25 kg)', 105, 6, 400.0, 16, FALSE, TRUE),
    (32, 'Castrazione cane maschio', 60, 6, 200.0, 17, FALSE, TRUE),
    (33, 'Medicazione ferita semplice', 20, 7, 35.0, 18, FALSE, TRUE),
    (34, 'Medicazione ferita complessa / bendaggio', 30, 7, 55.0, 16, FALSE, TRUE),
    (35, 'Rimozione punti di sutura', 15, 7, 25.0, 17, FALSE, TRUE),
    (36, 'Iniezione intramuscolare / sottocutanea', 10, 7, 20.0, 18, FALSE, TRUE),
    (37, 'Infusione endovenosa', 30, 7, 45.0, 16, FALSE, TRUE),
    (38, 'Lavaggio auricolare', 15, 7, 30.0, 17, FALSE, TRUE),
    (39, 'Taglio unghie', 10, 7, 15.0, 18, FALSE, TRUE),
    (40, 'Visita d''urgenza (in orario)', 30, 8, 85.0, 16, FALSE, TRUE),
    (41, 'Visita fuori orario / reperibilità', 30, 8, 110.0, 17, FALSE, TRUE)
ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, durata = EXCLUDED.durata, id_categoria = EXCLUDED.id_categoria, prezzo = EXCLUDED.prezzo, id_dottore = EXCLUDED.id_dottore, is_deleted = EXCLUDED.is_deleted, attivo = EXCLUDED.attivo;


-- ============================================================
-- 5. Vaccinations
-- Vaccinazioni sheet -> tipi_vaccino and vaccinazioni.
-- Data Scadenza and Stato are not present in the current Vaccinazione entity, so they are not inserted.
-- ============================================================

INSERT INTO tipi_vaccino (id, tipologia, durata, note, is_deleted) VALUES
    (1, 'Polivalente cane (DHPPI)', 366, 'Duration inferred from Data Scadenza in Dati_file_unico.xlsx.', FALSE),
    (2, 'Antirabbico', 1096, 'Duration inferred from Data Scadenza in Dati_file_unico.xlsx.', FALSE),
    (3, 'Trivalente felina (HCP)', 366, 'Duration inferred from Data Scadenza in Dati_file_unico.xlsx.', FALSE),
    (4, 'Leucemia felina (FeLV)', 366, 'Duration inferred from Data Scadenza in Dati_file_unico.xlsx.', FALSE),
    (5, 'Leishmaniosi', 366, 'Duration inferred from Data Scadenza in Dati_file_unico.xlsx.', FALSE)
ON CONFLICT (id) DO UPDATE SET tipologia = EXCLUDED.tipologia, durata = EXCLUDED.durata, note = EXCLUDED.note, is_deleted = EXCLUDED.is_deleted;

INSERT INTO vaccinazioni (id, id_tipo, data_vaccinazione, lotto, id_animale, is_deleted) VALUES
    (1, 1, '2024-01-10 10:00:00', 'LOT-2024-0011', 1, FALSE),
    (2, 2, '2024-01-10 10:00:00', 'LOT-2024-0012', 1, FALSE),
    (3, 1, '2023-03-15 10:00:00', 'LOT-2023-0045', 2, FALSE),
    (4, 2, '2023-03-15 10:00:00', 'LOT-2023-0046', 2, FALSE),
    (5, 3, '2024-01-18 10:00:00', 'LOT-2024-0031', 3, FALSE),
    (6, 4, '2024-01-18 10:00:00', 'LOT-2024-0032', 3, FALSE),
    (7, 3, '2023-04-22 10:00:00', 'LOT-2023-0078', 5, FALSE),
    (8, 1, '2024-01-15 10:00:00', 'LOT-2024-0019', 6, FALSE),
    (9, 2, '2024-01-15 10:00:00', 'LOT-2024-0020', 6, FALSE),
    (10, 5, '2024-01-15 10:00:00', 'LOT-2024-0021', 6, FALSE),
    (11, 3, '2024-02-19 10:00:00', 'LOT-2024-0041', 7, FALSE),
    (12, 1, '2023-05-20 10:00:00', 'LOT-2023-0099', 8, FALSE),
    (13, 1, '2023-06-07 10:00:00', 'LOT-2023-0102', 9, FALSE),
    (14, 2, '2023-06-07 10:00:00', 'LOT-2023-0103', 9, FALSE),
    (15, 3, '2023-03-14 10:00:00', 'LOT-2023-0061', 10, FALSE),
    (16, 1, '2023-08-10 10:00:00', 'LOT-2023-0121', 11, FALSE),
    (17, 3, '2024-02-26 10:00:00', 'LOT-2024-0051', 13, FALSE),
    (18, 1, '2024-01-29 10:00:00', 'LOT-2024-0022', 14, FALSE),
    (19, 2, '2024-01-29 10:00:00', 'LOT-2024-0023', 14, FALSE),
    (20, 3, '2024-03-19 10:00:00', 'LOT-2024-0062', 15, FALSE),
    (21, 1, '2023-11-10 10:00:00', 'LOT-2023-0155', 16, FALSE),
    (22, 2, '2023-11-10 10:00:00', 'LOT-2023-0156', 16, FALSE),
    (23, 3, '2024-03-08 10:00:00', 'LOT-2024-0058', 17, FALSE),
    (24, 1, '2023-04-25 10:00:00', 'LOT-2023-0088', 18, FALSE),
    (25, 1, '2023-07-12 10:00:00', 'LOT-2023-0112', 19, FALSE)
ON CONFLICT (id) DO UPDATE SET id_tipo = EXCLUDED.id_tipo, data_vaccinazione = EXCLUDED.data_vaccinazione, lotto = EXCLUDED.lotto, id_animale = EXCLUDED.id_animale, is_deleted = EXCLUDED.is_deleted;


-- ============================================================
-- 6. Visits
-- Visite sheet -> visita using only animal/date plus generated veterinarian assignment.
-- tipo_visita is deterministic pseudo-random from Tipi di Visita. Payments are intentionally NULL.
-- ============================================================

INSERT INTO visita (id, data_visita, id_tipo_visita, id_animale, id_veterinario, id_pagamento, note, nota_privata, stato, is_deleted) VALUES
    (1, '2024-01-10 09:00:00', 27, 1, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (2, '2024-01-12 10:00:00', 34, 2, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (3, '2024-01-15 11:00:00', 8, 6, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (4, '2024-01-18 12:00:00', 31, 3, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (5, '2024-01-22 13:00:00', 28, 10, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (6, '2024-01-25 14:00:00', 39, 8, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (7, '2024-01-29 15:00:00', 13, 14, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (8, '2024-02-02 16:00:00', 24, 5, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (9, '2024-02-07 09:30:00', 27, 9, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (10, '2024-02-12 10:30:00', 13, 16, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (11, '2024-02-15 11:30:00', 41, 4, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (12, '2024-02-19 12:30:00', 7, 7, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (13, '2024-02-22 13:30:00', 37, 11, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (14, '2024-02-26 14:30:00', 4, 13, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (15, '2024-03-01 15:30:00', 12, 1, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (16, '2024-03-05 16:30:00', 37, 19, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (17, '2024-03-08 09:00:00', 4, 17, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (18, '2024-03-12 10:00:00', 20, 18, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (19, '2024-03-15 11:00:00', 24, 6, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (20, '2024-03-19 12:00:00', 8, 15, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (21, '2024-03-22 13:00:00', 37, 2, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (22, '2024-03-26 14:00:00', 5, 20, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (23, '2024-04-02 15:00:00', 27, 12, 17, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (24, '2024-04-09 16:00:00', 34, 3, 18, NULL, NULL, NULL, 'COMPLETATA', FALSE),
    (25, '2024-04-16 09:30:00', 41, 14, 16, NULL, NULL, NULL, 'COMPLETATA', FALSE)
ON CONFLICT (id) DO UPDATE SET data_visita = EXCLUDED.data_visita, id_tipo_visita = EXCLUDED.id_tipo_visita, id_animale = EXCLUDED.id_animale, id_veterinario = EXCLUDED.id_veterinario, id_pagamento = EXCLUDED.id_pagamento, note = EXCLUDED.note, nota_privata = EXCLUDED.nota_privata, stato = EXCLUDED.stato, is_deleted = EXCLUDED.is_deleted;


-- ============================================================
-- 7. Reset identity sequences after explicit IDs
-- ============================================================

SELECT setval(pg_get_serial_sequence('ruoli', 'id'), COALESCE((SELECT MAX(id) FROM ruoli), 1), TRUE);

SELECT setval(pg_get_serial_sequence('specie', 'id'), COALESCE((SELECT MAX(id) FROM specie), 1), TRUE);

SELECT setval(pg_get_serial_sequence('razze', 'id'), COALESCE((SELECT MAX(id) FROM razze), 1), TRUE);

SELECT setval(pg_get_serial_sequence('utenti', 'id'), COALESCE((SELECT MAX(id) FROM utenti), 1), TRUE);

SELECT setval(pg_get_serial_sequence('animali', 'id'), COALESCE((SELECT MAX(id) FROM animali), 1), TRUE);

SELECT setval(pg_get_serial_sequence('categorie_visite', 'id'), COALESCE((SELECT MAX(id) FROM categorie_visite), 1), TRUE);

SELECT setval(pg_get_serial_sequence('tipi_visite', 'id'), COALESCE((SELECT MAX(id) FROM tipi_visite), 1), TRUE);

SELECT setval(pg_get_serial_sequence('tipi_vaccino', 'id'), COALESCE((SELECT MAX(id) FROM tipi_vaccino), 1), TRUE);

SELECT setval(pg_get_serial_sequence('vaccinazioni', 'id'), COALESCE((SELECT MAX(id) FROM vaccinazioni), 1), TRUE);

SELECT setval(pg_get_serial_sequence('visita', 'id'), COALESCE((SELECT MAX(id) FROM visita), 1), TRUE);


COMMIT;
