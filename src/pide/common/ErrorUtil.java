/*
 * Includes code cribbed from StatusUtil. Copied here because StatusUtil is an
 * internal API.
 */
package pide.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Utility class for handling warning and error statuses by logging and/or
 * displaying to the user.
 */
public class ErrorUtil {
  /**
   * Severity levels.
   */
  public static enum Level {
    /**
     * Nominal status.
     */
    OK(IStatus.OK),

    /**
     * Status is an error.
     */
    ERROR(IStatus.ERROR),

    /**
     * Status is informational.
     */
    INFO(IStatus.INFO),

    /**
     * Status is a warning.
     */
    WARNING(IStatus.WARNING),

    /**
     * Status is a cancellation.
     */
    CANCEL(IStatus.CANCEL);

    /**
     * The corresponding IStatus severity level.
     */
    private int severity;

    Level(int severity) {
      this.severity = severity;
    }
  }

  /**
   * Converts an IStatus severity number to a Level.
   * 
   * @param defaultLevel
   *          the default level to return in case the given {@code severity}
   *          number is undefined.
   */
  public static Level toLevel(int severity, Level defaultLevel) {
    switch (severity) {
    case IStatus.OK:
      return Level.OK;
    case IStatus.ERROR:
      return Level.ERROR;
    case IStatus.INFO:
      return Level.INFO;
    case IStatus.WARNING:
      return Level.WARNING;
    case IStatus.CANCEL:
      return Level.CANCEL;
    default:
      return defaultLevel;
    }
  }

  /**
   * Styles by which errors can be handled.
   */
  public static enum Style {
    /**
     * A style indicating that the error should not be acted on.
     */
    NONE(StatusManager.NONE),

    /**
     * A style indicating that the error should be logged.
     */
    LOG(StatusManager.LOG),

    /**
     * A style indicating that the user should be notified of the error without
     * blocking the calling method while waiting for a user response. Typically
     * done with a non-modal dialog.
     */
    SHOW(StatusManager.SHOW),

    /**
     * A style indicating that the calling thread should be blocked until the
     * error is acknowledged by the user. Typically done with a modal dialog.
     */
    BLOCK(StatusManager.BLOCK);

    /**
     * The corresponding StatusManager style number.
     */
    private int style;

    Style(int style) {
      this.style = style;
    }
  }

  /**
   * Handles an error by logging it and/or displaying it to the user.
   * 
   * @param severity
   *          the severity of the error.
   * @param pluginId
   *          the id of the plug-in from which the error originated.
   * @param title
   *          the title of any dialog that will be shown to the user.
   * @param message
   *          a human-readable message.
   * @param exception
   *          the exception being handled, or {@code null} if none.
   * @param styles
   *          the styles by which the error should be handled.
   */
  public static void handleError(Level severity, String pluginId, String title,
      String message, Throwable exception, Style... styles) {
    // If the message is empty, derive one from the exception.
    if ((message == null || message.trim().length() == 0) && exception != null) {
      message = exception.getMessage();
      if (message == null) {
        message = exception.toString();
      }
    }

    Status status =
        new Status(severity.severity, pluginId, message, getCause(exception));
    StatusAdapter statusAdapter = new StatusAdapter(status);
    statusAdapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, title);

    int style = StatusManager.NONE;
    for (Style s : styles)
      style |= s.style;
    StatusManager.getManager().handle(statusAdapter, style);
  }

  /**
   * Handles an error by logging it and/or displaying it to the user.
   * 
   * @param severity
   *          the severity of the error.
   * @param pluginId
   *          the id of the plug-in from which the error originated.
   * @param title
   *          the title of any dialog that will be shown to the user.
   * @param exception
   *          the exception being handled, or {@code null} if none.
   * @param styles
   *          the styles by which the error should be handled.
   */
  public static void handleError(Level severity, String pluginId, String title,
      Throwable exception, Style... styles) {
    handleError(severity, pluginId, title, null, exception, styles);
  }

  /**
   * Determines the exception that should actually be logged. If the given
   * exception is a wrapper, unwrap it.
   */
  private static Throwable getCause(Throwable exception) {
    if (exception == null) return null;

    if (exception instanceof CoreException) {
      Throwable result = exception.getCause();
      if (result != null) return result;
    }
    
    return exception;
  }
}
