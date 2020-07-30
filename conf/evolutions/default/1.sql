CREATE TABLE `batting_scores` (
  `id`                            int unsigned AUTO_INCREMENT NOT NULL,
  `match_id`                      int unsigned NOT NULL,
  `player_id`                     int unsigned NOT NULL,
  `team_id`                       int unsigned NOT NULL,
  `runs`                          int unsigned NOT NULL DEFAULT '0',
  `balls`                         int unsigned NOT NULL DEFAULT '0',
  `fours`                         int unsigned NOT NULL DEFAULT '0',
  `sixes`                         int unsigned NOT NULL DEFAULT '0',
  `mode_of_dismissal`             int unsigned DEFAULT NULL,
  `bowler_id`                     int unsigned DEFAULT NULL,
  `innings_id`                    int unsigned NOT NULL,
  `team_innings_id`               int unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mode_of_dismissal` (`mode_of_dismissal`),
  KEY `match` (`match_id`),
  KEY `team` (`team_id`),
  KEY `bowler` (`bowler_id`),
  KEY `player` (`player_id`),
  UNIQUE KEY `uk_bs_match_player_team_innings` (`match_id`, `player_id`, `team_id`, `innings_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `bowler_dismissals` (
   `id`                            int unsigned AUTO_INCREMENT NOT NULL,
   `player_id`                     int unsigned NOT NULL,
   `team_id`                       int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    KEY `bowler` (`player_id`),
    KEY `team` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `bowling_figures` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `match_id`                      int unsigned NOT NULL,
    `player_id`                     int unsigned NOT NULL,
    `team_id`                       int unsigned NOT NULL,
    `balls`                         int unsigned DEFAULT '0',
    `maidens`                       int unsigned DEFAULT '0',
    `runs`                          int unsigned DEFAULT '0',
    `wickets`                       int unsigned DEFAULT '0',
    `innings_id`                    int unsigned NOT NULL,
    `team_innings_id`               int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    KEY `player` (`player_id`),
    KEY `team` (`team_id`),
    KEY `match` (`match_id`),
    UNIQUE KEY `uk_bf_match_player_team_innings` (`match_id`, `player_id`, `team_id`, `innings_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `countries` (
   `id`                          int unsigned AUTO_INCREMENT NOT NULL,
   `name`                        varchar(100) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_cc_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `dismissal_modes` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `name`                          varchar(30),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dm_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `extras` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `match_id`                      int unsigned NOT NULL,
    `type`                          int unsigned NOT NULL,
    `runs`                          int unsigned NOT NULL,
    `batting_team`                  int unsigned NOT NULL,
    `bowling_team`                  int unsigned NOT NULL,
    `innings_id`                    int unsigned not null,
    `team_innings_id`               int unsigned not null,
    PRIMARY KEY (`id`),
    KEY `batting_team` (`batting_team`),
    KEY `bowling_team` (`bowling_team`),
    KEY `match` (`match_id`),
    UNIQUE KEY `uk_e_match_type_batting_innings` (`match_id`, `type`, `batting_team`, `innings_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table fielder_dismissals (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `score_id`                      int unsigned NOT NULL,
    `player_id`                     int unsigned NOT NULL,
    `team_id`                       int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    KEY `score` (`score_id`),
    KEY `player` (`player_id`),
    KEY `team` (`team_id`),
    UNIQUE KEY `uk_fd_score_player_team` (`score_id`, `player_id`, `team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `man_of_the_match` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `match_id`                      int unsigned NOT NULL,
    `player_id`                     int unsigned NOT NULL,
    `team_id`                       int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `player_match` (`match_id`, `player_id`, `team_id`),
    KEY `match` (`match_id`),
    KEY `player` (`player_id`),
    KEY `team` (`team_id`),
    UNIQUE KEY `uk_motm_match_player_team` (`match_id`, `player_id`, `team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `man_of_the_series` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `series_id`                     int unsigned NOT NULL,
    `player_id`                     int unsigned NOT NULL,
    `team_id`                       int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `player_series` (`series_id`, `player_id`, `team_id`),
    KEY `series` (`series_id`),
    KEY `player` (`player_id`),
    KEY `team` (`team_id`),
    UNIQUE KEY `uk_mots_match_player_series` (`series_id`, `player_id`, `team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `matches` (
     `id`                            int unsigned AUTO_INCREMENT NOT NULL,
     `series`                        int unsigned NOT NULL,
     `team_1`                        int unsigned NOT NULL,
     `team_2`                        int unsigned NOT NULL,
     `toss_winner`                   int unsigned DEFAULT NULL,
     `bat_first`                     int unsigned DEFAULT NULL,
     `result`                        int unsigned NOT NULL,
     `winner`                        int unsigned DEFAULT NULL,
     `win_margin`                    int unsigned DEFAULT NULL,
     `win_margin_type`               int unsigned DEFAULT NULL,
     `stadium`                       int unsigned NOT NULL,
     `start_time`                    timestamp NOT NULL,
     `tag`                           varchar(20),
     PRIMARY KEY (`id`),
     KEY `series` (`series`),
     KEY `team_1` (`team_1`),
     KEY `team_2` (`team_2`),
     KEY `toss_winner` (`toss_winner`),
     KEY `bat_first` (`bat_first`),
     KEY `winner` (`winner`),
     KEY `stadium` (`stadium`),
     UNIQUE KEY `uk_m_stadium_start` (`stadium`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `match_player_map` (
      `id`                            int unsigned AUTO_INCREMENT NOT NULL,
      `match_id`                      int unsigned NOT NULL,
      `player_id`                     int unsigned NOT NULL,
      `team_id`                       int unsigned NOT NULL,
      PRIMARY KEY (`id`),
      KEY `match` (`match_id`),
      KEY `team` (`team_id`),
      KEY `player` (`player_id`),
      UNIQUE KEY `uk_mpm_match_player_team` (`match_id`, `player_id`, `team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `players` (
     `id`                            int unsigned AUTO_INCREMENT NOT NULL,
     `name`                          varchar(50) NOT NULL,
     `country_id`                    int unsigned NOT NULL,
     `image`                         varchar(255) NOT NULL,
     PRIMARY KEY (`id`),
     KEY `country` (`country_id`),
     UNIQUE KEY `uk_p_name_country` (`country_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `series` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `name`                          varchar(50) NOT NULL,
    `home_country_id`               int unsigned NOT NULL,
    `tour_id`                       int unsigned NOT NULL,
    `type`                          int unsigned NOT NULL,
    `game_type`                     int unsigned NOT NULL,
    `start_time`                    timestamp NOT NULL,
    PRIMARY KEY (`id`),
    KEY `home_country` (`home_country_id`),
    KEY `tour` (`tour_id`),
    UNIQUE KEY `uk_s_name_tour_game_type` (`name`, `tour_id`, `game_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `series_teams_map` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `series_id`                     int unsigned NOT NULL,
    `team_id`                       int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk` (`series_id`,`team_id`),
    KEY `series` (`series_id`),
    KEY `teams` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `stadiums` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `name`                          varchar(200) NOT NULL,
    `city`                          varchar(100) NOT NULL,
    `state`                         varchar(100) DEFAULT NULL,
    `country_id`                    int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    KEY `country` (`country_id`),
    UNIQUE KEY `uk_s_name_country` (`name`, `country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `teams` (
    `id`                            int unsigned AUTO_INCREMENT NOT NULL,
    `name`                          varchar(100) NOT NULL,
    `country_id`                    int unsigned NOT NULL,
    `team_type_id`                  int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    KEY `country` (`country_id`),
    UNIQUE KEY `uk_t_name_country_type` (`name`, `country_id`, `team_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tours` (
   `id`                            int unsigned AUTO_INCREMENT NOT NULL,
   `name`                          varchar(100) NOT NULL,
   `start_time`                    timestamp NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_t_name` (`name`)
);

ALTER TABLE batting_scores ADD CONSTRAINT fk_batting_scores_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE batting_scores ADD CONSTRAINT fk_batting_scores_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE batting_scores ADD CONSTRAINT fk_batting_scores_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE batting_scores ADD CONSTRAINT fk_batting_scores_dismissal_mode FOREIGN KEY (`mode_of_dismissal`) REFERENCES `dismissal_modes` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE batting_scores ADD CONSTRAINT fk_batting_scores_bowler_id FOREIGN KEY (`bowler_id`) REFERENCES `bowler_dismissals` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE bowler_dismissals ADD CONSTRAINT fk_bowler_dismissals_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE bowler_dismissals ADD CONSTRAINT fk_bowler_dismissals_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE bowling_figures ADD CONSTRAINT fk_bowling_figures_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE bowling_figures ADD CONSTRAINT fk_bowling_figures_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE bowling_figures ADD CONSTRAINT fk_bowling_figures_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE extras ADD CONSTRAINT fk_extras_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE extras ADD CONSTRAINT fk_extras_batting_team FOREIGN KEY (`batting_team`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE extras ADD CONSTRAINT fk_extras_bowling_team FOREIGN KEY (`bowling_team`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE fielder_dismissals ADD CONSTRAINT fk_fielder_dismissals_score_id FOREIGN KEY (`score_id`) REFERENCES `batting_scores` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE fielder_dismissals ADD CONSTRAINT fk_fielder_dismissals_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE fielder_dismissals ADD CONSTRAINT fk_fielder_dismissals_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE man_of_the_match ADD CONSTRAINT fk_man_of_the_match_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE man_of_the_match ADD CONSTRAINT fk_man_of_the_match_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE man_of_the_match ADD CONSTRAINT fk_man_of_the_match_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE man_of_the_series ADD CONSTRAINT fk_man_of_the_series_series_id FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE man_of_the_series ADD CONSTRAINT fk_man_of_the_series_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE man_of_the_series ADD CONSTRAINT fk_man_of_the_series_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE matches ADD CONSTRAINT fk_matches_series_id FOREIGN KEY (`series`) REFERENCES `series` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_team_1 FOREIGN KEY (`team_1`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_team_2 FOREIGN KEY (`team_2`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_toss_winner FOREIGN KEY (`toss_winner`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_bat_first FOREIGN KEY (`bat_first`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_winner FOREIGN KEY (`winner`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE matches ADD CONSTRAINT fk_matches_stadium FOREIGN KEY (`stadium`) REFERENCES `stadiums` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE match_player_map ADD CONSTRAINT fk_match_player_map_match_id FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE match_player_map ADD CONSTRAINT fk_match_player_map_player_id FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE match_player_map ADD CONSTRAINT fk_match_player_map_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE players ADD CONSTRAINT fk_players_country_id FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE series ADD CONSTRAINT fk_series_tour_id FOREIGN KEY (`tour_id`) REFERENCES `tours` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE series ADD CONSTRAINT fk_series_home_country_id FOREIGN KEY (`home_country_id`) REFERENCES `countries` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE series_teams_map ADD CONSTRAINT fk_series_teams_series_id FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE series_teams_map ADD CONSTRAINT fk_series_teams_team_id FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE stadiums ADD CONSTRAINT fk_stadiums_country_id FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE teams ADD CONSTRAINT fk_teams_country_id FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;