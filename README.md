# comsol-builder
This repository is intended to automate the construction of COMSOL models of semiconductor quantum dot devices.

## Overview

![overview](https://raw.githubusercontent.com/adamfrees/comsol-builder/master/documentation/overview.png)

The comsol-builder application is designed to automatically generate a COMSOL .mph file, given a 2D electrode design in a .dxf file. This application can either include or omit an intermediate step of rendering the electrodes in 3D before importing into COMSOL.

## Without pre-rendering (no FreeCAD/STL)

This method is more light-weight but potentially less accurate. It is best used in cases in which there are no overlapping electrodes (such as in a "stadium-style" device), or for building simple prototyping models. This allows for a simple extrusion of 2D electrode designs.

An example of this process can be found in `examples/from_dxf/test_device.java`. To build this example on Linux/MacOS, after set-up run:

`./generate_mph examples/from_dxf/test_device.java`

from the `comsol-builder` directory.

### Set-up

The comsol-builder application requires an active [COMSOL installation](https://www.comsol.com/product-download) and a [standard installation of Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html). Note that COMSOL requires the purchase of a license.

**(Linux/MacOS):** make sure that the COMSOL path in `generate_mph` is correct. The default is `comsolPath="/Applications/COMSOL54/Multiphysics"`, which should be correct for COMSOL version 5.4 running on MacOS.

### Building COMSOL file (Linux/MacOS)

For Linux and MacOS, a COMSOL file can be generated from a 2D DXF file by running from this repository `./generate_mph /path/to/file/javaFile.java`, where `javaFile.java` is a Java file with some information about the device's materials and heterostructure. An example Java file can be found at `examples/from_dxf/test_device.java` in this repository.

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

An example of this last process (.stl->.mph) can be found in `examples/from_stl/test_device.java`. To build this example on Linux/MacOS, after set-up run:

`./generate_mph examples/from_stl/test_device.java`

from the `comsol-builder` directory.

### Set-up

**The steps required to set up comsol-builder without pre-rendering are required for comsol-builder with pre-rendering as well.** Here, we describe the steps required to set up the pre-rendering steps.

First, install Microsoft's [QMT Repository](https://github.com/microsoft/qmt/). The easiest method to do this is to use Anaconda/Miniconda; instructions on how to install using this method can be found [here](https://github.com/Microsoft/qmt/wiki/Conda-Setup).

Installing QMT also installs the application FreeCAD. After installing, copy the FreeCAD Macro in this repository `utilities/dxf2Sketch.FCMacro` into the FreeCAD Macro folder. To find the active folder, in FreeCAD go to the menu option `Macro->Macros...`. The default folder on MacOS is `~/Library/Preferences/FreeCAD/Macro`.

### Building COMSOL file

Creating a .mph file from a .dxf file using this method consists of three steps: .dxf->.FCStd, .FCStd->.stl, and .stl->.mph.

#### .dxf -> .FCStd

Both of these file types describe 2D electrode geometries. While .dxf files are a commonly-used 2D CAD format, QMT requires FreeCAD files (.FCStd) as input.

To perform this transformation, modify the values of `dxfFilename` and `saveFilename` in the `dxf2Sketch.FCMacro` file located in the FreeCAD Macro folder. These values should reflect the desired input and output filenames.

Next, in FreeCAD go to the menu option `Macro->Macros...`, select `dxf2Sketch.FCMacro` and click "Execute".

#### .FCStd -> .stl

While a .FCStd file describes a 2D geometry, a .stl file is a simple 3D mesh. To find the correct 3D geometry of the electrodes, we use the QMT repository. An example of how to perform this transformation can be found [in the repository](https://github.com/microsoft/qmt/tree/master/examples/quantum_dot_device). If this example is no longer working, please contact [Adam Frees](mailto:adam.j.frees@gmail.com).

#### .stl -> .mph

The final step of constructing a COMSOL file can be performed using the comsol-builder application. Just as in the "without pre-processing" case, a COMSOL file can be generated by running `./generate_mph /path/to/file/javaFile.java`, where `javaFile.java` is a Java file with some information about the device's materials and heterostructure. An example Java file can be found at `examples/from_stl/test_device.java` in this repository.

