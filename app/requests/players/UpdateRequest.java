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
    private Long dateOfBirth;
    private Long countryId;
    private String image;

    public void validate()
    {

    }
}
