package project1.armyfr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class WorkDescriptionDto {

    @NotBlank
    @Length(max = 100)
    private String content;

    @NotBlank
    private String fullDescription;
}
