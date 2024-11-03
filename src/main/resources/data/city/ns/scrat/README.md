# scrat

```
 _,                          _                
.'  `.                  ___.>"''-..-.          
`-.   ;           .--"""        .-._@;         
   ;  !_.--..._ .'      /     .[_@'`'.         
  ;            /       : .'  ; :_.._  `.       
  :           ;        ;[   _T-"  `.'-. `-.    
   \        .-:      ; `.`-=_,88p.   _.}.-"    
    `-.__.-'   \    /L._ Y",P$T888;  ""        
             .-'_.-'  / ;$$$$$$]8P;            
             \ /     / / "Y$$P" ^"             
     fsc      ;\_    `.\_._                    
              ]__\     \___;          
```

## Local Testing

### Server Setup

(Optional) If running scrat in an image, setup the network
```shell
docker network create scrat
```

Start the MongoDB Container:
```shell
docker run --name scrat_mongo --net scrat -p 27017:27017 -d mongo:latest
```

Run Scrat (docker):
```shell
bazel run //ns/scrat/cmd/server:image.load && \
  docker run --name scrat_server --rm --net scrat -p 8082:8082 \
  ns/scrat/cmd/server:image --db-name scrat --port 8082 --db-uri mongodb://scrat_mongo:27017
```

### Comparing Commits

Fill Many Commits
```shell
bazel run //ns/scrat/cmd/filler:filler -- --server-uri "http://[::1]:8082" --commits 1000
```

Compare Commits
```shell
bazel run //ns/scrat/cmd/cli:cli -- --server-uri "http://[::1]:8082" compare $COMMIT_1 $COMMIT_2
```
*Note*: pick any of the inserted commits for commit 1 and 2

### Uploading BEP

Generate a Build Event File:
```shell
GIT_COMMIT=$(git rev-parse HEAD) bazel build //... --aspects ns/scrat/bazel/aspect.bzl%scratspect --output_groups=+scrat --build_event_binary_file=./bep.binary
```

Upload Build Event
```shell
bazel run //ns/scrat/cmd/cli:cli -- -s "http://[::1]:8082" scan ./bep.binary
```
