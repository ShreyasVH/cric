package requests.series;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import enums.GameType;
import enums.SeriesType;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRequest
{
    private String name;
    private Long homeCountryId;
    private SeriesType type;
    private GameType gameType;
    private String startTime;
    private Long tourId;
    private List<Long> teams = new ArrayList<>();

    public void validate()
    {
        if(StringUtils.isEmpty(this.name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        if(null == this.homeCountryId)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        if(null == this.tourId)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid tour id");
        }

        if((null == this.teams) || (this.teams.size() < 2))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
        }

        if(null == this.type)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Series Type");
        }
        else
        {
            if((SeriesType.BI_LATERAL == this.type) && (this.getTeams().size() != 2))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
            }
            else if((SeriesType.TRI_SERIES == this.type) && (this.getTeams().size() != 3))
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Teams");
            }
        }

        if(null == this.gameType)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Game Type");
        }

        if(StringUtils.isEmpty(this.startTime))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Series Start Time");
        }
        else
        {
            try
            {
                Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.startTime));
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Series Start Time");
            }
        }
    }
}
