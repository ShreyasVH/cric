package requests.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.TeamType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;

    private Long countryId;

    private TeamType teamType;

    public void validate()
    {

    }
}
