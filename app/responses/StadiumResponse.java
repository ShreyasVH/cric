package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StadiumResponse
{
    private Long id;

    private String name;

    private String city;

    private String state;

    private CountryResponse country;
}
