package comsolbuilder;

import com.comsol.model.*;
import com.comsol.model.util.*;
import java.io.File;

/**
* An instance of the ComsolBuilder class is an object
* which can build a COMSOL model. The purpose of This
* class is to abstract away the nasty COMSOL API calls.
*
* @author  Adam Frees
*/
public class ComsolBuilder {
  /**
  * COMSOL model that will be saved as a .mph file.
  */
  public Model model;
  /**
  * Length (in nm) along the x direction.
  */
  public String xDim;
  /**
  * Length (in nm) along the y direction.
  */
  public String yDim;
  /**
  * Height (in nm) along the z direction.
  */
  public Double currentHeight;

  /**
  * The constructor for the ComsolBuilder object.
  * @param xDimInput Length (in nm) along the x direction.
  * @param yDimInput Length (in nm) along the y direction.
  */

  public ComsolBuilder(Double xDimInput, Double yDimInput) {
    model = ModelUtil.create("Model");
    model.modelNode().create("comp1");
    model.geom().create("geom1", 3);
    model.geom("geom1").lengthUnit("nm");
    xDim = Double.toString(xDimInput);
    yDim = Double.toString(yDimInput);
    currentHeight = 0.;

  }

  /**
  * Adds layers to comsol model.
  * @param layerLabels Labels of layers used.
  * @param layerHeights Height (in nm) of each layer.
  */

  public void addHeterostructure(String layerLabels[], String layerHeights[]){
    model.component("comp1").geom("geom1").selection().create("SiGeSel", "CumulativeSelection");

    for (int i = 0; i<layerLabels.length; i++) {
      model.geom("geom1").feature().create(layerLabels[i], "Block");
      model.geom("geom1").feature(layerLabels[i]).set("size", new String[]{xDim, yDim, layerHeights[i]});
      model.geom("geom1").feature(layerLabels[i]).set("pos", new String[]{"0.", "0.", Double.toString(currentHeight+Double.parseDouble(layerHeights[i])/2.)});
      model.geom("geom1").feature(layerLabels[i]).set("base", "center");
      model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
      if (i==0 || i==2) { //TODO: This only works for SiGe/Si/SiGe heterostructures!
        model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
        model.geom("geom1").feature(layerLabels[i]).set("contributeto", "SiGeSel");
      }
      currentHeight += Double.parseDouble(layerHeights[i]);
    }
  }

  /**
  * Imports and extrudes DXF File
  * @param dxfFile Path to dxf file (cannot be a relative path).
  * @param dxfLayers Names of layers in dxf file, in the order that they should be built.
  * @param dxfLayerHeights Height of each layer.
  * @param scale Factor to scale DXF file (e.g. 1000 to transfer from um to nm).
  */

