# Postgres インスタンスのリストア方法について

このドキュメントでは、Point-in-Time Recovery (PITR) を使って稼働中の Postgres インスタンスに復元する方法について説明します。
また、リストア方法の説明は [official Postgres operator documentation](https://access.crunchydata.com/documentation/postgres-operator/latest/tutorial/disaster-recovery/)の代わりではありません。そのため、必ずそちらをご確認ください。
検証時のバージョンは5.2.1です。

## Point-in-Time Recovery (PITR) を使って稼働中の Postgres インスタンスに復元

※前提条件として、バックアップが実行されている必要があります。
```
postgresql-pgbackrest-repo1-full-27858693-lqmfv          0/2     Completed   0          85m
postgresql-pgbackrest-repo1-full-27858753-2z9bw          0/2     Completed   0          25m
postgresql-pgbackrest-repo1-incr-27858720-rsb2r          0/2     Completed   0          58m
```

以下のコマンドで、利用可能なバックアップを取得することが出来ます。
PGO v4では[pgo show backup](https://access.crunchydata.com/documentation/postgres-operator/4.1.2/operatorcli/cli/pgo_show_backup/)が利用出来ましたが、PGO v5 では pgo クライアントコマンドが廃止されましたので、kubectlを利用して取得しています。
```
kubectl -n id get pod --sort-by=.status.startTime -l 'postgres-operator.crunchydata.com/pgbackrest-cronjob in (full, incr)' -o json | jq -r '.items[] |"Name:\(.metadata.name) BackupTypes:\(.metadata.labels."postgres-operator.crunchydata.com/pgbackrest-cronjob") Timestamp:\(.status.startTime) Phase:\(.status.phase)"'
```

稼働中のPostgreSQLクラスタを以前の時点にリストアするには、既存のリソースに `spec.backups.pgbackrest.restore` を設定します。既存のデータディレクトリでリストアが実行されると、クラスタは停止して再作成されます。一時的にアクセスできなくなる期間があることに注意してください。
たとえば、2022-12-16 14:00:00の時点にリストアするには、次の手順を実行します。
まず、PostgresClusterリソースを構成するYAMLファイルpostgres.yamlを編集して適用します。
この時点では、リストアは実行されません。

```
$ vi postgres.yaml
(省略)
spec:
  backups:
    pgbackrest:
      restore:
        enabled: true
        repoName: repo1
        options:
        - --type=time
        - --target="2022-12-16 14:00:00+09"
(省略)

$ kubectl apply -f postgres.yaml
```

次に、PostgresCluster のアノテーションを設定します。
PostgresClusterアノテーションが設定されると、リストアが実行されます。
2回目以降は、アノテーションのNoを増やします。
クラスタに入力されたアノテーションの数より多くない場合は実行されません。

```
$ kubectl annotate postgrescluster postgres  -n postgres --overwrite \
  postgres-operator.crunchydata.com/pgbackrest-restore=id1
```

※リストアを実行すると、jobが起動し、Pod:postgresql-pgbackrest-restore-xxxxxが開始されます。ただ、Pod内のistio-proxyコンテナが終了しないので、以下のコマンドを実行する必要があります。

```
$ kubectl -n postgres exec -it postgresql-pgbackrest-restore-xxxxx -c istio-proxy -- curl -fsI -X POST http://localhost:15020/quitquitquit
```

リストア完了後、リストア設定を無効にします。
optionsとrepoNameは不要になったので削除します。

```
$ vi postgres.yaml
(省略)
spec:
  backups:
    pgbackrest:
      restore:
        enabled: false
(省略)

$ kubectl apply -f postgres.yaml
```

Fluxでリソースを管理している場合、これらの作業を行う前にFluxの一時停止が必要です。

```
# 対象のnamespaceのリソースを一時停止する。
flux suspend kustomization postgres

# 作業終了後、対象のnamespaceリソースを再開する。
flux resumue kustomization postgres
```
