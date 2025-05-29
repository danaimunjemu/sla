package zw.co.afc.orbit.sla.enums;

public enum SLAType {
    SINGLE, // only one user involved in the process
    SEQUENTIAL, // it is passed from one user/group to the next until the last user/group
    PARALLEL, // multiple users can act simultaneously
}
