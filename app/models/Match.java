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
    @Column
    private Long id;

    @Column
    private Long series;

    @Column(name = "team_1")
    private Long team1;

    @Column(name = "team_2")
    private Long team2;

    @Column
    private Long tossWinner;

    @Column
    private Long batFirst;

    @Column
    private ResultType result;

    @Column
    private Long winner;

    @Column
    private Integer winMargin;

    @Column
    private WinMarginType winMarginType;

    @Column
    private Long stadium;

    @Column
    private Long startTime;

    @Column
    private String tag;
}
