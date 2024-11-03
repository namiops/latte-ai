# Example Grafana Dashboard

This directory contains an example of how to deploy a dashboard into Grafana in
the following environments:

* Lab2 (Note: this environment is for Agora Team only)
* Preprod

## How to Use

Teams are free to copy/paste these files into their configuration. The JSON file
provided generates a "general" dashboard with the following alerts attached

* Ready Status
* Pods Up
* CPU and Memory Usage

# Modifying the files

Teams are free to modify these files inside their own infrastructure folders.
Please **DO NOT** change these files directly if you wish to use them. First
copy them to the desired location and then change them.

## Tips on Modifying the JSON

For the JSON the easiest way to change it is by going to
the [dashboard](https://athena.agora-dev.w3n.io/grafana/dashboards) and then
doing the following

1) Perform the modifications to the panels, graphs, or PromQL queries made to
   your team's desired state
2) Go to `Dashboard Settings` and then in the left sidebar select `JSON Model`
3) Copy the JSON Model to your clipboard
4) Paste the JSON Model to your dashboard JSON in your project's infra folders

This will persist and modify the dashboard to your team's desired state