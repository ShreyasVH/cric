package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.players.CreateRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "players")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Long countryId;

    @Column
    private Long dateOfBirth;

    @Column
    private String image;

    public Player(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.countryId = createRequest.getCountryId();
        this.dateOfBirth = createRequest.getDateOfBirth();
        this.image = createRequest.getImage();
    }
}
