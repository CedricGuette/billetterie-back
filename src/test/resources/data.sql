--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS users;
CREATE TABLE users 
(
  dtype varchar(31) NOT NULL,
  id UUID DEFAULT RANDOM_UUID() NOT NULL ,
  created_date TIMESTAMP(6) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  role VARCHAR(255) DEFAULT NULL,
  username VARCHAR(255) DEFAULT NULL,
  first_login BOOLEAN DEFAULT NULL,
  customer_key UUID DEFAULT NULL,
  first_name VARCHAR(255) DEFAULT NULL,
  last_name VARCHAR(255) DEFAULT NULL,
  phone_number VARCHAR(255) DEFAULT NULL,
  profile_is_validate BOOLEAN DEFAULT NULL,
  verification_photo_id VARCHAR(255) DEFAULT NULL
);

INSERT INTO users VALUES
('Admin','019d5397-0a89-485f-95e2-00451582f1cd','2025-06-20 16:49:39.500601','$2a$10$tAWBrxJMllWWCNwy5ypgIORd7TVByoBlWNvjoO7gAROEIQ1n8rV4q','ROLE_ADMIN','a@a',FALSE,NULL,NULL,NULL,NULL,NULL,NULL),
('Customer','43729766-67b3-47d2-80f7-6ab87e0dd0b1','2025-06-20 16:51:01.867671','$2a$10$j2bAoW6akvEoX7SUAhkEqulBZ5Rj9cO0Q8t9F0cZDXksn0bbBeR8G','ROLE_USER','gabriel@gmail.com',NULL,'8d771743-187c-4e59-bdad-364046cd0803','Gabriel','Lapage','0102050607',TRUE,'e78432f1-34b1-4da8-aa53-92e0850e4f2c'),
('Customer','67c3557c-174d-4017-b8f6-d9ca5e6aaf71','2025-06-20 19:46:38.364679','$2a$10$eh.dfBZ7raZcUNgAVw2Y0Oy9.UvdoO9UAfnY09hIYJEgRqB40QiVi','ROLE_USER','fabien@gmail.com',NULL,'d04da381-2112-47a1-90ab-05f9b5d66a29','Fabien','Delacompta','0102050607',TRUE,'dff0e290-ba63-436e-adf9-681f2fb84ec2'),
('Moderator','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd','2025-06-20 16:49:53.700600','$2a$10$cSZhT8TSMQhwlFlxrFX3qe76a9FtMfU5tMMUFT9Vsq2ZJlLWZAL6a','ROLE_MODERATOR','m@m',NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('Customer','7cea4f86-f0f4-4a6c-9504-156c4f7ece5f','2025-06-20 19:19:47.113397','$2a$10$.xTcIa2yHmwAyu7IHC3/t.OGpHsH.K0vNnTskVfrkv6nP4HuPvIhC','ROLE_USER','pablo@gmail.com',NULL,'44b1621e-7cb6-4526-ba14-d334357ecb63','Pablo','Picasso','0102050607',TRUE,'2191cfde-50eb-4d30-8eff-b978f07ebc81'),
('Security','8f2ff1cd-8075-43a3-b504-a3fbfcb6b9e4','2025-06-20 16:50:03.832100','$2a$10$wWfDZ6bWSybExEUdzH0UbuDrD0SaRln27uI7QOwgAD7n9T98kxnt6','ROLE_SECURITY','s@s',NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('Customer','9a65095a-3edb-451c-90cf-20b5afac8b6f','2025-06-20 20:46:50.771745','$2a$10$9QhzVHeCnvroRK91dn6A0.s98sLsrf9HAENrYR99URbwS1W5mfuUq','ROLE_USER','patoche@msn.com',NULL,'94b7847a-c08c-4707-b5cf-0df4245d5ce3','Patrick','Petit','0102050607',TRUE,'56245662-63e3-4c82-95e1-8e934c70ffdd'),
('Security','bc9ba1a3-7e30-4e62-9573-d6c150326be7','2025-06-20 16:50:07.127310','$2a$10$eQb0iT.QpDsklPyp/MWeXOlGA7VSjZ4/.DfFrxe7EsCmmX5K.leJ.','ROLE_SECURITY','s2@s2',NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('Customer','d64400c8-8054-4a77-9908-250c55036594','2025-06-20 20:51:54.840509','$2a$10$M.iveOTFmzOXqKuymo9qJuoCM6tBiLORTclYgXQD2LZYho42zUaXS','ROLE_USER','raph@ptdr.fr',NULL,'57890899-cb3b-4fdd-96c7-fb17ab6bde20','Raph','Aelle','0102050607',TRUE,'b25147bf-fbb5-44aa-8a06-49076ed2fad3'),
('Customer','f45be7f7-a93d-4328-b0eb-13f7795c5856','2025-06-20 20:38:53.947787','$2a$10$xxylsMEhyLeGVQPcK6gIZefsUXIXlrH31mAz6Z6vO.2Jv1ognCdrO','ROLE_USER','george@aol.fr',NULL,'3550c8fd-c2fb-42ea-81ca-7d97deafb0f2','George','Abitbol','0102050607',TRUE,'b1d9d623-e3ba-4b71-9aa4-fb5dde61569f');

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS tickets;
CREATE TABLE tickets (
  id UUID DEFAULT RANDOM_UUID() NOT NULL,
  how_many_tickets INTEGER DEFAULT NULL,
  qr_code_url VARCHAR(255) DEFAULT NULL,
  selling_key UUID DEFAULT NULL,
  ticket_created_date TIMESTAMP(6) DEFAULT NULL,
  ticket_is_payed BOOLEAN DEFAULT NULL,
  ticket_is_used BOOLEAN DEFAULT NULL,
  ticket_url VARCHAR(255) DEFAULT NULL,
  ticket_validation_date TIMESTAMP(6) DEFAULT NULL,
  customer_id VARCHAR(255) DEFAULT NULL,
  event_id VARCHAR(255) DEFAULT NULL,
  security_id VARCHAR(255) DEFAULT NULL,
  session_created_date TIMESTAMP(6) DEFAULT NULL,
  session_id VARCHAR(255) DEFAULT NULL,
  session_client_secret VARCHAR(255) DEFAULT NULL
);

INSERT INTO tickets VALUES
('317419e5-beb9-4e0e-b471-0bd551865034',1,'','2d7d3bdd-3fa1-45cb-9d6f-e3be15a3feba','2025-06-20 20:52:29.307152',TRUE,FALSE,'tickets/pdf/1750445549179_ticket.pdf',NULL,'d64400c8-8054-4a77-9908-250c55036594','67207e92-dc13-4a29-8f41-d96c3e191b98',NULL,'2025-06-20 20:52:11.121479','cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W','cs_test_a1l1EyMn84anrxdhKMvlZxDC4okfWDtZAgVzoQRYiKcS9J5hYMSz2MIG3W_secret_fidwbEhqYWAnPydmcHZxamgneCUl'),
('63b017c1-01a3-44a5-a2df-0a9e95ad478a',4,'src/main/resources/static/tickets/qrcodes/1750444510777_qr_code.png','87be60d7-aef5-410c-8237-bc7ae890b31e','2025-06-20 20:35:11.015415',TRUE,FALSE,'tickets/pdf/1750444510875_ticket.pdf',NULL,'67c3557c-174d-4017-b8f6-d9ca5e6aaf71','67207e92-dc13-4a29-8f41-d96c3e191b98',NULL,'2025-06-20 19:46:50.371698','cs_test_a1i2jm81KLBo5P4GDGVhX9LiRbe7n2wdBcOqXZkl6Fz6zkGuzfWvmJ8Otp','cs_test_a1i2jm81KLBo5P4GDGVhX9LiRbe7n2wdBcOqXZkl6Fz6zkGuzfWvmJ8Otp_secret_fidwbEhqYWAnPydmcHZxamgneCUl'),
('7d5303f1-99c8-4eae-8376-4d741ba268eb',4,'','75f75661-5cc8-44c0-aab6-ae45aa67697e','2025-06-20 20:47:33.472499',TRUE,FALSE,'tickets/pdf/1750445253345_ticket.pdf',NULL,'9a65095a-3edb-451c-90cf-20b5afac8b6f','67207e92-dc13-4a29-8f41-d96c3e191b98',NULL,'2025-06-20 20:47:03.791235','cs_test_a14KffJkWwoj4ifSUaWvB3bWTOBFhto6v4GC3iWJF7v3azQHA9PbRzXUI7','cs_test_a14KffJkWwoj4ifSUaWvB3bWTOBFhto6v4GC3iWJF7v3azQHA9PbRzXUI7_secret_fidwbEhqYWAnPydmcHZxamgneCUl'),
('80947444-62df-4ac4-aae9-97dcdc27a812',2,'src/main/resources/static/tickets/qrcodes/1750431105935_qr_code.png','ed316402-0603-4eeb-b43f-15c6df756b32','2025-06-20 16:51:45.949996',TRUE,TRUE,'tickets/pdf/1750431105946_ticket.pdf','2025-06-20 16:53:03.843176','43729766-67b3-47d2-80f7-6ab87e0dd0b1','67207e92-dc13-4a29-8f41-d96c3e191b98','8f2ff1cd-8075-43a3-b504-a3fbfcb6b9e4',NULL,NULL,NULL),
('b8aa6406-b61c-4d16-9671-5db6943c9172',1,'src/main/resources/static/tickets/qrcodes/1750444754037_qr_code.png','1d2ddbd2-dbae-4a90-9da5-cd0b90dfb163','2025-06-20 20:39:14.049179',TRUE,FALSE,'tickets/pdf/1750444754046_ticket.pdf',NULL,'f45be7f7-a93d-4328-b0eb-13f7795c5856','67207e92-dc13-4a29-8f41-d96c3e191b98',NULL,'2025-06-20 20:39:06.488520','cs_test_a1TkX9C0UN2oXhSsMSV5HrCF9NC7KPRybP0MFFCCdJ4c4MOje9Wp3sCmKt','cs_test_a1TkX9C0UN2oXhSsMSV5HrCF9NC7KPRybP0MFFCCdJ4c4MOje9Wp3sCmKt_secret_fidwbEhqYWAnPydmcHZxamgneCUl'),
('f7669ca6-539f-4725-8bde-f933da360635',2,NULL,NULL,NULL,FALSE,FALSE,NULL,NULL,'7cea4f86-f0f4-4a6c-9504-156c4f7ece5f','67207e92-dc13-4a29-8f41-d96c3e191b98',NULL,'2025-06-20 19:22:56.636728','cs_test_a1WjasV0m1wMPKBZk6bkyPH4urxpf6IgM2TcIKuFBmOaxMulGkP7Mp5VjH',NULL);

--
-- Table structure for table `verification_photo`
--

DROP TABLE IF EXISTS verification_photo;
CREATE TABLE verification_photo (
  id UUID DEFAULT RANDOM_UUID() NOT NULL,
  url VARCHAR(255) DEFAULT NULL,
  verification_date TIMESTAMP(6) DEFAULT NULL,
  moderator_id VARCHAR(255) DEFAULT NULL
);

INSERT INTO verification_photo VALUES
('2191cfde-50eb-4d30-8eff-b978f07ebc81',NULL,'2025-06-20 19:20:24.714375','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd'),
('56245662-63e3-4c82-95e1-8e934c70ffdd',NULL,'2025-06-20 20:46:56.653694','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd'),
('b1d9d623-e3ba-4b71-9aa4-fb5dde61569f',NULL,'2025-06-20 20:39:00.058063','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd'),
('b25147bf-fbb5-44aa-8a06-49076ed2fad3',NULL,'2025-06-20 20:52:00.072999','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd'),
('dff0e290-ba63-436e-adf9-681f2fb84ec2',NULL,'2025-06-20 19:46:44.098304','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd'),
('e78432f1-34b1-4da8-aa53-92e0850e4f2c',NULL,'2025-06-20 16:51:29.534181','6d3b4384-5adf-442a-b18d-aeeb3dedcfcd');

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS event;
CREATE TABLE event (
  id UUID DEFAULT RANDOM_UUID() NOT NULL,
  amount INTEGER NOT NULL,
  date TIMESTAMP(6) DEFAULT NULL,
  description LONGTEXT,
  duo_price DOUBLE NOT NULL,
  family_price DOUBLE NOT NULL,
  image VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  solo_price DOUBLE NOT NULL,
  ticket_left INTEGER DEFAULT NULL
);

INSERT INTO event VALUES
('67207e92-dc13-4a29-8f41-d96c3e191b98',44260,'2024-08-09 18:00:00.000000','Après un parcours impressionnant la France de Thierry Henry emmenée par Lacazette ainsi que l''Espagne se retrouvent en finale pour se disputer l''or pendant un match qui restera sans aucun doute dans les mémoires.',90,160,'/uploads/event/initial.jpeg','Finale football masculin France - Espagne',50,44246),
('134c0aa8-0c22-4c94-8edf-f81c333db574',17000,'2024-07-28 20:30:00.000000','Serez-vous présent pour voir concourir la coqueluche de ces jeux olympiques, Léon Marchand, déjà un géant malgré son jeune âge.',60,110,'/uploads/event/natation.jpeg','Finale 400 mètres 4 nages masculin',35,17000);