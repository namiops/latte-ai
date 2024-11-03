# creating kafka-connect-sample docker image

This directory is for building & pushing the kafka-connect image to our artifactory.

ref: https://developer.woven-city.toyota/docs/default/Component/kafka-service/10_kafka_connect/#building-your-container-image

## Usage
Edit the tag in `build_and_push.sh` and 
Please run this script on the same kind of machine of EKS (linux/amd64)

```shell
./build_and_push.sh
```
