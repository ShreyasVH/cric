package responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StatsResponse
{
    private Integer count = 0;
    private List<Map<String, String>> stats = new ArrayList<>();
}
