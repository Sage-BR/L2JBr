DROP TABLE IF EXISTS `account_premium`;
CREATE TABLE `account_premium` (
  `account_name` varchar(45) NOT NULL DEFAULT '',
  `enddate` decimal(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;