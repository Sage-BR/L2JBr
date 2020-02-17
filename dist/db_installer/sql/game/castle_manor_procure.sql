DROP TABLE IF EXISTS `castle_manor_procure`;
CREATE TABLE IF NOT EXISTS `castle_manor_procure` (
 `castle_id` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
 `crop_id` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `amount` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `start_amount` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `price` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `reward_type` TINYINT(1) UNSIGNED NOT NULL DEFAULT '0',
 `next_period` TINYINT(1) UNSIGNED NOT NULL DEFAULT '1',
  PRIMARY KEY (`castle_id`,`crop_id`,`next_period`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;