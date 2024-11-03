# Cleanup

## Remove everything

```sh
terraform apply -destroy
```
__Note: The `Log group` will not be cleaned up by TF. Either clean it up
manually, or rename `aws_cloudwatch_log_group` on the next run__

