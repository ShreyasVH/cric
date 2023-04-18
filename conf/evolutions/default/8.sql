-- !Ups

ALTER TABLE `matches` ADD `is_official` BOOLEAN NOT NULL DEFAULT TRUE AFTER `tag`;

-- !Downs