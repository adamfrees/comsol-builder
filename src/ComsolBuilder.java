package comsolbuilder;

import com.comsol.model.*;
import com.comsol.model.util.*;

public class ComsolBuilder {
  public Model model;
  public String xDim;
  public String yDim;
  public Double currentHeight;

  public ComsolBuilder(Double xDimInput, Double yDimInput) {
    model = ModelUtil.create("Model");
    model.modelNode().create("comp1");
    model.geom().create("geom1", 3);
    model.geom("geom1").lengthUnit("nm");
    xDim = Double.toString(xDimInput);
    yDim = Double.toString(yDimInput);
    currentHeight = 0.;

  }

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

  public void addElectrodesDXF(String dxfFile, String dxfLayers[], Double dxfLayerHeights[], Double scale){
    int extrudeCount = 0;
    model.component("comp1").geom("geom1").selection().create("ElectrodeSel", "CumulativeSelection");
    for (int i = dxfLayers.length; i>0; i--) {
      for (int j = 1; j<=i; j++) {
        extrudeCount++;

        model.geom("geom1").create("wp"+extrudeCount, "WorkPlane");
        model.geom("geom1").feature("wp"+extrudeCount).set("quickz", currentHeight-55.);//TODO: change, shouldn't be 55
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("imp1", "Import");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("filename", dxfFile);
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layerselection", "selected");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layers", new String[]{dxfLayers[j-1]});
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("sca1", "Scale");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").set("isotropic", scale);
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").selection("input").set("imp1");
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("r1", "Rectangle");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("base", "center");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("size", new String[]{xDim, yDim});
        model.geom("geom1").feature("wp"+extrudeCount).geom().create("int1", "Intersection");
        model.geom("geom1").feature("wp"+extrudeCount).geom().feature("int1").selection("input").set("r1", "sca1");

        model.geom("geom1").create("ext"+extrudeCount, "Extrude");
        model.geom("geom1").feature("ext"+extrudeCount).set("workplane", "wp"+extrudeCount);
        model.geom("geom1").feature("ext"+extrudeCount).selection("input").set("wp"+extrudeCount);
        model.geom("geom1").feature("ext"+extrudeCount).setIndex("distance", dxfLayerHeights[j-1], 0);

      }
      if (i>1) {
        model.geom("geom1").create("dif"+i, "Difference");
        model.geom("geom1").feature("dif"+i).selection("input").set("ext"+(extrudeCount));
        if (i==3) {
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
  public void addElectrodesDXF(String dxfFile, String dxfLayers[], Double dxfLayerHeights[]){
    addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1.);
  }
}
