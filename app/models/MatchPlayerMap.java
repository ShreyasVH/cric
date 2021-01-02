package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "match_player_map")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchPlayerMap extends Model
{
    @Id
    private Long id;

    @Column
    private Long matchId;

    @Column
    private Long playerId;

    @Column
    private Long teamId;
}
