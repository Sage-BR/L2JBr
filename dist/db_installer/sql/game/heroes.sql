DROP TABLE IF EXISTS `heroes`;
CREATE TABLE IF NOT EXISTS `heroes` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `class_id` decimal(3,0) NOT NULL DEFAULT 0,
  `count` decimal(3,0) NOT NULL DEFAULT 0,
  `played` decimal(1,0) NOT NULL DEFAULT 0,
  `claimed` ENUM('true','false') NOT NULL DEFAULT 'false',
  `message` varchar(300) NOT NULL DEFAULT '',
  PRIMARY KEY (`charId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;