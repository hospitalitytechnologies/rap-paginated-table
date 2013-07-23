package ru.hospitalitytechnologies.rap.widget.table.viewer;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import ru.hospitalitytechnologies.rap.widget.table.AbstarctPageViewer;


@SuppressWarnings( "rawtypes" )
public class TreePageViewer< T > extends AbstarctPageViewer {

  private BaseTreeViewerN< T > treePageViewer;
  private ITreeContentProvider treeContentProvider;
  private Tree tree;
  /**
	 * 
	 * 
	 */
  private static final long serialVersionUID = 4310325637720738897L;

  public TreePageViewer( Composite parent,
                         ITableLabelProvider labelProvider,
                         ITreeContentProvider treeContentProvider,
                         final String[] columnNames,
                         double width )
  {
    super( parent, SWT.NONE );
    setLayout( new FormLayout() );
    this.treeContentProvider = treeContentProvider;
    Composite treeComposite = new Composite( this, SWT.NONE );
    tree = new Tree( treeComposite, SWT.VIRTUAL
                                    | SWT.H_SCROLL
                                    | SWT.V_SCROLL
                                    | SWT.SINGLE
                                    | SWT.FULL_SELECTION );
    treePageViewer = new BaseTreeViewerN< T >( tree,
                                               labelProvider,
                                               treeContentProvider,
                                               columnNames,
                                               width );
    super.createControls( treeComposite );
  }

  @SuppressWarnings( "unchecked" )
  protected void setInputWidget( Object[] data ) {
    treePageViewer.setInput( ( T[] )data );
  }

  @Override
  public Object getSelection() {
    return treePageViewer.getSelection();
  }

  protected Object getParentElement( Object element ) {
    Object parent = treeContentProvider.getParent( element );
    if( ( parent == element ) || ( parent == null ) )
      return null;
    else
      return parent;
  }

  public TreeViewer getTreeViewer() {
    return treePageViewer;
  }

  public Tree getTree() {
    return tree;
  }
}
