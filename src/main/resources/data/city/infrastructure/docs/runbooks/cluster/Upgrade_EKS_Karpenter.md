# Upgrade Agora EKS clusters on Karpenter nodes

<!-- vim-markdown-toc GFM -->
- [Upgrade Agora EKS clusters on Karpenter nodes](#upgrade-agora-eks-clusters-on-karpenter-nodes)
  - [Intro](#intro)
  - [Required software](#required-software)
    - [Important resources](#important-resources)
  - [Planning](#planning)
    - [Review the changelog](#review-the-changelog)
    - [Assess compatability](#assess-compatability)
    - [Preparation](#preparation)
  - [Execution of the upgrade](#execution-of-the-upgrade)
  - [After the upgrade](#after-the-upgrade)
    - [After upgrading production/development environment](#after-upgrading-productiondevelopment-environment)
<!-- vim-markdown-toc -->

## Intro

This runbook describes the upgrade procedure for the next-generation Kubernetes clusters using karpenter nodes.

## Required software

- [KubePug]
- [Pluto]
- [Terraform]

### Important resources

- Access to all environments is across AWS SSO, and this is baked by Azure AD
  groups configurable on [Portal Azure] - currently the only person who can
  grant access is [Mathieu sauve-frankel]
- You can access the AWS Console by [Woven AWS Apps]

## Planning

### Review the changelog

Review the [Kubernetes change log] alongside [EKS Kubernetes versions] for any possible impacts, such as breaking changes that may affect the workloads.

### Assess compatability

- Check [karpenter compatability]
- Check [flux compatability]
- Check [gloo mesh compatability]
- Check [cert-manager compatability]
- Check [EBS CSI driver compatability]
- Check [AWS LBC compatability]
- Check [cluster add-Ons compatibility] (if any)

Overall check any other operators which we are using in the target environment. The above are critical and are a MUST. There could be operators not listed above, please add them if discovered.

### Preparation

1. Create a Monday.com Story in the [Agora Infra Stories Board] for the upgrade
    - Assign a *Shadow Engineer* to verify that all steps are done correctly by
      the *Primary Engineer*.
    - Assign a *Coordinator* to check that all steps are followed as described in
      this runbook.
    - Put all updates regarding the upgrade into the Monday.com Story.
2. Again, check for any incompatibilities, deprecations and **deletions**  with [KubePug] and [Pluto].
    - Notify affected teams prior to scheduling the upgrade.
    - KubePug example:

      ```sh
      kubectl krew install deprecations
      alias kdeprecationsudo='kubectl deprecations --as-group aad:0f158ca2-948a-4d79-83b1-f21380bd16aa --as sudo'
      kdeprecationsudo --k8s-version v1.25.15 -v info
      ```

      Note:
      - Get the right k8s-version from the tags of the [Kubernetes Git Repo]
      - Make sure there is no Deleted APIs, otherwise these resources will not be
        deployed after upgrade
    - Pluto example:

      ```sh
      brew install FairwindsOps/tap/pluto
      pluto detect-all-in-cluster -o wide  --target-versions k8s=v1.25.15 2>/dev/null
      ```

      Note:
      - Make sure there is no resources that will be removed (defined in
        `REMOVED` field)
3. Again, Read the notifications on the EKS page in the AWS account to see if there
   are any breaking changes we should be aware of.
4. Verify the Karpenter Drift feature gate is enabled:

      ```sh
      kubectl -n karpenter get deployments.apps karpenter -o yaml | grep "Drift" -B1
      ```

5. After verifying the above actions, schedule the upgrade and send an announcement
    - We do Kubernetes upgrades during office hours and we depend on support. Therefore schedule to start at after 14:00 and be finished by 17:00 JST.
    - Send an announcement to [#wcm-agora-announcement]. [Template](upgrade_kube_template.md)

## Execution of the upgrade

1. Take note of the current cluster situation by issuing the following commands
   and appending the output to the Monday.com item.
    - `kubectl get nodes -o wide | tee  nodes-wide.out` - Dump state of current nodes
    - `kubectl describe nodes | tee nodes-desc.out` - Dump information about current nodes
    - `kubectl get pod -o wide --all-namespaces | tee pod-assign.out` - Dump information on which POD is on which node
2. Create a PR to update the terraform in the target cluster. For example: <https://github.com/wp-wcm/city/pull/11847>
3. Copy the terraform plan from our [protected workflows] and include it in the PR for reviewers.
4. After the PR is merged and terraform applied (check [protected workflows] for the target environment), you will observe the following effects:
    - EKS managed initial nodegroup gets replaced first.
    - For Kubernetes nodes provisioned with Karpenter, Karpenter provisions new nodes first, evicts pods from the old nodes, and then terminates.
    - Karpenter will mark its nodes as drifted and start replacing the nodes.

## After the upgrade

### After upgrading production/development environment

1. Development tools in the repository should use compatible versions. For example:
   - [k8s-tools](https://github.com/wp-wcm/city/tree/11b8302c810fbc6f6d214765b0ace7fb98dcd3fd/tools/k8s-tools)
   - [kubebuilder](https://github.com/wp-wcm/city/blob/24dc56c4faa6e9cb30529e4dc5edc6bf16e20bd5/WORKSPACE#L1118)
2. Communicate to ensure affected teams migrate to compatible tools (if any).

<!-- Below are the links used in the document -->
[#wcm-agora-announcement]:https://tri-ad-global.slack.com/archives/C04PUC88KUH
[Agora Infra Stories Board]:https://wovencity.monday.com/boards/3891277770/views/96006295
[KubePug]:https://github.com/rikatz/kubepug
[Kubernetes Git Repo]:https://github.com/kubernetes/kubernetes
[Terraform]:https://developer.hashicorp.com/terraform/install
[Mathieu sauve-frankel]:mailto:mathieu.sauve-frankel@woven-planet.global
[Pluto]:https://github.com/FairwindsOps/pluto
[Portal Azure]:http://portal.azure.com/
[Woven AWS Apps]:https://woven.awsapps.com/start#/
[protected workflows]:https://github.com/wp-wcm/protected-workflows/actions/workflows/
[karpenter compatability]:https://karpenter.sh/preview/upgrading/compatibility/#compatibility
[flux compatability]:https://fluxcd.io/flux/installation/#prerequisites
[gloo mesh compatability]:https://docs.solo.io/gloo-mesh-enterprise/latest/reference/version/versions/
[cert-manager compatability]:https://cert-manager.io/docs/releases/#currently-supported-releases
[EBS CSI driver compatability]:https://github.com/kubernetes-sigs/aws-ebs-csi-driver#compatibility
[AWS LBC compatability]:https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.XX/deploy/installation/#supported-kubernetes-versions
[cluster add-Ons compatibility]:https://docs.aws.amazon.com/eks/latest/userguide/managing-add-ons.html#updating-an-add-on
[EKS Kubernetes versions]:https://docs.aws.amazon.com/eks/latest/userguide/kubernetes-versions-standard.html
[Kubernetes change log]:https://github.com/kubernetes/kubernetes/tree/master/CHANGELOG
