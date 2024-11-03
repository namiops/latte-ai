# What is here?

Some scripts to help generating data.

# scripts

## package\_k8s-json-schemas.sh

### description
A script that downloads the K8S JSON schemas used for Kubeconform and puts them
in archives with version numbers.
These files are then typically uploaded to our Artifactory.

### usage
see
```bash
package_k8s-json-schemas.sh -h
```

Running the script without any option will download ALL versions into the
current working directory. This will take roughly 1 hour.

### example
```bash
./package_k8s-json-schemas.sh -o ~/tmp/schemas -s 'v1.24*'
```
Will get you a number of tar-balls with all versions v1.24\* in ~/tmp/schemas.

## upload\_to\_wcm-cityos\_artifactory.sh
A script that can be used to upload multiple files to Artifactory.

### Quirks
The script assumes that your have your API key in ~/.config/artifactory.key as
a flat file.

The scripts keeps the file-structure the same as from the source.

### usage
upload\_to\_wcm-cityos\_artifactory.sh <files to upload>

### example
```bash
upload_to_wcm-cityos_artifactory.sh ./k8s-json-schemas/*
```
