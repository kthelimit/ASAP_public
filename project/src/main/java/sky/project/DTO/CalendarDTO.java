package sky.project.DTO;

import lombok.Data;
import java.util.Date;

@Data
public class CalendarDTO {
    private Long calendarId;
    private String eventName;
    private String eventType;
    private Date startDate;
    private Date endDate;
}

