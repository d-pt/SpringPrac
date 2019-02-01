package local.app.user.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorMesssage {

    private int errorCode;
    private Date timeStamp;
    private String message;
    private String detail;

}
