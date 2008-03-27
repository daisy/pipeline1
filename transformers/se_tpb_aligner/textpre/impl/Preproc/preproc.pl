#!/usr/bin/perl
#use Encode;
use locale;
use File::Basename;


our $output_log = 1;

$my_lang = 'swe';
$mode = "preproc";

# Synthesis mode
if (($input) = @ARGV) {
	if ($input =~ /^useSTDIN -(...)/) {
		$lang = $1;
		$main_lang = $lang;
	} else {
		$lang = $my_lang;
		$main_lang = $my_lang;
	}
} else {
	$lang = $my_lang;
	$main_lang = $my_lang;
}

$TimeData = 0;

#***********************************************************#
#
#
#***********************************************************#
if ($ENV{'USERNAME'} =~ /^christinae$/i) {
	
	$path = "C:/TPB/Preproc";
	$path2 = "lang";
	$main_path = "C:/TPB/Preproc";
	$preproc_path = "C:/TPB/Preproc";

} elsif ($ENV{'USERNAME'} =~ /^kares$/i) {
	
	$path = "C:/Preproc";
	$path2 = "lang";
	$main_path = "C:/Preproc";
	$preproc_path = "C:/Preproc";


# Linux
} else{
	
	$user = $ENV{'USER'};
	$path = "/home/$user/Preproc";
	$main_path = "/home/$user/Preproc";
	$preproc_path = "lang/";
	$path2 = "/home/$user/Preproc/lang";

#	$dirname = dirname($0);
#	$preproc_path = "$dirname/Preproc";
	$preproc_path = $main_path;
	
	$output_log = 0;
}


# Parser path
$parser_path = "$main_path/Parser/swe/SPARKwRELCL";



# File requirements path
#require "$preproc_path/lang/$lang/require_files.pl" or die "Cannot find $path2/lang/$lang/require_files.pl: $!";

# Read/tie lists (lexicon, abbreviations...)
#&read_lists($lang);
#&tie_lists($lang);

our $tagger_mode = 'preproc';


if ( $output_log == 1 ) {
	open OUTPUT,">$main_path/Output/output.txt" or die "Cannot open OUTPUT: $main_path/Output/output.txt: $!";
}


if (($input) = @ARGV) {
	
	$| = 1;

	if ($input =~ /^useSTDIN/) { ## -(...)/) {
		
		if ( $input =~ /^useSTDIN lang\=(.+) mode\=(.+)$/ ) {
			$lang = $1;
			$mode = $2;
			$main_lang = $lang;
		}

		# File requirements path
		require "$preproc_path/lang/$lang/require_files.pl" or die "Cannot find $path2/lang/$lang/require_files.pl: $!";
		
		# Read/tie lists (lexicon, abbreviations...)
		&read_lists($lang);
		
		while (<STDIN>) {

			chomp ($string = $_);

			$output = &process_string($string,$lang,$mode);
		
####			$output =~ s/<SPLITTER>/\n/g;
			$output =~ s/<SPLITTER>/ /g;
			
			print "$output\n";
		}
	
	} else {
			chomp ($string = $input);

			# File requirements path
			require "$preproc_path/lang/$lang/require_files.pl" or die "Cannot find $path2/lang/$lang/require_files.pl: $!";
			
			# Read/tie lists (lexicon, abbreviations...)
			&read_lists($lang);

			$output = &process_string($string,$lang,$mode);
		
####			$output =~ s/<SPLITTER>/\n/g;
			$output =~ s/<SPLITTER>/ /g;
			
			print "$output\n";
	}

	
# Read from file	
} else {

	open INPUT,"C:/TPB/Textprocessning/Testmaterial/psyk.txt";
#	open INPUT,"D:/Korpus/HS/hs_korpus_080229.txt";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/norska.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/grek2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/grek2.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/grek2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/bok2.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/larandets_konst.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/fordran.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/utmaningomtanke.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/pysk2.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/psykfel.txt" or die "Cannot open C:/TPB/Textprocessning/Testmaterial/psyk2.txt: $!";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/testmaterial.txt";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/eng_test.txt";
#	open INPUT,"C:/TPB/Tag/TPBtag/Test/testtext.txt";

#	open INPUT,"C:/TPB/Narrator/franky2.xml";
	
	# Empty logfile.
	open OUTPUT,">$main_path/Output/output.txt" or die "Cannot open $main_path/Output/output.txt: $!";
	close OUTPUT;
	open SSMLOUT,">$main_path/Output/ssml_out.txt" or die "Cannot open $main_path/Output/ssml_out.txt: $!";
	close SSMLOUT;

	open TIMEOUT,">$main_path/Output/timeout.txt" or die "Cannot open $main_path/Output/timeout.txt: $!";

#_#	if ( $mode eq 'preproc' ) {
#_#		open TAGOUT,">C:/TPB/Tag/TPBtag/Test/viterbi.out";
#_#	}

	

	# File requirements path
	require "$preproc_path/lang/$lang/require_files.pl" or die "Cannot find $path2/lang/$lang/require_files.pl: $!";
		
	# Read/tie lists (lexicon, abbreviations...)
	&read_lists($lang);
	
	my $line_count = 0;
	open SSMLOUT,">>C:/TPB/Preproc/Output/ssml_out.txt";
	open OUTPUT,">>C:/TPB/Preproc/Output/output.txt";
	while (<INPUT>) {
		chomp;
		
#		s/?//;	# Remove header
		
		$line_count++;
		#next if $line_count < 300;
		#next if $line_count > 400;
		next if /^\#/;
		next if $_ !~ /(\w)/i;
		
		$string = $_;


#		print OUTPUT "S $string\n";

		$string =~ s/\t/ /g;
		$string = &clean_blanks($string);
		
 #$string .= decode($encoding, $buffer, Encode::FB_WARN);

		#$data = encode("utf8", decode("iso-8859-1", $data));
		#$string = decode("utf8", $string, Encode::FB_WARN);
		
#		$string = decode("utf8", $string) ;

		$string =~ s/($char_norm_list)/$char_norm_list{ $1 }/g;
		$string =~ s/Ã /à/g;
		

#		$string =~ s/?/-/g;
		
#		$string = decode_utf8( $string );

		$output = &process_string($string,$lang,$mode);

#		$output = &ssml_output();

###		$string = encode_utf8( $string );

#		print SSMLOUT "$string\n$output\n\n\n";

#		&print_all_output();
		
		
	}
	close INPUT;
	close OUTPUT;
	close SSMLOUT;
}
#***********************************************************#


