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
}
