package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CacheQueryTuning;
import io.ebean.annotation.Cache;
import lombok.Getter;
import lombok.Setter;
import requests.stadiums.CreateRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Entity
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@Table(name = "stadiums")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stadium extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private Long countryId;

    public Stadium(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.city = createRequest.getCity();
        this.state = createRequest.getState();
        this.countryId = createRequest.getCountryId();
    }
}
