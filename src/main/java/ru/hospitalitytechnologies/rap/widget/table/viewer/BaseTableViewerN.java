package ru.hospitalitytechnologies.rap.widget.table.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ru.hospitalitytechnologies.rap.widget.internal.helpers.SizeHelper;


public abstract class BaseTableViewerN< T > extends TableViewer {

  private static final long serialVersionUID = -6205225470094377986L;
  private static int MIN_COLUMN_WIDTH = 25;

  abstract protected String getDataItemFieldText( T element, int columnIndex );

  abstract protected Color getColor( T element );

  protected Color getForegroundColor( T element ) {
    return null;
  }

  public BaseTableViewerN( final Table table,
                           final String[] columnNames,
                           double width,
                           double height )
  {
    super( table );
    table.setLinesVisible( true );
    table.setHeaderVisible( true );
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    TableColumnLayout layout = new TableColumnLayout();
    table.getParent().setLayout( layout );
    final int columnWidthSize = ( int )( SizeHelper.getCurrentWorkBenchSize().x * width )
                                / columnNames.length;
    setColumnProperties( columnNames );
    for( int i = 0; i < columnNames.length; i++ ) {
      final int columnIndex = i;
      final TableViewerColumn col = createTableViewerColumn( this,
                                                             columnNames[ i ],
                                                             columnWidthSize,
                                                             i );
      layout.setColumnData( col.getColumn(), new ColumnWeightData( columnWidthSize ) );
      col.setLabelProvider( new ColumnLabelProvider() {

        private static final long serialVersionUID = 602676785386022201L;

        @SuppressWarnings( "unchecked" )
        @Override
        public String getText( final Object element ) {
          return getDataItemFieldText( ( T )element, columnIndex );
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public Color getBackground( Object element ) {
          return getColor( ( T )element );
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public Color getForeground( Object element ) {
          return getForegroundColor( ( T )element );
        }
      } );
      final GridData gdTbl = new GridData( GridData.FILL, GridData.FILL, true, true );
      gdTbl.heightHint = ( int )( SizeHelper.getCurrentWorkBenchSize().y * height );
      table.setLayoutData( gdTbl );
      setUseHashlookup( true );
      setContentProvider( new ArrayContentProvider() );
    }
    Listener setDataListener = new Listener() {

      private static final long serialVersionUID = -3067547872921690430L;

      public void handleEvent( Event event ) {
        final TableItem item = ( TableItem )event.item;
        wrapItem( item );
      }
    };
    table.addListener( SWT.SetData, setDataListener );
  }

  public void setInput( T[] input ) {
    TableItem[] items = this.getTable().getItems();
    for( TableItem item : items )
      item.setData( "Labels", null );
    super.setInput( input );
  }

  public void setInput( List< T > input ) {
    TableItem[] items = this.getTable().getItems();
    for( TableItem item : items )
      item.setData( "Labels", null );
    super.setInput( input );
  }

  private void wrapAllItems() {
    TableItem[] items = this.getTable().getItems();
    for( TableItem item : items )
      wrapItem( item );
  }

  private String[] matchText( String text ) {
    Pattern pattern = Pattern.compile( ">(.*?)<" );
    Matcher matcher = pattern.matcher( text );
    boolean didMatch = matcher.find();
    String leftText = "";
    String rightText = "";
    if( didMatch ) {
      leftText = text.substring( 0, matcher.start() + 1 );
      rightText = text.substring( matcher.end() - 1 );
      text = text.substring( matcher.start() + 1, matcher.end() - 1 );
    }
    return new String[] {
      leftText, text, rightText
    };
  }

  @SuppressWarnings( "unchecked" )
  private void wrapItem( TableItem item ) {
    Table table = this.getTable();
    GC gc = new GC( table );
    ArrayList< String > arrLabels = null;
    Boolean isNewList = false;
    if( item.getData( "Labels" ) != null )
      arrLabels = ( ArrayList< String > )item.getData( "Labels" );
    else {
      isNewList = true;
      arrLabels = new ArrayList< String >();
    }
    for( int i = 0; i < table.getColumnCount(); i++ ) {
      String text = null;
      if( isNewList ) {
        text = item.getText( i );
        arrLabels.add( text );
      } else
        text = arrLabels.get( i );
      String[] arrText = matchText( text );
      text = arrText[ 1 ];
      StringBuilder builder = new StringBuilder();
      TableColumn column = table.getColumn( i );
      if( column.getWidth() > MIN_COLUMN_WIDTH ) {
        int maxTextSize = column.getWidth() - gc.textExtent( "  " ).x - 2;
        String[] words = text.split( " " );
        int curerntPos = 0;
        for( int k = 0; k < words.length; k++ ) {
          curerntPos = wrap( builder, gc, maxTextSize, curerntPos, words[ k ], "<br/>" );
        }
        item.setText( i, arrText[ 0 ] + builder.toString() + arrText[ 2 ] );
        int newWidth = 0;
        Point sizeText = gc.textExtent( text );
        int wholeNum = sizeText.x / ( maxTextSize );
        int moduloNum = sizeText.x % ( maxTextSize );
        if( wholeNum > 0 )
          if( moduloNum > 0 )
            newWidth = ( sizeText.y + 2 ) * ( wholeNum + 1 );
          else
            newWidth = ( sizeText.y + 2 ) * wholeNum;
        if( newWidth > item.getBounds().height )
          table.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( newWidth ) );
      }
    }
    item.setData( "Labels", arrLabels );
  }

  private static int wrap( StringBuilder builder,
                           GC gc,
                           int maxTextSize,
                           Integer curerntPos,
                           String word,
                           String wrapStr )
  {
    if( word != null && !word.isEmpty() ) {
      int sizeTextStr = gc.textExtent( word ).x;
      int spaceSize = gc.textExtent( " " ).x;
      int currentSize = sizeTextStr + curerntPos;
      if( curerntPos != 0 )
        currentSize += spaceSize;
      if( currentSize > maxTextSize ) {
        if( curerntPos != 0 ) {
          builder.append( wrapStr );
          curerntPos = 0;
        }
        int j;
        for( j = 0; j < word.length(); j++ ) {
          String strToAppend = word.substring( 0, j );
          sizeTextStr = gc.textExtent( strToAppend ).x;
          if( sizeTextStr > maxTextSize ) {
            if( j != 0 ) {
              String addStr = word.substring( 0, j - 1 );
              builder.append( addStr );
              curerntPos += gc.textExtent( addStr ).x;
              break;
            } else
              return curerntPos;
          }
        }
        if( j != word.length() ) {
          word = word.substring( j - 1, word.length() );
          curerntPos = wrap( builder, gc, maxTextSize, curerntPos, word, wrapStr );
        } else {
          builder.append( word );
          curerntPos += gc.textExtent( word ).x;
        }
      } else {
        if( curerntPos != 0 ) {
          builder.append( " " );
          curerntPos += spaceSize;
        }
        builder.append( word );
        curerntPos += sizeTextStr;
      }
    }
    return curerntPos;
  }

  private TableViewerColumn createTableViewerColumn( final TableViewer viewer,
                                                     final String title,
                                                     final int bound,
                                                     final int colNumber )
  {
    final TableViewerColumn viewerColumn = new TableViewerColumn( viewer, SWT.CENTER );
    final TableColumn column = viewerColumn.getColumn();
    column.addControlListener( new ControlListener() {

      private static final long serialVersionUID = 1459196879908567517L;

      @Override
      public void controlResized( ControlEvent arg0 ) {
        wrapColumn( column, title );
        viewer.getTable().setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 27 ) );
        wrapAllItems();
      }

      @Override
      public void controlMoved( ControlEvent arg0 ) {
      }
    } );
    column.setWidth( bound );
    wrapColumn( column, title );
    column.setResizable( true );
    column.setMoveable( false );
    return viewerColumn;
  }

  private static void wrapColumn( TableColumn column, String text ) {
    final GC gc = new GC( column.getDisplay() );
    StringBuilder builder = new StringBuilder();
    int maxTextSize = column.getWidth() - gc.textExtent( "  " ).x - 2;
    if( column.getWidth() > MIN_COLUMN_WIDTH ) {
      String[] words = text.split( " " );
      int curerntPos = 0;
      for( int k = 0; k < words.length; k++ ) {
        curerntPos = wrap( builder, gc, maxTextSize, curerntPos, words[ k ], "\n" );
      }
      column.setText( builder.toString() );
    }
  }
}
