-- ==========================================================
--  HEALTHLINK_CLINIC – FULL DATABASE SETUP (READY TO RUN)
-- ==========================================================

-- Create the database if not exists
CREATE DATABASE IF NOT EXISTS healthlink_clinic;
USE healthlink_clinic;

-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
-- ------------------------------------------------------
-- Host: localhost    Database: healthlink_clinic
-- Server version    8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
 /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
 /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 /*!50503 SET NAMES utf8mb4 */;
 /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
 /*!40103 SET TIME_ZONE='+00:00' */;
 /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
 /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
 /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
 /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ==========================================================
-- TABLE: appointments
-- ==========================================================

DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `appointment_date` date NOT NULL,
                                `appointment_time` time(6) NOT NULL,
                                `notes` varchar(255) DEFAULT NULL,
                                `status` enum('CANCELLED','COMPLETED','NO_SHOW','SCHEDULED') DEFAULT NULL,
                                `doctor_id` bigint NOT NULL,
                                `patient_id` bigint NOT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKmujeo4tymoo98cmf7uj3vsv76` (`doctor_id`),
                                KEY `FK8exap5wmg8kmb1g1rx3by21yt` (`patient_id`),
                                CONSTRAINT `FK8exap5wmg8kmb1g1rx3by21yt` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
                                CONSTRAINT `FKmujeo4tymoo98cmf7uj3vsv76` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `appointments` WRITE;
INSERT INTO `appointments` VALUES
                               (10,'2025-11-09','11:59:00.000000','skin','SCHEDULED',3,2),
                               (14,'2025-11-24','22:53:00.000000','er','SCHEDULED',1,1),
                               (16,'2025-11-09','23:50:00.000000','','SCHEDULED',3,1),
                               (17,'2025-11-21','19:40:00.000000','eye','SCHEDULED',1,1),
                               (18,'2025-11-11','20:00:00.000000','heart','SCHEDULED',2,2),
                               (19,'2025-11-12','17:19:00.000000','eye','NO_SHOW',1,2),
                               (20,'2025-11-20','14:19:00.000000','','SCHEDULED',1,2),
                               (21,'2025-11-16','21:20:00.000000','eye','SCHEDULED',1,2),
                               (22,'2025-11-17','14:15:00.000000','eye pain','SCHEDULED',1,3);
UNLOCK TABLES;

-- ==========================================================
-- TABLE: doctors
-- ==========================================================

DROP TABLE IF EXISTS `doctors`;
CREATE TABLE `doctors` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `active` bit(1) DEFAULT NULL,
                           `email` varchar(255) DEFAULT NULL,
                           `first_name` varchar(255) NOT NULL,
                           `last_name` varchar(255) NOT NULL,
                           `phone` varchar(255) DEFAULT NULL,
                           `specialization` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `doctors` WRITE;
INSERT INTO `doctors` VALUES
                          (1,_binary '','suegiven@live.com','Akosua','Otu','0549215916','Optician'),
                          (2,_binary '','kossymintz@gmail.com','Yommie','Solomon','0243416513','CARDIOLOGY'),
                          (3,_binary '','amesropaula@gmail.com','Yali','Wang','0549215916','DERMATOLOGY');
UNLOCK TABLES;

-- ==========================================================
-- TABLE: patients
-- ==========================================================

DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `address` varchar(255) DEFAULT NULL,
                            `created_at` datetime(6) DEFAULT NULL,
                            `date_of_birth` date DEFAULT NULL,
                            `email` varchar(255) NOT NULL,
                            `first_name` varchar(255) NOT NULL,
                            `last_name` varchar(255) NOT NULL,
                            `phone` varchar(255) NOT NULL,
                            `updated_at` datetime(6) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `UKa370hmxgv0l5c9panryr1ji7d` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `patients` WRITE;
INSERT INTO `patients` VALUES
                           (1,'GZ-183-7790','2025-11-06 16:44:31.478723','2015-09-12','suegiven@live.com','Akosua','Otu','0549215916','2025-11-06 16:44:31.478723'),
                           (2,'5 AVIATION HIGHWAY, SPINTEX ROAD','2025-11-09 20:24:32.291541','2025-11-13','amesropaula@gmail.com','Paula Adwoa','Amesro','0549215915','2025-11-09 20:24:32.291541'),
                           (3,'GZ-183-7790','2025-11-17 14:13:24.815592','2025-11-04','lala@yahoo.com','Joe','Mark','0248521452','2025-11-17 14:13:24.815592'),
                           (4,'Red Deer, Alberta','2025-11-20 00:19:49.993938','1998-05-12','johndoe@gmail.com','John','Doe','7801112233','2025-11-20 00:19:49.993938'),
                           (5,'GZ-183-7752','2025-11-20 22:23:18.078195','2021-11-04','lalu@yahoo.com','Linda','Markus','0248521478','2025-11-20 22:23:18.078195');
UNLOCK TABLES;

-- ==========================================================
-- TABLE: user_preferences
-- ==========================================================

DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `compact_mode` bit(1) DEFAULT NULL,
                                    `email_notifications` bit(1) DEFAULT NULL,
                                    `font_size` int DEFAULT NULL,
                                    `high_contrast` bit(1) DEFAULT NULL,
                                    `reduced_motion` bit(1) DEFAULT NULL,
                                    `sidebar_collapsed` bit(1) DEFAULT NULL,
                                    `sms_notifications` bit(1) DEFAULT NULL,
                                    `theme` varchar(255) NOT NULL,
                                    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user_preferences` WRITE;
