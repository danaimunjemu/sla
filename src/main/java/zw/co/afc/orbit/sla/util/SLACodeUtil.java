package zw.co.afc.orbit.sla.util;

import zw.co.afc.orbit.sla.enums.ExtendedSLAType;
import zw.co.afc.orbit.sla.enums.SLAType;

public class SLACodeUtil {

    public static String slaCodeGenerator(SLAType slaType, ExtendedSLAType extendedSlaType) {
        switch (slaType) {
            case SINGLE:
                switch (extendedSlaType){
                    case NONE -> {
                        return "SN";
                    }
                    case VERIFICATION -> {
                        return "SV";
                    }
                    default -> {
                        throw new IllegalArgumentException("This is an illegal pairing for SLA and Extended SLA");
                    }
                }
            case SEQUENTIAL:
                switch (extendedSlaType){
                    case APPROVAL_CHAIN -> {
                        return "SQAC";
                    }
                    case COLLABORATIVE -> {
                        return "SQC";
                    }
                    case QUOTA_BASED -> {
                        return "SQQB";
                    }
                    case VERIFICATION -> {
                        return "SQV";
                    }
                    case CYCLIC -> {
                        return "SQCY";
                    }
                    default -> {
                        throw new IllegalArgumentException("This is an illegal pairing for SLA and Extended SLA");
                    }
                }
            case PARALLEL:
                switch (extendedSlaType){
                    case COLLABORATIVE -> {
                        return "PC";
                    }
                    case QUOTA_BASED -> {
                        return "PQB";
                    }
                    case VERIFICATION -> {
                        return "PV";
                    }
                    case CYCLIC -> {
                        return "PCY";
                    }
                    default -> {
                        throw new IllegalArgumentException("This is an illegal pairing for SLA and Extended SLA");
                    }
                }
            default:
                throw new IllegalArgumentException("This is an illegal SLA type");
        }
    }

}
