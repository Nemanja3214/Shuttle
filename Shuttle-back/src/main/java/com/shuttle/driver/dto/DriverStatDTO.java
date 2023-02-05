package com.shuttle.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatDTO {
    double rejections;
    double rides;
    double hoursWorked;
    double moneyEarned;
}

