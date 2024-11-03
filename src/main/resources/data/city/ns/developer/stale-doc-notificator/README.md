# Stale Doc Notifier

This project is for running monthly health check for documentations hosted by [Developer Portal](https://developer.woven-city.toyota/), finding all the stale docs (no updates for 6 months) and sending Slack notification to its owner.

## Development

1. Add environment variables

```shell
# change this to path to mono repo on your PC
export PATH_TO_REPO=~/city
# whether you want to send slack msg
export ENABLE_MSG=true
# slack bot user token used for sending msg, ignore this if you don't wish to send message
export SLACK_BOT_TOKEN=token
# when set to true, msg will be sent to test channel, or it will be sent to real team channel
export TEST_ENV=true
```

2. Build the python binary with Bazel

```shell
bazel build //ns/developer/stale-doc-notificator/src:src_bin
```

3. Run the python binary with Bazel

```shell
bazel run //ns/developer/stale-doc-notificator/src:src_bin
```

If you use the same environment variable as step 1, the script will be able to go through your local city repo, find stale docs and send slack message to the test channel.

### Note

You can find the Slack bot token in OnePassword. The vault's name is Agora Devrel and the Secret Note is called Stale Doc Detector Secrets.

If you can't find the previous test channel (it's a private one), you can create your own test channel, replace the `TEST_CHANNEL_ID` with yours in `utils/slack_msg.py`, and invite the bot into your test channel (search for bot called `Developer Portal`). 

### Dependencies

If you wish to add a new dependency and it's not in `py_requirements.in` under the root directory of city monorepo, you should follow [this documentation](https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/python/#adding-a-package) to add it.

You can also use github action for adding new python dependency, for more details please reach out to build team.

## Deploy

The deployment config is under `infrastructure/k8s/environments/lab2/clusters/worker1-east/developer-portal/stale-doc-notificator`.

## .dochealthignore

By adding a `.dochealthignore` in the docs folder, you can ignore certain files or directories under docs folder. The syntax and rule is the same with `.gitignore`.
