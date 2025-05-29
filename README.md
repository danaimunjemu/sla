# AFC SLA Service

## Introduction

## Installation

## Models

### SLA Types
### Extended SLA Types
#### Legal Pairings
##### Version 1 Pairings
The following parings are what will be released on version 1 of the SLA Service.
| Code | SLA Type   | Extended SLA Type | Meaning                                                                                                                                |
|-----|------------|-------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| SQC | SEQUENTIAL | COLLABORATIVE     | Approval is passed in levels from one group to another. Groups can have one or more users and only one user per level needs to approve |

##### Version 2 Pairings

| Code | SLA Type   | Extended SLA Type | Meaning                                                                                                                         |
|-----|------------|-------------------|-------------------------------------------------------------------------------------------------------------------------------- -|
| SN  | SINGLE     | NONE              | Only affects one user                                                                                                           |
| SQAC | SEQUENTIAL | APPROVAL_CHAIN    | Passed from one user to the next                                                                                                |
| SQQB | SEQUENTIAL | QUOTA_BASED       | Approval is passed in levels and a certain number of actions (defined) must happen at each level before it is passed to the next |
| PC  | PARALLEL   | COLLABORATIVE     | Any one user from a group needs to approve for the whole group to have completed                                                |
| PQB | PARALLEL   | QUOTA_BASED       | A certain number of actions (defined) must happen in each group to complete                                                     |

##### Version 3 Pairings
| Code | SLA Type   | Extended SLA Type | Meaning                                                                                                                                |
|------|------------|-------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| SV   |            | VERIFICATION      | Needs another action to complete it                                                                                                    |
| SQV  |            | VERIFICATION      | Someone at each level has to approve before it moves to the next level                                                                 |
| SQCY |            | CYCLIC            | After it has reached the last level, control is returned to a predefined user to take action                                           |
| PV   |            | VERIFICATION      | Someone in each group has to approve to complete                                                                                       |
| PCY  |            | CYCLIC            | After all groups have actioned, control is returned to a predefined user to take action                                                |

## Messaging
### Topics
1. sla-record-create
2. sla-record-update
3. sla-record-status-update-authorise
4. sla-record-status-update-isupport
