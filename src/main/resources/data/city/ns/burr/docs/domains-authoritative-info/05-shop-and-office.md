# Shop & Office
The Shop & Office domain offers tenant management features. It manages business tenants, corporations, and its membership, and provides information about which organization an individual belongs to.

Every record is based on tenant lease contracts made between TWC and the tenants.

![domain diagram](../diagrams/domain/shop-and-office.png)

## Business Tenant
Business Tenant represents a business entity that occupies a physical space in the tenant area (typically, it’s a room) in the city, either to operate a shop or provide an office for its employees. Each Business Tenant is populated based on tenant lease contracts and is directly linked to a specific contract in a one-to-one relationship.

Each Business Tenant must be associated with exactly one Corporation.

## Membership
Membership describes a relationship between a Person and a Business Tenant or Corporation, indicating which organization an individual belongs to.

A Membership may be associated with multiple Business Tenants while having a one-to-one association between Corporations. Additionally, a Person may be associated with multiple Corporations via multiple Memberships.

Membership may include additional personal information about the Person that is relevant to their role within the organization. For example, this could include company email address, department, and job title.

## Corporation
Corporation represents the entity subject to a lease contract, which can be a sole proprietor or any legal entity. Multiple Business Tenants and Memberships may be associated with a single Corporation.

!!! note
    Each Corporation must be based on a basic contract, but this requirement is not reflected in the specification currently. The main focus of the Shop & Office domain feature so far is managing Business Tenants. Therefore, a Corporation is just for organizing Business Tenants. We’ll revisit it with OBJ and WIG folks soon to support inventor onboarding scenarios.

## IDs
Each Business Tenant and Corporation is assigned an ID issued by BURR during the registration.

!!! note
    This is a temporary measure since the tenant/corporation onboarding process is still under discussion. It’s likely to be changed soon to use IDs issued by an external service or procedure when registering entries in BURR.

## Occupancy
Every Business Tenant occupies a physical space in the tenant area in the city. It must be associated with a tenant area identified by a City Address. The basic principle is that a single space can be occupied by at most one Business Tenant at any given point in time.

However, there is an exception. A single physical space in the tenant area might be separated by partitions and occupied by different Business Tenants. In this case, each separated area is identified by a pair of City Addresses and branch numbers.

## Validity
Both Business Tenant and Membership have validity periods defined by effective date and termination date. The validity period of a Business Tenant must be identical to one specified in its tenant lease contracts.

A Corporation is considered valid as long as it has at least one valid Business Tenant. Unlike Business Tenants, whose validity periods are dictated by lease contracts, the validity period for a Corporation is not explicitly defined in such contracts.

!!! note
    This will be updated soon. Each Corporation must be based on a basic contract, but currently, the requirement is not reflected in the specification. Once it gets updated, the validity period of a Corporation must be identical to one specified in its basic contracts.

TBD: How the validity period of Membership should be defined/provided.
