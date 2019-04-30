# comsol-builder
This repository is intended to automate the construction of COMSOL models of semiconductor quantum dot devices.

To build the example, run:

1. Create .class files for comsolbuilder package:
`javac -d . -cp "/Applications/COMSOL54/Multiphysics/plugins/*" -source 8 -target 8 src/*`

2. Create .class file for example:
`javac -cp "/Applications/COMSOL54/Multiphysics/plugins/*":"." -source 8 -target 8 examples/from_dxf/test_device.java`

3. Create .jar file for comsolbuilder package:
`jar -cvf comsolbuilder.jar comsolbuilder/*`

4. Create .mph file: 
`/Applications/COMSOL54/Multiphysics/bin/comsol batch -dev /path/to/repo/comsol-builder/comsolbuilder.jar -inputfile /path/to/repo/comsol-builder/examples/from_dxf/test_device.class -outputfile /path/to/repo/comsol-builder/examples/from_dxf/test.mph`
