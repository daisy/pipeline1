#!/usr/bin/perl -w

# Converting TPA to CMU transcription alphabet.

sub tpa2cmu {
	my $string = shift;

	$string =~ s/^([^\|]+)\s*\|.+/$1/;

	# Remove stress
	$string =~ s/(?:[\'\"\`]|\&quot\;)/ /g;
	
	# Remove "."
	$string =~ s/\.//g;

	# Remove word and syllable breaks
	$string =~ s/[\_\-\|]/ /g;

	# Remove consonant length
	$string =~ s/($consonants_ort):/$1/og;

	# Long vowels without length mark
	$string =~ s/2 /2: /g;
	$string =~ s/2$/2:/g;

	# Two phonemes --> one phoneme
	$string =~ s/\ba j\b/ay/g;
	$string =~ s/\be j\b/ey/g;
	$string =~ s/\bå j\b/oy/g;

	$string =~ s/\bë r3\b/er/g;
	$string =~ s/\bë r\b/er/g;
	$string =~ s/\bö3: r3\b/er/g;
	$string =~ s/\bö3 r3\b/er/g;
	$string =~ s/\bö3: r\b/er/g;
	$string =~ s/\bö3 r\b/er/g;


	$string = &clean_blanks($string);


	my @string = split/ +/,$string;
	
	
	foreach my $s ( @string ) {
		
		if ( exists( $tpa2cmu{ $s } ) ) {
			$s = $tpa2cmu{ $s };
		} elsif ( $s !~ /^(?:ay|ey|oy|er)$/ ) {
#			print "\n$s does not exist in tpa2cmu\n\n";
		}
	}
	$string = join" ",@string;
	return $string;
}
1;
