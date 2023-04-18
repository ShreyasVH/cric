-- !Ups

CREATE TABLE `captains` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `match_id` INT UNSIGNED NOT NULL,
    `player_id` INT UNSIGNED NOT NULL,
    `team_id` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

ALTER TABLE `cric`.`captains` ADD UNIQUE `uk_captains` (`match_id`, `player_id`, `team_id`);

ALTER TABLE captains ADD CONSTRAINT fk_captains_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE captains ADD CONSTRAINT fk_captains_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE captains ADD CONSTRAINT fk_captains_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

CREATE TABLE `wicket_keepers` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `match_id` INT UNSIGNED NOT NULL,
    `player_id` INT UNSIGNED NOT NULL,
    `team_id` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

ALTER TABLE `cric`.`wicket_keepers` ADD UNIQUE `uk_wicketkeepers` (`match_id`, `player_id`, `team_id`);

ALTER TABLE wicket_keepers ADD CONSTRAINT fk_wicket_keepers_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE wicket_keepers ADD CONSTRAINT fk_wicket_keepers_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE wicket_keepers ADD CONSTRAINT fk_wicket_keepers_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

-- !Downs