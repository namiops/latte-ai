# Resident
The Resident domain provides personal information as a resident and describes relationships for residents, households, and addresses in the city.

Every record is based on house lease contracts made between TWC and the residents. It focuses exclusively on residents within the city and does not include information about living outside the city.

The data will be populated during the resident onboarding process.

![domain diagram](../diagrams/domain/resident.png)

## City Household
City Household represents a household in the city. Each City Household is associated with a house lease contract.

## Occupancy
Every City Household occupies a residence within the city. It must be associated with a residence identified by a City Address. A single residence can be occupied by at most one City Household at any given point in time, which reflects real estate business in the real world.

## Validity
City Household has validity period defined by effective date and termination date. The validity period must be identical to one specified in its house lease contracts.
