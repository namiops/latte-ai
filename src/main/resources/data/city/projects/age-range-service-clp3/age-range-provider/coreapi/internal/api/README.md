# The code in this directory is automatically generated.


## How to generate


### Precondition

required following

* docker
* git
* oapi-codegen

For WSL2 Ubuntu (I think you understand docker and git, so I omit it)

#### oapi-codegen

DO NOT USE latest version(=v2.0.0)!!

```bash
$ go install github.com/deepmap/oapi-codegen/cmd/oapi-codegen@v1.12.4
```

Add `./bashrc` to the execution path

```
$ export PATH=$PATH:~/go/bin
```

### Generate code


```bash
$ ./projects/age-range-service-clp3/age-range-provider/scripts/generate_coreapi_code_from_oapi_spec.sh 
```