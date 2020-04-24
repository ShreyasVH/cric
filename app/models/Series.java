package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.GameType;
import enums.SeriesType;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "series")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series extends Model
{
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne
    private Country homeCountry;

    @ManyToOne
    @JoinColumn(name = "team_1")
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team_2")
    private Team team2;

    @Column(nullable = false)
    private SeriesType type;

    @Column(name = "match_type", nullable = false)
    private GameType matchType;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}
