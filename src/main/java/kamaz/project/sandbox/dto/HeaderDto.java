package kamaz.project.sandbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeaderDto {
    String key;
    String label;
    Integer colspan;
    Integer rowspan;
    Integer row;
}
