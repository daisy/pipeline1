#!/usr/bin/perl

use XML::Parser;
use utf8;
use File::Basename;

# * perl path/textprocXML.pl --inputXML=pathspec --inputLanguage=langspec --resultPath=pathspec --mode=align 
 
 ( $dummy,$xmlfile ) = split/\=/,$ARGV[0];
 ( $dummy,$xml_lang ) = split/\=/,$ARGV[1];
 ( $dummy,$outfile ) = split/\=/,$ARGV[2];
 ( $dummy,$mode ) = split/\=/,$ARGV[3];
 

# C:\TPB\Aligner\textpre>perl textprocXML.pl --inputXML=C:/eclipse/aligner-input/xml/1/christ-pages.xml --inputLanguage=en --resultPath=C:/TPB/Aligner/textpre/out.xml --mode=align  

open OUT,">:utf8","$outfile" or die "Cannot open $outfile: $!";

#$preproc_path = "C:/eclipse/workspace/pipeline2/transformers/se_tpb_aligner/textpre/impl/Preproc";

$dirname = dirname($0);
$preproc_path = "$dirname/Preproc";
 

$| = 1;

if ( $xml_lang =~ /^en/i) {
#	$eng_path = "C:/TPB/Preproc";

	require "$preproc_path/lang/eng/require_files.pl";

	&read_lists($lang);
	
} elsif ( $xml_lang eq "sv" ) {
#	$swe_path = "C:/TPB/Textprocessning/Textprocessning/Scripts";
#	require "$swe_path/textprocessning.pl";
#	$swe_path = "C:/TPB/Preproc";
	require "$preproc_path/lang/swe/require_files.pl";
	&read_lists($lang);
	
}


my $parser = new XML::Parser;

$parser->setHandlers(    
	Start => \&startElement,
	End => \&endElement,
	Char => \&characterData,
	# Default => \&default
);

$parser->parsefile($xmlfile);

sub startElement {
	my( $parseinst, $element, %attrs ) = @_;

	$pageFlag = 0;
	
	print OUT "<$element";
	while (($k,$v) = each ( %attrs )) {
		print OUT " $k\=\"$v\"";
	}

	# SSML
	if ( $element eq "dtbook" ) {
		print OUT " xmlns:ssml=\"http://www.w3.org/2001/10/synthesis\"";
	}
			
	if ( $element ne "meta" ) {
		print OUT ">";
	}

	# Structure announcer
	if ( $element =~ /blockquote/i ) {
		print OUT "<ssml:sub ssml:alias\=\"Quote.\"><\/ssml:sub>";
	
	} elsif ( $element =~ /table/i ) {
		print OUT "<ssml:sub ssml:alias\=\"Table.\"><\/ssml:sub>";
	
	} elsif ( $element =~ /caption/i ) {
		print OUT "<ssml:sub ssml:alias\=\"Caption.\"><\/ssml:sub>";
	
	} elsif ( $element =~ /sidebar/i ) {
		print OUT "<ssml:sub ssml:alias\=\"Side bar.\"><\/ssml:sub>";
	
	} elsif ( $element =~ /page/i ) {
		$pageFlag = 1;
	}

}

sub endElement {
	my( $parseinst, $element ) = @_;
	
	# Structure announcer
	if ( $element eq "blockquote" ) {
		print OUT "<ssml:sub ssml:alias\=\"End of quote.\"><\/ssml:sub>";
	
	} elsif ( $element eq "table" ) {
		print OUT "<ssml:sub ssml:alias\=\"End of table.\"><\/ssml:sub>";
	
	} elsif ( $element eq "caption" ) {
		print OUT "<ssml:sub ssml:alias\=\"End of caption.\"><\/ssml:sub>";
	
	} elsif ( $element eq "sidebar" ) {
		print OUT "<ssml:sub ssml:alias\=\"End of side bar.\"><\/ssml:sub>";
	}		

	if ( $element eq "meta" ) {
		print OUT "/>";
	} else {
		print OUT "</$element>";
	}



}


sub characterData {
       my( $parseinst, $data ) = @_;

	# Swedish text processing
	if ( $xml_lang eq "sv" ) {
				
		$data = &process_string($data,"swe",$mode);
						
	# English text processing
	} elsif ( $xml_lang =~ /^en/i ) {
		$data = &process_string($data,"eng",$mode);
		
	}

	# Page numbers
	if ( $pageFlag == 1 ) {
		print OUT "<ssml:sub ssml:alias\=\"Page.\"><\/ssml:sub>";
	}
	
	$pageFlag = 0;
						
	# Remove unnecessary ssml (e.g. break information)
	$data = &clean_ssml($data);
	
	print OUT "$data";

}
#*************************************************************************#
# Remove unnecessary ssml.
#*************************************************************************#
sub clean_ssml {

	my $str = shift;

	$str =~ s/<break time\=\"\d+ms\"\/>/ /g;
	
#	$str =~ s/([<>\"\'\&])/<\!\[CDATA\[$1\]\]>/g;
	
	$str =~ s/^ *//;
	$str =~ s/ *$//;

	return $str;
}
#*************************************************************************#


