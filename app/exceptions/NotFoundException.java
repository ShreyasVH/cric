package exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotFoundException extends MyException
{
    static final long serialVersionUID = 2L;

    private Integer httpStatusCode = 404;

    public NotFoundException(Integer code, String description)
    {
        super(code, description);
    }
}
