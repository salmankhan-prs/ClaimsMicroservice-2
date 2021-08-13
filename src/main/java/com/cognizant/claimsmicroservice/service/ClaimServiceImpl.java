package com.cognizant.claimsmicroservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.claimsmicroservice.client.PolicyClient;
import com.cognizant.claimsmicroservice.dto.ClaimStatusDTO;
import com.cognizant.claimsmicroservice.exception.BenefitsNotFoundException;
import com.cognizant.claimsmicroservice.exception.ProviderNotFoundException;
import com.cognizant.claimsmicroservice.model.Benefits;
import com.cognizant.claimsmicroservice.model.Claim;
import com.cognizant.claimsmicroservice.model.ProviderPolicy;
import com.cognizant.claimsmicroservice.repository.ClaimRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService {

	@Autowired
	ClaimRepository claimRepository;

	@Autowired
	ClaimStatusDTO claimStatusDTO;

	@Autowired
	PolicyClient policyClient;

	@Override
	public ClaimStatusDTO getClaimStatus(int claimId, int policyId, int memberId){
		
		Claim claim = claimRepository.getById(claimId);
		

		log.debug("claim status={}", claim.getClaimStatus());
		log.debug("Policy={}", claim.getPolicyId());
		log.debug("claimid={}", claim.getClaimId());
		log.debug("member={}", claim.getMemberId());
		
		if (claim.getPolicyId() == policyId && claim.getMemberId() == memberId) {
			claimStatusDTO.setClaimStatus(claim.getClaimStatus());
			claimStatusDTO.setDescription(claim.getDescription());
			log.debug("claimdto={}", claimStatusDTO.getClaimStatus());
		}
		else {
			throw new ProviderNotFoundException("provider not found");
		}
		
		return claimStatusDTO;
	
	}

	@Override
	public ClaimStatusDTO processSubmitClaim(int policyId, int claimId, int memberId, int providerId, int benefitId,
			double totalAmount, double claimedAmount, String token) throws ProviderNotFoundException, BenefitsNotFoundException {
		Claim claimObj = null;
		 List<ProviderPolicy> chainOfProviders = policyClient.getChainOfProviders(policyId, token);
		log.debug("chainOfProvider={}", chainOfProviders);
		 List<Benefits> eligibleBenefits = policyClient.getEligibleBenefits(policyId, memberId, token);
		double eligibleClaimAmount = policyClient.getClaimAmount(policyId, memberId, benefitId, token);
		log.debug("claim Amount={}", eligibleClaimAmount);

		if (claimedAmount <= eligibleClaimAmount) {

			if (claimedAmount > totalAmount) {
				claimObj = new Claim(claimId, "Under Dispute",
						"Claim Amount cannot be settled more than its actually required!", policyId, memberId,
						benefitId, providerId, claimedAmount, 0);
				claimRepository.save(claimObj);
				claimStatusDTO.setClaimStatus(claimObj.getClaimStatus());
				claimStatusDTO.setDescription(claimObj.getDescription());
			} else {
				
				ProviderPolicy providers = chainOfProviders.stream().filter(p -> p.getProviderId() == providerId)
						.findFirst().orElseThrow(()->new ProviderNotFoundException("provider not found"));

				Benefits benefits = eligibleBenefits.stream().filter(b -> b.getBenefitId() == benefitId).findFirst()
						.orElseThrow(()->new BenefitsNotFoundException("benefit not found"));
				
				
				claimObj = new Claim(claimId, "Pending Action",
						"claim has been submitted! Please check after few days for confirmation", policyId,
						memberId, benefitId, providerId, claimedAmount, claimedAmount);
				claimRepository.save(claimObj);
				claimStatusDTO.setClaimStatus(claimObj.getClaimStatus());
				claimStatusDTO.setDescription(claimObj.getDescription());

				}
			
		} else {
			claimObj = new Claim(claimId, "Claim Rejected", "Claim Amount is greater than the eligible claim Amount",
					policyId, memberId, benefitId, providerId, claimedAmount, 0);
			claimRepository.save(claimObj);
			claimStatusDTO.setClaimStatus(claimObj.getClaimStatus());
			claimStatusDTO.setDescription(claimObj.getDescription());
		}

		return claimStatusDTO;
	}

}
