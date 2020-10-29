ALTER TABLE `tours` CHANGE `start_time` `start_time` BIGINT NOT NULL;

ALTER TABLE `series` CHANGE `start_time` `start_time` BIGINT NOT NULL;

ALTER TABLE `matches` CHANGE `start_time` `start_time` BIGINT NOT NULL;

ALTER TABLE `cric`.`tours` DROP INDEX `uk_t_name`, ADD UNIQUE `uk_t_name` (`name`, `start_time`) USING BTREE;