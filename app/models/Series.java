package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.GameType;
import enums.SeriesType;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "series")
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Long homeCountryId;

    @Column
    private Long tourId;

    @Column
    private SeriesType type;

    @Column
    private GameType gameType;

    @Column
    private Long startTime;
}
