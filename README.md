# comsol-builder
This repository is intended to automate the construction of COMSOL models of semiconductor quantum dot devices.

## Overview

![overview](https://raw.githubusercontent.com/adamfrees/comsol-builder/master/documentation/overview.png)

The comsol-builder application is designed to automatically generate a COMSOL .mph file, given a 2D electrode design in a .dxf file. This application can either include or omit an intermediate step of rendering the electrodes in 3D before importing into COMSOL.

## Without pre-rendering (no FreeCAD/STL)

This method is more light-weight but potentially less accurate. It is best used in cases in which there are no overlapping electrodes (such as in a "stadium-style" device), or for building simple prototyping models. This allows for a simple extrusion of 2D electrode designs.

### Set-up

The comsol-builder application requires an active [COMSOL installation](https://www.comsol.com/product-download) and a [standard installation of Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html). Note that COMSOL requires the purchase of a license.

### Building COMSOL file (Linux/MacOS)

For Linux and MacOS, a COMSOL file can be generated from a 2D DXF file by running from this repository `./generate_mph /path/to/file/javaFile.java`, where `javaFile.java` is a Java file with some information about the device's materials and heterostructure. An example Java file can be found at `examples/from_dxf/test_device.java` in this repository.

**Before running for the first time:** make sure that the COMSOL path in `generate_mph` is correct. The default is `comsolPath="/Applications/COMSOL54/Multiphysics"`, which should be correct for COMSOL version 5.4 running on MacOS.

`generate_mph` is a bash file which performs 5 steps:

1. `javac -d . -cp "$comsolPath/plugins/*" -source 8 -target 8 src/*`: Compiles (using Java 8) the classes contained in the `src` folder, placing the compiled .class files into a package folder `comsolbuilder`.

2. `javac -cp "$comsolPath/plugins/*":"." -source 8 -target 8 $javaFile`: Compiles (using Java 8) the input Java file, writing the results into a .class file.

3. `jar -cvf comsolbuilder.jar comsolbuilder/*`: Takes the package folder created in step 1 and creates a package file `comsolbuilder.jar`.

4. `$comsolPath/bin/comsol batch -dev $PWD/comsolbuilder.jar -inputfile $PWD/${javaFile::${#javaFile}-5}.class -outputfile $PWD/${javaFile::${#javaFile}-5}.mph`: Uses the `comsol batch` command to run the compiled Java code, and write the results to disk in a COMSOL .mph file.

5. Delete intermediate .class and .jar files.

### Building COMSOL file (Windows)

Bash files do not run on Windows, so the 5 steps outlined in the previous section need to be run individually.

## With pre-rendering (FreeCAD/STL)

This method involves more moving pieces but more accurately captures the geometry of overlapping electrodes. It relies on [FreeCAD](https://www.freecadweb.org/) to reformat the 2D .dxf file into a 2D .FCStd file, and the [QMT Repository](https://github.com/microsoft/qmt/) to render the 2D .FCStd file into a 3D .stl file. The comsol-builder application then imports this .stl file into COMSOL.

### Set-up


## Examples

To build the examples, run:

`./generate_mph examples/from_dxf/test_device.java`

or

`./generate_mph examples/from_stl/test_device.java`

from the `comsol-builder` directory.
