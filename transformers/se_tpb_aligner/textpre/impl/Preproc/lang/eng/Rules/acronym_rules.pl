#!/usr/bin/perl -w

use locale;

#**************************************************************#
# Acronyms in acronym_list.txt are already tagged,
# also with genitive suffixes such as "s" or ":s".
#
# TODO
# always acronyms if uppercase:		(LO|ID)
#
#
# 071025 Lexicon is not used for English aligning, use ACRONYM 
# only for unknown words without vowels.
#**************************************************************#
sub acronym_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	if ( $exp{ $curr }	eq	"$default_expansion" ) {
# Not for English aligning		&acronym_1();
# Not for English aligning		&acronym_2();
# Not for English aligning		&acronym_3();
# Not for English aligning		&acronym_4();
# Not for English aligning		&acronym_5();
# Not for English aligning		&acronym_6();
		&acronym_7();
	}

}
#**************************************************************#
# acronym_1
#
# Compounds with acronym in lexicon as first part.
# Example:	TPB-personal
#
#**************************************************************#
sub acronym_1 {

	if ( 
		$transcription{ $curr } eq "$default_transcription"
		&&
		$ort{ $curr } =~ /^($acronym_list)-(.+)$/
	) {
		
		my $acronym = $1;
		my $word = $2;
		
		# Get acronym transcription
		my ($trans,$x,$y) = split/\t/,$acronym_list{$acronym};
	
		$transcription { $curr }	=	"$trans<ACR>";
		
		# Lookup second part
		&ort_lookup($word,$curr,"ACR" );
	
		# Choose the first pos, morph and lang if multiple analyses.
		$pos{ $curr }		=~	s/\|.*$//;
		$morphology{ $curr }	=~	s/\|.*$//;
		$lang{ $curr }		=~	s/\|.*$//;
	
		# No transcription was found for second part.
		if (
			$transcription{ $curr }		!~	/\|/
		) {
#			$trans			=	&first_part_stress($trans);
			$transcription{ $curr }	=	$trans . " - <UNKNOWN>";

		
		} else {
			# Choose the first transcription of second part.
			my $second_trans	=	$transcription{ $curr };
			$second_trans		=~	s/^(.+)<ACR>\|([^\|]+).*$/$2/;
						
			# Stress assignment
#			$trans		=	&first_part_stress($trans);
#			$second_trans	=	&second_part_stress($second_trans);

			$transcription{ $curr }	=	$trans . " - " . $second_trans;

#			$pos{ $curr }		=	"ACR";
			$type{ $curr }		=	"ACRONYM COMPOUND";

#			print OUTPUT "\n\n111111111111111111111111111\t$ort{ $curr}\n\n";
		}
	}
}
#**************************************************************#
# acronym_2
#
# Compounds with possible acronym as first part.
# Example:	WRT-personal
#		S-E-banken
#		N/V-kvoten
#
#**************************************************************#
sub acronym_2 {

#	print "$ort{ $curr }\n\n";

	if ( 
		$transcription{ $curr } eq "$default_transcription"
		&&
		( $ort{ $curr } =~ /^([A-Z](?:\.[A-Z]\.[A-Z]|[a-z]*[A-Z])+)-([a-zA-Z]+)$/
		||
		$ort{ $curr } =~ /^([A-Z](?:[\.\-\/][A-Z])+)-([a-zA-Z][a-zA-Z][a-zA-Z]+)$/ )
	) {
		
		my $poss_acr = $1;
		my $word = $2;

		if ( $poss_acr !~ /(?:ID|LO)$/ ) {
			# Check if the possible acronym exist in lexicon.
			&ort_lookup( $poss_acr,$curr,"ACR" );
		}
		
		if (
			# First part does not exist in lexicon.
			$transcription{ $curr }	eq	"$default_transcription"
			||
			$poss_acr		=~	/^(?:ID|LO)$/
		) {
			
			my $trans = &acronym_expansion($poss_acr);

			$transcription { $curr }	=	"$trans<ACR>";

			# Lookup second part
			&ort_lookup($word,$curr,"ACR" );
			
			# No transcription was found for second part.
			if (
				$transcription{ $curr }		!~	/\|/
			) {
#				$trans			=	&first_part_stress($trans);
				$transcription{ $curr }	=	$trans . " - <UNKNOWN>";
				$type{ $curr }		=	"ACRONYM COMPOUND";
			
			} else {
		
				# Choose the first transcription of second part.
				my $second_trans	=	$transcription{ $curr };
	
				$second_trans		=~	s/^(.+)<ACR>\|([^\|]+).*$/$2/;
							
				# Stress assignment
#				$trans		=	&first_part_stress($trans);
#				$second_trans	=	&second_part_stress($second_trans);
	
				$transcription{ $curr }	=	$trans . " - " . $second_trans;

	#			$pos{ $curr }		=	"ACR";
	#			$type{ $curr }		=	"ACRONYM";
				$type{ $curr }		=	"ACRONYM COMPOUND";

#			print OUTPUT "\n\n2222222222222222222222222$ort{ $curr}\n\n";
			}
			
		# Possible acronym exists in lexicon, clean transcription field.
		} else {
			$transcription{ $curr } = $default_transcription;
		}
	}
}
#**************************************************************#
# acronym_3
#
# Normal acronyms
# Mixed upper and lowercase
# 
#**************************************************************#
sub acronym_3 {
	
	if ( $pos{ $curr }	eq	"$default_pos" ) {

#		print "PPP $ort{ $curr } __\n\n";

		if ( 
			# XxX		SvD, SvD:s
			# XXX		KPR, KPR:arna
			$ort{ $curr }	=~	/^[A-Z]+[a-z]*[A-Z]+\:?(?:$acronym_endings)?$/
			||
			# XxXx		PaGt
			# XXx		KPr
			$ort{ $curr }	=~	/^[A-Z]+[a-z]*[A-Z]+[a-z]\:?(?:$acronym_endings)?$/
			||
			# X-X-X		A-P-G
			# X.X.X		S.S.D
			$ort{ $curr }	=~	/^[A-Z](?:[\.\-][A-Z])+\:?(?:$acronym_endings)?$/

		) {
			my $trans;
			if ( exists ( $acronym_list{ $ort{ $curr } } )) {
				$trans = $acronym_list{ $ort{ $curr } };
			} else {
				$trans = &acronym_expansion($ort{ $curr });
			}
			
			
			$type{ $curr }		=	"ACRONYM";
			$pos{ $curr }		=	"ACR";
			$transcription{ $curr }	=	$trans;
#			print OUTPUT "\n\n333333333333333333$ort{ $curr}\n\n";
		}
		
	}
}
#**************************************************************#
# acronym_4
#
# 
# Example:	C- och D-uppsats
# 
#**************************************************************#
sub acronym_4 {

	if ( 
		$ort{ $curr }		=~	/^([A-Z])-$/
	) {
		my $transcription	=	$1;
		$transcription		=	$alphabet{ $transcription };
		$transcription{ $curr }	=	$transcription;

		$pos{ $curr }		=	"ACR";
		$type{ $curr }		=	"ACRONYM";
	}
	
}
#**************************************************************#
# acronym_5
#
# Compounds with acronym in lexicon as second part.
# Example:	företags-VD
#
#**************************************************************#
sub acronym_5 {


	if ( 
		$transcription{ $curr } eq "$default_transcription"
		&&
		$ort{ $curr } =~ /^(.+)-((?:$acronym_list)\:?(?:$acronym_endings)?)$/
	) {
		
		my $word = $1;
		my $acronym = $2;


		# Get acronym transcription
		#my ($trans,$x,$y) = split/\t/,$acronym_list{$acronym};
		&acronym_lookup($acronym,$curr,"ACR");
		
#		print "NUNU $ort{ $curr }\t$transcription{ $curr }\n\n";
		
		$trans = $transcription{ $curr };
		$trans =~ s/^0\|(.+)$/$1/;
	
		$transcription { $curr }	=	"$trans<ACR>";
		
		# Lookup second part
		&ort_lookup($word,$curr,"ACR" );
	
		# No transcription was found for second part.
		if (
			$transcription{ $curr }		!~	/\|/
		) {
#			$trans			=	&second_part_acr_stress($trans);
			$transcription{ $curr }	=	"<UNKNOWN> - " . $trans;

		
		} else {
			# Choose the first transcription of second part.
			my $second_trans	=	$transcription{ $curr };
			$second_trans		=~	s/^(.+)<ACR>\|([^\|]+).*$/$2/;
						
			# Stress assignment
			$trans		=	&second_part_acr_stress($trans);
#			$second_trans	=	&first_part_stress($second_trans);

			$transcription{ $curr }	=	"$second_trans - " . $trans;

			$pos{ $curr }		=	"ACR";
			$type{ $curr }		=	"ACRONYM COMPOUND";

			# Genitive
			if ( $ort{ $curr }	=~	/:s$/ ) {
				$morphology{ $curr }	=	"GEN";
			} else {
				$morphology{ $curr }	=	"NOM";
			}

#			print OUTPUT "\n\n555555555555555555\t$ort{ $curr}\n\n";
		}
	}
}
#**************************************************************#
# acronym_6
#
# Compounds with single letter as first part.
# Example:	P-plats
#
#**************************************************************#
sub acronym_6 {

	if ( 
		$transcription{ $curr } eq "$default_transcription"
		&&
		$ort{ $curr } =~ /^([A-Z])-(.+)$/
	) {
		
		my $letter = $1;
		my $word = $2;
		
		# Get acronym transcription
		my ($trans,$x,$y) = split/\t/,$alphabet{$letter};
#		print "$ort{ $curr }\t$trans\n";
	
		$transcription { $curr }	=	"$trans<ACR>";
		
		# Lookup second part
		&ort_lookup($word,$curr,"ACR" );
	
		# No transcription was found for second part.
		if (
			$transcription{ $curr }		!~	/\|/
		) {
#			$trans			=	&first_part_stress($trans);
			$transcription{ $curr }	=	$trans . " - <UNKNOWN>";

		
		} else {
			# Choose the first transcription of second part.
			my $second_trans	=	$transcription{ $curr };
			$second_trans		=~	s/^(.+)<ACR>\|([^\|]+).*$/$2/;
						
			# Stress assignment
#			$trans		=	&first_part_stress($trans);
#			$second_trans	=	&second_part_stress($second_trans);

			$transcription{ $curr }	=	$trans . " - " . $second_trans;

#			$pos{ $curr }		=	"ACR";
			$type{ $curr }		=	"ACRONYM COMPOUND";

#			print OUTPUT "\n\n6666666666666666666666666666666\t$ort{ $curr}\n\n";
		}
	}
}
#**************************************************************#
# acronym_7
#
# Unknown words without vowels
# 
#**************************************************************#
sub acronym_7 {
	
	if ( $pos{ $curr }	eq	"$default_pos" ) {

#		print "PPP $ort{ $curr } __\n\n";

		if ( 
			# XxX		SvD, SvD:s
			# XXX		KPR, KPR:arna
			$ort{ $curr }	=~	/^($consonants_ort)+\:?(?:\'s)?$/i

		) {

			my $trans;
			if ( exists ( $acronym_list{ $ort{ $curr } } )) {
				$trans = $acronym_list{ $ort{ $curr } };
			} else {
				$trans = &acronym_expansion($ort{ $curr });
			}
			
			
			$type{ $curr }		=	"ACRONYM";
			$pos{ $curr }		=	"ACR";
			$transcription{ $curr }	=	$trans;
#			print OUTPUT "\n\n333333333333333333$ort{ $curr}\n\n";
		}
		
	}

}
#**************************************************************#

1;
