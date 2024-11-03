# Frequently Asked Questions

## I cannot reach my application

### I recieve an error: `upstream connect error or disconnect/reset before handlers`

Please refer to the [Istio Guidelines](01_istio.md) and **Applications should
bind to the pod's reported IP Address** to make sure that your application is
correctly configured to listen to the correct port and host. It is likely that
your application is listening on an invalid path, which causes Envoy/Istio to be
unable to reach your application correctly.
