package ru.hospitalitytechnologies.rap.widget.internal.helpers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;


public class SizeHelper {

  public static Point getCurrentWorkBenchSize() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getSize();
  }
}
