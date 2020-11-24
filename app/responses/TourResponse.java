package responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Series;
import models.Tour;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TourResponse
{
    private Long id;
    private String name;
    private Long startTime;
    private List<Series> seriesList = new ArrayList<>();

    public TourResponse(Tour tour)
    {
        this.id = tour.getId();
        this.name = tour.getName();
        this.startTime = tour.getStartTime();
    }
}