  public void addElectrodesDXF(String dxfFile, String dxfLayers[], Double dxfLayerHeights[], Double scale){
    int extrudeCount = 0;
    model.component("comp1").geom("geom1").selection().create("ElectrodeSel", "CumulativeSelection");
    /*
    * These nested loops create the layers multiple times. We create the top-most
    * layer first by creating all of the other layers and subtracting them from
    * the top-most. This process is repeated for the second top-most layer and so
    * on until all layers are formed.
    */
    for (int i = dxfLayers.length; i>0; i--) {
      for (int j = 1; j<=i; j++) {
        extrudeCount++;

        //Create workplane
        model.geom("geom1").create("wp"+extrudeCount, "WorkPlane");
        model.geom("geom1").feature("wp"+extrudeCount).set("quickz", currentHeight-dxfLayerHeights[dxfLayerHeights.length - 1]);
        //Import correct layer of dxf file
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("imp1", "Import");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("filename", dxfFile);
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layerselection", "selected");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layers", new String[]{dxfLayers[j-1]});
        //Scale electrodes
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("sca1", "Scale");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").set("isotropic", scale);
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").selection("input").set("imp1");
        //Trim outside of xDim / yDim.
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("r1", "Rectangle");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("base", "center");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("size", new String[]{xDim, yDim});
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("int1", "Intersection");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("int1").selection("input").set("r1", "sca1");
        //Extrude electrodes
        model.geom("geom1").create("ext"+extrudeCount, "Extrude");
        model.geom("geom1").feature("ext"+extrudeCount).set("workplane", "wp"+extrudeCount);
        model.geom("geom1").feature("ext"+extrudeCount).selection("input").set("wp"+extrudeCount);
        model.geom("geom1").feature("ext"+extrudeCount).setIndex("distance", dxfLayerHeights[j-1], 0);

      }
      if (i>1) {
        model.geom("geom1").create("dif"+i, "Difference");
        model.geom("geom1").feature("dif"+i).selection("input").set("ext"+(extrudeCount));
        if (i==3) {//TODO: doesn't handle 4 layers
          model.geom("geom1").feature("dif"+i).selection("input2").set("ext"+(extrudeCount-2), "ext"+(extrudeCount-1));
        }
        if (i==2) {
          model.geom("geom1").feature("dif"+i).selection("input2").set("ext"+(extrudeCount-1));
        }
        model.geom("geom1").feature("dif"+i).set("selresult", true);
        model.geom("geom1").feature("dif"+i).set("contributeto", "ElectrodeSel");
      }
      else {
        model.geom("geom1").feature("ext"+extrudeCount).set("selresult", true);
        model.geom("geom1").feature("ext"+extrudeCount).set("contributeto", "ElectrodeSel");
      }
    }
  }

  /**
  * Imports and extrudes DXF File (without scale factor)
  * @param dxfFile Path to dxf file (cannot be a relative path).
  * @param dxfLayers Names of layers in dxf file, in the order that they should be built.
  * @param dxfLayerHeights Height of each layer.
  */

  public void addElectrodesDXF(String dxfFile, String dxfLayers[], Double dxfLayerHeights[]){
    addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1.);
  }

  /**
  * Imports stl files.
  * @param stlFolder Path to folder containing stl files (cannot be a relative path).
  * @param startHeight Height (in nm) at which the electrodes start.
  * @param scale Factor to scale STL file (e.g. 1000 to transfer from um to nm).
  */

  public void addElectrodesSTL(String stlFolder, Double startHeight, int scale){

    model.component("comp1").geom("geom1").selection().create("ElectrodeSel", "CumulativeSelection");

    File folder = new File(stlFolder);
    File[] listOfFiles = folder.listFiles();

    int importCount = 1;

    for (File file : listOfFiles) {
      if (file.isFile() && file.getName().endsWith(".stl")) {
          System.out.println(folder+"/"+file.getName());
          //Create import node
          model.component("comp1").geom("geom1").create("imp"+importCount, "Import");
          model.component("comp1").geom("geom1").feature("imp"+importCount).set("filename", folder+"/"+file.getName());
          //Create mesh node
          model.component().create("mcomp"+importCount, "MeshComponent");
          model.geom().create("mgeom"+importCount,3);
          model.mesh().create("mpart"+importCount, "mgeom"+importCount);
          model.component("comp1").geom("geom1").feature("imp"+importCount).set("mesh", "mpart"+importCount);
          //Associate import node with mesh node
          model.mesh("mpart"+importCount).create("imp"+importCount, "Import");
          model.mesh("mpart"+importCount).feature("imp"+importCount).set("filename", folder+"/"+file.getName());
          model.component("comp1").geom("geom1").feature("imp"+importCount).set("meshfilename", "");
          //Create mesh of import
          model.mesh("mpart"+importCount).run();
          //import file
          model.component("comp1").geom("geom1").run("imp"+importCount);
          //Change settings of mesh so that we get a reasonable mesh
          model.mesh("mpart"+importCount).feature("imp"+importCount).set("facepartition", "auto");
          model.mesh("mpart"+importCount).feature("imp"+importCount).set("stltoltype", "auto");
          model.mesh("mpart"+importCount).feature("imp"+importCount).set("facepartition", "detectfaces");
          model.mesh("mpart"+importCount).feature("imp"+importCount).set("facemaxangle", "0.0");
          //Re-import file
          model.component("comp1").geom("geom1").run("imp"+importCount);
          //associate domains with correct domain group
          model.geom("geom1").feature("imp"+importCount).set("contributeto", "ElectrodeSel");

          importCount += 1;


          }
    }
    /*
    * Next we want to trim the electrodes to xDim / yDim
    */
    //scale by amount perscribed.
    model.component("comp1").geom("geom1").create("sca1", "Scale");
    model.component("comp1").geom("geom1").feature("sca1").selection("input").named("ElectrodeSel");
    model.component("comp1").geom("geom1").feature("sca1").set("type", "anisotropic");
    model.component("comp1").geom("geom1").feature("sca1").set("anisotropic", new int[]{scale, scale, 1});
    //Create box to keep
    model.component("comp1").geom("geom1").create("blk1", "Block");
    model.component("comp1").geom("geom1").feature("blk1").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk1").set("size", new String[]{xDim, yDim, "200."});
    //Create larger box
    model.component("comp1").geom("geom1").create("blk2", "Block");
    model.component("comp1").geom("geom1").feature("blk2").set("base", "center");
    model.component("comp1").geom("geom1").feature("blk2").set("size", new String[]{"4000.", "4000.", "200."});//TODO: this box should contain all of stl files; does it always?
    //Take difference - this is the domain we want to remove
    model.component("comp1").geom("geom1").create("dif1", "Difference");
    model.component("comp1").geom("geom1").feature("dif1").selection("input").set("blk2");
    model.component("comp1").geom("geom1").feature("dif1").selection("input2").set("blk1");
    //remove electrode domains in domain we want to remove
    model.component("comp1").geom("geom1").create("dif2", "Difference");
    model.component("comp1").geom("geom1").feature("dif2").selection("input").named("ElectrodeSel");
    model.component("comp1").geom("geom1").feature("dif2").selection("input2").set("dif1");
    //Now that we have trimmed the geometry, move it to correct spot.
    model.component("comp1").geom("geom1").create("mov1", "Move");
    model.component("comp1").geom("geom1").feature("mov1").selection("input").named("ElectrodeSel");
    model.component("comp1").geom("geom1").feature("mov1").set("displz", startHeight);



  }

  /**
  * Imports and extrudes STL File (without scale factor)
  * @param stlFolder Path to folder containing stl files (cannot be a relative path).
  * @param startHeight Height (in nm) at which the electrodes start.
  */

  public void addElectrodesSTL(String stlFolder, Double startHeight){
    addElectrodesSTL(stlFolder,startHeight,1);
  }

  /**
  * Associates a material with a domain within the model
  * @param mat Material.
  * @param domain Domain.
  */

  public void addMaterial(ComsolMaterial mat, String domain){
    model.component("comp1").material().create(mat.name, "Common");
    model.component("comp1").material(mat.name).selection().named(domain);
    model.component("comp1").material(mat.name).propertyGroup("def").set("relpermittivity", new String[]{Double.toString(mat.relpermittivity)});
  }

  /**
  * Creates a potential node in COMSOL for each domain in the domain group.
  * @param totalDomain Domain group of electrodes.
  */

  public void selectVoltages(String totalDomain){
    int blkDomainSelectionEntities[] = model.selection(totalDomain).entities(3);

    for (int selNumber = 0; selNumber<blkDomainSelectionEntities.length; selNumber++) {
      model.component("comp1").selection().create("sel"+(selNumber+1), "Explicit");
      model.component("comp1").selection("sel"+(selNumber+1)).geom("geom1", 3, 2, new String[]{"exterior"});
      model.component("comp1").selection("sel"+(selNumber+1)).set(blkDomainSelectionEntities[selNumber]);
      model.component("comp1").physics("es").create("pot"+(selNumber+1), "ElectricPotential", 2);
      model.component("comp1").physics("es").feature("pot"+(selNumber+1)).selection().named("sel"+(selNumber+1));

    }
  }
}
