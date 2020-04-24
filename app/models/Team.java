package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.TeamType;
import lombok.Getter;
import lombok.Setter;
import requests.teams.CreateRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Series> series = new ArrayList<>();

    public Team(CreateRequest createRequest)
    {
        this.name = createRequest.getName();
        this.teamType = createRequest.getTeamType();
    }
}
