package models;

import java.util.*;

import javax.persistence.*;

import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;
import play.db.ebean.Model.Finder;

@Entity
public class Measurement extends Model {
	@Id
	public Long id;
	
	@Constraints.Required
	public Date timestamp;
	@Constraints.Required
	public float value;
	
}