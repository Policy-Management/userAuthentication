package com.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {
    private String id;
    private String policyName;
    private Date startDate;
    private long durationInYears;
    private String companyName;
    private long amount;
    private String policyType;
    private String userTypes;
    private int termsPerYear;
    private long termAmount;
    private int interest;
}