package com.cognizant.claimsmicroservice.controller;

import com.cognizant.claimsmicroservice.model.SubmitClaimRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.claimsmicroservice.client.AuthClient;
import com.cognizant.claimsmicroservice.dto.ClaimStatusDTO;
import com.cognizant.claimsmicroservice.dto.PathDetailsDTO;
import com.cognizant.claimsmicroservice.exception.BenefitsNotFoundException;
import com.cognizant.claimsmicroservice.exception.ProviderNotFoundException;
import com.cognizant.claimsmicroservice.service.ClaimService;

import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = "Claims Controller")
@RestController
public class ClaimsController {

	@Autowired
	ClaimService claimServiceImpl;

	@Autowired
	AuthClient authClient;

	@Autowired
	ClaimStatusDTO claimsStatusDto;

	
	@ApiOperation(value = "Get Claim Status")
	@GetMapping(value = "/getClaimStatus/{claimId}/{policyId}/{memberId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getClaimStatus(@PathVariable int claimId, @PathVariable int policyId,
			@PathVariable int memberId, @RequestHeader(value = "Authorization", required = false) String token) throws ProviderNotFoundException {
		log.debug("token={}",token);
	
			if (!authClient.getValidity(token).getValid()) {

				return new ResponseEntity<>("Token is either expired or invalid...", HttpStatus.FORBIDDEN);
			}

		return new ResponseEntity<ClaimStatusDTO>(claimServiceImpl.getClaimStatus(claimId, policyId, memberId),
				HttpStatus.OK);
	}
	
	@ApiOperation(value = "Submit Claim")
	@PostMapping(value = "/submitClaim", produces =MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> submitClaim(@RequestBody SubmitClaimRequest submitClaimRequest,
			@RequestHeader(value = "Authorization", required = false) String token) throws ProviderNotFoundException, BenefitsNotFoundException {
		try {
			if (!authClient.getValidity(token).getValid()) {

				return new ResponseEntity<>("Token is either expired or invalid...", HttpStatus.FORBIDDEN);
			}
		}catch (FeignException e) {
			return new ResponseEntity<>("Token is either expired or invalid...", HttpStatus.BAD_REQUEST);

		}

			claimsStatusDto = claimServiceImpl.processSubmitClaim(submitClaimRequest.getPolicyId(),submitClaimRequest.getClaimId(), submitClaimRequest.getMemberId(), submitClaimRequest.getProviderId(), submitClaimRequest.getBenefitId(), submitClaimRequest.getTotalAmount(),
					submitClaimRequest.getClaimedAmount(),token);

		log.debug("claimsDTO={}", claimsStatusDto);
		return new ResponseEntity<ClaimStatusDTO>(claimsStatusDto, HttpStatus.OK);
	}

}
