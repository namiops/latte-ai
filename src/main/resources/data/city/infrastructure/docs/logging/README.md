# Agora Logging Platform 

Agora Logging Platform is a robust and scalable solution for collecting and querying log data from distributed systems. Leveraging Loki's unique indexing and querying capabilities, the platform enables users to easily filter and analyze large volumes of log data in real time. Fluent-bit provides a lightweight and flexible log collector that can be deployed to a variety of environments, including Kubernetes and cloud-based infrastructure. 

With its streamlined architecture and optimized resource usage, the platform is well-suited for organizations looking to optimize their log management and analysis workflows. Overall Agora logging platform provides a powerful and customizable solution for managing and analyzing log data, enabling teams to gain valuable insights into their systems and applications.

Istio mTLS is utilized to encrypt traffic in transit between services within the platform, providing an additional layer of security. Furthermore, the use of encrypted S3 buckets for log storage is an additional security measure that adds to the protection of sensitive data. REST APIs are also secured through authentication and authorization mechanisms, ensuring that only authorized users have access to log data. By combining these security features, the Agora Logging Platform is a highly secure and reliable solution for managing and analyzing log data, which is essential for organizations that handle sensitive information.


## Useful links

[Simple dashboards with filtered {.msg}{.log} field](https://observability.cityos-dev.woven-planet.tech/grafana/goto/Q-TkANBVk?orgId=1)

[Exploring Logs from the Dev cluster](https://observability.cityos-dev.woven-planet.tech/grafana/goto/9yxiANf4z?orgId=1)

[Security Kubernetes Standard Violation Dashboard](https://observability.cityos-dev.woven-planet.tech/grafana/goto/Q_Y60Nf4k?orgId=1)

[Istio-Proxy Access Logs](https://observability.cityos-dev.woven-planet.tech/grafana/goto/l7vR1NB4k?orgId=1)


## External Documentation and Courses

[Loki LogQL](https://grafana.com/docs/loki/v2.6.x/logql/)

[Fluent-Bit Documentation](https://docs.fluentbit.io/manual/)

Please note, you can reach **Dojo Team** to get access to external courses on slack [#dojo-help](https://woven-by-toyota.slack.com/archives/CDXFXTEBZ)

[Exploring Logs with Loki - Learn Grafana 7.0 in OReilly](https://learning.oreilly.com/library/view/learn-grafana-7-0/9781838826581/1a312c0c-d812-4017-90cf-e83ce1662610.xhtml)

[Logging and Tracing Course in Pluralsight](https://app.pluralsight.com/course-player?clipId=ab2ee6b2-e769-437a-8e19-aaa841f9bedf)

## Demo

[Agora Logging Demo, March 26th](Recording)

## Architectural Documents

[Architectural Decision Record](https://docs.google.com/document/d/1byyy8Nhy3dlRw-UJsUQNzKAFjUWfTdz4ZcihG_1F5BA/edit)

[Changelog](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/logging/changelog/CHANGELOG.md)
