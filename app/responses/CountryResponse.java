package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResponse
{
    private Long id;

    private String name;

    private Date createdAt;

    private Date updatedAt;
}
