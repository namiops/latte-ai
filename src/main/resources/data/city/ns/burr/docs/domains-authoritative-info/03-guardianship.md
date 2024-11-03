# Guardianship
The Guardianship domain describes guardianship relationships between Persons, primarily focusing on minors as wards. This is necessary because all the people who enter the city must make certain agreements in advance, such as the traffic rule in the city. For minors, their guardians must provide this agreement on their behalf.

![domain diagram](../diagrams/domain/guardianship.png)

## Guardianship
Guardianship defines the relationships between wards and their guardians. A Person may be associated with at most one Guardianship as a ward, as well as multiple guardians may be involved in a Guardianship. A Person may participate in different Guardianships.

A Person who is a ward must not be a guardian for others. This reflects real world legal principles: a ward requires guardians due to a lack of legal capability, which means they can not be a guardian for others.
