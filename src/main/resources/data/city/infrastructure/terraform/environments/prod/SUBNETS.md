# PROD SUBNET ALLOCATIONS

The design of IP allocations is available at [TN - MVP Agora-managed AWS accounts for Production](https://docs.google.com/document/d/1uZ8akOPzhLCGRRjp0BpuDgs3M9LgQYNOPJuskUrzJx4/edit#heading=h.e6wt2b7z8nso)

| Network Address | Broadcast Address | Direction   | Name                        |
| --------------- | ----------------- | ----------- | --------------------------- |
| 10.128.0.0/10   | 10.191.255.255    | User-Facing | root                        |
| 10.128.0.0/13   | 10.135.255.255    | User-Facing | Dev2 East                   |
| 10.136.0.0/13   | 10.143.255.255    | User-Facing | Lab2 East                   |
| 10.144.0.0/13   | 10.151.255.255    | User-Facing | Dev2 West                   |
| 10.152.0.0/13   | 10.159.255.255    | User-Facing | Lab2 West                   |
| 10.160.0.0/11   | 10.191.255.255    | User-Facing | Prod                        |
| 10.160.0.0/14   | 10.163.255.255    | User-Facing | Prod East IPAM Managed IPs  |
| 10.164.0.0/14   | 10.167.255.255    | User-Facing | Reserved                    |
| 10.168.0.0/14   | 10.171.255.255    | User-Facing | Reserved                    |
| 10.172.0.0/18   | 10.172.63.255     | User-Facing | Prod Transit East           |
| 10.172.64.0/18  | 10.172.127.255    | User-Facing | Prod AWS Marketplace East   |
| 10.172.128.0/18 | 10.172.191.255    | User-Facing | Prod Storage Valet East     |
| 10.172.192.0/18 | 10.172.255.255    | User-Facing | Prod Platform Internal East |
| 10.173.0.0/18   | 10.173.63.255     | User-Facing | Prod MLOps1 East            |

