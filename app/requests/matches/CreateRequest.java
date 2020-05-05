package requests.matches;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ResultType;
import enums.WinMarginType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRequest
{
    private Long seriesId;
    private Long team1;
    private Long team2;
    private Long tossWinner;
    private Long batFirst;
    private ResultType result;
    private Long winner;
    private int winMargin;
    private WinMarginType winMarginType;
    private Long stadium;
    private String startTime;
    private String tag;
    private List<Map<String, String>> players;
    private List<Map<String, String>> extras = new ArrayList<>();
    private List<Map<String, String>> battingScores = new ArrayList<>();
    private List<Map<String, String>> bowlingFigures = new ArrayList<>();
    private List<Long> manOfTheMatchList = new ArrayList<>();
    private List<Map<String, Long>> manOfTheSeriesList = new ArrayList<>();

    public void validate()
    {

    }
}
