# comsol-builder
This repository is intended to automate the construction of COMSOL models of semiconductor quantum dot devices.

## Overview

![overview](https://raw.githubusercontent.com/adamfrees/comsol-builder/master/documentation/overview.png)

The comsol-builder application is designed to automatically generate a COMSOL .mph file, given a 2D electrode design in a .dxf file. This application can either include or omit an intermediate step of rendering the electrodes in 3D before importing into COMSOL.

## Getting started

As indicated in the diagram above, there are two use-cases for the comsol-builder application: [with](https://github.com/adamfrees/comsol-builder/wiki/With-pre-rendering-(FreeCAD-STL)) and [without](https://github.com/adamfrees/comsol-builder/wiki/Without-pre-rendering-(no-FreeCAD-STL)) a pre-rendering step.

The pre-rendering step introduces more complications into the simulation, however it more accurately reflects the physical geometry of a semiconductor quantum device, particularly those made with overlapping layers of electrodes.

It is **highly recommended** for first-time users to start by building a model without the pre-rendering step. Instructions on setting up and running the application for this use-case can be found [here](https://github.com/adamfrees/comsol-builder/wiki/Without-pre-rendering-(no-FreeCAD-STL)).

Instructions on seting up and performing the pre-rendering steps can be found [here](https://github.com/adamfrees/comsol-builder/wiki/With-pre-rendering-(FreeCAD-STL)).

Documentation for the Java comsol-builder package can be found at [https://adamfrees.github.io/comsol-builder/](https://adamfrees.github.io/comsol-builder/).
