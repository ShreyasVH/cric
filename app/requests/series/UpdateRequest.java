package requests.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import enums.GameType;
import enums.SeriesType;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import models.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private Long homeCountryId;
    private SeriesType type;
    private GameType gameType;
    private Long startTime;
    private Long tourId;
    private List<Long> teams;
    private List<Map<String, Long>> manOfTheSeriesList;

    public void validate(Series existingSeries)
    {
        SeriesType seriesType = existingSeries.getType();
        if(null != this.type)
        {
            seriesType = this.type;
        }

//        int teamSize = existingSeries.getTeams().size();
//        if(null != this.getTeams())
//        {
//            teamSize = this.teams.size();
//            if(this.teams.size() < 2)
//            {
//                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
//            }
//        }
//
//        if((SeriesType.BI_LATERAL == seriesType) && (teamSize != 2))
//        {
//            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
//        }
//        else if((SeriesType.TRI_SERIES == seriesType) && (teamSize != 3))
//        {
//            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
//        }
    }
}
