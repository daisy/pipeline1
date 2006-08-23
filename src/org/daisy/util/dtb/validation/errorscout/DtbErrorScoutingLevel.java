/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.dtb.validation.errorscout;

/**
 * Constants defining various scopes of testing.
 * @author Markus Gylling
 */
public final class DtbErrorScoutingLevel {

	private DtbErrorScoutingLevel() {}          
    /**
     * Testlevel <code>SLIM</code> performs fundamental integrity tests on the DTB.
     * <ul>
     * <li>file existance</li>
     * <li>file readability</li>
     * <li>DTD Validity</li>
     * <li>Interdocument fragment link integrity</li>
     * </ul>
     */
    public static final DtbErrorScoutingLevel SLIM = new DtbErrorScoutingLevel();
    
    /**
     * Testlevel <code>MEDIUM</code> performs all tests of level SLIM, with additions
     * <ul>
     * <li>execution of RelaxNG, Smil timing and Mp3 tests</li>
     * <li>Schematron tests on manifest files (ncc, opf)</li>
     * </ul>
     */
    public static final DtbErrorScoutingLevel MEDIUM = new DtbErrorScoutingLevel();
    
    /**
     * Testlevel <code>MAXED</code> performs all tests of the SLIM and MEDIAN levels, with additions 
     * <ul>
     * <li>additional (timeconsuming) Schematron tests (on smil files, etc).</li>
     * </ul>
     * <p>Note: do not assume that level MAXED performs full conformance validation. For that, get a tool such as ZedVal</p>
     */
    public static final DtbErrorScoutingLevel MAXED = new DtbErrorScoutingLevel();

    /**
     * Parses a string representation of a level into a <code>DtbErrorScoutingLevel</code>. 
     * @param level the level to parse
     * @return a <code>DtbErrorScoutingLevel</code>
     * @throws IllegalArgumentException
     */
    public static DtbErrorScoutingLevel parse(String level) throws IllegalArgumentException {
        if ("SLIM".equals(level)) {
            return SLIM;
        }
        if ("MEDIUM".equals(level)) {
            return MEDIUM;
        }
        if ("MAXED".equals(level)) {
            return MAXED;
        }
        throw new IllegalArgumentException("DtbErrorScoutingLevel must be one of SLIM, MEDIUM or MAXED");
    }
}
