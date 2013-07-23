package ru.hospitalitytechnologies.rap.widget.input;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class InputWithText extends Composite {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private Text textInput;
  private Text textComment;

  public InputWithText( Composite parent, int style ) {
    super( parent, style );
    draw();
  }

  protected void draw() {
    setLayout( new FormLayout() );
    textInput = new Text( this, SWT.RIGHT );
    FormData fd_textInput = new FormData();
    fd_textInput.top = new FormAttachment( 0, 0 );
    fd_textInput.left = new FormAttachment( 0, 0 );
    fd_textInput.right = new FormAttachment( 45, 0 );
    textInput.setLayoutData( fd_textInput );
    textComment = new Text( this, SWT.READ_ONLY | SWT.LEFT );
    FormData fd_textComment = new FormData();
    fd_textComment.top = new FormAttachment( 0, 0 );
    fd_textComment.left = new FormAttachment( textInput, 0 );
    fd_textComment.right = new FormAttachment( 100, 0 );
    textComment.setLayoutData( fd_textComment );
  }

  public Text getInputTextWidget() {
    return textInput;
  }

  public Text getCommentTextWidget() {
    return textComment;
  }
}
