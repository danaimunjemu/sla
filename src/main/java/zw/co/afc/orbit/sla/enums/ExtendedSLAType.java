package zw.co.afc.orbit.sla.enums;

public enum ExtendedSLAType {
    NONE, // only uses the sla type without an extended type
    APPROVAL_CHAIN, // requires approval from designated users at each level
    COLLABORATIVE, // only one user at the same level needs to approve so that it moves to the next level
    QUOTA_BASED, // requires a certain number of actions at each level before progressing
    VERIFICATION, // includes a separate verification step at each level
    CYCLIC, // cycles back to the first level for continuous processing until termination conditions are met
}
