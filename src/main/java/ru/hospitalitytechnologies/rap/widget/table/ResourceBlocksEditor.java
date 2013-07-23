package ru.hospitalitytechnologies.rap.widget.table;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

import ru.hospitalitytechnologies.rap.widget.table.model.IModelProvider;
import ru.hospitalitytechnologies.rap.widget.table.viewer.TablePageViewer;



public class ResourceBlocksEditor extends EditorPart {

  public static final String ID = "ru.hospitalitytechnologies.rap.widget.table.ResourceBlocksEditor";
  private TablePageViewer< People > blocksViewer;
  private String[] blocksColumnNames = {
    "FirstName", "LastName", "Comment"
  };
  private ScrolledForm form;

  @Override
  public void createPartControl( final Composite parent ) {
    FormToolkit toolkit = new FormToolkit( parent.getDisplay() );
    form = toolkit.createScrolledForm( parent );
    form.setText( "Northern stream" );
    final Composite container = form.getBody();
    container.setLayout( new FormLayout() );
    ReservationResourceBlocktModelProvider modelColumns = new ReservationResourceBlocktModelProvider();
    blocksViewer = new TablePageViewer< People >( container,
                                                  modelColumns,
                                                  this.blocksColumnNames,
                                                  .8,
                                                  5.5 );
    FormData fd_blocksTable = new FormData();
    fd_blocksTable.top = new FormAttachment( 0, 10 );
    fd_blocksTable.left = new FormAttachment( 0, 10 );
    fd_blocksTable.right = new FormAttachment( 100, -10 );
    fd_blocksTable.bottom = new FormAttachment( 100, -10 );
    blocksViewer.setLayoutData( fd_blocksTable );
    updateTable();
  }

  private class People {

    private String firstName;
    private String lastName;
    private String comment;

    public People( String firstName, String lastName, String comment ) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.comment = comment;
    }

    public String getLastName() {
      return lastName;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getComment() {
      return comment;
    }
  }
  private class ReservationResourceBlocktModelProvider implements IModelProvider< People > {

    @Override
    public People[] getData( HashMap< String, Object > filters ) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getDataItemFieldText( People element, int columnIndex ) {
      switch( columnIndex ) {
        case 0:
          return element.getFirstName();
        case 1:
          return element.getLastName();
        case 2:
          return element.getComment();
      }
      return "";
    }
  }

  private void updateTable() {
    People[] peoples = new People[ 4 ];
    peoples[ 0 ] = new People( "Ivan", "Ivanov", "a very popular man" );
    peoples[ 1 ] = new People( "Genrich", "Miller", "friend Angela Merkil" );
    peoples[ 2 ] = new People( "Angela", "Merkil", "friend Genrich Miller" );
    peoples[ 3 ] = new People( "wrap data wrap data", "wrap data wrap data", "wrap data wrap data" );
    this.blocksViewer.setInput( peoples );
  }

  @Override
  public void init( IEditorSite site, IEditorInput editorInput ) throws PartInitException {
    setSite( site );
    setInput( editorInput );
    setPartName( "Page TableViewer" );
  }

  @Override
  public void doSave( IProgressMonitor arg0 ) {
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void setFocus() {
    form.reflow( true );
  }
}
