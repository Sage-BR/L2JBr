DROP TABLE IF EXISTS `residence_functions`;
CREATE TABLE IF NOT EXISTS `residence_functions` (
  `id`  int NOT NULL ,
  `level`  int NOT NULL ,
  `expiration`  bigint NOT NULL ,
  `residenceId`  int NOT NULL ,
  PRIMARY KEY (`id`, `level`, `residenceId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;