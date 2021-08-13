package com.cognizant.claimsmicroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitClaimRequest {
    private int claimId;


    private int policyId;
    /**
     * This will be Id of the member.
     *
     */
    private int memberId;
    /**
     * This will be the total billed amount.
     *
     */
    private int benefitId;
    /**
     * This will be id of the provider.
     *
     */
    private int providerId;
    private double totalAmount;
    private  double claimedAmount;
}
