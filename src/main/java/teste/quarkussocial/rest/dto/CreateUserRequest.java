package teste.quarkussocial.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateUserRequest {
    @NotBlank(message = "NeedAName!")
    private String name;
    @NotNull(message = "NeedAnAge!")
    private Integer age;

}
