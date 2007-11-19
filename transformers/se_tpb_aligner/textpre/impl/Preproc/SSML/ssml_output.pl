#!/usr/bin/perl -w


#***********************************************************#
# 
# 
#
#
#***********************************************************#
sub ssml_output {
	
	my @text = ();
	
	# Orthography
	foreach $k (sort(keys(%orthography))) {

		# No homograph disambiguation is done, chose the first pronunciation alternative.
		$transcription{ $k }	=~	s/^([^\|]+)\|.+$/$1/;


		# Regain section signs
		$orthography{ $k }	=~	s/\&paraett\;/\§/g;
		$orthography{ $k }	=~	s/\&paratva\;/\§\§/g;


		# Acronyms with pronunciation
		if ( $type{ $k }	=~	/ACR/ ) {
			my $transcr	=	$transcription{ $k };
			$transcr	=~	s/\"/\&quot\;/g;

			push @text,"<ssml:say-as ssml:type\=\"acronym\"><ssml:phoneme alphabet\=\"$phoneme_alphabet\" ssml:ph\=\"$transcr\">$orthography{ $k }<\/ssml:phoneme><\/ssml:say-as>";
			
		# Initials with pronunciation
		} elsif ( $type{ $k }	=~	/INITIAL/ ) {
			my $transcr	=	$transcription{ $k };
			$transcr	=~	s/\"/\&quot\;/g;
			
			push @text,"<ssml:say-as ssml:type\=\"spell-out\"><ssml:phoneme alphabet\=\"$phoneme_alphabet\" ssml:ph\=\"$transcr\">$orthography{ $k }<\/ssml:phoneme><\/ssml:say-as>";


		# Abbreviations etc. <sub alias>
		} elsif ( 	$exp{ $k } 	!~	/^(?:<NONE>|0)$/
				&&
				$pos{ $k } =~ /^(?:UNK|0)$/
				&&
				$transcription{ $k } eq $default_transcription
			) {
			
				push @text,"<ssml:sub alias\=\"$exp{ $k }\">$orthography{ $k }<\/ssml:sub>";


		# Proper names with pronunciation
		# Proper names without pronunciation
		} elsif ( $type{ $k }	=~	/PM/ ) {
			
			# Pronunciation exists
			if ( $transcription{ $k }	ne	$default_transcription ) {
				my $transcr	=	$transcription{ $k };
				$transcr	=~	s/\"/\&quot\;/g;

				push @text,"<ssml:say-as ssml:type\=\"proper name\"><ssml:phoneme alphabet\=\"$phoneme_alphabet\" ssml:ph\=\"$transcr\">$orthography{ $k }<\/ssml:phoneme><\/ssml:say-as>";
			
			# Pronunciation does not exist
			} else {
				push @text,"<ssml:say-as ssml:type\=\"proper name\">$orthography{ $k }<\/ssml:say-as>";
			}				
		
		
		# English with pronunciation (not for English as main language)
		# English without pronunciation
		} elsif (
			$lang{ $k }	!~	/$main_lang/
			&&
			$lang{ $k }	=~	/eng/
			&&
			$orthography{ $k }	=~	/[a-z]/i
			) {
			
			# Pronunciation exists
			if ( $transcription{ $k }	ne	$default_transcription ) {
				my $transcr	=	$transcription{ $k };
				$transcr	=~	s/\"/\&quot\;/g;

				push @text,"<ssml:say-as ssml:type\=\"english\"><ssml:phoneme alphabet\=\"$phoneme_alphabet\" ssml:ph\=\"$transcr\">$orthography{ $k }<\/ssml:phoneme><\/ssml:say-as>";
			
			# Pronunciation does not exist
			} else {
				push @text,"<ssml:say-as ssml:type\=\"english\">$orthography{ $k }<\/ssml:say-as>";
			}
			
		# Pronunciation exists
		} elsif ( $transcription{ $k }	ne	$default_transcription ) {
			my $transcr	=	$transcription{ $k };
			$transcr	=~	s/\"/\&quot\;/g;

			push @text,"<ssml:phoneme alphabet\=\"$phoneme_alphabet\" ssml:ph\=\"$transcr\">$orthography{ $k }<\/ssml:phoneme>";
	
		} else {
			push @text,$orthography{ $k };
		}			

	}

#	my $text = join"<SPLITTER>",@text;
	my $text = join"",@text;
	
	
	# Rewrite "&", ">" and "<"
	$text =~ s/> *\& *</>\&amp\;</g;
	$text =~ s/> *\< *</>\&lt\;</g;
	$text =~ s/> *\> *</>\&gt\;</g;
	
	
	return $text;

}
#***********************************************************#
1;
