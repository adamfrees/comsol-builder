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

      String dxfFile = "/Users/adamfrees/Documents/comsol-builder/examples/from_dxf/qqd-v2-6.dxf";
      String dxfLayers[] = {"L1","L2","L3"};
      Double dxfLayerHeights[] = {33.,44.,55.};

      builder.addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1000.);

      builder.model.geom("geom1").run("fin");

      builder.addMaterial(si,"geom1_SiWell_dom");
      builder.addMaterial(sige,"geom1_SiGeSel_dom");
      builder.addMaterial(alO2,"geom1_Oxide_dom");
      builder.addMaterial(al,"geom1_ElectrodeSel_dom");


      builder.model.physics().create("es", "Electrostatics", "geom1");



      return builder.model;
   }

}
