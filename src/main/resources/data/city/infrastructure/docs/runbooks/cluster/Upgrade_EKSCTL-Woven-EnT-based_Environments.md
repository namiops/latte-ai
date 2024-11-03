# Upgrade EKSCTL-Woven-EnT-based Environments

<!-- vim-markdown-toc GFM -->

- [Intro](#intro)
- [Required software](#required-software)
  - [Important resources](#important-resources)
- [Steps to Upgrade](#steps-to-upgrade)
  - [Preparation](#preparation)
  - [Roll out](#roll-out)
- [Known Issues](#known-issues)
  - [Pipeline stuck during draining the nodes](#pipeline-stuck-during-draining-the-nodes)
  - [pipeline fails because of time-out](#pipeline-fails-because-of-time-out)
  - [pipeline fails on rerun step](#pipeline-fails-on-rerun-step)
  - [Vault injected pods not working correctly after upgrade](#vault-injected-pods-not-working-correctly-after-upgrade)
  - [Cannot evict pod as it would violate the pod's disruption budget](#cannot-evict-pod-as-it-would-violate-the-pods-disruption-budget)
  - [Out-of IP issue](#out-of-ip-issue)
  - [Kube-proxy broken](#kube-proxy-broken)
  - [Auto Scaling groups not cleaned up after update](#auto-scaling-groups-not-cleaned-up-after-update)
  - [Controllers' unevictable pods in `knative-serving` namespace](#controllers-unevictable-pods-in-knative-serving-namespace)
  - [Slow termination of KServe InferenceService's pods](#slow-termination-of-kserve-inferenceservices-pods)

<!-- vim-markdown-toc -->

## Intro

This runbook describes the upgrade procedure for the Kubernetes clusters based
on the Woven Enterprise Technologies Concourse pipelines, which uses the
[eksctl] tool.

This is only applicable to the old *legacy* environments:

- agora-ci.woven-planet.tech at AWS account [093116320723]
- agora-lab.woven-planet.tech at AWS account [370564492268]
- cityos-dev.woven-planet.tech at AWS account [835215587209]

## Required software

- [eksctl] version [0.129.0](https://github.com/weaveworks/eksctl/releases/tag/v0.129.0)
- [KubePug]
- [Pluto]

### Important resources

- The configuration of the Woven IT pipelines is in the
  [EKS-provision-configuration repository] under the CityOS Platform GitHub
  organization
- Every environment is provided by the `eksctl` tool in version `0.129.0`
  according to Woven Enterprise Technologies
- Access to all environments is across AWS SSO, and this is baked by Azure AD
  groups configurable on [Portal Azure] - currently the only person who can
  grant access is [Mathieu sauve-frankel]
- You can access the AWS Console by [Woven AWS Apps]

## Steps to Upgrade

### Preparation

1. Create a Monday.com Story in the [Agora Infra Stories Board] for the upgrade
    - Assign a *Shadow Engineer* to verify that all steps are done correctly by
      the *Primary Engineer*.
    - Assign a *Coordinator* to check that all steps are followed as described in
      this runbook.
    - Put all updates regarding the upgrade into the Monday.com Story.
2. Check for any incompatibilities, deprecations and **deletions**  with [KubePug] and [Pluto]
    - KubePug example:

      ```sh
      kubectl krew install deprecations
      alias kdeprecationsudo='kubectl deprecations --as-group aad:0f158ca2-948a-4d79-83b1-f21380bd16aa --as sudo'
      kdeprecationsudo --k8s-version v1.23.17 -v info
      ```

      Note:
      - Get the right k8s-version from the tags of the [Kubernetes Git Repo]
      - Make sure there is no Deleted APIs, otherwise these resources will not be
        deployed after upgrade
    - Pluto example:

      ```sh
      brew install FairwindsOps/tap/pluto
      pluto detect-all-in-cluster -o wide  --target-versions k8s=v1.23.0 2>/dev/null
      ```

      Note:
      - Make sure there is no resources that will be removed (defined in
        `REMOVED` field), except pod-identity-webhook
3. Read the notifications on the EKS page in the AWS account to see if there
   are any breaking changes we should be aware of.
4. Prepare PRs for upgrading Kubernetes in the relevant Cluster to the
   relevant version in the [EKS-provision-configuration repository]
    - Refer to this [PR for upgrading the LAB Cluster to 1.22] as an example.
      - The `Control Plane` will be upgraded to the version set in the `cluster.yml` file.
      - The `NodeGroups` need to be recreated because of the limitation of
        `eksctl`.  Just change the version strings in the `NodeGroup` names (E.g.
        `1-21` to `1-22`). Additional version strings like *genX- can be removed
        for the new version.
    - Create multiple PRs to upgrade node group gradually using following order:
      - control-plane
      - istio
      - ng-shared
      - others
      - logging
    - *(Optionally only if needed)* Modify the `addons` section to change the
      configuration of specific `add-ons` as described in the
      [eksctl documentation about updating addons]
    - *(Optionally only if needed)* Modify the `cluster-iamidentify.yml` file to
      add additional emergency access to the cluster, if for some reason the main
      RBAC groups are broken or escalation to `super-admin` is needed.
      - This should not need to be necessary if `aws-auth`
        [ConfigMap](https://github.com/kubernetes-sigs/aws-iam-authenticator#full-configuration-format)
        is valid, therefore access is correctly configured already. But it is
        good practice to double-check if the `ARN` of the role here is correct.
        1. Go to the [Woven AWS Apps]
        2. Choose the right account listed above.
        3. Click on the `Management Console`
        4. Go to the [IAM Roles]
        5. Find `AWSReservedSSO_AdministratorAccess` prefixed role, and check if
           `ARN` in a file is correct one
        6. The string `/aws-reserved/sso.amazonaws.com/ap-northeast-1` should be
           trimmed from `ARN` because of the limitation of `aws-auth`
        > **:warning: Important :warning:**
        > Be really careful when you modify this file as if you will break this file you can break the whole cluster.
        >
        > Check by `yamlfmt` is the structure is correct.
5. Take note of the current cluster situation by issuing the following commands
   and appending the output to the Monday.com item.
    - `kubectl get nodes -o wide | tee  nodes-wide.out` - Dump state of current nodes
    - `kubectl describe nodes | tee nodes-desc.out` - Dump information about current nodes
    - `kubectl get pod -o wide --all-namespaces | tee pod-assign.out` - Dump information on which POD is on which node
6. Confirm that everything is prepared in regards of the pipeline, such as:
    - The pipeline is upgraded, and `eksctl` is the current one.
    - The `terraform` state is migrated to the currently used version of
      `terraform` by their pipeline.
    - Access to [wcm-servicemsh-k8s-deployment concourse pipeline] has been
      granted for both engineers.
7. Schedule the upgrade and send an announcement
    - We do Kubernetes upgrades during office hours and we depend on support from
      our team in India. Therefore schedule to start at after 14:00 and be
      finished by 17:00 JST.
    - Send an announcement to [#wcm-agora-announcement]. [Template](upgrade_kube_template.md)

### Roll out

:placard: Log all commands you run on the console with a time-stamp and take
notes :spiral_notepad: of all operations that you do on the AWS Console during
the update. *Use Tmux, GNU Screen or another terminal that can save it's
output.*
This information might be of value in case of problems later (trouble shouting,
documentation, post-mortem, ...)

1. Merge the PR in the [EKS-provision-configuration repository] gradually
   following the node group order and go to the `AWS Management Console`
2. The pipeline will automatically start. Observe it from the
   [wcm-servicemsh-k8s-deployment concourse pipeline] webui.
    - Click on `wcm-servicemesh-k8s-development`
    - On the bottom right confirm the version of concourse (e.g. v7.9.3)
    - Click on `apline-eksctl-kubectl-docker-image` - confirm digest
    - Click on `wcm-servicemesh-k8s-deployment` then on `development4`
3. Check the [EKS Control Plane] and verify that the `version` is `Updating...`
4. Monitor EKS cloudwatch metrics and logs
   - Refs: https://repost.aws/knowledge-center/eks-get-control-plane-logs
   - For example, monitoring high spike of failed api requests to EKS.
     In this case, we do not want to overload the control plane before it scales.

    ```
    fields @timestamp, responseStatus.code, @message
     | filter @logStream like /^kube-apiserver-audit/
     | filter responseStatus.code >= 400
     | stats count(*) as count
    ```

5. Observe the status of the PODs in a `Terminal` by running:
   `watch -n 0.5 "kubectl get pod -A | grep -E 'Error|CrashLoopBackOff|Init|ImageBackPullOff'"`
   If there is an issue with `kube-proxy` or `aws-node`, check the **Known
   Issues section** and fix the issues.

    > **:warning: Important :warning:**
    > Remember to check critical resources such as the `kube-system` namespace first.

6. After confirming via the WEB-UI [EKS Control Plane] that the `Control Plane`
   is upgraded, run the following command to double check:
    `kubectl version`
7. Verify on the [CloudFormation Stacks] that the new `NodeGroups` are created.
8. Observe the new nodes appearing and wait until the old
    nodes get the `Scheduling Disabled` flag by running:
    `kubectl watch get nodes -o wide` or by using `k9s`.
9. The [Fargate] nodes need to be manually restarted. This is a known issue.
   This can be fixed by restarting the `rollout` of the `deployments` running
   on the fargate nodes.
   Currently those deployments are:
    - dns-autoscaler-cluster-proportional-autoscaler
    - coredns
10. Double-check that everything in the `kube-system` namespace is healthy.
    Especially that the [CSI Drivers] are not stuck.
11. Watch for problems with the PODs as described in step 4.
    For best practice the *Shadow Engineer* should be doing this in parallel.
12. Check that the Agora Infra Services, such as `Observability` and
    `Logging` are working, and that there are no new alerts appearing.
13. After ensuring everything is working fine, ask the `Coordinator` to
    announce the success of the update, and ask other Teams to verify if
    everything is working fine.
14. Make sure pods that are supposed to be injected with the `vault-agent` are
    injected. To list all the pods with vault-agent injection run:
    `kubectl get po -Ao json | jq -r '.items[] | select(.metadata.annotations."vault.hashicorp.com/agent-inject" == "true") | .metadata.namespace + " " + .metadata.name + " " + .status.phase' | column -t`
15. Dump current situation - see step 4 in Preparation
    - Compare the status of PODs pre and post the upgrade.
16. Make sure that the `Auto Scaling Group` in the AWS account is upgraded as in the PR and
    there is no leftover ASG.
17. Send a message to [#wcm-agora-announcement] that the upgrade has been completed.

## Known Issues

### Pipeline stuck during draining the nodes

You may see a log message in the pipeline log like `1 unevictable pod on node XXX`.

Investigate the issue by looking at the pods on that node. Check the pods with
`kubernetes describe pod <POD>` to find the problematic pod(s).

If the draining takes more than 5 minutes, you can forcefully drain a node with:

```sh
ksudo drain --ignore-daemonsets --delete-emptydir-data --force <NODE NAME>
```

### pipeline fails because of time-out

The pipeline can fail due to time-out. Often that happens because the node eviction took too much time.

If this happens, you can re-run the failed step of the pipeline.
> **:warning: Important :warning:**
> Make sure that you run the correct step with the **correct :hash:revision**

### pipeline fails on rerun step

You can re-run the whole pipeline from scratch if the state of the cluster is
unhealthy after pipeline failure.

E.g. When you accidentally re-ran a step of the pipeline with the wrong revision.

In that case, create a new PR in the [EKS-provision-configuration repository].
Append a version string like `gen2` or similar to the nodegroup names. (E.g.
`1-24` -> `1-24-gen2`). See [PR #62] for an example.

Then continue the roll out from step 6. Confirm that the pipeline is rerun and
monitor it's logs.

### Vault injected pods not working correctly after upgrade

There is a current issue :construction: Link details :construction:
After the upgrade finished, get a list of *broken* PODs with this command:

```sh
kubectl get pods -A -o jsonpath='{.items[?(@.metadata.annotations.vault\.hashicorp\.com/agent-inject=="true")].metadata.name}' | tr " " "\n" | tee plist.txt
kubectl get pods -A | grep -f plist.txt | grep -v '3/3'
```

*Note: The assumption here is, that normally the number of containers per pod
is 3. This could change in the future, if we inject more/other things*

### Cannot evict pod as it would violate the pod's disruption budget

Nodes may fail to even drain forcefully when you get an error like `Cannot
evict pod as it would violate the pod's disruption budget.`
In this case, delete the pod that gives this error. `kubernetes pod delete <POD>`

### Out-of IP issue

If you are observing a situation when your IPs are exhausted, you need to tweak
your [AWS VPC CNI]

Follow the AWS documentation [about eni and ip target] according to the
`WARM_IP_TARGET` and `MINIMUM_IP_TARGET`.

Battle-tested configuration currently is managed by Flux in the repository, the
example for Agora CI is [here](/infrastructure/k8s/ci/kube-system/daemonset-aws-node.yaml).
So for a modification, you need to do PR or during the actual issue freezes
this `kustomization` by `flux suspend kustomization kube-system` and test
setting and later do PR after resolving the situation.

> **:warning: Important :warning:**
> Remember to NEVER do `kubectl edit` and always do a backup of the manifest to your operation directory !!!

### Kube-proxy broken

If after running the pipeline you are observing `ImagePullBackOff` errors on
`kube-proxy` PODs you must manually patch the `DaemonSet` as soon as possible,
any delays can destabilize your environment and this could escalate so quickly.

1. Check which image is accurate at [AWS Kube-Proxy Documentation]

2. In our situation, we are looking at the `default type`

3. Patch `DaemonSet` by command:

   ```bash
   kubectl set image -n kube-system daemonset/kube-proxy kube-proxy=602401143452.dkr.ecr.ap-northeast-1.amazonaws.com/eks/kube-proxy:TAG-FROM-DOCUMENTATION
   ```

### Auto Scaling groups not cleaned up after update

For unknown reason it can happen that the *Auto Scaling group* for the old version's nodegroup is not cleaned up by the pipeline.
You can easily see the version associated to the *Auto Scaling group* because it contains the kubernetes version (see PR).
Check this via the AWS Management console (EC2 -> Auto Scaling group [bottom left]).

To remove the *Auto Scaling group* edit the max/min number of pods via the UI.

### Controllers' unevictable pods in `knative-serving` namespace

**NOTE:** This issue has been fixed in the newer KNative Serving (`v1.9.4` and `v1.10.2`). However, these versions can be installed on K8S 1.24+.

For the official release file, there are 6 KNative Serving controllers' pods annotated with `cluster-autoscaler.kubernetes.io/safe-to-evict: "false"`.
We can manually restart the deployments as listed below or delete associated pods.

- activator
- autoscaler
- domainmapping-webhook
- net-certmanager-webhook
- net-istio-webhook
- webhook

Refer to [KNative Serving safe-to-evict issue] for more details.

### Slow termination of KServe InferenceService's pods

**NOTE:** The pod name pattern is `<service_name>-predictor-default-0000X-deployment-<hash>`.

Terminating InferenceService's pods take up to 5 minutes, since they need to wait for draining all requests.
Those pods will be automatically deleted, after they time out. By default, the timeout is set to 5 minutes.
We can decrease this value by explicitly setting it in a InferenceService manifest file or update Knative configmap `config-defaults` at `revision-timeout-seconds` key.

Refer to [KServe slow termination issue] for more details.

<!-- Below are the links used in the document -->
[PR #62]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/pull/62/files
[#wcm-agora-announcement]:https://woven-by-toyota.slack.com/archives/C04PUC88KUH
[093116320723]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/tree/master/eks/093116320723/ap-northeast-1/dev
[370564492268]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/tree/master/eks/370564492268/ap-northeast-1/dev
[835215587209]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/tree/master/eks/835215587209/ap-northeast-1/dev
[AWS Kube-Proxy Documentation]:https://docs.aws.amazon.com/eks/latest/userguide/managing-kube-proxy.html
[AWS VPC CNI]:https://github.com/aws/amazon-vpc-cni-k8s
[Agora Infra Stories Board]:https://wovencity.monday.com/boards/3891277770/views/96006295
[CSI Drivers]:https://kubernetes-csi.github.io/docs/drivers.html
[CloudFormation Stacks]:https://ap-northeast-1.console.aws.amazon.com/cloudformation/home?region=ap-northeast-1#/stacks
[EKS Control Plane]:https://ap-northeast-1.console.aws.amazon.com/eks/home?region=ap-northeast-1#/clusters
[EKS-provision-configuration repository]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/
[Fargate]:https://aws.amazon.com/fargate/
[IAM Roles]:https://us-east-1.console.aws.amazon.com/iamv2/home?region=ap-northeast-1#/roles
[KubePug]:https://github.com/rikatz/kubepug
[Kubernetes Git Repo]:https://github.com/kubernetes/kubernetes
[Mathieu sauve-frankel]:mailto:mathieu.sauve-frankel@woven-planet.global
[PR for upgrading the LAB Cluster to 1.22]:https://github.tri-ad.tech/cityos-platform/EKS-provision-configuration/pull/41/files
[Pluto]:https://github.com/FairwindsOps/pluto
[Portal Azure]:http://portal.azure.com/
[Woven AWS Apps]:https://woven.awsapps.com/start#/
[about eni and ip target]:https://github.com/aws/amazon-vpc-cni-k8s/blob/master/docs/eni-and-ip-target.md
[eksctl documentation about updating addons]:https://eksctl.io/usage/addons/#updating-addons
[eksctl]:https://github.com/weaveworks/eksctl/blob/main/README.md#installation
[wcm-servicemsh-k8s-deployment concourse pipeline]:https://concourse.tri-ad.tech/teams/woven-city/pipelines/wcm-servicemesh-k8s-deployment
[KNative Serving safe-to-evict issue]:https://github.com/knative/serving/issues/13984
[KServe slow termination issue]:https://github.com/kserve/kserve/discussions/2339#discussioncomment-3162630
