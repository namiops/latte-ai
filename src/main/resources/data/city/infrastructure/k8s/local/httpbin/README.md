# httpbin service
This is [httpbin](https://httpbin.org) service deployed to the this cluster.
It is useful to test a variety of http requests.
In the cluster, all endpoints are protected by [Agora API Gateway](https://github.tri-ad.tech/cityos-platform/keycloak-tutorial/tree/main/samples/agora_api_gateway) and integrated with Woven ID.

# How to access
From internal company network(or connecting with VPN), access follwoing URL. 

[https://httpbin.cityos-dev.woven-planet.tech/](https://httpbin.cityos-dev.woven-planet.tech/)

![httpbin screen shot](./httpbin_image.png)

You will be redirected to WovenID login page.

![Woven ID screen shot](./wovenid_login.png)

You can register the user at "Register" link or you can login with your account if you already have.

# How to deploy to local cluster
You need to apply this application by hand if you want to deploy httpbin in `local` cluster(minikube).
This application is not deployed by flux by default since only those who want to test Agora API gateway needs this.

To deploy httpbin into your local cluster, run following command at this directcoty.
```
kubectl apply -k .
```

# How to access to httpbin service in your local cluster
## Configure /etc/hosts
After you deployed httpbin, httpbin service will be listen [here](http://httpbin.woven-city.local).
This domain name is not publc domain, so you may need to add the entry at `/etc/hosts` in your computer like this.

```
# /etc/hosts
127.0.0.1 localhost
10.97.96.205 id.woven-city.local httpbin.woven-city.local
```

The IP address that is pointed by the local domain is `extenal ip address` of `city-ingress` service.
You can get the IP address like this.
```
devec2@ip-10-13-93-219:~/git/cityos/$ kubectl -n city-ingress get svc
NAME                          TYPE           CLUSTER-IP     EXTERNAL-IP    PORT(S)                                      AGE
city-ingress-gateway-1-12-1   LoadBalancer   10.97.96.205   10.97.96.205   15021:31737/TCP,80:30770/TCP,443:31375/TCP   6h34m
```

## Access to the service
From your browser, go to [http://httpbin.woven-city.local](http://httpbin.woven-city.local).
