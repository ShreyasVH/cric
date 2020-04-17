package requests.stadiums;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private String city;
    private String state;
    private Long countryId;

    public void validate()
    {

    }
}
