package org.daisy.util.dtb.validation.errorscout;

/**
 * @author Markus Gylling
 */
public final class DtbErrorScoutingLevel {

	private DtbErrorScoutingLevel() {}          
    /**
     * <p>Testlevel <code>SLIM</code> performs fundamental integrity tests on the DTB:</p>
     * <ul>
     * <li>file existance</li>
     * <li>file readability</li>
     * <li>DTD Validity</li>
     * <li>Interdocument fragment link integrity</li>
     * </ul>
     */
    public static final DtbErrorScoutingLevel SLIM = new DtbErrorScoutingLevel();
    
    /**
     * <p>Testlevel <code>MEDIUM</code> performs all tests of level SLIM, plus:</p>
     * <ul>
     * <li>execution of RelaxNG, Smil timing and Mp3 tests</li>
     * </ul>
     */
    public static final DtbErrorScoutingLevel MEDIUM = new DtbErrorScoutingLevel();
    
    /**
     * <p>Testlevel <code>MAXED</code> performs all tests of the SLIM and MEDIAN levels, plus:</p> 
     * <ul>
     * <li>additional (timeconsuming) Schematron tests on metadata.</li>
     * </ul>
     * <p>Note: do not assume that level MAXED performs full conformance validation. For that, get a tool such as ZedVal</p>
     */
    public static final DtbErrorScoutingLevel MAXED = new DtbErrorScoutingLevel();


}
