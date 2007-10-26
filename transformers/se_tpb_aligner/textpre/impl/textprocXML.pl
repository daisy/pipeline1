#!/usr/bin/perl

use XML::Parser;
use utf8;
use File::Basename;

# * perl path/textprocXML.pl --inputXML=pathspec --inputLanguage=langspec --resultPath=pathspec --mode=align 
 
 ( $dummy,$xmlfile ) = split/\=/,$ARGV[0];
 ( $dummy,$xml_lang ) = split/\=/,$ARGV[1];
 ( $dummy,$outfile ) = split/\=/,$ARGV[2];
 ( $dummy,$mode ) = split/\=/,$ARGV[3];
 
 
 
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
}

sub endElement {
       my( $parseinst, $element ) = @_;
	
	if ( $element eq "meta" ) {
		print OUT "/>";
	} else {
		print OUT "</$element>";
	}
}


sub characterData {
       my( $parseinst, $data ) = @_;


#		if ( $data !~ /^\s*$/ ) {

			# Swedish text processing
			if ( $xml_lang eq "sv" ) {
				
				$data = &process_string($data,"swe",$mode);
						
			# English text processing
			} elsif ( $xml_lang =~ /^en/i ) {
				
				$data = &process_string($data,"eng",$mode);
				
			}
						
			# Remove unnecessary ssml (e.g. break information)
			$data = &clean_ssml($data);
	
			print OUT "$data";
#		}

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


