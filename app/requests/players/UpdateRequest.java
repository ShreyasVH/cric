package requests.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.util.StringUtils;
import java.text.ParseException;
import exceptions.BadRequestException;
import enums.ErrorCode;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private String dateOfBirth;
    private Long countryId;
    private String image;

    public void validate()
    {
        if(!StringUtils.isEmpty(this.dateOfBirth))
        {
            try
            {
                Date dateOfBirth = (new SimpleDateFormat("yyyy-MM-dd").parse(this.dateOfBirth));
            }
            catch(ParseException ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid date of birth");
            }
        }
    }
}
