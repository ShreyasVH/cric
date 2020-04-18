package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.TeamType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
}
