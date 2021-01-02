package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "batting_scores")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BattingScore extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private Long matchId;

    @Column
    private Long playerId;

    @Column
    private Long teamId;

    @Column
    private int runs;

    @Column
    private int balls;

    @Column
    private int fours;

    @Column
    private int sixes;

    @Column(name = "mode_of_dismissal")
    private Integer dismissalMode;

    @Column(name = "bowler_id")
    private Long bowlerDismissalId;

    @Column(name = "innings_id")
    private int innings;

    @Column(name = "team_innings_id")
    private int teamInnings;
}