INSERT INTO `user_preferences` VALUES
                                   (1,_binary '\0',NULL,20,_binary '',_binary '',NULL,NULL,'dark','2025-11-10 00:41:46','2025-11-17 21:20:48'),
                                   (2,_binary '\0',NULL,16,_binary '\0',_binary '\0',NULL,NULL,'auto','2025-11-10 02:36:00','2025-11-21 05:57:29'),
                                   (3,NULL,NULL,14,NULL,NULL,NULL,NULL,'light','2025-11-11 06:10:01','2025-11-21 05:41:51');
UNLOCK TABLES;

-- ==========================================================
-- TABLE: users
-- ==========================================================

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_at` datetime(6) DEFAULT NULL,
                         `email` varchar(255) NOT NULL,
                         `is_active` bit(1) DEFAULT NULL,
                         `password` varchar(255) NOT NULL,
                         `role` enum('ADMIN','DOCTOR','RECEPTIONIST') NOT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         `username` varchar(255) NOT NULL,
                         `preferences_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
                         UNIQUE KEY `UKp500u51iqqij1u0b4eqv13lwx` (`preferences_id`),
                         CONSTRAINT `fk_users_preferences_id` FOREIGN KEY (`preferences_id`) REFERENCES `user_preferences` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES
                        (1,'2025-11-08 17:52:11.000000','suegiven@yahoo.com',_binary '','$2a$10$dxPz.3V6sytEIOhIrWO7I.5gtTDolQyRmm2sc.QYJVsOd5vKCbA3S','ADMIN','2025-11-09 19:42:16.262464','admin',1),
                        (2,'2025-11-08 17:52:11.000000','reception@example.com',_binary '','$2a$10$4unZypoAR3FLsuG0.vFMuOwef2BMT9NS3dTrm5UAe9D0eICA7TfCy','RECEPTIONIST','2025-11-09 19:36:00.545633','reception',2),
                        (3,'2025-11-08 17:52:11.000000','doctor@example.com',_binary '','$2a$10$nzIBjzCka1tMgOAU.Z9S8Okq2rPBxgjei51tYyTMS/1Ovi1q.4ES6','DOCTOR','2025-11-10 23:10:01.874038','doctor',3);
UNLOCK TABLES;

-- ==========================================================
-- RESTORE SETTINGS
-- ==========================================================

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
 /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
 /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
 /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
 /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
 /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
 /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
 /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
