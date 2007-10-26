#!/usr/bin/perl -w


#**************************************************************#
sub abbreviation_expansion {
	
	if (
		$type{ $curr }	=~	/ABBR/
		&&
		$exp{ $curr }	eq	"$default_expansion"
	) {
		
		my $abbr	=	quotemeta( $ort{ $curr } );


		# Get abbreviation information.
		my ($expansions,$context,$case)	=	&get_abbreviation_info($abbr);		

#		print "AAA $abbr\n$context\n\n";
		
		if (
			# No contextual rules
			$context	eq	"1"
		) {

			# Pick the first expansion in expansion list.
			$expansions	=~	s/;.*$//;
			$exp{ $curr }	=	$expansions;
			
		} elsif (
			# Special contextual rules.
			$context	eq	"SPECIAL"
		) {
			&abbreviation_special_context();
			
		} else {

			# Contextual rules from abbreviation list file.
			&abbreviation_context($expansions,$context);
			
		} # end if contextual rules
	} # end $type eq "ABBREVIATION"


} # end sub
#**************************************************************#
# get_abbreviation_info
#**************************************************************#
sub get_abbreviation_info {

	my $abbreviation	=	shift;
	my $lc_abbreviation	= 	lc($abbreviation);


	if ( exists ( $abbreviation_list{ $abbreviation } )) {
		
		my $info	=	$abbreviation_list{ $abbreviation };

		# Split information		
		$info		=~	/^ *exp\=(.+)\tcon\=(.+)\tcas\=(.+) *$/;
		my $expansions = $1;
		my $context = $2;
		my $case = $3;
		
		# Remove quotemeta from $expansions.
		$expansions	=~	s/\\//g;
		
		return ($expansions,$context,$case);
	
	} elsif ( exists ( $abbreviation_list{ $lc_abbreviation } )) {
		
		my $info	=	$abbreviation_list{ $lc_abbreviation };

		# Split information		
		$info		=~	/^ *exp\=(.+)\tcon\=(.+)\tcas\=(.+) *$/;
		my $expansions = $1;
		my $context = $2;
		my $case = $3;
		
		# Remove quotemeta from $expansions.
		$expansions	=~	s/\\//g;
		
		return ($expansions,$context,$case);
		
	} else {
		return "<NONE>";
	}
		
}
#**************************************************************#
# run_special_context
#**************************************************************#
sub abbreviation_special_context {


	
#	if (
#		$orthography{ $curr }	=~	/^st\.?$/i
#	) {	
#		# Preceeded by ordinal.
#		if (
#			exists ( $ort{ $prev_2 } )
#			&&
#			$pos{ $prev_2 }		eq	"NUM ORD"
#			&&
#			$pos{ $prev_1 }		eq	"DEL"
#		) {
#			$exp{ $curr }		=	"stycket";
#		
#		} elsif (
#			exists ( $ort{ $prev_1 } )
#			&&
#			$pos{ $prev_1 }		eq	"NUM ORD"
#		) {
#			$exp{ $curr }		=	"stycket";
#		
#		# Preceeded by other numeral.
#		} elsif (
#			exists ( $ort{ $prev_2 } )
#			&&
#			$pos{ $prev_2 }		=~	/^NUM/
#			&&
#			$pos{ $prev_1 }		eq	"DEL"
#		) {
#			$exp{ $curr }		=	"stycken";
#		
#		} elsif (
#			exists ( $ort{ $prev_1 } )
#			&&
#			$pos{ $prev_1 }		=~	/^NUM/
#		) {
#			$exp{ $curr }		=	"stycken";
#		
#		# else treat as acronym.
#		} else {
#			$pos{ $curr }		=	"ACR";
#			$type{ $curr }		=	"ACRONYM";
#		}
#
#	} elsif (
#		$orthography{ $curr }	=~	/^dir\.?$/i
#	) {	
#		
#		# dir.		direktiv;direktör;dir.				HC=NUM;HC=PM;HC=.	1	0
#		# Preceeded by ordinal.
#		if (
#			exists ( $ort{ $next_2 } )
#		) {
#
#			if (
#				$pos{ $next_2 }	=~	/NUM/
#			) {
#				$exp{ $curr }		=	"direktiv";
#			
#			} elsif (
#				$pos{ $next_2 }	=~	/PM/
#			) {
#				$exp{ $curr }		=	"direktör";
#			}
#		} # end exists
#		
#	
#	#---------------------------- sidan ----------------------------#
#	} elsif (
#		$orthography{ $curr }	=~	/^(?:s|sid)\.?$/i
#		&&
#		(
#			$pos{ $next_1 }		=~	/NUM/
#			||
#			(
#				$pos{ $next_1 }	eq	"DEL"
#				&&
#				$pos{ $next_2 }	=~	/NUM/
#			)
#		)
#	) {	
#		$exp{ $curr }		=	"sidan";
#	} # end $ort


}
#**************************************************************#
# abbreviation_context
#**************************************************************#
sub abbreviation_context {

	my ($expansions,$context)	=	@_;
	$expansions		=~	s/\t+$//;

#	print "EXP. $expansions\t\t$context\n\n";

	# Lists of expansions and rules.	
	my @expansions 		=	split/\ *\;\ */,$expansions;

	my $apply_rule		=	0;
	
	# Test rule conditions
	if ($context =~ /abbreviation_rule_1/) {
		$apply_rule	=	&abbreviation_rule_1($context);
	
	} elsif ($context =~ /abbreviation_rule_2/) {
		$apply_rule	=	&abbreviation_rule_2($context);

	} elsif ($context =~ /abbreviation_rule_3/) {
		$apply_rule	=	&abbreviation_rule_3($context);

	} elsif ($context =~ /abbreviation_rule_4/) {
		$apply_rule	=	&abbreviation_rule_4($context);

	} elsif ($context =~ /abbreviation_rule_5/) {
		$apply_rule	=	&abbreviation_rule_5($context);

	} elsif ($context =~ /abbreviation_rule_6/) {
		$apply_rule	=	&abbreviation_rule_6($context);

	} elsif ($context =~ /abbreviation_rule_7/) {
		$apply_rule	=	&abbreviation_rule_7($context);
	}
		
	# No rule was applied, don't expand.
	if ($apply_rule		==	100) {
		$exp{ $curr }	=	"<NONE>";
		
	} else {
		
#		print "ER @expansions\n\n";
		
		# Pick the number of $apply_rule from expansion list.
		$exp{ $curr }	=	$expansions[$apply_rule];
		
#		print "EEEEE $exp{$curr}\n@expansions";
	}
		
	
}
#**************************************************************#
# abbreviation_rule_1
#
# Left context decides singular or plural form of the abbreviation.
# Example:	1 tablespoon		3 tablespoons
#
#**************************************************************#
sub abbreviation_rule_1 {
	
	my $rule = shift;
	
	my $apply_rule	=	100;
	
#	print "\n---------------\n222. $orthography{$prev_2}\t111. $pos{$prev_1}\n\n";
	
	if ( exists ( $orthography{ $prev_1 } )) {
	
		if ( 
			$orthography{ $prev_1 }		=~	/^(?:[02-9]|\d\d)$/
			||
			(exists ( $orthography{ $prev_2 } )
			&&
			$orthography{ $prev_2 }		=~	/^(?:[02-9]|\d\d)$/
			&&
			$pos{ $prev_1 }			eq	"DEL")
				
		) {
			$apply_rule			=	1;
			
		} elsif (
			$orthography{ $prev_1 }		=~	/^(?:1|[\.\,]5)$/
			||
			(exists ( $orthography{ $prev_2 } )
			&&
			$orthography{ $prev_2 }		=~	/^(?:1|[\.\,]5)$/
			&&
			$pos{ $prev_1 }			eq	"DEL")
		) {
			
			$apply_rule			=	0;
		}	
	
	} 
	
	# No rule was applied
	if ( $apply_rule == 100 ) {
		# 1a - chose first expansion in list, otherwise do not expand.
		if ( $rule	=~	/abbreviation_rule_1a/ ) {
			$apply_rule = 0;
		}
	}
	
#	print "APPLY: $apply_rule\n\n";
	
	return $apply_rule;
}
#**************************************************************#
# abbreviation_rule_2
#
# Expansion only if left context is numeral.
# Example:		10 mm.
#
#**************************************************************#
sub abbreviation_rule_2 {
	
	my $rule = shift;
	
	my $apply_rule	=	100;


	if ( exists ( $orthography{ $prev_1 } )) {
	
		if ( 
			$orthography{ $prev_1 }		=~	/\d$/
			||
			(exists ( $orthography{ $prev_2 } )
			&&
			$orthography{ $prev_2 }		=~	/\d$/
			&&
			$pos{ $prev_1 }			eq	"DEL")
				
		) {
			$apply_rule			=	1;
		}
	
	} 
	
	return $apply_rule;
}	
#**************************************************************#
# abbreviation_rule_3
#
# Gender and numerus decide expansion form.
#
#**************************************************************#
sub abbreviation_rule_3 {
	
	my $apply_rule	=	100;

	if (
		exists ( $orthography{ $next_2 } )
		&&
		$pos{ $next_1 }		eq	"DEL"
	) {
		
		# så kallad
		if (
			$morphology{ $next_2 }		=~	/(?:^|\|)UTR SIN IND/
		) {
			$apply_rule	=	0;
			
		# så kallat
		} elsif (
			$morphology{ $next_2 }		=~	/(?:^|\|)NEU SIN IND/
		) {
			$apply_rule	=	1;
			
		# så kallade
		} elsif (
			$morphology{ $next_2 }		=~	/(?:PLU|DEF)/
		) {
			$apply_rule	=	2;
		}		
		
	} # end exists
	
	if ( $apply_rule == 100 ) {
		if (
			exists ( $orthography{ $next_1 } )
		) {
			# så kallad
			if (
				$morphology{ $next_1 }		=~	/(?:^|\|)UTR SIN IND/
			) {
				$apply_rule	=	0;
				
			# så kallat
			} elsif (
				$morphology{ $next_1 }		=~	/(?:^|\|)NEU SIN IND/
			) {
				$apply_rule	=	1;
				
			# så kallade
			} elsif (
				$morphology{ $next_1 }		=~	/(?:PLU|DEF)/
			) {
				$apply_rule	=	2;
			}
		} # end exists
	} # end if == 100		
				
	if ( $apply_rule == 100 ) {
		if (
			# så kallade
			exists ( $orthography{ $prev_2 } )
			&&
			$pos{ $prev_1 }				eq	"DEL"
			&&
			$morphology{ $prev_2 }			=~	/(?:PLU|DEF)/
		) {
			$apply_rule	=	2;

		} # end exists
	} # end if == 100
		
	if ( $apply_rule == 100 ) {
		if (
			# så kallade
			exists ( $orthography{ $prev_1 } )
			&&
			$morphology{ $prev_1 }		=~	/(?:PLU|DEF)/
		) {
			$apply_rule	=	2;
		} # end exists
	} # end if == 100		
		
	return $apply_rule;	
}
#**************************************************************#
# abbreviation_rule_4
#
# Cardinal or ordinal decide expansion form.
#
#**************************************************************#
sub abbreviation_rule_4 {
	
	my $apply_rule	=	100;
	
	
#	print "\n
#	-------------
#	abbreviation_rule_4
#	Prev2:	$type{ $prev_2 }
#	Prev1:	$pos{ $prev_1 }	
#	Curr:	$ort{ $curr }
#	\n";
	
#	print "\n
#	-------------
#	abbreviation_rule_4
#	$string
#	Curr:	$ort{ $curr }
#	Next1:	$pos{ $next_1 }	
#	Next2:	$pos{ $next_2 }
#	\n";

	
	if (
		exists ( $ort{ $prev_2 } )
		&&
		$pos{ $prev_1 }			eq	"DEL"
		&&
		$type{ $prev_2 }		=~	/NUM ORD/	
	) {
		$apply_rule	=	1;
	}

	if ( $apply_rule == 100 ) {
		if (
			# 4 kap.
			exists ( $ort{ $prev_1 } )
			&&
			$type{ $prev_1 }	=~	/NUM ORD/	
		) {
			$apply_rule	=	1;
		
		} elsif (
			# kap. 4
			exists ( $ort{ $next_2 } )
			&&
			$pos{ $next_1 }			eq	"DEL"
			&&
			$pos{ $next_2 }	=~	/NUM/	
		) {
			$apply_rule	=	0;
		
		} elsif (
			# kap.4
			exists ( $ort{ $next_1 } )
			&&
			$pos{ $next_1 }	=~	/NUM/	
		) {
			$apply_rule	=	0;
		}
			
	}
	

		


	return $apply_rule;
	
}
#**************************************************************#
# abbreviation_rule_5 {
#
# Ordinal preeceding or numeral following abbreviation.
#
#**************************************************************#
sub abbreviation_rule_5 {

	my $apply_rule = 100;

#	print "\n
#	-------------
#	abbreviation_rule_5
#	Prev2:	$pos{ $prev_2 }
#	Prev1:	$pos{ $prev_1 }	
#	Curr:	$ort{ $curr }
#	Next1:	$pos{ $next_1 }	
#	Next2:	$pos{ $next_2 }
#	\n";
		
	if (
		# 4 §
		exists ( $ort{ $prev_2 } )
		&&
		$pos{ $prev_1 }		eq	"DEL"
		&&
		$pos{ $prev_2 }		=~	/NUM/
	) {
		$apply_rule	=	1;
	} elsif (
		# 4§
		exists ( $ort{ $prev_1 } )
		&&
		$pos{ $prev_1 }		=~	/NUM/
	) {
		$apply_rule	=	1;
	}
	
	if ( $apply_rule == 100 ) {
		if (
			# § 4
			exists ( $ort{ $next_2 } )
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$pos{ $next_2 }		=~	/NUM/
		) {
			$apply_rule	=	0;
		} elsif (
			# §4
			exists ( $ort{ $next_1 } )
			&&
			$pos{ $next_1 }		=~	/NUM/
		) {
			$apply_rule	=	0;
		}
	} # end if == 100		
	
	# Regain original orthography
	$orthography{ $curr }	=~	s/\&paraett\;/\§/;
	$orthography{ $curr }	=~	s/\&paratva\;/\§\§/;
		
		
	return $apply_rule;
}
#**************************************************************#
# abbreviation_rule_6
#
# Numeral preceeding or following.
#
#**************************************************************#
sub abbreviation_rule_6 {
	
	my $apply_rule = 100;
	
	if (
		# USD 40
		exists ( $ort{ $prev_2 } )
		&&
		$pos{ $prev_1 }		eq	"DEL"
		&&
		$pos{ $prev_2 }		=~	/NUM/
	) {
		$apply_rule	=	0;
	} elsif (
		# USD40
		exists ( $ort{ $prev_1 } )
		&&
		$pos{ $prev_1 }		=~	/NUM/
	) {
		$apply_rule	=	0;

	} elsif (
		# 40 USD
		exists ( $ort{ $next_2 } )
		&&
		$pos{ $next_1 }		eq	"DEL"
		&&
		$pos{ $next_2 }		=~	/NUM/
	) {
		$apply_rule	=	0;
	} elsif (
		# 40USD
		exists ( $ort{ $next_1 } )
		&&
		$pos{ $next_1 }		=~	/NUM/
	) {
		$apply_rule	=	0;
	}

	return $apply_rule;
}
#**************************************************************#
# abbreviation_rule_7
#
# Proper name preceeding
#
#**************************************************************#
sub abbreviation_rule_7 {
	
	my $apply_rule = 100;

#	print "\n
#	-----------
#	abbreviation_rule_7
#	Prev1:	$pos{ $prev_1 }
#	Prev2:	$pos{ $prev_2 }
#	\n";
	
	if (
		# Preben Chr.
		exists ( $ort{ $prev_2 } )
		&&
		$pos{ $prev_1 }		eq	"DEL"
		&&
		$pos{ $prev_2 }		=~	/PM/
	) {
		$apply_rule	=	0;
	}
	
	return $apply_rule;
}
#**************************************************************#
1;
