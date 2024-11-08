# Confirm the differences of generated manifests between dev and local

# Usage
```
bash dump.sh -c <kube context> -d <exported directory>
```
`c` option specify the kubernetes context the script use.
`d` option specify the directory the manifests exported to. The directory will be created under the `./work` directory

# 1. Test CityService operator with manifets exported from DEV
## 1. Move Workdir
```
cd ./tools/diff
```
## 2. Dump manifests from dev
```
bash dump.sh -c dev -d dev
```

## 3. Apply cityservice CRs to local
```
for d in work/dev/input/*; do kubectl apply -f $d/namespace-*.yaml ; done
for d in work/dev/input/*; do kubectl apply -f $d/cityservice-*.yaml ; done
```

## 4. Dump manifests from local
- Dump manifests generated by cityservice operator from local
```
bash dump.sh -c local -d local
```

## 5. Check and visually confirm differences
```
diff -r local/ dev/
```

# 2. Compate manifests genereated by CityService operator before and after a change
## 1. Move Workdir
```
cd ./tools/diff
```
## 2. Dump manifests from dev before a change
```
bash dump.sh -c dev -d before
```

## 3. Apply Change
Apply change. The deployment operation is out of scope of this document. 

## 4. Dump manifest from dev after the change
```
bash dump.sh -c dev -d after
```

## 5. Check and confirm differnces with your eyes
```
diff -r before/ after/
```

