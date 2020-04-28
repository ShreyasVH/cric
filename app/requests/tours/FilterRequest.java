package requests.tours;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterRequest
{
    private int year;
    private int offset = 0;
    private int count = 20;

    public void validate()
    {
        if(0 == year)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid year");
        }
    }
}
