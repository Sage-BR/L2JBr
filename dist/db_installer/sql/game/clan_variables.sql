DROP TABLE IF EXISTS `clan_variables`;
CREATE TABLE IF NOT EXISTS `clan_variables` (
  `clanId` int(10) UNSIGNED NOT NULL,
  `var` varchar(255) NOT NULL,
  `val` text NOT NULL,
  KEY `clanId` (`clanId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;