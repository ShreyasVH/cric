package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ResultType;
import enums.WinMarginType;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "matches")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match extends Model
{
    @Id
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "series", referencedColumnName = "id")
    @JsonIgnoreProperties("matches")
    private Series series;

    @OneToOne
    @JoinColumn(name = "team_1")
    private Team team1;

    @OneToOne
    @JoinColumn(name = "team_2")
    private Team team2;

    @OneToOne
    @JoinColumn(name = "toss_winner")
    private Team tossWinner;

    @OneToOne
    @JoinColumn(name = "bat_first")
    private Team battingFirst;

    private ResultType result;

    @OneToOne
    @JoinColumn(name = "winner")
    private Team winner;

    @Column(name = "win_margin")
    private Integer winMargin;

    @Column(name = "win_margin_type")
    private WinMarginType winMarginType;

    @OneToOne
    @JoinColumn(name = "stadium")
    private Stadium stadium;

    @Column(name = "start_time")
    private Date startTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    @OrderBy("id asc")
    private List<MatchPlayerMap> players;

    private String tag;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    @OrderBy("id asc")
    private List<BattingScore> battingScores;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    @OrderBy("id asc")
    private List<BowlingFigure> bowlingFigures;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    private List<ManOfTheMatch> manOfTheMatchList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "match_id", referencedColumnName = "id")
    @OrderBy("id asc")
    private List<Extras> extras;
}
