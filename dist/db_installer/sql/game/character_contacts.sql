DROP TABLE IF EXISTS `character_contacts`;
CREATE TABLE IF NOT EXISTS `character_contacts` (
  charId INT UNSIGNED NOT NULL DEFAULT 0,
  contactId INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`contactId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;