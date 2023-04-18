-- !Ups

ALTER TABLE `players` ADD `date_of_birth` DATE NULL AFTER `country_id`;
ALTER TABLE `cric`.`players` DROP INDEX `uk_p_name_country`, ADD UNIQUE `uk_p_name_country_dob` (`country_id`, `name`, `date_of_birth`) USING BTREE;

-- !Downs