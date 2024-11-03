# Identiy static page

## How to dev/test
Run this command and access `htttp://localhost:8080`
```
docker run -p 8080:80 \
    -v $(pwd)/nginx/nginx.conf:/etc/nginx/nginx.conf \
    -v $(pwd)/nginx/conf.d:/etc/nginx/conf.d \
    -v $(pwd)/html:/usr/share/nginx/html docker.artifactory-ha.tri-ad.tech/library/nginx:1.23.1
```

# Changelog

## v0.0.1
- add static maintainance page
- listen to both ipv4 and ipv6
