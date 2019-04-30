import com.comsol.model.*;
import com.comsol.model.util.*;
import comsolbuilder.*;

public class test_device {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      Double xDims = 1000.;
      Double yDims = 1000.;
      ComsolBuilder builder = new ComsolBuilder(xDims,yDims);
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","60."};

      builder.addHeterostructure(layerLabels,layerHeights);

      String dxfFile = "qqd-v2-6.dxf";
      String dxfLayers[] = {"L1","L2","L3"};
      Double dxfLayerHeights[] = {33.,44.,55.};

      builder.addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1000.);

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
