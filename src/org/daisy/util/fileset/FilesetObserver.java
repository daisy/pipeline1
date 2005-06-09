package org.daisy.util.fileset;

import java.net.URI;

/**
 * Acts as an observer and mediator between a registered fileset and loosely coupled instantiated members
 * @author Markus Gylling
 */

public class FilesetObserver {
    private Fileset currentListener = null;
        
    private FilesetObserver(){}
    
    static private FilesetObserver _instance = null;    
    
    static public FilesetObserver getInstance() {
        if (null == _instance) _instance = new FilesetObserver();        
        return _instance;
    }
    
    /**
     * @param listener 
     * @throws TooManyListenersException Only one listener at a time.
     */
    public void addListener(Fileset listener) throws FilesetException {
        if (currentListener==null) {
            currentListener=listener;
        }else{
        	throw new FilesetException("Too many listeners; only one at a time");
        }        
    }
    
    public void removeListener(Fileset listener) {
        currentListener = null;
    }

    protected Fileset getCurrentListener() {
    	return currentListener;
    }

    public void errorEvent(URI uri,Exception e) {       
        //currentListener.addError(uri, e);       
    	currentListener.addError(e,uri);
    }

    public void localResourceEvent(AbstractFile file) {       
        currentListener.addLocalMember(file);       
    }
    
    public void remoteResourceEvent(String uri) {       
        currentListener.addRemoteMember(uri);       
    }
            
}
