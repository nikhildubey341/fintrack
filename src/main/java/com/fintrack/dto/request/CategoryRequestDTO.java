package com.fintrack.dto.request;

import com.fintrack.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Type is required (INCOME or EXPENSE)")
    private TransactionType type;

    private String description;

    @NotNull(message = "User ID is required")
    private Long userId;
}
