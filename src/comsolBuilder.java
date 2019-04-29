import com.comsol.model.*;
import com.comsol.model.util.*;

package comsolbuilder;

public class ComsolBuilder {
  public Model model;

  public ComsolBuilder() {
    model = ModelUtil.create("Model");
    model.modelNode().create("comp1");
    model.geom().create("geom1", 3);
    model.geom("geom1").lengthUnit("nm");
  }
}
