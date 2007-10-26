#!/usr/bin/perl -w


#***********************************************************#
#
#
#***********************************************************#
$path = "C:/TPB/Textprocessning/Textprocessning";
$lang = "eng";
$main_lang = "(?:eng|-)";
$path2 = "C:/TPB/Preproc";
$mode = "align";

require "$path2/lang/$lang/require_files.pl" or die "Cannot find $path2/lang/$lang/require_files.pl: $!";

&read_lists($lang);
#&tie_lists($lang);

if ($string = shift) {

	open OUTPUT,">C:/TPB/Preproc/Output/output.txt";
	&process_string($string,$lang,$mode);

	$output = &ssml_output();

	$output =~ s/<SPLITTER>/\n/g;
	
	&print_all_output();
	
	print "$output\n";
	
} else {
	
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/sou.txt";
#	open INPUT,"C:/TPB/Textprocessning/Testmaterial/testmaterial.txt";
	open INPUT,"C:/TPB/Textprocessning/Testmaterial/eng_test.txt";
	open OUTPUT,">>C:/TPB/Preproc/Output/output.txt";
	while (<INPUT>) {
		chomp;
		next if /^\#/;
		next if $_ !~ /(\w)/i;
		$string = $_;
		
		$output = &process_string($string,$lang);
		
		
		#&print_all_output();
		
	}
	close INPUT;
	close OUTPUT;
}
#***********************************************************#


