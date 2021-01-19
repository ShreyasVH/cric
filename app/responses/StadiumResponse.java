package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Country;
import models.Stadium;

@Getter
@Setter
@NoArgsConstructor
public class StadiumResponse
{
    private Long id;
    private String name;
    private String city;
    private String state;
    private Country country;

    public StadiumResponse(Stadium stadium)
    {
        this.id = stadium.getId();
        this.name = stadium.getName();
        this.city = stadium.getCity();
        this.state = stadium.getState();
    }
}
