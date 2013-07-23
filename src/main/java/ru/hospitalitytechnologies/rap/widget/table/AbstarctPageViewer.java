package ru.hospitalitytechnologies.rap.widget.table;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.osgi.framework.FrameworkUtil;

import ru.hospitalitytechnologies.rap.widget.input.InputWithText;


public abstract class AbstarctPageViewer< T > extends Composite {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public AbstarctPageViewer( Composite parent, int style ) {
    super( parent, style );
  }

  private Spinner spPageSize;
  // private Spinner spPageIndex;
  private Button buttonRight;
  private Button buttonLeft;
  private ArrayList< T > dataObjects = new ArrayList< T >();
  private int currentPageIndex = 1;
  private int maxPageIndex = 1;
  private int recordsInPage = 20;
  private InputWithText txtPageMax;
  private Button buttonEnd;
  private Button buttonBegin;

  protected void setRecordsInPage( int recordsInPage ) {
    this.recordsInPage = recordsInPage;
  }

  protected int getRecordsInPage( int recordsInPage ) {
    return this.recordsInPage;
  }

  public void setInput( T[] data ) {
    dataObjects = new ArrayList< T >();
    for( T element : data )
      if( getParentElement( element ) == null )
        dataObjects.add( element );
    updateMaxPageIndex();
    refreshData();
  }

  @SuppressWarnings( "unchecked" )
  private void refreshData() {
    // txtPageMax.getInputTextWidget().setText(currentPageIndex + "");
    txtPageMax.getCommentTextWidget().setText( " of " + maxPageIndex );
    int begiIndex = 0 + ( currentPageIndex - 1 ) * recordsInPage;
    if( dataObjects.size() > begiIndex ) {
      int endIndex = ( int )Math.min( ( double )( recordsInPage + ( currentPageIndex - 1 )
                                                                  * recordsInPage ),
                                      ( double )dataObjects.size() );
      setInputWidget( ( T[] )dataObjects.subList( begiIndex, endIndex ).toArray() );
    }
  }

  private void updateMaxPageIndex() {
    currentPageIndex = 1;
    maxPageIndex = 1;
    int wholeNum = dataObjects.size() / ( recordsInPage );
    int moduloNum = dataObjects.size() % ( recordsInPage );
    if( wholeNum > 0 )
      if( moduloNum > 0 )
        maxPageIndex = ( wholeNum + 1 );
      else
        maxPageIndex = wholeNum;
    txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
    txtPageMax.getCommentTextWidget().setText( " of " + maxPageIndex );
  }

  private void updateButtonsState() {
    if( currentPageIndex < maxPageIndex ) {
      buttonRight.setEnabled( true );
      buttonEnd.setEnabled( true );
    } else {
      buttonRight.setEnabled( false );
      buttonEnd.setEnabled( false );
    }
    if( currentPageIndex > 1 ) {
      buttonLeft.setEnabled( true );
      buttonBegin.setEnabled( true );
    } else {
      buttonLeft.setEnabled( false );
      buttonBegin.setEnabled( false );
    }
  }

  protected abstract void setInputWidget( T[] data );

  protected abstract Object getParentElement( Object element );

  public abstract Object getSelection();

