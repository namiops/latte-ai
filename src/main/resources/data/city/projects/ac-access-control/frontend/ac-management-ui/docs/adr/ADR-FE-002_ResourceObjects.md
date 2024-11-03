# ADR-FE-0002 Resource Objects

| Status | Last Updated |
|---|---|
|Approved| 2024-09-24 |

## Context and Problem Statement

In Ac management UI, it is not clear how resource objects are managed.

This ADR summarizes the details of the resource object

### Preconditions

- We use the [react konva](https://konvajs.org/docs/react/index.html) library for manipulate figure.
- The pages to which the resource object relates are
  - FloorDetail Page
    - Editing and browsing objects
  - EquipmentMonitoring Page
    - Browsing objects（resource status indication）

## Decision Outcome

### About Objects

- When editing resource objects, the following images can be drawn, resized and rotated by clicking and dragging. (Asset detail is [here](https://www.figma.com/design/tDc7pbt54I3e9w9qqbhcLZ/%5BFE%5D-Resource-Objects-Image?m=auto&t=JkChPnuQjqwVub3R-6).)
- When selecting a resource, if the resource has a device whose direction is Enter or Exit, the image is replaced with a Bidirectional image (if not selected, the image is Unidirectional).

|Bidirectional|Unidirectional|
|---|---|
|<img src="../../src/assets/resource_object_bidirectional.svg" width="150">|<img src="../../src/assets/resource_object_unidirectional.svg" width="150">|

### Management

- When saving the image, the upper left corner of the image is used as the reference point and the X,Y coordinates and their Width, Height and Rotation information are sent to the BE for management.

## Note
- 2024-09-25 : Approved
- 2024-09-24 : Drafted, Originator: Masamitsu Sugimoto