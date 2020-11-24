package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.TeamType;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;
import requests.teams.CreateRequest;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@Table(name = "teams")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Long countryId;

    @Column(name = "team_type_id")
    private TeamType teamType;

    public Team(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.teamType = createRequest.getTeamType();
        this.countryId = createRequest.getCountryId();
    }
}
