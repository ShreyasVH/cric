package requests;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FilterRequest
{
    private Map<String, List<String>> filters = new HashMap<>();

    private Map<String, List<String>> orFilters = new HashMap<>();

    private Map<String, List<String>> notFilters = new HashMap<>();

    private int count = 25;

    private int offset = 0;

    private boolean includeDeleted = false;
}
