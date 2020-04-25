package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ResultType;
import enums.WinMarginType;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "matches")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match extends Model
{
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "series")
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

    @Column(name = "end_time")
    private Date endTime;

    private String tag;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    private List<BattingScore> battingScores;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    private List<BowlingFigure> bowlingFigures;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "match_id")
    private List<ManOfTheMatch> manOfTheMatchList;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
