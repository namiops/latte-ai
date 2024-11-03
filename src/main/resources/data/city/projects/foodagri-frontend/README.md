### Yott

## How to setup `lint-staged` ?

https://github.tri-ad.tech/R-D-WCM/wcm-backend-monorepo/pull/1731#issue-318605

## Checking if your k8s files are valid before applying to cluster

`kubectl apply -k . --dry-run=client -o yaml`

## Copying k8s secrets from one namespace to another

`kubectl get secret redis-secret --namespace=foodagri -o yaml | sed 's/namespace: .*/namespace: foodagri-qa/' | kubectl apply -f -`

## Decode k8s secrets

`kubectl get secret -n foodagri <SECRET-NAME> -o go-template='
{{range $k,$v := .data}}{{printf "%s: " $k}}{{if not $v}}{{$v}}{{else}}{{$v | base64decode}}{{end}}{{"\n"}}{{end}}'`

## How to run Storybook

```bash
npm run storybook
```

## Do something for linking Big Query to Google Analytics

```bash
export TRINO_MANIFESTS_DIR=infrastructure/k8s/common/foodagri/foodagri-dev-trino # assuming the current dir is set to root of the city repo

export EKS_OIDC_PROVIDER=https://oidc.eks.ap-northeast-1.amazonaws.com/id/6650625D283964F0BAC2BC1AE56FE0E9
export TRINO_NAMESPACE=foodagri-dev
export TRINO_SERVICE_ACCOUNT=trino-sa

export GCP_PROJECT_NUMBER=1087773809748
export GCP_PROJECT_ID=wp-dev-wovenec-waow
export GCP_SERVICE_ACCOUNT=wp-dev-wovenec-waow-sa

export GCP_POOL_ID=agora-eks-dev2-pool # The unique ID for the GCP workload identity pool
export GCP_PROVIDER_ID=agora-eks-dev2-oidc # A unique workload identity pool provider ID of your choice
export GCP_SERVICE_ACCOUNT_EMAIL=$GCP_SERVICE_ACCOUNT@$GCP_PROJECT_ID.iam.gserviceaccount.com


gcloud iam workload-identity-pools create $GCP_POOL_ID \
    --location="global" \
    --description="Idenity pool for the federation with Agora Dev2 EKS" \
    --display-name="Agora Dev2 EKS Federation Pool"
gcloud iam workload-identity-pools providers create-oidc $GCP_PROVIDER_ID \
    --location="global" \
    --workload-identity-pool="$GCP_POOL_ID" \
    --issuer-uri="$EKS_OIDC_PROVIDER" \
    --attribute-mapping="google.subject=assertion.sub" \
    --attribute-condition="assertion['kubernetes.io']['namespace'] in ['$TRINO_NAMESPACE']"
gcloud iam service-accounts add-iam-policy-binding $GCP_SERVICE_ACCOUNT_EMAIL \
    --role=roles/iam.workloadIdentityUser \
    --member="principal://iam.googleapis.com/projects/$GCP_PROJECT_NUMBER/locations/global/workloadIdentityPools/$GCP_POOL_ID/subject/system:serviceaccount:$TRINO_NAMESPACE:$TRINO_SERVICE_ACCOUNT"
gcloud iam workload-identity-pools create-cred-config \
    projects/$GCP_PROJECT_NUMBER/locations/global/workloadIdentityPools/$GCP_POOL_ID/providers/$GCP_PROVIDER_ID \
    --service-account=$GCP_SERVICE_ACCOUNT_EMAIL \
    --credential-source-file=/var/run/service-account/token \
    --credential-source-type=text \
    --output-file=$TRINO_MANIFESTS_DIR/bigquery-cred-config.json
```

```bash
## If using the same identity pool as dev1 (it might not be recommended but simpler?):
export EKS_OIDC_PROVIDER=https://oidc.eks.ap-northeast-1.amazonaws.com/id/6650625D283964F0BAC2BC1AE56FE0E9 # Dev2 oidc provider
export TRINO_NAMESPACE=foodagri-dev
export TRINO_SERVICE_ACCOUNT=trino-sa
export GCP_PROJECT_NUMBER=1087773809748
export GCP_PROJECT_ID=wp-dev-wovenec-waow
export GCP_SERVICE_ACCOUNT=wp-dev-wovenec-waow-sa

export GCP_POOL_ID=agora-eks-dev-pool # The unique ID for the GCP workload identity pool
export GCP_PROVIDER_ID=agora-eks-dev-oidc # A unique workload identity pool provider ID of your choice
export GCP_SERVICE_ACCOUNT_EMAIL=$GCP_SERVICE_ACCOUNT@$GCP_PROJECT_ID.iam.gserviceaccount.com

gcloud iam workload-identity-pools providers create-oidc $GCP_PROVIDER_ID \
    --location="global" \
    --workload-identity-pool="$GCP_POOL_ID" \
    --issuer-uri="$EKS_OIDC_PROVIDER" \
    --attribute-mapping="google.subject=assertion.sub" \
    --attribute-condition="assertion['kubernetes.io']['namespace'] in ['$TRINO_NAMESPACE']"
gcloud iam service-accounts add-iam-policy-binding $GCP_SERVICE_ACCOUNT_EMAIL \
    --role=roles/iam.workloadIdentityUser \
    --member="principal://iam.googleapis.com/projects/$GCP_PROJECT_NUMBER/locations/global/workloadIdentityPools/$GCP_POOL_ID/subject/system:serviceaccount:$TRINO_NAMESPACE:$TRINO_SERVICE_ACCOUNT"
```
