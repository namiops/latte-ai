# Authoring Portal

## Overview

The Map Authoring Portal provides a user interface to build maps and Geodata, ie Point-of-Interests (POIs)/Area. Business users can create, read, update and delete without any technical assistance from the IT team.

This page guides you how to create a map using the Portal. 

## Map Structure

The Map Services organize the map using project, layer and place. In order to create a map. You need to create these 3 components either in Map Admin Portal or API. 

There are 5-level structures to compile a map.

| Structure | Description   | Example                                                                      |
|-----------|--------|-------------------------------------------------------------------------------------|
| Project   | The top level of a map, contains metadata for a place and layers | Woven City Restaurant Map |
| Layer     | A logical grouping of Places | Japanese Restaurant, Café |
| Place     | The actual Point-of-Interest (POI), contains metadata for a place and user reviews | Restaurant A, Café B |
| Review    | User review for a place  | Review content: Good Restaurant A |
| Media     | User upload for an image  | Image for a place |


[See internal hierarchy for details](./02_internal_hierarchy.md)

## Permission for User Role and Resource

| Role | Project | Layer | Place | Review | Media |
|------|---------|-------|-------|-------|--------|
| Super Admin                  | CURD | CURD | CURD | CURD | CURD |
| User in resource Admin list  | UR | CURD | CURD | CURD | CURD |
| User in resource Editor list | R | UR | UR | UR | UR |
| User in resource Viewer list | R | R | R | R | R |

## Quickstart

### Before You Start
You need following

* Woven ID, using WbT SSO 
* Network access to Agora

### Sample Scenario

The following will create a Woven restaurant map and put a restaurant A on the map. We use one stage workflow in this example to simply the case.

#### Login
Navigate to the Authoring Portal: https://utility.cityos-dev.woven-planet.tech/map-service/portal
The Portal utilizes the Agora Woven ID to authenticate and authorize user. Users need to use the Woven ID in the login page.

![Login Page](./images/07_authoring_portal/login.png)

---

#### Create Project
After login, the landing page is displayed, the left navigation shows the available projects for the authenticated user.
* Only available for superadmin, Create a Project: Click "ADD PROJECT" on top left hand

![Landing Page](./images/07_authoring_portal/landing.png)

Enter the Project name and Descriptions and click "CREATE", project is created.

![Create Project](./images/07_authoring_portal/createproject.png)

---

#### Create Layer
Click on "ADD LAYER TO PROJECT" under a project

![Add layer to project](./images/07_authoring_portal/addlayertoproject.png)

Enter the Layer Name and Description, then click "CREATE"

![Layer name desc](./images/07_authoring_portal/layernamedesc.png)

---

#### Create Place
Click on "ADD PLACE TO LAYER"

![Add place](./images/07_authoring_portal/addplace.png)

Enter all the metadata for a place and click "CREATE"

![Place metadata](./images/07_authoring_portal/placemetadata.png)

A Place is created

![Place created](./images/07_authoring_portal/placecreated.png)

Place list

![Place list](./images/07_authoring_portal/placelist.png)


Congratulations, you have created project, layer and place are created, so you can

* use the rendering engine to render the map (this could be rendered in Woven Map later)
* use API to retrieve the map information 

---

## Workflow & Workflow Stage
Each project has a workflow and the workflow will apply on all children layers. Each layer has their own lifecycle according to project workflow. 

### Workflow
There are 3 workflow available for a project. Depends on the business requirements, user can select the workflow when creating a project. When create a new layer, the first initial stage will apply to the layer.

After the workflow is selected, the workflow is fix and cannot be changed for a project.

![Project workflow](./images/07_authoring_portal/projectworkflow.png)


| Workflow ID | Name | Description | Flow |
|------|---------|-------|-------| 
| 1 | Single Stage | The default workflow only has one stage, the layer is published without going through any workflow and all the viewer can render the layer.  | ![Single Stage workflow](./images/07_authoring_portal/singlestageworkflow.png) |
| 2 | Three Stage | The default3stages contains 3 workflow stage, ie draft, publish and expire, the viewer can only view and render the layer on the publish stage| ![Three Stage workflow](./images/07_authoring_portal/threestageworkflow.png) |
| 3 | Single Stage | The defaultScheduled3stage contains 3 workflow stage, ie draft, publish and expire. The layer will be published and expired according to the predefined dates by user publish date and expire date | ![Scheduled Three Stage workflow](./images/07_authoring_portal/schedulethreestage.png) |


### Manual Move Workflow Stage

For each Layer, authorized user can move the workflow stage manually.

#### Next Stage/Previous Stage

Click on "Next Stage/Previous Stage" button, Layer moves to next stage or previous Stage

![Manual move stage](./images/07_authoring_portal/manualmovestage.png)
