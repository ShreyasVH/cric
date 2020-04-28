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
@Table(name = "man_of_the_match")
//@Cache(enableQueryCache=true)
//@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOfTheMatch extends Model
{
    @Id
    private Long id;

//    @ManyToOne
//    @JsonBackReference
//    private Match match;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    @OneToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
}
