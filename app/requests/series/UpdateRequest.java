package requests.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.GameType;
import enums.SeriesType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private Long homeCountryId;
    private SeriesType type;
    private GameType gameType;
    private String startTime;
    private String endTime;
    private List<Long> teams = new ArrayList<>();

    public void validate()
    {

    }
}
