import com.comsol.model.*;
import com.comsol.model.util.*;
import comsolbuilder.*;

public class test_device {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      ComsolBuilder builder = new ComsolBuilder();
      String xDim = "1000.";
      String yDim = "1000.";
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","60."};
      Double currentHeight = 0.;
      builder.model.component("comp1").geom("geom1").selection().create("SiGeSel", "CumulativeSelection");
      builder.model.component("comp1").geom("geom1").selection().create("ElectrodeSel", "CumulativeSelection");

      for (int i = 0; i<layerLabels.length; i++) {
        builder.model.geom("geom1").feature().create(layerLabels[i], "Block");
        builder.model.geom("geom1").feature(layerLabels[i]).set("size", new String[]{xDim, yDim, layerHeights[i]});
        builder.model.geom("geom1").feature(layerLabels[i]).set("pos", new String[]{"0.", "0.", Double.toString(currentHeight+Double.parseDouble(layerHeights[i])/2.)});
        builder.model.geom("geom1").feature(layerLabels[i]).set("base", "center");
        builder.model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
        if (i==0 || i==2) {
          builder.model.geom("geom1").feature(layerLabels[i]).set("selresult", true);
          builder.model.geom("geom1").feature(layerLabels[i]).set("contributeto", "SiGeSel");
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

          builder.model.geom("geom1").create("wp"+extrudeCount, "WorkPlane");
          builder.model.geom("geom1").feature("wp"+extrudeCount).set("quickz", currentHeight-55.);
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().create("imp1", "Import");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("filename", dxfFile);
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layerselection", "selected");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("imp1").set("layers", new String[]{dxfLayers[j-1]});
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().create("sca1", "Scale");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").set("isotropic", 1000);
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("sca1").selection("input").set("imp1");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().create("r1", "Rectangle");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("base", "center");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("r1").set("size", new String[]{xDim, yDim});
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().create("int1", "Intersection");
          builder.model.geom("geom1").feature("wp"+extrudeCount).geom().feature("int1").selection("input").set("r1", "sca1");

          builder.model.geom("geom1").create("ext"+extrudeCount, "Extrude");
          builder.model.geom("geom1").feature("ext"+extrudeCount).set("workplane", "wp"+extrudeCount);
          builder.model.geom("geom1").feature("ext"+extrudeCount).selection("input").set("wp"+extrudeCount);
          builder.model.geom("geom1").feature("ext"+extrudeCount).setIndex("distance", dxfLayerHeights[j-1], 0);

        }
        if (i>1) {
          builder.model.geom("geom1").create("dif"+i, "Difference");
          builder.model.geom("geom1").feature("dif"+i).selection("input").set("ext"+(extrudeCount));
          if (i==3) {
            builder.model.geom("geom1").feature("dif"+i).selection("input2").set("ext"+(extrudeCount-2), "ext"+(extrudeCount-1));
          }
          if (i==2) {
            builder.model.geom("geom1").feature("dif"+i).selection("input2").set("ext"+(extrudeCount-1));
          }
          builder.model.geom("geom1").feature("dif"+i).set("selresult", true);
          builder.model.geom("geom1").feature("dif"+i).set("contributeto", "ElectrodeSel");
        }
        else {
          builder.model.geom("geom1").feature("ext"+extrudeCount).set("selresult", true);
          builder.model.geom("geom1").feature("ext"+extrudeCount).set("contributeto", "ElectrodeSel");
        }
      }
      builder.model.geom("geom1").run("fin");


      builder.model.component("comp1").material().create("Si", "Common");
      builder.model.component("comp1").material("Si").selection().named("geom1_SiWell_dom");
      builder.model.component("comp1").material("Si").propertyGroup("def").set("relpermittivity", new String[]{"11.7"});


      builder.model.component("comp1").material().create("SiGe", "Common");
      builder.model.component("comp1").material("SiGe").selection().named("geom1_SiGeSel_dom");
      builder.model.component("comp1").material("SiGe").propertyGroup("def").set("relpermittivity", new String[]{"13.19"});

      builder.model.component("comp1").material().create("AlO2", "Common");
      builder.model.component("comp1").material("AlO2").selection().named("geom1_Oxide_dom");
      builder.model.component("comp1").material("AlO2").propertyGroup("def").set("relpermittivity", new String[]{"9."});

      builder.model.component("comp1").material().create("Al", "Common");
      builder.model.component("comp1").material("Al").selection().named("geom1_ElectrodeSel_dom");
      builder.model.component("comp1").material("Al").propertyGroup("def").set("relpermittivity", new String[]{"1."});


      builder.model.physics().create("es", "Electrostatics", "geom1");



      return builder.model;
   }

}
