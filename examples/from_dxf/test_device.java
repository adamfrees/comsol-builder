import com.comsol.model.*;
import com.comsol.model.util.*;
import comsolbuilder.*;

public class test_device {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      ComsolMaterial si = new ComsolMaterial("Si",11.7);
      ComsolMaterial sige = new ComsolMaterial("SiGe",13.19);
      ComsolMaterial alO2 = new ComsolMaterial("AlO2",9.);
      ComsolMaterial al = new ComsolMaterial("Al",1.);
      Double xDims = 1000.;
      Double yDims = 1000.;
      ComsolBuilder builder = new ComsolBuilder(xDims,yDims);
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","60."};

      builder.addHeterostructure(layerLabels,layerHeights);
      String path = ComsolBuilder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      System.out.println("Working Directory = " +
              path);

      String dxfFile = "/Users/adamfrees/Documents/comsol-builder/examples/from_dxf/qqd-v2-6.dxf"; //TODO: make this a relative path?
      String dxfLayers[] = {"L1","L2","L3"};
      Double dxfLayerHeights[] = {33.,44.,55.};

      builder.addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1000.);

      builder.model.geom("geom1").run("fin");

      builder.addMaterial(si,"geom1_SiWell_dom");//TODO: user shouldn't have to know domain names
      builder.addMaterial(sige,"geom1_SiGeSel_dom");
      builder.addMaterial(alO2,"geom1_Oxide_dom");
      builder.addMaterial(al,"geom1_ElectrodeSel_dom");


      builder.model.physics().create("es", "Electrostatics", "geom1");

      int blkDomainSelectionEntities[] = builder.model.selection("geom1_ElectrodeSel_dom").entities(3);

      for (int selNumber = 0; selNumber<blkDomainSelectionEntities.length; selNumber++) {
        builder.model.component("comp1").selection().create("sel"+(selNumber+1), "Explicit");
        builder.model.component("comp1").selection("sel"+(selNumber+1)).geom("geom1", 3, 2, new String[]{"exterior"});
        builder.model.component("comp1").selection("sel"+(selNumber+1)).set(blkDomainSelectionEntities[selNumber]);
        builder.model.component("comp1").physics("es").create("pot"+(selNumber+1), "ElectricPotential", 2);
        builder.model.component("comp1").physics("es").feature("pot"+(selNumber+1)).selection().named("sel"+(selNumber+1));

      }

      return builder.model;
   }

}
