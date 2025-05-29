package zw.co.afc.orbit.sla.util;

import lombok.extern.slf4j.Slf4j;
import zw.co.afc.orbit.sla.model.AgreementDetails;

import java.util.*;

@Slf4j
public class RecordDurationCalculatorUtil {

    public static Integer calculateRecordDuration(String slaCode, Boolean contractsRunConcurrently, List<AgreementDetails> agreementDetails) {
        Integer recordDuration = 0;
        switch (slaCode){
            case "SN":
                // In this case, there is only one user who is expected to perform an action
                Optional<AgreementDetails> maxAgreementDetailTime = agreementDetails.stream()
                        .max(Comparator.comparingInt(AgreementDetails::getTime));
                if (contractsRunConcurrently){
                    recordDuration = maxAgreementDetailTime.get().getTime();
                } else {
                    for (AgreementDetails agreementDetail: agreementDetails){
                        recordDuration = recordDuration + agreementDetail.getTime();
                    }
                }
                break;
            case "SQAC":
                // One after the other
                for (AgreementDetails agreementDetail: agreementDetails) {
                    recordDuration = recordDuration + agreementDetail.getTime();
                }
                break;
            case "SQC":
            case "SQQB":
                // One unique group after the other
                recordDuration = calculateUniqueIndexDuration(agreementDetails);
                break;
            case "PC":
            case "PQB":
                recordDuration = findMaxTime(agreementDetails);
                break;
            default:
                break;
        }
        return recordDuration;
    }

    public static int calculateUniqueIndexDuration(List<AgreementDetails> agreementDetails) {
        log.info("Calculating Unique Index Duration");
        Set<Integer> uniqueIndices = new HashSet<>();
        int recordDuration = 0;

        for (AgreementDetails agreementDetail : agreementDetails) {
            Integer index = agreementDetail.getIndex();
            if (uniqueIndices.add(index)) { // add returns true if index was not already in the set
                recordDuration += agreementDetail.getTime();
            }
        }

        return recordDuration;
    }

    public static int findMaxTime(List<AgreementDetails> agreementDetails) {
        OptionalInt maxTime = agreementDetails.stream()
                .mapToInt(AgreementDetails::getTime)
                .max();

        // Handle the case where the list might be empty
        return maxTime.orElse(0);
    }

}
