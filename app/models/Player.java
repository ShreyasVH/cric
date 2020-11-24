package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.players.CreateRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "players")
@NoArgsConstructor
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player extends Model
{
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne
    private Country country;

    @Column(name = "date_of_birth")
    private Long dateOfBirth;

    @Column(name = "image", nullable = false, length = 255)
    private String image;

    public Player(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.dateOfBirth = createRequest.getDateOfBirth();
        this.image = createRequest.getImage();
    }
}
