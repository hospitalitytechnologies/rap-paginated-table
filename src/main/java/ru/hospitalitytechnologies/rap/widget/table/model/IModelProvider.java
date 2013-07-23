package ru.hospitalitytechnologies.rap.widget.table.model;

import java.util.HashMap;


public interface IModelProvider< T > {

  public T[] getData( HashMap< String, Object > filters );

  public String getDataItemFieldText( T item, int index );
}
