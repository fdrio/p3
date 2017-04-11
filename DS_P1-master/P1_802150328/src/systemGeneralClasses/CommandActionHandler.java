 package systemGeneralClasses;

import java.util.ArrayList;

/**
 * Specifies the type of object that handles the actions
 * required for a particular command.
 * @author pirvos
 *
 */
public interface CommandActionHandler { 
    ArrayList<String> execute(Command c); 
}
