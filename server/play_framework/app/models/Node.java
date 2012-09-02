package models;

import java.util.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;

@Entity
public class Node extends Model {
 
  @Id
  public Long id;
  public String name;
  public String description;
  
  @OneToMany(cascade = {CascadeType.ALL} )
  @JoinColumn(name="deviceUid")
  public List<NodeConfig> nodeConfigs;
  
  public static Finder<Long,Node> find = new Finder(Long.class, Node.class);
   
  public void save(){
	  System.out.println("Saving Node");
	  super.save();
  }
}