package responses;

import enums.GameType;
import enums.SeriesType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Country;
import models.Match;
import models.Series;
import models.Team;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SeriesResponse
{
    private Long id;
    private String name;
    private Country homeCountry;
    private Long tourId;
    //TODO: see if tourname is required
//    private String tourName;
    private SeriesType type;
    private GameType gameType;
    private Long startTime;
    private List<Team> teams = new ArrayList<>();
    private List<ManOfTheSeriesResponse> manOfTheSeriesList = new ArrayList<>();
    List<Match> matches = new ArrayList<>();

    public SeriesResponse(Series series)
    {
        this.id = series.getId();
        this.name = series.getName();
        this.tourId = series.getTourId();
        this.type = series.getType();
        this.gameType = series.getGameType();
        this.startTime = series.getStartTime();
    }
}
