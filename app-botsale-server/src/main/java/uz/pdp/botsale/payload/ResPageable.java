package uz.pdp.botsale.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResPageable {
    private Object object;
    private Long totalElements;
    private Integer currentPage;
}