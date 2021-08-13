package com.cognizant.claimsmicroservice.dto;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Component
@AllArgsConstructor
public class PathDetailsDTO {

	@JsonProperty
	int policyId;
   @JsonProperty
	int claimId;
   @JsonProperty
	int memberId;
   @JsonProperty
	int providerId;
   @JsonProperty
	int benefitId;
   @JsonProperty
	double totalAmount;
   @JsonProperty
	double claimedAmount;
}
