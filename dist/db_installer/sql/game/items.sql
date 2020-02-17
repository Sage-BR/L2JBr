DROP TABLE IF EXISTS `items`;
CREATE TABLE IF NOT EXISTS `items` (
  `owner_id` INT, -- object id of the player or clan,owner of this item
  `object_id` INT NOT NULL DEFAULT 0, -- object id of the item
  `item_id` INT,
  `count` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `enchant_level` INT,
  `loc` VARCHAR(10), -- inventory,paperdoll,npc,clan warehouse,pet,and so on
  `loc_data` INT,    -- depending on location: equiped slot,npc id,pet id,etc
  `time_of_use` INT, -- time of item use, for calculate of breackages
  `custom_type1` INT DEFAULT 0,
  `custom_type2` INT DEFAULT 0,
  `mana_left` decimal(5,0) NOT NULL DEFAULT -1,
  `time` decimal(13) NOT NULL DEFAULT 0,
  PRIMARY KEY (`object_id`),
  KEY `owner_id` (`owner_id`),
  KEY `item_id` (`item_id`),
  KEY `loc` (`loc`),
  KEY `time_of_use` (`time_of_use`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;