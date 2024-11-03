# Storage Valet - Introduction

## What is Storage Valet

Storage Valet is the name the Agora Storage team has assigned a group of
solutions that offer Cloud Storage options like databases or object storage for
teams on Agora. Like a valet with a car Storage takes charge of your data and
keeps it within its preprepared AWS account but teams using the solutions
maintain ownership and can retrieve their data at any time.

Storage Valet implements standard cloud solutions and prepares access to those
solutions from the clusters that run Agora. Currently there are two solutions
offered in this manner.

- AWS S3 is provided by [Storage Valet S3]()
- AWS RDS is provided by [Storage Valet RDS]()

Notably these solutions are NOT reimplementations of the cloud solutions listed
above. These are direct implementations OF the cloud solution. Meaning that the
implementations that are provided to the users actually has exactly the cloud
resources that are requested as the backing storage solution. Put more simply
if you use Storage Valet S3 you are getting an S3 bucket from Amazon, but
accessed through the mechanisms detailed in the documentation for each
solution. Currently this mechanism is IAM Roles for Service Accounts (IRSA).
