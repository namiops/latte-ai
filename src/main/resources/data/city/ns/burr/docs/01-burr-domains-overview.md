# BURR Domains Overview
BURR is a service of Test Course. Every BURR domains should fall into either of two parts, authoritative info or user managed info.

![domain context overview](./diagrams/domain/overview.png)

## Authoritative Info
Authoritative info is a concern of Test Course, which primarily involves personal information and organizational one owned/managed by TWC as an authority. As for the personal information, subject end users cannot register, modify, or delete the records without approval from TWC.

Examples of data attributes:
- Person name
- Date of birth
- Guardianship
- City household

Every personal information record must be uniquely tied to a single Woven ID. This data is collected during the onboarding processes defined by TWC, and then is populated in BURR Authoritative Info Service. Therefore, the data has a different lifecycle than Woven ID.

You can find the details in [Authoritative Info Domains](./domains-authoritative-info/README.md).

## User Managed Info
User managed info is personal information owned and managed by end users themselves, which is typically done in Woven App (coming soon). Every personal information record must be uniquely tied to a single Woven ID.

Examples of data attributes:
- Display name
- Avatar image

Every record shares its lifecycle with correspoinding Woven ID. The records are populated during the Woven ID sign up process with minimum attributes, Woven ID and email address. Additional attributes may be provided by end users on their own. They are destroyed during the Woven ID deletion process.

You can find the details in User Managed Info Domains (coming soon).
