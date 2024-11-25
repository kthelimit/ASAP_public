package sky.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorResponse {
    private String message;

    public CustomErrorResponse(String message) {
        this.message = message;
    }

}