  /**
	 * 
	 */
  protected void createControls( Composite parent ) {
    spPageSize = new Spinner( this, SWT.BORDER );
    final FormData fd_spPageSize = new FormData();
    fd_spPageSize.left = new FormAttachment( 100, -80 );
    fd_spPageSize.right = new FormAttachment( 100, 0 );
    fd_spPageSize.bottom = new FormAttachment( 100, 0 );
    spPageSize.setLayoutData( fd_spPageSize );
    spPageSize.setMinimum( 1 );
    spPageSize.setMaximum( 999 );
    spPageSize.setSelection( recordsInPage );
    Label lblPageIndex = new Label( this, SWT.NONE );
    final FormData fd_lblPageIndex = new FormData();
    // fd_lblPageIndex.left = new FormAttachment(0, 0);
    fd_lblPageIndex.right = new FormAttachment( spPageSize, -10 );
    fd_lblPageIndex.bottom = new FormAttachment( 100, -4 );
    lblPageIndex.setLayoutData( fd_lblPageIndex );
    lblPageIndex.setText( "Records per page" );
    lblPageIndex.setData( RWT.CUSTOM_VARIANT, "PageViewer" );
    buttonEnd = new Button( this, SWT.NONE );
    final FormData fd_buttonEnd = new FormData();
    // fd_buttonEnd.left = new FormAttachment(65, 5);
    fd_buttonEnd.right = new FormAttachment( lblPageIndex, -250 );
    fd_buttonEnd.bottom = new FormAttachment( 100, -1 );
    buttonEnd.setLayoutData( fd_buttonEnd );
    Image imgLogo = getImg( "/images/default/button/green_arrow-end.png" );
    buttonEnd.setImage( imgLogo );
    buttonEnd.setEnabled( false );
    buttonRight = new Button( this, SWT.NONE );
    final FormData fdbuttonRight = new FormData();
    // fdbuttonRight.left = new FormAttachment(60, 5);
    fdbuttonRight.right = new FormAttachment( buttonEnd, -10 );
    fdbuttonRight.bottom = new FormAttachment( 100, -1 );
    buttonRight.setLayoutData( fdbuttonRight );
    imgLogo = getImg( "/images/default/button/green_arrow-right.png" );
    buttonRight.setImage( imgLogo );
    buttonRight.setEnabled( false );
    txtPageMax = new InputWithText( this, SWT.BORDER );
    final FormData fd_txtPageMax = new FormData();
    fd_txtPageMax.left = new FormAttachment( buttonRight, -150 );
    fd_txtPageMax.right = new FormAttachment( buttonRight, -10 );
    fd_txtPageMax.bottom = new FormAttachment( 100, -3 );
    txtPageMax.setLayoutData( fd_txtPageMax );
    txtPageMax.getInputTextWidget().setText( "1" );
    txtPageMax.getCommentTextWidget().setText( " of 1" );
    buttonLeft = new Button( this, SWT.NONE );
    final FormData fdbuttonLeft = new FormData();
    // fdbuttonLeft.left = new FormAttachment(40, 5);
    fdbuttonLeft.right = new FormAttachment( txtPageMax, -10 );
    fdbuttonLeft.bottom = new FormAttachment( 100, -1 );
    buttonLeft.setLayoutData( fdbuttonLeft );
    imgLogo = getImg( "/images/default/button/green_arrow-left.png" );
    buttonLeft.setImage( imgLogo );
    buttonLeft.setEnabled( false );
    buttonBegin = new Button( this, SWT.NONE );
    final FormData fd_buttonBegin = new FormData();
    // fd_buttonBegin.left = new FormAttachment(35, 5);
    fd_buttonBegin.right = new FormAttachment( buttonLeft, -10 );
    fd_buttonBegin.bottom = new FormAttachment( 100, -1 );
    buttonBegin.setLayoutData( fd_buttonBegin );
    imgLogo = getImg( "/images/default/button/green_arrow-begin.png" );
    buttonBegin.setImage( imgLogo );
    buttonBegin.setEnabled( false );
    txtPageMax.getInputTextWidget().addVerifyListener( new VerifyListener() {

      private static final long serialVersionUID = -9023039191257911554L;

      @Override
      public void verifyText( VerifyEvent event ) {
        // Assume we allow it
        event.doit = true;
        String text = event.text;
        char[] chars = text.toCharArray(); // Don't allow if text
        // contains non-digit
        // characters
        for( int i = 0; i < chars.length; i++ ) {
          if( !Character.isDigit( chars[ i ] ) ) {
            event.doit = false;
            break;
          }
        }
      }
    } );
    final FormData fd_generalWidget = new FormData();
    fd_generalWidget.top = new FormAttachment( 0, 0 );
    fd_generalWidget.left = new FormAttachment( 0, 0 );
    fd_generalWidget.right = new FormAttachment( 100, 0 );
    fd_generalWidget.bottom = new FormAttachment( spPageSize, -10 );
    parent.setLayoutData( fd_generalWidget );
    buttonEnd.addSelectionListener( new SelectionListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = -4811132935443402014L;

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        currentPageIndex = maxPageIndex;
        txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
        updateButtonsState();
        refreshData();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
        // TODO Auto-generated method stub
      }
    } );
    buttonBegin.addSelectionListener( new SelectionListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = 2044323196567996364L;

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        currentPageIndex = 1;
        txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
        updateButtonsState();
        refreshData();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
        // TODO Auto-generated method stub
      }
    } );
    txtPageMax.getInputTextWidget().addModifyListener( new ModifyListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = 1L;

      @Override
      public void modifyText( ModifyEvent arg0 ) {
        currentPageIndex = Integer.valueOf( txtPageMax.getInputTextWidget().getText() );
        updateButtonsState();
        refreshData();
      }
    } );
    spPageSize.addSelectionListener( new SelectionListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = -4811132935443402014L;

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        recordsInPage = spPageSize.getSelection();
        txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
        updateMaxPageIndex();
        updateButtonsState();
        refreshData();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
        // TODO Auto-generated method stub
      }
    } );
    buttonLeft.addSelectionListener( new SelectionListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = -4811132935443402014L;

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        if( currentPageIndex > 1 ) {
          currentPageIndex -= 1;
        }
        txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
        updateButtonsState();
        refreshData();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    } );
    buttonRight.addSelectionListener( new SelectionListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = -4811132935443402014L;

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        if( currentPageIndex < maxPageIndex ) {
          currentPageIndex += 1;
        }
        txtPageMax.getInputTextWidget().setText( currentPageIndex + "" );
        updateButtonsState();
        refreshData();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
        // TODO Auto-generated method stub
      }
    } );
  }

  private Image getImg( String iconPath ) {
    URL url = FrameworkUtil.getBundle( this.getClass() )
      .getBundleContext()
      .getBundle()
      .getResource( iconPath );
    ImageDescriptor imgDescriptor = ImageDescriptor.createFromURL( url );
    return imgDescriptor.createImage();
  }
}
