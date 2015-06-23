#!/usr/bin/perl -w


#**************************************************************#
sub mixed_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	if ( $type{ $curr }	eq	"$default_type" ) {

#		&mixed_num_1();
		&mixed_num_2();
		&mixed_num_3();
		&mixed_num_4();
		&mixed_num_5();
		&mixed_num_6();
		&mixed_num_7();
		&mixed_num_8();
	}


} # end sub

#**************************************************************#
sub mixed_num_concat_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;
	&mixed_num_concat();
}
#**************************************************************#
# mixed_num_1
#
# Example:	58ff	62f.
#
#**************************************************************#
sub mixed_num_1 {

	if ( exists ( $ort{ $prev_1 } ) ) {

		if (	
			# Context
			$ort{ $prev_1 }	=~	/^\d+$/
			&&
			$ort{ $curr }	=~	/^(ff|f)$/i
	
		) {
					
			# Retag
			$pos{ $prev_1 }		=	"NUM CARD";
			$type{ $prev_1 }	=	"PAGENUM";
			$pos{ $curr }		=	"ABBR";
			$type{ $curr }		=	"PAGENUM";

			if ($ort{ $curr }	eq	"ff") {
				$exp{ $curr }		=	"och följande sidor";
			} else {
				$exp{ $curr }		=	"och följande sida";
			}
				
		}
	} # end exists

} # end sub
#**************************************************************#
# mixed_num_2
#
# Example:	2000a		6b
#
#**************************************************************#
sub mixed_num_2 {

	if ( exists ( $ort{ $prev_1 } ) ) {

		if (	
			# Context
			$ort{ $prev_1 }	=~	/^\d+$/
			&&
			$ort{ $curr }	=~	/^([a-zåäö])$/i
	
		) {
			
			my $letter	=	$1;
			
			# Retag
			$type{ $prev_1 }	=	"MIXED";
			$morphology{ $curr }	=	"NEU SIN IND NOM";
			$type{ $curr }		=	"MIXED";
			$transcription{ $curr }	=	$alphabet{$letter};
		}
	} # end exists
} # end sub
#**************************************************************#
# mixed_num_3
#
# Example:		Läroplanen-94
#
#**************************************************************#
sub mixed_num_3 {


	if ( exists ( $ort{ $prev_2 } ) ) {

#		print "\n\n-----------------
#		mixed_num_3
#		P2: $ort{$prev_2}
#		P1: $ort{$prev_1}
#		CURR: $ort{$curr}
#		";

		if (	
			# Context
			$ort{ $prev_2 }	=~	/^[a-zåäö]+$/i
			&&
			$ort{ $prev_1 }	eq	"-"
			&&
			$ort{ $curr }	=~	/^\d+$/i
	
		) {
			
			# Retag
			$exp{ $prev_1 }		=	"<NONE>";	# Silent "-".
			$pos{ $curr}		=	"NUM CARD";
		
		}
	} # end exists
} # end sub
#**************************************************************#
# mixed_num_4
#
# Example:		A4-papper
#
#**************************************************************#
sub mixed_num_4 {

	if ( exists ( $ort{ $next_3 } ) ) {

#		print "\n\n-----------------
#		mixed_num_4
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		N3: $ort{$next_3}
#		";

		if (	
			# Context
			$ort{ $curr }	=~	/^[a-zåäö]+$/i
			&&
			$ort{ $next_1 }	=~	/^\d+$/i
			&&
			$ort{ $next_2 }	eq	"-"
			&&
			$ort{ $next_3 }	=~	/^[a-zåäö]+$/i
	
		) {
		
			# Retag
			$type{ $curr }		=	"MIXED";
			$type{ $next_1 }	=	"MIXED";
			$type{ $next_2 }	=	"MIXED";
			$type{ $next_3 }	=	"MIXED";

			$pos{ $curr }		=	"NN";
			$morphology{ $curr }	=	"NEU SIN IND NOM";
			$transcription{ $curr }	=	$alphabet{$ort{ $curr } };
		
		}
	
	} # end exists
	
	
} # end sub
#**************************************************************#
# mixed_num_5
#
# Example:		A4
#
#**************************************************************#
sub mixed_num_5 {

	if ( exists ( $ort{ $next_1 } ) ) {

#		print "\n\n-----------------
#		mixed_num_5
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		";

		if (	
			# Context
			$ort{ $curr }	=~	/^[a-zåäö]$/i
			&&
			$ort{ $next_1 }	=~	/^\d+$/i
	
		) {
		
			# Retag
			$type{ $curr }		=	"MIXED";
			$type{ $next_1 }	=	"MIXED";

			$pos{ $curr }		=	"NN";
			$morphology{ $curr }	=	"NEU SIN IND NOM";
			$transcription{ $curr }	=	$alphabet{$ort{ $curr } };
		
		}
	
	} # end exists
	
	
} # end sub
#**************************************************************#
# mixed_num_6
#
# Mix with unknown letter sequence
# Example:	xty23
#
#**************************************************************#
sub mixed_num_6 {
	
	if ( exists ( $ort{ $next_1 } )) {

#		print "\n\n-----------------
#		mixed_num_6
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		";
	
		if (	
			# Context
			$pos{ $curr }	eq	"UNK"
			&&
			$ort{ $curr }	=~	/^[a-zåäö]+$/i
			&&
			$ort{ $next_1 }	=~	/^\d+$/i
	
		) {

			my $lc_letters	=	lc( $ort{ $curr } );
			my $ucf_letters	=	ucfirst( $ort{ $curr } );
	
			# Check if letters exists in any lexicon.
			if (
				exists ( $any_lexicon{ $lc_letters } ) 
				||
				exists ( $any_lexicon{ $ucf_letters } ) 
			) {
				# Retag - not as "MIXED".
				$type{ $curr }		=	"TEXT";
				$type{ $next_1 }	=	"TEXT";
				$pos{ $next_1 }		=	"NUM CARD";
				
			# Letters does not exist, tag as "MIXED".
			} else {
				# Retag as "MIXED".	
				$type{ $curr }		=	"MIXED";
				$type{ $next_1 }	=	"MIXED";
				
			}	
		}
	} # end exists	
}
#**************************************************************#
# mixed_num_7
#
# Example:	60-årsdagen
#
#**************************************************************#
sub mixed_num_7 {
	
	if ( exists ( $ort{ $next_2 } )) {

#		print "\n\n-----------------
#		mixed_num_7
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		";
	
		if (	
			# Context
			$ort{ $curr }	=~	/^\d+$/
			&&
			$ort{ $next_1 }	eq	"-"
			&&
			$ort{ $next_2 }	=~	/^[a-zåäö]+$/i
	
		) {
			# Retag
			$type{ $curr }		=	"MIXED";
			$type{ $next_1 }	=	"MIXED";
			$type{ $next_2 }	=	"MIXED";
		}
	} # end exists
}
#**************************************************************#
# mixed_num_8
#
# Example:	moment-22-dilemma
#
#**************************************************************#
sub mixed_num_8 {

	if (exists ( $ort{ $prev_2 } ) && exists ( $ort{ $next_2 } )) {
		
#		print "\n\n-----------------
#		mixed_num_8
#		P2: $ort{$prev_2}
#		P1: $ort{$prev_1}
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		";

		if (	
			# Context
			$ort{ $curr }	=~	/^\d+$/i
			&&
			$ort{ $prev_2 }	=~	/^[a-zåäö]+$/i
			&&
			$ort{ $prev_1 }	eq	"-"
			&&
			$ort{ $next_1 }	eq	"-"
			&&
			$ort{ $next_2 }	=~	/^[a-zåäö]+$/i
		) {
			# Retag
			$type{ $prev_2 }	=	"MIXED";
			$type{ $prev_1 }	=	"MIXED";
			$type{ $curr }		=	"MIXED";
			$type{ $next_1 }	=	"MIXED";
			$type{ $next_2 }	=	"MIXED";
			
			$exp{ $prev_1 }		=	"<NONE>";
			$exp{ $next_1 }		=	"<NONE>";

			$pos{ $curr }		=	"NUM CARD";
			
		}
		
	} # end exists
}
#**************************************************************#
# mixed_num_concat
#
# Tag "-" between mixed expressions as "MIXED".
#
#**************************************************************#
sub mixed_num_concat {

	if (exists ( $ort{ $prev_1 } ) && exists ( $ort{ $next_1 } )) {
		
#		print "\n\n-----------------
#		mixed_num_concat
#		P1: $type{$prev_1}
#		CURR: $ort{$curr}
#		N1: $type{$next_1}
#		";

		if (	
			# Context
			$ort{ $curr }		eq	"-"
			&&	
			$type{ $prev_1 }	eq	"MIXED"
			&&
			$type{ $next_1 }	eq	"MIXED"
		) {
			# Retag
			$type{ $curr }		=	"MIXED";
		}
	} # end exists
	
}
#**************************************************************#
1;
