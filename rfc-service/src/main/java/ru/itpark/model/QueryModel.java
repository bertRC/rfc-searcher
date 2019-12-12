package ru.itpark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itpark.enumeration.QueryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryModel {
    private String id; // UUID
    private String query;
    private QueryStatus status;
}
