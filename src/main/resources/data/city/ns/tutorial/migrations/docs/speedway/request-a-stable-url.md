# Request for Stable URL

[go/stable-apis](<go/stable-apis>)

## When to make a Stable URL

The stable URL is typically used when a service has reached **a level of stability and reliability that warrants its widespread use.**

There is a long term plan in the works for how to manage domains for the stable APIs.
Meanwhile, if you need a domain for your stable API please follow these instructions noting that the Agora team will reach out if there is any issues when migrating to the long term plan once it roles out.

!!! note
    This document covers in depth the _stable API_ domains only; they are `.toyota` domains and should point to internet facing IPs.
    Agora's private ingress filter access based on source IPs, but are valid internet address.
    If you require a domain pointing to a private IP address please make sure to use a `.tech` top level domain.
    Configuration structures in this case are outside of the scope of this document.

### Key Indicators

- Completion of Development and Testing.
  - Major bugs and issues have been identified and resolved.
- Internal Reviews and Approvals.
  - Stakeholders and team leads have approved the service for public or broader internal use.
- Performance and Scalability
  - Performance and scalability has been tested under expected load conditions.
  - Metrics and monitoring are in place.

### Public vs Private Ingress

- **Public Ingress:** Accessible from the internet, suitable for externally-facing services, requires strong security measures.
  - Use it for public services when you need real world users.
- **Private Ingress:** Accessible only within a private network, suitable for internal services, inherently more secure due to isolation from the public internet.
  - Use it for internal services.

## Adding and managing records

All the stable APIs zone files contain a block similar to that at the beginning:

```plain
locals {
  ##############
  # Dev / Gen1 #
  ##############
  dev_private  = "ae1c303c2306e47b5896272b034e2c5c-1544833688.ap-northeast-1.elb.amazonaws.com."

  ##################
  # Preprod / Gen2 #
  ##################
  preprod_public       = "city-public-ingress-117d06698eea8815.elb.ap-northeast-1.amazonaws.com."

  #########################
  # Production / Speedway #
  #########################

  # for most workloads (if unsure this is the one you probably want)
  prod_private         = "a8ba0ad2418be48079c1c4b71c5295ae-1707094323.ap-northeast-1.elb.amazonaws.com."
  # for users running GPU workloads
  prod_gpu_private     = "ac8bcfd6fdb4d48f1aa2490d3dafe608-2063757883.ap-northeast-1.elb.amazonaws.com."
}
```

This is a local variables declaration block, and here you can see each one of the environments with an
explanation of what each variable means.

When pointing to one of these environments, make sure to use a CNAME record leveraging that. For pointing to other addresses, use the record type that is more appropriate for your use case.

As an example, imagine you want to setup domains for the `My Amazing App`. Your app has 2 services:

- **myamazingapp.woven-city.toyota:** user-facing page for your users; this is your landing page
- **myamazingapp.woven-city-api.toyota:** your REST backend protected by `drako`.

Both services are hosted in Agora's production cluster. Also, neither of them uses a pod with GPU.
You will need to:

1. [Fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo) the repository.  
1. Make changes.
1. Send a PR justifying why you are doing this.

The changes you will need to do are (please change `X` and `Y` to the appropriate numbers):

```hcl title="public_hosted_zones/toyota/zones/woven-city-api.toyota.tf"
# ---------------------------------------------------------------------------------------------------------------------
# CNAME records
# ---------------------------------------------------------------------------------------------------------------------
resource "aws_route53_record" "woven-city-api-toyota-cname-X" {
  zone_id = aws_route53_zone.woven-city-api-toyota.zone_id
  name    = "myamazingapp.woven-city-api.toyota."
  records = [local.prod_private,]
  ttl     = 300
  type    = "CNAME"
}
```

```hcl title="public_hosted_zones/toyota/zones/woven-city.toyota.tf"
# ---------------------------------------------------------------------------------------------------------------------
# CNAME records
# ---------------------------------------------------------------------------------------------------------------------
resource "aws_route53_record" "woven-city-toyota-cname-X" {
  zone_id = aws_route53_zone.woven-city-toyota.zone_id
  name    = "myamazingapp.woven-city.toyota."
  records = [local.prod_private,]
  ttl     = 300
  type    = "CNAME"
}
```

## Management

The public domains owned by Woven by Toyota are managed on the Infrastructure Security's woven-aws-dns-management repository([go/dns](https://github.tri-ad.tech/information-security/woven-aws-dns-management)) via Terraform.  

At least for now, this is the infrastructure and flow we are leveraging to manage domains.

Suggested patterns for domain name 

| Pattern  | Usecase |
| -------- | ------- |
| `DOMAIN-NAME`-adm.toyota | For domains pointing to admin applications |
| `DOMAIN-NAME`-api.toyota    | For domains pointing to api(s)|
| `DOMAIN-NAME`.toyota  | For domains pointing to any other applications   |


The stable APIs zone files are located at:

- [`DOMAIN-NAME`.toyota](https://github.tri-ad.tech/information-security/woven-aws-dns-management/blob/main/public_hosted_zones/toyota/zones/woven-city.toyota.tf) for user-facing websites.
- [`DOMAIN-NAME`-adm.toyota](https://github.tri-ad.tech/information-security/woven-aws-dns-management/blob/main/public_hosted_zones/toyota/zones/woven-city-adm.toyota.tf) for admin URLs.
- [`DOMAIN-NAME`-api.toyota](https://github.tri-ad.tech/information-security/woven-aws-dns-management/blob/main/public_hosted_zones/toyota/zones/woven-city-api.toyota.tf) for API endpoints.

If you need a different domain, please follow EnTec's process by filling up a ticket [here](http://go/snow-sd).
Expect long lead time (upwards to a month) if under `.toyota` top-level domain.

You will fork [this](https://github.tri-ad.tech/information-security/woven-aws-dns-management/fork) repository and send a PR to the `main` branch.
Once it gets approved by security, it will be automatically merged and your entry will start propagating.

## Advanced configuration

There are more advanced configurations that can be done, including domain delegation. However, those fall outside
of the scope of reserving a long term protected API domain name and, therefore, are out of the scope of this document.
