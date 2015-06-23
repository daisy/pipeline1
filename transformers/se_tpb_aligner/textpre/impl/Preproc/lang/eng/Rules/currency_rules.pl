#!/usr/bin/perl -w

#**************************************************************#
sub currency_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&currency_1();
	&currency_2();

}
#**************************************************************#
# currency_1
#
# $ 12,50
#
#**************************************************************#
sub currency_1 {

	# $ 12,50
	if ( 
		exists ( $ort{ $next_2 } )
		&&
		$ort{ $curr }		=~	/^$currency_list$/io
	) {
		if (
			$ort{ $curr }	=~	/^.$/
			&&
			$pos{ $next_2 }		=~	/NUM/
			&&
			$pos{ $next_1 }		eq	"DEL"
		) {
			# Switch expansions for currency and number.
			$type{ $curr }		=	"CURRENCY";
			$type{ $next_1 }	=	"CURRENCY";
			$type{ $next_2 }	=	"CURRENCY";

			
			if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
				$morphology{ $curr }	=	"UTR";
				$morphology{ $next_1 }	=	"UTR";
				$morphology{ $next_2 }	=	"UTR";
			}
			
			# If following fields are decimal number,
			# tag the two fields after as currency.
			if  ( 
				exists ( $ort{ $next_4 } )
				&&
				$type{ $next_3 }	=~	/DEC/
				&&
				$type{ $next_4 }	=~	/DEC/
			) {
				$type{ $next_3 }	=	"CURRENCY";
				$type{ $next_4 }	=	"CURRENCY";

				if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
					$morphology{ $next_3 }	=	"UTR";
					$morphology{ $next_4 }	=	"UTR";
				}

			} # end exists


		}
	} # end exists

	# $12,50
	if ( 
		exists ( $ort{ $next_1 } )
		&&
		$ort{ $curr }		=~	/^$currency_list$/io
	) {
		if (
			$ort{ $curr }	=~	/^.$/
			&&
			$pos{ $next_1 }		=~	/NUM/
		) {
			# Switch expansions for currency and number.
			$type{ $curr }		=	"CURRENCY";
			$type{ $next_1 }	=	"CURRENCY";

			if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
				$morphology{ $curr }	=	"UTR";
				$morphology{ $next_1 }	=	"UTR";
			}
			

			# If following fields are decimal numbers,
			# tag the two fields after as currency.
			if  ( 
				exists ( $ort{ $next_3 } )
				&&
				$type{ $next_2 }	=~	/DEC/
				&&
				$type{ $next_3 }	=~	/DEC/
			) {
				$type{ $next_2 }	=	"CURRENCY";
				$type{ $next_3 }	=	"CURRENCY";

				if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
					$morphology{ $next_2 }	=	"UTR";
					$morphology{ $next_3 }	=	"UTR";
				}

						

			} # end exists

		}
	} # end exists

}
#**************************************************************#
# currency_2
#
# 12,50:-
#
#**************************************************************#
sub currency_2 {

			
	# 12,50 :-
	if ( 
		exists ( $ort{ $prev_2 } )
		&&
		$ort{ $curr }		=~	/^$currency_list$/io
	) {
		
		if (
			$ort{ $curr }	=~	/^$currency_list$/io
			&&
			$pos{ $prev_2 }		=~	/NUM/
			&&
			$pos{ $prev_1 }		eq	"DEL"
		) {
			# Tag
			$type{ $curr }		=	"CURRENCY";
			$type{ $prev_1 }	=	"CURRENCY";
			$type{ $prev_2 }	=	"CURRENCY";
			
			if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
				$morphology{ $prev_1 }	=	"UTR";
				$morphology{ $prev_2 }	=	"UTR";
			}

			
#			print "P2: $type{ $prev_2 }\nP3: $type{ $prev_3 }\nP4: $type{ $prev_4 }\n\n";
			
			# If preceding fields are decimal number,
			# tag the following two fields as currency.
			if  ( 
				exists ( $ort{ $prev_4 } )
				&&
				$type{ $prev_3 }	=~	/DEC/
				&&
				$type{ $prev_4 }	=~	/DEC/
			) {
				$type{ $prev_3 }	=	"CURRENCY";
				$type{ $prev_4 }	=	"CURRENCY";


				if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
					$morphology{ $prev_3 }	=	"UTR";
					$morphology{ $prev_4 }	=	"UTR";
				}
			} # end exists

			# Singular
			if ( $ort{ $curr }	=~	/^(:-|kr\.|kr)$/i ) {
				
#				print "\nP22: $ort{ $prev_1 }\tMM $morphology{ $prev_1 }\n\n";
				if ( $ort{ $prev_1 }	eq	"1" ) {
					$exp{ $curr }	=	"krona";
				#Plural
				} else {
					$exp{ $curr }	=	"kronor";
				}
			} # end if $type
		}


	} # end exists



	# 12.50£
	if ( 
		exists ( $ort{ $prev_1 } )
		&&
		$ort{ $curr }		=~	/^$currency_list$/io

	) {
#		print "\nP1. $ort{$prev_1}\t$type{$prev_1}\nP2. $ort{$prev_2}\t$type{$prev_2}\nP3. $ort{$prev_3}\t$type{$prev_3}\n\n";

		if (
			$pos{ $prev_1 }		=~	/NUM/
		) {
			# Tag as CURRENCY.
			$type{ $curr }		=	"CURRENCY";
			$type{ $prev_1 }	=	"CURRENCY";
			
			if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
				$morphology{ $prev_1 }	=	"UTR";
			}

#			print "\nP1. $ort{$prev_1}\t$type{$prev_1}\nP2. $ort{$prev_2}\t$type{$prev_2}\nP3. $ort{$prev_3}\t$type{$prev_3}\n\n";
		
		
			# If following fields are decimal number,
			# tag the two fields after as currency.
			if  ( 
				exists ( $ort{ $prev_3 } )
				&&
				$type{ $prev_2 }	=~	/DEC/
				&&
				$type{ $prev_3 }	=~	/DEC/
			) {
				$type{ $prev_2 }	=	"CURRENCY";
				$type{ $prev_3 }	=	"CURRENCY";

#				print "TYJOTJO\n";
				
				if ( $ort{ $curr }	!~	/(?:\£|pund)/i ) {
					$morphology{ $prev_2 }	=	"UTR";
					$morphology{ $prev_3 }	=	"UTR";
				}
			}		

			# Singular
			if ( $ort{ $curr }	=~	/^(:-|kr\.|kr)$/i ) {
				
#				print "\nP22: $ort{ $prev_1 }\tMM $morphology{ $prev_1 }\n\n";
				if ( $ort{ $prev_1 }	eq	"1" ) {
					$exp{ $curr }	=	"krona";
				#Plural
				} else {
					$exp{ $curr }	=	"kronor";
				}
			} # end if $type
		}
		
	} # end exists


}
#**************************************************************#
1;
#**************************************************************#
