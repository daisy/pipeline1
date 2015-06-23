#!/usr/bin/perl -w

#***************************************************************#
#	process_string.pl
#	
#	CE 070307
#***************************************************************#
sub process_string {

	my ( $string,$lang,$mode ) = @_;


	$main_lang = $lang;

	$string =~ s/\&\#x002D\;/-/g;	# mdash
	$string =~ s/\&\#x00A9\;/©/g;	# copyright
	$string =~ s/\&\#x0026\;/\&/g;	# ampersand
	$string =~ s/\&\#x00AE\;/\®/g;	# registrated trademark
	$string =~ s/–/-/g;
	
	my $sentences = &sentence_split( $string );
	my @sentences = split/<SENT_SPLIT>/,$sentences;


	my $ret_string;

	foreach my $sent ( @sentences ) {

#		print "\n---------------\nSENTENCE: $sent\n";

		my $push_string;

		# Multiword expression lookup	Example: "art. nr.", "o. s. v.".
#		$string = &multiword_expression_lookup($sent);

	
		# Count number of components in string.
		($number_of_components) = &count_components_blanks($sent);
	
		
		# Create sublists with default values.
		&create_sublists($number_of_components);
		
		
		# Field lookups
		&field_lookups();
	
		# Merge numbers as "10.000".
		&run_merge_num_subs();

		($number_of_components) = &count_components($string);

	
		# Create sublists with default values.
		&create_sublists($number_of_components);

	
		# Field lookups
		&field_lookups();
	
#		&print_all_output();
	


		# Rules for mark-up and expansions.
		&run_rules($sent);
	
	
		# Language detection (twice to catch modified contexts).
#		&language_detection("eng");
		
		# Homograph disambiguation
#		&homograph_disambiguation();
	

		# Convert TPA to CMU transcription alphabet.
		if ( $mode eq "align" ) {
			foreach my $k ( sort(keys(%transcription))) {
				
				if ( $transcription{ $k } !~ /^(?:$default_transcription|<UNKNOWN>)$/ ) {
					$transcription{ $k } = &tpa2cmu($transcription{ $k });
	
				}
			}

			$push_string = &ssml_output;


			$ret_string .= "$push_string ";
			
			&print_all_output();
		} else {
			&print_all_output();
		}
		
		
	} # end foreach sent

	if ( $mode eq "align" ) {
		return $ret_string;
	}


}
#***************************************************************#
#	Field lookups						#
#	A lookup for every sublist field to get all possible	#
#	values for the token.					#
#***************************************************************#
sub field_lookups {
	
	my $counter = 0;
	my $pcounter = 0;
	
	
	
	foreach my $o (@orthography) {

	
		$pcounter = &format_pcounter($counter);

		# Reconstruct orthography list
		$orthography{$pcounter} = $o;
	
		# Abbreviation lookup: is it a possible abbreviation?
#		&abbreviation_lookup($o,$pcounter);
		
		# Acronym lookup: is it a possible acronym?
		#&acronym_lookup($o,$pcounter);
		
		# Token lookup: is it any kind of delimiter or special character?
		#&token_lookup($o,$pcounter);
		
		# Numeral lookup: is it a numeral?
		#&numeral_lookup($o,$pcounter);
		
		# Pos lookup: does part-of-speech-tags exist (including morphological tags)?
		&ort_lookup($o,$pcounter);
				
		
		$counter++;
	}
	
}	
#***************************************************************#




1;
