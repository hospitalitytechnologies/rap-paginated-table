package ru.hospitalitytechnologies.rap.widget.table.viewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import ru.hospitalitytechnologies.rap.widget.table.AbstarctPageViewer;
import ru.hospitalitytechnologies.rap.widget.table.model.IModelProvider;


public class TablePageViewer< T > extends AbstarctPageViewer {

  private Table table;
  private BaseTableViewerN< T > tableViewer;
  /**
	 * 
	 */
  private static final long serialVersionUID = 4310325637720738897L;

  public TablePageViewer( Composite parent,
                          final IModelProvider< T > modelProvider,
                          final String[] columnNames,
                          double width,
                          double height )
  {
    super( parent, SWT.NONE );
    setLayout( new FormLayout() );
    Composite tableComposite = new Composite( this, SWT.NONE );
    table = new Table( tableComposite, SWT.VIRTUAL
                                       | SWT.H_SCROLL
                                       | SWT.V_SCROLL
                                       | SWT.FULL_SELECTION
                                       | SWT.HIDE_SELECTION
                                       | SWT.BORDER );
    tableViewer = new BaseTableViewerN< T >( table, columnNames, width, height ) {

      private static final long serialVersionUID = -7527880573257867026L;

      @Override
      protected String getDataItemFieldText( T element, int columnIndex ) {
        return modelProvider.getDataItemFieldText( ( T )element, columnIndex );
      }

      @Override
      protected Color getColor( T element ) {
        return null;
      }
    };
    super.createControls( tableComposite );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  protected void setInputWidget( Object[] data ) {
    tableViewer.setInput( ( T[] )data );
  }

  @Override
  public Object getSelection() {
    return tableViewer.getSelection();
  }

  @Override
  protected Object getParentElement( Object element ) {
    return null;
  }

  public TableViewer getTableViewer() {
    return tableViewer;
  }

  public Table getTable() {
    return table;
  }
}
