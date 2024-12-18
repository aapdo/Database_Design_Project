package com.drawit.drawit.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private String target;
    private String color;
    private Integer cost;

}
