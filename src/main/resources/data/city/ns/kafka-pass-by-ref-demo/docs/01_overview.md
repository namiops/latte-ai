## Pass-by-reference for async request-response

In order to overcome the issue described in the previous page, we encourage service developers to adopt what we call _pass-by-reference_ pattern, when it comes to cross-namespace asynchronous request-response communication.


!!! note "Intra-namespace Kafka usage"

    If you are looking to share personal data through Kafka ONLY WITHIN your Kubernetes namespace, things can be a lot easier. Please refer to [TN-0205 PII, Kafka and consent - Private Topics](https://docs.google.com/document/d/1wWupLXcqrbyLrVkLJZKvjFFk5qumtZjMIKTJItC9OmM/edit).

Here’s the overview of this pattern.

![overview-pass-by-ref](./assets/overview-pass-by-ref.png)

(Source: https://drive.google.com/file/d/1L1kn3V2kWolLD20SAozfuhzbR2XkXl48/view?usp=sharing)

Producer, instead of writing any personal data directly to the topic, writes the reference that points to it (e.g. resource ID). When the message is read by Consumer, it SYNChronously queries the specified reference, at which point the consent is checked.

!!! info "Consent docs"

    This design leverages the existing consent mechanism for synchronous workflow (step4 in the above diagram). Please refer to [the consent doc](https://developer.woven-city.toyota/docs/default/Component/consent/consent/) to know more.

Comparing with purely writing the data to the messaging system, while it introduces additional complexity and hinders some benefits async messaging brings, it solves the earlier mentioned issues.

* **Consumer is forced to respect user consent**  
  The message delivered through the topic doesn’t contain the actual data but only the reference. In order for the consumer to obtain the data, it has to synchronously query the data source, which involves consent checking.
* **Consent is checked at the very time the consumer processes the data**  
  Earlier we explain the difficulty of a case where the user revokes the consent after Producer has written their personal data to the topic. That is not a problem in this design, because it’s when Consumer tries to consume the data that the consent is checked.

In the following pages, we’ll go over the reference implementation (in Golang) so the service developers can adopt the pattern easily.
