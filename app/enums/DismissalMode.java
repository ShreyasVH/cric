package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum DismissalMode {
    BOWLED(1),
    CAUGHT(2),
    LBW(3),
    RUN_OUT(4),
    STUMPED(5),
    HIT_TWICE(6),
    HIT_WICKET(7),
    OBSTRUCTING_THE_FIELD(8),
    TIMED_OUT(9),
    RETIRED_HURT(10),
    HANDLED_THE_BALL(11);

    private int id;

    public static DismissalMode getById(int id)
    {
        DismissalMode dismissalMode = null;
        for(DismissalMode mode: DismissalMode.values())
        {
            if(mode.getId() == id)
            {
                dismissalMode = mode;
                break;
            }
        }
        return dismissalMode;
    }
}
