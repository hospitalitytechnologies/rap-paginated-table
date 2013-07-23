package ru.hospitalitytechnologies.rap.widget.table.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import ru.hospitalitytechnologies.rap.widget.internal.helpers.SizeHelper;


public class BaseTreeViewerN< T > extends TreeViewer {

  /**
	 * 
	 */
  private static final long serialVersionUID = 2680794416291671527L;
  private static int MIN_COLUMN_WIDTH = 25;
  private static int SIZE_OFFSET = 16;

  public BaseTreeViewerN( final Tree tree,
                          ITableLabelProvider labelProvider,
                          ITreeContentProvider treeContentProvider,
                          final String[] columnNames,
                          double width )
  {
    super( tree );
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 27 ) );
    tree.setLinesVisible( true );
    tree.setHeaderVisible( true );
    TreeColumnLayout layout = new TreeColumnLayout();
    tree.getParent().setLayout( layout );
    if( labelProvider != null )
      this.setLabelProvider( labelProvider );
    if( treeContentProvider != null )
      this.setContentProvider( treeContentProvider );
    this.setUseHashlookup( true );
    if( columnNames != null ) {
      int columnWidthSize = ( int )( ( SizeHelper.getCurrentWorkBenchSize().x * width ) / columnNames.length );
      for( int i = 0; i < columnNames.length; i++ ) {
        TreeColumn column = createTreeViewerColumn( this, columnNames[ i ], columnWidthSize, i );
        layout.setColumnData( column, new ColumnWeightData( columnWidthSize ) );
      }
    }
    tree.addTreeListener( new TreeListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = 6557091058711596917L;

      @Override
      public void treeExpanded( TreeEvent event ) {
        TreeItem item = ( TreeItem )event.item;
        BaseTreeViewerN.this.wrapText( item.getItems(), 0 );
      }

      @Override
      public void treeCollapsed( TreeEvent arg0 ) {
      }
    } );
  }

  public void setInput( T[] input ) {
    TreeItem[] items = this.getTree().getItems();
    clearItems( items );
    super.setInput( input );
    this.wrapText( this.getTree().getItems(), 0 );
  }

  public void setInput( List< T > input ) {
    TreeItem[] items = this.getTree().getItems();
    clearItems( items );
    super.setInput( input );
    this.wrapText( this.getTree().getItems(), 0 );
  }

  private void clearItems( TreeItem[] items ) {
    for( TreeItem item : items )
      item.setData( "Labels", null );
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
  private void wrapText( TreeItem items[], int numParents ) {
    Tree tree = this.getTree();
    GC gc = new GC( tree.getDisplay() );
    for( TreeItem item : items ) {
      ArrayList< String > arrLabels = null;
      Boolean isNewList = false;
      if( item.getData( "Labels" ) != null )
        arrLabels = ( ArrayList< String > )item.getData( "Labels" );
      else {
        isNewList = true;
        arrLabels = new ArrayList< String >();
      }
      for( int i = 0; i < tree.getColumnCount(); i++ ) {
        StringBuilder builder = new StringBuilder();
        TreeColumn column = tree.getColumn( i );
        int minWidth = MIN_COLUMN_WIDTH;
        if( i == 0 )
          minWidth += SIZE_OFFSET * ( numParents + 1 );
        if( column.getWidth() > minWidth ) {
          String text = "";
          if( isNewList ) {
            text = item.getText( i );
            arrLabels.add( text );
          } else if( arrLabels.size() > i )
            text = arrLabels.get( i );
          String[] arrText = matchText( text );
          text = arrText[ 1 ];
          text = text.replaceAll( "<br/>", "" );
          int maxTextSize = column.getWidth() - gc.textExtent( "  " ).x - 2;
          if( i == 0 ) {
            maxTextSize -= SIZE_OFFSET * ( numParents + 1 );
          }
          String[] words = text.split( " " );
          int curerntPos = 0;
          for( int k = 0; k < words.length; k++ ) {
            curerntPos = wrap( builder, gc, maxTextSize, curerntPos, words[ k ], "<br/>" );
          }
          item.setText( i, arrText[ 0 ] + builder.toString() + arrText[ 2 ] );
          int countLines = builder.toString().split( "<br/>" ).length + 1;
          Point sizeText = gc.textExtent( text );
          int newWidth = ( sizeText.y ) * ( countLines );
          int oldWidth = ( Integer )tree.getData( RWT.CUSTOM_ITEM_HEIGHT );
          if( newWidth > oldWidth )
            tree.setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( newWidth ) );
        }
      }
      if( item.getItemCount() > 0 )
        wrapText( item.getItems(), numParents + 1 );
      item.setData( "Labels", arrLabels );
    }
  }

  public TreeColumn createTreeViewerColumn( final TreeViewer treeViewer,
                                            final String title,
                                            final int bound,
                                            final int colNumber )
  {
    final TreeColumn column = new TreeColumn( treeViewer.getTree(), SWT.CENTER );
    column.addControlListener( new ControlListener() {

      /**
			 * 
			 */
      private static final long serialVersionUID = 1L;

      @Override
      public void controlResized( ControlEvent arg0 ) {
        wrapColumn( column, title );
        treeViewer.getTree().setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 27 ) );
        BaseTreeViewerN.this.wrapText( treeViewer.getTree().getItems(), 0 );
      }

      @Override
      public void controlMoved( ControlEvent arg0 ) {
      }
    } );
    column.setWidth( bound );
    column.setResizable( true );
    wrapColumn( column, title );
    column.setAlignment( SWT.CENTER );
    return column;
  }

  private void wrapColumn( TreeColumn column, String text ) {
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

  private int wrap( StringBuilder builder,
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
}
