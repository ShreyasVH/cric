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
//@Cache(enableQueryCache=true)
//@CacheQueryTuning(maxSecsToLive = 3600)
@Table(name = "teams")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team
{
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne
    private Country country;

    @Column(name = "team_type_id", nullable = false)
    private TeamType teamType;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP", nullable = false)
    private Date updatedAt;

    public Team(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.teamType = createRequest.getTeamType();
    }
}
