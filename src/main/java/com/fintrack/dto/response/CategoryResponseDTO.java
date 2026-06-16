package com.fintrack.dto.response;

import com.fintrack.enums.TransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private TransactionType type;
    private String description;
    private Long userId;
}
