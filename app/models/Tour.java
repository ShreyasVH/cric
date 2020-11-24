package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tours")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tour
{
    @Id
    private Long id;

    private String name;

    @Column(name = "start_time")
    private Long startTime;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tour")
    private List<Series> seriesList;
}
