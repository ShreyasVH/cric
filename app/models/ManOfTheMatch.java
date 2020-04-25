package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "man_of_the_match")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManOfTheMatch extends Model
{
    @Id
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Match match;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;
}
