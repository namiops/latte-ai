# Certification
The Certification domain provides information about evidence for Person. There are two types of evidence, identity and training qualification.

![domain diagram](../diagrams/domain/certification.png)

## Identity
Identity contains pieces of information for Person’s identity.

## ID Verification
ID Verification manages details about the evidence used for identity verification. The verification is conducted through eKYC as a part of the visitor onboarding process.

On the other hand, we don’t manage verification evidence for residents and workers. As for the resident onboarding, the verification is conducted offline with paperwork as the part of process. Additionally, we trust corporations with whom we have basic contracts to verify their employees' identities, assuming that workers are already verified.

## Face Image
Face Image manages Person’s facial photograph, which is used for identification. This image must be a photo of the actual user’s face and is mandatory for all the people who enter the city.

## Training Qualification
Training Qualification manages records of required training for people who enter the city. Currently, this only includes traffic rule training.

!!! note
    This is an ongoing project, as we don’t yet have a content management system for training. The development or procurement of such a system is under discussion, and it’s likely that this responsibility will be moved away from BURR once a decision is made.
