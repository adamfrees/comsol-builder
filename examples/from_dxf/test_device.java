import com.comsol.model.*;
import com.comsol.model.util.*;

public class test_device {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      Model model = ModelUtil.create("Model");
      model.modelNode().create("comp1");
      model.geom().create("geom1", 3);
      model.geom("geom1").lengthUnit("nm");
      String xDim = "1000.";
      String yDim = "1000.";
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","60."};
      Double currentHeight = 0.;
      model.component("comp1").geom("geom1").selection().create("SiGeSel", "CumulativeSelection");
      model.component("comp1").geom("geom1").selection().create("ElectrodeSel", "CumulativeSelection");

      for (int i = 0; i<layerLabels.length; i++) {
        model.geom("geom1").feature().create(layerLabels[i], "Block");
        model.geom("geom1").feature(layerLabels[i]).set("size", new String[]{xDim, yDim, layerHeights[i]});
        model.geom("geom1").feature(layerLabels[i]).set("pos", new String[]{"0.", "0.", Double.toString(currentHeight+Double.parseDouble(layerHeights[i])/2.)});
        model.geom("geom1").feature(layerLabels[i]).set("base", "center");
        model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
        if (i==0 || i==2) {
          model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
          model.geom("geom1").feature(layerLabels[i]).set("contributeto", "SiGeSel");
        }
        currentHeight += Double.parseDouble(layerHeights[i]);
      }
      String dxfFile = "qqd-v2-6.dxf";
      String dxfLayers[] = {"L1","L2","L3"};
      Double dxfLayerHeights[] = {33.,44.,55.};
      int extrudeCount = 0;
      for (int i = dxfLayers.length; i>0; i--) {
        for (int j = 1; j<=i; j++) {
          extrudeCount++;

          model.geom("geom1").create("wp"+extrudeCount, "WorkPlane");
          model.geom("geom1").feature("wp"+extrudeCount).set("quickz", currentHeight-55.);
          model.geom("geom1").feature("wp"+extrudeCount).geom().create("imp1", "Import");
          model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("filename", dxfFile);
          model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layerselection", "selected");
          model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layers", new String[]{dxfLayers[j-1]});
          model.geom("geom1").feature("wp"+extrudeCount).geom().create("sca1", "Scale");
          model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").set("isotropic", 1000);
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
      model.geom("geom1").run("fin");


      model.component("comp1").material().create("Si", "Common");
      model.component("comp1").material("Si").selection().named("geom1_SiWell_dom");
      model.component("comp1").material("Si").propertyGroup("def").set("relpermittivity", new String[]{"11.7"});


      model.component("comp1").material().create("SiGe", "Common");
      model.component("comp1").material("SiGe").selection().named("geom1_SiGeSel_dom");
      model.component("comp1").material("SiGe").propertyGroup("def").set("relpermittivity", new String[]{"13.19"});

      model.component("comp1").material().create("AlO2", "Common");
      model.component("comp1").material("AlO2").selection().named("geom1_Oxide_dom");
      model.component("comp1").material("AlO2").propertyGroup("def").set("relpermittivity", new String[]{"9."});

      model.component("comp1").material().create("Al", "Common");
      model.component("comp1").material("Al").selection().named("geom1_ElectrodeSel_dom");
      model.component("comp1").material("Al").propertyGroup("def").set("relpermittivity", new String[]{"1."});


      model.physics().create("es", "Electrostatics", "geom1");



      return model;
   }

}
