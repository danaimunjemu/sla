package zw.co.afc.orbit.sla.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zw.co.afc.orbit.sla.dto.request.ContractRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.model.Contract;
import zw.co.afc.orbit.sla.repository.ContractRepository;
import zw.co.afc.orbit.sla.service.iface.ContractService;
import zw.co.afc.orbit.sla.util.ResponseUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;


    @Override
    public ResponseEntity<APIResponse> createContract(ContractRequestDTO contractRequest) {
        // TODO this should be abstracted
        return null;
    }

    @Override
    public ResponseEntity<APIResponse> getContracts(Boolean isDeleted) {
        List<Contract> contracts = new ArrayList<>();
        if (isDeleted){
            contracts = contractRepository.findByIsDeletedTrue();
        } else {
            contracts = contractRepository.findByIsDeletedNullOrIsDeletedFalse();
        }
        return ResponseUtil.createResponse(
                200,
                true,
                "Contracts retrieved successfully",
                "We have retrieved " + contracts.size() +" contracts successfully",
                null,
                contracts
        );
    }

    @Override
    public ResponseEntity<APIResponse> getContract(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Contract with id " + id + " was not found."));
        return ResponseUtil.createResponse(
                200,
                true,
                "Contract retrieved successfully",
                "We have retrieved the contract successfully",
                null,
                contract
        );
    }

    @Override
    public ResponseEntity<APIResponse> updateContract(String id, ContractRequestDTO contractRequest) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Contract with id " + id + " was not found."));

        contract.setCompletionDeadline(contractRequest.completionDeadline());
        contract.setCompletionTime(contractRequest.completionTime());
        contract.setRole(contractRequest.role());
        contract.setAssignedUser(contractRequest.assignedUser());
        contract.setEscalationUser(contractRequest.escalationUser());
        contract.setDuration(contractRequest.duration());
        contract.setHasEscalated(contractRequest.hasEscalated());
        contract.setStatus(contractRequest.status());
        contract.setIndex(contractRequest.index());
        contract.setEscalationType(contractRequest.escalationType());

        Contract updatedContract = contractRepository.save(contract);
        return ResponseUtil.createResponse(
                200,
                true,
                "Contract updated successfully",
                "We have updated the Contract successfully",
                null,
                updatedContract
        );
    }

    @Override
    public ResponseEntity<APIResponse> deleteContract(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Contract with id " + id + " was not found."));

        contract.setIsDeleted(true);

        Contract deletedContract = contractRepository.save(contract);
        return ResponseUtil.createResponse(
                200,
                true,
                "Contract deleted successfully",
                "We have deleted the Contract successfully",
                null,
                deletedContract
        );
    }

    @Override
    public ResponseEntity<APIResponse> restoreContract(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Contract with id " + id + " was not found."));

        contract.setIsDeleted(false);

        Contract restoredContract = contractRepository.save(contract);
        return ResponseUtil.createResponse(
                200,
                true,
                "Contract restored successfully",
                "We have restored the Contract successfully",
                null,
                restoredContract
        );
    }
}
