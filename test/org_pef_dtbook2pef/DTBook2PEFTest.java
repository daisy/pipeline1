package org_pef_dtbook2pef;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DTBook2PEFTest {

	public DTBook2PEFTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testDotifyVersion_01() {
		List<String> version = DTBook2PEF.getDotifyVersion(new File("test/org_pef_dtbook2pef/version"));
		assertEquals("2", version.get(0));
		assertEquals("4", version.get(1));
		assertEquals("0", version.get(2));
		assertEquals(3, version.size());
	}

	@Test
	public void testDotifyVersion_02() {
		List<String> version = DTBook2PEF.getDotifyVersion(new File("test/org_pef_dtbook2pef/version2"));
		assertEquals("2", version.get(0));
		assertEquals("4", version.get(1));
		assertEquals("1", version.get(2));
		assertEquals(3, version.size());
	}

	@Test
	public void testDotifyVersion_03() {
		List<String> version = DTBook2PEF.getDotifyVersion(new File("test/org_pef_dtbook2pef/version3"));
		assertEquals("2", version.get(0));
		assertEquals("4", version.get(1));
		assertEquals("1", version.get(2));
		assertEquals(3, version.size());
	}

	@Test
	public void testDotifyVersion_noFile() {
		List<String> version = DTBook2PEF.getDotifyVersion(new File("test/org_pef_dtbook2pef/version23"));
		assertEquals("2", version.get(0));
		assertEquals("0", version.get(1));
		assertEquals("0", version.get(2));
		assertEquals(3, version.size());
	}


	@Test
	public void testDotifyVersion_04() {
		List<String> version = DTBook2PEF.getDotifyVersion(new File("test/org_pef_dtbook2pef/version4"));
		assertEquals("3", version.get(0));
		assertEquals("0", version.get(1));
		assertEquals("0", version.get(2));
		assertEquals(3, version.size());
	}
}
