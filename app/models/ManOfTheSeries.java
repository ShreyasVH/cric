package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "man_of_the_series")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOfTheSeries extends Model
{
    @Id
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Series series;

    @OneToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;
}
