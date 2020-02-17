DROP TABLE IF EXISTS `auction_bid`;
CREATE TABLE IF NOT EXISTS `auction_bid` (
  `id` INT NOT NULL DEFAULT 0,
  `auctionId` INT NOT NULL DEFAULT 0,
  `bidderId` INT NOT NULL DEFAULT 0,
  `bidderName` varchar(50) NOT NULL,
  `clan_name` varchar(50) NOT NULL,
  `maxBid` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `time_bid` bigint(13) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY  (`auctionId`,`bidderId`),
  KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;