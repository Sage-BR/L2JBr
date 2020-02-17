DROP TABLE IF EXISTS `commission_items`;
CREATE TABLE IF NOT EXISTS `commission_items` (
	`commission_id` BIGINT NOT NULL AUTO_INCREMENT,
	`item_object_id` INT NOT NULL,
	`price_per_unit` BIGINT NOT NULL,
	`start_time` TIMESTAMP NOT NULL,
	`duration_in_days` TINYINT NOT NULL,
	PRIMARY KEY (`commission_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;