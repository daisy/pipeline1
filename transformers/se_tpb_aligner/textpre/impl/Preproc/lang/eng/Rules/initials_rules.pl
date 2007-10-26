#!/usr/bin/perl -w

#**************************************************************#
sub initials_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	
	&initials_1();		# C. Karlsson
	&initials_2();		# Karlsson, C.
	
}
#**************************************************************#
# initials_1
#
# Name initials before last name.
# Example:			C. Karlsson
#				C.Karlsson
#				C Karlsson
#
#**************************************************************#
sub initials_1 {


	# With period and blanks:		C. Karlsson
	if ( exists ( $ort{ $next_3 } ) ) {
		
#		print "\n
#		-----------
#		initials_1
#		Curr:	$ort{ $curr }
#		Next1:	$ort{ $next_1 }
#		Next2:	$pos{ $next_2 }
#		Next3:	$pos{ $next_3 }
#		\n";
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$ort{ $next_1 }		eq	"."
			&&
			$pos{ $next_2 }		eq	"DEL"
			&&
			$pos{ $next_3 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			$type{ $next_1 }	=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";
			
			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };
			&initial_spread_1();
		}
		
		
		
	} # end exists
	
	# With period and without blanks:		C.Karlsson
	if ( exists ( $ort{ $next_2 } ) ) {
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$ort{ $next_1 }		eq	"."
			&&
			$pos{ $next_2 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			$type{ $next_1 }	=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";

			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };
			&initial_spread_1();
		}

		
	} # end exists

	# Without period and with blanks:		C Karlsson
	if ( exists ( $ort{ $next_2 } ) ) {
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$pos{ $next_2 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";

			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };
			&initial_spread_1();
		}
		
		
	} # end exists
}
#**************************************************************#
# initials_2
#
# Name initials after last name.
# Example:			Karlsson, C.
#				Karlsson,C.
#				Karlsson, C
#				Karlsson C,
#
#**************************************************************#
sub initials_2 {



	# With period and blanks:		Karlsson, C.
	if ( exists ( $ort{ $prev_3 } ) && exists ( $ort{ $next_1 } )) {
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$ort{ $next_1 }		eq	"."
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $prev_2 }		eq	","
			&&
			$pos{ $prev_3 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			$type{ $next_1 }	=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";
			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };

			&initial_spread_2();
		}

		
	} # end exists
	
	# With period and without blanks:		Karlsson,C.	Karlsson C.
	if ( exists ( $ort{ $prev_2 } )) {
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$ort{ $prev_1 }		=~	/^[\.\, ]$/
			&&
			$pos{ $prev_2 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";
			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };

			&initial_spread_2();

			# Next field is "."
			if ( 
				exists ( $type{ $next_1 } )
				&&
				$ort{ $next_1 }		eq	"."

			) {
				$type{ $next_1 }	=	"INITIAL";
			}

		}

	} # end exists

	# Without period and with blanks:		Karlsson, C
	if ( exists ( $ort{ $prev_3 } ) ) {
				
		if (
			# Context
			$ort{ $curr }		=~	/^[A-ZÅÄÖ]$/
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $prev_2 }		eq	","
			&&
			$pos{ $prev_3 }		=~	/^PM/
		) {
			$type{ $curr }		=	"INITIAL";
			
			$pos{ $curr }		=	"PM";
			$morphology{ $curr }	=	"NOM";
			$lang{ $curr }		=	"swe";

			$transcription{ $curr }	=	$alphabet{ $ort{ $curr } };

			&initial_spread_2();
		}

		
	} # end exists
}
#**************************************************************#
# sub initial_spread_1
#
# Looks for more intials in front of the tagged one.
#
# Example:	H. C. Karlsson
#
#**************************************************************#
sub initial_spread_1 {

	
	# With blanks and periods:		H. C. Karlsson
	if (exists ( $ort{ $prev_3 } )) {
			
		if (
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $prev_2 }	eq	"."
			&&
			$ort{ $prev_3 }	=~	/^[A-ZÅÄÖ]$/
		) {
			$type{ $prev_2 }	=	"INITIAL";
			$type{ $prev_3 }	=	"INITIAL";
			
			$pos{ $prev_3 }		=	"PM NOM";
			$transcription{ $prev_3 }	=	$alphabet{ $ort{ $prev_3 } };
			
		}
	
	} # end exists

	# Without blanks and with periods:	H.C. Karlsson, H-C Karlsson
	if (exists ( $ort{ $prev_2 } )) {
			
		if (
			$ort{ $prev_1 }		=~	/^[\.\-]$/
			&&
			$ort{ $prev_2 }	=~	/^[A-ZÅÄÖ]$/
		) {
			$type{ $prev_1 }	=	"INITIAL";
			$type{ $prev_2 }	=	"INITIAL";
			
			$transcription{ $prev_2 }	=	$alphabet{ $ort{ $prev_2 } };
			$pos{ $prev_2 }		=	"PM NOM";

		}
	} # end exists

	# With blanks and without periods:	H C. Karlsson
	if (exists ( $ort{ $prev_2 } )) {
			
		if (
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $prev_2 }		=~	/^[A-ZÅÄÖ]$/
		) {
			$type{ $prev_2 }	=	"INITIAL";
			$transcription{ $prev_2 }	=	$alphabet{ $ort{ $next_2 } };
			
			$pos{ $prev_2 }		=	"PM NOM";

		}
	} # end exists

}
#**************************************************************#
# sub initial_spread_2
#
# Looks for more intials after the tagged one.
#
# Example:	Karlsson, H.C
#
#**************************************************************#
sub initial_spread_2 {
	
	# With blanks and periods:		Karlsson, H. C. 
	if (exists ( $ort{ $next_3 } )) {
			
		if (
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^[A-ZÅÄÖ]$/
			&&
			$ort{ $next_3 }		eq	"."
		) {
			$type{ $next_2 }	=	"INITIAL";
			$type{ $next_3 }	=	"INITIAL";
			
			$transcription{ $next_2 }	=	$alphabet{ $ort{ $next_2 } };
			
			$pos{ $next_3 }		=	"PM NOM";
		}
	} # end exists

	# Without blanks and with periods:	Karlsson, H.C., Karlsson H-C 
	if (exists ( $ort{ $next_2 } )) {
			
		if (
			$ort{ $next_1 }		=~	/^[\.\-]$/
			&&
			$ort{ $next_2 }		=~	/^[A-ZÅÄÖ]$/
		) {
			$type{ $next_1 }	=	"INITIAL";
			$type{ $next_2 }	=	"INITIAL";
			$transcription{ $next_2 }	=	$alphabet{ $ort{ $next_2 } };
			
			$pos{ $next_2 }		=	"PM NOM";
		}
	} # end exists

	# With blanks and without periods:	Karlsson, H C.
	if (exists ( $ort{ $next_2 } )) {
			
		if (
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		=~	/^[A-ZÅÄÖ]$/
		) {
			$type{ $next_2 }	=	"INITIAL";
			$transcription{ $next_2 }	=	$alphabet{ $ort{ $next_2 } };
			
			$pos{ $next_2 }		=	"PM NOM";
		}
	} # end exists

}
#**************************************************************#
1;
#**************************************************************#
