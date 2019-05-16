package comsolbuilder;

/**
* An instance of the ComsolMaterial class is an object
* which contains the parameters associated with a material
* in COMSOL.
*
* @author  Adam Frees
*/

public class ComsolMaterial {
  /**
  * Name of material.
  */
  public String name;
  /**
  * Relative permittivity of material.
  */
  public Double relpermittivity;

  /**
  * The constructor for the ComsolMaterial object.
  * @param nameInput Name of material.
  * @param relpermittivityInput Relative permittivity of material.
  */

  public ComsolMaterial(String nameInput, Double relpermittivityInput){
    name = nameInput;
    relpermittivity = relpermittivityInput;
  }

}
