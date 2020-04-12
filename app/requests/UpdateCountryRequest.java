package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateCountryRequest
{
    private Long id;

    private String name;

    public void validate()
    {
        if((null != this.getName()) && StringUtils.isEmpty(this.getName()))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }
}
