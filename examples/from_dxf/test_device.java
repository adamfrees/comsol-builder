import com.comsol.model.*;
import com.comsol.model.util.*;
import comsolbuilder.*;

public class test_device {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      // First, define materials used in study
      ComsolMaterial si = new ComsolMaterial("Si",11.7);
      ComsolMaterial sige = new ComsolMaterial("SiGe",13.19);
      ComsolMaterial alO2 = new ComsolMaterial("AlO2",9.);
      ComsolMaterial al = new ComsolMaterial("Al",1.);
      // the x-y span of the geometry is 1000 nm x 1000 nm
      Double xDims = 1000.;
      Double yDims = 1000.;
      // Create ComsolBuilder instance
      ComsolBuilder builder = new ComsolBuilder(xDims,yDims);
      // Define heterostructure layer names and heights, add them to the builder
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","60."};
      builder.addHeterostructure(layerLabels,layerHeights);
      //Add DXF file to builder
      String dxfFile = "/Users/adamfrees/Documents/comsol-builder/examples/from_dxf/qqd-v2-6.dxf"; //TODO: make this a relative path?
      String dxfLayers[] = {"L1","L2","L3"};//These are metadata in the .dxf file. TODO: scrape this info automatically?
      Double dxfLayerHeights[] = {33.,44.,55.};
      builder.addElectrodesDXF(dxfFile,dxfLayers,dxfLayerHeights,1000.);//1000 scale factor to translate from um to nm.
      //Finish building geometry
      builder.model.geom("geom1").run("fin");
      //Specify which materials are used with which domains
      builder.addMaterial(si,"geom1_SiWell_dom");//TODO: user shouldn't have to know domain names
      builder.addMaterial(sige,"geom1_SiGeSel_dom");
      builder.addMaterial(alO2,"geom1_Oxide_dom");
      builder.addMaterial(al,"geom1_ElectrodeSel_dom");
      //Add electrostatics to model
      builder.model.physics().create("es", "Electrostatics", "geom1");
      //Each domain in domain group is a different electrode
      builder.selectVoltages("geom1_ElectrodeSel_dom");

      return builder.model;
   }

}
