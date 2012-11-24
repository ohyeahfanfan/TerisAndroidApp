import java.io.Serializable;
import java.util.*;
 
/**
 * @author http://lycog.com
 */
 
//Implement Serializable to do marshaling
public class MyDate implements Serializable{
  private Date date_;
  private int number_;
  static int i = 0;
 
  MyDate() {
    date_ = new Date();
    number_ = i++;
  }
 
  //To be invoked at the client
  public Date getDate() {
    return date_;
  }
 
  //To be invoked at the client
  public int getNumber() {
    return number_;
  }
}