# Monorobot

## What is Monorobot?

Monorobot is a tool that allows developers to configure and customize slack notifications in the
context of a monorepo. We use this tool because the slack integration provided by Github does not
work well with monorepos. Monorobot allows developers to customize notifications based on pathing,
labels, and Github Event type. 

## How to use Monorobot

All of the monorobot configurations are stored in the [.monorobot.json](/.monorobot.json) file at
the root of the repo. To add any configurations, please modify this file.

Instructions on how to modify the file can be found [here](https://github.com/ahrefs/monorobot/blob/master/documentation/config_docs.md).

In addition, if you want to set up notifications for a specific channel, please add the `WCM-Monorobot`
Slack App to the channel. This will allow the App to send messages to your channel. To do this, go
to the channel settings, click `Integrations` and then `Add an App`. Afterwards, search for 
`WCM-Monorobot`, and add the app.