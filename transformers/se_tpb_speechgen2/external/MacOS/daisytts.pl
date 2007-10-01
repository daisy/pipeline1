#! /usr/bin/perl

$voice = $ARGV[0];
$results = "OK";
local $| = 1;

while ($results eq 'OK') {
	my $filename = <STDIN>; #the file to store the audio in.
	chomp($filename);
	if ($filename eq "") {exit;}
	        
	my $phrase = <STDIN>; #the phase to say.
	chomp($phrase);
	if ($phrase eq "") {exit;} 
	
	
	my $say = "/usr/bin/say"; #path to the say application.
	my $lame = "/usr/local/bin/lame"; #path to lame to make the mp3 files.
	my $sox = "/usr/local/bin/sox"; #path to lame to make the mp3 files.
	
	if ($voice ne '') {
		# create a voice command option for say if voice is given.
		$voice_cmd = "-v '$voice' ";
	} else {
		# otherwise use the selected system voice.
		$voice_cmd = "";
	}
	
	
	
	
	#filename handling.
	# remove any extention that was given to the file fromthe calling program.
	$filename =~ s/\..[a-zA-Z0-9]+$//gi; 
	# make sure teh file extention is .aiff
	if ($filename =~ /\.aiff/gi) {} else {$filename = $filename . ".aiff";}
	
	$mp3file = $filename . ".mp3";
	$mp3file =~ s/\.aiff//gi; #remove the .aiff extention from the mp3 filename.
	
	$wav3file = $filename . ".wav";
	$wav3file =~ s/\.aiff//gi; #remove the .aiff extention from the mp3 filename.
	
	
	#recording using say. The voice is choosen in the MacOS Speech preferences.

	`$say $voice_cmd -o "$filename" "$phrase"; $sox "$filename" "$wav3file"`;
	
	unlink("$filename"); #remove the aiff file.
	
	if (-e $wav3file) {
		#return OK
		$results = "OK";
		print "OK\n";
	} else {
		#no wav file so return an error.
		$results = "error";
		print "error no file\n";
	}
	
}