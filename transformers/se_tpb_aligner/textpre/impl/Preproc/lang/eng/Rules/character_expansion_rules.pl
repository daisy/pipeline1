#!/usr/bin/perl -w

#**************************************************************#
# character_expansion
#
# Characters
#		
#**************************************************************#
sub character_expansion {


	if ( $exp{ $curr }	=~	/^(?:$default_expansion|<\d+>)$/ ) {	


		if ( $ort{ $curr }	  	=~	/^(\…|\.\.\.)$/	) {	# ellipsis
			&ellipsis_expansion();
	
		} elsif ( $ort{ $curr }		eq	","  		) {	# comma
			&comma_expansion();
			
		} elsif ( $ort{ $curr }		eq	"."  		) {	# period
			&period_expansion();
				
		#**************************************************************#
		#
		#**************************************************************#
		} elsif ( $ort{ $curr }	  	eq	"+"		) {	# plus sign
			&plus_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"-"		) {	# minus sign/dash
			&dash_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"/"		) {	# slash
			&slash_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"?"		) {	# question mark
			&question_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"!"		) {	# exclamation mark
			&exclamation_expansion();
	
		} elsif (
			$ort{ $curr }	  	eq	"§"			# paragraph/section sign
			||
			$ort{ $curr }	  	eq	"&paraett;"		# paragraph/section sign
			) {
			&paragraph_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"@"		) {	# at sign
			&at_sign_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"%"		) {	# percent
			&percent_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"‰"		) {	# permille
			&permille_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"&"		) {	# ampersand
			&ampersand_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"="		) {	# equal sign
			&equal_sign_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	":"		) {	# colon
			&colon_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	";"		) {	# semicolon
			&semicolon_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"="		) {	# equal sign
			&equal_sign_expansion();
	
		} elsif ( $ort{ $curr }	  	=~	/\\/		) {	# backslash
			&backslash_expansion();
	
		#**************************************************************#
		# Brackets and quotes
		#**************************************************************#
		} elsif ( $ort{ $curr }		=~	/^[\(\)]$/  	) {	# parentheses
			&parenthesis_expansion();
	
		} elsif ( $ort{ $curr }		=~	/^[\[\]]$/  	) {	# square bracket
			&square_bracket_expansion();
	
		} elsif ( $ort{ $curr }	  	=~	/^[\{\}]$/	) {	# curly bracket
			&curly_bracket_expansion();
	
		} elsif ( $ort{ $curr }	  	=~	/^\"$/		) {	# double quote
			&doublequote_expansion();
	
		} elsif ( $ort{ $curr }	  	=~	/^\'$/		) {	# single quote
			&singlequote_expansion();
	
		#**************************************************************#
		# Currency symbols
		#**************************************************************#
		} elsif ( $ort{ $curr }	  	=~	/\$/		) {	# dollar sign
			&dollar_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"£"		) {	# pound
			&pound_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"€"		) {	# euro
			&euro_expansion();
			
		} elsif ( $ort{ $curr }	  	eq	"¥"		) {	# yen
			&yen_expansion();
			
		} elsif ( $ort{ $curr }	  	eq	"¢"		) {	# cent
			&cent_expansion();
	
		#**************************************************************#
		#
		#**************************************************************#
		} elsif ( $ort{ $curr }	  	eq	"~"		) {	# tilde
			&tilde_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"*"		) {	# asterisk
			&asterisk_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"<"		) {	# less than sign
			&less_than_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	">"		) {	# greater than sign
			&greater_than_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"|"		) {	# vertical bar
			&vertical_bar_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"_"		) {	# underscore
			&underscore_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"^"		) {	# caret
			&caret_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"º"		) {	# degree
			&degree_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"¿"		) {	# inverted question mark
			&inverted_question_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"©"		) {	# copyright
			&copyright_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"#"		) {	# number sign
			&number_sign_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"•"		) {	# bullet
			&bullet_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"±"		) {	# plus-minus sign
			&plus_minus_expansion();
	
		} elsif ( $ort{ $curr }	  	eq	"†"		) {	# dagger
			&dagger_expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"‡"		) {	# obelisk
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"¸"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"×"		) {	# 
	#		&_expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"¹"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"³"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"³"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"²"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"®"		) {	# 
	#		&expansion();
	
	#	} elsif ( $ort{ $curr }	  	eq	"»"		) {	# 
	#		&_expansion();
	
		}
	}
}
#**************************************************************#
# comma_expansion
#**************************************************************#
sub comma_expansion {
	
	# Decimal commas	"12,5"
	if (
		$type{ $curr }	=~	/(?:NUM DEC|CURRENCY)/
	) {
		$exp{ $curr }	=	"point";

	# Otherwhise nothing
	} else {
		$exp{ $curr }	=	"<NONE>";
	}

}
#**************************************************************#
# period_expansion
#**************************************************************#
sub period_expansion {

	# Decimal periods	"12.5"
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"dot";
		
	} elsif (
		$type{ $curr }	=~	/(NUM DEC)/
	) {
		$exp{ $curr }	=	"point";
	
	# Otherwhise nothing
	} else {
		$exp{ $curr }	=	"<NONE>";
	}
}
#**************************************************************#
# ellipsis_expansion
#**************************************************************#
sub ellipsis_expansion {
}
#**************************************************************#
# plus_expansion
#**************************************************************#
sub plus_expansion {

	# Maths			"5+4"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"plus";
	
	# Otherwhise
	} else {
		$exp{ $curr }	=	"plus sign";
	}
}
#**************************************************************#
# dash_expansion
#**************************************************************#
sub dash_expansion {
	
	# Maths			"5-4"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"minus";
	
	# Interval		"1939-45"
	} elsif (
		$type{ $curr }	eq	"INTERVAL"
	) {
		$exp{ $curr }	=	"to";

	# ID number
	} elsif (
		$type{ $curr }	=~	/ID NUM/
	) {
		$exp{ $curr }	=	"dash";

	# Otherwhise
	} else {
		$exp{ $curr }	=	"<NONE>";
		$pause{ $curr }	=	"$short_pause";
	}
}
#**************************************************************#
# slash_expansion
#**************************************************************#
sub slash_expansion {
	
	# Maths			"20/5=4"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"divided by";
		
	} else {
		$exp{ $curr }	=	"slash";
	}
}
#**************************************************************#
# question_expansion
#**************************************************************#
sub question_expansion {
	
	# Email, URL:s and file names.	http://siej~is?jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"question mark";
		
	} else {
		$exp{ $curr }	=	"<NONE>";
	}
}
#**************************************************************#
# exclamation_expansion
#**************************************************************#
sub exclamation_expansion {
	
	# Email, URL:s and file names.	http://siej~is!jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"exclamation mark";
		
	} else {
		$exp{ $curr }	=	"<NONE>";
	}
}
#**************************************************************#
# paragraph_expansion
#**************************************************************#
sub paragraph_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"section sign";
		
	} else {
		$exp{ $curr }	=	"section";
	}
}
#**************************************************************#
# at_sign_expansion
#**************************************************************#
sub at_sign_expansion {
	
	$exp{ $curr }	=	"at-sign";
}
#**************************************************************#
# percent_expansion
#**************************************************************#
sub percent_expansion {
	
	# Email, URL:s and file names.	http://siej~is%jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"percent sign";
		
	} else {
		$exp{ $curr }	=	"percent";
	}
}
#**************************************************************#
# permille_expansion
#**************************************************************#
sub permille_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"perthousand sign";
		
	} else {
		$exp{ $curr }	=	"per thousand";
	}
}
#**************************************************************#
# ampersand_expansion
#**************************************************************#
sub ampersand_expansion {
	
	# Email, URL:s and file names.	http://siej~is&jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"ampersand";
		
	# As conjunction		Kalle & lisa
		
	} else {
		$exp{ $curr }	=	"and";
	}
}
#**************************************************************#
# equal_sign_expansion
#**************************************************************#
sub equal_sign_expansion {
	
	# Maths			"7+8=15"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"equals";
		
	} else {
		$exp{ $curr }	=	"equal sign";
	}
}
#**************************************************************#
# colon_expansion
#**************************************************************#
sub colon_expansion {
	
	# Between year and other number		2007:54
	
	
	# In url:s and email addresses
	if (
		$type{ $curr }	=~	/(?:EMAIL|URL|FILENAME|EXPAND)/
	) {
		$exp{ $curr }	=	"colon";
	
	} else {
		
		# Otherwise pause.
		$exp{ $curr }		=	"<NONE>";
		$pause{ $curr }		=	"$short_pause";
	}
		
}
#**************************************************************#
# semicolon_expansion
#**************************************************************#
sub semicolon_expansion {
}
#**************************************************************#
# backslash_expansion
#**************************************************************#
sub backslash_expansion {

	$exp{ $curr }		=	"backslash";	
}
#**************************************************************#
# parentheses_expansion
#**************************************************************#
sub parenthesis_expansion {
	
	my $expanded = 0;
	
	if (
		$ort{ $curr }	eq	"("
	
	) {

		# Current location
		my $cmp_curr	=	$curr;
		$cmp_curr	=~	s/^0+(\d+)$/$1/;
		
		# Location counter
		my $loc_counter = 0;


		# Look for closing parenthesis at least three words from current position.
		foreach my $loc (@orthography_list) {

			# Location comparison
			my $cmp_loc	=	$loc;
			$cmp_loc	=~	s/^0+(\d+)$/$1/;
			
			
#			print "OOO $cmp_loc\t\t$cmp_curr\t\t$ort{ $curr }\n"; exit;
			
#			print "PL $pos{ $loc}\t$par_counter\n";
			
			# Look only to the right of current.
			if ( $cmp_loc > $cmp_curr && ($pos{ $loc } ne 'DEL' || $orthography{$loc} eq ")")) {
				$loc_counter++;
				
#				print "--------------------------\nStart loc: $cmp_curr\tThis loc: $cmp_loc\nThis ort: $orthography{ $loc }\tThis pos: $pos{ $loc }\nCOUNT: $par_counter\tLIMIT: $expand_limit\n";
#				print "\n------------\nLOC: $loc\tCURR: $cmp_curr\t$ort{ $curr }\nPC: $par_counter\nOO $orthography{$loc}\nLIMIT: $expand_limit\n\n";
	
				if (
					$loc_counter >= $expand_limit + 1
					&&
					$orthography{ $loc }	eq	")"
				) {
#					print "PC $par_counter\t\tEL: $expand_limit\n\n";
					$exp{ $curr }	=	"parenthesis";
					$pause{ $curr }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$exp{ $loc }	=	"end of parenthesis";
					$pause{ $loc }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$expanded	=	"1";
#					print "EXP: $exp{ $curr}\t$exp{ $loc}\n";
				}
					
	
			}
			
		} # end foreach $loc
	}
	
	# If expansion isn't applied, add pause.
	if ( $expanded == 0 ) {
		$exp{ $curr } 	=	"<NONE>";
		$pause{ $curr }	=	"$parenthesis_pause";
	}
	
}
#**************************************************************#
# square_bracket_expansion
# same as above
#**************************************************************#
sub square_bracket_expansion {
	
	my $expanded = 0;
	
	if (
		$ort{ $curr }	eq	"["
	
	) {

		# Current location
		my $cmp_curr	=	$curr;
		$cmp_curr	=~	s/^0+(\d+)$/$1/;
		
		# Location counter
		my $loc_counter = 0;


		# Look for closing parenthesis at least three words from current position.
		foreach my $loc (@orthography_list) {

			# Location comparison
			my $cmp_loc	=	$loc;
			$cmp_loc	=~	s/^0+(\d+)$/$1/;
			
			
#			print "OOO $cmp_loc\t\t$cmp_curr\t\t$ort{ $curr }\n"; exit;
			
#			print "PL $pos{ $loc}\t$par_counter\n";
			
			# Look only to the right of current.
			if ( $cmp_loc > $cmp_curr && ($pos{ $loc } ne 'DEL' || $orthography{$loc} eq "]")) {
				$loc_counter++;
				
#				print "--------------------------\nStart loc: $cmp_curr\tThis loc: $cmp_loc\nThis ort: $orthography{ $loc }\tThis pos: $pos{ $loc }\nCOUNT: $par_counter\tLIMIT: $expand_limit\n";
#				print "\n------------\nLOC: $loc\tCURR: $cmp_curr\t$ort{ $curr }\nPC: $par_counter\nOO $orthography{$loc}\nLIMIT: $expand_limit\n\n";
	
				if (
					$loc_counter >= $expand_limit + 1
					&&
					$orthography{ $loc }	eq	"]"
				) {
					$exp{ $curr }	=	"square bracket";
					$pause{ $curr }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$exp{ $loc }	=	"end of square bracket";
					$pause{ $loc }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$expanded	=	"1";
				}
					
	
			}
			
		} # end foreach $loc
	}
	
	# If expansion isn't applied, add pause.
	if ( $expanded == 0 ) {
		$exp{ $curr } 	=	"<NONE>";
		$pause{ $curr }	=	"$parenthesis_pause";
	}

}
#**************************************************************#
# curly_bracket_expansion
#**************************************************************#
sub curly_bracket_expansion {

}
#**************************************************************#
# doublequote_expansion
#**************************************************************#
sub doublequote_expansion {
	
	my $expanded = 0;
	
	if (
		$ort{ $curr }	=~	/^[\"\»\«]$/
	
	) {

		# Current location
		my $cmp_curr	=	$curr;
		$cmp_curr	=~	s/^0+(\d+)$/$1/;
		
		# Location counter
		my $loc_counter = 0;


		# Look for closing parenthesis at least three words from current position.
		foreach my $loc (@orthography_list) {

			# Location comparison
			my $cmp_loc	=	$loc;
			$cmp_loc	=~	s/^0+(\d+)$/$1/;
			
			
#			print "OOO $cmp_loc\t\t$cmp_curr\t\t$ort{ $curr }\n"; exit;
			
#			print "PL $pos{ $loc}\t$par_counter\n";
			
			# Look only to the right of current.
			if ( $cmp_loc > $cmp_curr && ($pos{ $loc } ne 'DEL' || $orthography{$loc} =~ /^[\"\»\«]$/)) {
				$loc_counter++;
				
#				print "--------------------------\nStart loc: $cmp_curr\tThis loc: $cmp_loc\nThis ort: $orthography{ $loc }\tThis pos: $pos{ $loc }\nCOUNT: $par_counter\tLIMIT: $expand_limit\n";
#				print "\n------------\nLOC: $loc\tCURR: $cmp_curr\t$ort{ $curr }\nPC: $par_counter\nOO $orthography{$loc}\nLIMIT: $expand_limit\n\n";
	
				if (
					$loc_counter >= $expand_limit + 1

					&&
					$orthography{ $loc }	=~	/^[\"\»\«]$/
				) {
					$exp{ $curr }	=	"quote";
					$pause{ $curr }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$exp{ $loc }	=	"unquote";
					$pause{ $loc }	=	"$parenthesis_pause_before|$parenthesis_pause_after";
					$expanded	=	"1";
				}
					
	
			}
			
		} # end foreach $loc
	}
	
	# If expansion isn't applied, add pause.
	if ( $expanded == 0 ) {
		$exp{ $curr } 	=	"<NONE>";
		$pause{ $curr }	=	"$parenthesis_pause";
	}

}
#**************************************************************#
# singlequote_expansion
#**************************************************************#
sub singlequote_expansion {
}
#**************************************************************#
# dollar_expansion
#**************************************************************#
sub dollar_expansion {
	
	
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"dollar sign";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		if ( $exp{ $curr } =~	/<\d+>/ ) {
			$exp{ $curr }	.=	"dollar";
		} else {
			$exp{ $curr }	=	"dollar";
		}
		$pos{ $curr }	=	"NN UTR - IND NOM";
		
	}
}
#**************************************************************#
# pound_expansion
#**************************************************************#
sub pound_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"pound sign";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		if ( $exp{ $curr } =~	/<\d+>/ ) {
			$exp{ $curr }	.=	"pounds";
		} else {
			$exp{ $curr }	=	"pounds";
		}
		$pos{ $curr }	=	"NN NEU - IND NOM";
	}
}
#**************************************************************#
# euro_expansion
#**************************************************************#
sub euro_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"euro sign";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		if ( $exp{ $curr } =~	/<\d+>/ ) {
			$exp{ $curr }	.=	"euro";
		} else {
			$exp{ $curr }	=	"euro";
		}
		$pos{ $curr }	=	"NN UTR - IND NOM";
	}
}
#**************************************************************#
# yen_expansion
#**************************************************************#
sub yen_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"yen sign";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		if ( $exp{ $curr } =~	/<\d+>/ ) {
			$exp{ $curr }	.=	"yen";
		} else {
			$exp{ $curr }	=	"yen";
		}
		$pos{ $curr }	=	"NN UTR - IND NOM";
	}
}
#**************************************************************#
# cent_expansion
#**************************************************************#
sub cent_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"cent sign";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		if ( $exp{ $curr } =~	/<\d+>/ ) {
			$exp{ $curr }	.=	"cent";
		} else {
			$exp{ $curr }	=	"cent";
		}
		$pos{ $curr }	=	"NN NEU - IND NOM";
	}
}
#**************************************************************#
# tilde_expansion
#**************************************************************#
sub tilde_expansion {
	
	# Email, URL:s and file names.	http://siej~is§jksjf.com
	if (
		$type{ $curr }	=~	/(EMAIL|URL|FILENAME)/
	) {
		$exp{ $curr }	=	"tilde";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
		
	} else {
		$exp{ $curr }	=	"<NONE>";
		$pos{ $curr }	=	"NN NEU SIN IND NOM";
	}
}
#**************************************************************#
# asterisk_expansion
#**************************************************************#
sub asterisk_expansion {
	
	$exp{ $curr }	=	"asterisk";
}
#**************************************************************#
# less_than_expansion
#**************************************************************#
sub less_than_expansion {

	# Maths			"3 < 4"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"is less than";
		
	} else {
		$exp{ $curr }	=	"less-than-sign";
		$transcription{ $curr }	=	"l 'e s - dh ä3 n | s 'a j n";
	}
}
#**************************************************************#
# greater_than_expansion
#**************************************************************#
sub greater_than_expansion {
	
	# Maths			"4 > 3"
	if (
		$type{ $curr }	eq	"MATHS"
	) {
		$exp{ $curr }	=	"is greater than";
		
	} else {
		$exp{ $curr }	=	"greater-than-sign";
		$transcription{ $curr }	=	"g r3 'e j t ë r - dh ä3 n | s 'a j n";
	}
}
#**************************************************************#
# vertical_bar_expansion
#**************************************************************#
sub vertical_bar_expansion {
	
	$exp{ $curr }	=	"vertical bar";

}
#**************************************************************#
# underscore_expansion
#**************************************************************#
sub underscore_expansion {
	
	$exp{ $curr }	=	"underscore";
	$pos{ $curr }	=	"NN NEU SIN IND NOM";

}
#**************************************************************#
# caret_expansion
#**************************************************************#
sub caret_expansion {
}
#**************************************************************#
# degree_expansion
#**************************************************************#
sub degree_expansion {
	
#	$type{ $curr }	==	"grader";
#	$pos{ $curr }	=	"NN UTR PLU IND NOM";

}
#**************************************************************#
# inverted_question_expansion
#**************************************************************#
sub inverted_question_expansion {
}
#**************************************************************#
# copyright_expansion
#**************************************************************#
sub copyright_expansion {
	
	$exp{ $curr }	=	"copyright";
	
}
#**************************************************************#
# number_sign_expansion
#**************************************************************#
sub number_sign_expansion {
}
#**************************************************************#
# bullet_expansion
#**************************************************************#
sub bullet_expansion {
}
#**************************************************************#
# plus_minus_expansion
#**************************************************************#
sub plus_minus_expansion {
	
	$type{ $curr }	=	"plus minus"

}
#**************************************************************#
# dagger_expansion
#**************************************************************#
sub dagger_expansion {
	
	$type{ $curr }	=	"dagger"

}
#**************************************************************#
1;
