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
@Table(name = "fielder_dismissals")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FielderDismissal extends Model
{
    @Id
    private Long id;

    @ManyToOne
    @JsonBackReference
    private BattingScore score;

    @OneToOne
    private Player player;

    @OneToOne
    private Team team;
}
