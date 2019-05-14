# comsol-builder
This repository is intended to automate the construction of COMSOL models of semiconductor quantum dot devices.

## Overview

![overview](https://raw.githubusercontent.com/adamfrees/comsol-builder/master/documentation/overview.png)

The comsol-builder application is designed to automatically generate a COMSOL .mph file, given a 2D electrode design in a .dxf file. This application can either include or omit an intermediate step of rendering the electrodes in 3D before importing into COMSOL.

## Without pre-rendering (no FreeCAD/STL)

This method is more light-weight but potentially less accurate. It is best used in cases in which there are no overlapping electrodes (such as in a "stadium-style" device), or for building simple prototyping models. This allows for a simple extrusion of 2D electrode designs.

### Set-up

## With pre-rendering (FreeCAD/STL)

This method involves more moving pieces but more accurately captures the geometry of overlapping electrodes. It relies on [FreeCAD](https://www.freecadweb.org/) to reformat the 2D .dxf file into a 2D .FCStd file, and the [QMT Repository](https://github.com/microsoft/qmt/) to render the 2D .FCStd file into a 3D .stl file. The comsol-builder application then imports this .stl file into COMSOL.

### Set-up


## Examples

To build the examples, run:

`./generate_mph examples/from_dxf/test_device.java`

or

`./generate_mph examples/from_stl/test_device.java`

from the `comsol-builder` directory.
