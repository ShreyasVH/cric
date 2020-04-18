package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TeamType
{
    INTERNATIONAL(0),
    DOMESTIC(1),
    FRANCHISE(2);

    @Getter
    private int value;
}
