package requests.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRequest
{
    private String name;
    private Long countryId;
    private String dateOfBirth;
    private String image;

    public void validate()
    {
        if(StringUtils.isEmpty(this.name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        if(null ==  this.countryId)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        if(StringUtils.isEmpty(this.image))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }

        if(StringUtils.isEmpty(this.dateOfBirth))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Date of Birth cannot be empty");
        }
        else
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
