package ru.itpark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressValue {
    private int value;
    private int maxValue;

    public int getPercent() {
        return maxValue != 0 ? 100 * value / maxValue : 0;
    }

    public String getStringPercent() {
        return String.valueOf(getPercent());
    }
}
