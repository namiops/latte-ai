# Top

[Object Storage](https://en.wikipedia.org/wiki/Object_storage) is a solution to manage data as objects, often used by teams to retain large amounts of unstructured data such as images, videos, or arbitrary files.

Agora Storage has been working on designing an Object Storage solution 
that everyone on the Agora platform can use with necessary preset configurations
that follow the Woven City security and privacy policies.

Agora Storage is planning to start offering an Object Storage solution in FY2023.

## How to use Object Storage until Agora Storage starts offering it

Agora Storage recommends using S3 on each team's AWS account as a short-term way to get Object Storage for your project
until Agora Storage is ready to offer a turnkey solution.

Agora Storage will respect S3 APIs as a defacto standard Object Storage API even if we decide to use other Object Storage solutions than S3.
This should allow teams to have a relatively painless experience migrating their services from S3 used as a temporary solution to the future Agora Object Storage solution.

You can see more detailed instructions in "[How to create an S3 bucket on your AWS account](./how-to-create-s3-bucket.md)".

## Related technical notes for Agora internal discussions

- [TN-0323 Random thoughts on S3 usage](https://docs.google.com/document/d/18tks0b2uV3reMrlGPm_A6l1_RVqGdwq-N45J64nsBAQ/edit#heading=h.t2wwxeng0uj4)
