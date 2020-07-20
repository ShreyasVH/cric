package requests.tours;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private String startTime;

    public void validate()
    {

        if(!StringUtils.isEmpty(this.startTime))
        {
            try
            {
                Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.startTime));
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Tour Start Time");
            }
        }
    }
}
