package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tours")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tour
{
    @Id
    private Long id;

    private String name;

    @Column(name = "start_time")
    private Date startTime;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tour")
    private List<Series> seriesList;
}
