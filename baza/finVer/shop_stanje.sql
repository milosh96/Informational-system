-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: shop
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `stanje`
--

DROP TABLE IF EXISTS `stanje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stanje` (
  `idStanja` int(11) NOT NULL AUTO_INCREMENT,
  `idArtikal` int(11) NOT NULL,
  `idProdavnica` int(11) NOT NULL,
  `kolicina` int(11) NOT NULL,
  PRIMARY KEY (`idStanja`),
  KEY `idProdavnica_idx` (`idProdavnica`),
  KEY `idArtikalSt_idx` (`idArtikal`),
  CONSTRAINT `idArtikalSt` FOREIGN KEY (`idArtikal`) REFERENCES `artikal` (`idArtikal`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `idProdavnicaSt` FOREIGN KEY (`idProdavnica`) REFERENCES `prodavnica` (`idProdavnica`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stanje`
--

LOCK TABLES `stanje` WRITE;
/*!40000 ALTER TABLE `stanje` DISABLE KEYS */;
INSERT INTO `stanje` VALUES (1,1,1,10),(2,1,2,5),(3,1,3,25),(4,2,1,5),(5,2,4,9),(6,2,5,10),(7,3,1,8),(8,3,2,2),(9,3,3,1),(10,3,4,17),(11,4,1,1),(12,4,2,4),(13,4,3,10),(14,4,4,8),(15,4,5,8),(16,5,1,14),(17,5,3,5),(18,6,2,0),(19,7,1,19),(20,7,3,8),(21,7,4,5),(22,9,2,2),(23,9,5,4),(24,10,4,20),(25,11,2,22),(26,12,1,3),(27,12,4,18),(28,13,1,31),(29,14,5,2),(30,15,2,6),(31,15,3,10),(32,16,1,4),(33,16,4,4),(34,16,5,2),(35,6,1,7);
/*!40000 ALTER TABLE `stanje` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-01-27  5:26:02
