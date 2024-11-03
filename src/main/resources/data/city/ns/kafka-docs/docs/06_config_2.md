# Setting up Kafka for your application - continued

You have to create the following mTLS resources by yourself if you are not using zebra kafka config template:

```yaml
---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: kafka-client-certs
  namespace: kafka-quickstart
spec:
  duration: 2160h
  renewBefore: 360h
  commonName: kafka-quickstart.${cluster_domain}
  subject:
    organizationalUnits:
    - CityOS
  dnsNames:
  - kafka-quickstart.${cluster_domain}
  usages:
  - client auth
  privateKey:
    rotationPolicy: Always
    algorithm: RSA
    size: 2048
  secretName: kafka-client-certs
  issuerRef:
    group: awspca.cert-manager.io
    kind: AWSPCAClusterIssuer
    name: aws-pca-cluster-issuer
---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: kafka-mtls
  namespace: kafka-quickstart
spec:
  host: kafka.generated
  exportTo:
  - .
  trafficPolicy:
    tls:
      mode: MUTUAL
      clientCertificate: /etc/kafka-certs/tls.crt
      privateKey: /etc/kafka-certs/tls.key
      caCertificates: /etc/kafka-cacert/kafka-ca-cert.pem
---
apiVersion: v1
data:
  # Amazon RootCA certificate: https://www.amazontrust.com/repository/SFSRootCAG2.pem
  kafka-ca-cert.pem: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUQ3ekNDQXRlZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBRENCbURFTE1Ba0dBMVVFQmhNQ1ZWTXgKRURBT0JnTlZCQWdUQjBGeWFYcHZibUV4RXpBUkJnTlZCQWNUQ2xOamIzUjBjMlJoYkdVeEpUQWpCZ05WQkFvVApIRk4wWVhKbWFXVnNaQ0JVWldOb2JtOXNiMmRwWlhNc0lFbHVZeTR4T3pBNUJnTlZCQU1UTWxOMFlYSm1hV1ZzClpDQlRaWEoyYVdObGN5QlNiMjkwSUVObGNuUnBabWxqWVhSbElFRjFkR2h2Y21sMGVTQXRJRWN5TUI0WERUQTUKTURrd01UQXdNREF3TUZvWERUTTNNVEl6TVRJek5UazFPVm93Z1pneEN6QUpCZ05WQkFZVEFsVlRNUkF3RGdZRApWUVFJRXdkQmNtbDZiMjVoTVJNd0VRWURWUVFIRXdwVFkyOTBkSE5rWVd4bE1TVXdJd1lEVlFRS0V4eFRkR0Z5ClptbGxiR1FnVkdWamFHNXZiRzluYVdWekxDQkpibU11TVRzd09RWURWUVFERXpKVGRHRnlabWxsYkdRZ1UyVnkKZG1salpYTWdVbTl2ZENCRFpYSjBhV1pwWTJGMFpTQkJkWFJvYjNKcGRIa2dMU0JITWpDQ0FTSXdEUVlKS29aSQpodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQU5VTU9zUXErVTdpOWI0WmwxK09pRk94SHovTHo1OGdFMjBwCk9zZ1BmVHozYTNZNFk5azJZS2liWGx3QWdMSXZXWC8yaC9rbFE0Ym5hUnRTbXBEaGNlUFlMUTFPYi9iSVNkbTIKOHhwV3JpdTJkQlRyei9zbTR4cTZIWll1YWp0WWxJbEhWdjhsb0pOd1U0UGFoSFFVdzJlZUJHZzYzNDVBV2gxSwpUczlEa1R2blZ0WUFjTXRTN250OXJqcm52REg1UmZiQ1lNOFRXUUlyZ013MFI5KzUzcEJsYlFMUExKR21wdWZlCmhSaEpmR1pPb3pwdHFiWHVOQzY2RFFPNE05OUg2N0ZyalNYWm04NkIwVVZHTXBad2g5NENEa2xEaGJac2M3dGsKNm1GQnJNblVWTitITDhjaXNpYk1uMWxVYUovOHZpb3Z4RlVjZFVCZ0Y0VUNWVG1MZndVQ0F3RUFBYU5DTUVBdwpEd1lEVlIwVEFRSC9CQVV3QXdFQi96QU9CZ05WSFE4QkFmOEVCQU1DQVFZd0hRWURWUjBPQkJZRUZKeGZBTitxCkFkY3dLemlJb3JodFNwenlFWkdETUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFCTE5xYUVkMm5kT3htZlp5TUkKYnc1aHlmMkUzRi9ZTm9ITjJCdEJMWjlnM2NjYWFOblJib2JoaUNQUEU5NUR6K0kwc3dTZEh5blZ2L2hleU5YQgp2ZTZTYnpKMDhwR0NMNzJDUW5xdEtyY2dmVTI4ZWxVU3doWHF2ZmRxbFM1c2RKL1BITFR5eFFHamhkQnlQcTF6CnF3dWJkUXh0UmJlT2xLeVdON1dnMEk4VlJ3N2o2SVBkai8zdlFRRjN6Q2VwWW9VejhqY0k3M0hQZHdiZXlCa2QKaUVEUGZVWWQveDdINGM3L0k5dkcrbzFWVHFrQzUwY1JSajcwL2IxN0tTYTdxV0ZpTnlpMkxTcjJFSVpreVhDbgowcTIzS1hCNTZqemFZeVdmL1dpM01PeHcrM1dLdDIxZ1o3SWV5TG5wMktodkFvdG5EVTBtVjNIYUlQekJTbENOCnNTaTYKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
kind: Secret
metadata:
  name: kafka-ca-cert
  namespace: kafka-quickstart
type: Opaque
```

Then, create your deployments like below:
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka-quickstart
  namespace: kafka-quickstart
  labels:
    app: kafka-quickstart
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka-quickstart
  template:
    metadata:
      labels:
        app: kafka-quickstart
    spec:
      containers:
      - name: kafka-quickstart
        image: kafka-quickstart:0.0.1
```

You don't have to add the following annotation to the deployment because it will be added to your pods by the mutating webhook of kafka-operator.

```yaml
metadata:
  annotations:
    # `secretName` must match `secretName` on the namespace's Certificate.
    sidecar.istio.io/userVolume: |-
      [
         {"name": "kafka-certs", "secret": {"secretName": "kafka-client-certs"}},
         {"name": "kafka-cacert", "secret": {"secretName": "kafka-ca-cert"}}
       ]
    # `mountPath` must match paths declared in the DestinationRule.
    # `name` must match `name` in the `userVolume` annotation above.
    sidecar.istio.io/userVolumeMount: |-
      [
        {"name": "kafka-certs", "mountPath":"/etc/kafka-certs", "readonly": true},
        {"name": "kafka-cacert", "mountPath":"/etc/kafka-cacert", "readonly": true}
      ]
```

!!! Note
    this setup allows your application code to use PLAINTEXT to communicate with Kafka. Communication is still secure, however, as the TLS encryption is handled by Envoy. Application-level TLS setup is not required.
