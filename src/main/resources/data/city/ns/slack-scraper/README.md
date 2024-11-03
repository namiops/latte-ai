# Slack Scraper

## Overview

This application is a small Slack Scraper that scrapes and collects messages
from a provided Slack Channel. This application uses
the [Slack APIs](https://api.slack.com/methods/conversations.info)
and can be used for further reference.

## How to Use

The Scraper works as follows:

```shell
bazel run //contrib/slack-scraper/src/main/java/global/wovencity:SlackScraper [-- <Conversation ID> <Num of Messages>]
```

The scraper can take in command-line arguments which can be declared by use
of `--`

* Conversation ID -
  The [Slack Conversation ID](https://api.slack.com/docs/conversations-api#conversations_api).
* Num of Messages - The Limit of messages to print out

The output is sent to a CSV (`.csv`) for the purposes of being able to further
process the scraped messages. The output will be sent to the following directly
in your local box via Bazel

```
city/bazel-bin/contrib/slack-scraper/src/main/java/global/wovencity/SlackScraper.runfiles/
```

## Suggestions or Contributions

If you like this tool or want to provide feedback please feel free to leave a
comment, or make a PR and add to this project. 