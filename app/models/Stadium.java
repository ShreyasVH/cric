package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "stadiums")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stadium extends Model
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country_id", nullable = false, columnDefinition = "UNSIGNED int")
    private Long countryId;

    @ManyToOne
    private Country country;
}
