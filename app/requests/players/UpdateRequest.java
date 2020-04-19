package requests.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequest
{
    private String name;
    private Long countryId;
    private String image;

    public void validate()
    {

    }
}
