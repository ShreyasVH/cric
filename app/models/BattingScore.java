package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "batting_scores")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BattingScore extends Model
{
    @Id
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Match match;

//    @ManyToMany
//    @JoinTable(name = "player_team_map",
//            joinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id")
//    )
//    private Player batsman;

    private int runs;

    private int balls;

    private int fours;

    private int sixes;

    @OneToOne()
    @JoinColumn(name = "mode_of_dismissal")
    private DismissalMode dismissalMode;

    @Column(name = "innings_id")
    private int innings;

    @Column(name = "team_innings_id")
    private int teamInnings;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
