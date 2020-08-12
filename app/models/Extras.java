package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ExtrasType;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "extras")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Extras extends Model
{
    @Id
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Match match;

    private ExtrasType type;

    private int runs;

    @OneToOne
    @JoinColumn(name = "batting_team")
    private Team battingTeam;

    @OneToOne
    @JoinColumn(name = "bowling_team")
    private Team bowlingTeam;

    @Column(name = "innings_id")
    private int innings;

    @Column(name = "team_innings_id")
    private int teamInnings;
}
