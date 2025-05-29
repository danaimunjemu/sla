package zw.co.afc.orbit.sla.util;

import zw.co.afc.orbit.sla.enums.ExtendedSLAType;
import zw.co.afc.orbit.sla.enums.SLAType;

import java.util.*;

public class SLAValidatorUtil {
    private static final Map<SLAType, Set<ExtendedSLAType>> validCombinations = new HashMap<>();

    static {
        validCombinations.put(SLAType.SINGLE, new HashSet<>(Arrays.asList(
//                ExtendedSLAType.NONE
//                ExtendedSLAType.VERIFICATION
        )));
        validCombinations.put(SLAType.SEQUENTIAL, new HashSet<>(Arrays.asList(
//                ExtendedSLAType.APPROVAL_CHAIN,
                ExtendedSLAType.COLLABORATIVE
//                ExtendedSLAType.QUOTA_BASED
//                ExtendedSLAType.VERIFICATION,
//                ExtendedSLAType.CYCLIC
        )));
        validCombinations.put(SLAType.PARALLEL, new HashSet<>(Arrays.asList(
//                ExtendedSLAType.COLLABORATIVE,
//                ExtendedSLAType.QUOTA_BASED
//                ExtendedSLAType.VERIFICATION,
//                ExtendedSLAType.CYCLIC
        )));
    }

    public static boolean isValidSLACombination(SLAType slaType, ExtendedSLAType extendedSLAType) {
        return validCombinations.containsKey(slaType) && validCombinations.get(slaType).contains(extendedSLAType);
    }
}
