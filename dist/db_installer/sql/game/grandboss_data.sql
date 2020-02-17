DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE IF NOT EXISTS `grandboss_data` (
  `boss_id` smallint(5) unsigned NOT NULL,
  `loc_x` mediumint(6) NOT NULL,
  `loc_y` mediumint(6) NOT NULL,
  `loc_z` mediumint(6) NOT NULL,
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `currentHP` decimal(30,15) NOT NULL,
  `currentMP` decimal(30,15) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`boss_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

INSERT IGNORE INTO `grandboss_data` (`boss_id`,`loc_x`,`loc_y`,`loc_z`,`heading`,`currentHP`,`currentMP`) VALUES
(29001, -21610, 181594, -5734, 0, 40218408, 300), -- Queen Ant
(29006, 17726, 108915, -6480, 0, 622493.58388, 3793.536), -- Core
(29325, 43400, 16504, -4395, 0, 247117958, 177258), -- Orfen
(29020, 116033, 17447, 10107, -25348, 53342293, 18000), -- Baium
(29068, 185708, 114298, -8221,32768, 1240440638, 201454), -- Antharas
(29028, -105200, -253104, -15264, 0, 1240440638, 201454), -- Valakas
(29240, 0, 0, 0, 0, 498728877, 95237), -- Lindvior
(29197, 81208, -182095, -9895, 0, 549579599.71479, 22800), -- Trasken
(29118, 0, 0, 0, 0, 4109288, 1220547), -- Beleth
(29348, 185080, -12613, -5499, 16550, 556345880, 86847), -- Anakim
(29336, 185062, -9605, -5499, 15640, 486021997, 79600), -- Lilith
(26124, 0, 0, 0, 0, 20949082.10169, 83501.371), -- Kelbim
(29305, 0, 0, 0, 0, 589355368, 51696), -- Helios
(19740, 180712, 210664, -14823, 22146, 412872295, 124077); -- Fafurion
