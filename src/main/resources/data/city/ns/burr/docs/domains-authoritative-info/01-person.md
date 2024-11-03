# Person
The Person domain provides users’ personal information managed by the city. The users must be a woven ID holder. The information is collected through the user onboarding process.

![domain diagram](../diagrams/domain/person.png)

## Person
The Person is the most essential model in BURR authoritative info part that represents the individuals who is a woven ID holder. Every Person must be associated with woven ID in one-to-one manner and identified by the ID.

## Basic Info
The Basic Info is a part of Person that manages basic personal information.

The matrix below shows what attributes will be collected through what onboarding process.

| | Resident | Visitor | Worker |
|:--:|:--:|:--:|:--:|
| Normative name | Optional | Optional | Optional |
| Phonetic name | N/A | N/A | N/A |
| Latin name | Required | Required | Required |
| Date of birth | Required | Required | Required |
| Email address | Required | Required | Required |
| Phone number | Required | Required | Required |

Basic Info has three different forms of person names, normative name, phonetic name, and latin name.

### Normative Name
TBD: Definition of normative name

### Phonetic Name
!!! warning
    Phonetic name will be obsoleted soon.

    Throughout working with stakeholders for over a year, BURR team figured out phonetic names won't be collected through the user onboarding process and concluded that it does not make sense anymore.

### Latin Name
The latin name is a person's name written in latin characters. e.g., John Smith, Taro Yamada.

## Emergency Contact
Emergency Contact is a part of Person that manages a contact person information in case of emergency, e.g., traffic accident or disaster. It consists of a pair of name and phone number.
