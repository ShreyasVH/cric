package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mysql.cj.util.StringUtils;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletionException;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryRequest
{
    private Long id;

    private String name;

    public void validate()
    {
        if(StringUtils.isNullOrEmpty(this.name))
        {
//            throw new CompletionException(new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription()));
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }
}
