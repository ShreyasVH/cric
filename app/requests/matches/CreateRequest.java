package requests.matches;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.DismissalMode;
import enums.ResultType;
import enums.WinMarginType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import exceptions.BadRequestException;
import enums.ErrorCode;

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
    private Long startTime;
    private String tag;
    private List<Map<String, String>> players;
    private List<Map<String, String>> bench;
    private List<Map<String, String>> extras = new ArrayList<>();
    private List<Map<String, String>> battingScores = new ArrayList<>();
    private List<Map<String, String>> bowlingFigures = new ArrayList<>();
    private List<Long> manOfTheMatchList = new ArrayList<>();
    private List<Map<String, Long>> manOfTheSeriesList = new ArrayList<>();
    private List<Long> captains = new ArrayList<>();
    private List<Long> wicketKeepers = new ArrayList<>();
    private boolean isOfficial = true;

    public void validate()
    {
        if(null != battingScores)
        {
            for(Map<String, String> battingScore: battingScores)
            {
                if(!battingScore.containsKey("playerId") || (null == battingScore.get("playerId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid batsman");
                }

                if(battingScore.containsKey("bowlerId") && (StringUtils.isEmpty(battingScore.get("bowlerId"))))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bowler");
                }

                if(battingScore.containsKey("fielders"))
                {
                    String fieldersString = battingScore.get("fielders");
                    if(StringUtils.isEmpty(fieldersString))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid fielders");
                    }

                    String[] fielders = fieldersString.split(", ");
                    for(String fielder: fielders)
                    {
                        if(StringUtils.isEmpty(fielder))
                        {
                            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid fielder");
                        }
                    }
                }

                if(battingScore.containsKey("dismissalMode") && (null != battingScore.get("dismissalMode")))
                {
                    DismissalMode dismissalMode = DismissalMode.getById(Integer.parseInt(battingScore.get("dismissalMode")));
                    String bowler = "";
                    if(battingScore.containsKey("bowlerId"))
                    {
                        bowler = battingScore.get("bowlerId");
                    }

                    String fielders = "";
                    if(battingScore.containsKey("fielders"))
                    {
                        fielders = battingScore.get("fielders");
                    }

                    boolean isBowlerRequired = false;
                    boolean isFielderRequired = false;

                    switch(dismissalMode)
                    {
                        case BOWLED:
                        case LBW:
                        case HIT_WICKET:
                            isBowlerRequired = true;
                            break;
                        case CAUGHT:
                        case STUMPED:
                            isFielderRequired = true;
                            isBowlerRequired = true;
                            break;
                        case RUN_OUT:
                            isFielderRequired = true;
                    }

                    if(isBowlerRequired && StringUtils.isEmpty(bowler))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bowler for dismissal");
                    }

                    if(isFielderRequired && StringUtils.isEmpty(fielders))
                    {
                        throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid fielders for dismissal");
                    }
                }
            }
        }

        if(null != extras)
        {
            for(Map<String, String> extra: extras)
            {
                if(!extra.containsKey("battingTeam") || (null == extra.get("battingTeam")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid batting team in extras");
                }

                if(!extra.containsKey("bowlingTeam") || (null == extra.get("bowlingTeam")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bowling team in extras");
                }
            }
        }

        if(null != bowlingFigures)
        {
            for(Map<String, String> bowlingFigure: bowlingFigures)
            {
                if(!bowlingFigure.containsKey("playerId") || (null == bowlingFigure.get("playerId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bowler");
                }
            }
        }

        if(null != players)
        {
            for(Map<String, String> player: players)
            {
                if(!player.containsKey("playerId") || (null == player.get("playerId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid player");
                }

                if(!player.containsKey("teamId") || (null == player.get("teamId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid team for player");
                }
            }
        }

        if(null != bench)
        {
            for(Map<String, String> player: bench)
            {
                if(!player.containsKey("playerId") || (null == player.get("playerId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid bench player");
                }

                if(!player.containsKey("teamId") || (null == player.get("teamId")))
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid team for bench player");
                }
            }
        }
    }
}
